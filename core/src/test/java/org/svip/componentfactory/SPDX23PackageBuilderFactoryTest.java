package org.svip.componentfactory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * file: SPDX23PackageBuilderFactoryTest.java
 * File to test SPDX23PackageBuilderFactory
 *
 * @author Matthew Morrison
 */
public class SPDX23PackageBuilderFactoryTest {
    SPDX23PackageBuilderFactory test_packageBuilder;

    @Test
    void createBuilder() {
        SPDX23PackageBuilderFactory spdx23PackageBuilderFactory = new SPDX23PackageBuilderFactory();
        test_packageBuilder = new SPDX23PackageBuilderFactory();
        assertEquals(test_packageBuilder, spdx23PackageBuilderFactory);

    }
}
