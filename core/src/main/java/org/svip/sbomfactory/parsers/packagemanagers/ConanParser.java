package org.svip.sbomfactory.parsers.packagemanagers;

import org.svip.builders.component.SVIPComponentBuilder;
import org.svip.sbom.model.shared.util.LicenseCollection;
import org.svip.utils.QueryWorker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.svip.utils.Debug.LOG_TYPE;
import static org.svip.utils.Debug.log;

/**
 * file: ConanParser.java
 * Description: Package-manager specific implementation of the PackageManagerParser (Conan/conanfile.txt or conanfile.py)
 *
 * @author Dylan Mulligan
 * @author Ping Liu
 * @author Ian Dunn
 */
public class ConanParser extends PackageManagerParser {

    public ConanParser() {
        super(
                "https://conan.io/center/",
                null,
                "" // Regex101: https://regex101.com/r/1Y2gb5/2
        );
    }


    /**
     * Parses a given set of raw data into Components and adds them to the given list
     * of Components. This method is abstract and should be implemented to parse each specific
     * dependency file differently, as needed.
     *
     * @param components list of Components to add to
     * @param data       map of data to be parsed
     */
    @Override
    protected void parseData(List<SVIPComponentBuilder> components, HashMap<String, Object> data) {

        // Iterate over dependencies
        for (final LinkedHashMap<String, String> d : (ArrayList<LinkedHashMap<String, String>>) data.get("dependencies")) {
            // Create ParserComponent from dep info
            SVIPComponentBuilder builder = new SVIPComponentBuilder();
            builder.setName(d.get("artifactId"));
            builder.setType("EXTERNAL");
                  //c.setGroup("None");
            if (d.containsKey("version")) builder.setVersion(d.get("version"));
            //Query for Licenses
            String name = d.get("artifactId");
            // Build URL and worker object
            if(name != null) {
                // Create and add QueryWorker with Component reference and URL
                this.queryWorkers.add(new QueryWorker(builder, this.STD_LIB_URL + name){
                    @Override
                    public void run() {
                        // Get page contents
                        final String contents = getUrlContents(queryURL(this.url, true));
                        // Parse license with content in the form : //license = \"Zlib\"
                        // example: https://regex101.com/r/EJMxrF/1
                        Matcher m = Pattern.compile("license\\s*=\\s*[\\\\]*\"([\\w.\\-]*)[\\\\]*\"", Pattern.MULTILINE).matcher(contents);
                        String r;
                        if(m.find()) {
                            r = m.group(1).trim();
                            LicenseCollection licenses = new LicenseCollection();
                            licenses.addConcludedLicenseString(r);
                            this.builder.setLicenses(licenses);
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
                            // examples : https://regex101.com/r/OIM0dl/1 and https://regex101.com/r/OIM0dl/2
                            m = Pattern.compile(String.format("<script.*\"name\"\\s*:\\s*\"%s\".*\"license\"\\s*:\\s*\"([\\w.\\\\-]*)\".*</script>",name), Pattern.MULTILINE).matcher(contents);
                            if(m.find()) {
                                r = m.group(1).trim();
                                LicenseCollection licenses = new LicenseCollection();
                                licenses.addConcludedLicenseString(r);
                                this.builder.setLicenses(licenses);
                            }
                        }
                    }
                    // TODO Add CPEs and PURLS
                });
            }
            queryURLs(this.queryWorkers);
            // Add ParserComponent to components
            components.add(builder);
            log(LOG_TYPE.DEBUG, String.format("New Component: %s", getName(builder)));
        }
    }

    /**
     * Parse the fileContents based on the given regex to form key-value pairs,
     * then call parseData() for further process
     * @param components list of Components to add to
     * @param fileContents the contents of the file to be parsed
     */
    @Override
    public void parse(List<SVIPComponentBuilder> components, String fileContents) {
        // Init main data structure
        final LinkedHashMap<String, Object> data = new LinkedHashMap<>();

        fileContents = removeComments(fileContents);

        // Init main Matcher for conanfile.txt(default)   // Regex101: https://regex101.com/r/p5wIc9/1
        Matcher m = Pattern.compile("(^|\\s*)(\\[[\\w]*\\])\\s*(((?!.*\\[([\\w]*)\\]).*\\s*)*)", Pattern.MULTILINE).matcher(fileContents);

        //Check file content to see if it is a conanfile.py
        if (fileContents.contains(" requirements(self") && fileContents.contains("self.requires(")) {
            //System.out.println("conanfile.py content found!");
            //example: https://regex101.com/r/vYtDIV/1
            m = Pattern.compile("(^|\\s*)(def\\s+[\\w]+\\(self.*\\)\\:)\\s*(((?!.*def\\s+[\\w]+\\(self.*\\)\\:).*\\s*)*)",Pattern.MULTILINE)
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
            key = key.replaceAll("\\(self.*","(self):");

            // Dependencies will need to be parsed further, so pass raw string
            switch(key) {
                case "[requires]":
                    String[] lines = value.split("\n");
                    // Process line by line
                    for (String line : lines) {
                        final LinkedHashMap<String, String> dep = new LinkedHashMap<>();
                        if(procline(dep,  line) != null) {
                            // Insert value
                            deps.add(dep);
                        }
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
                            String tline = line.trim().replaceAll(".*self\\.requires\\([\"']|[\"']\\).*", "");
                            if(procline(dep,  tline) != null) {
                                // Insert value
                                deps.add(dep);
                            }
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

    /**
     * <p><b>Removing any comments in the given text and returns the text without comments</b></p>
     * <!DOCTYPE html>
     * <html>
     * <head>
     * <style>
     * table {
     *   font-family: arial, sans-serif;
     *   border-collapse: collapse;
     *   width: 100%;
     * }
     *
     * td, th {
     *   border: 1px solid #dddddd;
     *   text-align: left;
     *   padding: 8px;
     * }
     *
     * tr:nth-child(even) {
     *   background-color: #dddddd;
     * }
     * </style>
     * </head>
     * <body>
     * <table>
     *   <tr>
     *     <th>Block comment(Pyrhon)</th>
     *     <th>Cases(... denotes content)</th>
     *     <th>Note</th>
     *   </tr>
     *   <tr>
     *     <td>1</td>
     *     <td> <p>""" or '''</p>
     *          <p>...</p>
     *          <p> """ or '''</p>
     *     </td>
     *     <td>Quotes on different lines</td>
     *   </tr>
     *   <tr>
     *     <td>2</td>
     *     <td>
     *       <p>""" or '''</p>
     *       <p>...   """ or '''</p>
     *     </td>
     *     <td>Quotes on different lines</td>
     *   </tr>
     *   <tr>
     *     <td>3</td>
     *     <td><p>""" or '''   ...   """ or '''</p>
     *     </td>
     *     <td>Quotes on the line</td>
     *   </tr>
     *   <tr>
     *     <th>Line comment</th>
     *     <th>Cases</th>
     *     <th>Note</th>
     *   </tr>
     *   <tr>
     *     <td>A</td>
     *     <td><p>#  ...</p></td>
     *     <td>The comment takes up a whole line</td>
     *   </tr>
     *   <tr>
     *     <td>B</td>
     *     <td><p>...  # ...</p></td>
     *     <td>
     *       <p>The comment takes up a portion
     *         of a line at lease 1 space
     *         between the content and
     *         the # sign(confile.txt)</p>
     *     </td>
     *   </tr>
     * </table>
     *
     * </body>
     * </html>
     * @param text A file content from conanfile.txt or conanfile.py
     * @return The text without comments
     */
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

    /**
     * Split the data {@code line} into key-value pair and store the pair in the param {@code dep} and returned
     * @param dep  Data storage to contain key-value pair to be returned
     * @param line Data source
     * @return The populated {@code dep} data storage, or {@code null} if no valid key in the {@code line} data source
     */
    public LinkedHashMap<String, String> procline(LinkedHashMap<String, String> dep,  String line) {
        final String[] linei = line.trim().split("/");
        if(linei.length > 0 && !linei[0].isEmpty()) {
            dep.put("artifactId", linei[0]);
        }
        else
            return null;

        if(linei.length > 1 ) dep.put("version", linei[1]);

        return dep;
    }
}
