package org.svip.sbomfactory.generators.parsers.packagemanagers;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.svip.sbomfactory.generators.utils.ParserComponent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ConanParserTest extends ParseDepFileTestCore {
    /**
     * Constructor initializes the respective parser and assigns both the
     * fileContents to test it against and the source directory to test on.
     */
    protected ConanParserTest() throws IOException {
        super(new ConanParser(),
                Files.readString(Paths.get("src/test/java/org/svip/sbomfactory/generators/TestData/Conan/conanfile.txt")),
                "src/test/java/org/svip/sbomfactory/generators/TestData/Conan");
    }

    @Test
    @DisplayName("Test Properties")
    void testProperties() {
        int n = 5;
        // Get properties from PARSER
        //final ArrayList<ParserComponent> props = this.components;

        // Test correct count is found
//        assertEquals(4, props.size());
//
//        // Get keySet
//        final Set<String> keySet = props.keySet();
//
//        // Check for correct element insertion
//        assertTrue(keySet.contains("arcgisVersion"));
//        assertTrue(keySet.contains("sampleVersion1"));
//        assertTrue(keySet.contains("rootPath"));
//        assertTrue(keySet.contains("testPath"));
//
//        // Check values
//        assertEquals("200.1.0", props.get("arcgisVersion"));
//        assertEquals("this/is/a/path", props.get("rootPath"));
//        assertEquals("this/is/a/path/test", props.get("testPath"));
    }

    @Disabled
    @Test
    @DisplayName("Test Dependencies")
    void testDependencies() {
        // Rebuild list of deps as map, with artifactId as key
        final HashMap<String, LinkedHashMap<String, String>> deps = this.PARSER.dependencies;

        // Test correct count is found
        assertEquals(15, deps.size());


        // Get keySet
        final Set<String> keySet = deps.keySet();
        HashMap<String, String> dep;

        // Check for correct element insertion
        assertTrue(keySet.contains("arcgis-java"));
        dep = deps.get("arcgis-java");
        assertEquals("com.esri.arcgisruntime", dep.get("groupId"));
        assertEquals("$arcgisVersion", dep.get("version"));
        assertEquals("200.1.0", this.PARSER.properties.get("arcgisVersion"));

        assertTrue(keySet.contains("spring-api"));
        dep = deps.get("spring-api");
        assertEquals("org.springframework", dep.get("groupId"));
        assertEquals("3.6", dep.get("version"));

        assertTrue(keySet.contains("slf4j-nop"));
        dep = deps.get("slf4j-nop");
        assertEquals("org.slf4j", dep.get("groupId"));
        assertEquals("2.0.5", dep.get("version"));

        assertTrue(keySet.contains("spring-core"));
        dep = deps.get("spring-core");
        assertEquals("org.springframework", dep.get("groupId"));
        assertEquals("2.5", dep.get("version"));

    }
}
