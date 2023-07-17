package org.svip.sbomfactory.parsers.packagemanagers.Nuget;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.svip.builders.component.SVIPComponentBuilder;
import org.svip.sbomfactory.parsers.Parser;
import org.svip.sbomfactory.parsers.packagemanagers.NugetParser;
import org.svip.sbomfactory.parsers.packagemanagers.ParseDepFileTestCore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * file: NugetParserDependenciesAndFrameworkAssembliesTest.java
 * Description: Tests for Nuget Package-manager implementation of the PackageManagerParser (.nuspec/.json)
 *
 * @author Juan Francisco Patino
 */
public class NugetParserDependenciesAndFrameworkAssembliesTest extends ParseDepFileTestCore {

    protected NugetParserDependenciesAndFrameworkAssembliesTest() throws IOException {
        super(new NugetParser(),
                Files.readString(Paths.get("src/test/java/org/svip/sbomfactory/parsers/TestData/CSharp/Nuget/WithFrameworkAssembliesAndDependencies.nuspec")),
                "src/test/java/org/svip/sbomfactory/parsers/TestData/CSharp/Nuget");
    }

    @Test
    @DisplayName("Nuget Test Dependencies")
    void testDependencies() {

        // Get Components from PARSER
        final List<SVIPComponentBuilder> components = this.components;

        // Test correct count is found
        assertEquals(8, components.size());

        //Make ValueSet
        final Set<String> ValueSet = new HashSet<>();;
        for(SVIPComponentBuilder c : components) {
            ValueSet.add(Parser.getName(c));
        }

        //Check component's name

        String str = "System.Json" ;
        assertTrue(ValueSet.contains(str));
        assertTrue(getComponent(str).getLicenses().getConcluded().contains("MIT"));

        str = "System.Web" ;
        assertTrue(ValueSet.contains(str));

        str = "System.Net" ;
        assertTrue(ValueSet.contains(str));

        str = "Microsoft.Devices.Sensors" ;
        assertTrue(ValueSet.contains(str));

        str = "Newtonsoft.Json" ;
        assertTrue(ValueSet.contains(str));
        assertTrue(getComponent(str).getLicenses().getConcluded().contains("MIT"));

        str = "RestSharp" ;
        assertTrue(ValueSet.contains(str));
        assertTrue(getComponent(str).getLicenses().getConcluded().contains("Apache-2.0"));

        str = "Selenium.Support" ;
        assertTrue(ValueSet.contains(str));

        str = "Selenium.WebDriver" ;
        assertTrue(ValueSet.contains(str));

        int languageComponents = 0;
        int externalComponents = 0;

        for (SVIPComponentBuilder f: components //assert there are exactly four language components
        ) {

            if(Parser.getType(f).equalsIgnoreCase("language"))
                languageComponents++;
            else if(Parser.getType(f).equalsIgnoreCase("external"))
                externalComponents++;

        }

        assertEquals(languageComponents, 4);
        assertEquals(externalComponents, 4);

     }


}
