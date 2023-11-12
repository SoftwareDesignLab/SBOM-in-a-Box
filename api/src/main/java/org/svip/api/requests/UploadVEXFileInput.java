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
