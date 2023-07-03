package org.svip.sbombuilders;

import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.interfaces.schemas.CycloneDX14.CDX14Package;
import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23File;
import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23Package;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.sbom.model.shared.Relationship;
import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbom.model.shared.util.ExternalReference;
import org.svip.sbombuilder.interfaces.CDX14SBOMBuilder;
import org.svip.sbombuilder.interfaces.SBOMBuilder;
import org.svip.sbombuilder.interfaces.SPDX23SBOMBuilder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * file: SPDX23Builder.java
 * Class for the SVIP SBOM Builder
 *
 * @author Thomas Roman
 */
public class SVIPSBOMBuilder implements CDX14SBOMBuilder, SPDX23SBOMBuilder {

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
    /** Set<VEX> vulnerabilities;
     Set<Service> services;
     Set<Composition> compositions;
     Signature signature;
     Set<Snippet> snippets;
     Set<LicenseInfo> additionalLicenseInformation;
     Set<Annotation> annotationInformation;</Annotation>*/
    private String SPDXLicenseListVersion;
    @Override
    public SVIPSBOMBuilder addCDX14Package(CDX14Package cdx14Package) {
        /** cannot be added to components list because the CDX14Package does not inherit Component */
        return null;
    }

    @Override
    public SVIPSBOMBuilder setFormat(String format) {
        this.format = format;
        return this;
    }

    @Override
    public SVIPSBOMBuilder setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public SVIPSBOMBuilder setUID(String uid) {
        this.uid = uid;
        return this;
    }

    @Override
    public SVIPSBOMBuilder setVersion(String version) {
        this.version = version;
        return this;
    }

    @Override
    public SVIPSBOMBuilder setSpecVersion(String specVersion) {
        this.specVersion = specVersion;
        return this;
    }

    @Override
    public SVIPSBOMBuilder addLicense(String license) {
        this.licenses.add(license);
        return this;
    }

    @Override
    public SVIPSBOMBuilder setCreationData(CreationData creationData) {
        this.creationData = creationData;
        return this;
    }

    @Override
    public SVIPSBOMBuilder setDocumentComment(String documentComment) {
        this.documentComment = documentComment;
        return this;
    }

    @Override
    public SVIPSBOMBuilder setRootComponent(Component rootComponent) {
        this.rootComponent = rootComponent;
        return this;
    }

    @Override
    public SVIPSBOMBuilder addComponent(Component component) {
        this.components.add(component);
        return this;
    }

    @Override
    public SVIPSBOMBuilder addRelationship(String componentName, Relationship relationship) {
        /** create a temporary list to maintain all the previously established relationships*/
        Set<Relationship> relationshipList = new HashSet<Relationship>();
        if (relationships.containsKey(componentName)) {
            relationshipList.addAll(relationships.get(componentName));
        }
        relationshipList.add(relationship);
        this.relationships.put(componentName, relationshipList);
        return this;
    }

    @Override
    public SVIPSBOMBuilder addExternalReference(ExternalReference externalReference) {
        this.externalReferences.add(externalReference);
        return this;
    }

    @Override
    public SVIPSBOMBuilder setSPDXLicenseListVersion(String licenseListVersion) {
        this.SPDXLicenseListVersion = licenseListVersion;
        return this;
    }

    @Override
    public SVIPSBOMBuilder addSPDX23Package(SPDX23Package spdx23Package) {
        this.components.add(spdx23Package);
        return this;
    }

    @Override
    public SVIPSBOMBuilder addSPDX23File(SPDX23File spdx23File) {
        this.components.add(spdx23File);
        return this;
    }

    @Override
    public SBOM Build() {
        return null;
    }

    @Override
    public SPDX23SBOM buildSPDX23SBOM() {
        return null;
    }

    @Override
    public CDX14SBOM buildCDX14SBOM() {
        return null;
    }
}
