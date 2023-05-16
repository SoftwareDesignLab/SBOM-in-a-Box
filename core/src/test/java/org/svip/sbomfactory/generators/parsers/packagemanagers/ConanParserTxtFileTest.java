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

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;


public class ConanParserTxtFileTest extends ParseDepFileTestCore {
    /**
     * Constructor initializes the respective parser and assigns both the
     * fileContents to test it against and the source directory to test on.
     */
    protected ConanParserTxtFileTest() throws IOException {
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

    @Test
    @DisplayName("Conan Test Components")
    void testComponents() {

        // Test correct count is found
        assertEquals(9, this.components.size());

        //Make ValueSet
        final Set<String> ValueSet = new HashSet<>();;
        for(ParserComponent pc : this.components) {
            ValueSet.add(pc.getName());
        }



//        imgui/1.79
//        glfw/3.3.2
//        glew/2.1.0
//        opencv/2.4.13.7
//        poco/[>1.0,<1.9]
//        zlib/1.2.13#revision1
//        boost/1.70.0#revision2


        //Check component's name
        String str = "folly" ;
        assertTrue(ValueSet.contains(str));
        //Check component's Version
        assertEquals("2020.08.10.00", getComponent(str).getVersion());

        //Check component's name
        str = "openssl" ;
        assertTrue(ValueSet.contains(str));
        //Check component's Version
        assertEquals("1.1.1k", getComponent(str).getVersion());

//        //Check component's name
//        str = "DataSetExtensions" ;
//        assertTrue(ValueSet.contains(str));
//        //Check component's group
//        assertEquals("System/Data", getComponent(str).getGroup());
//
//        //Check component's name
//        str = "Program" ;
//        assertTrue(ValueSet.contains(str));
//        //Check component's group
//        assertNull(getComponent(str).getGroup());
//
//        //Check component's name
//        str = "packages" ;
//        assertTrue(ValueSet.contains(str));
//        //Check component's group
//        assertNull(getComponent(str).getGroup());
    }
}
