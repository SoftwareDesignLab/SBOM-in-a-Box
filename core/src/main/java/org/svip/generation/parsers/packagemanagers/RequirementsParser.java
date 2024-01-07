/ **
* Copyright 2021 Rochester Institute of Technology (RIT). Developed with
* government support under contract 70RCSA22C00000008 awarded by the United
* States Department of Homeland Security for Cybersecurity and Infrastructure Security Agency.
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the “Software”), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
* /

package org.svip.generation.parsers.packagemanagers;

import org.svip.generation.parsers.utils.QueryWorker;
import org.svip.sbom.builder.objects.SVIPComponentBuilder;
import org.svip.sbom.model.shared.util.LicenseCollection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * file: RequirementsParser.java
 * Description: Package-manager specific implementation of the PackageManagerParser (PIP/requirements.txt)
 *
 * @author Dylan Mulligan
 * @author Ian Dunn
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
    protected void parseData(List<SVIPComponentBuilder> components, HashMap<String, Object> data) {
        final ArrayList<LinkedHashMap<String, String>> requirements =
                (ArrayList<LinkedHashMap<String, String>>) data.get("requirements");

        for (LinkedHashMap<String, String> req : requirements) {
            final String name = req.get("name");
            SVIPComponentBuilder builder = new SVIPComponentBuilder();
            builder.setName(name);
            if (req.containsKey("version")) builder.setVersion(req.get("version"));
            if (req.containsKey("src")) builder.setGroup(req.get("src"));

            // Build URL and worker object
            if (name != null) {
                // Create and add QueryWorker with Component reference and URL
                this.queryWorkers.add(new QueryWorker(builder, this.STD_LIB_URL + name) {
                    @Override
                    public void run() {
                        // Get page contents
                        final String contents = getUrlContents(queryURL(this.url, true));
                        // Parse license
                        // https://regex101.com/r/LKcQrx/1
                        final Matcher m = Pattern.compile("<p><strong>License:</strong>(.*?)</p>", Pattern.MULTILINE).matcher(contents);

                        while (m.find()) {
                            LicenseCollection licenses = new LicenseCollection();
                            licenses.addConcludedLicenseString(m.group(1).trim());
                            this.builder.setLicenses(licenses);
                        }
                    }

                    // TODO Add CPEs and PURLS
                });
            }

            // Add ParserComponent to components
            components.add(builder);
        }

        // Query all found URLs and store any relevant data
        queryURLs(this.queryWorkers);
    }

    @Override
    public void parse(List<SVIPComponentBuilder> components, String fileContents) {
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
        while (m.find()) {
            // Init match data structure
            final LinkedHashMap<String, String> req = new LinkedHashMap<>();

            // Collect match information
            // Requirement name
            req.put("name", m.group(1));
            // Requirement version (Optional)
            if (m.group(2) != null) req.put("version", m.group(2));
            // Requirement source (Optional)
            if (m.group(3) != null) req.put("src", m.group(3));

            // Add collected information to list
            requirements.add(req);
        }

        // Insert requirements list into data
        data.put("requirements", requirements);

        // Parse collected data
        this.parseData(components, data);
    }
}
