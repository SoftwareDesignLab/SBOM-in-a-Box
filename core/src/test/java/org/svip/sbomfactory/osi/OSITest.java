/**
 * @file OSITest.java
 *
 * Contains test class for the OSI class
 *
 * @author Henry Lu
 */

package org.svip.sbomfactory.osi;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Tests for the OSI class
 */
public class OSITest {

    /**
     * Checks to see if Docker is installed and running. If not, all tests in OSITest will be ignored.
     */
    @BeforeAll
    static void dockerCheck() {
        // Use OSI.dockerCheck() to check if docker is running
        Assumptions.assumeTrue(OSI.dockerCheck() == 0);
    }

    /**
     * Tests generating sboms from empty project
    */
    @Test
    public void generateSBOMsEmptyTest() {
        OSI osi = new OSI("core/src/main/java/org/svip/sbomfactory/osi/bound_dir" , "/src/main/java/org/svip/sbomfactory/osi/Dockerfile");
        assert osi.generateSBOMs(System.getProperty("user.dir") + "/src/test/java/org/svip/sbomfactory/osi/sampleProjectEmpty") == 1;
    }

    /**
    * Tests generating sboms from sample project
    */
    @Test
    public void generateSBOMsTest() {
        OSI osi = new OSI("core/src/main/java/org/svip/sbomfactory/osi/bound_dir" , "/src/main/java/org/svip/sbomfactory/osi/Dockerfile");
        assert osi.generateSBOMs(System.getProperty("user.dir") + "/src/test/java/org/svip/sbomfactory/osi/sampleProject") == 0;
    }

}
