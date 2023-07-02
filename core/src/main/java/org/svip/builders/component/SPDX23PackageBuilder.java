package org.svip.builders.component;

import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.shared.util.Description;
import org.svip.sbom.model.shared.util.ExternalReference;
import org.svip.sbom.model.shared.util.LicenseCollection;

/**
 * file: SPDX23PackageBuilder.java
 * Builder class for SPDX 2.3 specific packages
 *
 * @author Matthew Morrison
 */
public class SPDX23PackageBuilder implements SPDX23PackageBuilder_I{
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

    @Override
    public SPDX23ComponentBuilder setComment(String comment) {
        return null;
    }

    @Override
    public SPDX23ComponentBuilder setAttributionText(String attributionText) {
        return null;
    }

    @Override
    public SPDX23PackageBuilder_I setDownloadLocation(String downloadLocation) {
        return null;
    }

    @Override
    public SPDX23PackageBuilder_I setFileName(String fileName) {
        return null;
    }

    @Override
    public SPDX23PackageBuilder_I setFilesAnalyzed(Boolean filesAnalyzed) {
        return null;
    }

    @Override
    public SPDX23PackageBuilder_I setVerificationCode(String verificationCode) {
        return null;
    }

    @Override
    public SPDX23PackageBuilder_I setHomePage(String homePage) {
        return null;
    }

    @Override
    public SPDX23PackageBuilder_I setSourceInfo(String sourceInfo) {
        return null;
    }

    @Override
    public SPDX23PackageBuilder_I setReleaseDate(String releaseDate) {
        return null;
    }

    @Override
    public SPDX23PackageBuilder_I setBuildDate(String buildDate) {
        return null;
    }

    @Override
    public SPDX23PackageBuilder_I setValidUntilDate(String validUntilDate) {
        return null;
    }
}
