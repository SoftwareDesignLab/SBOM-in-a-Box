package org.svip.sbomfactory.generators.parsers.packagemanagers.Nuget;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.svip.sbomfactory.generators.parsers.packagemanagers.NugetParser;
import org.svip.sbomfactory.generators.parsers.packagemanagers.ParseDepFileTestCore;
import org.svip.sbomfactory.generators.utils.ParserComponent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * file: NugetParserDependenciesAndFrameworkAssembliesTest.java
 * Description: Tests for Nuget Package-manager implementation of the PackageManagerParser (.nuspec/.json)
 *
 * @author Juan Francisco Patino
 */
public class NugetParserDependenciesAndFrameworkAssembliesTest extends ParseDepFileTestCore {

    protected NugetParserDependenciesAndFrameworkAssembliesTest() throws IOException {
        super(new NugetParser(),
                Files.readString(Paths.get("src/test/java/org/svip/sbomfactory/" +
                        "generators/TestData/CSharp/Nuget/WithFrameworkAssembliesAndDependencies.nuspec")),
                "src/test/java/org/svip/sbomfactory/generators/TestData/CSharp/Nuget");
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
    @DisplayName("Nuget Test Dependencies")
    void testDependencies() {

        // Get Components from PARSER
        final ArrayList<ParserComponent> components = this.components;

        // Test correct count is found
        assertEquals(8, components.size());

        //Make ValueSet
        final Set<String> ValueSet = new HashSet<>();;
        for(ParserComponent pc : components) {
            ValueSet.add(pc.getName());
        }

        //Check component's name

        String str = "System.Web" ;
        assertTrue(ValueSet.contains(str));

        str = "System.Net" ;
        assertTrue(ValueSet.contains(str));

        str = "Microsoft.Devices.Sensors" ;
        assertTrue(ValueSet.contains(str));

        str = "Newtonsoft.Json" ;
        assertTrue(ValueSet.contains(str));

        str = "RestSharp" ;
        assertTrue(ValueSet.contains(str));

        str = "Selenium.Support" ;
        assertTrue(ValueSet.contains(str));

        str = "Selenium.WebDriver" ;
        assertTrue(ValueSet.contains(str));

        int languageComponents = 0;
        int externalComponents = 0;

        for (ParserComponent f: components //assert there are exactly four language components
        ) {

            if(f.getType() == ParserComponent.Type.LANGUAGE)
                languageComponents++;
            else if(f.getType() == ParserComponent.Type.EXTERNAL)
                externalComponents++;

        }

        assertEquals(languageComponents, 4);
        assertEquals(externalComponents, 4);

     }


}
