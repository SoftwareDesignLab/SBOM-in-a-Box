package org.svip.generation.parsers.packagemanagers;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.svip.sbom.builder.objects.SVIPComponentBuilder;
import org.svip.generation.parsers.Parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ConanParserPyFileTest extends ParseDepFileTestCore {
    /**
     * Constructor initializes the respective parser and assigns both the
     * fileContents to test it against and the source directory to test on.
     */
    public ConanParserPyFileTest() throws IOException {
        super(new ConanParser(),
                Files.readString(Paths.get(TEST_DATA_PATH + "Conan/conanfile.py")),
                "Conan");
    }

//        //Check component's name
//        String str = "contourpy" ;
//        assertTrue(ValueSet.contains(str));
//        //Check component's version
//        String version = getComponent(str).getVersion();
//        int r = version.compareTo("1.0.6");
//        //version is less than 1.0.6:
//        if (r < 0) assertEquals("1.0.6", version);

    @Disabled("Package is missing?")
    @Test
    @DisplayName("Conan PY Test Components")
    void testComponents() {
        // Test correct count is found
        assertEquals(8, this.components.size());

        //Make ValueSet
        final Set<String> ValueSet = new HashSet<>();

        for (SVIPComponentBuilder c : this.components)
            ValueSet.add(Parser.getName(c));


        //Check component's name
        String str = "matrix";
        assertTrue(ValueSet.contains(str));
        //Check component's Version
        assertEquals("[>=1.0 <2.0]", getComponent(str).getVersion());
        assertTrue(getComponent(str).getLicenses().getConcluded().contains("Apache-2.0"));

        //Check component's name
        str = "sound32";
        assertTrue(ValueSet.contains(str));
        //Check component's Version
        assertEquals("[>=1.0 <2.0]", getComponent(str).getVersion());

        //Check component's name
        str = "imgui";
        assertTrue(ValueSet.contains(str));
        //Check component's Version
        assertEquals("1.7.0#revision1", getComponent(str).getVersion());
        assertTrue(getComponent(str).getLicenses().getConcluded().contains("MIT"));
    }
}
