package org.svip.sbomfactory.generators.parsers.packagemanagers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.svip.sbomfactory.generators.utils.ParserComponent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class CSProjParserTest extends ParseDepFileTestCore {

    protected CSProjParserTest() throws IOException {
        super(new CSProjParser(),
                Files.readString(Paths.get("src/test/java/org/svip/sbomfactory/generators/TestData/CSharp/Bar/sample.csproj")),
                "src/test/java/org/svip/sbomfactory/generators/TestData/CSharp/Bar");
    }


    @Test
    @DisplayName("CSharp Test Components")
    void testComponents() {

        // Test correct count is found
        assertEquals(12, this.components.size());

        //Make ValueSet
        final Set<String> ValueSet = new HashSet<>();;
        for(ParserComponent pc : this.components) {
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
