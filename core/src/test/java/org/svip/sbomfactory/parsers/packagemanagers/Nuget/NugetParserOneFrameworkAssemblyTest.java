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
 * file: NugetParserOneFrameworkAssemblyTest.java //todo
 * Description: Tests for the case of only one dependency / one Framework
 *
 * @author Juan Francisco Patino
 */
public class NugetParserOneFrameworkAssemblyTest extends ParseDepFileTestCore {

    protected NugetParserOneFrameworkAssemblyTest() throws IOException {
        super(new NugetParser(),
                Files.readString(Paths.get("src/test/java/org/svip/sbomfactory/" +
                        "generators/TestData/CSharp/Nuget/WithOneFrameworkAssembly.nuspec")),
                "src/test/java/org/svip/sbomfactory/generators/TestData/CSharp/Nuget");
    }

    @Test
    @DisplayName("Nuget Test Dependencies")
    void testDependencies() {

        // Get Components from PARSER
        final List<SVIPComponentObject> components = this.components;

        // Test correct count is found
        assertEquals(1, components.size());

        assertSame("language", components.get(0).getType().toLowerCase());
        assertSame("microsoft", components.get(0).getPublisher().toLowerCase());
        //Make ValueSet
        final Set<String> ValueSet = new HashSet<>();;
        for(SVIPComponentObject c : components) {
            ValueSet.add(c.getName());
        }

        //Check component's name

        String str = "System.Web" ;
        assertTrue(ValueSet.contains(str));


     }

}
