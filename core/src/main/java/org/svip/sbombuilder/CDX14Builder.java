package org.svip.sbombuilder;

import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.interfaces.schemas.CycloneDX14.CDX14Package;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.shared.Relationship;
import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbom.model.shared.util.ExternalReference;

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
public class CDX14Builder implements CDX14SBOMBuilder {

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

    //TODO Service need implementation
    /**Holds the services of the SBOM*/
    // private Set<Service> services;

    //TODO addComposition needs implementation
    /**Holds the compositions of the SBOM*/
    // private Set<Composition> compositions;

    //TODO addSignature method needs implementation
    /**Holds the Signature of the SBOM*/
    // private Signature signature;


    @Override
    public CDX14SBOMBuilder addCDX14Package(CDX14Package cdx14Package) {
        return null;
    }

    @Override
    public CDX14SBOM buildCDX14SBOM() {
        return null;
    }

    @Override
    public SBOMBuilder setFormat(String format) {
        this.format = format;
        return null;
    }

    @Override
    public SBOMBuilder setName(String name) {
        this.name = name;
        return null;
    }

    @Override
    public SBOMBuilder setUID(String uid) {
        this.uid = uid;
        return null;
    }

    @Override
    public SBOMBuilder setVersion(String version) {
        this.version = version;
        return null;
    }

    @Override
    public SBOMBuilder setSpecVersion(String specVersion) {
        this.specVersion = specVersion;
        return null;
    }

    @Override
    public SBOMBuilder addLicense(String license) {
        this.licenses.add(license);
        return null;
    }

    @Override
    public SBOMBuilder setCreationData(CreationData creationData) {
        this.creationData = creationData;
        return null;
    }

    @Override
    public SBOMBuilder setDocumentComment(String documentComment) {
        this.documentComment = documentComment;
        return null;
    }

    @Override
    public SBOMBuilder setRootComponent(Component rootComponent) {
        this.rootComponent = rootComponent;
        return null;
    }

    @Override
    public SBOMBuilder addComponent(Component component) {
        this.components.add(component);
        return null;
    }

    @Override
    public SBOMBuilder addRelationship(String componentName, Relationship relationship) {
        Set<Relationship> relationships;
        if(this.relationships.containsKey(componentName)){
            relationships = this.relationships.get(componentName);

        } else{
            relationships = new HashSet<>();
        }
        relationships.add(relationship);
        this.relationships.put(componentName, relationships);

        return null;
    }

    @Override
    public SBOMBuilder addExternalReference(ExternalReference externalReference) {
        this.externalReferences.add(externalReference);
        return null;
    }

    @Override
    public SBOM Build() {
        return null;
    }

    public CDX14Builder CDX14Builder(){
        return null;
    }
}
