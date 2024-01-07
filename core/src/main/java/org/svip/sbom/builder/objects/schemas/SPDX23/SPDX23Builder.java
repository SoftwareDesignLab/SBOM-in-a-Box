/** Copyright 2021 Rochester Institute of Technology (RIT). Developed with
* government support under contract 70RCSA22C00000008 awarded by the United
* States Department of Homeland Security for Cybersecurity and Infrastructure Security Agency.
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the “Software”), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
 */

package org.svip.sbom.builder.objects.schemas.SPDX23;

import org.svip.sbom.builder.interfaces.schemas.SPDX23.SPDX23SBOMBuilder;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23Component;
import org.svip.sbom.model.objects.SPDX23.SPDX23PackageObject;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.sbom.model.shared.Relationship;
import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbom.model.shared.util.ExternalReference;

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
    private final Set<String> licenses = new HashSet<>();
    private CreationData creationData;
    private String documentComment;
    private Component rootComponent;
    private final Set<Component> components = new HashSet<>();
    private final HashMap<String, Set<Relationship>> relationships = new HashMap<>();
    private final Set<ExternalReference> externalReferences = new HashSet<>();
    /**
     * Set<VEX> vulnerabilities;
     * Set<Snippet> snippets;
     * Set<LicenseInfo> additionalLicenseInformation;
     * Set<Annotation> annotationInformation;</Annotation>
     */
    private String SPDXLicenseListVersion;

    @Override
    public SPDX23Builder setFormat(String format) {
        this.format = format;
        return this;
    }

    @Override
    public SPDX23Builder setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public SPDX23Builder setUID(String uid) {
        this.uid = uid;
        return this;
    }

    @Override
    public SPDX23Builder setVersion(String version) {
        this.version = version;
        return this;
    }

    @Override
    public SPDX23Builder setSpecVersion(String specVersion) {
        this.specVersion = specVersion;
        return this;
    }

    @Override
    public SPDX23Builder addLicense(String license) {
        this.licenses.add(license);
        return this;
    }

    @Override
    public SPDX23Builder setCreationData(CreationData creationData) {
        this.creationData = creationData;
        return this;
    }

    @Override
    public SPDX23Builder setDocumentComment(String documentComment) {
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
        this.components.add(component);
        return this;
    }

    @Override
    public SPDX23Builder addSPDX23Component(SPDX23Component component) {
        this.components.add(component);
        return this;
    }

    @Override
    public SPDX23Builder addRelationship(String componentName, Relationship relationship) {
        if (!relationships.containsKey(componentName))
            this.relationships.put(componentName, new HashSet<>());

        this.relationships.get(componentName).add(relationship);
        return this;
    }

    @Override
    public SPDX23Builder addExternalReference(ExternalReference externalReference) {
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
