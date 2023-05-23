package org.svip.sbomfactory.generators.parsers.packagemanagers;

import org.svip.sbomfactory.generators.utils.ParserComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.svip.sbomfactory.generators.utils.QueryWorker;

import static org.svip.sbomfactory.generators.utils.Debug.LOG_TYPE;
import static org.svip.sbomfactory.generators.utils.Debug.log;

/**
 * file: GradleParser.java
 * Description: Package-manager specific implementation of the PackageManagerParser (Gradle/gradle.build)
 *
 * @author Dylan Mulligan
 */
public class GradleParser extends PackageManagerParser {

    //#region Constructors

    public GradleParser() {
        super(
                //gradle can use any maven or ivy repo provided, however not all repos will provide license information.
                "https://central.sonatype.com/artifact/",
                null,
                "\\$([^\\n/\\\\]*)" // Regex101: https://regex101.com/r/1Y2gb5/2
        );
    }

    //#endregion

    //#region Core Methods

    @Override
    protected void parseData(ArrayList<ParserComponent> components, HashMap<String, Object> data) {
        // Init properties
        this.properties = new HashMap<>();

        // Init dependencies
        this.dependencies = new LinkedHashMap<>();

        // Insert data
        this.resolveProperties(
                this.dependencies,
                (HashMap<String, String>) data.get("dependencies")
        );

        // Get properties
        final ArrayList<String> ext = (ArrayList<String>) data.get("ext");

        if(ext != null) {
            // Store properties
            this.resolveProperties(
                    this.properties,
                    (HashMap<String, String>) ext
                            .stream().collect(
                                    Collectors.toMap(
                                            e -> e.substring(0, e.indexOf('=')).trim(),
                                            e -> e.substring(e.indexOf('=') + 1)
                                                    .trim()
                                                    .replace("'", "")
                                                    .replace("\"", "")
                                    ))
            );
        }


        // Iterate over dependencies
        for (final String artifactId : this.dependencies.keySet()) {
            // Get value from map
            final HashMap<String, String> d = this.dependencies.get(artifactId);

            // Create ParserComponent from dep info
            final ParserComponent c = new ParserComponent(artifactId);
            c.setGroup(d.get("groupId"));
            if (d.containsKey("type")) {
                final String type = d.get("type");
                if (type.equals("file")) c.setType(ParserComponent.Type.INTERNAL);
            }
            if (d.containsKey("version")) c.setVersion(d.get("version"));

            if(c.getGroup() == null) continue; // Prevent null groups in URL

            String url = STD_LIB_URL + c.getGroup() + "/" + c.getName() + "/" +
                    (c.getVersion() == null ? "" : c.getVersion());
            this.queryWorkers.add(new QueryWorker(c, url) {
                @Override
                public void run() {
                    // Get page contents
                    final String contents = getUrlContents(queryURL(this.url, false));

                    if(contents.length() == 0) {
                        return;
                    }

                    // Parse license(s)
                    // Regex101: https://regex101.com/r/FUOPSK/1
                    final Matcher m = Pattern.compile("<li data-test=\\\"license\\\">(.*?)</li>", Pattern.MULTILINE).matcher(contents);

                    // Add all found licenses
                    while(m.find()) {
                        this.component.addLicense(m.group(1).trim());
                    }
                }
            });

            queryURLs(this.queryWorkers); // TODO is thsi correct?
            // Add ParserComponent to components
            components.add(c);
            log(LOG_TYPE.DEBUG, String.format("New Component: %s", c.toReadableString()));
        }
    }

    @Override
    public void parse(ArrayList<ParserComponent> components, String fileContents) {
        // Init main data structure
        final LinkedHashMap<String, Object> data = new LinkedHashMap<>();

        // Init main Matcher
        // Regex101: https://regex101.com/r/a3rIlp/3
        final Matcher m = Pattern.compile("^(.*) \\{([\\s\\S]*?)^\\}", Pattern.MULTILINE)
                .matcher(fileContents);

        // Store results in data
        for (final MatchResult mr : m.results().toList()) {
            // Get key and value of match ("dependencies", "...")
            final String key = mr.group(1).trim();
            final String value = mr.group(2).trim().replace("\r", "");

            // Dependencies will need to be parsed further, so pass raw string
            if (key.equals("dependencies")) {
                // Init dependencies list
                final HashMap<String, LinkedHashMap<String, String>> deps = new HashMap<>();

                // Init dependency Pattern
                // Regex101: https://regex101.com/r/cFnCpF/12
                final Pattern dependenciesPattern =
                        Pattern.compile("^\\s*\\w* ?\\(?\\[?(?:(?:(?:[\\\"'](.*:.*)[\\\"'])|(?:(group: [^\\n{]*, name: [^\\n{]*, version: [^\\n{)\\]]*)\\)?\\[?))|file.*\\((.*)?\\))(?:(?=.*\\{\\n?).*\\{([\\S\\s]*?)^\\t?(?: {4})?\\})?", Pattern.MULTILINE);

                // Init dependency matcher
                final Matcher depMatcher = dependenciesPattern.matcher(value);

                // Iterate over results
                for (final MatchResult depResult : depMatcher.results().toList()) {
                    final LinkedHashMap<String, String> dep = new LinkedHashMap<>();
                    // Group 1 format: "org.springframework:spring-api:3.6"
                    if(depResult.group(1) != null) {
                        // Extract values from dep string
                        final String[] depValues = depResult.group(1).split(":");
                        dep.put("groupId", depValues[0]);
                        if(depValues.length > 1) dep.put("artifactId", depValues[1]);
                        if(depValues.length > 2) dep.put("version", depValues[2]);
                    } // Group 2 format: "group: 'org.springframework', name: 'spring-core', version: '2.5'"
                    else if(depResult.group(2) != null) {
                        final String[] depValues = depResult.group(2).split(",");
                        final HashMap<String, String> props = new HashMap<>(depValues.length);
                        Arrays.stream(depValues).forEach(d -> {
                            d = d.replace("'", "").replace("\"", "");
                            final String dKey = d.substring(0, d.indexOf(":")).trim();
                            final String dValue = d.substring(d.indexOf(":") + 1).trim();
                            props.put(dKey, dValue);
                        });
                        if(props.containsKey("group")) dep.put("groupId", props.get("group"));
                        if(props.containsKey("name")) dep.put("artifactId", props.get("name"));
                        if(props.containsKey("version")) dep.put("version", props.get("version"));
                    } // Group 3 format for file(s)/dir(s): "'hibernate.jar', 'libs/spring.jar'"
                    else if(depResult.group(3) != null) {
                        // Extract values from dep string
                        final String[] depValues =  depResult.group(3)
                                .replace("'", "")
                                .replace("\"", "")
                                .split(",");

                        // If one file is found, add its data to dep
                        if (depValues.length == 1) dep.put("artifactId", depValues[0].trim());
                        // If more than one file is found, create and add a new LHM for each one
                        else {
                            for (final String depValue : depValues) {
                                final String artifactId = depValue.trim();
                                dep.put("artifactId", artifactId);
                                deps.put(artifactId, dep);
                            }
                            continue;
                        }
                    }

                    // Add any values found in group 4, if present
                    // This can only occur when group 1 or 2 != null
                    if(depResult.group(4) != null) {
                        // TODO: Process group 4 values
                        //  Group 4 captures
                    }

                    // Insert value
                    deps.put(dep.get("artifactId"), dep); // TODO: Uniqueness? Tests fail on non-unique artifactIds
                }
                data.put("dependencies", deps);
            }
            // Other collected data can be split on "\n"
            else data.put(key, new ArrayList<>(Arrays.stream(value.split("\n")).map(String::trim).toList()));
        }

        // Parse data
        this.parseData(components, data);
    }

    //#endregion
}
