package org.svip.generation.osi;

import org.apache.commons.io.FileUtils;
import org.svip.generation.osi.exceptions.DockerNotAvailableException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.svip.generation.osi.OSIClient.isOSIContainerAvailable;

/**
 * File: OSI.java
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
        BOUND_DIR.CODE.flush();
        BOUND_DIR.SBOMS.flush();

        // Run Docker check and throw if we can't validate an install
        if (!isOSIContainerAvailable())
            throw new DockerNotAvailableException("OSI container API not running or returned non-200 response code.");

        this.client = new OSIClient(); // Create OSIClient instance
    }

    public void addProject(ZipInputStream inputStream) throws IOException {
        // Remove all SBOMs and source files in the bound_dir folder before uploading files
        BOUND_DIR.CODE.flush();

        Path path = Paths.get(BOUND_DIR.CODE.getPath());
        for (ZipEntry entry; (entry = inputStream.getNextEntry()) != null; ) {
            Path resolvedPath = path.resolve(entry.getName());
            if (!entry.isDirectory()) {
                Files.createDirectories(resolvedPath.getParent());
                Files.copy(inputStream, resolvedPath);
            } else {
                Files.createDirectories(resolvedPath);
            }
        }
    }

    /**
     * Get the list of all OSI tools
     *
     * @param listType Either "all" (default) or "project",
     *             all gets all tools installed in OSI
     *             project gets all applicable tools installed for the project in the bound directory
     * @return List of OIS tools
     */
    public List<String> getTools(String listType) {
        return client.getTools(listType);
    }

    /**
     * Use the Open Source Integration container via OSIClient to generate a series of SBOMs from the
     * given source code given a list of tool names to use.
     *
     * @param toolNames A list of tool names. If null or empty, by default all tools will be used.
     *                  Invalid/non-applicable tool names will be skipped.
     * @return A map of each SBOM's filename to its contents.
     */
    public Map<String, String> generateSBOMs(List<String> toolNames) throws IOException {
        // remove any old sboms
        BOUND_DIR.SBOMS.flush();

        boolean status = this.client.generateSBOMs(toolNames);

        Map<String, String> sboms = new HashMap<>();

        File sbomDir = new File(BOUND_DIR.SBOMS.getPath());
        // If container failed or files are null, return empty map.
        if (status || !sbomDir.exists() || sbomDir.listFiles() == null) return sboms;

        for (File file : Objects.requireNonNull(sbomDir.listFiles()))
            if (!file.getName().equalsIgnoreCase(".gitignore"))
                sboms.put(file.getName(), Files.readString(file.toPath()));

        // cleanup
        BOUND_DIR.CODE.flush();
        BOUND_DIR.SBOMS.flush();

        return sboms;
    }
}
