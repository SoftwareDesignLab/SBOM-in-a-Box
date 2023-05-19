package org.svip.sbomfactory.generators.parsers.packagemanagers;

import org.svip.sbomfactory.generators.utils.ParserComponent;
import org.svip.sbomfactory.generators.utils.QueryWorker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * file: RequirementsParser.java
 * Description: Package-manager specific implementation of the PackageManagerParser (PIP/requirements.txt)
 *
 * @author Dylan Mulligan
 */
public class RequirementsParser extends PackageManagerParser {
    public RequirementsParser() {
        super(
                "https://pypi.org/project/",
                null,
                "" // TODO: Token regex? I don't think PIP has need for this
        );
    }

    @Override
    protected void parseData(ArrayList<ParserComponent> components, HashMap<String, Object> data) {
        final ArrayList<LinkedHashMap<String, String>> requirements =
                (ArrayList<LinkedHashMap<String, String>>) data.get("requirements");

        for (LinkedHashMap<String, String> req : requirements) {
            final String name = req.get("name");
            final ParserComponent c = new ParserComponent(name);
            if(req.containsKey("version")) c.setVersion(req.get("version"));
            if(req.containsKey("src")) c.setGroup(req.get("src"));

            // Build URL and worker object
            if(name != null) {
                // Create and add QueryWorker with Component reference and URL
                this.queryWorkers.add(new QueryWorker(c, this.STD_LIB_URL + name){
                    @Override
                    public void run() {
                        // Get page contents
                        final String contents = getUrlContents(queryURL(this.url, true));

                        // Parse license
                        // https://regex101.com/r/LKcQrx/1
                        final Matcher m = Pattern.compile("<p><strong>License:</strong>(.*?)</p>", Pattern.MULTILINE).matcher(contents);

                        while(m.find()) {
                            this.component.addLicense(m.group(1).trim());
                        }
                    }

                    // TODO Add CPEs and PURLS
                });
            }

            // Add ParserComponent to components
            components.add(c);
        }

        // Query all found URLs and store any relevant data
        queryURLs(this.queryWorkers);
    }

    @Override
    public void parse(ArrayList<ParserComponent> components, String fileContents) {
        // Init main data structure
        final LinkedHashMap<String, Object> data = new LinkedHashMap<>();

        // Init requirements list
        final ArrayList<LinkedHashMap<String, String>> requirements = new ArrayList<>();

        // Init Matcher
        // Regex101: https://regex101.com/r/JY3cex/4
        final Matcher m =
                Pattern.compile("^\\s*(?!#)([^#=@\\s]+)(?: ?== ?(.*)| ?@ ?(.*))?$", Pattern.MULTILINE)
                        .matcher(fileContents);

        // Parse requirements from fileContents and add to list
        while(m.find()) {
            // Init match data structure
            final LinkedHashMap<String, String> req = new LinkedHashMap<>();

            // Collect match information
            // Requirement name
            req.put("name", m.group(1));
            // Requirement version (Optional)
            if(m.group(2) != null) req.put("version", m.group(2));
            // Requirement source (Optional)
            if(m.group(3) != null) req.put("src", m.group(3));

            // Add collected information to list
            requirements.add(req);
        }

        // Insert requirements list into data
        data.put("requirements", requirements);

        // Parse collected data
        this.parseData(components, data);
    }
}
