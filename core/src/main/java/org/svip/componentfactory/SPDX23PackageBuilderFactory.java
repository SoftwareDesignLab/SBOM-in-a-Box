package org.svip.componentfactory;


import org.svip.builders.component.SPDX23FileBuilder;
import org.svip.builders.component.interfaces.generics.ComponentBuilder;
import org.svip.componentfactory.interfaces.ComponentBuilderFactory;

/**
 * file: SPDX23PackageBuilderFactory.java
 * Class to build SPDX 2.3 package specifications
 *
 * @author Matthew Morrison
 */
public class SPDX23PackageBuilderFactory implements ComponentBuilderFactory {

    /**
     * Create a new Builder
     * @return a new SPDX23PackageBuilder
     */
    @Override
    public ComponentBuilder createBuilder() {
        return new SPDX23FileBuilder();
    }
}
