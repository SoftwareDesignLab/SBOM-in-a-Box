package org.svip.conversion.manipulate;

/**
 * Name: SchemaManipulationMap.java
 * Description: Holds default values for fields by format. These maps are typically
 * used by convert to determine the values of several fields during the conversion
 * process.
 *
 * @author Tyler Drake
 */
public enum SchemaManipulationMap {

    CDX14("CycloneDX", "1.4", "bom-ref:uuid:"),
    SPDX23("SPDX", "2.3", "SPDXRef-"),
    SVIP10("SVIP", "1.0-a", "SVIPComponent- ");

    /**
     * Schema's name
     */
    private final String schema;

    /**
     * The Version of the Schema
     */
    private final String version;

    /**
     * Component identifier type the Schema uses
     */
    private final String componentIDType;

    SchemaManipulationMap(String schema, String version, String componentIDType) {
        this.schema = schema;
        this.version = version;
        this.componentIDType = componentIDType;
    }

    public String getSchema() {
        return schema;
    }

    public String getVersion() {
        return version;
    }

    public String getComponentIDType() {
        return componentIDType;
    }

}
