package org.svip.generation.osi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.svip.generation.osi.exceptions.DockerNotAvailableException;
import org.svip.utils.Debug;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * file name: OSI.java
 * <p>
 * Open Source Integration. Interacts with Docker to build containers that use auto-detected open source tools to
 * generate SBOMs.
 *
 * @author Derek Garcia
 * @author Matt London
 * @author Ian Dunn
 **/
public class OSI {

    /**
     * Private enumeration to store, validate, and retrieve full file paths for subdirectories of /bound_dir
     */
    private enum BOUND_DIRS {
        CODE("code"),
        SBOMS("sboms");

        /**
         * The location of the bound directory relative to the build path (core).
         */
        private static final String BOUND_DIR = "/src/main/java/org/svip/generation/osi/bound_dir";

        /**
         * The directory name of the /bound_dir subdirectory
         */
        private final String dirName;

        BOUND_DIRS(String dirName) {
            this.dirName = dirName;
        }

        /**
         * Gets the path to the OSI bound_dir folder from anywhere in the system.
         *
         * @return A File containing a reference to that folder, which is guaranteed to exist.
         */
        public File getPath() {
            String path = System.getProperty("user.dir") + BOUND_DIR + "/" + dirName;
            // Replace api from core if running in api working directory
            File file = new File(path.replaceFirst("api", "core"));

            // Make directories
            if (!file.exists()) file.mkdirs();

            return file;
        }

        /**
         * Cleans the subdirectory in /bound_dir to remove all files and re-replace the .gitignore.
         *
         * @throws IOException If a file cannot be removed from the directory or if the .gitignore could not be written.
         */
        public void cleanPath() throws IOException {
            File dir = this.getPath();

            FileUtils.cleanDirectory(dir);

            // Add gitignore
            try (PrintWriter w = new PrintWriter(dir + "/.gitignore")) {
                w.print("*\n" + "!.gitignore");
            }
        }
    }

    /**
     * The OSIClient that manages the container.
     */
    private final OSIClient client;

    /**
     * Cleans directories, checks that Docker is running, and then creates a container.
     *
     * @throws DockerNotAvailableException If Docker is not installed or not running, or if the container couldn't be
     *                                     constructed
     * @throws IOException                 If one of the bound directories could not be cleaned.
     */
    public OSI() throws DockerNotAvailableException, IOException {
        // Remove all SBOMs and source files in the bound_dir folder
        BOUND_DIRS.CODE.cleanPath();
        BOUND_DIRS.SBOMS.cleanPath();

        // Run Docker check and throw if we can't validate an install
        int dockerStatus = dockerCheck();

        switch (dockerStatus) {
            case 0:
                // Docker is installed and running
                break;
            case 1:
                // Docker is installed but not running
                throw new DockerNotAvailableException("Docker is installed but not running");
            case 2:
                // Docker is not installed
                throw new DockerNotAvailableException("Docker is not installed");
        }

        this.client = new OSIClient(); // Create container
    }

    /**
     * Function to check if docker is installed and available
     *
     * @return 0 - Docker is installed and available,
     * 1 - Docker is not running but installed,
     * 2 - Docker is not installed
     */
    public static int dockerCheck() {
        try {
            // If running in a container, we know the Docker daemon is available (with a system host link)
            File f = new File("/.dockerenv");
            if (f.exists()) return 0;

            // Check if docker is installed
            Process process = Runtime.getRuntime().exec("docker --version");
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = br.readLine();
            br.close();
            // See if it returns the expected response for an installed application
            if (line == null || !line.startsWith("Docker version")) {
                // This means docker is not installed
                return 2;
            }

            // Check if Docker daemon is running
            process = Runtime.getRuntime().exec("docker ps");
            br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            line = br.readLine();
            br.close();

            if (line != null && line.startsWith("CONTAINER ID")) {
                // This means docker is installed and running
                return 0;
            } else {
                // This means docker is not running
                return 1;
            }
        } catch (IOException e) {
            // This means that the command hit an unknown error, we can assume that means docker is not installed
            return 2;
        }

    }

    /**
     * Adds a source file to generate an SBOM from when the container is run.
     *
     * @param fileName     The file name of the source file.
     * @param fileContents The contents of the source file.
     * @throws IOException If the file could not be written to the code bind directory.
     */
    public void addSourceFile(String fileName, String fileContents) throws IOException {
        File project = BOUND_DIRS.CODE.getPath();

        // Constructing the printwriter with a file means that it takes care of all system-specific path problems
        try (PrintWriter writer = new PrintWriter(new File(project.getAbsolutePath() + "/" + fileName))) {
            writer.println(fileContents);
        } catch (FileNotFoundException e) {
            throw new IOException("Could not write file to " + project);
        }
    }

    /**
     * Copies an entire directory of source files to generate an SBOM from when the container is run.
     *
     * @param dirPath The File object representing the directory to copy.
     * @throws IOException If one of the files in the copied directory could not be written to the code bind directory.
     */
    public void addSourceDirectory(File dirPath) throws IOException {
        File project = BOUND_DIRS.CODE.getPath();

        FileUtils.copyDirectory(dirPath, project);
    }

    public List<String> getAllTools() {
        return client.getAllTools();
    }

    /**
     * Use the Open Source Integration container via OSIClient to generate a series of SBOMs from the
     * given source code given a list of tool names to use.
     *
     * @param toolNames A list of tool names. Invalid/non-applicable tool names will be skipped.
     * @return A map of each SBOM's filename to its contents.
     */
    public Map<String, String> generateSBOMs(List<String> toolNames) throws IOException {
        boolean status = this.client.generateSBOMs(toolNames);

        BOUND_DIRS.CODE.cleanPath();

        Map<String, String> sboms = new HashMap<>();

        File sbomDir = BOUND_DIRS.SBOMS.getPath();
        // If container failed or files are null, return empty map.
        if (status || !sbomDir.exists() || sbomDir.listFiles() == null) return sboms;

        for (File file : sbomDir.listFiles())
            if (!file.getName().equalsIgnoreCase(".gitignore"))
                sboms.put(file.getName(), Files.readString(file.toPath()));

        BOUND_DIRS.SBOMS.cleanPath();

        return sboms;
    }

    /**
     * OSI Client to interact with the Docker container's Flask API.
     */
    private static class OSIClient {

        private static final String url = "http://localhost:5000/";

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
            String jsonString;
            HttpURLConnection conn = null;

            try {
                URL tools = new URL(url + "tools");
                conn = (HttpURLConnection) tools.openConnection();
                conn.setRequestMethod("GET");
                BufferedReader bufferedReader =
                        new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder builder = new StringBuilder();

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    builder.append(line).append('\n');
                }

                jsonString = builder.toString();
            } catch (IOException e) {
                return null;
            } finally {
                if (conn != null) conn.disconnect();
            }

            ObjectMapper mapper = new ObjectMapper();
            try {
                return (List<String>) mapper.readValue(jsonString, List.class);
            } catch (JsonProcessingException e) {
                return null;
            }
        }

        protected boolean generateSBOMs(List<String> toolNames) {
            HttpURLConnection conn = null;

            try {
                URL tools = new URL(url + "generate");
                conn = (HttpURLConnection) tools.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

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
            } catch (IOException e) {
                return false;
            } finally {
                if (conn != null) conn.disconnect();
            }

            return true;
        }
    }
}
