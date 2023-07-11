package org.svip.componentfactory;

import org.junit.jupiter.api.Test;
import org.svip.builderfactory.SVIPSBOMBuilderFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * file: SVIPSBOMComponentFactoryTest.java
 * File to test SVIPSBOMComponentFactory
 *
 * @author Matthew Morrison
 */
public class SVIPSBOMComponentFactoryTest {
    SVIPSBOMComponentFactory test_componentBuilder;

    @Test
    void svipsbomComponentFactory_is_test_componentBuilder_when_createBuilder_is_used_test() {
        SVIPSBOMComponentFactory svipsbomComponentFactory = new SVIPSBOMComponentFactory();
        test_componentBuilder = new SVIPSBOMComponentFactory();
        assertEquals(test_componentBuilder, svipsbomComponentFactory);

    }
}
