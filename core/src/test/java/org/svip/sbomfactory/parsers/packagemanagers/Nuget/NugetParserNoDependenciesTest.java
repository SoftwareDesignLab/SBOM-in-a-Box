package org.svip.sbomfactory.parsers.packagemanagers.Nuget;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.svip.sbom.model.objects.SVIPComponentObject;
import org.svip.sbomfactory.parsers.packagemanagers.NugetParser;
import org.svip.sbomfactory.parsers.packagemanagers.ParseDepFileTestCore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * file: NugetParserNoDependenciesTest.java
 * Description: Tests for Nuget Package-manager implementation of the PackageManagerParser (.nuspec/.json)
 *
 * @author Juan Francisco Patino
 */
public class NugetParserNoDependenciesTest extends ParseDepFileTestCore {

    protected NugetParserNoDependenciesTest() throws IOException {
        super(new NugetParser(),
                Files.readString(Paths.get("src/test/java/org/svip/sbomfactory/" +
                        "generators/TestData/CSharp/Nuget/NoDependencies.nuspec")),
                "src/test/java/org/svip/sbomfactory/generators/TestData/CSharp/Nuget");
    }

    @Test
    @DisplayName("Nuget Test Dependencies")
    void testDependencies() {

        // Get Components from PARSER
        final List<SVIPComponentObject> components = this.components;

        // Test correct count is found
        assertEquals(0, components.size());

     }

}
