package org.svip.componentfactory;

import org.svip.builders.component.SVIPComponentBuilder;
import org.svip.builders.component.interfaces.generics.ComponentBuilder;
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
    public ComponentBuilder createBuilder() {
        return new SVIPComponentBuilder();
    }
}
