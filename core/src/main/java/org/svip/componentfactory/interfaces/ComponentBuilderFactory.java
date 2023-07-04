package org.svip.componentfactory.interfaces;

import org.svip.builders.component.interfaces.generics.ComponentBuilder;

/**
 * file: ComponentBuilderFactory.java
 * Interface that aids in building components/packages in any
 * type of SBOM
 *
 * @author Matthew Morrison
 */
public interface ComponentBuilderFactory {

    /**
     * Create a new ComponentBuilder object
     * @return a new ComponentBuilder
     */
    ComponentBuilder createBuilder();
}
