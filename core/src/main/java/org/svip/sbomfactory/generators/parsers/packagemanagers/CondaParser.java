package org.svip.sbomfactory.generators.parsers.packagemanagers;

import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.svip.sbomfactory.generators.utils.ParserComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class CondaParser extends PackageManagerParser{

    public CondaParser() {
        super(
                "https://anaconda.org/",
                new YAMLFactory(),
                "\\$\\{([^\\n/\\\\]*)\\}" //regex101: https://regex101.com/r/Dy500U/1
        );
    }

    @Override
    protected void parseData(ArrayList<ParserComponent> components, HashMap<String, Object> data) {
        // Init properties
        this.properties = new HashMap<>();

        // Init dependencies
        this.dependencies = new HashMap<>();

        // get dependencies
        HashMap<String, LinkedHashMap<String, String>> deps = new HashMap<>();

        //attempt the dangerous cast
        ArrayList<String> rawDependencies;
        try{
            rawDependencies = (ArrayList<String>) data.get("dependencies");
        } catch (Exception e){
            System.err.println("Error: Could not cast dependencies to ArrayList<String>");
            return;
        }

        for (Object unknown : rawDependencies){
            LinkedHashMap<String, String> newDependecy = new LinkedHashMap<>();

            //cast to the correct data type and process. Need to handle hashmap sections, and single line strings

            //Basic dependencies, simply under the dependencies tag
            // EG.
            // dependencies:
            //   - python=3.6
            //   - numpy=1.13.1
            try{
                String line = (String) unknown;
                //split the line into the name and version
                String[] temp = line.split("=");

                String name = temp[0];
                newDependecy.put("artifactId", name);

                if (temp.length > 2) {
                    String version = temp[2];
                    newDependecy.put("version", version);
                }

                this.dependencies.put(name, newDependecy);
            } catch (ClassCastException e){
                ; //perfectly normal, this means it is another datatype
            } catch (Exception e){
                System.err.println("Error: Could not parse dependency: " + unknown);
                e.printStackTrace();
            }

/* todo
    fix bug where loop variables are not cleared for some reason
    split up git requests to pull out the version data
 */
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

                for (String line : section.get(sectionName)){
                    String[] temp = line.split("==");

                    String name = temp[0];
                    newDependecy.put("artifactId", name);

                    if (temp.length > 1) {
                        String version = temp[1];
                        newDependecy.put("version", version);
                    }

                    //not so sure about these. Please give feedback
                    newDependecy.put("from", sectionName);
                    newDependecy.put("groupId", sectionName);

                    this.dependencies.put(sectionName + ':' + name, newDependecy);
                }
            } catch (ClassCastException e){
                ; //perfectly normal, this means it is another datatype
            } catch (Exception e){
                System.err.println("Error: Could not parse dependency: " + unknown);
                e.printStackTrace();
            }

            if (newDependecy.isEmpty()){
                //not normal, because this last check this means we couldn't find the type
                System.err.println("Error: Could not parse dependency: " + unknown);
            }
        }
    }
}
