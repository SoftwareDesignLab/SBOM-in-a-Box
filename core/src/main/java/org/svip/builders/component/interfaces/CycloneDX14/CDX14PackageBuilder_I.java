package org.svip.builders.component.interfaces.CycloneDX14;

import org.svip.builders.component.interfaces.generics.PackageBuilder;
import org.svip.sbom.model.shared.util.ExternalReference;

/**
 * file: CDX14PackageBuilder_I.java
 * Generic Package Builder interface for CycloneDX 1.4
 * SBOM components
 *
 * @author Matthew Morrison
 */
public interface CDX14PackageBuilder_I extends PackageBuilder {

    /**
     * Set the mime type of the package
     * @param mimeType the package's mime type
     * @return a CDX14PackageBuilder_I
     */
    CDX14PackageBuilder_I setMimeType(String mimeType);

    /**
     * Set the publisher of the package
     * @param publisher the package's publisher
     * @return a CDX14PackageBuilder_I
     */
    CDX14PackageBuilder_I setPublisher(String publisher);

    /**
     * Set the scope of the package
     * @param scope the package's scope
     * @return a CDX14PackageBuilder_I
     */
    CDX14PackageBuilder_I setScope(String scope);

    /**
     * Set the group for the package
     * @param group the package's group
     * @return a CDX14PackageBuilder_I
     */
    CDX14PackageBuilder_I setGroup(String group);

    /**
     * Add an external reference to the package
     * @param externalReference a package's external reference
     * @return a CDX14PackageBuilder_I
     */
    CDX14PackageBuilder_I addExternalReferences(ExternalReference externalReference);


    /**
     * Add a property to the package
     * @param name the name of the property
     * @param value the value of the property
     * @return a CDX14PackageBuilder_I
     */
    CDX14PackageBuilder_I addProperty(String name, String value);
}
