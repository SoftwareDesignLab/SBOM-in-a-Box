package org.svip.sbomfactory.generators.parsers.packagemanagers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;

public class POMParserParseTest extends ParseDepFileTestCore {
    /**
     * Constructor initializes the respective parser and assigns both the
     * fileContents to test it against and the source directory to test on.
     */
    protected POMParserParseTest() throws IOException {
        super(new POMParser(),
                Files.readString(Paths.get("TestData/Java/pom.xml")),
                "TestData/Java");
    }

    @Test
    @DisplayName("Test Properties")
    void testProperties() { // TODO: Split into individual tests
        final LinkedHashMap<String, String> props = ((POMParser) this.PARSER).properties;
        // Test correct count is found
        assertEquals(69, props.size()); // TODO: Manually count and verify this number

        // Get keySet
        final Set<String> keySet = props.keySet();

        // Check for correct element insertion
        assertTrue(keySet.contains("gatling-maven-plugin.version"));
        assertTrue(keySet.contains("sonar.issue.ignore.multicriteria.BoldAndItalicTagsCheck.resourceKey"));
        assertTrue(keySet.contains("maven.compiler.source"));
        assertTrue(keySet.contains("argLine"));

        // Check values
        assertEquals("1.8", props.get("maven.compiler.source"));
        // TODO: More complex variable cases
    }
}
