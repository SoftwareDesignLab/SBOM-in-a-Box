package org.svip.componentfactory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * file: SPDX23FileBuilderFactoryTest.java
 * File to test SPDX23FileBuilderFactory
 *
 * @author Matthew Morrison
 */
public class SPDX23FileBuilderFactoryTest {
    SPDX23FileBuilderFactory test_fileBuilder;

    @Test
    void createBuilder() {
        SPDX23FileBuilderFactory spdx23FileBuilderFactory = new SPDX23FileBuilderFactory();
        test_fileBuilder = new SPDX23FileBuilderFactory();
        assertEquals(test_fileBuilder, spdx23FileBuilderFactory);

    }
}
