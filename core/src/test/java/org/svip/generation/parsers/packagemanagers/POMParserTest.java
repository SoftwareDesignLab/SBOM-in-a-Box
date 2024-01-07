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

public class POMParserTest extends ParseDepFileTestCore {
    /**
     * Constructor initializes the respective parser and assigns both the
     * fileContents to test it against and the source directory to test on.
     */
    protected POMParserTest() throws IOException {
        super(new POMParser(),
                Files.readString(Paths.get(TEST_DATA_PATH + "Java/pom.xml")),
                "Java");
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
        final HashMap<String, LinkedHashMap<String, String>> deps = this.PARSER.dependencies;

        // Test correct count is found
        assertEquals(69, deps.size());

        // Get keySet
        final Set<String> keySet = deps.keySet();
        HashMap<String, String> dep;

        // Check for correct element insertion
        assertTrue(keySet.contains("spring-social-security"));
        dep = deps.get("spring-social-security");
        assertEquals("org.springframework.social", dep.get("groupId"));

        assertTrue(keySet.contains("httpclient"));
        dep = deps.get("httpclient");
        assertEquals("org.apache.httpcomponents", dep.get("groupId"));

        assertTrue(keySet.contains("logstash-logback-encoder"));
        dep = deps.get("logstash-logback-encoder");
        assertEquals("net.logstash.logback", dep.get("groupId"));
        assertEquals("4.9", dep.get("version"));
    }
}
