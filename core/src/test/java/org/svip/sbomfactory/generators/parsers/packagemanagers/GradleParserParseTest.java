package org.svip.sbomfactory.generators.parsers.packagemanagers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

import static org.svip.sbomfactory.generators.utils.Debug.*;

public class GradleParserParseTest extends ParseDepFileTestCore {
    /**
     * Constructor initializes the respective parser and assigns both the
     * fileContents to test it against and the source directory to test on.
     */
    protected GradleParserParseTest() throws IOException {
        super(new GradleParser(),
                Files.readString(Paths.get("src/test/java/org/svip/sbomfactory/generators/TestData/Java/build.gradle")),
                "src/test/java/org/svip/sbomfactory/generators/TestData/Java");
    }

    @Test
    @DisplayName("Test Properties")
    void testProperties() {
        // Get properties from PARSER
        final HashMap<String, String> props = this.PARSER.properties;

        // Test correct count is found
        assertEquals(4, props.size());

        // Get keySet
        final Set<String> keySet = props.keySet();

        // Check for correct element insertion
        assertTrue(keySet.contains("arcgisVersion"));
        assertTrue(keySet.contains("sampleVersion1"));
        assertTrue(keySet.contains("rootPath"));
        assertTrue(keySet.contains("testPath"));

        // Check values
        assertEquals("200.1.0", props.get("arcgisVersion"));
        assertEquals("this/is/a/path", props.get("rootPath"));
        assertEquals("$rootPath/test", props.get("testPath"));
    }

    @Test
    @DisplayName("Test Dependencies")
    void testDependencies() {
        // Rebuild list of deps as map, with artifactId as key
        final HashMap<String, LinkedHashMap<String, String>> deps =
                (HashMap<String, LinkedHashMap<String, String>>) this.PARSER.dependencies
                        .stream().collect(
                                Collectors.toMap(
                                        d -> d.get("artifactId"),
                                        d -> d,
                                        // Merge conflict function, currently overrides existing values
                                        (d1, d2) -> {
                                            log(LOG_TYPE.WARN, String.format("Duplicate key found: %s", d2.get("artifactId")));
                                            return d2;
                                        }
                                ));

        // Test correct count is found
        assertEquals(14, deps.size());


        // Get keySet
        final Set<String> keySet = deps.keySet();

        // Check for correct element insertion
        assertTrue(keySet.contains("arcgis-java"));
        LinkedHashMap<String, String> depi = deps.get("arcgis-java");
        assertEquals("com.esri.arcgisruntime", depi.get("groupId"));
        assertEquals("$arcgisVersion", depi.get("version"));
        assertEquals("200.1.0", this.PARSER.properties.get("arcgisVersion"));

        assertTrue(keySet.contains("spring-api"));
        depi = deps.get("spring-api");
        assertEquals("org.springframework", depi.get("groupId"));
        assertEquals("3.6", depi.get("version"));

        assertTrue(keySet.contains("slf4j-nop"));
        depi = deps.get("slf4j-nop");
        assertEquals("org.slf4j", depi.get("groupId"));
        assertEquals("2.0.5", depi.get("version"));

        assertTrue(keySet.contains("spring-core"));
        depi = deps.get("spring-core");
        assertEquals("org.springframework", depi.get("groupId"));
        assertEquals("2.5", depi.get("version"));

    }
}
