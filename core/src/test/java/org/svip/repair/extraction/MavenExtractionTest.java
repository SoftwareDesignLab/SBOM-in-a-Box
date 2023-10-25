package org.svip.repair.extraction;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.svip.sbom.model.uids.Hash.Algorithm;
import org.svip.sbom.model.uids.PURL;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class MavenExtractionTest {

    private static MavenExtraction ext;
    private static PURL PURL;
    private static final String PURL_STRING = "pkg:maven/com.fasterxml.jackson.core/jackson-core@2.13.3?type=jar";
    private static final String MD5_HASH = "9a6679e6a2f7d601a9f212576fda550c";
    private static final String SHA1_HASH = "a27014716e4421684416e5fa83d896ddb87002da";

    @BeforeAll
    static void setup() throws Exception {
        PURL = new PURL(PURL_STRING);
        ext = new MavenExtraction(PURL);
        ext.extract();
    }

    @Test
    public void getHashes() {
        HashMap<Algorithm, String> hashes = ext.getHashes();
        assertEquals(MD5_HASH, hashes.get(Algorithm.MD5));
        assertEquals(SHA1_HASH, hashes.get(Algorithm.SHA1));
    }

}
