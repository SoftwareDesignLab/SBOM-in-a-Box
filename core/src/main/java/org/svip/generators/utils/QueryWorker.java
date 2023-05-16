package org.svip.generators.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.svip.generators.utils.Debug.log;

/**
 * File: QueryWorker.java
 * <p>
 * Abstract worker class that queries a URL and parses information to add to a ParserComponent.
 * </p>
 * @author Dylan Mulligan
 */
public abstract class QueryWorker implements Runnable {
    protected final ParserComponent component; // Component to be appended to
    protected final String url; // URL to be queried

    /**
     * Creates a new object with the url to be queried and the Component to store any collected information in.
     *
     * @param component the Component to store any collected information in
     * @param url the url to be queried
     */
    protected QueryWorker(ParserComponent component, String url) {
        this.component = component;
        this.url = url;
    }

    /**
     * Gets the URL that this QueryWorker instance is parsing from.
     *
     * @return A string web URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * Queries a given URL and returns the HttpURLConnection
     * object containing response data.
     *
     * @param urlString URL to be queried
     * @param allowRedirects whether or not to allow the connection to redirect
     * @return a HttpURLConnection object
     */
    protected static HttpURLConnection queryURL(String urlString, boolean allowRedirects) {
        // Init connection variable
        HttpURLConnection connection = null;

        try {
            final long t1 = System.nanoTime();

            // Create and open connection object
            final URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();

            // Init request details
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(allowRedirects);
            connection.setConnectTimeout(1000);

            // Attempt to connect
            log(Debug.LOG_TYPE.DEBUG, "Attempting to connect to URL: " + url);
            connection.connect(); // This pings the server
            connection.getResponseCode(); // This waits for the response, can take a long time

            // Log connection stats
            final long t2 = System.nanoTime();
            log(Debug.LOG_TYPE.DEBUG, String.format(
                    "Connection to URL: %s successful. Done in %s ms",
                    urlString,
                    (int)((t2 - t1) / 1000000)
            ));

        }
        catch (IOException ignored) {
            log(Debug.LOG_TYPE.WARN, String.format("Failed to query URL: '%s'", urlString));
        }

        // Return connection (with response data)
        return connection;
    }

    /**
     * Given a pre-queried connection object, collect contents of url
     * result file in a String and return it.
     *
     * @param urlConnection a pre-queried connection object
     * @return the contents of the file at the specified url
     */
    protected static String getUrlContents(HttpURLConnection urlConnection) {
        // Init content StringBuilder
        StringBuilder content = new StringBuilder();

        // Attempt to get url contents
        try {
            // Create a reader object with the connection input stream
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            // Read all lines from input stream
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line).append("\n");
            }

            // Close reader
            bufferedReader.close();
        } catch (IOException ignored) {
            log(Debug.LOG_TYPE.WARN, String.format("Failed to get contents of URL: '%s'", urlConnection.getURL().toString()));
        }

        // Return built content string
        return content.toString();
    }

    /**
     * Queries this.url and retrieves the page contents for parsing. Contents are then parsed and
     * any information that can be collected is, and is added to this.component.
     */
    @Override
    public abstract void run();
}
