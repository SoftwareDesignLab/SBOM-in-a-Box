package org.svip.componentfactory;

import org.svip.builders.component.ComponentBuilder;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.shared.util.LicenseCollection;

/**
 * file: CDX14PackageBuilderFactory.java
 * Class to build CycloneDX specific packages
 *
 * @author Matthew Morrison
 */
public class CDX14PackageBuilderFactory implements ComponentBuilderFactory {

    /**
     * Set the CycloneDX package's type
     * @param type the designated type of component
     * @return a ComponentBuilder
     */
    @Override
    public ComponentBuilder setType(String type) {
        return null;
    }

    /**
     * Set the CycloneDX package's UID
     * @param uid the uid of the component
     * @return a ComponentBuilder
     */
    @Override
    public ComponentBuilder setUID(String uid) {
        return null;
    }

    /**
     * Set the CycloneDX package's author
     * @param author the author of the component
     * @return a ComponentBuilder
     */
    @Override
    public ComponentBuilder setAuthor(String author) {
        return null;
    }

    /**
     * Set the CycloneDX package's name
     * @param name the name of the component
     * @return a ComponentBuilder
     */
    @Override
    public ComponentBuilder setName(String name) {
        return null;
    }

    /**
     * Set the CycloneDX package's licenses
     * @param licenses a collection of licenses
     * @return a ComponentBuilder
     */
    @Override
    public ComponentBuilder setLicenses(LicenseCollection licenses) {
        return null;
    }

    /**
     * Set the CycloneDX package's copyright info
     * @param copyright the copyright info of the component
     * @return a ComponentBuilder
     */
    @Override
    public ComponentBuilder setCopyright(String copyright) {
        return null;
    }

    /**
     * Add a hash to the CycloneDX package
     * @param algorithm the algorithm of the hash
     * @param hash the value of the hash
     * @return a ComponentBuilder
     */
    @Override
    public ComponentBuilder addHash(String algorithm, String hash) {
        return null;
    }

    /**
     * Build the CycloneDX package
     * @return a Component
     */
    @Override
    public Component build() {
        return null;
    }

    /**
     * Build and flush the CycloneDX package
     * @return a Component
     */
    @Override
    public Component buildAndFlush() {
        return null;
    }

    /**
     * Create a new Builder
     * @return a ComponentBuilder
     */
    @Override
    public ComponentBuilder createBuilder() {
        return null;
    }
}
