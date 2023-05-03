package org.svip.sbomfactory.generators.parsers.packagemanagers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class GradleParserParseTest extends ParseDepFileTestCore {
    /**
     * Constructor initializes the respective parser and assigns both the
     * fileContents to test it against and the source directory to test on.
     */
    protected GradleParserParseTest() throws IOException {
        super(new GradleParser(),
                Files.readString(Paths.get("src/test/java/org/svip/sbomfactory/generators/TestData/Java/gradle.build")),
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
        assertEquals("1.8", props.get("maven.compiler.source"));
        // TODO: More complex variable cases
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
                                        d -> d
                                ));

        // Test correct count is found
        assertEquals(4, deps.size());

        // create valueSet for the values
        final Set<String> valueSet = new HashSet<>();
        deps.values().forEach(v->{valueSet.add(v.get("artifactId"));});
//        valueSet.add("com.esri.arcgisruntime:arcgis-java:200.1.0");
//        valueSet.add("com.esri.arcgisruntime:arcgis-java-jnilibs:200.1.0");
//        valueSet.add("com.esri.arcgisruntime:arcgis-java-resources:200.1.0");
//        valueSet.add("org.slf4j:slf4j-nop:2.0.5");

        assertTrue(valueSet.contains("com.esri.arcgisruntime:arcgis-java:200.1.0"));
        assertTrue(valueSet.contains("com.esri.arcgisruntime:arcgis-java-jnilibs:200.1.0"));
        assertTrue(valueSet.contains("com.esri.arcgisruntime:arcgis-java-resources:200.1.0"));
        assertTrue(valueSet.contains("org.slf4j:slf4j-nop:2.0.5"));

    }
}
