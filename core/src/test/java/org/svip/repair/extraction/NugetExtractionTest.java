package org.svip.repair.extraction;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.svip.sbom.model.uids.PURL;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for the NugetExtraction class
 *
 * @author Justin Jantzi
 */
public class NugetExtractionTest {

    private static NugetExtraction ext;
    private static PURL PURL;
    private static final String PURL_STRING = "pkg:nuget/System.Text.Json@8.0.0-rc.2.23479.6?packaging=jar";
    private static final String MICROSOFT_COPYRIGHT = "© Microsoft Corporation. All rights reserved.";
    private static final String MICROSOFT_LICENSE = "MIT";

    @BeforeAll
    static void setup() throws Exception {
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

    @Test
    public void getInvalidPURL() {
        NugetExtraction ne = new NugetExtraction(null);
        assertEquals("", ne.getCopyright());
    }
}
