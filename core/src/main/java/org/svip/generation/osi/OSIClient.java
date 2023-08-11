package org.svip.generation.osi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.svip.generation.osi.exceptions.DockerNotAvailableException;
import org.svip.utils.Debug;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * File: OSIClient.java
 *
 * OSI Client to interact with the Docker container's Flask API to get and use selected Open Source Tools to generate
 * multiple SBOMs.
 *
 * @author Derek Garcia
 * @author Matt London
 * @author Ian Dunn
 **/
public class OSIClient {

    /**
     * Private enumeration to store all possible calls to the Docker API container.
     */
    private enum URL {
        GET_TOOLS("tools", "GET"),
        GENERATE("generate", "POST");

        /**
         * The root URL of the Flask API inside the Docker container.
         */
        private static final String url = "http://localhost:5000/"; // TODO Move port to config file

        /**
         * The endpoint of the enum.
         */
        private final String endpoint;

        /**
         * The intended request method of the enum.
         */
        private final String requestMethod;

        URL(String endpoint, String requestMethod) {
            this.endpoint = endpoint;
            this.requestMethod = requestMethod;
        }

        public java.net.URL getURL() throws MalformedURLException {
            return new java.net.URL(url + endpoint);
        }

        public String getRequestMethod() {
            return requestMethod;
        }
    }

    /**
     * Initializes default Docker client object and creates OSI container.
     *
     * @throws DockerNotAvailableException If an error occurred creating the container.
     */
    public OSIClient() throws DockerNotAvailableException {
        // Check if docker is installed and running
        if (dockerCheck() != 0) {
            Debug.log(Debug.LOG_TYPE.ERROR, "Docker is not running or not installed");
            throw new DockerNotAvailableException("Docker is not running or not installed");
        }
    }

    protected List<String> getAllTools() {
        try {
            HttpURLConnection conn = connectToURL(URL.GET_TOOLS);

            BufferedReader bufferedReader =
                    new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder builder = new StringBuilder();

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                builder.append(line).append('\n');
            }

            conn.disconnect();

            String jsonString = builder.toString();
            ObjectMapper mapper = new ObjectMapper();

            return (List<String>) mapper.readValue(jsonString, List.class);
        } catch (IOException e) {
            return null;
        }
    }

    protected boolean generateSBOMs(List<String> toolNames) {
        try {
            HttpURLConnection conn = connectToURL(URL.GENERATE);

            try (OutputStream os = conn.getOutputStream()) {
                try (OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_8)) {
                    // Ensure there is at least one valid tool, if not don't put anything in the request body
                    if (toolNames != null && toolNames.size() > 0) osw.write(toolNames.toString());
                    osw.flush();
                }
            }

            conn.connect();

            // DO NOT REMOVE THIS LINE. This is needed for a connection to actually be made, regardless of
            // calling .connect()
            if (conn.getResponseCode() != 200 || conn.getResponseCode() != 204) return false;

            conn.disconnect();
        } catch (IOException e) {
            return false;
        }

        return true; // Return true, assuming a connection has been made
    }

    /**
     * Function to check if docker is installed and available
     *
     * @return 0 - Docker is installed and available,
     *         1 - Docker is not running but installed,
     *         2 - Docker is not installed
     */
    public static int dockerCheck() {
        try {
            HttpURLConnection conn = connectToURL(URL.GET_TOOLS);

            conn.connect();
            if (conn.getResponseCode() != 200) return 1;
            conn.disconnect();
        } catch (IOException e) {
            return 1;
        }

        return 0;
    }

    /**
     * Private helper method to connect to a URL and return the configured HttpURLConnection.
     *
     * @param url The URL enum to connect to.
     * @return The configured HttpURLConnection of that URL.
     * @throws IOException If a connection could not be made or there was a configuration error.
     */
    private static HttpURLConnection connectToURL(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.getURL().openConnection();
        conn.setRequestMethod(url.getRequestMethod());

        if (url.getRequestMethod().equals("POST")) conn.setDoOutput(true);

        return conn;
    }
}
