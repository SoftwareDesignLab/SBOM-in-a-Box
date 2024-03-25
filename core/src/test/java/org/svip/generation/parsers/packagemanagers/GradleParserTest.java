/** Copyright 2021 Rochester Institute of Technology (RIT). Developed with
* government support under contract 70RCSA22C00000008 awarded by the United
* States Department of Homeland Security for Cybersecurity and Infrastructure Security Agency.
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the “Software”), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
 */

package org.svip.generation.parsers.packagemanagers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class GradleParserTest extends ParseDepFileTestCore {
    /**
     * Constructor initializes the respective parser and assigns both the
     * fileContents to test it against and the source directory to test on.
     */
    protected GradleParserTest() throws IOException {
        super(new GradleParser(),
                Files.readString(Paths.get(TEST_DATA_PATH + "Java/build.gradle")),
                "Java");
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
        assertEquals("this/is/a/path/test", props.get("testPath"));
    }

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
        Assertions.assertEquals("200.1.0", this.PARSER.properties.get("arcgisVersion"));

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
