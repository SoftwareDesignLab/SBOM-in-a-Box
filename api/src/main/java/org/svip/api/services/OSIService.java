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

@Service
public class OSIService {

    private class OSIURLBuilder{

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

        private final String rootEndpoint = new File("/.dockerenv").exists()
                // If running in container, access OSI by container name due to Docker's default network
                ? "http://osi:5000/"
                // If running outside of container, access OSI by the container's port on localhost
                : "http://localhost:50001/"; // TODO Move port to config file

        private final OSIURLBuilder.RequestEndpoint requestEndpoint;
        private final OSIURLBuilder.RequestMethod requestMethod;

        private final HashMap<String, String> requestParams = new HashMap<>();


        public OSIURLBuilder(OSIURLBuilder.RequestEndpoint requestEndpoint, OSIURLBuilder.RequestMethod requestMethod){
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

            if(this.requestMethod == OSIURLBuilder.RequestMethod.POST)
                conn.setDoOutput(true);

            return conn;


        }


    }

    private enum BOUND_DIR {
        CODE("code/"),
        SBOMS("sboms/");

        // The location of the bound directory relative to the build path (core).
        private static final String BOUND_DIR = "/core/src/main/java/org/svip/generation/osi/bound_dir/";
        // The directory name of the /bound_dir subdirectory
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

    private boolean enabled = false;


    public OSIService(){

        try{
            this.enabled = isOSIContainerAvailable();
        } catch (Exception ignored){

        }

    }


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




    public List<String> generateSBOMs(List<String> toolNames) throws Exception {
        BOUND_DIR.SBOMS.flush();

        HttpURLConnection conn =
                new OSIURLBuilder(OSIURLBuilder.RequestEndpoint.GENERATE, OSIURLBuilder.RequestMethod.POST).buildConnection();;

        try (OutputStream os = conn.getOutputStream()) {
            try (OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_8)) {
                // Ensure there is at least one valid tool, if not don't put anything in the request body
                if (toolNames != null && !toolNames.isEmpty())
                    osw.write(toolNames.toString());
                osw.flush();
            }
        }

        conn.connect();

        // SBOMs weren't generate
        if (conn.getResponseCode() != 200 && conn.getResponseCode() != 204)
            return new ArrayList<>();

        conn.disconnect();
        List<String> sbomPaths = new ArrayList<>();
        File[] files = new File(BOUND_DIR.SBOMS.getPath()).listFiles();
        assert files != null;
        for (File file : files) {
            if (file.isFile() && !file.getName().equals(".gitignore")) {
                sbomPaths.add(file.getPath());
            }
        }

        return sbomPaths;
    }

    public void addProject(ZipInputStream inputStream) throws IOException {
        // Remove all SBOMs and source files in the bound_dir folder before uploading files
        BOUND_DIR.CODE.flush();

        Path path = Paths.get(BOUND_DIR.CODE.getPath());
        for (ZipEntry entry; (entry = inputStream.getNextEntry()) != null; ) {
            Path resolvedPath = path.resolve(entry.getName());
            if (!entry.isDirectory()) {
                Files.createDirectories(resolvedPath.getParent());
                Files.copy(inputStream, resolvedPath, StandardCopyOption.REPLACE_EXISTING);
            } else {
                Files.createDirectories(resolvedPath);
            }
        }
    }

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

            HttpURLConnection conn =
                    new OSIURLBuilder(OSIURLBuilder.RequestEndpoint.TOOLS, OSIURLBuilder.RequestMethod.GET).buildConnection();

            conn.connect();
            if (conn.getResponseCode() != 200)
                return false;

            conn.disconnect();
        } catch (IOException e) {
            throw new DockerNotAvailableException(Arrays.toString(e.getStackTrace()));
        }

        return true;
    }


}
