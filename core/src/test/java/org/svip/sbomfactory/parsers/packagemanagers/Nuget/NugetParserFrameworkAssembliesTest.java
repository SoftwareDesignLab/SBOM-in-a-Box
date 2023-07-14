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
 * file: NugetParserFrameworkAssembliesTest.java //todo
 * Description: Tests for the case of only one dependency / one Framework
 *
 * @author Juan Francisco Patino
 */
public class NugetParserFrameworkAssembliesTest extends ParseDepFileTestCore {

    protected NugetParserFrameworkAssembliesTest() throws IOException {
        super(new NugetParser(),
                Files.readString(Paths.get("src/test/java/org/svip/sbomfactory/" +
                        "parsers/TestData/CSharp/Nuget/WithFrameworkAssemblies.nuspec")),
                "src/test/java/org/svip/sbomfactory/parsers/TestData/CSharp/Nuget");
    }

    @Test
    @DisplayName("Nuget Test Dependencies")
    void testDependencies() {

        // Get Components from PARSER
        final List<SVIPComponentObject> components = this.components;

        for (SVIPComponentObject f: components //assert these are language components
             ) {
            assertEquals("language", f.getType().toLowerCase());
            assertEquals("microsoft", f.getPublisher().toLowerCase());
        }

        // Test correct count is found
        assertEquals(4, components.size());

        //Make ValueSet
        final Set<String> ValueSet = new HashSet<>();;
        for(SVIPComponentObject c : components) {
            ValueSet.add(c.getName());
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
