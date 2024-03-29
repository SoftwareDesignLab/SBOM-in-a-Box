/** Copyright 2021 Rochester Institute of Technology (RIT). Developed with
* government support under contract 70RCSA22C00000008 awarded by the United
* States Department of Homeland Security for Cybersecurity and Infrastructure Security Agency.
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the “Software”), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
 */

package org.svip.generation;

import org.svip.generation.parsers.ParserManager;
import org.svip.generation.parsers.utils.VirtualPath;
import org.svip.sbom.model.objects.SVIPSBOM;
import org.svip.serializers.SerializerFactory;
import org.svip.serializers.serializer.Serializer;
import org.svip.utils.Debug;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.svip.utils.Debug.LOG_TYPE;
import static org.svip.utils.Debug.log;

/**
 * file: SBOMGeneratorCLI.java
 * Description: SBOM Generation CLI utility.
 *
 * @author Dylan Mulligan
 * @author Derek Garcia
 * @author Ian Dunn
 */
public class SBOMGeneratorCLI {

    //#region Exceptions

    /**
     * Custom Internal Error for handling arguments
     */
    private static class InvalidArgumentException extends Exception {

        /**
         * Create new Invalid Argument Exception
         *
         * @param msg Error message
         */
        public InvalidArgumentException(String msg) {
            super(msg);
        }
    }

    //#endregion

    //#region Attributes

    private static final int MAX_ALLOWED_ATTEMPTS = 5;                  // Max attempts for getting information
    private static final String PWD = System.getProperty("user.dir");   // System property for pwd
    private static final String OUT_DIRECTORY = "SBOMOut";               // Default Output directory

    /**
     * This is the usage text.
     */
    private static final String USAGES = """
            Usages:
                 java -jar jarfile targetPath:[componentName] [options]

            targetPath      : Required. Path to a target file or root directory to parse.
            [options]       : All optionals. Forms:  a flag or key=value .
              Flag form:
                  -d : Show additional debug information, overrides Summary when combined with -s.\s
                  -h : Display this usage information.
                  -s : Show Summary information, disabling ALL default messages.
              Key=value form:
                 -o=specification\s
                      Output specification. Select a supported format (CDX14, SPDX23) to output to,
                      Output specification defaults to CycloneDX if not specified.

                 -f=format\s
                      Output format. Select a supported format (JSON, XML, YAML) to output to,
                      Output specification defaults to JSON if not specified.

                Examples:
                   Display usages:  java -jar parser.jar -h\s
                   Basic: java -jar parser.jar MyProject/src
                   Debug: java -jar parser.jar MyProject/src -d
                   Summary: java -jar parser.jar MyProject/src -s
                   Debug (Overrides Summary): java -jar parser.jar MyProject/src -d -s
                   Debug, Output as JSON: java -jar parser.jar MyProject/src -o=json -d
                   CycloneDX:
                       - CycloneDX JSON:            java -jar parser.jar MyProject/src -d
                       - CycloneDX JSON (no debug):  java -jar parser.jar MyProject/src -o=CDX14 -f=JSON
                   SPDX:
                       - SPDX JSON:                 java -jar parser.jar MyProject/src -d -o=SPDX23
                       - SPDX Tag-Value (no debug):      java -jar parser.jar MyProject/src -o=SPDX23 -f=TAG_VALUE
            """;

    /**
     * This flag is set to true when -h option is present from the command line, otherwise false.
     */
    private static boolean showUsages = false;
    //#endregion

    //#region Core Methods

    /**
     * Attempts to validate all found arguments. It checks both
     * required and optional arguments, and throws an Exception
     * if any parameter cannot be validated.
     *
     * @param reqArgs an ArrayList of required arguments
     * @param optArgs a HashMap of optional arguments
     * @throws InvalidArgumentException if a required argument fails to validate
     * @throws FileNotFoundException    if a path to a valid file or directory is
     *                                  not given as the first required argument
     * @throws IllegalArgumentException if an optional argument fails to validate
     */
    private static void validateArgs(ArrayList<String> reqArgs, HashMap<String, String> optArgs) throws InvalidArgumentException, FileNotFoundException, IllegalArgumentException {
        // Validate Required Arguments

        // Check correct number of required args
        if (reqArgs.size() < 1)
            throw new InvalidArgumentException("Incorrect number of arguments; Expecting at least 1 but got 0.");

        String targetPath = reqArgs.get(0);

        // Validate Optional Arguments

        for (final Map.Entry<String, String> arg : optArgs.entrySet()) {
            final String key = arg.getKey();
            final String value = arg.getValue();
            // Make sure arg is at least 2 chars long and begins with '-'
            // Arg will be in the form of "-x"
            if (key.length() < 2) throw new IllegalArgumentException("Argument not in correct form: '" + arg + "'");

            // Grabs prefix char from string
            final char prefix = key.charAt(1);
            // Switch on char to parse correct optional argument
            // To add a new optional argument, simply add a new case (comma separated)
            // Either add it to the key/value args or the boolean args section
            // e.x. "case 'o', 'a', 'b', 'c', ... -> {...}
            switch (prefix) {

                // KEY/VALUE ARGUMENTS

                // Split into checks on all args and checks on specific args (inner switch)
                case 'o', 'f' -> {
                    // Do this for all valued args

                    // If both key and value are not present, throw exception
                    if (value.equals(""))
                        throw new IllegalArgumentException("Argument not in correct form: '-" + key + "=<value>'");
                }

                // BOOLEAN ARGUMENTS

                case 'd', 's', 'h' -> {
                } // No validation needed for boolean flags
                default -> throw new IllegalArgumentException("Argument '" + arg + "' is Unrecognized or Unsupported");
            }
        }

        // Check if target file/directory exists
        final boolean fileExists = Files.exists(Paths.get(targetPath));

        // Check if a file path is given and that file exists
        if (!fileExists || targetPath.equals(""))
            throw new FileNotFoundException("File '" + targetPath + "' does not exist");
    }


    /**
     * If required command line arguments fail, get user input. Program will
     * terminate if user enters nothing. Method modifies the given ArrayList
     * in-place with the updated values, given valid ones were collected.
     * <p>
     * InvalidArgumentException: Get Both Arguments
     * FileNotFoundException: Get Path Argument
     * IllegalArgumentException: Get Language argument
     *
     * @param e    Exception to determine what type of input is needed
     * @param args Existing arguments to use if needed
     */
    private static boolean getArgs(Exception e, ArrayList<String> args) {
        // Init scanner
        Scanner scanner = new Scanner(System.in);

        // Add minimum number of required slots for arguments
        while (args.size() < 1) {
            args.add("");
        }

        // Get new file path
        if (e instanceof InvalidArgumentException || e instanceof FileNotFoundException) {
            System.out.print("\nTarget Directory: ");
            // Remove double-quotes and replace back-slashes with forward-slashes
            args.set(0, scanner.nextLine()
                    .replace("\"", "")
                    .replace('\\', '/'));

            // Check empty input was given
            if (args.get(0).equals("")) {
                // If so, terminate program
                System.err.println("No Input Given; terminating. . .");
                return false;
            }
        }
        return true;
    }

    /**
     * Formats the given args array into "reqArgs" and "optArgs",
     * two collections of formatted arguments. "reqArgs" is an
     * ArrayList of values and "optArgs" is a HashMap of key:value.
     *
     * @param args an array of arguments to format
     * @return a map containing two entries: <br/>
     * "reqArgs": ArrayList <br/>
     * "optArgs": HashMap
     */
    private static Map<String, Object> formatArgs(String[] args) {
        final ArrayList<String> reqArgs = new ArrayList<>();
        final HashMap<String, String> optArgs = new HashMap<>();

        // Separate required and optional args
        for (final String arg : args) {
            // Log any
            if (arg.startsWith("-")) {
                // Split "-k=value?" -> "k" : "value" or "k" : "" if value is not present
                final int index = arg.lastIndexOf("=");
                // Key is arg[0, index]
                final String key = arg.substring(0, index == -1 ? arg.length() : index);
                // Value is arg[index, end] or empty string if either condition
                // If index > 0, "=" was found
                // If index + 1 < arg.length(), "=" is not the last character
                final String value = (index > 0 && index + 1 < arg.length()) ?
                        arg.substring(index + 1) : "";
                optArgs.put(key, value);
            } else reqArgs.add(arg);
        }

        // If path was found, replace "\" with "/"
        if (reqArgs.size() > 1)
            reqArgs.set(0, reqArgs.get(0).replace('\\', '/'));

        // Return formatted collections
        return new HashMap<>() {{
            put("reqArgs", reqArgs);
            put("optArgs", optArgs);
        }};
    }

    /**
     * Verifies the given arguments (and their values) are present and valid.
     * This method will attempt to reacquire invalid required arguments,
     * and will terminate the program if MAX_ALLOWED_ATTEMPTS is reached
     * before it can do so.
     *
     * @param reqArgs a list of required arguments
     * @param optArgs a map of optional arguments to their values
     */
    private static boolean verifyArgs(ArrayList<String> reqArgs, HashMap<String, String> optArgs) {
        // Attempt get valid arguments until exceed max attempts
        int attempt = 0;

        // Input loop
        for (; ; ) {
            // Break if exceed allow attempts
            if (attempt > MAX_ALLOWED_ATTEMPTS) {
                System.err.println("Exceeded Max Attempts; terminating. . .");
                return false;
            }

            // Try to validate the arguments
            try {
                validateArgs(reqArgs, optArgs);
                break;
            } // If parsing required args fails, re-acquire those args
            catch (InvalidArgumentException | FileNotFoundException e) {
                if (showUsages) {
                    displayUsages("");
                }
                // Print error message and get new arguments
                System.err.println("Error: " + e.getMessage());
                System.err.println("Arguments: <path/to/target>");
                return getArgs(e, reqArgs);
            } // If parsing optional args fails, inform user and end program
            catch (IllegalArgumentException e) {
                System.err.println("Error: " + e.getMessage());
                return false;
            } finally {
                attempt++;  // Increment attempt count
            }
        }
        return true;
    }

    /**
     * Display the usages of this program.
     *
     * @param msg Custom text in addition to the {@code USAGES} for a developer to show where this method is invoked.
     *            Please pass empty string "" after done development.
     */
    private static void displayUsages(String msg) {
        System.out.println(msg + "\n" + USAGES);
    }

    //#endregion

    /**
     * Main Driver for CodeParser and expects 1 required argument
     * and accepts any number of valid optional arguments.
     * <p>
     * reqArgs[0]: Path to Parsing Target
     * optArgs[...]: -d, -s, -o, -a, -f, -h
     *
     * @param args command line arguments
     * @throws NullPointerException if the creation of the parser fails
     */
    public static void main(String[] args) throws NullPointerException {
        // Header printout
        System.out.print("BenchmarkData Parsers: ");
        System.out.println("Current Working Directory: " + PWD);

        // Format args into separate collections
        final Map<String, Object> formattedArgs = formatArgs(args);

        // Initialize required and optional args lists
        final ArrayList<String> reqArgs = (ArrayList<String>) formattedArgs.get("reqArgs");
        final HashMap<String, String> optArgs = (HashMap<String, String>) formattedArgs.get("optArgs");

        // Set flag for Help prompt
        showUsages = optArgs.containsKey("-h");

        // TODO: Rework this, just low priority
        //  I'm thinking of unifying the arg lists to one HashMap<String, Object>
        //  Each value (Object) can be verified and possibly modified (e.x. string -> enum)
        //  This would mean each value has a key and is either ready for use or null.
        // Verify args
        if (!verifyArgs(reqArgs, optArgs)) {
            // If verification fails, exit program
            return;
        }

        // Show Usages if -h is on the cli and exit program
        if (showUsages) {
            displayUsages("");
            return;
        }

        // Enable summary run if indicated
        if (optArgs.containsKey("-s")) Debug.enableSummary();
        // Enable debug console logging if indicated
        if (optArgs.containsKey("-d")) Debug.enableDebug();

        // Instantiate controller with the filepath
        VirtualPath sourcePath = new VirtualPath(reqArgs.get(0));
        final ParserManager controller = new ParserManager(sourcePath.getFileName().toString(), buildFileMap(sourcePath));
        controller.parseAll(); // Parse all files

        // Build outPath
        String outPath = PWD;

        // Language specific slash
        final String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) outPath += '\\';
        if (os.contains("mac") || os.contains("nix") || os.contains("nux") || os.contains("aix")) outPath += '/';

        outPath += OUT_DIRECTORY;
//        outPath = null; // UNCOMMENT FOR TESTING ONLY, THIS DOES NOT GENERATE SBOM FILES, ONLY STRINGS

        // Get schema from optional args, if not present, default to CycloneDX
        SerializerFactory.Schema schema = SerializerFactory.Schema.CDX14;
        if (optArgs.containsKey("-o")) {
            try {
                schema = SerializerFactory.Schema.valueOf(optArgs.get("-o").toUpperCase());
            } catch (IllegalArgumentException ignored) {
                log(LOG_TYPE.WARN, String.format(
                        "Invalid schema type provided: '%s', defaulting to '%s'",
                        optArgs.get("-o").toUpperCase(),
                        schema
                ));
            }
        }

        // Get format from optional args, if not present, default to JSON
        SerializerFactory.Format format = SerializerFactory.Format.JSON;
        if (optArgs.containsKey("-f")) {
            try {
                format = SerializerFactory.Format.valueOf(optArgs.get("-f").toUpperCase());
            } catch (IllegalArgumentException ignored) {
                log(LOG_TYPE.WARN, String.format(
                        "Invalid format type provided: '%s', defaulting to '%s'",
                        optArgs.get("-f").toUpperCase(),
                        format
                ));
            }
        }

        try {
            // Write to file
            Serializer s = SerializerFactory.createSerializer(schema, format, true);
            SVIPSBOM sbom = controller.buildSBOM(schema);

            Debug.log(LOG_TYPE.SUMMARY, "Serializing " + schema + " SBOM to " + format);
            String serialized = s.writeToString(sbom);

            String sbomFile = outPath + "/" + sbom.getName() + "_" + schema + "." + format.toString().toLowerCase();
            Debug.log(LOG_TYPE.DEBUG, "Attempting to write SBOM to file " + sbomFile);
            Files.createDirectories(Path.of(outPath));
            PrintWriter out = new PrintWriter(sbomFile);
            out.println(serialized);
            out.close();
            Debug.log(LOG_TYPE.SUMMARY, "SUCCESSFULLY WRITTEN " + schema + " " + format + " SBOM to path: " + sbomFile);
        } catch (IOException e) {
            log(Debug.LOG_TYPE.EXCEPTION, e);
            log(Debug.LOG_TYPE.ERROR, "Error writing to file " + outPath);
        }
    }

    /**
     * Builds a VirtualTree from an existing file tree located in the user's filesystem given a source path. This loops
     * through all files and directories using Files.walk() over the source path, and adds each file with its contents
     * to the VirtualTree.
     *
     * @param src The source path to build the file tree representation from.
     * @return A new VirtualTree that represents all files and directories in the source path.
     */
    public static Map<VirtualPath, String> buildFileMap(VirtualPath src) {
        Map<VirtualPath, String> fileMap = new HashMap<>();
        final long buildStart = System.currentTimeMillis();

        AtomicInteger directoryCounter = new AtomicInteger();

        // Build the tree by finding each file and adding to the virtual tree
        try (Stream<Path> stream = Files.walk(src.getPath())) {
            stream.forEach(filepath -> {
                // Only add the directory + files of the path if the file is found - no empty directories
                if (!Files.isDirectory(filepath)) {
                    try {
                        fileMap.put(new VirtualPath(filepath), Files.readString(filepath));
                    } catch (IOException e) {
                        Debug.log(Debug.LOG_TYPE.WARN, "Unable to read file contents of: " + filepath);
                    }
                } else directoryCounter.getAndIncrement();
            });
        } catch (Exception e) {
            Debug.log(Debug.LOG_TYPE.ERROR, "Unable to access file");
            Debug.log(Debug.LOG_TYPE.EXCEPTION, e.getMessage());
        }

        // Report stats
        log(Debug.LOG_TYPE.SUMMARY, String.format("VirtualTree construction complete. " +
                        "Found %s Directories and %s Files in %.2f seconds",
                directoryCounter,
                fileMap.size(),
                (float) (System.currentTimeMillis() - buildStart) / 1000));

        return fileMap;
    }
}
