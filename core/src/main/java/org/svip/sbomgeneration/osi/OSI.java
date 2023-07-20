package org.svip.sbomgeneration.osi;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ListContainersCmd;
import com.github.dockerjava.api.command.WaitContainerResultCallback;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import org.apache.commons.io.FileUtils;
import org.svip.sbomgeneration.osi.exceptions.DockerNotAvailableException;

import java.io.*;
import java.time.Duration;

/**
 * file name: OSI.java
 * Open Source Integration. Interacts with Docker to build containers that use OS tools
 *
 * @author Derek Garcia
 * @author Matt London
 * @author Ian Dunn
 **/
public class OSI {

    private static final String BOUND_DIR = "/src/main/java/org/svip/sbomgeneration/osi/bound_dir";

    private final OSIClient osiClient;

    /**
     * Builds the docker container, but does not bind or run
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

        // Start new thread to build the docker image
        this.osiClient = new OSIClient();
    }

    ///
    /// Static Methods
    ///

    /**
     * Function to check if docker is installed and available
     *
     * @return 0 if docker is installed and available, 1 if docker is not running but installed, 2 if docker is not installed
     */
    protected static int dockerCheck() {
        try {
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

    public void addSourceFile(String fileName, String fileContents) throws IOException {
        File project = new File(System.getProperty("user.dir") + BOUND_DIR + "/code");

        if (!project.exists()) project.mkdirs();

        try (PrintWriter writer = new PrintWriter(project.getAbsolutePath() + "/" + fileName)) {
            writer.println(fileContents);
        } catch (FileNotFoundException e) {
            throw new IOException("Could not write file to /bound_dir/code/" + fileName);
        }
    }

    public void addSourceDirectory(File dirPath) throws IOException {
        File project = new File(System.getProperty("user.dir") + BOUND_DIR + "/code");

        if (!project.exists()) project.mkdirs();

        FileUtils.copyDirectory(dirPath, project);
    }

    /**
     * Uses Open Source tools to generate a series of SBOMs from the given source code
     *
     * @return success code
     */
    public int generateSBOMs() throws IOException {
        int code = osiClient.runContainer();
        cleanBoundDirectory("code");
        return code;
    }

    public void cleanBoundDirectory(String directoryName) throws IOException {
        String path = System.getProperty("user.dir") + BOUND_DIR + "/" + directoryName;
        File dir = new File(path);

        if (!dir.exists()) dir.mkdirs();

        FileUtils.cleanDirectory(dir);

        // Add gitignore
        try (PrintWriter w = new PrintWriter(path + "/.gitignore")) { w.print("*\n" + "!.gitignore"); }
    }

    /**
     * OSI Client to interact with Docker
     */
    private static class OSIClient {
        private final String OSI_CONTAINER_NAME = "svip-osi";

        private String osiContainerId;

        // Docker Components
        private final DockerClient dockerClient;

        /**
         * Inits default Docker Client Object to Use
         */
        public OSIClient() {
            // Check if docker is installed and running
            if (dockerCheck() != 0) {
                System.err.println("Docker is not running or not installed");

                throw new DockerNotAvailableException("Docker is not running or not installed");
            }

            // Default from GitHub
            // todo review and update for our needs
            DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().build();
            DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                    .dockerHost(config.getDockerHost())
                    .sslConfig(config.getSSLConfig())
                    .maxConnections(100)
                    .connectionTimeout(Duration.ofSeconds(30))
                    .responseTimeout(Duration.ofSeconds(45))
                    .build();
            this.dockerClient = DockerClientImpl.getInstance(config, httpClient);

            this.osiContainerId = getOSIContainerId(OSI_CONTAINER_NAME);
        }

        public String getOSIContainerId(String containerName) {
            ListContainersCmd listContainersCmd = dockerClient.listContainersCmd().withShowAll(true);

            for (Container container: listContainersCmd.exec())
                if (container.toString().contains(containerName)) return container.getId();

            return null;
        }

        /**
         * Runs the OSI container
         *
         * @return Container exit code
         */
        public int runContainer() {
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
