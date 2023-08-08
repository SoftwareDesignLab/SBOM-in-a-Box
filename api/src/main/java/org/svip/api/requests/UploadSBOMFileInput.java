package org.svip.api.requests;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.svip.api.entities.SBOM;
import org.svip.api.services.SBOMFileService;
import org.svip.serializers.SerializerFactory;
import org.svip.serializers.deserializer.Deserializer;

/**
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
    public SBOM toSBOMFile() throws JsonProcessingException {
        SBOM sbom = new SBOM();

        sbom.setName(fileName)
            .setContent(contents);

        // Attempt to deserialize
        Deserializer d = SerializerFactory.createDeserializer(sbom.getContent());
        d.readFromString(sbom.getContent());

        // If reach here, SBOM is valid, set additional fields
        sbom.setSchema(d)
            .setFileType(d).id = SBOMFileService.generateSBOMFileId();

        return sbom;
    }
}
