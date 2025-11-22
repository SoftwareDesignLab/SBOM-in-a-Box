/**
 * Copyright 2021 Rochester Institute of Technology (RIT). Developed with
 * government support under contract 70RCSA22C00000008 awarded by the United
 * States Department of Homeland Security for Cybersecurity and Infrastructure Security Agency.
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the “Software”), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.svip.api.services;

import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * File: OSIService.java
 * Service to access and use OSI container
 *
 * @author Derek Garica
 * @author Ian Dunn
 */
@Service
public class OSIService {

    private final OSIComponent osi;
    private boolean enabled = false;    // default to no access to OSI

    /**
     * Create new OSI service
     *
     * @param osi internal interface to make actual requests to osi
     */
    public OSIService(OSIComponent osi) {
        this.osi = osi;
    }

    /**
     * Get a list of tools from OSI based on parameter
     *
     * @param listArg Optional argument, either "all" (default) or "project",
     *                all gets all tools installed in OSI
     *                project gets all applicable tools installed for the project in the bound directory
     * @return A listArg of string tool names.
     */
    public List<String> getTools(String listArg) {
        try {
            // build url
            URI uri = osi.initRequest("/tools")
                    .addParameter("list", listArg)
                    .build();

            // make request
            try (CloseableHttpResponse response = osi.request(new HttpGet(uri))) {
                String responseBody = EntityUtils.toString(response.getEntity());
                // convert and return as list
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(responseBody, new TypeReference<>() {
                });
            }

        } catch (URISyntaxException | IOException e) {
            // error with getting tools
            return null;
        }
    }

    /**
     * Upload project to be run OSI against
     *
     * @param zipBytes zip bytes of the project to upload
     */
    public void uploadProject(byte[] zipBytes) throws IOException, URISyntaxException {
        // build post
        HttpPost post = new HttpPost(osi.initRequest("/upload").build());
        post.setEntity(new ByteArrayEntity(zipBytes, ContentType.APPLICATION_OCTET_STREAM));
        // make request
        try (CloseableHttpResponse response = osi.request(post)) {
            response.close();
        }
    }

    /**
     * Use OSI to generate SBOMs
     *
     * @param toolNames list of tools to use
     * @return Generated file names and their base64 encoded content
     * @throws IOException problem with OSI connection
     */
    public HashMap<String, String> generateSBOMs(List<String> toolNames) throws IOException, URISyntaxException {
        // build post
        HttpPost post = new HttpPost(osi.initRequest("/generate").build());
        // add tools if provided
        if (!toolNames.isEmpty()) {
            Map<String, List<String>> body = new HashMap<>();
            body.put("tools", toolNames);
            ObjectMapper mapper = new ObjectMapper();
            post.setHeader("Content-Type", "application/json");
            post.setEntity(new StringEntity(mapper.writeValueAsString(body), ContentType.APPLICATION_JSON));
        }

        // make request
        try (CloseableHttpResponse response = osi.request(post)) {
            // SBOMs weren't generated
            if (response.getStatusLine().getStatusCode() == 204)
                return new HashMap<>();

            // Convert osi json string into map. Increase max string length to support large base64 bodies
            JsonFactory factory = JsonFactory.builder()
                    .streamReadConstraints(StreamReadConstraints.builder()
                            .maxStringLength(500_000_000) // 500MB, increased from 100MB to handle very large SBOMs
                            .build())
                    .build();
            ObjectMapper mapper = new ObjectMapper(factory);
            return mapper.readValue(EntityUtils.toString(response.getEntity()), new TypeReference<>() {
            });
        }
    }

    /**
     * After construction, check if OSI available
     */
    @PostConstruct
    private void setStatus() {
        try {
            // will fail if osi container not available
            this.enabled = osi.healthcheck();
        } catch (Exception e) {
            System.err.println("Failed OSI health check: " + e.getMessage());
        }
    }

    /**
     * @return If the service is enabled or not
     */
    public boolean isEnabled() {
        return enabled;
    }

}
