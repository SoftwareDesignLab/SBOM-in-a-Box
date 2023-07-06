package org.svip.sbom.builder.interfaces.schemas.SPDX23;

import org.svip.sbom.builder.interfaces.generics.ComponentBuilder;

/**
 * file: SPDX23ComponentBuilder.java
 * Generic component details that are specific to SPDX 2.3
 * formatted files
 *
 * @author Matthew Morrison
 */
public interface SPDX23ComponentBuilder extends ComponentBuilder {

    /**
     * Set the comment of the component
     * @param comment the comment for the component
     * @return an SPDX23ComponentBuilder
     */
    SPDX23ComponentBuilder setComment(String comment);

    /**
     * Set the attribution text of the component
     * @param attributionText the attribution text of the component
     * @return an SPDX23ComponentBuilder
     */
    SPDX23ComponentBuilder setAttributionText(String attributionText);
}
