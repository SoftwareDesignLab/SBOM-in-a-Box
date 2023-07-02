package org.svip.builders.component;

import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.shared.util.LicenseCollection;

/**
 * file: SPDX23FileBuilder.java
 * Builder class for SPDX 2.3 file specifics
 *
 * @author Matthew Morrison
 */
public class SPDX23FileBuilder implements SPDX23FileBuilder_I{
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
    public SPDX23ComponentBuilder setComment(String comment) {
        return null;
    }

    @Override
    public SPDX23ComponentBuilder setAttributionText(String attributionText) {
        return null;
    }

    @Override
    public SPDX23FileBuilder_I setFileNotice(String fileNotice) {
        return null;
    }
}
