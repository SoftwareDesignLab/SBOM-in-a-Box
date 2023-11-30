package org.svip.api.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.svip.generation.osi.exceptions.DockerNotAvailableException;

import java.io.*;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
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

    ///
    /// UTILITY CLASSES
    ///

    /**
     * URL Builder for requests to OSI
     */
    private static class OSIURLBuilder{

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

        /*
        Build root based on run location. NOTE if running OSI outside the container (not recommended)
        the endpoint will be at `http://localhost:5000/`, these services will most likely fail
         */
        private final String rootEndpoint = new File("/.dockerenv").exists()
                // If running in container, access OSI by container name due to Docker's default network
                ? "http://osi:5000/"
                // If running outside of container, access OSI by the container's port on localhost
                : "http://localhost:50001/"; // TODO Move port to config file

        private final OSIURLBuilder.RequestEndpoint requestEndpoint;
        private final OSIURLBuilder.RequestMethod requestMethod;
        private final HashMap<String, String> requestParams = new HashMap<>();

        /**
         * Create builder with required arguments
         *
         * @param requestEndpoint Target OSI endpoint
         * @param requestMethod http request method
         */
        public OSIURLBuilder(RequestEndpoint requestEndpoint, RequestMethod requestMethod){
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
        public OSIURLBuilder addParam(String param, String value){
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
            for(String param : this.requestParams.keySet()) {
                url.append(paramCount++ == 0 ? "?" : "&")
                        .append(param)
                        .append("=")
                        .append(this.requestParams.get(param));
            }

            // Build connection
            HttpURLConnection conn = (HttpURLConnection) new java.net.URL(url.toString()).openConnection();
            conn.setRequestMethod(this.requestMethod.value);

            // Get POST return value
            if(this.requestMethod == OSIURLBuilder.RequestMethod.POST)
                conn.setDoOutput(true);

            return conn;
        }


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


    //
    // OSI SERVICE METHODS
    //

    private boolean enabled = false;    // default to no access to OSI

    /**
     * Attempt to create a new service if OSI is available
     */
    public OSIService(){
        try{
            this.enabled = isOSIContainerAvailable();
        } catch (Exception ignored){
        }
    }


    /**
     * Get a list of tools from OSI based on parameter
     *
     * @param list Optional argument, either "all" (default) or "project",
     *             all gets all tools installed in OSI
     *             project gets all applicable tools installed for the project in the bound directory
     * @return A list of string tool names.
     */
    public List<String> getTools(String list) {
        try {
            // Build new connection
            OSIURLBuilder osiurlBuilder =
                    new OSIURLBuilder(OSIURLBuilder.RequestEndpoint.TOOLS, OSIURLBuilder.RequestMethod.GET);
            osiurlBuilder.addParam("list", list);
            HttpURLConnection conn = osiurlBuilder.buildConnection();

            // Create new br to read response
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder builder = new StringBuilder();

            // Parse output
            String line;
            while ((line = bufferedReader.readLine()) != null)
                builder.append(line).append('\n');

            conn.disconnect();

            // Convert json string to list
            String jsonString = builder.toString();
            ObjectMapper mapper = new ObjectMapper();
            return (List<String>) mapper.readValue(jsonString, List.class);
        } catch (IOException e) {
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
                new OSIURLBuilder(OSIURLBuilder.RequestEndpoint.GENERATE, OSIURLBuilder.RequestMethod.POST).buildConnection();

        if(!toolNames.isEmpty()){
            conn.setRequestProperty("Content-Type", "application/json");
            String jsonInputString = "{\"tools\": " + Arrays.toString(toolNames.toArray()) + "}";

            // append requested tools to the connection
            try(OutputStream os = conn.getOutputStream()) {
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

    ///
    /// OSI SERVICE UTILITY METHODS
    ///

    /**
     * @return If the service is enabled or not
     */
    public boolean isEnabled(){
        return this.enabled;
    }


    /**
     * Function to check if the Docker API is running.
     *
     * @return True if the Docker API is running and can accept connections.
     *         False if the Docker API returned an error when pinging.
     * @throws DockerNotAvailableException If the container is not accessible/running at all.
     */
    private boolean isOSIContainerAvailable() throws DockerNotAvailableException {
        try {
            // build connection
            HttpURLConnection conn =
                    new OSIURLBuilder(OSIURLBuilder.RequestEndpoint.TOOLS, OSIURLBuilder.RequestMethod.GET).buildConnection();

            // test connection
            conn.connect();
            if (conn.getResponseCode() != 200)
                return false;

            conn.disconnect();
        } catch (IOException e) {
            // OSI not available
            throw new DockerNotAvailableException(Arrays.toString(e.getStackTrace()));
        }
        // OSI available
        return true;
    }


}
