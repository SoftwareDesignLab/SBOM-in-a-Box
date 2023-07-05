package org.svip.sbom.builder.interfaces.generics;

import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.shared.Relationship;
import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbom.model.shared.util.ExternalReference;

/**
 * file: SBOMBuilder.java
 * Interface for SBOM Builder
 *
 * @author Thomas Roman
 */
public interface SBOMBuilder {
    /**
     * Set the SBOM's format
     * @param format
     * @return an SBOMBuilder
     */
    SBOMBuilder setFormat(String format);

    /**
     * Set the SBOMBuilder's name
     * @param name
     * @return an SBOMBuilder
     */
    SBOMBuilder setName(String name);

    /**
     * Set the unique identifier for the SBOMBuilder
     * @param uid
     * @return an SBOMBuilder
     */
    SBOMBuilder setUID(String uid);

    /**
     * Set the SBOMBuilder's version
     * @param version
     * @return an SBOMBuilder
     */
    SBOMBuilder setVersion(String version);
    /**
     * Set the SBOMBuilder's specVersion
     * @param specVersion
     * @return an SBOMBuilder
     */
    SBOMBuilder setSpecVersion(String specVersion);
    /**
     * Add a license to the SBOMBuilder
     * @param license
     * @return an SBOMBuilder
     */
    SBOMBuilder addLicense(String license);
    /**
     * Set the SBOMBuilder's creation data
     * @param creationData
     * @return an SBOMBuilder
     */
    SBOMBuilder setCreationData(CreationData creationData);
    /**
     * Set a document comment for the SBOMBuilder
     * @param documentComment
     * @return an SBOMBuilder
     */
    SBOMBuilder setDocumentComment(String documentComment);
    /**
     * Set the root component of the SBOMBuilder
     * @param rootComponent
     * @return an SBOMBuilder
     */
    SBOMBuilder setRootComponent(Component rootComponent);
    /**
     * Add a component to the SBOMBuilder
     * @param component
     * @return an SBOMBuilder
     */
    SBOMBuilder addComponent(Component component);

    /**
     * Add relationships between SBOMBuilder components
     * @param componentName
     * @param relationship
     * @return an SBOMBuilder
     */
    SBOMBuilder addRelationship(String componentName, Relationship relationship);
    /**
     * Add external reference data to the SBOMBuilder
     * @param externalReference
     * @return an SBOMBuilder
     */
    SBOMBuilder addExternalReference(ExternalReference externalReference);

    /**
     * Build an SBOM from the data in the SBOMBUILDER
     * @return SBOM
     */
    SBOM Build();
}
