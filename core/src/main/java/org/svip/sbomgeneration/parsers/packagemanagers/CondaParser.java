package org.svip.sbomgeneration.parsers.packagemanagers;

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
public class CondaParser extends PackageManagerParser{

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
        if(data.containsKey("channels")){
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
        if(data.containsKey("variables")) {
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
        try{
            rawDependencies = (ArrayList<String>) data.get("dependencies");
        } catch (Exception e){
            System.err.println("Error: Could not cast dependencies to ArrayList<String>");
            return;
        }

        for (Object unknown : rawDependencies){
            //cast to the correct data type and process. Need to handle hashmap sections, and single line strings
            boolean dependencyFoundFlag = false;

            //Basic dependencies, simply under the dependencies tag
            // EG.
            // dependencies:
            //   - python=3.6
            //   - numpy=1.13.1
            try{
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
            } catch (ClassCastException e){
                ; //perfectly normal, this means it is another datatype
            } catch (Exception e){
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

                for (String line : section.get(sectionName)){
                    LinkedHashMap<String, String> newDependecy = new LinkedHashMap<>();
                    String[] temp;

                    //handle git links
                    if(line.substring(0, 4).equals("git+")){
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
            } catch (ClassCastException e){
                ; //perfectly normal, this means it is another datatype
            } catch (Exception e){
                System.err.println("Error: Could not parse dependency: " + unknown);
                e.printStackTrace();
            }

            if (!dependencyFoundFlag){
                //not normal, because this last check this means we couldn't find the type
                System.err.println("Error: Could not parse dependency: " + unknown);
            }
        }
    }
}
