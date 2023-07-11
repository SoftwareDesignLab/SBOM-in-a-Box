package org.svip.sbom.builder.objects.schemas.SPDX23;

import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23Component;
import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23Package;
import org.svip.sbom.model.objects.SPDX23.SPDX23PackageObject;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.sbom.model.shared.Relationship;
import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbom.model.shared.util.ExternalReference;
import org.svip.sbom.builder.interfaces.generics.SBOMBuilder;
import org.svip.sbom.builder.interfaces.schemas.SPDX23.SPDX23SBOMBuilder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * file: SPDX23Builder.java
 * Class for the SPDX 2.3 SBOM Builder
 *
 * @author Thomas Roman
 */
public class SPDX23Builder implements SPDX23SBOMBuilder {
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
    Set<Snippet> snippets;
    Set<LicenseInfo> additionalLicenseInformation;
     Set<Annotation> annotationInformation;</Annotation>*/
    private String SPDXLicenseListVersion;
    
    @Override
    public SPDX23Builder setFormat(String format)
    {
        this.format = format;
        return this;
    }

    @Override
    public SPDX23Builder setName(String name)
    {
        this.name = name;
        return this;
    }

    @Override
    public SPDX23Builder setUID(String uid)
    {
        this.uid = uid;
        return this;
    }

    @Override
    public SPDX23Builder setVersion(String version)
    {
        this.version = version;
        return this;
    }

    @Override
    public SPDX23Builder setSpecVersion(String specVersion)
    {
        this.specVersion = specVersion;
        return this;
    }

    @Override
    public SPDX23Builder addLicense(String license)
    {
        // initialize the hash set
        if (this.licenses == null) {
            this.licenses = new HashSet<String>();
        }
        this.licenses.add(license);
        return this;
    }

    @Override
    public SPDX23Builder setCreationData(CreationData creationData)
    {
        this.creationData = creationData;
        return this;
    }

    @Override
    public SPDX23Builder setDocumentComment(String documentComment)
    {
        this.documentComment = documentComment;
        return this;
    }

    @Override
    public SPDX23Builder setRootComponent(Component rootComponent) {
        this.rootComponent = rootComponent;
        return this;
    }

    @Override
    public SPDX23Builder addComponent(Component component) {
        // initialize the hash set
        if (this.components == null) {
            this.components = new HashSet<Component>();
        }
        this.components.add(component);
        return this;
    }

    @Override
    public SPDX23Builder addSPDX23Component(SPDX23Component component) {
        this.components.add(component);
        return this;
    }

    @Override
    public SPDX23Builder addRelationship(String componentName, Relationship relationship)
    {
        if (this.relationships == null)
            this.relationships = new HashMap<>();

        if( !relationships.containsKey(componentName))
            this.relationships.put(componentName, new HashSet<>());

        this.relationships.get(componentName).add(relationship);
        return this;
    }

    @Override
    public SPDX23Builder addExternalReference(ExternalReference externalReference)
    {
        if(this.externalReferences == null)
            this.externalReferences = new HashSet<ExternalReference>();
        this.externalReferences.add(externalReference);
        return this;
    }

    @Override
    public SPDX23Builder setSPDXLicenseListVersion(String licenseListVersion) {
        this.SPDXLicenseListVersion = licenseListVersion;
        return this;
    }

    @Override
    public SBOM Build() {
        return new SPDX23SBOM(format, name, uid, version, specVersion, licenses, creationData, documentComment,
                (SPDX23PackageObject) rootComponent, components, relationships, externalReferences, SPDXLicenseListVersion);
    }

    @Override
    public SPDX23SBOM buildSPDX23SBOM() {
        return new SPDX23SBOM(format, name, uid, version, specVersion, licenses, creationData, documentComment,
                (SPDX23PackageObject) rootComponent, components, relationships, externalReferences, SPDXLicenseListVersion);
    }
}
