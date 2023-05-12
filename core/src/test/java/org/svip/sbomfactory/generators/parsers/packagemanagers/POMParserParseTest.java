package org.svip.sbomfactory.generators.parsers.packagemanagers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;

public class POMParserParseTest extends ParseDepFileTestCore {
    /**
     * Constructor initializes the respective parser and assigns both the
     * fileContents to test it against and the source directory to test on.
     */
    protected POMParserParseTest() throws IOException {
        super(new POMParser(),
                Files.readString(Paths.get("src/test/java/org/svip/sbomfactory/generators/TestData/Java/pom.xml")),
                "src/test/java/org/svip/sbomfactory/generators/TestData/Java");
    }

    @Test
    @DisplayName("Test Properties")
    void testProperties() {
        // Get properties from PARSER
        final HashMap<String, String> props = this.PARSER.properties;

        // Test correct count is found
        assertEquals(69, props.size());

        // Get keySet
        final Set<String> keySet = props.keySet();

        // Check for correct element insertion
        assertTrue(keySet.contains("gatling-maven-plugin.version"));
        assertTrue(keySet.contains("sonar.issue.ignore.multicriteria.BoldAndItalicTagsCheck.resourceKey"));
        assertTrue(keySet.contains("maven.compiler.source"));
        assertTrue(keySet.contains("argLine"));

        // Check values
        assertEquals("jdt_apt", props.get("m2e.apt.activation"));
        assertEquals("${project.src.directory}/build", props.get("project.build.directory"));
        assertEquals("${project.src.directory}/build/test-results", props.get("project.testresult.directory"));
        assertEquals("src/main/webapp/content/**/*.*, src/main/webapp/bower_components/**/*.*, src/main/webapp/i18n/*.js, target/www/**/*.*", props.get("sonar.exclusions"));
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
        assertEquals(69, deps.size());

        // Get keySet
        final Set<String> keySet = deps.keySet();

        // Check for correct element insertion
        assertTrue(keySet.contains("spring-social-security"));
        final LinkedHashMap<String, String> dep1 = deps.get("spring-social-security");
        assertEquals("org.springframework.social", dep1.get("groupId"));

        assertTrue(keySet.contains("httpclient"));
        final LinkedHashMap<String, String> dep2 = deps.get("httpclient");
        assertEquals("org.apache.httpcomponents", dep2.get("groupId"));

        assertTrue(keySet.contains("logstash-logback-encoder"));
        final LinkedHashMap<String, String> dep3 = deps.get("logstash-logback-encoder");
        assertEquals("net.logstash.logback", dep3.get("groupId"));
        assertEquals("4.9", dep3.get("version"));
    }
}
