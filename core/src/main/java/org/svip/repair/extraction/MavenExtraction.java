package org.svip.repair.extraction;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.svip.sbom.model.uids.PURL;
import org.svip.utils.Debug;

/**
 * file: MavenExtraction.java
 * Description: Package-manager specific implementation of the Extraction (Maven)
 *
 * @author Jordan Wong
 */
public class MavenExtraction extends Extraction {

    private static final String URL = "https://repo1.maven.org/maven2/%s/%s/%s/%s";

    public MavenExtraction(PURL purl) {
        super(purl);
    }

    @Override
    public void extract() {

        if(!purl.getType().equals("maven") || purl.getName().equals("") || purl.getVersion().equals("")) {
            Debug.log(Debug.LOG_TYPE.WARN, "Can't extract due to incorrect type or missing name or version");
            return;
        }

        String formattedURL = String.format(this.URL,
            String.join("/", purl.getNamespace()).toLowerCase(),
            purl.getName().toLowerCase(),
            purl.getVersion().toLowerCase(),
            String.join("-", purl.getName().toLowerCase(), purl.getVersion().toLowerCase(), purl.getType().toLowerCase())
        );

        try {
            ObjectMapper mapper = new ObjectMapper();
            results.put("md5", mapper.readValue(this.URL + ".md5", String.class));
            results.put("sha1", mapper.readValue(this.URL + ".sha1", String.class));
        } catch (Exception ex) {
            Debug.log(Debug.LOG_TYPE.WARN, "Failed making request to retrieve md5 or sha1 hashes");
        }

    }
}
