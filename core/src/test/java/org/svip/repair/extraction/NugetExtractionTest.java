package org.svip.repair.extraction;

import org.junit.jupiter.api.Test;
import org.svip.sbom.model.uids.PURL;

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
    private final String PURL_STRING = "pkg:nuget/System.Text.Json@8.0.0-rc.2.23479.6?packaging=jar";
    private final String MICROSOFT_COPYRIGHT = "© Microsoft Corporation. All rights reserved.";
    private final PURL PURL;
    private final String MICROSOFT_LICENSE = "MIT";

    public NugetExtractionTest() throws Exception {

        PURL = new PURL(PURL_STRING);
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
