/ **
* Copyright 2021 Rochester Institute of Technology (RIT). Developed with
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
* /

package org.svip.api.requests;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.svip.api.entities.SBOMFile;
import org.svip.api.entities.VEXFile;
import org.svip.vex.VEXResult;

/**
 * File: UploadVEXFileInput.java
 * Input request to create a new VEX FIle
 *
 * @author Derek Garcia
 **/
public record  UploadVEXFileInput(VEXResult vexResult) {

    /**
     * Create a new VEX File Object
     *
     * @param sbomFile SBOM file of the vex generation was run on
     * @return VEXFile
     * @throws JsonProcessingException Failed to parse VEX and is invalid
     */
    public VEXFile toVEXFile(SBOMFile sbomFile, VEXFile.Database database) throws JsonProcessingException {
        VEXFile vf = new VEXFile();

        // Configure object mapper to remove null and empty arrays
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        // Set attributes
        vf.setName("vex")
          .setContent(mapper.writeValueAsString(vexResult))
          .setSchema(vexResult.vex().getOriginType())
          .setDatasource(database)
          .setSBOMFile(sbomFile);      // adds relationship

        // add to sbom
        sbomFile.setVEXFile(vf);

        return vf;
    }
}
