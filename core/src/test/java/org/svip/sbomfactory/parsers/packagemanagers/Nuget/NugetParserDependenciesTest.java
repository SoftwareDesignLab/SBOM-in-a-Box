package org.svip.sbomfactory.parsers.packagemanagers.Nuget;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.svip.sbom.model.objects.SVIPComponentObject;
import org.svip.sbomfactory.parsers.packagemanagers.NugetParser;
import org.svip.sbomfactory.parsers.packagemanagers.ParseDepFileTestCore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * file: NugetParserDependenciesTest.java //todo
 * Description: Tests for the case of only one dependency / one Framework
 *
 * @author Juan Francisco Patino
 */
public class NugetParserDependenciesTest extends ParseDepFileTestCore {

    protected NugetParserDependenciesTest() throws IOException {
        super(new NugetParser(),
                Files.readString(Paths.get("src/test/java/org/svip/sbomfactory/parsers/TestData/CSharp/Nuget/WithOnlyDependencies.nuspec")),
                "src/test/java/org/svip/sbomfactory/parsers/TestData/CSharp/Nuget");
    }

    @Test
    @DisplayName("Nuget Test Dependencies")
    void testDependencies() {

        // Get Components from PARSER
        final List<SVIPComponentObject> components = this.components;

        // Test correct count is found
        assertEquals(2, components.size());

        assertEquals("external", components.get(0).getType().toLowerCase());
        assertSame("external", components.get(1).getType().toLowerCase());

        //Make ValueSet
        final Set<String> ValueSet = new HashSet<>();;
        for(SVIPComponentObject c : components) {
            ValueSet.add(c.getName());
        }

        //Check component's name

        String str = "another-package" ;
        assertTrue(ValueSet.contains(str));

        str = "yet-another-package" ;
        assertTrue(ValueSet.contains(str));

     }

}
