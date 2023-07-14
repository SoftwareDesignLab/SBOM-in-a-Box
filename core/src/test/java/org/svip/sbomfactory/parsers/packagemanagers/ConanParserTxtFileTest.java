package org.svip.sbomfactory.parsers.packagemanagers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.svip.sbom.model.objects.SVIPComponentObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConanParserTxtFileTest extends ParseDepFileTestCore {
    /**
     * Constructor initializes the respective parser and assigns both the
     * fileContents to test it against and the source directory to test on.
     */
    public ConanParserTxtFileTest() throws IOException {
        super(new ConanParser(),
                Files.readString(Paths.get("src/test/java/org/svip/sbomfactory/generators/TestData/Conan/conanfile.txt")),
                "src/test/java/org/svip/sbomfactory/generators/TestData/Conan");
    }

          //Check component's name
//        String str = "contourpy" ;
//        assertTrue(ValueSet.contains(str));
//        //Check component's version
//        String version = getComponent(str).getVersion();
//        int r = version.compareTo("1.0.6");
//        //version is less than 1.0.6:
//        if (r < 0) assertEquals("1.0.6", version);

    @Test
    @DisplayName("Conan TXT Test Components")
    void testTxtComponents() {
        // Test correct count is found
        assertEquals(9, this.components.size());

        //Make ValueSet
        final Set<String> ValueSet = new HashSet<>();

        for (SVIPComponentObject pc : this.components) {
            ValueSet.add(pc.getName());
        }

        //Check component's name
        String str = "folly";
        assertTrue(ValueSet.contains(str));
        //Check component's Version
        assertEquals("2020.08.10.00", getComponent(str).getVersion());

        //Check component's name
        str = "openssl";
        assertTrue(ValueSet.contains(str));
        //Check component's Version
        assertEquals("1.1.1k", getComponent(str).getVersion());

        //Check component's name
        str = "poco";
        assertTrue(ValueSet.contains(str));
        //Check component's version
        assertEquals("[>1.0,<1.9]", getComponent(str).getVersion());
        assertTrue(getComponent(str).getLicenses().getConcluded().contains("BSL-1.0"));

        //Check component's name
        str = "boost";
        assertTrue(ValueSet.contains(str));
        //Check component's version
        assertEquals("1.70.0#revision2", getComponent(str).getVersion());

        //Check component's name
        str = "imgui";
        assertTrue(ValueSet.contains(str));
        //Check component's Version
        assertEquals("1.79", getComponent(str).getVersion());
        assertTrue(getComponent(str).getLicenses().getConcluded().contains("MIT"));
    }
}
