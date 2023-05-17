package org.svip.sbomfactory.generators.parsers.packagemanagers;

import org.svip.sbom.model.CPE;
import org.svip.sbom.model.PURL;
import org.svip.sbomfactory.generators.utils.Debug;
import org.svip.sbomfactory.generators.utils.QueryWorker;
import org.svip.sbomfactory.generators.parsers.Parser;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.svip.sbomfactory.generators.utils.ParserComponent;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
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
    protected HashMap<String, LinkedHashMap<String, String>> dependencies;

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
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void resolveProperties(HashMap resolvedMap, HashMap<String, String> rawMap) {
        // Iterate over keys
        rawMap.keySet().forEach(key -> {
            // Resolve property
            resolvedMap.put(key, this.resolveProperty(rawMap.get(key), rawMap));
        });
    }

    // TODO: Docstring
    @SuppressWarnings({"unchecked"})
    protected Object resolveProperty(Object value, HashMap<String, String> props) {
        // Ingore value if null
        if(value == null) return value;

        // If value contains multiple values (is a map), store it as such
        if(value instanceof HashMap) {
            // Get map of properties to resolve
            final HashMap<String, String> rawProperties = (HashMap<String, String>) value;

            // Resolve properties
            return resolveMap(rawProperties, props);
        }
        // Otherwise, value is a string
        else {
            // Cast value to string
            final String valueString = (String) value;

            // If value is empty, return null
            if (valueString.isBlank()) return null;

            // Otherwise, resolve string
            return resolveString(valueString, props);
        }
    }

    private HashMap<String, String> resolveMap(HashMap map, HashMap<String, String> props) {
        // Init resolved map
        final HashMap<String, String> resolvedMap = new HashMap<>(map.size());

        // Iterate over unresolved map and store resolved values
        map.forEach(
                (k, v) -> {
                    if(v instanceof String) {
                        final String keyString = (String) k;
                        final String valueString = (String) v;
                        resolvedMap.put(keyString, this.resolveString(valueString, props));
                    } else log(LOG_TYPE.WARN, String.format("Could not resolve illegal value of type: %s (Expected type: String)", v.getClass().getSimpleName()));
                });

        // Return resolved map
        return resolvedMap;
    }

    private String resolveString(String string, HashMap<String, String> props) {
        // Get results
        final List<MatchResult> results =  this.TOKEN_PATTERN.matcher(string).results().toList();

        // Init resolved value to raw value
        String resolvedValue = string;

        // Iterate over match results
        for (MatchResult result : results) {
            final String varKey = result.group(1).trim();
            // Get value from this.properties
            String varValue = this.properties.get(varKey);

            // If no corresponding value, get value from props
            if(varValue == null || this.TOKEN_PATTERN.matcher(varValue).find()) {
                varValue = props.get(varKey);
                // If value is null, this property cannot be resolved, store original value
                if(varValue == null)  resolvedValue = string;
                    // Otherwise, recurse resolution
                else {
                    resolvedValue = string.substring(0, result.start()) + varValue + string.substring(result.end());
                    resolveString(resolvedValue, props);
                }
            }
            // Otherwise, this property can be resolved
            else resolvedValue = string.substring(0, result.start()) + varValue + string.substring(result.end());
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

    /**
     * Builds URLs and instantiates ParserComponent objects
     *
     * @param components the ParserComponent array to fill
     * @param parser the package-manager parser
     * @param packageManager the package manager
     */
    public static void buildURLs(ArrayList<ParserComponent> components, PackageManagerParser parser, String packageManager) {
        // Iterate and build URLs

        boolean nugetParser = packageManager.equals("nuget");

        for (String id : parser.dependencies.keySet()) { //todo this shares a lot of code with POMParser. Maybe make static method
            // Get value from map
            final HashMap<String, String> d = parser.dependencies.get(id);

            // Format all property keys -> values
            String licenseRegex = "<li data-test=\\\"license\\\">(.*?)</li>"; // Regex101: https://regex101.com/r/FUOPSK/1

            // Refactor variables if Nuget parser
            parserConfig result = getParserConfig(nugetParser, d, licenseRegex);

            String version = d.get("version");

            final ParserComponent c = new ParserComponent(id);

            //framework assemblies use assemblyName + targetFramework
            if (result.groupId() != null) c.setGroup(result.groupId());
            if (version != null) c.setVersion(version);

            // TODO: Find this PURL regex a home (Translator?): https://regex101.com/r/sbFd7Z/2
            //  "^pkg:([^/]+)/([^#\n@]+)(?:@([^\?\n]+))?(?:\?([^#\n]+))?(?:#([^\n]+))?"

            // is this a .NET assembly
            boolean frameworkAssembly = //todo ensure these are the only cases
                    nugetParser && id != null && (id.toLowerCase().contains("system") || id.toLowerCase().contains("microsoft"));

            // Build PURL String
            final HashMap<String, String> PURLData = new HashMap<>();
            PURLData.put("type", packageManager);
            PURLData.put("name", id);
            if (result.groupId() != null) PURLData.put("namespace", result.groupId());
            if (version != null) PURLData.put("version", version);

            if (frameworkAssembly) {
                c.setPublisher("Microsoft");
                id = d.get("assemblyName");
                c.setType(ParserComponent.Type.LANGUAGE);
            }
            String PURLString = PackageManagerParser.buildPURL(PURLData);

            // Add built PURL
            c.addPURL(new PURL(PURLString));
            log(Debug.LOG_TYPE.DEBUG, String.format("Dependency Found with PURL: %s", PURLString));

            // Build CPE
            CPE cpe = new CPE(packageManager, id, version);
            String cpeFormatString = cpe.bindToFS();
            c.addCPE(cpeFormatString);
            log(Debug.LOG_TYPE.DEBUG, String.format("Dependency Found with CPE: %s", cpeFormatString));

            // Build URL and worker object
            if (result.groupId() != null) {
                String url = parser.STD_LIB_URL;
                if(!nugetParser)
                    url += result.groupId();
                url += "/" + id;
                if (version != null) url += "/" + version;
                // Create and add QueryWorker with Component reference and URL
                String finalLicenseRegex = result.licenseRegex();
                parser.queryWorkers.add(new QueryWorker(c, url) {
                    @Override
                    public void run() {
                        // Get page contents
                        final String contents = getUrlContents(queryURL(this.url, false));

                        // Parse license(s)
                        final Matcher m = Pattern.compile(finalLicenseRegex,
                                Pattern.MULTILINE).matcher(contents); //todo look into this more for nuget

                        // Add all found licenses
                        while (m.find()) {
                            this.component.addLicense(m.group(1).trim());
                        }
                    }
                });
            }

            // Add ParserComponent to components
            components.add(c);
        }
    }

    /**
     * Configures the buildURL method for either parser
     * @param nugetParser whether this is NugetParser
     * @param d data
     * @param licenseRegex license regex for parsing
     * @return this variable configuration
     */
    private static parserConfig getParserConfig(boolean nugetParser, HashMap<String, String> d, String licenseRegex) {
        String groupId;
        if(nugetParser){
            licenseRegex = ">(.*?)</a>( *)license"; // Regex101: https://regex101.com/r/tskCMf/1
            groupId = d.get("id");
            if(groupId == null)
                groupId = d.get("targetFramework").split("[.]")[0]; // framework assembly name //todo this messes up query. fine for the sake of PURLs
        }
        else
            groupId = d.get("groupId");
        return new parserConfig(groupId, licenseRegex);
    }

    private record parserConfig(String groupId, String licenseRegex) {
    }

    //#endregion
}
