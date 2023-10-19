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
