package org.svip.manipulation.toSVIP;

public enum SchemaManipulationMap {

    CDX14("CycloneDX", "1.4", "bom-ref"),
    SPDX23("SPDX", "2.3", "SPDXRef"),
    SVIP10("SVIP", "1.0-a", "SVIPComponent");

    /**
     * Schema's name
     */
    private final String name;

    /**
     * The Version of the Schema
     */
    private final String version;

    /**
     * Component identifier type the Schema uses
     */
    private final String componentIDType;

    SchemaManipulationMap(String name, String version, String componentIDType) {
        this.name = name;
        this.version = version;
        this.componentIDType = componentIDType;
    }

}
