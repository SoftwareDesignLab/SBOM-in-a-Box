/** Copyright 2021 Rochester Institute of Technology (RIT). Developed with
* government support under contract 70RCSA22C00000008 awarded by the United
* States Department of Homeland Security for Cybersecurity and Infrastructure Security Agency.
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the “Software”), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
 */

package org.svip.api.requests;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.svip.api.entities.SBOMFile;
import org.svip.serializers.SerializerFactory;
import org.svip.serializers.deserializer.Deserializer;

/**
 * File: UploadSBOMFileInput.java
 * Input request to create a new SBOM File via API
 *
 * @author Derek Garcia
 **/
public record UploadSBOMFileInput(String fileName, String contents) {

    /**
     * Create a new SBOM File Object
     * @return SBOM File
     * @throws JsonProcessingException Failed to parse SBOM and is invalid
     */
    public SBOMFile toSBOMFile() throws JsonProcessingException {
        SBOMFile sbomFile = new SBOMFile();

        sbomFile.setName(fileName)
            .setContent(contents);

        // Attempt to deserialize
        Deserializer d = SerializerFactory.createDeserializer(sbomFile.getContent());
        d.readFromString(sbomFile.getContent());

        // If reach here, SBOM is valid, set additional fields
        sbomFile.setSchema(d)
            .setFileType(d);

        return sbomFile;
    }
}
