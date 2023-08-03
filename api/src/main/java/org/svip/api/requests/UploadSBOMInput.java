package org.svip.api.requests;

import org.svip.api.entities.SBOM;

/**
 * @author Derek Garcia
 **/

public record UploadSBOMInput(String fileName, String contents) {
    public SBOM toSBOM() {
        SBOM sbom = new SBOM();

        sbom.setName(fileName)
            .setContent(contents);

        return sbom;
    }
}
