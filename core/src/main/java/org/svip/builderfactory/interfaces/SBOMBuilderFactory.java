package org.svip.builderfactory.interfaces;

import org.svip.sbom.builder.interfaces.generics.SBOMBuilder;

/**
 * file: SBOMBuilderFactory.java
 * Generic interface for the SBOM Factories
 *
 * @author Thomas Roman
 */

public interface SBOMBuilderFactory {
    SBOMBuilder createBuilder();
}
