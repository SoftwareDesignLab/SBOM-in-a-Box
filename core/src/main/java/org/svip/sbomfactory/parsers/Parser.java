package org.svip.sbomfactory.parsers;

import org.svip.builders.component.SVIPComponentBuilder;
import org.svip.utils.VirtualPath;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Set;

import static org.svip.utils.Debug.LOG_TYPE;
import static org.svip.utils.Debug.log;

/**
 * <b>File</b>: Parser.java<br>
 * <b>Description</b>: Abstract core Class for file parsers.
 * This handles all general parsing logic and defines the required
 * methods to be implemented for child Parser instances.
 *
 * @author Dylan Mulligan
 * @author Derek Garcia
 * @author Ian Dunn
 */
public abstract class Parser {

    //#region Attributes

    /**
     * The maximum connection timeout of each URL query (in milliseconds).
     */
    private static final int MAX_CONNECTION_TIMEOUT = 1000;

    /**
     * The VirtualPath to the target directory of the Parser.
     */
    protected VirtualPath PWD;

    /**
     * A list of file paths from the set VirtualTree. Setting a VirtualTree enables the parser to check for internal
     * components.
     */

    protected Set<VirtualPath> sourceFiles;

    /**
     * The standard library URL of the parser.
     */
    protected final String STD_LIB_URL;

    //#endregion

    //#region Constructors

    /**
     * Protected Constructor meant for use by language implementations
     * to store their language-specific static values in their respective
     * attributes.
     *
     * @param STD_LIB_URL a URL to the Standard Language Library of the language
     *                    parser
     */
    protected Parser(String STD_LIB_URL) {
        this.STD_LIB_URL = STD_LIB_URL;
    }

    //#endregion

    //#region Setters

    /**
     * Sets the current working directory of the parser.
     *
     * @param PWD The current working directory of the parser.
     */
    public void setPWD(VirtualPath PWD) { this.PWD = PWD; }

    /**
     * Sets the current internal files list of the parser, enabling the parser to check for internal components. The
     * list of VirtualNodes is converted to a list of all full file paths.
     *
     * @param internalFiles The current internal VirtualTree of the parser.
     */
    public void setSourceFiles(Set<VirtualPath> internalFiles) {
        this.sourceFiles = internalFiles;
    }

    //#endregion

    //#region Static Methods

    /**
     * Queries a given URL and returns the HttpURLConnection
     * object containing response data.
     *
     * @param urlString URL to be queried
     * @param allowRedirects whether or not to allow the connection to redirect
     * @return HttpURLConnection object
     * @throws IOException if an exception is thrown while
     *                     attempting to connect to the URL
     */
    protected static HttpURLConnection queryURL(String urlString, boolean allowRedirects) throws IOException {
        try {
            final long t1 = System.nanoTime();

            // Create and open connection object
            final URL url = new URL(urlString);
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Init request details
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(allowRedirects);
            connection.setConnectTimeout(MAX_CONNECTION_TIMEOUT);

            // Attempt to connect
            log(LOG_TYPE.DEBUG, "Attempting to connect to URL: " + url);
            connection.connect(); // This pings the server
            connection.getResponseCode(); // This waits for the response, can take a long time

            // Log connection stats
            final long t2 = System.nanoTime();
            log(LOG_TYPE.DEBUG, String.format(
                    "Connection to URL: %s successful. Done in %s ms",
                    urlString,
                    (int)((t2 - t1) / 1000000)
            ));

            // Return connection (with response data)
            return connection;
        }
        // Only thrown if connection times out
        catch (SocketTimeoutException ignored) {
            throw new SocketTimeoutException("Connection timed out...");
        }
    }

    //#endregion

    //#region Abstract Methods

    /**
     * Parses given fileContents through language specific regex and appends the found components to the provided
     * ArrayList.
     *
     * @param components A list of ParserComponents that the found components will be appended to.
     * @param fileContents file contents to be parsed
     */
    public abstract void parse(ArrayList<SVIPComponentBuilder> components, String fileContents);

    //#endregion
}
