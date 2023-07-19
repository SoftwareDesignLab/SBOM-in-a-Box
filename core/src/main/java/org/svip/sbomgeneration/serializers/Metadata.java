package org.svip.sbomgeneration.serializers;

import org.svip.sbom.model.shared.metadata.CreationTool;

public class Metadata {
    public static final String VERSION = "1.0.0";
    public static final String VENDOR = "SVIP";
    public static final String NAME = "SVIP Serializer";
    public static final String SERIALIZED_COMMENT = "This SBOM was generated using the SVIP serializer tooling.";
    public static final String DESERIALIZED_COMMENT = "This SBOM was generated using the SVIP deserializer tooling.";

    public static CreationTool getCreationTool() {
        CreationTool tool = new CreationTool();
        tool.setVendor(VENDOR);
        tool.setVersion(VERSION);
        tool.setName(NAME);
//        tool.addHash();
        return tool;
    }
}
