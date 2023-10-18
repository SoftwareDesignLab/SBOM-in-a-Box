package org.svip.generation.parsers.packagemanagers.Nuget;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.svip.sbom.builder.objects.SVIPComponentBuilder;
import org.svip.generation.parsers.Parser;
import org.svip.generation.parsers.packagemanagers.NugetParser;
import org.svip.generation.parsers.packagemanagers.ParseDepFileTestCore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * file: NugetParserFrameworkAssembliesTest.java //todo
 * Description: Tests for the case of only one dependency / one Framework
 *
 * @author Juan Francisco Patino
 */
public class NugetParserFrameworkAssembliesTest extends ParseDepFileTestCore {

    protected NugetParserFrameworkAssembliesTest() throws IOException {
        super(new NugetParser(),
                Files.readString(Paths.get(TEST_DATA_PATH + "CSharp/Nuget/WithFrameworkAssemblies.nuspec")),
                TEST_DATA_PATH + "CSharp/Nuget");
    }

    @Test
    @DisplayName("Nuget Test Dependencies")
    void testDependencies() {

        // Get Components from PARSER
        final List<SVIPComponentBuilder> components = this.components;

        for (SVIPComponentBuilder f: components //assert these are language components
             ) {
            assertEquals("language", Parser.getType(f).toLowerCase());
            assertEquals("microsoft", Parser.getPublisher(f).toLowerCase());
        }

        // Test correct count is found
        assertEquals(4, components.size());

        //Make ValueSet
        final Set<String> ValueSet = new HashSet<>();;
        for(SVIPComponentBuilder c : components) {
            ValueSet.add(Parser.getName(c));
        }

        //Check component's name

        String str = "System.Web" ;
        assertTrue(ValueSet.contains(str));

        str = "System.Net" ;
        assertTrue(ValueSet.contains(str));

        str = "Microsoft.Devices.Sensors" ;
        assertTrue(ValueSet.contains(str));

        str = "System.Json" ;
        assertTrue(ValueSet.contains(str));

     }

}
