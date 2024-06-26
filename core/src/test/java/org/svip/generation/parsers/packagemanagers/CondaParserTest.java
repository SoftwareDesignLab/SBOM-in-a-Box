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

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class CondaParserTest extends ParseDepFileTestCore{
    protected CondaParserTest() throws IOException {
        super(new CondaParser(),
                Files.readString(Paths.get(TEST_DATA_PATH + "Conda/realData/environment.yaml")),
                "Conda/realData");
    }

    @Test
    void testProperties() {
        final HashMap<String, String> properties = this.PARSER.properties;

        //there should be no properties in this enviroment file.
        //but 6 sources are listed.
        assertEquals(6, properties.size());

        final Set<String> keySet = properties.keySet();

        //check all the keys are right
        assertTrue(keySet.contains("source0"));
        assertTrue(keySet.contains("source1"));
        assertTrue(keySet.contains("source2"));
        assertTrue(keySet.contains("source3"));
        assertTrue(keySet.contains("source4"));
        assertTrue(keySet.contains("source5"));

        //check all the values are right
        assertEquals("https://conda.anaconda.org/kne", properties.get("source0"));
        assertEquals("https://conda.anaconda.org/tlatorre", properties.get("source1"));
        assertEquals("https://conda.anaconda.org/cjs14", properties.get("source2"));
        assertEquals("https://conda.anaconda.org/menpo", properties.get("source3"));
        assertEquals("jjhelmus", properties.get("source4"));
        assertEquals("soumith", properties.get("source5"));
    }

    @Test
    void testDependencies() {
        final HashMap<String, LinkedHashMap<String, String>> dependencies = this.PARSER.dependencies;

        //there should be 34 dependencies in this enviroment file.
        assertEquals(54, dependencies.size());

        //check a few dependencies
        final Set<String> keySet = dependencies.keySet(); //variables
        HashMap<String, String> testDep;

        assertTrue(keySet.contains("python"));
        testDep = dependencies.get("python");
        assertEquals("3.5.2", testDep.get("version"));

        assertTrue(keySet.contains("numpy"));
        testDep = dependencies.get("numpy");
        assertEquals("1.12.0", testDep.get("version"));

        assertTrue(keySet.contains("mpi4py"));
        testDep = dependencies.get("mpi4py");
        assertNull(testDep.get("version"));

        assertTrue(keySet.contains("pip:Pillow"));
        testDep = dependencies.get("pip:Pillow");
        assertNull(testDep.get("version"));

        assertTrue(keySet.contains("pip:https://github.com/inksci/mujoco-py-v0.5.7"));
        testDep = dependencies.get("pip:https://github.com/inksci/mujoco-py-v0.5.7");
        assertNull(testDep.get("version"));

        assertTrue(keySet.contains("pip:https://github.com/Theano/Theano"));
        testDep = dependencies.get("pip:https://github.com/Theano/Theano");
        assertEquals("adfe319ce6b781083d8dc3200fb4481b00853791#egg=Theano", testDep.get("version"));

        assertTrue(keySet.contains("pip:https://github.com/openai/gym"));
        testDep = dependencies.get("pip:https://github.com/openai/gym");
        assertEquals("v0.7.4#egg=gym", testDep.get("version"));

        assertTrue(keySet.contains("pip:https://storage.googleapis.com/tensorflow/linux/gpu/tensorflow_gpu-1.0.1-cp35-cp35m-linux_x86_64.whl; 'linux' in sys_platform"));
        testDep = dependencies.get("pip:https://storage.googleapis.com/tensorflow/linux/gpu/tensorflow_gpu-1.0.1-cp35-cp35m-linux_x86_64.whl; 'linux' in sys_platform");
        assertNull(testDep.get("version"));

        assertTrue(keySet.contains("pip:pylru"));
        testDep = dependencies.get("pip:pylru");
        assertEquals("1.0.9", testDep.get("version"));

        assertTrue(keySet.contains("pip:polling"));
        testDep = dependencies.get("pip:polling");
        assertNull(testDep.get("version"));
    }
}
