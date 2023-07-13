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
    private final HttpClient client;

    public NVDClient() {
        this.client = HttpClient.newHttpClient();
    }

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

    private CompletableFuture<String> sendRequestAsync(HttpRequest request) {
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);
    }

    private CompletableFuture<String> getRequestAsync(String... params) {
        String url = buildUrlWithParams(params);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        return sendRequestAsync(request);
    }

    public CompletableFuture<String> getCveByIdAsync(String cveId) {
        return getRequestAsync("cveId", cveId);
    }

    public CompletableFuture<String> getCvesByCpeNameAsync(String cpeName) {
        return getRequestAsync("cpeName", cpeName);
    }

    public CompletableFuture<String> getCvesByCvssV2MetricsAsync(String cvssV2Metrics) {
        return getRequestAsync("cvssV2Metrics", cvssV2Metrics);
    }

    public CompletableFuture<String> getCvesByCvssV2SeverityAsync(String cvssV2Severity) {
        return getRequestAsync("cvssV2Severity", cvssV2Severity);
    }

    public CompletableFuture<String> getCvesByCvssV3Metrics(String cvssV3Metrics) {
        return getRequestAsync("cvssV3Metrics", cvssV3Metrics);
    }

    public CompletableFuture<String> getCvesByCvssV3SeverityAsync(String cvssV3Severity) {
        return getRequestAsync("cvssV3Severity", cvssV3Severity);
    }

    public CompletableFuture<String> getCvesByCweIdAsync(String cweId) {
        return getRequestAsync("cweId", cweId);
    }

    public CompletableFuture<String> getCvesWithCertAlertsAsync() {
        return getRequestAsync("hasCertAlerts", "");
    }

    public CompletableFuture<String> getCvesWithCertNotesAsync() {
        return getRequestAsync("hasCertNotes", "");
    }

    public CompletableFuture<String> getCvesInKevAsync() {
        return getRequestAsync("hasKev", "");
    }

    public CompletableFuture<String> getCvesWithOvalRecordAsync() {
        return getRequestAsync("hasOval", "");
    }

    public CompletableFuture<String> getCvesByVulnerabilityAndCpeAsync(String cpeName) {
        return getRequestAsync("cpeName", cpeName + "&isVulnerable");
    }

    public CompletableFuture<String> getCvesByExactKeywordAsync(String keyword) {
        return getRequestAsync("keywordSearch", keyword + "&keywordExactMatch");
    }

    public CompletableFuture<String> getCvesByKeywordsAsync(String keywords) {
        return getRequestAsync("keywordSearch", keywords);
    }

    public CompletableFuture<String> getCvesByLastModDateAsync(String startDate, String endDate) {
        return getRequestAsync("lastModStartDate", startDate, "lastModEndDate", endDate);
    }

    public CompletableFuture<String> getCvesWithoutRejectedStatusAsync() {
        return getRequestAsync("noRejected", "");
    }

    public CompletableFuture<String> getCvesByPubDateAsync(String startDate, String endDate) {
        return getRequestAsync("pubStartDate", startDate, "pubEndDate", endDate);
    }

    public CompletableFuture<String> getCvesByResultsPerPageAsync(int limit) {
        return getRequestAsync("resultsPerPage", String.valueOf(limit));
    }

    public CompletableFuture<String> getCvesByStartIndexAsync(int index) {
        return getRequestAsync("startIndex", String.valueOf(index));
    }

    public CompletableFuture<String> getCvesBySourceIdentifierAsync(String sourceIdentifier) {
        return getRequestAsync("sourceIdentifier", sourceIdentifier);
    }

    public CompletableFuture<String> getCvesByVersionEndAsync(String versionEnd, String versionEndType, String virtualMatchString) {
        return getRequestAsync("virtualMatchString", virtualMatchString, "versionEnd", versionEnd, "versionEndType", versionEndType);
    }

    public CompletableFuture<String> getCvesByVersionStartAsync(String versionStart, String versionStartType, String virtualMatchString) {
        return getRequestAsync("virtualMatchString", virtualMatchString, "versionStart", versionStart, "versionStartType", versionStartType);
    }


    public void run(){
    }
}
