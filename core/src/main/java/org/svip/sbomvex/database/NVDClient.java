package org.svip.sbomvex.database;



/**
 * file: NVDClient.java
 * Client class for the NVD Database
 *
 * @author Matthew Morrison
 */
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class NVDClient {
    private static final String ENDPOINT = "https://services.nvd.nist.gov/rest/json/cves/2.0";

    private String buildUrlWithParams(String... params) {
        if (params.length % 2 != 0) {
            throw new IllegalArgumentException("Odd number of parameters");
        }

        StringBuilder url = new StringBuilder(ENDPOINT);

        if (params.length > 0) {
            url.append("?");
            for (int i = 0; i < params.length; i+=2) {
                url.append(params[i]).append("=").append(params[i+1]);

                if (i+2 < params.length) {
                    url.append("&");
                }
            }
        }

        return url.toString();
    }

    public void run(){
    }
}
