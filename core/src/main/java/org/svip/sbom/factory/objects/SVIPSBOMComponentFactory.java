package org.svip.sbom.factory.objects;

import org.svip.sbom.builder.objects.SVIPComponentBuilder;
import org.svip.sbom.factory.interfaces.ComponentBuilderFactory;
import org.svip.sbom.model.objects.SVIPComponentObject;

/**
 * file: SVIPSBOMComponentFactory.java
 * Class to build any type of SBOM component
 *
 * @author Matthew Morrison
 */
public class SVIPSBOMComponentFactory implements ComponentBuilderFactory {

    /**
     * Create a new SVIP SBOM Builder
     * @return a new SVIPSBOMComponentBuilder
     */
    @Override
    public SVIPComponentBuilder createBuilder() {
        return new SVIPComponentBuilder();
    }

    /**
     * Create a new SVIP SBOM Builder from an existing component.
     * @return a new SVIPSBOMComponentBuilder
     */
    public SVIPComponentBuilder createBuilder(SVIPComponentObject component) {
        return new SVIPComponentBuilder(component);
    }
}
