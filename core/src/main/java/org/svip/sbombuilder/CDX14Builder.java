package org.svip.sbombuilder;

import org.cyclonedx.model.Composition;
import org.cyclonedx.model.Signature;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.interfaces.schemas.CycloneDX14.CDX14Package;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.shared.Relationship;
import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbom.model.shared.util.ExternalReference;
import org.svip.sbombuilder.interfaces.CDX14SBOMBuilder;
import org.svip.sbombuilder.interfaces.SBOMBuilder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * file: CDX14Builder.java
 * Class that builds a new CycloneDX 1.4 SBOM using the
 * CDX14SBOMBuilder interface
 *
 * @author Matthew Morrison
 */
public class CDX14Builder implements CDX14SBOMBuilder{

    /**Holds the format of the SBOM*/
    private String format;

    /**Holds the name of the SBOM*/
    private String name;

    /**Holds the UID of the SBOM*/
    private String uid;

    /**Holds the version of the SBOM*/
    private String version;

    /**Holds the spec version of the SBOM*/
    private String specVersion;

    /**Holds the licenses of the SBOM*/
    private Set<String> licenses;

    /**Holds the creation data of the SBOM*/
    private CreationData creationData;

    /**Holds the document comments of the SBOM*/
    private String documentComment;

    /**Holds the root component of the SBOM*/
    private Component rootComponent;

    /**Holds the components of the SBOM*/
    private Set<Component> components;

    /**Holds the relationships of components in the SBOM*/
    private HashMap<String, Set<Relationship>> relationships;

    /**Holds the external references of the SBOM*/
    private Set<ExternalReference> externalReferences;

    //TODO VEX needs implementation
    /**Holds the vulnerabilities expressed in the SBOM*/
    // private Set<VEX> vulnerabilities

    //TODO Service needs implementation
    /**Holds the services of the SBOM*/
    // private Set<Service> services;

    //TODO Composition needs implementation
    /**Holds the compositions of the SBOM*/
    // private Set<Composition> compositions;

    //TODO Signature needs implementation
    /**Holds the Signature of the SBOM*/
    // private Signature signature;

    /**
     * Set the SBOM's format
     * @param format the SBOM format
     * @return a CDX14Builder
     */
    @Override
    public CDX14Builder setFormat(String format) {
        this.format = format;
        return this;
    }

    /**
     * Set the SBOM's name
     * @param name the name
     * @return a CDX14Builder
     */
    @Override
    public CDX14Builder setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Set the SBOM's UID
     * @param uid the UID
     * @return a CDX14Builder
     */
    @Override
    public CDX14Builder setUID(String uid) {
        this.uid = uid;
        return this;
    }

    /**
     * Set the SBOM's version
     * @param version the version
     * @return a CDX14Builder
     */
    @Override
    public CDX14Builder setVersion(String version) {
        this.version = version;
        return this;
    }

    /**
     * Set the SBOM's spec version
     * @param specVersion the spec version
     * @return a CDX14Builder
     */
    @Override
    public CDX14Builder setSpecVersion(String specVersion) {
        this.specVersion = specVersion;
        return this;
    }

    /**
     * Add a license to the SBOM
     * @param license the license to add
     * @return a CDX14Builder
     */
    @Override
    public CDX14Builder addLicense(String license) {
        this.licenses.add(license);
        return this;
    }

    /**
     * Set the SBOM's creation data
     * @param creationData the creation data
     * @return a CDX14Builder
     */
    @Override
    public CDX14Builder setCreationData(CreationData creationData) {
        this.creationData = creationData;
        return this;
    }

    /**
     * Set the SBOM's document comment
     * @param documentComment the document comment
     * @return a CDX14Builder
     */
    @Override
    public CDX14Builder setDocumentComment(String documentComment) {
        this.documentComment = documentComment;
        return this;
    }

    /**
     * Set the root component of the SBOM
     * @param rootComponent the root component
     * @return a CDX14Builder
     */
    @Override
    public CDX14Builder setRootComponent(Component rootComponent) {
        this.rootComponent = rootComponent;
        return this;
    }

    /**
     * Add a component to an SBOM
     * @param component the component to add
     * @return a CDX14Builder
     */
    @Override
    public CDX14Builder addComponent(Component component) {
        this.components.add(component);
        return this;
    }

    /**
     * Add a relationship to the SBOM
     * @param componentName the component name
     * @param relationship the relationship
     * @return a CDX14Builder
     */
    @Override
    public CDX14Builder addRelationship(String componentName, Relationship relationship) {
        if (this.relationships == null)
            this.relationships = new HashMap<>();

        if( !relationships.containsKey(componentName))
            this.relationships.put(componentName, new HashSet<>());

        this.relationships.get(componentName).add(relationship);
        return this;
    }

    /**
     * Add an external reference to the SBOM
     * @param externalReference the external reference
     * @return a CDX14Builder
     */
    @Override
    public CDX14Builder addExternalReference(ExternalReference externalReference) {
        this.externalReferences.add(externalReference);
        return this;
    }

    //TODO add addService, addVulnerability, addComposition, addSignature when implemented

    /**
     * Build a new SBOM
     * @return an SBOM Object
     */
    //TODO implement once SBOM refactor is complete
    @Override
    public SBOM Build() {
        return null;
    }

    /**
     * Build the CycloneDX 1.4 SBOM
     * @return a CDX14SBOM object
     */
    //TODO implement once SBOM refactor is complete
    @Override
    public CDX14SBOM buildCDX14SBOM() {
        return null;
    }
}
