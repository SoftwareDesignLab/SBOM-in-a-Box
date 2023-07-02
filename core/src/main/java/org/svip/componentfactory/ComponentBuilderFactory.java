package org.svip.componentfactory;

import org.svip.builders.component.ComponentBuilder;

/**
 * file: ComponentBuilderFactory.java
 * Interface that aids in building components/packages in an SBOM
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
