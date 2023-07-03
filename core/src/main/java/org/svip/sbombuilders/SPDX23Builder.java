package org.svip.sbombuilders;

import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23File;
import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23Package;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.sbom.model.shared.Relationship;
import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbom.model.shared.util.ExternalReference;
import org.svip.sbombuilder.interfaces.SPDX23SBOMBuilder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * file: SPDX23Builder.java
 * Class for the SPDX 2.3 SBOM Builder
 *
 * @author Thomas Roman
 */
public abstract class SPDX23Builder implements SPDX23SBOMBuilder {
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
    public SPDX23Builder setRootComponent(Component rootComponent)
    {
        this.rootComponent = rootComponent;
        return this;
    }

    @Override
    public SPDX23Builder addComponent(Component component)
    {
        this.components.add(component);
        return this;
    }

    @Override
    public SPDX23Builder addRelationship(String componentName, Relationship relationship)
    {
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
    public SPDX23Builder addExternalReference(ExternalReference externalReference)
    {
        this.externalReferences.add(externalReference);
        return this;
    }

    @Override
    public SPDX23Builder setSPDXLicenseListVersion(String licenseListVersion) {
        this.SPDXLicenseListVersion = licenseListVersion;
        return this;
    }

    /** adds an SPDX 2.3 package to the components list */
    @Override
    public SPDX23Builder addSPDX23Package(SPDX23Package spdx23Package) {
        this.components.add(spdx23Package);
        return this;
    }

    /** adds an SPDX 2.3 file to the components list */
    @Override
    public SPDX23Builder addSPDX23File(SPDX23File spdx23File) {
        this.components.add(spdx23File);
        return this;
    }

    /** the current SBOM classes don't seem to have constructors */
    @Override
    public SBOM Build() {return null;}
    @Override
    public SPDX23SBOM buildSPDX23SBOM() {return null;}
}
