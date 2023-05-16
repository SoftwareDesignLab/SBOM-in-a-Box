package org.svip.sbomfactory.generators.parsers.packagemanagers;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.svip.sbomfactory.generators.utils.ParserComponent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ConanParserPyFileTest extends ParseDepFileTestCore {
    /**
     * Constructor initializes the respective parser and assigns both the
     * fileContents to test it against and the source directory to test on.
     */
    public ConanParserPyFileTest() throws IOException {
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

    @Test
    @DisplayName("Conan PY Test Components")
    void testComponents() {

        // Test correct count is found
        assertEquals(9, this.components.size());

        //Make ValueSet
        final Set<String> ValueSet = new HashSet<>();

        for (ParserComponent pc : this.components) {
            ValueSet.add(pc.getName());
        }

        //Check component's name
        String str = "folly";
        assertTrue(ValueSet.contains(str));
        //Check component's Version
        assertEquals("2020.08.10.00", getComponent(str).getVersion());

        //Check component's name
        str = "openssl";
        assertTrue(ValueSet.contains(str));
        //Check component's Version
        assertEquals("1.1.1k", getComponent(str).getVersion());

        //Check component's name
        str = "poco";
        assertTrue(ValueSet.contains(str));
        //Check component's version
        assertEquals("[>1.0,<1.9]", getComponent(str).getVersion());

        //Check component's name
        str = "boost";
        assertTrue(ValueSet.contains(str));
        //Check component's version
        assertEquals("1.70.0#revision2", getComponent(str).getVersion());
    }
}
