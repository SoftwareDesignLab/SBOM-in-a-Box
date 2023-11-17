package org.svip.generation.osi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.svip.generation.osi.exceptions.DockerNotAvailableException;
import org.svip.utils.Debug;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
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

    private static class OSIURLBuilder{

        public enum RequestMethod {

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

        public enum RequestEndpoint {

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

        private final String rootEndpoint = isRunningInsideContainer()
                // If running in container, access OSI by container name due to Docker's default network
                ? "http://osi:5000/"
                // If running outside of container, access OSI by the container's port on localhost
                : "http://localhost:50001/"; // TODO Move port to config file

        private final RequestEndpoint requestEndpoint;
        private final RequestMethod requestMethod;

        private final HashMap<String, String> requestParams = new HashMap<>();


        public OSIURLBuilder(RequestEndpoint requestEndpoint, RequestMethod requestMethod){
            this.requestEndpoint = requestEndpoint;
            this.requestMethod = requestMethod;
        }

        public OSIURLBuilder addParam(String param, String value){
            this.requestParams.put(param, value);
            return this;
        }

        public HttpURLConnection buildConnection() throws IOException {
            StringBuilder url = new StringBuilder(this.rootEndpoint + this.requestEndpoint);

            int paramCount = 0;
            for(String param : this.requestParams.keySet()) {
                url.append(paramCount++ == 0 ? "?" : "&")
                        .append(param)
                        .append("=")
                        .append(this.requestParams.get(param));
            }


            HttpURLConnection conn = (HttpURLConnection) new java.net.URL(url.toString()).openConnection();
            conn.setRequestMethod(this.requestMethod.value);

            if(this.requestMethod == RequestMethod.POST)
                conn.setDoOutput(true);

            return conn;


        }


    }

    /**
     * Initializes default Docker client object and creates OSI container.
     *
     * @throws DockerNotAvailableException If an error occurred creating the container.
     */
    public OSIClient() throws DockerNotAvailableException {
        // Check if docker is installed and running
        if (!isOSIContainerAvailable()) {
            Debug.log(Debug.LOG_TYPE.ERROR, "Docker is not running or not installed");
            throw new DockerNotAvailableException("Docker is not running or not installed");
        }
    }

    /**
     * Sends a request to the OSI API to retrieve a list of all available tool names.
     *
     * @return A list of all available tool names.
     */
    public List<String> getTools(String listType) {
        try {

            OSIURLBuilder osiurlBuilder =
                    new OSIURLBuilder(OSIURLBuilder.RequestEndpoint.TOOLS, OSIURLBuilder.RequestMethod.GET);
            osiurlBuilder.addParam("list", listType);

            HttpURLConnection conn = osiurlBuilder.buildConnection();

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

    /**
     * Sends a request to the OSI API to generate SBOMs using a provided list of tool names.
     *
     * @param toolNames A list of tool names. If null or empty, all tools will be used by default. Any invalid or
     *                  non-applicable tool names will be skipped.
     * @return True if the SBOMs were successfully generated in the /sboms directory, false if otherwise.
     */
    public boolean generateSBOMs(List<String> toolNames) {
        try {
            HttpURLConnection conn =
                    new OSIURLBuilder(OSIURLBuilder.RequestEndpoint.GENERATE, OSIURLBuilder.RequestMethod.POST).buildConnection();;

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
     * Function to check if the Docker API is running.
     *
     * @return True if the Docker API is running and can accept connections.
     *         False if the Docker API returned an error when pinging.
     * @throws DockerNotAvailableException If the container is not accessible/running at all.
     */
    public static boolean isOSIContainerAvailable() throws DockerNotAvailableException {
        try {

            HttpURLConnection conn =
                    new OSIURLBuilder(OSIURLBuilder.RequestEndpoint.TOOLS, OSIURLBuilder.RequestMethod.GET).buildConnection();

            conn.connect();
            if (conn.getResponseCode() != 200) return false;
            conn.disconnect();
        } catch (IOException e) {
            throw new DockerNotAvailableException(Arrays.toString(e.getStackTrace()));
        }

        return true;
    }

    /**
     * Tests to see if SVIP is running inside a Docker container.
     *
     * @return True if SVIP is running inside a Docker container, false otherwise.
     */
    public static boolean isRunningInsideContainer() {
        // All docker containers have a .dockerenv file at the root, so just check for that.
        File f = new File("/.dockerenv");
        return f.exists();
    }
}
