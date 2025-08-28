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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.serializers.FileFormat;
import org.svip.serializers.exceptions.DeserializerException;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * <b>File:</b> Deserializer.java
 * <p>
 * <b>Description:</b> Generic serializer that loads JSON and XML files into hashmaps to be used by schema implementations
 *
 * @author Derek Garcia
 */
public abstract class Deserializer {

    protected final FileFormat fileFormat;
    protected final ObjectMapper mapper = new ObjectMapper();

    /**
     * Create new deserializer
     *
     * @param fileFormat Type of deserializer
     */
    protected Deserializer(FileFormat fileFormat) {
        this.fileFormat = fileFormat;
    }

    /**
     * Load JSON file into a hashmap
     *
     * @param jsonFile JSON file to load
     * @return HashMap of JSON object
     * @throws IOException Failed to map JSON to hashmap
     */
    private Map<String, Object> loadJsonFile(File jsonFile) throws IOException {
        return new ObjectMapper().readValue(jsonFile, new TypeReference<>() {
        });
    }

    /**
     * Load xml file into a hashmap
     *
     * @param xmlFile XML file to load
     * @return HashMap of XML object
     * @throws IOException Failed to map XML to hashmap
     */
    private Map<String, Object> loadXmlFile(File xmlFile) throws IOException {
        return new XmlMapper().readValue(xmlFile, new TypeReference<>() {
        });
    }

    /**
     * Load tag-value into a hashmap
     *
     * @param tagValueFile Tag-Value file to load
     * @return HashMap of TagValue object
     * @throws IOException Failed to map TagValue to hashmap
     */
    private Map<String, Object> loadTagValueFile(File tagValueFile) throws IOException {
        return new SPDX23TagValueDeserializer().readValue(tagValueFile);
    }

    /**
     * Load the file into hashmap using the appropriate mapper
     *
     * @param file File to load
     * @return HashMap representation of the file
     * @throws DeserializerException Failed to load file
     */
    protected Map<String, Object> loadFile(File file) throws DeserializerException {
        try {
            return switch (fileFormat) {
                case JSON -> loadJsonFile(file);
                case XML -> loadXmlFile(file);
                case TAG_VALUE -> loadTagValueFile(file);
            };
        } catch (IOException e) {
            throw new DeserializerException("Failed to load " + file.getName(), file, fileFormat, e);
        }
    }

    /**
     * Deserialize the file into an SBOM object
     *
     * @param file File to deserialize
     * @return SBOM object
     */
    public abstract SBOM deserialize(File file) throws DeserializerException;

}
