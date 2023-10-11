package org.svip.repair.extraction;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests for the NugetExtraction class
 *
 * @author Justin Jantzi
 */
public class NugetExtractionTest {

    private NugetExtraction ext;
    private final String MICROSOFT_COPYRIGHT = "© Microsoft Corporation. All rights reserved.";
    private final HashMap<String, String> PURL = new HashMap<String, String>() {{
        put("name", "System.Text.Json");
        put("version", "8.0.0-rc.2.23479.6");
    }};
    private final String MICROSOFT_LICENSE = "MIT";

    public NugetExtractionTest() {
        ext = new NugetExtraction(PURL);
        ext.extract();
    }

    @Test
    public void getValidCopyright() {
        String copyright = ext.getCopyright();
        assertEquals(MICROSOFT_COPYRIGHT, copyright);
    }

    @Test
    public void getValidLicense() {
        String license = ext.getLicense();
        assertEquals(MICROSOFT_LICENSE, license);
    }

    @Test public void getInvalidPURL() {
        NugetExtraction ne = new NugetExtraction(null);
        assertNull(ne.getCopyright());
    }
}
