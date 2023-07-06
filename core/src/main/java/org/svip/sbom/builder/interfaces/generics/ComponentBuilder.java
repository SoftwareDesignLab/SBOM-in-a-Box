package org.svip.sbom.builder.interfaces.generics;


import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.shared.util.LicenseCollection;

/**
 * file: ComponentBuilder.java
 * Generic interface for components in an SBOM that are common to both
 * SPDX and CycloneDX SBOMs
 *
 * @author Matthew Morrison
 */
public interface ComponentBuilder {

    /**
     * Set the component's type
     * @param type the designated type of component
     * @return a ComponentBuilder
     */
    ComponentBuilder setType(String type);

    /**
     * Set the component's UID
     * @param uid the uid of the component
     * @return a ComponentBuilder
     */
    ComponentBuilder setUID(String uid);

    /**
     * Set the component's author
     * @param author the author of the component
     * @return a ComponentBuilder
     */
    ComponentBuilder setAuthor(String author);

    /**
     * Set the component's name
     * @param name the name of the component
     * @return a ComponentBuilder
     */
    ComponentBuilder setName(String name);

    /**
     * Set the licenses associated with the component
     * @param licenses a collection of licenses
     * @return a ComponentBuilder
     */
    ComponentBuilder setLicenses(LicenseCollection licenses);

    /**
     * Set the component's copyright
     * @param copyright the copyright info of the component
     * @return a ComponentBuilder
     */
    ComponentBuilder setCopyright(String copyright);

    /**
     * Add a hash value to the component's info
     * @param algorithm the algorithm of the hash
     * @param hash the value of the hash
     * @return a ComponentBuilder
     */
    ComponentBuilder addHash(String algorithm, String hash);

    /**
     * Build the component
     * @return a new Component
     */
    Component build();

    /**
     * Build and flush the component
     * @return a new Component
     */
    Component buildAndFlush();
}
