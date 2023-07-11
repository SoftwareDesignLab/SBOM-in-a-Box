package org.svip.componentfactory;

import org.junit.jupiter.api.Test;
import org.svip.builders.component.CDX14PackageBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * file: CDX14PackageBuilderFactoryTest.java
 * File to test CDX14PackageBuilderFactory
 *
 * @author Matthew Morrison
 */
public class CDX14PackageBuilderFactoryTest {
    CDX14PackageBuilderFactory test_packageBuilder;

    @Test
    void cdx14PackageBuilderFactory_is_test_packageBuilder_when_createBuilder_is_used_test() {
        CDX14PackageBuilderFactory cdx14PackageBuilderFactory = new CDX14PackageBuilderFactory();
        test_packageBuilder = new CDX14PackageBuilderFactory();
        assertEquals(test_packageBuilder, cdx14PackageBuilderFactory);

    }
}
