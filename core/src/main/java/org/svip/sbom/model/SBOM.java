package org.svip.sbom.model;

import java.util.*;

/**
 * File: SBOM.java
 * Represents a single component inside an SBOM
 *
 * @author Matt London
 * @author Kevin LaPorte
 * @author Ian Dunn
 */
public class SBOM {

    // Common SBOM Schemas
    public enum Type {
        CYCLONE_DX,
        SPDX,
        Other
    }

    /**
     * Dependency tree of the components
     */
    private final DependencyTree dependencyTree;

    /**
     * Type of SBOM which this object came from (whatever the bom.xml, bom.json was formatted in)
     */
    private Type originFormat;

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
     * Metadata of SBOM
     */
    private Map<String, String> metadata;


    /**
     *  Application tools
     */
    public Set<AppTool> appTools;


    /**
     * Default constructor
     */
    public SBOM () {
        this.dependencyTree = new DependencyTree();
        this.signature = new HashSet<>();
        this.serialNumber = "urn:uuid:" + UUID.randomUUID().toString();
        this.metadata = new HashMap<>();
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
        this.metadata = new HashMap<>();
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
    public SBOM(Type originFormat, String specVersion, String sbomVersion, String supplier, String serialNumber,
                String timestamp, Set<Signature> signature, DependencyTree dependencyTree) {
        this.originFormat = originFormat;
        this.specVersion = specVersion;
        this.sbomVersion = sbomVersion;
        this.supplier = supplier;
        this.dependencyTree = dependencyTree;
        this.serialNumber = serialNumber;
        this.timestamp = timestamp;
        this.signature = signature;
        this.metadata = new HashMap<>();
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

    /**
     * Constructs an SBOM with null values, except for the DependencyTree head component.
     *
     * @param headComponent The head component of the SBOM.
     */
    public SBOM(Component headComponent) {
        // Creates an empty SBOM Object with a new DependencyTree for ParserController
        this((String) null, null, null, null, null, null, null, new DependencyTree());

        // Creates a head component for the dependencies to exist in
        this.dependencyTree.addComponent(null, headComponent);
    }

    /**
     * Get the name of the head component of the SBOM, aka the bom/document name.
     *
     * @return The name of the SBOM.
     */
    public String getName() {
        return getComponent(getHeadUUID()).getName();
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
    public Type assignOriginFormat(String format) {
        if (format != null) {
            if (format.toLowerCase().contains("cyclonedx")) {
                return Type.CYCLONE_DX;
            } else if (format.toLowerCase().contains("spdx")) {
                return Type.SPDX;
            }
        }
        return Type.Other;
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

    public Type getOriginFormat() {
        return originFormat;
    }

    public void setOriginFormat(Type originFormat) {
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

    public void addMetadata(String k, String v){
        if(metadata == null)
            metadata = new HashMap<>();
        AppTool potentialTool = checkForTool(v);
        if(!getAppTools().contains(potentialTool))
            if(potentialTool != null)
                addAppTool(potentialTool);
            else metadata.put(k,v);
    }
    public void setMetadata(Map<String,String> md){
        for (String m: md.keySet()
        ) {
            addMetadata(m, md.get(m));
        }
    }
    public Map<String,String> getMetadata(){
        return metadata;
    }

    public Set<AppTool> getAppTools() {
        if(appTools == null)
            appTools = new HashSet<>();
        return appTools;
    }

    public void setAppTools(Set<AppTool> appTools) {
        this.appTools = appTools;
    }

    public void addAppTool(AppTool a){
        if(appTools == null)
            appTools = new HashSet<>();
        appTools.add(a);
    }

    ///
    /// Overrides
    ///

    @Override
    public String toString() {
        return "\nSBOM Information\n" +
                "  + Origin Format: " + getOriginFormat() + "\n" +
                "  + Specification Version: " + getSpecVersion() + "\n" +
                "  + SBOM Version: " + getSbomVersion() + "\n" +
                "  + Serial Number: " + getSerialNumber() + "\n" +
                "  + Supplier: " + getSupplier() + "\n" +
                "  + Time Stamp: " + getTimestamp() + "\n" +
                "  + Dependency Tree: " + dependencyTree + "\n";
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

    public AppTool checkForTool(String m){
        if(m.toLowerCase().startsWith("[tool")){
            String[] split = m.split("\\s+");
            return new AppTool(split[2], split[3], split[4]);
        }
        return null;
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
