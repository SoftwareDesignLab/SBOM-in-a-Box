package org.svip.componentfactory;

import org.svip.builders.component.SVIPComponentBuilder;
import org.svip.componentfactory.interfaces.ComponentBuilderFactory;

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
}
