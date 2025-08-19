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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * <b>File:</b> Serializer.java
 * <p>
 * <b>Description:</b> Generic serializer that loads JSON and XML files into hashmaps to be used by schema implementations
 *
 * @author Derek Garcia
 */
public abstract class Deserializer {

    private final ObjectMapper jsonMapper = new ObjectMapper();
    private final XmlMapper xmlMapper = new XmlMapper();

    /**
     * Load JSON file into a hashmap
     *
     * @param jsonFile JSON file to load
     * @return HashMap of JSON object
     * @throws IOException Failed to map JSON to hashmap
     */
    private HashMap<String, Object> loadJsonFile(File jsonFile) throws IOException {
        return jsonMapper.readValue(jsonFile, new TypeReference<>() {
        });
    }
    // todo TagValue mapper

    /**
     * Load xml file into a hashmap
     *
     * @param xmlFile XML file to load
     * @return HashMap of XML object
     * @throws IOException Failed to map XML to hashmap
     */
    private HashMap<String, Object> loadXmlFile(File xmlFile) throws IOException {
        return xmlMapper.readValue(xmlFile, new TypeReference<>() {
        });
    }

    /**
     * Load tag-value into a hashmap
     *
     * @param tagValueFile Tag-Value file to load
     * @return HashMap of TagValue object
     * @throws IOException Failed to map TagValue to hashmap
     */
    private HashMap<String, Object> loadTagValueFile(File tagValueFile) throws IOException {
        // todo
        throw new IOException("Not implemented");
    }

    /**
     * Load the file into hashmap using the appropriate mapper
     *
     * @param file   File to load
     * @param format Format of file
     * @return HashMap representation of the file
     * @throws DeserializerException Failed to load file
     */
    protected HashMap<String, Object> loadFile(File file, FileFormat format) throws DeserializerException {
        try {
            return switch (format) {
                case JSON -> loadJsonFile(file);
                case XML -> loadXmlFile(file);
                case TAG_VALUE -> loadTagValueFile(file);
            };
        } catch (IOException e) {
            throw new DeserializerException("Failed to load " + file.getName(), file, format, e);
        }
    }

    /**
     * Deserialize the file into an SBOM object
     *
     * @param file   File to deserialize
     * @param format Format of file
     * @return SBOM object
     */
    public abstract SBOM deserialize(File file, FileFormat format) throws DeserializerException;

    /**
     * Standardized deserialization error message
     */
    public static class DeserializerException extends IOException {

        private final File file;
        private final FileFormat fileFormat;
        private Exception e;

        /**
         * Failed to load into hashmap
         *
         * @param message    Error message
         * @param file       File attempting to load
         * @param fileFormat Format of file
         */
        public DeserializerException(String message, File file, FileFormat fileFormat) {
            super(message);
            this.file = file;
            this.fileFormat = fileFormat;
        }

        /**
         * Failed to load into hashmap
         *
         * @param message    Error message
         * @param file       File attempting to load
         * @param fileFormat Format of file
         * @param e          Exception
         */
        public DeserializerException(String message, File file, FileFormat fileFormat, Exception e) {
            this(message, file, fileFormat);
            this.e = e;
        }

        /**
         * @return Exception
         */
        public Exception getException() {
            return e;
        }

        /**
         * @return File
         */
        public File getFile() {
            return file;
        }

        /**
         * @return File Format
         */
        public FileFormat getFileFormat() {
            return fileFormat;
        }
    }

}
