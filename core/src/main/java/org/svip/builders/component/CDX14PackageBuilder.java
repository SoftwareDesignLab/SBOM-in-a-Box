package org.svip.builders.component;

import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.shared.util.Description;
import org.svip.sbom.model.shared.util.ExternalReference;
import org.svip.sbom.model.shared.util.LicenseCollection;

/**
 * file: CDX14PackageBuilder.java
 * Builder class for CycloneDX 1.4 Packages
 *
 * @author Matthew Morrison
 */
public class CDX14PackageBuilder implements CDX14PackageBuilder_I{

    @Override
    public CDX14PackageBuilder_I setMimeType(String mimeType) {
        return null;
    }

    @Override
    public CDX14PackageBuilder_I setPublisher(String publisher) {
        return null;
    }

    @Override
    public CDX14PackageBuilder_I setScope(String scope) {
        return null;
    }

    @Override
    public CDX14PackageBuilder_I setGroup(String group) {
        return null;
    }

    @Override
    public CDX14PackageBuilder_I addExternalReferences(ExternalReference externalReference) {
        return null;
    }

    @Override
    public CDX14PackageBuilder_I addProperty(String name, String value) {
        return null;
    }

    @Override
    public ComponentBuilder setType(String type) {
        return null;
    }

    @Override
    public ComponentBuilder setUID(String uid) {
        return null;
    }

    @Override
    public ComponentBuilder setAuthor(String author) {
        return null;
    }

    @Override
    public ComponentBuilder setName(String name) {
        return null;
    }

    @Override
    public ComponentBuilder setLicenses(LicenseCollection licenses) {
        return null;
    }

    @Override
    public ComponentBuilder setCopyright(String copyright) {
        return null;
    }

    @Override
    public ComponentBuilder addHash(String algorithm, String hash) {
        return null;
    }

    @Override
    public Component build() {
        return null;
    }

    @Override
    public Component buildAndFlush() {
        return null;
    }

    @Override
    public SBOMComponentBuilder setSupplier(Organization supplier) {
        return null;
    }

    @Override
    public SBOMComponentBuilder setVersion(String version) {
        return null;
    }

    @Override
    public SBOMComponentBuilder setDescription(Description description) {
        return null;
    }

    @Override
    public SBOMComponentBuilder addCPE(String cpe) {
        return null;
    }

    @Override
    public SBOMComponentBuilder addPURL(String purl) {
        return null;
    }

    @Override
    public SBOMComponentBuilder addExternalReference(ExternalReference externalReference) {
        return null;
    }
}
