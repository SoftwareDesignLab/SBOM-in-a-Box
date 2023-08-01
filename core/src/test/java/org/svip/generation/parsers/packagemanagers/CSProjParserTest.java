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
