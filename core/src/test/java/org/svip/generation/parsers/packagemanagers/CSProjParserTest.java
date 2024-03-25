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
import org.svip.sbom.builder.objects.SVIPComponentBuilder;
import org.svip.generation.parsers.Parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class CSProjParserTest extends ParseDepFileTestCore {

    protected CSProjParserTest() throws IOException {
        super(new CSProjParser(),
                Files.readString(Paths.get(TEST_DATA_PATH + "CSharp/Bar/sample.csproj")),
                "CSharp/Bar");
    }


    @Test
    @DisplayName("CSharp Test Components")
    void testComponents() {

        // Test correct count is found
        assertEquals(12, this.components.size());

        //Make ValueSet
        final Set<String> ValueSet = new HashSet<>();;
        for(SVIPComponentBuilder c : this.components)
            ValueSet.add(Parser.getName(c));

        //Check component's name
        String str = "Expressions" ;
        assertTrue(ValueSet.contains(str));
        //Check component's group
        assertEquals("Mono/Linq", getComponent(str).getGroup());

        //Check component's name
        str = "Data" ;
        assertTrue(ValueSet.contains(str));
        //Check component's group
        assertEquals("System", getComponent(str).getGroup());

        //Check component's name
        str = "DataSetExtensions" ;
        assertTrue(ValueSet.contains(str));
        //Check component's group
        assertEquals("System/Data", getComponent(str).getGroup());

        //Check component's name
        str = "Program" ;
        assertTrue(ValueSet.contains(str));
        //Check component's group
        assertNull(getComponent(str).getGroup());

        //Check component's name
        str = "packages" ;
        assertTrue(ValueSet.contains(str));
        //Check component's group
        assertNull(getComponent(str).getGroup());
    }

}
