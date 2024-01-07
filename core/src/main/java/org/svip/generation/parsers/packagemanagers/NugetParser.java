/** Copyright 2021 Rochester Institute of Technology (RIT). Developed with
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
 */

package org.svip.generation.parsers.packagemanagers;

import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import org.svip.sbom.builder.objects.SVIPComponentBuilder;
import org.svip.utils.Debug;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import static org.svip.utils.Debug.log;

/**
 * file: NugetParser.java
 * Description: Package-manager specific implementation of the PackageManagerParser (.nuspec/.json)
 *
 * @author Juan Francisco Patino
 * @author Ian Dunn
 */
public class NugetParser extends PackageManagerParser {

    /**
     * Protected Constructor meant for use by parser implementations
     * to store their package-manager-specific static values in their respective
     * attributes.
     */
    public NugetParser() {
        super(
                "https://www.nuget.org/packages",
                new XmlFactory(),
                "\\$\\{([^}]*)\\}"
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

        /*
                <?xml version="1.0" encoding="utf-8"?>
        <package xmlns="http://schemas.microsoft.com/packaging/2010/07/nuspec.xsd">
            <metadata>
                <!-- Required elements-->
                <id></id>
                <version></version>
                <description></description>
                <authors></authors>

                <!-- Optional elements -->
                <dependencies></dependencies>
                <frameworkAssemblies></frameworkAssemblies>
                <!-- ... -->
            </metadata>
            <!-- Optional 'files' node -->
        </package>
         */

        this.dependencies = new HashMap<>();
        HashMap<String, String> metadata = new HashMap((LinkedHashMap<String, ArrayList<HashMap<String, String>>>) data.get("metadata"));
        String type;
        for (Object o : metadata.values()
        ) {

            String s = o.toString();

            if (s.contains("frameworkAssembly")) { // treat C# framework assemblies as dependencies
                type = "frameworkAssembly";
                try {
                    this.resolveProperties(
                            this.dependencies,


                            new HashMap(((ArrayList<LinkedHashMap<String, String>>) (((LinkedHashMap<?, ?>) o).get(type)))
                                    .stream().collect(
                                            Collectors.toMap(
                                                    d -> d.get("assemblyName"),
                                                    d -> d,
                                                    (d1, d2) -> {
                                                        log(Debug.LOG_TYPE.WARN, String.format("Duplicate key found: %s", d2.get("assemblyName")));
                                                        return d2;
                                                    }
                                            )
                                    )
                            )
                    );
                } catch (ClassCastException c) {
                    oneDependency(s, type);
                }


            } else if (s.contains("dependency") && !s.contains("group")) { // no dependency group format
                type = "dependency";
                /*
                https://learn.microsoft.com/en-us/nuget/reference/nuspec
                The group format cannot be intermixed with a flat list.
                 */

                try {
                    this.resolveProperties(this.dependencies,

                            new HashMap(((ArrayList<LinkedHashMap<String, String>>) (((LinkedHashMap<?, ?>) o).get(type)))
                                    .stream().collect(
                                            Collectors.toMap(
                                                    d -> d.get("id"),
                                                    d -> d,
                                                    (d1, d2) -> {
                                                        log(Debug.LOG_TYPE.WARN, String.format("Duplicate key found: %s", d2.get("id")));
                                                        return d2;
                                                    }
                                            )
                                    )

                            ));
                } catch (ClassCastException e) { // only one dependency

                    oneDependency(s, type);

                }

            }

        }

        PackageManagerParser.buildURLs(components, this, "nuget");

        queryURLs(this.queryWorkers);

    }

    /**
     * Resolves one dependency of either type
     *
     * @param s    Metadata string
     * @param type Dependency type
     */
    private void oneDependency(String s, String type) {

        String idType = "id";
        if (type.equals("frameworkAssembly"))
            idType = "assemblyName";

        int i;
        String[] split = s.split("[=,}]");
        i = 0;
        LinkedHashMap<String, String> id = null;
        for (String elem : split
        ) {

            if (elem.contains(idType)) {
                id = new LinkedHashMap<>();
                id.put(idType, split[i + 1]);
                dependencies.put(split[i + 1], id);
            } else if (elem.contains("version")) {
                assert (id != null);
                id.put("version", split[i + 1]);
                break;
            } else if (elem.contains("targetFramework") && type.equals("frameworkAssembly")) {
                assert (id != null);
                id.put("targetFramework", split[i + 1]);
                break;
            }

            i++;

        }
    }


}
