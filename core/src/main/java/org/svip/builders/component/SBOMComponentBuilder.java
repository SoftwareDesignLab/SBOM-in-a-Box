package org.svip.builders.component;

import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.shared.util.Description;
import org.svip.sbom.model.shared.util.ExternalReference;

/**
 * file: SBOMComponent.java
 * Generic component details that many components would share
 * regardless of SBOM type
 *
 * @author Matthew Morrison
 */
public interface SBOMComponentBuilder extends ComponentBuilder{

    /**
     * Set the supplier of the component
     * @param supplier the component's supplier
     * @return an SBOMComponentBuilder
     */
    SBOMComponentBuilder setSupplier(Organization supplier);

    /**
     * Set the version of the component
     * @param version the component's version
     * @return an SBOMComponentBuilder
     */
    SBOMComponentBuilder setVersion(String version);

    /**
     * Set teh description of the component
     * @param description the component's description
     * @return an SBOMComponentBuilder
     */
    SBOMComponentBuilder setDescription(Description description);

    /**
     * Add a CPE to the component
     * @param cpe the cpe string to add
     * @return an SBOMComponentBuilder
     */
    SBOMComponentBuilder addCPE(String cpe);

    /**
     * Add a PURL to the component
     * @param purl the purl string to add
     * @return an SBOMComponentBuilder
     */
    SBOMComponentBuilder addPURL(String purl);

    /**
     * Ann an external reference to the component
     * @param externalReference the external component to add
     * @return an SBOMComponentBuilder
     */
    SBOMComponentBuilder addExternalReference(ExternalReference externalReference);
}
