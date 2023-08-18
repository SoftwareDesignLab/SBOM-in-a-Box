package org.svip.api.requests.generators;

import org.springframework.web.multipart.MultipartFile;
import org.svip.api.entities.generators.OSIFile;

/**
 * file: UploadOSIFileInput.java
 * Input request to create a new OSI File
 *
 * @author Derek Garcia
 **/
public record UploadOSIFileInput(MultipartFile zipFile, String[] tools) {

    /**
     * Create a new OSIFile.
     *
     * @return OSIFile
     */
    public OSIFile toOSIFile() {
        return new OSIFile(zipFile, tools);
    }
}
