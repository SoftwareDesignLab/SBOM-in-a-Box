/** Copyright 2021 Rochester Institute of Technology (RIT). Developed with
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
 */

package org.svip.repair.extraction;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.svip.sbom.builder.objects.SVIPComponentBuilder;
import org.svip.sbom.factory.objects.SVIPSBOMComponentFactory;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.uids.Hash.Algorithm;
import org.svip.sbom.model.uids.PURL;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the MavenExtraction class
 *
 * @author Jordan Wong
 */
class MavenExtractionTest {

    private static MavenExtraction ext;

    private static final String PURL = "pkg:maven/com.fasterxml.jackson.core/jackson-core@2.13.3?type=jar";
    private static final String MD5_HASH = "9a6679e6a2f7d601a9f212576fda550c";
    private static final String SHA1_HASH = "a27014716e4421684416e5fa83d896ddb87002da";

    @BeforeAll
    static void setup() throws Exception {
        PURL purl = new PURL(PURL);
        ext = new MavenExtraction(purl);
        ext.extract();
    }

    @Test
    public void getHashes() {
        Map<Algorithm, String> hashes = ext.getHashes();
        assertEquals(MD5_HASH, hashes.get(Algorithm.MD5));
        assertEquals(SHA1_HASH, hashes.get(Algorithm.SHA1));
    }

    @Test
    public void isExtractable() {
        Component component = buildComponent(true);
        assertTrue(MavenExtraction.isExtractable(Algorithm.MD5, component));
        assertTrue(MavenExtraction.isExtractable(Algorithm.SHA1, component));
        assertFalse(MavenExtraction.isExtractable(Algorithm.MD6, component));
        assertFalse(MavenExtraction.isExtractable(Algorithm.MD5, null));
        assertFalse(MavenExtraction.isExtractable(Algorithm.SHA1, null));
        assertFalse(MavenExtraction.isExtractable(null, null));

        component = buildComponent(false);
        assertFalse(MavenExtraction.isExtractable(Algorithm.MD5, component));
        assertFalse(MavenExtraction.isExtractable(Algorithm.SHA1, component));
        assertFalse(MavenExtraction.isExtractable(Algorithm.MD6, component));
        assertFalse(MavenExtraction.isExtractable(Algorithm.MD5, null));
        assertFalse(MavenExtraction.isExtractable(Algorithm.SHA1, null));
        assertFalse(MavenExtraction.isExtractable(null, null));
    }

    private Component buildComponent(boolean useMavenPURL) {
        SVIPSBOMComponentFactory packageBuilderFactory = new SVIPSBOMComponentFactory();
        SVIPComponentBuilder packageBuilder = packageBuilderFactory.createBuilder();
        packageBuilder.addHash(Algorithm.MD5.name(), MD5_HASH);
        if (useMavenPURL)
            packageBuilder.addPURL(PURL);
        return packageBuilder.buildAndFlush();
    }
}
