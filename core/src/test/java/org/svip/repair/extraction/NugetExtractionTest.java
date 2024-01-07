/ **
* Copyright 2021 Rochester Institute of Technology (RIT). Developed with
* government support under contract 70RCSA22C00000008 awarded by the United
* States Department of Homeland Security for Cybersecurity and Infrastructure Security Agency.
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the “Software”), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
* /

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
