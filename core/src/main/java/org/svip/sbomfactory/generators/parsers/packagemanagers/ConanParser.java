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
    //#region Constructors

    public ConanParser() {
        super(
                "https://conan.io/center/",
                null,
                "" // Regex101: https://regex101.com/r/1Y2gb5/2
        );
    }

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
            // Get value from ma
            // Create ParserComponent from dep info
            final ParserComponent c = new ParserComponent(d.get("artifactId"));
                  c.setType(ParserComponent.Type.EXTERNAL);
                  c.setGroup("None");
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
                        //System.out.println("QueryWorker(" + name +"): \n" + contents);
                        System.out.println("QueryWorker(" + name +"): " + this.url);
                        // Parse license
                        // https://regex101.com/r/LKcQrx/1
                        //license = \"Zlib\"\n
                        Matcher m = Pattern.compile("license\\s*=\\s*[\\\\]*\"([a-zA-Z0-9.\\-_]*)[\\\\]*\"", Pattern.MULTILINE).matcher(contents);
                        String r;
                        if(m.find()) {
                            r = m.group(1).trim();
                            System.out.println("QueryWorker-m(" + name +"): \n" + r);
                            this.component.addLicense(r);
                        }
                        else {
                            // <script data-n-head="ssr" type="application/ld+json">
                            //                                {
                            //                                        "@context": "http://schema.org",
                            //                                "@graph": [
                            //                        {
                            //                            "@context":"http://schema.org",
                            //                                "@type":"Dataset",
                            //                                "name":"imgui",    <----- matching this
                            //                                "description":"Bloat-free Immediate Mode Graphical User interface for C++ with minimal dependencies",
                            //                                "version":"cci.20230105+1.89.2.docking",
                            //                                "license":"MIT",    <----- matching this also
                            //                                "interactionCount": "30,144"
                            //                        }
                            //        ]
                            //      }
                            //    </script>
                            m = Pattern.compile(String.format("<script.*\"name\"[\\s:]+\"%s\".*\"license\"[\\s:]+\"([a-zA-Z0-9.\\\\-_]*)\".*</script>",name), Pattern.MULTILINE).matcher(contents);
                            if(m.find()) {
                                r = m.group(1).trim();
                                System.out.println("QueryWorker-m again(" + name +"): \n" + r);
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

        //Check file content to see if it is a conanfile.txt or conanfile.py
        if (fileContents.contains(" requirements(self):") && fileContents.contains("self.requires(")) {
            //System.out.println("conanfile.py content found!");
            m = Pattern.compile("(^|\\s*)(def\\s+[a-z_]+\\(self\\)\\:)\\s*(((?!.*def\\s+[a-z_]+\\(self\\)\\:).*\\s*)*)",Pattern.MULTILINE)
                    .matcher(fileContents);
        }

        ArrayList<LinkedHashMap<String, String>> deps = new ArrayList<>();
        // Store results in data
        for (final MatchResult mr : m.results().toList()) {
            // Get key and value of match ("[requires]...") or "defrequirements(self):..."
                  String key = mr.group(2).trim();
            final String value = mr.group(3).trim();
            //System.out.println("key:\n  " + key + "\nvalue:\n   " + value + "\n\n");
            //remove all white spaces in the key
            key = key.replaceAll("\\s+","");
            //System.out.println("R key:\n  " + key + "\nvalue:\n   " + value + "\n\n");

            // Dependencies will need to be parsed further, so pass raw string
            switch(key) {
                case "[requires]":
                    //System.out.println("got \"[requires]\" in switch stmt");
                    String[] lines = value.split("\n");
                    // Init dependencies list
                    for (String line : lines) {
                        final LinkedHashMap<String, String> dep = new LinkedHashMap<>();
                        procline(dep,  line);
                        // Insert value
                        deps.add(dep);
                    }  //for loop for lines
                    data.put("dependencies", deps);
                    break;
                case "defrequirements(self):":
                    //System.out.println("Got R key:\n  " + key + "\nvalue:\n   " + value + "\n\n");
                    lines = value.split("\n");
                    for (String line : lines) {
                        if(line.contains("self.requires(\"") || line.contains("self.requires('")) {
                            final LinkedHashMap<String, String> dep = new LinkedHashMap<>();
                            //getting the values in quotes
                            String tline = line.trim().replaceAll("self\\.requires\\([\"']|[\"']\\)", "");
                            procline(dep,  tline);
                            // Insert value
                            deps.add(dep);
                        }
                    }
                    data.put("dependencies", deps);
                    break;
                default:
                    break;
            }
        }   // for (final MatchResult mr : m.results().toList()) {
            // Parse data
            this.parseData(components, data);
        //#endregion
    }

    public String removeComments(String text) {
        String nocommenttext = "";
        Boolean inblockcomment = false;
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
                String rmedcmt = line.replaceAll("^#.*|\\s+#.*", "") + "\n"; //#/line sign comments
                nocommenttext = nocommenttext + rmedcmt;
            }
        }
        //System.out.println("text***********:\n" + text);
        //System.out.println("nocommenttext==============:\n" + nocommenttext);

        return nocommenttext;
    }

    public LinkedHashMap<String, String> procline(LinkedHashMap<String, String> dep,  String line) {
        final String[] linei = line.trim().split("/");
        dep.put("artifactId", linei[0]);
        dep.put("version", linei[1]);
        return dep;
    }
}
