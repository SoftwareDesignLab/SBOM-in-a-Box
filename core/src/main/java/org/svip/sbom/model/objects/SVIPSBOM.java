package org.svip.sbom.model.objects;

import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.schemas.CycloneDX14.CDX14Schema;
import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23Schema;
import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbom.model.shared.Relationship;
import org.svip.sbom.model.shared.util.ExternalReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * file: SVIPSBOM.java
 * Used to file for SVIP SBOM Generation
 *
 * @author Derek Garcia
 * @author Matthew Morrison
 */
// todo
public class SVIPSBOM implements CDX14Schema, SPDX23Schema{

    private String format;

    private String name;

    private String uid;

    private String version;

    private String specVersion;

    private Set<String> licenses;

    private CreationData creationData;

    private String documentComment;

    private Component rootComponent;

    private Set<Component> components;

    private HashMap<String, Set<Relationship>> relationships;

    private Set<ExternalReference> externalReferences;

    // TODO VEX needs implementation
    // private Set<VEX> vulnerabilities;

    // TODO Service needs implementation
    // private Set<Service> services;

    // TODO Composition needs implementation
    // private Set<Composition> compositions;

    // TODO Signature needs implementation
    // private Signature signature;


    // TODO Snippet needs implementation
    // private Set<Snippet> snippets;

    // TODO LicenseInfo needs implementation
    // private Set<LicenseInfo> additionalLicenseInformation;

    // TODO Annotation needs implementation
    // private Set<Annotation> annotationInformation;

    private String SPDXLicenseListVersion;

    @Override
    public String getFormat() {
        return this.format;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getUID() {
        return this.uid;
    }

    @Override
    public String getVersion() {
        return this.version;
    }

    @Override
    public String getSpecVersion() {
        return this.specVersion;
    }

    @Override
    public Set<String> getLicenses() {
        return this.licenses;
    }

    @Override
    public CreationData getCreationData() {
        return this.creationData;
    }

    @Override
    public String getDocumentComment() {
        return this.documentComment;
    }

    @Override
    public Map<String, Set<Relationship>> getRelationships() {
        return this.relationships;
    }

    @Override
    public Set<ExternalReference> getExternalReferences() {
        return this.externalReferences;
    }

    @Override
    public String getSPDXLicenseListVersion() {
        return this.SPDXLicenseListVersion;
    }


    //TODO add missing fields when implemented (VEX, Service, Composition, Signature, Snippet, LicenseInfo, Annotation)
    public SVIPSBOM(String format, String name, String uid, String version,
                      String specVersion, Set<String> licenses,
                      CreationData creationData, String documentComment,
                      Component rootComponent, Set<Component> components,
                      HashMap<String, Set<Relationship>> relationships,
                      Set<ExternalReference> externalReferences,
                      String spdxLicenseListVersion){
        this.format = format;
        this.name = name;
        this.uid = uid;
        this.version = version;
        this.specVersion = specVersion;
        this.licenses = licenses;
        this.creationData = creationData;
        this.documentComment = documentComment;
        this.rootComponent = rootComponent;
        this.components = components;
        this.relationships = relationships;
        this.externalReferences = externalReferences;
        this.SPDXLicenseListVersion = spdxLicenseListVersion;

    }
}
