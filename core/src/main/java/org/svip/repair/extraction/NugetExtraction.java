package org.svip.repair.extraction;

import org.svip.generation.parsers.utils.QueryWorker;
import org.svip.utils.Debug;

import java.io.IOException;
import java.util.HashMap;

import static org.svip.utils.Debug.log;

/**
 * file: NugetExtraction.java
 * Description: Package-manager specific implementation of the Extraction (Nuget)
 *
 * @author Justin Jantzi
 */
public class NugetExtraction extends Extraction {

    private final String URL = "api.nuget.org/v3-flatcontainer/id/version/id.nuspec";

    protected NugetExtraction(HashMap<String, String> purl) {
        super(purl);
    }

    @Override
    public String extractCopyright() {

        String url = this.URL.replaceAll("id", purl.get("name").toLowerCase());
        url = URL.replaceAll("version", purl.get("version").toLowerCase());

        this.queryWorkers.add(new QueryWorker(null, url) {
            @Override
            public void run() {
                try {
                    Object repsonse = queryURL(url, false).getContent();
                } catch (IOException e) {
                    log(Debug.LOG_TYPE.WARN, String.format("Failed to get contents of URL: '%s'", url));
                }
            }
        });

        return null;
    }
}
