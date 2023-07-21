package org.svip.sbomgeneration.osi;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.WaitContainerResultCallback;
import com.github.dockerjava.api.exception.ConflictException;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import org.apache.commons.io.FileUtils;
import org.svip.sbomgeneration.osi.exceptions.DockerNotAvailableException;
import org.svip.utils.Debug;

import java.io.*;
import java.nio.file.Files;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * file name: OSI.java
 *
 * Open Source Integration. Interacts with Docker to build containers that use auto-detected open source tools to
 * generate SBOMs.
 *
 * @author Derek Garcia
 * @author Matt London
 * @author Ian Dunn
 **/
public class OSI {
    /**
     * The location of the bound directory relative to the build path (core).
     */
    private static final String BOUND_DIR = "/src/main/java/org/svip/sbomgeneration/osi/bound_dir";

    /**
     * The OSIClient that manages the container.
     */
    private final OSIClient client;

    /**
     * Cleans directories, checks that Docker is running, and then creates a container.
     */
    public OSI() throws DockerNotAvailableException, IOException {
        // Remove all SBOMs and source files in the bound_dir folder
        cleanBoundDirectory("code");
        cleanBoundDirectory("sboms");

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
     *         1 - Docker is not running but installed,
     *         2 - Docker is not installed
     */
    protected static int dockerCheck() {
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
            }
            else {
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
     * @param fileName The file name of the source file.
     * @param fileContents The contents of the source file.
     * @throws IOException If the file could not be written to the code bind directory.
     */
    public void addSourceFile(String fileName, String fileContents) throws IOException {
        File project = new File(System.getProperty("user.dir") + BOUND_DIR + "/code");

        if (!project.exists()) project.mkdirs();

        try (PrintWriter writer = new PrintWriter(project.getAbsolutePath() + "/" + fileName)) {
            writer.println(fileContents);
        } catch (FileNotFoundException e) {
            throw new IOException("Could not write file to /bound_dir/code/" + fileName);
        }
    }

    /**
     * Copies an entire directory of source files to generate an SBOM from when the container is run.
     *
     * @param dirPath The File object representing the directory to copy.
     * @throws IOException If one of the files in the copied directory could not be written to the code bind directory.
     */
    public void addSourceDirectory(File dirPath) throws IOException {
        File project = new File(System.getProperty("user.dir") + BOUND_DIR + "/code");

        if (!project.exists()) project.mkdirs();

        FileUtils.copyDirectory(dirPath, project);
    }

    /**
     * Use the Open Source Integration container via OSIClient to generate a series of SBOMs from the
     * given source code with a variety of tools (auto-detected).
     *
     * @return A map of each SBOM's filename to its contents.
     */
    public Map<String, String> generateSBOMs() throws IOException {
        int status = this.client.runContainer();

        cleanBoundDirectory("code");

        Map<String, String> sboms = new HashMap<>();

        File sbomDir = new File(System.getProperty("user.dir") + BOUND_DIR + "/sboms");
        // If container failed or files are null, return empty map.
        if (status == 1 || !sbomDir.exists() || sbomDir.listFiles() == null) return sboms;

        for (File file : sbomDir.listFiles())
            if (!file.getName().equalsIgnoreCase(".gitignore"))
                sboms.put(file.getName(), Files.readString(file.toPath()));

        return sboms;
    }

    /**
     * Cleans a subdirectory in the bound directory (sboms, code) to remove all files and then re-replace the
     * .gitignore.
     *
     * @param directoryName The directory name to clean.
     * @throws IOException If a file cannot be removed from the directory or if the .gitignore could not be written.
     */
    protected void cleanBoundDirectory(String directoryName) throws IOException {
        String path = System.getProperty("user.dir") + BOUND_DIR + "/" + directoryName;
        File dir = new File(path);

        if (!dir.exists()) dir.mkdirs();

        FileUtils.cleanDirectory(dir);

        // Add gitignore
        try (PrintWriter w = new PrintWriter(path + "/.gitignore")) { w.print("*\n" + "!.gitignore"); }
    }



    /**
     * OSI Client to interact with Docker.
     */
    private static class OSIClient {

        /**
         * The ID of the created OSI container.
         */
        private final String osiContainerId;

        /**
         * The main Docker API client.
         */
        private final DockerClient dockerClient;

        /**
         * Initializes default Docker client object and creates OSI container.
         */
        public OSIClient() {
            // Check if docker is installed and running
            if (dockerCheck() != 0) {
                Debug.log(Debug.LOG_TYPE.ERROR, "Docker is not running or not installed");
                throw new DockerNotAvailableException("Docker is not running or not installed");
            }

            // Default from GitHub
            DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().build();
            DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                    .dockerHost(config.getDockerHost())
                    .sslConfig(config.getSSLConfig())
                    .maxConnections(100)
                    .connectionTimeout(Duration.ofSeconds(30))
                    .responseTimeout(Duration.ofSeconds(45))
                    .build();
            this.dockerClient = DockerClientImpl.getInstance(config, httpClient);

            this.osiContainerId = createContainer();
        }

        /**
         * Method to create the container (or find its ID if already created) from the ubuntu:latest OSI image.
         *
         * @return The ID of the container.
         */
        protected String createContainer() {
            try {
                CreateContainerResponse containerResponse = dockerClient
                        .createContainerCmd("ubuntu:latest")
                        .withName("svip-osi")
                        .exec();
                return containerResponse.getId();
            } catch (ConflictException e) { // Container already exists
                // Status 409: {"message":"Conflict. The container name \"/svip-osi\" is already in use by container \"f9762c4cf09e622daf09a95c22904b7fb39f6b9f2a2e2fea93010ea263260507\". You have to remove (or rename) that container to be able to reuse that name."}
                int startIndex = e.getMessage().indexOf("\"", e.getMessage().indexOf("in use by container")) + 1;
                int endIndex = e.getMessage().lastIndexOf("\\\"");
                return e.getMessage().substring(startIndex, endIndex);
            }
        }

        /**
         * Runs the OSI container to generate SBOMs on any source files.
         *
         * @return Container exit code
         */
        protected int runContainer() {
            // Run Container
            this.dockerClient.startContainerCmd(this.osiContainerId).exec();

            // Await completion
            int status = dockerClient.waitContainerCmd(this.osiContainerId)
                    .exec(new WaitContainerResultCallback())
                    .awaitStatusCode();

            return status;  // Docker exit code
        }
    }
}
