package org.svip.builderfactory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * file: CDX14SBOMBuilderFactoryTest.java
 * File to test CDX14SBOMBuilderFactory
 *
 * @author Matthew Morrison
 */
public class CDX14SBOMBuilderFactoryTest {

    CDX14SBOMBuilderFactory test_sbomBuilder;

    @Test
    void createBuilder() {
        CDX14SBOMBuilderFactory cdx14SBOMBuilderFactory = new CDX14SBOMBuilderFactory();
        test_sbomBuilder = new CDX14SBOMBuilderFactory();
        assertEquals(test_sbomBuilder, cdx14SBOMBuilderFactory);
    }
}
