package org.svip.repair.extraction;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.svip.generation.parsers.utils.QueryWorker;
import org.svip.sbom.model.uids.Hash.Algorithm;
import org.svip.sbom.model.uids.PURL;
import org.svip.utils.Debug;

import java.util.List;

import static org.svip.utils.Debug.log;

/**
 * file: MavenExtraction.java
 * Description: Package-manager specific implementation of the Extraction (Maven)
 *
 * @author Jordan Wong
 */
public class MavenExtraction extends Extraction {

    private static final String URL = "https://repo1.maven.org/maven2/%s/%s/%s/%s.%s";
    private static final List<Algorithm> HASH_ALGORITHMS = List.of(Algorithm.MD5, Algorithm.SHA1);

    public MavenExtraction(PURL purl) {
        super(purl);
    }

    @Override
    public void extract() {
        if (!purl.getType().equals("maven") || purl.getName().equals("") || purl.getVersion().equals("")) {
            Debug.log(Debug.LOG_TYPE.WARN, "Can't extract due to incorrect type or missing name or version");
            return;
        }

        String formattedURL = String.format(this.URL,
            purl.getNamespace().get(0).replaceAll("\\.", "/"),
            purl.getName(),
            purl.getVersion(),
            String.join("-", purl.getName(), purl.getVersion()),
            purl.getQualifiers().get("type")
        ).toLowerCase();

        QueryWorker qw = new QueryWorker(null, formattedURL) {
            @Override
            public void run() {
                for (Algorithm algorithm : HASH_ALGORITHMS) {
                    String response = getUrlContents(
                            queryURL(url + "." + algorithm.toString().toLowerCase(), false));
                    hashes.put(algorithm, response.trim());
                }
            }
        };

        Thread t = new Thread(qw);
        t.start();

        try {
            t.join();
        } catch (InterruptedException e) {
            log(Debug.LOG_TYPE.ERROR, "Thread interrupted while waiting for queryWorker.");
        }

    }

}
