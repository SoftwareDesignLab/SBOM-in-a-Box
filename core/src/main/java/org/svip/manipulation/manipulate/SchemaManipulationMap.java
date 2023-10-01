package org.svip.manipulation.manipulate;

public enum SchemaManipulationMap {

    CDX14("CycloneDX", "1.4", "bom-ref:uuid:"),
    SPDX23("SPDX", "2.3", "SPDXRef-"),
    SVIP10("SVIP", "1.0-a", "SVIPComponent- ");

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

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getComponentIDType() {
        return componentIDType;
    }

}
