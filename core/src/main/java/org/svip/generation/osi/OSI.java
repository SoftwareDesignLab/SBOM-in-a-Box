package org.svip.generation.osi;

import org.apache.commons.io.FileUtils;
import org.svip.generation.osi.exceptions.DockerNotAvailableException;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.svip.generation.osi.OSIClient.dockerCheck;

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

        BOUND_DIR(String dirName) {
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
        BOUND_DIR.CODE.cleanPath();
        BOUND_DIR.SBOMS.cleanPath();

        // Run Docker check and throw if we can't validate an install
        if (dockerCheck() == 1)
            throw new DockerNotAvailableException("OSI container API returned non-200 response code.");

        this.client = new OSIClient(); // Create OSIClient instance
    }

    /**
     * Adds a source file to generate an SBOM from when the container is run.
     *
     * @param fileName     The file name of the source file.
     * @param fileContents The contents of the source file.
     * @throws IOException If the file could not be written to the code bind directory.
     */
    public void addSourceFile(String fileName, String fileContents) throws IOException {
        File project = BOUND_DIR.CODE.getPath();

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
        File project = BOUND_DIR.CODE.getPath();

        FileUtils.copyDirectory(dirPath, project);
    }

    public List<String> getAllTools() {
        return client.getAllTools();
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
        boolean status = this.client.generateSBOMs(toolNames);

        BOUND_DIR.CODE.cleanPath();

        Map<String, String> sboms = new HashMap<>();

        File sbomDir = BOUND_DIR.SBOMS.getPath();
        // If container failed or files are null, return empty map.
        if (status || !sbomDir.exists() || sbomDir.listFiles() == null) return sboms;

        for (File file : sbomDir.listFiles())
            if (!file.getName().equalsIgnoreCase(".gitignore"))
                sboms.put(file.getName(), Files.readString(file.toPath()));

        BOUND_DIR.SBOMS.cleanPath();

        return sboms;
    }
}
