/*
 * Copyright 2021 Rochester Institute of Technology (RIT). Developed with
 *  government support under contract 70RCSA22C00000008 awarded by the United
 * States Department of Homeland Security for Cybersecurity and Infrastructure Security Agency.
 *  <p>
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the “Software”), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *  <p>
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *  <p>
 *  THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package org.svip.serializers.deserializer.v2;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * File: SPDX23TagValueDeserializer.java
 * <p>
 * This class implements the Deserializer interface and the Jackson StdDeserializer to provide all functionality to
 * read an SPDX 2.3 SBOM object from an SPDX 2.3 tag-value file string.
 * <p>
 * Private, only to be used by SPDX23Deserializer
 *
 * @author Ian Dunn
 * @author Tyler Drake
 * @author Matt London
 * @author Ethan Numan
 * @author Thomas Roman
 * @author Derek Garcia
 */
class SPDX23TagValueDeserializer {

    //#region Constants

    private static final String TAG = "#####";
    private static final String SEPARATOR = ": ";

    private static final String UNPACKAGED_FILES_HEADER = TAG + " Unpackaged files";
    private static final String PACKAGE_HEADER = TAG + " Package";
    private static final String RELATIONSHIPS_HEADER = TAG + " Relationships";

    private static final String EXTERNAL_REFERENCE_TAG = "ExternalRef";

    private static final String CREATION_INFO_TAG_RE = "^(LicenseListVersion|Creator|Created): (.*)";
    private static final String NORMALIZED_CHECKSUM_TAG_VALUE_RE = "^(?:File|Package)Checksum: (.*)";
    private static final String NORMALIZED_TAG_VALUE_RE = "^(?:File(?!Name)|Package|Document)(Name|License.*?|CopyrightText|SourceInfo|DownloadLocation|Version|Supplier): (.*)";
    // todo - probably more shared
    private static final String STRING_ARRAY_TAG_RE = "^(?:FileType|LicenseInfoInFile): .*";
    private static final Pattern NORMALIZED_TAG_VALUE = Pattern.compile(NORMALIZED_TAG_VALUE_RE);

    //#endregion

    /**
     * Get the head / left-hand side of seperator
     *
     * @param line Line to split
     * @return head / left-hand side of seperator
     */
    private String getTag(String line) {
        return line.split(SEPARATOR, 2)[0];
    }

    /**
     * Get the tail / right-hand side of seperator
     *
     * @param line Line to split
     * @return tail / right-hand side of seperator
     */
    private String getValue(String line) {
        return line.split(SEPARATOR, 2)[1];
    }

    /**
     * Lowercase the first letter of the given string
     *
     * @param input String to lower
     * @return String with first letter lowercased
     */
    private String lowercaseFirstLetter(String input) {
        if (input == null || input.isEmpty())
            return input;
        return input.substring(0, 1).toLowerCase() + input.substring(1);
    }


    /**
     * Parse the list of SPDX files in the doc
     *
     * @param lines Lines to process
     * @return List of SPDX File Objects
     */
    private List<Map<String, Object>> parseSPDXFiles(List<String> lines) {
        List<Map<String, Object>> spdxFiles = new ArrayList<>();
        // parse all file objects (until eof or reach next tag)
        while (!(lines.isEmpty() || lines.get(0).startsWith(TAG)))
            spdxFiles.add(parseSPDXTagValueObject(lines));

        return spdxFiles;

    }

    /**
     * Parse the list of relationships in the doc
     *
     * @param lines Lines to process
     * @return List of SPDX relationship objects
     */
    private List<Map<String, String>> parseSPDXRelationships(List<String> lines) {
        List<Map<String, String>> spdxRelationships = new ArrayList<>();
        // todo - relationships always eof?
        while (!lines.isEmpty()) {
            String line = lines.remove(0);
            // base case
            if (line.isEmpty())
                break;
            String[] components = getValue(line).split(" ");
            Map<String, String> relObj = new HashMap<>(Map.of(
                    "spdxElementId", components[0],
                    "relatedSpdxElement", components[2],
                    "relationshipType", components[1]
            ));
            // add comment if one
            if (!lines.isEmpty() && lines.get(0).startsWith("RelationshipComment"))
                relObj.put("comment", getValue(lines.remove(0)));
            // add relationship
            spdxRelationships.add(relObj);
        }
        return spdxRelationships;
    }


    /**
     * Recursively parse an SPDX Tag-Value Object into a Map
     *
     * @param lines List of lines remaining in the file
     * @return Map of SPDX Tag-Value objects
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> parseSPDXTagValueObject(List<String> lines) {
        // init object
        Map<String, Object> objectDetails = new LinkedHashMap<>();  // linked to keep order for convince

        while (!lines.isEmpty()) {
            // pop line
            String line = lines.remove(0).trim();
            // base case -- return if empty
            if (line.isEmpty())
                return objectDetails;

            // Metadata
            if (line.matches(CREATION_INFO_TAG_RE)) {
                // init if dne
                objectDetails.putIfAbsent("creationInfo", new HashMap<String, Object>());
                Map<String, Object> creationInfo = (Map<String, Object>) objectDetails.get("creationInfo");
                String value = getValue(line);
                switch (getTag(line)) {
                    case "LicenseListVersion" -> creationInfo.put("licenseListVersion", value);
                    case "Creator" -> {
                        creationInfo.putIfAbsent("creators", new ArrayList<>());
                        ((List<String>) creationInfo.get("creators")).add(value);
                    }
                    case "Created" -> creationInfo.put("created", value);
                }
                continue;
            }

            // Parse SPDX files
            if (line.startsWith(UNPACKAGED_FILES_HEADER)) {
                // pop whitespace
                lines.remove(0);
                // add to main obj
                objectDetails.put("files", parseSPDXFiles(lines));
                continue;
            }

            // Parse SPDX Relationships
            if (line.startsWith(RELATIONSHIPS_HEADER)) {
                // pop whitespace
                lines.remove(0);
                // add to main obj
                objectDetails.put("relationships", parseSPDXRelationships(lines));
                continue;
            }


            // Parse SPDX Package object
            if (line.startsWith(PACKAGE_HEADER)) {
                // pop whitespace
                lines.remove(0);
                // init if dne
                objectDetails.putIfAbsent("packages", new ArrayList<Map<String, Object>>());
                ((List<Object>) objectDetails.get("packages")).add(parseSPDXTagValueObject(lines));
                continue;
            }

            // Parse External Reference
            if (line.startsWith(EXTERNAL_REFERENCE_TAG)) {
                String[] components = getValue(line).split(" ");
                objectDetails.putIfAbsent("externalRefs", new ArrayList<Map<String, Object>>());
                ((List<Object>) objectDetails.get("externalRefs")).add(Map.of(
                                "referenceCategory", components[0],
                                "referenceType", components[1],
                                "referenceLocator", components[2]
                        )
                );
                continue;
            }

            // Parse Checksum
            if (line.matches(NORMALIZED_CHECKSUM_TAG_VALUE_RE)) {
                String[] components = getValue(line).split(SEPARATOR);
                objectDetails.putIfAbsent("checksums", new ArrayList<Map<String, Object>>());
                ((List<Object>) objectDetails.get("checksums")).add(Map.of(
                                "algorithm", components[0],
                                "checksumValue", components[1]
                        )
                );
                continue;
            }

            // Can hav 0..* string values
            if (line.matches(STRING_ARRAY_TAG_RE)) {
                String tag = lowercaseFirstLetter(getTag(line)) + "s";
                objectDetails.putIfAbsent(tag, new ArrayList<String>());
                ((List<String>) objectDetails.get(tag)).add(getValue(line));
                continue;
            }

            // add basic tag-value
            if (line.matches(NORMALIZED_TAG_VALUE_RE)) {
                // normalize tag (excluding checksum)
                Matcher m = NORMALIZED_TAG_VALUE.matcher(line);
                m.find();
                objectDetails.put(lowercaseFirstLetter(m.group(1)), m.group(2));
            } else {
                // else don't need to normalize, just add
                // edge cases
                String tag = line.startsWith("SPDX") ? getTag(line) : lowercaseFirstLetter(getTag(line));
                if (line.equals("SPDXVersion"))
                    tag = "spdxVersion";
                objectDetails.put(tag, getValue(line));
            }

        }

        return objectDetails;
    }


    /**
     * Deserialize the file into a map to be used by SPDX Deserializer
     *
     * @param file File to deserialize
     * @return SBOM object
     */
    public Map<String, Object> readValue(File file) throws IOException {
        List<String> lines = Files.readAllLines(file.toPath());
        // build root object
        Map<String, Object> objectDetails = new LinkedHashMap<>();
        while (!lines.isEmpty())
            objectDetails.putAll(parseSPDXTagValueObject(lines));

        return objectDetails;
    }
}
