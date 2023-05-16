/**
 * @file OSITest.java
 *
 * Contains test class for the OSI class
 *
 * @author Henry Lu
 */

package org.svip.sbomfactory.osi;

import org.junit.jupiter.api.Test;
import org.svip.sbomfactory.osi.OSI;

/**
 * Tests for the OSI class
 */
public class OSITest {
    
    /**
     * Tests generating sboms from empty project
    */
    @Test
    public void generateSBOMsEmptyTest() {
        OSI osi = new OSI("core/src/main/java/org/svip/sbomfactory/osi/bound_dir" , "core/src/main/java/org/svip/sbomfactory/osi/Dockerfile");
        assert osi.generateSBOMs(System.getProperty("user.dir") + "/src/test/java/org/svip/sbomfactory/osi/sampleProjectEmpty") == 1;
    }

    /**
    * Tests generating sboms from sample project
    */
    @Test
    public void generateSBOMsTest() {
        OSI osi = new OSI("core/src/main/java/org/svip/sbomfactory/osi/bound_dir" , "core/src/main/java/org/svip/sbomfactory/osi/Dockerfile");
        assert osi.generateSBOMs(System.getProperty("user.dir") + "/src/test/java/org/svip/sbomfactory/osi/sampleProject") == 0;
    }

}
