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

package org.svip.serializers.serializer.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.serializers.FileFormat;

import java.io.IOException;

/**
 * <b>File:</b> Serializer.java
 * <p>
 * <b>Description:</b> Generic serializer that loads JSON and XML files into hashmaps to be used by schema implementations
 *
 * @author Derek Garcia
 */
public abstract class Serializer {

    protected final FileFormat fileFormat;
    protected final boolean prettyPrint;
    protected ObjectMapper mapper;


    /**
     * Create new serializer
     *
     * @param fileFormat Type of serializer
     */
    protected Serializer(FileFormat fileFormat, Boolean prettyPrint) {
        this.fileFormat = fileFormat;
        this.prettyPrint = prettyPrint;
    }

    /**
     * Serialize an SBOM object into a string
     *
     * @param sbom SBOM to write
     * @throws SerializerException Failed to write to string
     */
    public abstract String writeToString(SBOM sbom) throws SerializerException;

    /**
     * Standardized serialization error message
     */
    public static class SerializerException extends IOException {

        private final SBOM sbom;
        private final FileFormat fileFormat;
        private Exception e;

        /**
         * Failed to write sbom
         *
         * @param message    Error message
         * @param sbom       SBOM attempting to serialize
         * @param fileFormat Format of file
         */
        public SerializerException(String message, SBOM sbom, FileFormat fileFormat) {
            super(message);
            this.sbom = sbom;
            this.fileFormat = fileFormat;
        }

        /**
         * Failed to write sbom
         *
         * @param message    Error message
         * @param sbom       SBOM attempting to serialize
         * @param fileFormat Format of file
         * @param e          Exception
         */
        public SerializerException(String message, SBOM sbom, FileFormat fileFormat, Exception e) {
            this(message, sbom, fileFormat);
            this.e = e;
        }

        /**
         * @return sbom
         */
        public SBOM getSBOM() {
            return sbom;
        }

        /**
         * @return Exception
         */
        public Exception getException() {
            return e;
        }


        /**
         * @return File Format
         */
        public FileFormat getFileFormat() {
            return fileFormat;
        }
    }

}
