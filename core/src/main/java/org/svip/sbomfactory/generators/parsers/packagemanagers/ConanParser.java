package org.svip.sbomfactory.generators.parsers.packagemanagers;

import org.svip.sbomfactory.generators.utils.ParserComponent;
import org.svip.sbomfactory.generators.utils.QueryWorker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import static org.svip.sbomfactory.generators.utils.Debug.LOG_TYPE;
import static org.svip.sbomfactory.generators.utils.Debug.log;

/**
 * file: ConanParser.java
 * Description: Package-manager specific implementation of the PackageManagerParser (Conan/conanfile.txt or conanfile.py)
 *
 * @author Dylan Mulligan, Ping Liu
 */
public class ConanParser extends PackageManagerParser {

    public ConanParser() {
        super(
                "https://conan.io/center/",
                null,
                "" // Regex101: https://regex101.com/r/1Y2gb5/2
        );
    }


    @Override
    protected void parseData(ArrayList<ParserComponent> components, HashMap<String, Object> data) {

        // Iterate over dependencies
        for (final LinkedHashMap<String, String> d : (ArrayList<LinkedHashMap<String, String>>) data.get("dependencies")) {
            // Create ParserComponent from dep info
            final ParserComponent c = new ParserComponent(d.get("artifactId"));
                  c.setType(ParserComponent.Type.EXTERNAL);
                  //c.setGroup("None");
            if (d.containsKey("version")) c.setVersion(d.get("version"));
            //Query for Licenses
            String name = d.get("artifactId");
            // Build URL and worker object
            if(name != null) {
                // Create and add QueryWorker with Component reference and URL
                this.queryWorkers.add(new QueryWorker(c, this.STD_LIB_URL + name){
                    @Override
                    public void run() {
                        // Get page contents
                        final String contents = getUrlContents(queryURL(this.url, true));
                        // Parse license with content in the form : //license = \"Zlib\"
                        // https://regex101.com/r/LKcQrx/1
                        Matcher m = Pattern.compile("license\\s*=\\s*[\\\\]*\"([a-zA-Z0-9.\\-_]*)[\\\\]*\"", Pattern.MULTILINE).matcher(contents);
                        String r;
                        if(m.find()) {
                            r = m.group(1).trim();
                            this.component.addLicense(r);
                        }
                        else {
                            // Parse license with content in the following form:
                            // <script (<----- matching this) data-n-head="ssr" type="application/ld+json">
                            //                                {
                            //                                        "@context": "http://schema.org",
                            //                                "@graph": [
                            //                        {
                            //                            "@context":"http://schema.org",
                            //                                "@type":"Dataset",
                            //                                "name":"imgui",    <----- matching this also
                            //                                "description":"Bloat-free Immediate Mode Graphical User interface for C++ with minimal dependencies",
                            //                                "version":"cci.20230105+1.89.2.docking",
                            //                                "license":"MIT",    <----- matching this also
                            //                                "interactionCount": "30,144"
                            //                        }
                            //        ]
                            //      }
                            //    </script>    <----- matching this also
                            m = Pattern.compile(String.format("<script.*\"name\"\\s*:\\s*\"%s\".*\"license\"\\s*:\\s*\"([a-zA-Z0-9.\\\\-_]*)\".*</script>",name), Pattern.MULTILINE).matcher(contents);
                            if(m.find()) {
                                r = m.group(1).trim();
                                this.component.addLicense(r);
                            }
                        }
                    }
                    // TODO Add CPEs and PURLS
                });
            }
            queryURLs(this.queryWorkers);
            // Add ParserComponent to components
            components.add(c);
            log(LOG_TYPE.DEBUG, String.format("New Component: %s", c.toReadableString()));
        }
    }

    @Override
    public void parse(ArrayList<ParserComponent> components, String fileContents) {
        // Init main data structure
        final LinkedHashMap<String, Object> data = new LinkedHashMap<>();

        fileContents = removeComments(fileContents);

        // Init main Matcher for conanfile.txt(default)   // Regex101: https://regex101.com/r/a3rIlp/3
        Matcher m = Pattern.compile("(^|\\s*)(\\[[a-z_-]*\\])\\s*(((?!.*\\[([a-z_-]*)\\]).*\\s*)*)", Pattern.MULTILINE).matcher(fileContents);

        //Check file content to see if it is a conanfile.py
        if (fileContents.contains(" requirements(self):") && fileContents.contains("self.requires(")) {
            //System.out.println("conanfile.py content found!");
            m = Pattern.compile("(^|\\s*)(def\\s+[a-z_]+\\(self\\)\\:)\\s*(((?!.*def\\s+[a-z_]+\\(self\\)\\:).*\\s*)*)",Pattern.MULTILINE)
                    .matcher(fileContents);
        }

        ArrayList<LinkedHashMap<String, String>> deps = new ArrayList<>();

        for (final MatchResult mr : m.results().toList()) {
            // Get key and value
                  String key = mr.group(2).trim();
            final String value = mr.group(3).trim();
            //keys:  "[requires]" from conanfile.txt files or "defrequirements(self):" from conanfile.py files
            //remove all white spaces in the key for conanfile.py files so that it becomes uniq
            key = key.replaceAll("\\s+","");

            // Dependencies will need to be parsed further, so pass raw string
            switch(key) {
                case "[requires]":
                    String[] lines = value.split("\n");
                    // Process line by line
                    for (String line : lines) {
                        final LinkedHashMap<String, String> dep = new LinkedHashMap<>();
                        procline(dep,  line);
                        // Insert value
                        deps.add(dep);
                    }
                    // Add to data
                    data.put("dependencies", deps);
                    break;
                case "defrequirements(self):":
                    lines = value.split("\n");
                    for (String line : lines) {
                        if(line.contains("self.requires(\"") || line.contains("self.requires('")) {
                            final LinkedHashMap<String, String> dep = new LinkedHashMap<>();
                            //getting the value in quotes by removing the rest
                            String tline = line.trim().replaceAll("self\\.requires\\([\"']|[\"']\\)", "");
                            procline(dep,  tline);
                            // Insert value
                            deps.add(dep);
                        }
                    }
                    // Store results in data
                    data.put("dependencies", deps);
                    break;
                default:
                    break;
            }
        }
            // Parse data
            this.parseData(components, data);
    }

    public String removeComments(String text) {
        String nocommenttext = "";
        Boolean inblockcomment = false;
        /*
        Block comment cases(python):
             1.   quotes on different lines
                a:
                  """ or '''
                    ...
                  """ or '''
                b:
                  """ or '''
                    ...   """ or '''

             2.  quotes are on the same line
                 """ or '''  ...   """ or '''
         */
        for (String line : text.split("\n")) {
            if (line.contains("\"\"\"") || line.contains("'''")) {
                inblockcomment = !inblockcomment;
                //check to see if the end comment(""" or ''') is on the same line
                String tline = line.trim();
                String bsubstr = tline.substring(0, 3);
                String substr = tline.substring(3);
                if ((bsubstr.contains("\"\"\"") || bsubstr.contains("'''")) && (substr.contains("\"\"\"") || substr.contains("'''"))) {
                    inblockcomment = !inblockcomment;
                }
                continue;  //skip the ending quotes
            }
            if (!inblockcomment) {
                /*
                Line comment cases:
                  3.  the comment takes up a whole line:
                      #  ...
                  4.  the comment takes up a portion of a line at lease 1 space between the content and the # sign
                      ...  # ...
                 */
                String rmedcmt = line.replaceAll("^#.*|\\s+#.*", "") + "\n";
                nocommenttext = nocommenttext + rmedcmt;
            }
        }
        return nocommenttext;
    }

    public LinkedHashMap<String, String> procline(LinkedHashMap<String, String> dep,  String line) {
        final String[] linei = line.trim().split("/");
        dep.put("artifactId", linei[0]);
        dep.put("version", linei[1]);
        return dep;
    }
}
