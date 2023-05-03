package org.svip.sbomfactory.generators.parsers.packagemanagers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

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
        // Get dependencies from PARSER
        final ArrayList<LinkedHashMap<String, String>> dep = this.PARSER.dependencies;

        // Test correct count is found
        final int entrycount = 4;
        int runningcount = 0;
        assertEquals(entrycount, dep.size());

        String[] keywords = {"implementation", "natives"};
        for (LinkedHashMap<String, String> ent : dep) {
            String[] values =  ent.get("artifactId").trim().split(" ");
            //validate a line comment line
            if(Pattern.matches("[\\s]*//.*", values[0])) {
                runningcount++;
            }
            for(String kw : keywords) {
                if (values[0].contains(kw)) {
                    assertTrue(values[0].contains(kw));
                    runningcount++;
                    break;
                }
            }
        }

        //fail if any has not keyword
        if (runningcount != entrycount) {
            //fail this test
            fail();
        }

//        // Check values
//        assertEquals("1.8", props.get("maven.compiler.source"));
//        // TODO: More complex variable cases
    }
}
