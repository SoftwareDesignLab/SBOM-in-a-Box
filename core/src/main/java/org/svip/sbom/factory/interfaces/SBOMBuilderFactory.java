package org.svip.sbom.factory.interfaces;

import org.svip.sbom.builder.interfaces.generics.SBOMBuilder;

/**
 * file: SBOMBuilderFactory.java
 * Generic interface for the SBOM Factories
 *
 * @author Thomas Roman
 */

public interface SBOMBuilderFactory {
    /**
     * Creates an SBOMBuilder from the data in the SBOMBuilderFactory
     *
     * @return an SBOMBuilder
     */
    SBOMBuilder createBuilder();
}
