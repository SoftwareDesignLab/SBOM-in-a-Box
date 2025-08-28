/**
 * Copyright 2021 Rochester Institute of Technology (RIT). Developed with
 * government support under contract 70RCSA22C00000008 awarded by the United
 * States Department of Homeland Security for Cybersecurity and Infrastructure Security Agency.
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the “Software”), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.svip.serializers.exceptions;

import org.svip.serializers.FileFormat;
import org.svip.utils.Debug;

import java.io.File;
import java.io.IOException;

/**
 * Exception thrown when there is an error in deserializing an SBOM
 *
 * @author Juan Francisco Patino
 * @author Derek Garcia
 */
public class DeserializerException extends IOException {
    private File file;
    private FileFormat fileFormat;
    private Exception e;

    @Deprecated
    public DeserializerException(String message) {
        super(message);
        Debug.log(Debug.LOG_TYPE.ERROR, message);
    }

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