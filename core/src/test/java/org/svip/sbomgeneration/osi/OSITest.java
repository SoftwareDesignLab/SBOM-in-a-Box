/**
 * @file OSITest.java
 *
 * Contains test class for the OSI class
 *
 * @author Henry Lu
 */

package org.svip.sbomgeneration.osi;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

/**
 * Tests for the OSI class
 */
public class OSITest {

    private static final String OSI_PATH = System.getProperty("user.dir") + "/src/test/java/org/svip/sbomgeneration/osi/";

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
    public void generateSBOMsEmptyTest() throws IOException {
        OSI osi = new OSI();
        osi.addSourceDirectory(new File(OSI_PATH + "sampleProjectEmpty"));
        assert osi.generateSBOMs() == 1;
    }

    /**
    * Tests generating sboms from sample project
    */
    @Test
    public void generateSBOMsTest() throws IOException {
        OSI osi = new OSI();
        osi.addSourceDirectory(new File(OSI_PATH + "sampleProject"));
        assert osi.generateSBOMs() == 0;
    }

}
