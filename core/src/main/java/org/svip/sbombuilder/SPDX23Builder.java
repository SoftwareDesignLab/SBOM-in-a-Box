package org.svip.sbombuilder;

import jdk.jshell.Snippet;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23File;
import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23Package;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.sbom.model.shared.Relationship;
import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbom.model.shared.util.ExternalReference;
import org.svip.sbomvex.VEXFactory;

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
    public SBOMBuilder setFormat(String format)
    {
        this.format = format;
        return this;
    }

    @Override
    public SBOMBuilder setName(String name)
    {
        this.name = name;
        return this;
    }

    @Override
    public SBOMBuilder setUID(String uid)
    {
        this.uid = uid;
        return this;
    }

    @Override
    public SBOMBuilder setVersion(String version)
    {
        this.version = version;
        return this;
    }

    @Override
    public SBOMBuilder setSpecVersion(String specVersion)
    {
        this.specVersion = specVersion;
        return this;
    }

    @Override
    public SBOMBuilder addLicense(String license)
    {
        this.licenses.add(license);
        return this;
    }

    @Override
    public SBOMBuilder setCreationData(CreationData creationData)
    {
        this.creationData = creationData;
        return this;
    }

    @Override
    public SBOMBuilder setDocumentComment(String documentComment)
    {
        this.documentComment = documentComment;
        return this;
    }

    @Override
    public SBOMBuilder setRootComponent(Component rootComponent)
    {
        this.rootComponent = rootComponent;
        return this;
    }

    @Override
    public SBOMBuilder addComponent(Component component)
    {
        this.components.add(component);
        return this;
    }

    @Override
    public SBOMBuilder addRelationship(String componentName, Relationship relationship)
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
    public SBOMBuilder addExternalReference(ExternalReference externalReference)
    {
        this.externalReferences.add(externalReference);
        return this;
    }

    @Override
    public SPDX23SBOMBuilder setSPDXLicenseListVersion(String licenseListVersion) {
        this.version = version;
        return this;
    }

    /** adds an SPDX 2.3 package to the components list */
    @Override
    public SPDX23SBOMBuilder addSPDX23Package(SPDX23Package spdx23Package) {
        this.components.add(spdx23Package);
        return this;
    }

    /** adds an SPDX 2.3 file to the components list */
    @Override
    public SPDX23SBOMBuilder addSPDX23File(SPDX23File spdx23File) {
        this.components.add(spdx23File);
        return this;
    }

    /** the current SBOM classes don't seem to have constructors */
    @Override
    public SBOM Build() {return null;}
    @Override
    public SPDX23SBOM buildSPDX23SBOM() {return null;}
}
