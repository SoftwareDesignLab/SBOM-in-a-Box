package org.svip.generators.utils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * <b>File</b>: Debug.java<br>
 * <b>Description</b>: Static class that provides Debugging utilities,
 * mainly the logging system used for most console output.
 *
 * @author Dylan Mulligan
 * @author Derek Garcia
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
    private static boolean debugMode = false; // Boolean toggle to allow debug logs
    private static boolean summaryMode = false; // Boolean toggle to suppress all but SUMMARY logs

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
                    // TODO: Test this? debugMode was marked as broken and commented out, but it appears functional
                    //  This should expand exceptions logged to include the root of their stack trace (when debug mode
                    //  is enabled)
                    if(Debug.debugMode && e.getStackTrace() != null)
                        out += " | " + e.getStackTrace()[0];
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
