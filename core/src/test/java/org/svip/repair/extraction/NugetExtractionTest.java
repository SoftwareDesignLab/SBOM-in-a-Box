package org.svip.repair.extraction;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for the NugetExtraction class
 *
 * @author Justin Jantzi
 */
public class NugetExtractionTest {

    private NugetExtraction ext;
    private final String MICROSOFT_COPYRIGHT = "© Microsoft Corporation. All rights reserved.";

    public NugetExtractionTest() {
        HashMap<String, String> examplePurl = new HashMap<String, String>();

        ext = new NugetExtraction(examplePurl);
    }

    @Test
    public void extractCopyright() {
        String copyright = ext.extractCopyright();
        assertEquals(MICROSOFT_COPYRIGHT, copyright);
    }
}
