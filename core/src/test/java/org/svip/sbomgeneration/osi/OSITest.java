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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for the OSI class
 */
public class OSITest {

    private final OSI osi;
    private static final String OSI_PATH = System.getProperty("user.dir") + "/src/test/java/org/svip/sbomgeneration/osi/";

    /**
     * Checks to see if Docker is installed and running. If not, all tests in OSITest will be ignored.
     */
    @BeforeAll
    static void setup() {
        // Use OSI.dockerCheck() to check if docker is running
        Assumptions.assumeTrue(org.svip.sbomgeneration.osi.OSI.dockerCheck() == 0);
    }

    public OSITest() throws IOException {
        // Create new OSI instance
        this.osi = new OSI();
    }

    /**
     * Tests generating sboms from empty project
    */
    @Test
    public void generateSBOMsEmptyTest() throws IOException {
        osi.addSourceDirectory(new File(OSI_PATH + "sampleProjectEmpty"));

        Map<String, String> resultMap = osi.generateSBOMs();
        assertEquals(0, resultMap.size());
    }

    /**
    * Tests generating sboms from sample project
    */
    @Test
    public void generateSBOMsDirectoryTest() throws IOException {
        osi.addSourceDirectory(new File(OSI_PATH + "sampleProject"));

        Map<String, String> resultMap = osi.generateSBOMs();
        assertEquals(2, resultMap.size());
    }

    @Test
    public void generateSBOMsFileTest() throws IOException {
        this.osi.addSourceFile("SampleJavaClass.java",
                Files.readString(Path.of(OSI_PATH + "/sampleProject/SampleJavaClass.java")));

        Map<String, String> resultMap = osi.generateSBOMs();
        assertEquals(2, resultMap.size());
    }
}
