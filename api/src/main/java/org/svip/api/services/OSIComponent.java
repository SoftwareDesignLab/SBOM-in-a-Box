package org.svip.api.services;

import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


/**
 * File: OSIComponent.java
 * Util component to handle actual API calls to OSI API
 *
 * @author Derek Garica
 */
@Component
public class OSIComponent {
    private final CloseableHttpClient httpClient = HttpClients.createDefault();
    @Value("${osi.api.url}")
    private String rootEndpoint;

    /**
     * Make a healthcheck request to OSI to check if online
     *
     * @return True if alive, false otherwise
     */
    public boolean healthcheck() {
        try {
            // build the endpoint
            URI uri = initRequest("/healthcheck").build();
            // check status code
            try (CloseableHttpResponse response = request(new HttpGet(uri))) {
                return response.getStatusLine().getStatusCode() == 200;
            }
        } catch (URISyntaxException | IOException e) {
            return false;
        }
    }

    /**
     * Create a URI builder with the OSI root endpoint as the base
     * Additional query params then can be added
     *
     * @param path Path from root endpoint
     * @return URI builder
     * @throws URISyntaxException Bad url
     */
    public URIBuilder initRequest(String path) throws URISyntaxException {
        return new URIBuilder(rootEndpoint + (path.startsWith("/") ? "" : '/') + path);
    }

    /**
     * Submit a request to OSI. Will raise for status
     *
     * @param request HTTP request make, ie GET, POST, etc
     * @return OSI response object
     * @throws IOException Failed to complete the request
     */
    public CloseableHttpResponse request(HttpRequestBase request) throws IOException {
        CloseableHttpResponse response = httpClient.execute(request);
        StatusLine statusLine = response.getStatusLine();
        int statusCode = statusLine.getStatusCode();
        // raise for status
        if (!(statusCode >= 200 && statusCode < 300))
            throw new IOException("HTTP error: " + statusCode + " " + statusLine.getReasonPhrase());
        // request was successful
        return response;
    }
}
