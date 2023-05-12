package org.svip.sbomfactory.generators.parsers.packagemanagers;

import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import org.svip.sbom.model.CPE;
import org.svip.sbom.model.PURL;
import org.svip.sbomfactory.generators.utils.Debug;
import org.svip.sbomfactory.generators.utils.ParserComponent;
import org.svip.sbomfactory.generators.utils.QueryWorker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
     *
     */
    protected NugetParser() {
        super(
                "https://www.nuget.org/packages",
                new XmlFactory(),
                "\\$\\{([^}]*)\\}" // TODO: Token regex
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




       // this.resolveProperties(this.dependencies, new HashMap((metadata data.get("dependencies")).get("dependency"));

        int i = 0;

        for (Object o: metadata.values()
             ) {

            String s = o.toString();

            if(s.contains("frameworkAssembly")){ // treat C# framework assemblies as dependencies

                this.resolveProperties(
                        this.dependencies,


                        new HashMap(((ArrayList<LinkedHashMap<String,String>>) (((LinkedHashMap<?, ?>)o).get("frameworkAssembly")))
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

            } else if (s.contains("dependency")) {

                this.resolveProperties(this.dependencies,

                new HashMap(((ArrayList<LinkedHashMap<String,String>>) (((LinkedHashMap<?, ?>)o).get("dependency")))
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

            }


        }

        PackageManagerParser.buildURLs(components, this, "nuget");

        queryURLs(this.queryWorkers);

    }


}
