package org.svip.componentfactory;

import org.svip.builders.component.CDX14PackageBuilder;
import org.svip.componentfactory.interfaces.ComponentBuilderFactory;


/**
 * file: CDX14PackageBuilderFactory.java
 * Class to build CycloneDX 1.4 specific packages
 *
 * @author Matthew Morrison
 */
public class CDX14PackageBuilderFactory implements ComponentBuilderFactory {

    /**
     * Create a new Builder
     * @return a new CDX14PackageBuilder
     */
    @Override
    public CDX14PackageBuilder createBuilder() {
        return new CDX14PackageBuilder();
    }
}
