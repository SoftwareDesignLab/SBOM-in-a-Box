package org.svip.sbomfactory.parsers.packagemanagers.Nuget;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.svip.sbomfactory.parsers.packagemanagers.NugetParser;
import org.svip.sbomfactory.parsers.packagemanagers.ParseDepFileTestCore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * file: NugetParserDependencyGroupsAndFrameworkAssembliesTest.java //todo
 * Description: Tests for Nuget Package-manager implementation of the PackageManagerParser (.nuspec/.json)
 *
 * @author Juan Francisco Patino
 */
public class NugetParserDependencyGroupsAndFrameworkAssembliesTest extends ParseDepFileTestCore {

    protected NugetParserDependencyGroupsAndFrameworkAssembliesTest() throws IOException {
        super(new NugetParser(),
                Files.readString(Paths.get("src/test/java/org/svip/sbomfactory/parsers/TestData/CSharp/Nuget/WithDependencyGroups.nuspec")),
                "src/test/java/org/svip/sbomfactory/parsers/TestData/CSharp/Nuget");
    }

    @Test
    @DisplayName("Nuget Test Dependencies")
    void testDependencies() {

        /*
        // Get Components from PARSER
        final ArrayList<ParserComponent> components = this.components;

        // Test correct count is found
        assertEquals(3, components.size());

        //Make ValueSet
        final Set<String> ValueSet = new HashSet<>();;
        for(ParserComponent pc : components) {
            ValueSet.add(pc.getName());
        }

        //Check component's name

        String str = "RouteMagic" ;
        assertTrue(ValueSet.contains(str));

        str = "jQuery" ;
        assertTrue(ValueSet.contains(str));

        str = "WebActivator" ;
        assertTrue(ValueSet.contains(str));

        //todo test group names?

         */

     }

}
