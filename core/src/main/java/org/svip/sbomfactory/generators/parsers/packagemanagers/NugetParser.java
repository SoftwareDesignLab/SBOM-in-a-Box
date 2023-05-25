package org.svip.sbomfactory.generators.parsers.packagemanagers;

import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import org.svip.sbomfactory.generators.utils.Debug;
import org.svip.sbomfactory.generators.utils.ParserComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

import static org.svip.sbomfactory.generators.utils.Debug.log;

/**
 * file: NugetParser.java
 * Description: Package-manager specific implementation of the PackageManagerParser (.nuspec/.json)
 *
 * @author Juan Francisco Patino
 */
public class NugetParser extends PackageManagerParser{

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
    protected void parseData(ArrayList<ParserComponent> components, HashMap<String, Object> data) {

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
        for (Object o: metadata.values()
             ) {

            String s = o.toString();

            if(s.contains("frameworkAssembly")){ // treat C# framework assemblies as dependencies
                type = "frameworkAssembly";
                try{
                    this.resolveProperties(
                            this.dependencies,


                            new HashMap(((ArrayList<LinkedHashMap<String,String>>) (((LinkedHashMap<?, ?>)o).get(type)))
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
                }catch (ClassCastException c){
                    oneDependency(s, type);
                }


            } else if (s.contains("dependency") && !s.contains("group")) { // no dependency group format
                type = "dependency";
                /*
                https://learn.microsoft.com/en-us/nuget/reference/nuspec
                The group format cannot be intermixed with a flat list.
                 */

                try{
                    this.resolveProperties(this.dependencies,

                            new HashMap(((ArrayList<LinkedHashMap<String,String>>) (((LinkedHashMap<?, ?>)o).get(type)))
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
                }catch(ClassCastException e){ // only one dependency

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
     * @param s Metadata string
     * @param type Dependency type
     */
    private void oneDependency(String s, String type) {

        String idType = "id";
        if(type.equals("frameworkAssembly"))
            idType = "assemblyName";

        int i;
        String[] split = s.split("[=,}]");
        i = 0;
        LinkedHashMap<String, String> id = null;
        for (String elem: split
             ) {

            if(elem.contains(idType)){
                id = new LinkedHashMap<>();
                id.put(idType,split[i+1]);
                dependencies.put(split[i+1], id);
            }
            else if(elem.contains("version")){
                assert(id != null);
                id.put("version", split[i+1]);
                break;
            }
            else if(elem.contains("targetFramework") && type.equals("frameworkAssembly")){
                assert(id != null);
                id.put("targetFramework", split[i+1]);
                break;
            }

            i++;

        }
    }


}
