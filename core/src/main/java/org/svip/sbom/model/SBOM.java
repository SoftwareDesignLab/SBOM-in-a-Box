package org.svip.sbom.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * File: SBOM.java
 * Represents a single component inside an SBOM
 *
 * @author Matt London
 * @author Kevin LaPorte
 */
public class SBOM {
    /**
     * Dependency tree of the components
     */
    private final DependencyTree dependencyTree;

    /**
     * Type of SBOM which this object came from (whatever the bom.xml, bom.json was formatted in)
     */
    private SBOMType originFormat;

    /**
     * Specification version of the origin format
     * Example: For CycloneDX 1.4, this would be 1.4
     */
    private String specVersion;

    /**
     * Version of this sbom for the specific project (1 if first generated sbom, n if nthed)
     */
    private String sbomVersion;

    /**
     * Serial number of the sbom matching regex (^urn:uuid:[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$)
     */
    private String serialNumber;

    /**
     * The creator or manufacturer of the software the SBOM is about
     */
    private String supplier;

    /**
     * Date and time of when the SBOM was created
     */
    private String timestamp;

    /**
     * Signatures on the SBOM
     */
    private Set<Signature> signature;

    /**
     * Default constructor
     */
    public SBOM () {
        this.dependencyTree = new DependencyTree();
        this.signature = new HashSet<>();
        this.serialNumber = "urn:uuid:" + UUID.randomUUID().toString();
    }

    /**
     * Constructor to take all parameters except for DependencyTree
     *
     * @param originFormat : original format SBOM was sent in
     * @param specVersion  :  Version of this Object
     * @param sbomVersion  :  Version of the SBOM
     * @param author
     * @param serialNumber : Serial number of the SBOM
     * @param timestamp    :    Timestamp of when this SBOM was created
     * @param signature    :    signature to verify the SBOM
     */
    public SBOM(String originFormat, String specVersion, String sbomVersion, String author, String serialNumber, String timestamp, Set<Signature> signature) {
        this(originFormat, specVersion, sbomVersion, author, serialNumber, timestamp, signature, new DependencyTree());
    }

    /**
     * Takes all parameters, will be used when merging boms so the tree can be built manually
     *
     * @param originFormat : original format SBOM was sent in
     * @param specVersion  : Version of this Object
     * @param sbomVersion  : Version of the SBOM
     * @param serialNumber : Serial number of the SBOM
     * @param timestamp    : Timestamp of when this SBOM was created
     * @param supplier     : Manufacturer of the software the SBOM is about
     * @param signature    : signature to verify the SBOM
     */
    public SBOM(String originFormat, String specVersion, String sbomVersion, String supplier,
                String serialNumber, String timestamp, Set<Signature> signature, DependencyTree dependencyTree) {
        this.originFormat = assignOriginFormat(originFormat);
        this.specVersion = specVersion;
        this.sbomVersion = sbomVersion;
        this.supplier = supplier;
        this.dependencyTree = dependencyTree;
        this.serialNumber = serialNumber;
        this.timestamp = timestamp;
        this.signature = signature;
    }

    /**
     * Takes all parameters, will be used when merging boms so the tree can be built manually
     *
     * @param originFormat: original format SBOM was sent in
     * @param specVersion:  Version of this Object
     * @param sbomVersion:  Version of the SBOM
     * @param serialNumber: Serial number of the SBOM
     * @param timestamp:    Timestamp of when this SBOM was created
     * @param signature:    signature to verify the SBOM
     */
    public SBOM(SBOMType originFormat, String specVersion, String sbomVersion, String supplier, String serialNumber,
                String timestamp, Set<Signature> signature, DependencyTree dependencyTree) {
        this.originFormat = originFormat;
        this.specVersion = specVersion;
        this.sbomVersion = sbomVersion;
        this.supplier = supplier;
        this.dependencyTree = dependencyTree;
        this.serialNumber = serialNumber;
        this.timestamp = timestamp;
        this.signature = signature;
    }

    /**
     * Copy constructor for the SBOM (THIS DOES NOT COPY COMPONENTS)
     *
     * @param from SBOM to copy from
     */
    public SBOM(SBOM from) {
        // This sets the dependencytree to null so it does not allow copying of dependencies
        this(from.getOriginFormat(), from.getSpecVersion(), from.getSbomVersion(), from.getSupplier(), from.getSerialNumber(), from.getTimestamp(), from.getSignature(), null);
    }

    // TODO: Docstring
    public SBOM(String projectName) {
        // Creates an empty SBOM Object with a new DependencyTree for ParserController
        this((String) null, null, null, null, null, null, null, new DependencyTree());

        // TODO: Used to be new ParserComponent, may not initialize everything correctly
        // Creates a head component for the dependencies to exist in
        this.dependencyTree.addComponent(null, new Component(projectName));
    }

    /**
     * Get a set of all components in the project
     *
     * @return Set of components
     */
    public Set<Component> getAllComponents() {
        return dependencyTree.getAllComponents();
    }

    /**
     * Add a component into the dependency tree
     *
     * @param parent Parent UUID (null for root component)
     * @param toAdd  Component to add into the tree
     * @return UUID of added component (null if failed)
     */
    public UUID addComponent(UUID parent, Component toAdd) {
        return dependencyTree.addComponent(parent, toAdd);
    }

    /**
     * Add multiple components into the dependency tree
     *
     * @param parent Parent UUID (null for root component)
     * @param toAdd  Components to add into the tree
     * @return UUID of added component (null if failed)
     */
    public void addComponents(UUID parent, List<? extends Component> toAdd) {
        dependencyTree.addComponents(parent, toAdd);
    }

    /**
     * Get a component's children from the component's UUID
     *
     * @param parent UUID of the parent component
     * @return Set of children components (null if failed)
     */
    public Set<Component> getComponentChildren(UUID parent) {
        Set<Component> componentChildren;

        componentChildren = dependencyTree.getComponentChildren(parent);

        return componentChildren;
    }

    public Set<UUID> getChildrenUUIDs(UUID parent) {
        Set<UUID> componentChildren;

        componentChildren = dependencyTree.getChildrenUUIDs(parent);

        return componentChildren;

    }

    /**
     * Get the head component of the dependency tree
     *
     * @return Head component UUID (null if failed)
     */
    public UUID getHeadUUID() {
        return dependencyTree.getHeadUUID();
    }

    /**
     * Get a component by its UUID
     *
     * @param componentUUID UUID to retrieve
     * @return Component object (null if failed)
     */
    public Component getComponent(UUID componentUUID) {
        return dependencyTree.getComponent(componentUUID);
    }

    /**
     * Convert string to SBOM Type Enum
     *
     * @param format SBOM Format string to convert to Format Enum
     * @return SBOM Type
     */
    public SBOMType assignOriginFormat(String format) {
        if (format != null) {
            if (format.toLowerCase().contains("cyclonedx")) {
                return SBOMType.CYCLONE_DX;
            } else if (format.toLowerCase().contains("spdx")) {
                return SBOMType.SPDX;
            }
        }
        return SBOMType.Other;
    }

    /**
     * Recurse through the tree and sum component string code multiplied by the depth
     *
     * @param parent UUID of parent to loop through
     * @param depth  Current depth of the component
     * @return Integer that will be the hashcode of the dependencytree
     */
    private int sumComponents(UUID parent, int depth) {
        Component currentParent = this.getComponent(parent);
        // Calculate the initial value for this component
        int retVal = currentParent.getName().hashCode() * depth;
        if (currentParent.getChildren().size() == 0) {
            // If no children then sum and return
            return retVal;
        }

        // Otherwise, we need to recurse through all children and sum them
        for (UUID child : currentParent.getChildren()) {
            retVal += sumComponents(child, depth + 1);
        }

        return retVal;
    }

    ///
    /// Getters and Setters
    ///

    public SBOMType getOriginFormat() {
        return originFormat;
    }

    public void setOriginFormat(SBOMType originFormat) {
        this.originFormat = originFormat;
    }

    public String getSpecVersion() {
        return specVersion;
    }

    public void setSpecVersion(String specVersion) {
        this.specVersion = specVersion;
    }

    public String getSbomVersion() {
        return sbomVersion;
    }

    public void setSbomVersion(String sbomVersion) {
        this.sbomVersion = sbomVersion;
    }

    public String getSupplier() { return supplier; }

    public void setSupplier(String supplier) { this.supplier = supplier; }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Set<Signature> getSignature() {
        return signature;
    }

    public void setSignature(Set<Signature> signature) {
        this.signature = signature;
    }

    ///
    /// Overrides
    ///

    @Override
    public String toString() {
        return "\nSBOM Information\n" +
                "  + Serial Number: " + getSerialNumber() + "\n" +
                "  + Version: " + getSpecVersion() + "\n" +
                "  + Tool Version: " + getSbomVersion() + "\n" +
                "  + Time Stamp: " + getTimestamp() + "\n";
    }

    /**
     * Traverse the tree and add the name of the component times the depth of it and sum that together
     *
     * @return Hash of the SBOM
     */
    @Override
    public int hashCode() {
        int retVal = 0;
        // Get hashcode of SBOM params
        if (this.getOriginFormat() != null) {
            retVal += this.getSbomVersion().hashCode();
        }
        if (this.getTimestamp() != null) {
            retVal += this.getTimestamp().hashCode();
        }
        if (this.getSerialNumber() != null) {
            retVal += this.getSerialNumber().hashCode();
        }
        if (this.getSpecVersion() != null) {
            retVal += this.getSpecVersion().hashCode();
        }
        // Get hashcode of dependency tree
        if (this.dependencyTree != null && this.getAllComponents().size() > 0) {
            retVal += sumComponents(this.getHeadUUID(), 1);

        }

        // Now we can return
        return retVal;
    }

    /**
     * Check if the component is in the dependency tree
     *
     * @param componentUUID UUID of the component to check
     * @return True if in the tree, false otherwise
     */
    public boolean hasComponent(UUID componentUUID) {
        return dependencyTree.hasComponent(componentUUID);
    }

    /**
     * Make sure a dependency tree exists
     *
     * @return True if tree was initialized, false otherwise
     */
    public boolean hasDependencyTree() {
        return dependencyTree != null;
    }

}
