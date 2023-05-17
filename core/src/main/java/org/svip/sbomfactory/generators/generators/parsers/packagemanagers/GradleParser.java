package org.svip.sbomfactory.generators.generators.parsers.packagemanagers;

import com.fasterxml.jackson.core.JsonFactory;
import org.svip.sbomfactory.generators.generators.utils.ParserComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * file: GradleParser.java
 * Description: Package-manager specific implementation of the PackageManagerParser (Gradle/gradle.build)
 *
 * @author Dylan Mulligan
 */
public class GradleParser extends PackageManagerParser {
    //#region Constructors

    public GradleParser() { super("https://docs.gradle.org/current/javadoc/", new JsonFactory()); }

    //#endregion

    //#region Core Methods

    @Override
    protected void parseData(ArrayList<ParserComponent> components, HashMap<String, Object> data) {
        // Init dependencies list
        final ArrayList<String> dependencies =
                (ArrayList<String>) data.get("dependencies");

        // Iterate over dependencies
        for (final String dep : dependencies) {
            // Ignore comments
            if(dep.startsWith("//")) continue;

            // Split on space to separate name and type
            final int spaceIndex = dep.indexOf(' ');
            String name = dep.substring(spaceIndex + 1);
            if(name.startsWith("'") || name.startsWith("\""))
                name = name.replace("\"", "").replace("'", "");
            final String type = dep.substring(0, spaceIndex);

            // Create ParserComponent from dep info
            final ParserComponent c = new ParserComponent(name);
            // TODO: Add more info
//            c.setType();

//            String url = "";
//            this.queryWorkers.add(new QueryWorker(c, url) {
//                @Override
//                public void run() {
//
//                }
//            });

            // Add ParserComponent to components
            components.add(c);
        }
    }

    @Override
    public void parse(ArrayList<ParserComponent> components, String fileContents) {
        // Init main data structure
        final LinkedHashMap<String, Object> data = new LinkedHashMap<>();

        // Init dependencies list
        final ArrayList<LinkedHashMap<String, String>> dependencies = new ArrayList<>();

        // Init Matcher
        // Regex101: https://regex101.com/r/a3rIlp/1
        final Matcher m = Pattern.compile("^(.*) \\{([^}]*)\\}", Pattern.MULTILINE)
                        .matcher(fileContents);

        // Store results in data
        for (final MatchResult mr : m.results().toList()) {
            final String value = mr.group(2).trim().replace("\r", "");
            final ArrayList<String> values =
                    new ArrayList<>(Arrays.stream(value.split("\n")).map(String::trim).toList());
            data.put(mr.group(1), values);
        }

        // Parse data
        this.parseData(components, data);
    }

    //#endregion
}
