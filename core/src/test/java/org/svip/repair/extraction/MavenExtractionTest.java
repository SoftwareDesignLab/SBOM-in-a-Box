package org.svip.repair.extraction;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.svip.sbom.builder.objects.SVIPComponentBuilder;
import org.svip.sbom.factory.objects.SVIPSBOMComponentFactory;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.uids.Hash.Algorithm;
import org.svip.sbom.model.uids.PURL;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

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
