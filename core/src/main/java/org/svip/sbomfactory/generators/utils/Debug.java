package org.svip.sbomfactory.generators.utils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

/**
 * <b>File</b>: Debug.java<br>
 * <b>Description</b>: Static class that provides Debugging utilities,
 * mainly the logging system used for most console output.
 *
 * @author Dylan Mulligan
 * @author Derek Garcia
 * @author Ian Dunn
 */
public class Debug {
    /**
     * Enums for classifying log types
     * <p>
     * <b>Info</b> : Informational logs that contain parsing data<br/>
     * <b>Summary</b> : Summative informational logs that contain broad parsing data<br/>
     * <b>Debug</b> : Debug logs that contain deeper parsing data<br/>
     * <b>Warn</b> : Warning logs that indicate possibly unsafe or erroneous operations<br/>
     * <b>Error</b>  : Error logs that display failures that have occurred during parsing<br/>
     * <b>Exception</b> : Exception logs that display caught exceptions that have occurred during parsing<br/>
     */
    public enum LOG_TYPE {
        INFO("INFO "),
        SUMMARY("SUMRY"),
        DEBUG("DEBUG"),
        WARN("WARN "),
        ERROR("ERROR"),
        EXCEPTION("EXCPT");

        private final String shortName;

        LOG_TYPE(String shortName) {
            this.shortName = shortName;
        }

        public String shortName() {
            return shortName;
        }
    }
    private static final String blockChar = "="; // Block character to use
    private static final int defaultBlockLength = 50;
    private static boolean debugMode = false; // Boolean toggle to allow debug logs
    private static boolean summaryMode = false; // Boolean toggle to suppress all but SUMMARY logs
    private static int blockLength = -1; // The size of the open block to match when closing

    /**
     * Internal Logging system for printing information
     *
     * @param logType Log type enum
     * @param msg     String or Exception depending on logType
     */
    public static void log(LOG_TYPE logType, Object msg) {

        // If in summary mode, only print SUMMARY and ERROR logs
        if (Debug.summaryMode && (logType != LOG_TYPE.SUMMARY && logType != LOG_TYPE.ERROR))
            return;

        // If debugMode is off, don't print debug logs
        if (!Debug.debugMode && logType == LOG_TYPE.DEBUG)
            return;

        // Format time and header
        final String time = Instant.now()
                .truncatedTo(ChronoUnit.MILLIS)
                .toString()
                .replace("T", " ")
                .replace("Z", "");
        final String header = time + " | " + logType.shortName() + " | ";

        // Internal color codes
        final String CLEAR = "\033[0m";
        final String YELLOW = "\033[0;33m";
        final String CYAN = "\033[0;36m";

        // Try catch in case logType and msg mismatch
        try {
            switch (logType) {
                // Print INFO, DEBUG, and WARN Message to stdout
                case INFO, SUMMARY -> System.out.println(header + msg.toString());
                case DEBUG -> System.out.println(CYAN + header + msg.toString() + CLEAR);
                case WARN -> System.out.println(YELLOW + header + msg.toString() + CLEAR);
                // Format Exception
                case EXCEPTION -> {
                    // convert msg to exception and get name
                    Exception e = (Exception) msg;
                    String out = header + e.getClass().getSimpleName();

                    // Append msg if one exists
                    if (e.getMessage() != null)
                        out += " | " + e.getMessage();
                    // Append root of stack trace if one exists
                    if(Debug.debugMode) {
                        final StackTraceElement[] stackTrace = e.getStackTrace();
                        final String[] shortStack = Arrays.stream(Arrays.copyOfRange(stackTrace, 0, 5)).map(StackTraceElement::toString).toArray(String[]::new);
                        if(shortStack.length > 0)
                            out += " | " + String.join("\n", shortStack);
                    }
                    System.err.println(out);
                }
                // Print ERROR to stderr
                case ERROR -> System.err.println(header + msg.toString());
                // Print unknown type error
                default -> System.err.println(header + " | UNKNOWN LOG TYPE");
            }
        } catch (Exception e) {
            System.err.println(header + " | LOGGING ERROR");
        }
    }

    /**
     * Log the beginning or end of a log block. A beginning log block will be preceded by a newline; an ending log
     * block will be concluded by a newline. An ending log block will match the length of its preceding log block.
     */
    public static void logBlock() {
        if(blockLength != -1) {
            System.out.println(blockChar.repeat(blockLength) + "\n");
            blockLength = -1;
        } else {
            System.out.println("\n" + blockChar.repeat(defaultBlockLength));
            blockLength = defaultBlockLength;
        }
    }

    /**
     * Log a beginning log block with a title (remaining the default length). The title will be centered in the log
     * block and preceded by a newline.
     *
     * @param title The title of the log block.
     */
    public static void logBlockTitle(String title) {
        if(title == null || title.equals("")) {
            logBlock();
            return;
        }

        String formattedTitle = "( " + title + " )";
        String block = blockChar.repeat((defaultBlockLength - formattedTitle.length()) / 2);
        String logBlock = block + formattedTitle + block;

        // Test for uneven blocks
        if(logBlock.length() == defaultBlockLength - 1) logBlock += blockChar;
        if(logBlock.length() == defaultBlockLength + 1) logBlock = logBlock.substring(0, logBlock.length() - 1);

        System.out.println("\n" + logBlock);
        blockLength = logBlock.length();
    }

    /**
     * Enables summary mode if debug mode is off
     */
    public static void enableSummary() {
        if (Debug.debugMode) {
            log(LOG_TYPE.ERROR, "Debug Mode is Enabled; Overrides Summary Flag");
        } else {
            Debug.summaryMode = true;
        }
    }

    /**
     * Enables debug mode; Overrules summary mode if enabled
     */
    public static void enableDebug() {
        Debug.debugMode = true;
        if (Debug.summaryMode) {
            log(LOG_TYPE.ERROR, "Debug Mode Overrides Summary Flag");
            Debug.summaryMode = false;
        }
        log(LOG_TYPE.DEBUG, "Debug Mode is Enabled");
    }
}
