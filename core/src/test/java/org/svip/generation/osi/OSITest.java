/**
 * @file OSITest.java
 *
 * Contains test class for the OSI class
 *
 * @author Henry Lu
 */

package org.svip.generation.osi;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.svip.utils.Debug;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.svip.generation.osi.OSIClient.dockerCheck;

/**
 * Tests for the OSI class
 */
public class OSITest {

    private final OSI osi;
    private static final String OSI_PATH = System.getProperty("user.dir") + "/src/test/java/resources/osi/";

    public OSITest() {
        OSI osi = null; // Disabled by default

        try {
            osi = new OSI(); // Try creating container
        } catch (Exception ignored) {
            // Ignore error, OSI disabled by default
        } finally { // Log and set
            if (osi == null) Debug.log(Debug.LOG_TYPE.WARN, "Could not create OSI container, disabling tests.");
            this.osi = osi;
        }

        assumeTrue(this.osi != null); // If OSI is null disable tests
    }

    /**
     * Checks to see if Docker is installed and running. If not, all tests in OSITest will be ignored.
     */
    @BeforeAll
    static void setup() {
        // Use OSI.dockerCheck() to check if docker is running
        assumeTrue(dockerCheck() == 0);
    }

    /**
     * Tests generating sboms from empty project
    */
    @Test
    public void generateSBOMsEmptyTest() throws IOException {
        osi.addSourceDirectory(new File(OSI_PATH + "sampleProjectEmpty"));

        Map<String, String> resultMap = osi.generateSBOMs(null);
        Debug.log(Debug.LOG_TYPE.SUMMARY, getSBOMFileList(resultMap));
        assertEquals(0, resultMap.size());
    }

    /**
    * Tests generating sboms from sample project
    */
    @Test
    public void generateSBOMsDirectoryTest() throws IOException {
        osi.addSourceDirectory(new File(OSI_PATH + "sampleProject"));

        Map<String, String> resultMap = osi.generateSBOMs(null);
        Debug.log(Debug.LOG_TYPE.SUMMARY, getSBOMFileList(resultMap));
        assertEquals(2, resultMap.size());
    }

    @Test
    public void generateSBOMsFileTest() throws IOException {
        this.osi.addSourceFile("SampleJavaClass.java",
                Files.readString(Path.of(OSI_PATH + "/sampleProject/SampleJavaClass.java")));

        Map<String, String> resultMap = osi.generateSBOMs(null);
        Debug.log(Debug.LOG_TYPE.SUMMARY, getSBOMFileList(resultMap));
        assertEquals(2, resultMap.size());
    }

    private String getSBOMFileList(Map<String, String> resultMap) {
        String out = "Found " + resultMap.size() + " generated SBOMs:\n";
        for (String fileName : resultMap.keySet())
            out += fileName + "\n";
        return out;
    }
}
