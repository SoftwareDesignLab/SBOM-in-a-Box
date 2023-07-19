package org.svip.sbom.builder.objects;

import org.svip.sbom.builder.interfaces.schemas.CycloneDX14.CDX14SBOMBuilder;
import org.svip.sbom.builder.interfaces.schemas.SPDX23.SPDX23SBOMBuilder;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.schemas.CycloneDX14.CDX14Package;
import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23Component;
import org.svip.sbom.model.objects.CycloneDX14.CDX14ComponentObject;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.objects.SPDX23.SPDX23PackageObject;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.sbom.model.objects.SVIPComponentObject;
import org.svip.sbom.model.objects.SVIPSBOM;
import org.svip.sbom.model.shared.Relationship;
import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbom.model.shared.util.ExternalReference;

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
    private Set<String> licenses = new HashSet<>();
    private CreationData creationData;
    private String documentComment;
    private Component rootComponent;
    private Set<Component> components = new HashSet<>();
    private HashMap<String, Set<Relationship>> relationships = new HashMap<>();
    private Set<ExternalReference> externalReferences = new HashSet<>();
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
        this.components.add(cdx14Package);
        return this;
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
        if( !relationships.containsKey(componentName))
            this.relationships.put(componentName, new HashSet<>());

        this.relationships.get(componentName).add(relationship);
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
    public SVIPSBOMBuilder addSPDX23Component(SPDX23Component component) {
        this.components.add(component);
        return this;
    }

    /** TO DO: add constructors to SBOM */
    @Override
    public SVIPSBOM Build() {
        return new SVIPSBOM(format, name, uid, version, specVersion, licenses, creationData, documentComment, (SVIPComponentObject) rootComponent, components, relationships,
                externalReferences, SPDXLicenseListVersion);
    }
    @Override
    public SPDX23SBOM buildSPDX23SBOM() {
        return new SPDX23SBOM(format, name, uid, version, specVersion, licenses, creationData, documentComment,
                (SPDX23PackageObject) rootComponent, components, relationships, externalReferences, SPDXLicenseListVersion);
    }
    @Override
    public CDX14SBOM buildCDX14SBOM() {
        return new CDX14SBOM(format, name, uid, version,
                specVersion, licenses,
                creationData, documentComment,
                (CDX14ComponentObject) rootComponent, components,
                relationships, externalReferences);
    }
}
