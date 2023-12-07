/ **
* Copyright 2021 Rochester Institute of Technology (RIT). Developed with
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
* /

package org.svip.generation.parsers.packagemanagers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.svip.sbom.builder.objects.SVIPComponentBuilder;
import org.svip.generation.parsers.Parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class RequirementsParserTest extends ParseDepFileTestCore {
    /**
     * Constructor initializes the respective parser and assigns both the
     * fileContents to test it against and the source directory to test on.
     */
    protected RequirementsParserTest() throws IOException {
        super(new RequirementsParser(),
        Files.readString(Paths.get(TEST_DATA_PATH + "Python/Absolute/requirements.txt")),
                "Python/Absolute");
        }


    @Test
    @DisplayName("Test Componemts")
    void testComponents() {
        // Get Components from PARSER
        final List<SVIPComponentBuilder> components = this.components;

        // Test correct count is found
        assertEquals(15, components.size());

        //Make ValueSet
        final Set<String> ValueSet = new HashSet<>();;
        for(SVIPComponentBuilder c : components)
            ValueSet.add(Parser.getName(c));

//        //Check component's name
//        String str = "contourpy" ;
//        assertTrue(ValueSet.contains(str));
//        //Check component's version
//        String version = getComponent(str).getVersion();
//        int r = version.compareTo("1.0.6");
//        //version is less than 1.0.6:
//        if (r < 0) assertEquals("1.0.6", version);

        //Check component's name
        final String[] str = new String[]{"contourpy"} ;
        assertTrue(ValueSet.contains(str[0]));
        //Check component's version
        components.forEach(i->{if(Parser.getName(i).equals(str[0]))assertEquals("1.0.6", Parser.getVersion(i));});


        //Check component's name
        str[0] = "numpy" ;
        assertTrue(ValueSet.contains(str[0]));
        //Check component's version
        components.forEach(i->{if(Parser.getName(i).equals(str[0]))assertEquals("1.23.5", Parser.getVersion(i));});


        //Check component's name
        str[0] = "scipy" ;
        assertTrue(ValueSet.contains(str[0]));
        //Check component's version
        components.forEach(i->{if(Parser.getName(i).equals(str[0]))assertEquals("1.9.3", Parser.getVersion(i));});

    }

}
