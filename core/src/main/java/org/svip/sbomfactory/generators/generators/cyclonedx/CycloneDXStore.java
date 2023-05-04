package org.svip.sbomfactory.generators.generators.cyclonedx;

import org.svip.sbomfactory.generators.generators.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.svip.sbomfactory.generators.generators.utils.GeneratorException;
import org.svip.sbomfactory.generators.generators.utils.GeneratorSchema;
import org.svip.sbomfactory.generators.generators.utils.License;
import org.svip.sbomfactory.generators.utils.Debug;
import org.svip.sbomfactory.generators.utils.ParserComponent;
import org.svip.sbom.model.Component;

import java.util.*;
import java.util.stream.Collectors;

import static org.svip.sbomfactory.generators.utils.Debug.log;

/**
 * File: CycloneDXStore.java
 * <p>
 * Dataclass to store all attributes and components (including nested components) relevant to a CycloneDX BOM.
 * </p>
 * @author Ian Dunn
 */
@JsonSerialize(using = CycloneDXSerializer.class)
public class CycloneDXStore extends BOMStore {

    //#region Attributes

    /**
     * The TOP-LEVEL components of the CycloneDX BOM only.
     */
    private final ArrayList<ParserComponent> components;

    /**
     * A map that maps a UUID of a top-level component or child to another component that depends on it.
     */
    private final Map<UUID, ArrayList<ParserComponent>> children;

    //#endregion

    //#region Constructors

    /**
     * The default constructor to create a new instance of a CycloneDXStore to store all CycloneDX-specific data.
     *
     * @param serialNumber The unique serial number of the SBOM.
     * @param bomVersion The version of the SBOM - 1 if the first one generated, n if the nth one generated.
     * @param headComponent The head component of the SBOM. This is the component that stores the SBOM name, licenses, etc
     */
    public CycloneDXStore(String serialNumber, Integer bomVersion, ParserComponent headComponent) {
        super(GeneratorSchema.CycloneDX, "1.4", serialNumber, bomVersion, headComponent);

        this.components = new ArrayList<>();
        this.children = new HashMap<>();
    }

    //#endregion

    //#region Getters

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
     * Adds a component to this CycloneDX BOM.
     *
     * @param component The ParserComponent storing all necessary component data.
     */
    @Override
    public void addComponent(ParserComponent component) {
        // Go through all found licenses and resolve them
        component.resolveLicenses();
        Set<License> unresolved = component.getUnresolvedLicenses();
        for(License u : unresolved) {
            component.resolveLicense(u.getLicenseName(), null);
        }

        components.add(component);
        log(Debug.LOG_TYPE.DEBUG, String.format("CycloneDXStore: Added component \"%s\". UUID: %s",
                component.getName(), component.getUUID()));
    }

    /**
     * Adds a child to an existing component in this CycloneDX BOM.
     *
     * @param parent The parent UUID that the child depends on.
     * @param child  The child ParserComponent storing all necessary component data.
     */
    @Override
    public void addChild(ParserComponent parent, ParserComponent child) throws GeneratorException {
        UUID parentUUID = parent.getUUID();

        // Get all possible parent component UUIDs
        Set<UUID> parents = components.stream().map(Component::getUUID).collect(Collectors.toSet());
        children.values().forEach(list -> {
            // One-liner to map all components to their UUIDs in a list, collect it in sets, and add to the parents list
            parents.addAll(list.stream().map(ParserComponent::getUUID).collect(Collectors.toSet()));
        });

        if(!parents.contains(parentUUID))
            throw new GeneratorException("Parent UUID " + parent + " does not exist in components.");

        // Go through all found licenses and resolve them
        child.resolveLicenses();

        // If the parent UUID doesnt exist as a key, initialize the arraylist before adding a child
        children.computeIfAbsent(parentUUID, k -> new ArrayList<>());
        children.get(parentUUID).add(child);

        log(Debug.LOG_TYPE.DEBUG, String.format("CycloneDXStore: Added child component \"%s\" that depends on parent UUID %s",
                child.getName(), parentUUID));
    }

    /**
     * Gets ALL components present in this BOMStore, including top-level components and their children.
     */
    @Override
    public Set<ParserComponent> getAllComponents() {
        Set<ParserComponent> allComponents = new HashSet<>(components);
        for(ArrayList<ParserComponent> components : children.values()) {
            allComponents.addAll(components);
        }
        return allComponents;
    }

    //#endregion

}
