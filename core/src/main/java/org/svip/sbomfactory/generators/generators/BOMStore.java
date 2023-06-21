package org.svip.sbomfactory.generators.generators;

import org.svip.sbomfactory.generators.utils.ParserComponent;
import org.svip.sbomfactory.generators.utils.generators.GeneratorException;
import org.svip.sbomfactory.generators.utils.generators.GeneratorSchema;
import org.svip.sbomfactory.generators.utils.generators.License;
import org.svip.sbomfactory.generators.utils.generators.Tool;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** TODO Add comments detailing generation info?
 * File: BOMStore.java
 * <p>
 * Abstract dataclass to store common attributes and methods between all CycloneDXStore specifications. This class will be
 * implemented by each specification store.
 * </p>
 * @author Ian Dunn
 */
public abstract class BOMStore {

    //#region Attributes

    /**
     * The format of the CycloneDXStore (ex. CycloneDX)
     */
    private final String bomFormat;

    /**
     * The version of the CycloneDXStore specification (ex. 1.4 for CycloneDX)
     */
    private String specVersion;

    /**
     * The unique serial number of the CycloneDXStore.
     */
    private final String serialNumber;


    /**
     * The version of the CycloneDXStore - 1 if the first one generated, n if the nth one generated.
     */
    private final int bomVersion;

    /**
     * The timestamp when the CycloneDXStore was generated. This is automatically generated when the store is initialized, since
     * the store is a temporary class to hold information when the document is serialized.
     */
    private final String timestamp;

    /**
     * The head component of the SBOM. This is the component that stores the CycloneDXStore name, licenses, etc.
     */
    private final ParserComponent headComponent;

    /**
     * List of tools used to generate the CycloneDXStore.
     */
    private final List<Tool> tools;

    //#endregion

    //#region Constructors

    /**
     * Constructor to store all non-SBOM-specific data in the attributes of this class.
     *
     * @param schema The schema/specification of the SBOM.
     * @param specVersion The version of the SBOM specification.
     * @param serialNumber The unique serial number of the SBOM.
     * @param bomVersion The version of the SBOM - 1 if the first one generated, n if the nth one generated.
     * @param headComponent The head component of the SBOM. This is the component that stores the SBOM name, licenses, etc
     */
    public BOMStore(GeneratorSchema schema, String specVersion, String serialNumber, int bomVersion,
                    ParserComponent headComponent) {
        bomFormat = schema.name();

        this.specVersion = "";
        if(schema == GeneratorSchema.SPDX)
            this.specVersion += "SPDX-";
        this.specVersion += specVersion;
        this.serialNumber = serialNumber;
        this.bomVersion = bomVersion;
        this.timestamp = Tool.createTimestamp();
        this.headComponent = headComponent;

        this.tools = new ArrayList<>();
    }

    //#endregion

    //#region Abstract Methods

    /**
     * Adds a component to this SBOM.
     *
     * @param component The ParserComponent storing all necessary component data.
     */
    public abstract void addComponent(ParserComponent component);

    /**
     * Adds a child to an existing component in this SBOM.
     *
     * @param parent The parent UUID that the child depends on.
     * @param child The child ParserComponent storing all necessary component data.
     */
    public abstract void addChild(ParserComponent parent, ParserComponent child) throws GeneratorException;

    /**
     * Gets ALL components present in this BOMStore, including top-level components and their children.
     */
    public abstract Set<ParserComponent> getAllComponents();

    //#endregion

    //#region Core Methods

    /**
     * Add a tool that was used to generate this SBOM.
     *
     * @param tool The tool that was used to generate this SBOM.
     */
    public void addTool(Tool tool) {
        tools.add(tool);
        tool.getLicenses().forEach(headComponent::addResolvedLicense); // Add license to head component
    }

    //#endregion

    //#region Getters

    public String getBomFormat() {
        return bomFormat;
    }

    public String getSpecVersion() {
        return specVersion;
    }

    public int getBOMVersion() {
        return bomVersion;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public ParserComponent getHeadComponent() {
        return headComponent;
    }

    public List<Tool> getTools() {
        return tools;
    }

    public List<License> getToolLicenses() {
        List<License> licenses = new ArrayList<>();
        tools.stream().map(Tool::getLicenses).forEach(licenses::addAll);
        return licenses;
    }

    //#endregion
}
