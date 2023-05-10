package org.svip.sbomfactory.generators.parsers.packagemanagers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.svip.sbomfactory.generators.utils.ParserComponent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * file: NugetParserTest.java
 * Description: Tests for Nuget Package-manager implementation of the PackageManagerParser (.nuspec/.json)
 *
 * @author Juan Francisco Patino
 */
public class NugetParserTest extends ParseDepFileTestCore{

    protected NugetParserTest() throws IOException {
        super(new NugetParser(),
                Files.readString(Paths.get("src/test/java/org/svip/sbomfactory/generators/TestData/CSharp/Bar/sample.csproj")),
                "src/test/java/org/svip/sbomfactory/generators/TestData/CSharp/Bar");
    }

    protected ParserComponent getComponent(String name) {
        for(ParserComponent i : this.components) {
            String cname = i.getName();
            if((cname != null) && cname.equals(name) ) {
                return i;
            }
        }
        return null;
    }

    @Test
    @DisplayName("Nuget Test Componemts")
    void testComponemts() {

        //todo refactor this for nuget pm

        // Get Components from PARSER
        final ArrayList<ParserComponent> components = this.components;

        // Test correct count is found
        assertEquals(12, components.size());

        //Make ValueSet
        final Set<String> ValueSet = new HashSet<>();;
        for(ParserComponent pc : components) {
            ValueSet.add(pc.getName());
        }

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
