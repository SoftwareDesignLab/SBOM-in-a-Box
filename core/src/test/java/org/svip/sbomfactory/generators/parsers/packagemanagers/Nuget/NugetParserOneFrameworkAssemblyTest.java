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
        assertEquals(1, components.size());

        assertSame(ParserComponent.Type.LANGUAGE, components.get(0).getType());
        assertSame("Microsoft", components.get(0).getPublisher());
        //Make ValueSet
        final Set<String> ValueSet = new HashSet<>();;
        for(ParserComponent pc : components) {
            ValueSet.add(pc.getName());
        }

        //Check component's name

        String str = "System.Web" ;
        assertTrue(ValueSet.contains(str));


     }

}
