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

import static org.svip.sbomfactory.generators.utils.Debug.LOG_TYPE;
import static org.svip.sbomfactory.generators.utils.Debug.log;

/**
 * file: ConanParser.java
 * Description: Package-manager specific implementation of the PackageManagerParser (Conan/conanfile.txt)
 *
 * @author Dylan Mulligan
 */
public class ConanParser extends PackageManagerParser {
    //#region Constructors

    public ConanParser() {
        super(
                "https://docs.conan.io/2/",
                null,
                "" // Regex101: https://regex101.com/r/1Y2gb5/2
        );
    }

    //#endregion

    //#region Core Methods

    @Override
    protected void parseData(ArrayList<ParserComponent> components, HashMap<String, Object> data) {
        // Init properties
        //pliu this.properties = new HashMap<>();

        // Init dependencies
        //pliu this.dependencies = new LinkedHashMap<>();

        // Insert data
//        this.resolveProperties(
//                this.dependencies,
//                (HashMap<String, String>) data.get("dependencies")
//        );

        // Get properties
//        final ArrayList<String> ext = (ArrayList<String>) data.get("ext");

        // Store properties
//        this.resolveProperties(
//                this.properties,
//                (HashMap<String, String>) ext
//                        .stream().collect(
//                                Collectors.toMap(
//                                        e -> e.substring(0, e.indexOf('=')).trim(),
//                                        e -> e.substring(e.indexOf('=') + 1)
//                                                .trim()
//                                                .replace("'", "")
//                                                .replace("\"", "")
//                                ))
//        );


        // Iterate over dependencies
        for (final LinkedHashMap<String, String> d : (ArrayList<LinkedHashMap<String, String>>) data.get("dependencies")) {
            // Get value from map
            //final HashMap<String, String> d = this.dependencies.get(artifactId);

            // Create ParserComponent from dep info
            final ParserComponent c = new ParserComponent(d.get("artifactId"));
            //c.setGroup(d.get("group"));
//            if (d.containsKey("type")) {
//                final String type = d.get("type");
//                if (type.equals("file")) c.setType(ParserComponent.Type.INTERNAL);
//            }
            if (d.containsKey("version")) c.setVersion(d.get("version"));

            // TODO: Query
//            String url = "";
//            this.queryWorkers.add(new QueryWorker(c, url) {
//                @Override
//                public void run() {
//
//                }
//            });

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
        final Matcher m = Pattern.compile("(^|\\s*)(\\[[a-z_-]*\\])\\s*(((?!.*\\[([a-z_-]*)\\]).*\\s*)*)", Pattern.MULTILINE)
                .matcher(fileContents);

        // Store results in data
        for (final MatchResult mr : m.results().toList()) {
            // Get key and value of match ("[requires]...")
            final String key = mr.group(2).trim();
            final String value = mr.group(3).trim();
            System.out.println("key:\n  " + key + "\nvalue:\n   " + value + "\n\n");

            // Dependencies will need to be parsed further, so pass raw string
            switch(key) {
                case "[requires]":
                    System.out.println("got \"[requires]\" in switch stmt");
                    String[] lines = value.split("\n");
                    // Init dependencies list
                    final ArrayList<LinkedHashMap<String, String>> deps = new ArrayList<>();
                    for (String line : lines) {
                        final LinkedHashMap<String, String> dep = new LinkedHashMap<>();
                        final String[] linei = line.trim().split("/");
                        dep.put("artifactId", linei[0]);
                        dep.put("version", linei[1]);
                        // Insert value
                        deps.add(dep);
                    }  //for loop for lines
                    data.put("dependencies", deps);
                    int i = 5;
                    break;
                default:
                    System.out.println("Not a \"[requires]\" in switch stmt");
                    break;
            }
        }   // for (final MatchResult mr : m.results().toList()) {
            // Parse data
            this.parseData(components, data);
        //#endregion
    }

}
