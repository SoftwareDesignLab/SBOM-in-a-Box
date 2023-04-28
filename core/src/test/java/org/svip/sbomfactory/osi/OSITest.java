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
        OSI osi = new OSI("src/main/java/com/svip/osi/core/bound_dir" , "/src/main/java/com/svip/osi/core/Dockerfile");
        assert osi.generateSBOMs(System.getProperty("user.dir") + "/src/test/java/com/svip/osi/core/sampleProjectEmpty") == 1;
    }

    /**
    * Tests generating sboms from sample project
    */
    @Test
    public void generateSBOMsTest() {
        OSI osi = new OSI("src/main/java/com/svip/osi/core/bound_dir" , "/src/main/java/com/svip/osi/core/Dockerfile");
        assert osi.generateSBOMs(System.getProperty("user.dir") + "/src/test/java/com/svip/osi/core/sampleProject") == 0;
    }

}
