package org.svip.api.entities.generators;

import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

public class OSIFile {
    private final MultipartFile zipFile;

    private final List<String> tools;

    public OSIFile(MultipartFile zipFile, String[] tools) {
        this.zipFile = zipFile;
        if (tools == null) this.tools = new ArrayList<>();
        else this.tools = List.of(tools);
    }

    public MultipartFile getZipFile() {
        return zipFile;
    }

    public List<String> getTools() {
        return tools;
    }
}
