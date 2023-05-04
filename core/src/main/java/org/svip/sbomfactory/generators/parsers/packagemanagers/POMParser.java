package org.svip.sbomfactory.generators.parsers.packagemanagers;

import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import org.svip.sbom.model.CPE;
import org.svip.sbomfactory.generators.utils.ParserComponent;
import org.svip.sbomfactory.generators.utils.QueryWorker;
import org.svip.sbom.model.PURL;

import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.svip.sbomfactory.generators.utils.Debug.*;

/**
 * file: POMParser.java
 * Description: Package-manager specific implementation of the PackageManagerParser (Maven/pom.xml)
 *
 * @author Dylan Mulligan
 */
public class POMParser extends PackageManagerParser {
    //#region Constructors

    public POMParser() { super("https://central.sonatype.com/artifact/", new XmlFactory()); }

    //#endregion

    //#region Core Methods

    @Override
    protected void parseData(ArrayList<ParserComponent> components, HashMap<String, Object> data) {
        // Init properties
        this.properties = new HashMap<>();

        // Resolve nested properties (e.x. "<maven.compiler.source>${java.version}</maven.compiler.source>")
        this.resolveProperties((LinkedHashMap<String, String>) data.get("properties"));

        // Get dependencies from data
        this.dependencies = ((LinkedHashMap<String, ArrayList<LinkedHashMap<String, String>>>)
                data.get("dependencies")).get("dependency");

        final LinkedHashMap build = (LinkedHashMap) data.get("build");

        // Get plugins from data
        final ArrayList<LinkedHashMap<String, String>> plugins =
                ((LinkedHashMap<String, ArrayList<LinkedHashMap<String, String>>>) build.get("plugins")).get("plugin");

        // Iterate and build URLs
        for (final LinkedHashMap<String, String> d : this.dependencies) {
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
            String PURLString = PackageManagerParser.buildPURL(PURLData);

            // Add built PURL
            c.addPURL(new PURL(PURLString)); // TODO: Use PURL class y/n?
            log(LOG_TYPE.DEBUG, String.format("Dependency Found with PURL: %s", PURLString));

            // Build CPE
            CPE cpe = new CPE("maven", artifactId, version);
            String cpeFormatString = cpe.bindToFS();
            c.addCPE(cpeFormatString);
            log(LOG_TYPE.DEBUG, String.format("Dependency Found with CPE: %s", cpeFormatString));

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
    @Override
    protected void resolveProperties(HashMap<String, String> props) {
        // Regex101: https://regex101.com/r/gIluSW/1
        // Regex first matches ALL intances of "${...}"
        final Pattern p = Pattern.compile("\\$\\{([^}]*)\\}", Pattern.MULTILINE);

        // Iterate over keys
        props.keySet().forEach(key -> {
            // Resolve property
            resolveProperty(key, props.get(key), props, p);
        });
    }

    protected void resolveProperty(String key, String value, HashMap<String, String> props, Pattern p) {
        // Get results
        final List<MatchResult> results =  p.matcher(value).results().toList();

        // If none found, this property does not contain a variable
        if(results.size() == 0) this.properties.put(key, props.get(key));
        else { // Otherwise, there is one or more variables that need to be resolved
            // Iterate over match results
            for (MatchResult result : results) {
                final String varKey = result.group(1).trim();
                // Get value from this.properties
                String varValue = this.properties.get(varKey);

                // If no corresponding value, get value from props
                if(varValue == null || p.matcher(varValue).find()) {
                    varValue = props.get(varKey);
                    // If value is null, this property cannot be resolved, store original value
                    if(varValue == null) this.properties.put(key, value);
                    // Otherwise, recurse resolution
                    else {
                        // TODO: Do this for all values
                        final String resolvedValue =
                                value.substring(0, result.start(1) - 2) +
                                varValue +
                                value.substring(result.end(1) + 1);
                        resolveProperty(key, resolvedValue, props, p);
                    }
                }
                // Otherwise, this property can be resolved
                else this.properties.put(key, varValue);
            }
        }
    }

    //#endregion
}
