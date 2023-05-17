package org.svip.sbomfactory.generators.parsers.packagemanagers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.svip.sbomfactory.generators.parsers.Parser;
import org.svip.sbomfactory.generators.utils.ParserComponent;
import org.svip.sbomfactory.generators.utils.QueryWorker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import static org.svip.sbomfactory.generators.utils.Debug.LOG_TYPE;
import static org.svip.sbomfactory.generators.utils.Debug.log;

/**
 * <b>File</b>: PackageManagerParser.java<br>
 * <b>Description</b>: Abstract core Class for generated dependency file parsers.
 * This handles all general parsing logic and defines the required
 * methods to be implemented for child PackageManagerParser instances.
 *
 * @author Dylan Mulligan
 */
public abstract class PackageManagerParser extends Parser {

    //#region Attributes

    protected static final String CPE_URL = "https://services.nvd.nist.gov/rest/json/cpes/2.0?cpeMatchString=";

    protected final ObjectMapper OM;

    protected final ArrayList<QueryWorker> queryWorkers;
    protected HashMap<String, String> properties;
    protected ArrayList<LinkedHashMap<String, String>> dependencies;

    protected final Pattern TOKEN_PATTERN;

    //#endregion

    //#region Constructors

    /**
     * Protected Constructor meant for use by parser implementations
     * to store their package-manager-specific static values in their respective
     * attributes.
     *
     * @param REPO_URL a URL to the repository of the package manager packages
     */
    protected PackageManagerParser(String REPO_URL, JsonFactory factory, String tokenPattern) {
        super(REPO_URL);
        this.queryWorkers = new ArrayList<>();
        // Safely init ObjectMapper instance with the given factory
        // If factory is null, OM is assumed to not be used and is also set to null
        if(factory == null) this.OM = null;
        else {
            // ObjectMapper Initialization & Configuration
            this.OM = new ObjectMapper(factory);
            this.OM.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        }
        this.TOKEN_PATTERN = Pattern.compile(tokenPattern, Pattern.MULTILINE);
    }

    //#endregion

    //#region Static Methods

    /**
     * Build a PURL string using the provided HashMap. The HashMap should contain the following keys with corresponding
     * values:
     * <ul>
     *     <li><code>type</code> (required)</li>
     *     <li><code>namespace</code></li>
     *     <li><code>name</code> (required)</li>
     *     <li><code>version</code></li>
     *     <li><code>qualifiers</code></li>
     *     <li><code>subpath</code></li>
     * </ul>
     *
     * @param data A HashMap with at least the required keys above.
     * @return A complete PURL string formatted to include the provided data.
     * @throws IllegalArgumentException If the data HashMap does not include required components to build a String, this
     * exception will be thrown.
     */
    protected static String buildPURL(HashMap<String, String> data) throws IllegalArgumentException {
        String type = "";
        String namespace = "";
        String name = "";
        String version = "";
        String qualifiers = "";
        String subpath = "";

        if(data.containsKey("type")) type = data.get("type");
        else throw new IllegalArgumentException("Cannot build PURL without required component 'type'");

        if(data.containsKey("namespace")) namespace = "/" + data.get("namespace");

        if(data.containsKey("name")) name = "/" + data.get("name");
        else throw new IllegalArgumentException("Cannot build PURL without required component 'name'");

        if(data.containsKey("version")) version = "@" + data.get("version");

        if(data.containsKey("qualifiers")) qualifiers = "?" + data.get("qualifiers");

        if(data.containsKey("subpath")) subpath = "#" + data.get("subpath");

        // Build and return with safe data
        return String.format("pkg:%s%s%s%s%s%s",
                type, // Type
                namespace, // Namespace
                name, // Name
                version, // Version
                qualifiers, // Qualifiers
                subpath  // Subpath
        );
    }

    /**
     * Executes the tasks of each member of the given list of workers.
     *
     * @param workers list of workers with tasks to be executed
     */
    protected static void queryURLs(List<QueryWorker> workers) {
        // Ensure worker list is not null
        if(workers == null) return;

        // If list has 1 or more elements, construct query pool and start workers
        if(workers.size() > 0) {
            // Init ExecutorService with new thread pool
            final ExecutorService es = Executors.newCachedThreadPool();

            // Start each worker
            for (QueryWorker qw: workers) {
                es.execute(qw);
            }

            // Teardown ExecutorService
            es.shutdown();

            // Wait for all tasks to complete, max timeout 1min
            try { es.awaitTermination(1, TimeUnit.MINUTES); }
            // Catch and log any errors thrown during thread pooling
            catch (InterruptedException ignored) {
                log(LOG_TYPE.WARN, "Error occurred during thread pooling.");
            }
        }

        // Log operation completion and query count
        log(LOG_TYPE.DEBUG, String.format("Querying done, %s queries executed", workers.size()));

        // Clear list of workers, as their tasks have been completed
        workers.clear();
    }

    //#endregion

    //#region Core Methods

    /**
     * Parses a given set of raw data into Components and adds them to the given list
     * of Components. This method is abstract and should be implemented to parse each specific
     * dependency file differently, as needed.
     *
     * @param components list of Components to add to
     * @param data map of data to be parsed
     */
    protected abstract void parseData(ArrayList<ParserComponent> components, HashMap<String, Object> data);

    // TODO: Docstring
    protected void resolveProperties(HashMap<String, String> props) {
        // Iterate over keys
        props.keySet().forEach(key -> {
            // Resolve property
            this.properties.put(key, resolveProperty(props.get(key), props));
        });
    }

    // TODO: Docstring
    protected String resolveProperty(String value, HashMap<String, String> props) {
        // Ingore value if null or blank
        if(value == null || value.isBlank()) return value;

        // Get results
        final List<MatchResult> results =  this.TOKEN_PATTERN.matcher(value).results().toList();

        // Init resolved value to raw value
        String resolvedValue = value;

        // Iterate over match results
        for (MatchResult result : results) {
            final String varKey = result.group(1).trim();
            // Get value from this.properties
            String varValue = this.properties.get(varKey);

            // If no corresponding value, get value from props
            if(varValue == null || this.TOKEN_PATTERN.matcher(varValue).find()) {
                varValue = props.get(varKey);
                // If value is null, this property cannot be resolved, store original value
                if(varValue == null)  resolvedValue = value;
                    // Otherwise, recurse resolution
                else {
                    resolvedValue = value.substring(0, result.start()) + varValue + value.substring(result.end());
                    resolveProperty(resolvedValue, props);
                }
            }
            // Otherwise, this property can be resolved
            else resolvedValue = value.substring(0, result.start()) + varValue + value.substring(result.end());
        }


        // Return resolved String value
        return resolvedValue;
    }

    /**
     * Parses a package manager dependency file and stores found Components
     * in the given list of Components.
     *
     * @param components list of Components to add to
     * @param fileContents the contents of the file to be parsed
     */
    @Override
    public void parse(ArrayList<ParserComponent> components, String fileContents) {
        try {
            final HashMap<String, Object> data = this.OM.readValue(fileContents, HashMap.class);
            this.parseData(components, data);
        } catch (IOException e) { log(LOG_TYPE.EXCEPTION, e); }
    }

    //#endregion
}
