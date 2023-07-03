package org.svip.componentfactory;


import org.svip.builders.component.SPDX23FileBuilder;
import org.svip.builders.component.interfaces.generics.ComponentBuilder;
import org.svip.componentfactory.interfaces.ComponentBuilderFactory;

/**
 * file: SPDX23FileBuilderFactory.java
 * Class to build SPDX 2.3 file specifics
 *
 * @author Matthew Morrison
 */
public class SPDX23FileBuilderFactory implements ComponentBuilderFactory {

    /**
     * Create a new SPDX File Builder
     * @return a new SPDX23FileBuilder
     */
    @Override
    public ComponentBuilder createBuilder() {
        return new SPDX23FileBuilder();
    }
}
