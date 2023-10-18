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
 * file: NugetParserErrLogIOTest.java
 * Description: Tests for the case of only one dependency
 *
 * @author Juan Francisco Patino
 */
public class NugetParserErrLogIOTest extends ParseDepFileTestCore {

    protected NugetParserErrLogIOTest() throws IOException {
        super(new NugetParser(),
                Files.readString(Paths.get(TEST_DATA_PATH + "CSharp/Nuget/ErrLog.IO.nuspec")),
                TEST_DATA_PATH + "CSharp/Nuget");
    }

    @Test
    @DisplayName("Nuget Test Dependencies")
    void testDependencies() {

        // Get Components from PARSER
        final List<SVIPComponentBuilder> components = this.components;

        // Test correct count is found
        assertEquals(1, components.size());

        //Make ValueSet
        final Set<String> ValueSet = new HashSet<>();;
        for(SVIPComponentBuilder c : components) {
            ValueSet.add(Parser.getName(c));
        }

        //Check component's name

        String str = "Newtonsoft.Json" ;
        assertTrue(ValueSet.contains(str));


     }

}
