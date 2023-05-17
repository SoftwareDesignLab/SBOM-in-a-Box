package generators.cyclonedx;

import generators.CycloneDXGenerator;
import generators.GeneratorException;
import generators.License;
import generators.Tool;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import utils.Debug;
import utils.ParserComponent;
import org.svip.utils.SBOM.Component;

import java.util.*;
import java.util.stream.Collectors;

import static utils.Debug.log;

/**
 * File: BOM.java
 * <p>
 * Dataclass to store all attributes and components (including nested components) relevant to a CDX document.
 * </p>
 * @author Ian Dunn
 */
@JsonSerialize(using = BOMSerializer.class)
public class BOM {

    //#region Constants

    /**
     * The format of the BOM - always CycloneDX.
     */
    protected static final String BOM_FORMAT = "CycloneDX";

    /**
     * The specification version of the BOM.
     */
    protected static final String SPEC_VERSION = CycloneDXGenerator.SPEC_VERSION;

    //#endregion

    //#region Attributes

    /**
     * The head component of the SBOM. This is the component that the CDX BOM is generated from.
     */
    private final ParserComponent headComponent;

    /**
     * The unique serial number of the BOM.
     */
    private final String serialNumber;

    /**
     * The version of the BOM - 1 if the first one generated, n if the nth one generated.
     */
    private final int version;

    /**
     * The timestamp when the BOM was generated.
     */
    private final String timestamp;

    /**
     * List of tools used to generate the BOM.
     */
    private final ArrayList<Tool> tools;

    /**
     * The TOP-LEVEL components of the BOM only.
     */
    private final ArrayList<ParserComponent> components;

    /**
     * A map that maps a UUID of a top-level component or child to another component that depends on it.
     */
    private final Map<UUID, ArrayList<ParserComponent>> children;

    //#endregion

    //#region Constructors

    /**
     * The default constructor to create a new instance of a BOM.
     *
     * @param headComponent The head component of the SBOM. This is the component that the CDX BOM is generated from.
     * @param serialNumber The unique serial number of the BOM.
     * @param version The version of the BOM - 1 if the first one generated, n if the nth one generated.
     */
    public BOM(ParserComponent headComponent, String serialNumber, int version) {
        this.headComponent = headComponent;
        this.serialNumber = serialNumber;
        this.version = version;
        this.timestamp = Tool.createTimestamp();

        this.tools = new ArrayList<>();
        this.components = new ArrayList<>();
        this.children = new HashMap<>();
    }

    //#endregion

    //#region Getters

    public ParserComponent getHeadComponent() {
        return headComponent;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public int getVersion() {
        return version;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public ArrayList<Tool> getTools() {
        return tools;
    }

    public ArrayList<ParserComponent> getComponents() {
        return components;
    }

    /**
     * Get the children of a parent component's UUID.
     *
     * @param parent The UUID of the parent component to get the children from.
     * @return A list of child components of the parent UUID. Empty list if no children.
     */
    public List<ParserComponent> getChildren(UUID parent) {
        return children.get(parent) == null ? new ArrayList<>() : children.get(parent);
    }

    //#endregion

    //#region Core Methods

    /**
     * Add a tool that was used to generate this BOM.
     *
     * @param tool The tool that was used to generate this BOM.
     */
    public void addTool(Tool tool) {
        tools.add(tool);
        tool.getLicenses().forEach(headComponent::addResolvedLicense); // Add license to head component
    }

    /**
     * Adds a component to this BOM instance.
     *
     * @param component The ParserComponent storing all necessary component data.
     */
    public void addComponent(ParserComponent component) {
        // Go through all found licenses and resolve them
        component.resolveLicenses();
        Set<License> unresolved = component.getUnresolvedLicenses();
        for(License u : unresolved) {
            component.resolveLicense(u.getLicenseName(), null);
        }

        components.add(component);
        log(Debug.LOG_TYPE.DEBUG, String.format("BOM: Added component \"%s\". UUID: %s",
                component.getName(), component.getUUID()));
    }

    /**
     * Adds a child to a component in this BOM instance.
     *
     * @param parent The parent UUID that the child depends on.
     * @param child The child ParserComponent storing all necessary component data.
     */
    public void addChild(UUID parent, ParserComponent child) throws GeneratorException {
        // Get all possible parent component UUIDs
        Set<UUID> parents = components.stream().map(Component::getUUID).collect(Collectors.toSet());
        children.values().forEach(list -> {
            // One-liner to map all components to their UUIDs in a list, collect it in sets, and add to the parents list
            parents.addAll(list.stream().map(ParserComponent::getUUID).collect(Collectors.toSet()));
        });

        if(!parents.contains(parent))
            throw new GeneratorException("Parent UUID " + parent + " does not exist in components.");

        // Go through all found licenses and resolve them
        child.resolveLicenses();

        // If the parent UUID doesnt exist as a key, initialize the arraylist before adding a child
        children.computeIfAbsent(parent, k -> new ArrayList<>());
        children.get(parent).add(child);

        log(Debug.LOG_TYPE.DEBUG, String.format("BOM: Added child component \"%s\" that depends on parent UUID %s",
                child.getName(), parent));
    }

    //#endregion

}
