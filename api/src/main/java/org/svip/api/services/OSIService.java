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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.apache.commons.io.FileUtils;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * File: OSIService.java
 * Service to access and use OSI container
 *
 * @author Derek Garica
 * @author Ian Dunn
 */
@Service
public class OSIService {

    private final OSI osi;
    // todo - remove
    @Value("${osi.api.url}")
    private String osiRootEndpoint;
    private boolean enabled = false;    // default to no access to OSI

    /**
     * Create new OSI service
     *
     * @param osi internal interface to make actual requests to osi
     */
    public OSIService(OSI osi) {
        this.osi = osi;
    }

    /**
     * Get a list of tools from OSI based on parameter
     *
     * @param listArg Optional argument, either "all" (default) or "project",
     *             all gets all tools installed in OSI
     *             project gets all applicable tools installed for the project in the bound directory
     * @return A listArg of string tool names.
     */
    public List<String> getTools(String listArg) {
        try {
            // build url
            URI uri = this.osi.initRequest("/tools")
                    .addParameter("list", listArg)
                    .build();

            // make request
            var u = new HttpGet(uri);
            try (CloseableHttpResponse response = this.osi.request(u)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                // convert and return as list
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(responseBody, new TypeReference<>() {});
            }

        } catch (URISyntaxException | IOException e) {
            // error with getting tools
            return null;
        }
    }

    /**
     * Upload project to be run OSI against
     *
     * @param inputStream Zip input stream of the project
     */
    public void addProject(ZipInputStream inputStream) throws IOException {
        // Remove all source files in the bound_dir folder before uploading files
        BOUND_DIR.CODE.flush();
        // Get bound code directory
        Path path = Paths.get(BOUND_DIR.CODE.getPath());
        // Write each file to bound directory
        for (ZipEntry entry; (entry = inputStream.getNextEntry()) != null; ) {
            Path resolvedPath = path.resolve(entry.getName());
            if (!entry.isDirectory()) {
                // write file and create any needed paths
                Files.createDirectories(resolvedPath.getParent());
                Files.copy(inputStream, resolvedPath, StandardCopyOption.REPLACE_EXISTING);
            } else {
                // write directory
                Files.createDirectories(resolvedPath);
            }
        }
    }

    /**
     * Use OSI to generate SBOMs
     *
     * @param toolNames list of tools to use
     * @return List of file paths to all the newly generated SBOMs
     * @throws IOException problem with OSI connection
     */
    public List<String> generateSBOMs(List<String> toolNames) throws IOException {
        // Remove all SBOMs in the bound_dir folder before writing files
        BOUND_DIR.SBOMS.flush();

        // build connection
        HttpURLConnection conn =
                new OSIURLBuilder(this.osiRootEndpoint, OSIURLBuilder.RequestEndpoint.GENERATE, OSIURLBuilder.RequestMethod.POST).buildConnection();

        if (!toolNames.isEmpty()) {
            conn.setRequestProperty("Content-Type", "application/json");
            String jsonInputString = "{\"tools\": " + Arrays.toString(toolNames.toArray()) + "}";

            // append requested tools to the connection
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
        }

        conn.connect();
        // SBOMs weren't generate
        if (conn.getResponseCode() != 200 && conn.getResponseCode() != 204)
            return new ArrayList<>();
        conn.disconnect();

        // Copy full paths of the resulting SBOMs
        List<String> sbomPaths = new ArrayList<>();
        File[] files = new File(BOUND_DIR.SBOMS.getPath()).listFiles();
        assert files != null;
        for (File file : files) {
            // if file and skip .gitignore
            if (file.isFile() && !file.getName().equals(".gitignore"))
                sbomPaths.add(file.getPath());
        }

        // Delete code
        BOUND_DIR.CODE.flush();

        return sbomPaths;
    }

    /**
     * After construction, check if OSI available
     */
    @PostConstruct
    private void setStatus() {
        this.enabled = this.osi.healthcheck();
    }

    /**
     * @return If the service is enabled or not
     */
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * Bound Directory for code and sboms
     */
    private enum BOUND_DIR {
        CODE("code/"),
        SBOMS("sboms/");

        // The location of the bound directory relative to the build path (core).
        private static final String BOUND_DIR = "/core/src/main/java/org/svip/generation/osi/bound_dir/";
        private final String dirName;

        BOUND_DIR(String dirName) {
            this.dirName = dirName;
        }

        /**
         * Gets the path to the OSI bound_dir folder from anywhere in the system.
         *
         * @return Path to this target bound folder
         */
        private String getPath() {
            return System.getProperty("user.dir") + BOUND_DIR + dirName;
        }

        /**
         * Cleans the subdirectory in /bound_dir to remove all files and re-replace the .gitignore.
         *
         * @throws IOException If a file cannot be removed from the directory or if the .gitignore could not be written.
         */
        public void flush() throws IOException {
            File dir = new File(this.getPath());

            FileUtils.cleanDirectory(dir);

            // Add gitignore
            try (PrintWriter w = new PrintWriter(dir + "/.gitignore")) {
                w.println("*");
                w.println("!.gitignore");
            }
        }
    }

    @Component
    public static class OSI {

        @Value("${osi.api.url}")
        private String rootEndpoint;
        private final CloseableHttpClient httpClient = HttpClients.createDefault();

        /**
         * Make a healthcheck request to OSI to check if online
         *
         * @return True if alive, false otherwise
         */
        public boolean healthcheck() {
            try {
                // build the endpoint
                URI uri = this.initRequest("/healthcheck").build();
                // check status code
                try (CloseableHttpResponse response = this.request(new HttpGet(uri))) {
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
            return new URIBuilder(this.rootEndpoint + (path.startsWith("/") ? "" : '/') + path);
        }

        /**
         * Submit a request to OSI. Will raise for status
         *
         * @param request HTTP request make, ie GET, POST, etc
         * @return OSI response object
         * @throws IOException Failed to complete the request
         */
        public CloseableHttpResponse request(HttpRequestBase request) throws IOException {
            CloseableHttpResponse response = this.httpClient.execute(request);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            // raise for status
            if (!(statusCode >= 200 && statusCode < 300))
                throw new IOException("HTTP error: " + statusCode + " " + statusLine.getReasonPhrase());
            // request was successful
            return response;
        }
    }

    /**
     * URL Builder for requests to OSI
     */
    private static class OSIURLBuilder {

        private final String rootEndpoint;
        private final OSIURLBuilder.RequestEndpoint requestEndpoint;
        private final OSIURLBuilder.RequestMethod requestMethod;
        private final HashMap<String, String> requestParams = new HashMap<>();

        /**
         * Create builder with required arguments
         *
         * @param requestEndpoint Target OSI endpoint
         * @param requestMethod   http request method
         */
        public OSIURLBuilder(String rootEndpoint, RequestEndpoint requestEndpoint, RequestMethod requestMethod) {
            this.rootEndpoint = rootEndpoint;
            this.requestEndpoint = requestEndpoint;
            this.requestMethod = requestMethod;
        }

        /**
         * Add a request param to the url string
         *
         * @param param param / key
         * @param value value of param
         * @return OSIURLBuilder
         */
        public OSIURLBuilder addParam(String param, String value) {
            this.requestParams.put(param, value);
            return this;
        }

        /**
         * Create a new HTTP connection to OSI
         *
         * @return HTTP connection to OSI
         * @throws IOException Failed to build connection
         */
        public HttpURLConnection buildConnection() throws IOException {
            // Initial URL
            StringBuilder url = new StringBuilder(this.rootEndpoint + this.requestEndpoint);

            // Append parameters
            int paramCount = 0;
            for (String param : this.requestParams.keySet()) {
                url.append(paramCount++ == 0 ? "?" : "&")
                        .append(param)
                        .append("=")
                        .append(this.requestParams.get(param));
            }

            // Build connection
            HttpURLConnection conn = (HttpURLConnection) URI.create(url.toString()).toURL().openConnection();
            conn.setRequestMethod(this.requestMethod.value);

            // Get POST return value
            if (this.requestMethod == OSIURLBuilder.RequestMethod.POST)
                conn.setDoOutput(true);

            return conn;
        }

        // Request Method
        private enum RequestMethod {
            GET("GET"),
            POST("POST");
            private final String value;

            RequestMethod(String requestMethodStr) {
                this.value = requestMethodStr;
            }

            @Override
            public String toString() {
                return this.value;
            }
        }

        // OSI Endpoints
        private enum RequestEndpoint {
            TOOLS("tools"),
            GENERATE("generate");
            private final String value;

            RequestEndpoint(String requestEndpoint) {
                this.value = requestEndpoint;
            }

            @Override
            public String toString() {
                return this.value;
            }

        }


    }
}
