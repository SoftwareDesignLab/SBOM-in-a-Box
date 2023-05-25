package org.svip.sbomfactory.osi;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.BuildImageResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.WaitContainerResultCallback;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Mount;
import com.github.dockerjava.api.model.MountType;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import org.apache.commons.io.FileUtils;
import org.svip.sbomfactory.osi.exceptions.DockerNotAvailableException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * file name: OSI.java
 * Open Source Integration. Interacts with Docker to build containers that use OS tools
 *
 * @author Derek Garcia
 * @author Matt London
 * @author Ian Dunn
 **/
public class OSI {

    private final OSIClient osiClient;

    /**
     * Builds the docker container, but does not bind or run
     */
    public OSI(String osiBoundDir, String dockerPath) throws DockerNotAvailableException {
        // Remove all SBOMs in the bound_dir/sboms folder, as it can cause crashing when permission is denied within the container
        File sboms = new File(osiBoundDir + "/sboms");
        // Create directory if it doesn't exist
        if (!sboms.exists()) {
            sboms.mkdirs();
        }
        for (File sbom: sboms.listFiles()) {
            // Don't delete git ignore
            if (!sbom.getName().equals(".gitignore")) {
                try {
                    FileUtils.forceDelete(sbom);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // Docker container will later try and delete all SBOMs on startup, but it will fail if the permissions aren't there
        // init docker client

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
        OSIThread dockerThread = new OSIThread(osiBoundDir, dockerPath);
        dockerThread.start();

        // Try to join this to main thread - halts the program until the OSI Client exists
        try {
            dockerThread.join(); // Join thread to main process to wait for image to build
            osiClient = dockerThread.getOSIClient(); // Get OSI client back from thread
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
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

    /**
     * Uses Open Source tools to generate a series of SBOMs from the given source code
     *
     * @param src Path to target source code
     * @return success code
     */
    public int generateSBOMs(String src) {
        // TODO possibly use OSIThread.isImageBuilt to show that docker image is building

        // Create and run Container
        CreateContainerResponse container = osiClient.createContainerInstance(src);

        return osiClient.runCleanContainer(container);
    }

    /**
     * Close the OSI instance
     */
    public void close() {
        // Cleanup
        osiClient.close();
    }

    /**
     *  Thread to run the OSI client separately from the main thread
     */
    private static class OSIThread extends Thread {
        private OSIClient osiClient;
        private String osiBoundDir;
        private String dockerPath;

        public OSIThread(String osiBoundDir, String dockerPath) {
            this.osiBoundDir = osiBoundDir;
            this.dockerPath = dockerPath;
        }

        /**
         * Executes when the thread is run
         */
        @Override
        public void run() {
            osiClient = new OSIClient(osiBoundDir, dockerPath); // Build image and get client
            // TODO: Use thread to get estimated uptime and estimated time remaining (see OSIClient.buildImage)
        }

        /**
         * Return the OSIClient generated by the thread
         *
         * @return The client to interact with a built image
         */
        public OSIClient getOSIClient() {
            return osiClient;
        }

        /**
         * Check if the image is completely built
         *
         * @return True if the image is built, false if otherwise
         */
        public boolean isImageBuilt() {
            return !this.isAlive(); // While thread is still alive, image is still building
        }
    }

    /**
     * OSI Client to interact with Docker
     */
    private static class OSIClient {

        private final String osiDockerfile;
        private final String osiBoundDir;
        private final String OSI_CONTAINER_NAME = "OSI-Container";

        // Docker Components
        private final DockerClient dockerClient;
        private final HashSet<String> OSI_DOCKER_TAGS = new HashSet<>(List.of("svip-osi"));
        private final String imageID;
        /**
         * Inits default Docker Client Object to Use
         */
        public OSIClient(String osiBoundDir, String dockerPath) {
            this.osiBoundDir = osiBoundDir;
            this.osiDockerfile = System.getProperty("user.dir") + dockerPath;

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
            this.imageID = buildImage();
        }

    
        /**
         * Builds a new image from the preset Dockerfile
         *
         * @return imageID string
         */
        private String buildImage() {
            // todo nicer async call to report build status, 1st time build approx 6 min
            // Build image

            return dockerClient.buildImageCmd()
                    .withDockerfile(new File(osiDockerfile))
                    .withTags(OSI_DOCKER_TAGS)
                    .exec(new BuildImageResultCallback())
                    .awaitImageId();
        }


        /**
         * Creates an instance of the Docker Image stored as a Container
         *
         * @param codePath Path to the source code to be analyzed
         * @return Docker Container Object
         */
        public CreateContainerResponse createContainerInstance(String codePath) {

            // Check for duplicate Containers and remove
            List<Container> duplicates = this.dockerClient.listContainersCmd()
                    .withShowAll(true)
                    .withNameFilter(new ArrayList<>(List.of(OSI_CONTAINER_NAME)))
                    .exec();
            // todo instead of removing reuse container?
            for (Container c : duplicates)
                this.dockerClient.removeContainerCmd(c.getId()).exec();

            // Create Mounting
            // /bound_dir/sbom mounting
            Mount sbomMount = new Mount()
                    .withType(MountType.BIND)   // type=bind
                    .withSource(System.getProperty("user.dir") + "/" + osiBoundDir + "/sboms")   // source="$(pwd)"/bound_dir
                    .withTarget("/bound_dir/sboms");  // target=/bound_dir/sbom
            // /bound_dir/code mounting
            Mount codeMount = new Mount()
                    .withType(MountType.BIND)   // type=bind
                    .withSource(codePath)   // source="$(pwd)"/bound_dir
                    .withTarget("/bound_dir/code");  // target=/bound_dir/sbom

            // Apply mounted directory
            HostConfig hostConfig = HostConfig.newHostConfig()
                    .withMounts(new ArrayList<>(List.of(sbomMount, codeMount)));

            // Build and return new container
            return this.dockerClient.createContainerCmd(this.imageID)
                    .withName(OSI_CONTAINER_NAME)
                    .withTty(true)          // -t
                    .withAttachStdin(true)  // -i
                    .withHostConfig(hostConfig)
                    .exec();
        }

        /**
         * Runs the given container
         *
         * @param container Container to run
         * @return Container exit code
         */
        public int runContainer(CreateContainerResponse container) {
            // Run Container
            this.dockerClient.startContainerCmd(container.getId()).exec();

            // Await completion
            int status = dockerClient.waitContainerCmd(container.getId())
                    .exec(new WaitContainerResultCallback())
                    .awaitStatusCode();

            return status;  // Docker exit code
        }

        /**
         * Cleans the given container
         *
         * @param container Container to clean
          */
        public void cleanContainer(CreateContainerResponse container) {
            // Remove container
            this.dockerClient.removeContainerCmd(container.getId()).exec();   // --rm
            try {
                this.dockerClient.close();
            }
            catch (IOException e) {
                System.err.println("Failed to close docker client");
            }
        }

        /**
         * Runs the given container and removes it when compete
         *
         * @param container Container to run
         * @return Container exit code
         */
        public int runCleanContainer(CreateContainerResponse container) {

            // Run Container
            this.dockerClient.startContainerCmd(container.getId()).exec();

            // Await completion
            int status = dockerClient.waitContainerCmd(container.getId())
                    .exec(new WaitContainerResultCallback())
                    .awaitStatusCode();

            // Remove container
            this.dockerClient.removeContainerCmd(container.getId()).exec();   // --rm
            return status;  // Docker exit code
        }

        /**
         * Closes the Docker Client
         *
         * @return exit code
         */
        public int close() {
            try {
                this.dockerClient.close();
                return 0;
            } catch (IOException e) {
                System.err.println("Failed to close docker client");
                return 1;
            }
        }

    }
}
