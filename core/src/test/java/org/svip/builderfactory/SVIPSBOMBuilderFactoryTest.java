package org.svip.builderfactory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * file: SVIPSBOMBuilderFactoryTest.java
 * File to test SVIPSBOMBuilderFactory
 *
 * @author Matthew Morrison
 */
public class SVIPSBOMBuilderFactoryTest {

    // TODO SVIP SBOM Builder Factory Tests
    SVIPSBOMBuilderFactory test_sbomBuilder;

    @Test
    void createBuilder() {
        SVIPSBOMBuilderFactory svipSbomBuilderFactory = new SVIPSBOMBuilderFactory();
        test_sbomBuilder = new SVIPSBOMBuilderFactory();
        assertEquals(test_sbomBuilder, svipSbomBuilderFactory);
    }
}
