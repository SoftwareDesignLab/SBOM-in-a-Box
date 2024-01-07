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

import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.svip.sbom.builder.objects.SVIPComponentBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * File: CondaParser
 * Package-manager specific implementation of the PackageManagerParser using Conda.
 *
 * @author Asa Horn ?
 * @author Ian Dunn
 */
public class CondaParser extends PackageManagerParser {

    public CondaParser() {
        super(
                "https://anaconda.org/",
                new YAMLFactory(),
                "\\$\\{([^\\n/\\\\]*)\\}" //regex101: https://regex101.com/r/Dy500U/1
        );
    }

    @Override
    protected void parseData(List<SVIPComponentBuilder> components, HashMap<String, Object> data) {
        // Init properties
        this.properties = new HashMap<>();

        // get properties from channels
        if (data.containsKey("channels")) {
            ArrayList<String> rawChannels;
            try {
                rawChannels = (ArrayList<String>) data.get("channels");
            } catch (Exception e) {
                System.err.println("Error: Could not cast channels to ArrayList<String>");
                return;
            }
            //add all found channels as properties
            for (int i = 0; i < rawChannels.size(); i++) {
                this.properties.put("source" + i, rawChannels.get(i));
            }
        }

        // get properties from variables
        ArrayList<HashMap<String, String>> rawVariables;
        if (data.containsKey("variables")) {
            try {
                rawVariables = (ArrayList<HashMap<String, String>>) data.get("variables");
            } catch (Exception e) {
                System.err.println("Error: Could not cast channels to ArrayList<String>");
                return;
            }
            //add all found variables as properties
            for (HashMap<String, String> line : rawVariables) {
                String key = line.keySet().iterator().next();
                this.properties.put(key, line.get(key));
            }
        }

        // Init dependencies
        this.dependencies = new HashMap<>();

        //attempt the dangerous cast
        ArrayList<String> rawDependencies;
        try {
            rawDependencies = (ArrayList<String>) data.get("dependencies");
        } catch (Exception e) {
            System.err.println("Error: Could not cast dependencies to ArrayList<String>");
            return;
        }

        for (Object unknown : rawDependencies) {
            //cast to the correct data type and process. Need to handle hashmap sections, and single line strings
            boolean dependencyFoundFlag = false;

            //Basic dependencies, simply under the dependencies tag
            // EG.
            // dependencies:
            //   - python=3.6
            //   - numpy=1.13.1
            try {
                String line = (String) unknown;

                LinkedHashMap<String, String> newDependecy = new LinkedHashMap<>();

                //split the line into the name and version
                String[] temp = line.split("=");

                String name = temp[0];
                newDependecy.put("artifactId", name);

                if (temp.length > 2) {
                    String version = temp[2];
                    newDependecy.put("version", version);
                }

                this.dependencies.put(name, newDependecy);
                dependencyFoundFlag = true;
            } catch (ClassCastException e) {
                //perfectly normal, this means it is another datatype
            } catch (Exception e) {
                System.err.println("Error: Could not parse dependency: " + unknown);
                e.printStackTrace();
            }

            //PIP dependencies, under the pip tag in the dependencies tag, or other tags defined like this (if they exist)
            // EG.
            // dependencies:
            //   - python=3.6 # not this
            //   - pip:
            //     - numpy==1.13.1 #this
            //     - scipy==0.19.1 #this
            try {
                LinkedHashMap<String, ArrayList<String>> section = (LinkedHashMap<String, ArrayList<String>>) unknown;
                String sectionName = section.keySet().iterator().next();
                dependencyFoundFlag = true;

                for (String line : section.get(sectionName)) {
                    LinkedHashMap<String, String> newDependecy = new LinkedHashMap<>();
                    String[] temp;

                    //handle git links
                    if (line.startsWith("git+")) {
                        temp = line.split("@");
                        temp[0] = temp[0].substring(4, temp[0].length() - 4); //remove git+ and .git from string
                    }

                    //handle raw lines
                    else {
                        temp = line.split("==");
                    }

                    String name = temp[0];
                    newDependecy.put("artifactId", name);

                    if (temp.length > 1) {
                        String version = temp[1];
                        newDependecy.put("version", version);
                    }

                    this.dependencies.put(sectionName + ':' + name, newDependecy);
                }
            } catch (ClassCastException e) {
                //perfectly normal, this means it is another datatype
            } catch (Exception e) {
                System.err.println("Error: Could not parse dependency: " + unknown);
                e.printStackTrace();
            }

            if (!dependencyFoundFlag) {
                //not normal, because this last check this means we couldn't find the type
                System.err.println("Error: Could not parse dependency: " + unknown);
            }
        }
    }
}
