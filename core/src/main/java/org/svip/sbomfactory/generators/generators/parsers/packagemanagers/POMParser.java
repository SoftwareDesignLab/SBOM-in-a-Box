package org.svip.sbomfactory.generators.generators.parsers.packagemanagers;

import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import org.svip.sbomfactory.generators.generators.utils.Debug;
import org.svip.sbomfactory.generators.generators.utils.ParserComponent;
import org.svip.sbomfactory.generators.generators.utils.QueryWorker;

import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * file: POMParser.java
 * Description: Package-manager specific implementation of the PackageManagerParser (Maven/pom.xml)
 *
 * @author Dylan Mulligan
 */
public class POMParser extends PackageManagerParser {
    //#region Attributes

    // "properties" field of a POM file
    protected LinkedHashMap<String, String> properties;

    //#endregion

    //#region Constructors

    public POMParser() { super("https://central.sonatype.com/artifact/", new XmlFactory()); }

    //#endregion

    //#region Core Methods

    @Override
    protected void parseData(ArrayList<ParserComponent> components, HashMap<String, Object> data) {
        // Get properties from data
        this.properties = (LinkedHashMap<String, String>) data.get("properties");

        // Resolve nested properties (e.x. "<maven.compiler.source>${java.version}</maven.compiler.source>")
//        this.resolveProperties(); // TODO: Finish recursive resolution

        // Get dependencies from data
        final ArrayList<LinkedHashMap<String, String>> deps =
                ((LinkedHashMap<String, ArrayList<LinkedHashMap<String, String>>>) data.get("dependencies")).get("dependency");

        final LinkedHashMap build = (LinkedHashMap) data.get("build");

        // Get plugins from data
        final ArrayList<LinkedHashMap<String, String>> plugins =
                ((LinkedHashMap<String, ArrayList<LinkedHashMap<String, String>>>) build.get("plugins")).get("plugin");

        // Iterate and build URLs
        for (LinkedHashMap<String, String> d : deps) {
            // Format all property keys -> values
            final String groupId = this.formatVariableNames(d.get("groupId"));
            final String artifactId = this.formatVariableNames(d.get("artifactId"));
            final String version = this.formatVariableNames(d.get("version"));

            // Skip dependency if no name
            if(artifactId == null) continue;

            final ParserComponent c = new ParserComponent(artifactId);
            if(groupId != null) c.setGroup(groupId);
            if(version != null) c.setVersion(version);


            // TODO: Find this PURL regex a home (Translator?): https://regex101.com/r/sbFd7Z/2
            //  "^pkg:([^/]+)/([^#\n@]+)(?:@([^\?\n]+))?(?:\?([^#\n]+))?(?:#([^\n]+))?"


            // Build PURL String
            final HashMap<String, String> PURLData = new HashMap<>();
            PURLData.put("type", "maven");
            PURLData.put("name", artifactId);
            if(groupId != null) PURLData.put("namespace", groupId);
            if(version != null) PURLData.put("version", version);
            String PURL = buildPURL(PURLData);

            // Add built PURL
            c.addPURL(PURL);
            Debug.log(Debug.LOG_TYPE.DEBUG, String.format("Dependency Found with PURL: %s", PURL));

            // Build URL and worker object
            if(groupId != null) {
                String url = this.STD_LIB_URL;
                url += groupId;
                url += "/" + artifactId;
                if(version != null) url += "/" + version;
                // Create and add QueryWorker with Component reference and URL
                this.queryWorkers.add(new QueryWorker(c, url) {
                    @Override
                    public void run() {
                        // Get page contents
                        final String contents = getUrlContents(queryURL(this.url, false));

                        // Parse license(s)
                        // Regex101: https://regex101.com/r/FUOPSK/1
                        final Matcher m = Pattern.compile("<li data-test=\\\"license\\\">(.*?)</li>", Pattern.MULTILINE).matcher(contents);

                        // Add all found licenses
                        while(m.find()) {
                            this.component.addLicense(m.group(1).trim());
                        }
                    }
                });
            }

            // TODO Find out how to use NVID API to query CPEs
            // QueryWorkers are generic and children should have no issue executing together
//            this.queryWorkers.add(new CPEQueryWorker());

            // Add ParserComponent to components
            components.add(c);
        }

        // Query all found URLs and store any relevant data
        queryURLs(this.queryWorkers);
    }

    /**
     * Given a raw string, this method will attempt to strip all instances of "${...}" -> "...",
     * retrieve their corresponding values from this.props, and return the formatted string.
     * If any step fails, the original string is returned.
     *
     * @param string the string to be formatted
     * @return formatted string if successful, otherwise, original string is returned
     */
    private String formatVariableNames(String string) {
        if(string == null) return null;
        // Regex101: https://regex101.com/r/gIluSW/1
        // Regex first matches ALL intances of "${...}"
        final Pattern p = Pattern.compile("\\$\\{([^}]*)\\}", Pattern.MULTILINE);

        // Then replace these instances with "..." (the stripped value)
        string = p.matcher(string).replaceAll("$1");
        String[] parts = string.split("/");
        for (int i = 0; i < parts.length; i++) {
            final String value = this.properties.get(parts[i]);
            if(value != null) parts[i] = value;
        }
        return String.join("/", parts);
    }


    // TODO: Complete this, currently it is broken

    private void resolveProperties() {
        // Regex101: https://regex101.com/r/gIluSW/1
        // Regex first matches ALL intances of "${...}"
        final Pattern p = Pattern.compile("\\$\\{([^}]*)\\}", Pattern.MULTILINE);

        final LinkedHashMap<String, String> resolvedMap = new LinkedHashMap<>();

        // Resolve this.properties with a copy of its keySet
        // Iterate over keySet
        this.properties.keySet().forEach(key -> {
            // Get value for respective key
            String value = this.properties.get(key);

            // Get variable matcher
            final Matcher m = p.matcher(value);

            // Resolve matches
            resolvedMap.put(key, this.resolveProperty(value, p, m.results().toList(), m));
//            this.resolveProperty(key, resolvedMap, p, m.results().toList(), m);
        });

        this.properties.putAll(resolvedMap);
    }

    private String resolveProperty(String propertyValue, Pattern p, List<MatchResult> results, Matcher m) {
        for (MatchResult result : results) {
            // Get value from this.properties
            String varValue = this.properties.get(result.group(1).trim());

            // If varValue is null, skip this result
            if(varValue == null)
                return m.replaceFirst(propertyValue.replace("$", "\\$"));

            // If varValue still contains a variable, update it
            final Matcher cMatcher = p.matcher(varValue);
            final List<MatchResult> cResults = cMatcher.results().toList();
            for (final MatchResult cResult : cResults) {
                final String cVarValue = this.properties.get(cResult.group(1).trim());
                if(cVarValue == null) {
                    return m.replaceFirst(varValue.replace("$", "\\$"));
                }
//                varValue = cMatcher.replaceFirst(cVarValue);
                resolveProperty(propertyValue, p, cResults, cMatcher);
            }

            // Update this.properties
            propertyValue = m.replaceFirst(varValue);
        }
        return propertyValue;
    }

    //#endregion
}
