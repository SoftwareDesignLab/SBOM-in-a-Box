package org.svip.sbomfactory.generators.parsers.packagemanagers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.svip.sbomfactory.generators.utils.ParserComponent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


import static org.junit.jupiter.api.Assertions.*;

public class RequirementsParserTest extends ParseDepFileTestCore {
    /**
     * Constructor initializes the respective parser and assigns both the
     * fileContents to test it against and the source directory to test on.
     */
    protected RequirementsParserTest() throws IOException {
        super(new RequirementsParser(),
                Files.readString(Paths.get("src/test/java/org/svip/sbomfactory/generators/TestData/Python/Absolute/requirements.txt")),
                "src/test/java/org/svip/sbomfactory/generators/TestData/Python/Absolute");
        }

    @Test
    @DisplayName("Test Componemts")
    void testComponemts() {
        // Get Components from PARSER
        final ArrayList<ParserComponent> components = this.components;

        // Test correct count is found
        assertEquals(15, components.size());

        //Make ValueSet
        final Set<String> ValueSet = new HashSet<>();;
        for(ParserComponent pc : components) {
            ValueSet.add(pc.getName());
        }

        //Check component's name
        final String[] str = new String[]{"contourpy"} ;
        assertTrue(ValueSet.contains(str[0]));
        //Check component's version
        components.forEach(i->{if(i.getName().equals(str[0]))assertEquals("1.0.6", i.getVersion());});


        //Check component's name
        str[0] = "numpy" ;
        assertTrue(ValueSet.contains(str[0]));
        //Check component's version
        components.forEach(i->{if(i.getName().equals(str[0]))assertEquals("1.23.5", i.getVersion());});


        //Check component's name
        str[0] = "scipy" ;
        assertTrue(ValueSet.contains(str[0]));
        //Check component's version
        components.forEach(i->{if(i.getName().equals(str[0]))assertEquals("1.9.3", i.getVersion());});

    }

}
