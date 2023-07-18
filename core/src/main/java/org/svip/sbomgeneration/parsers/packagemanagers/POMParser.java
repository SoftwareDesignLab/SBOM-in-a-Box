package org.svip.sbomgeneration.parsers.packagemanagers;

import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import org.svip.sbom.builder.objects.SVIPComponentBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import static org.svip.utils.Debug.LOG_TYPE;
import static org.svip.utils.Debug.log;

/**
 * file: POMParser.java
 * Description: Package-manager specific implementation of the PackageManagerParser (Maven/pom.xml)
 *
 * @author Dylan Mulligan
 * @author Ian Dunn
 */
public class POMParser extends PackageManagerParser {
    //#region Constructors

    public POMParser() {
        super(
                "https://central.sonatype.com/artifact/",
                new XmlFactory(),
                "\\$\\{([^}]*)\\}" // Regex101: https://regex101.com/r/gIluSW/1
        );
    }

    //#endregion

    //#region Core Methods

    @Override
    protected void parseData(List<SVIPComponentBuilder> components, HashMap<String, Object> data) {
        // Init properties
        this.properties = new HashMap<>();

        // Resolve nested properties (e.x. "<maven.compiler.source>${java.version}</maven.compiler.source>")
        this.resolveProperties(
                this.properties,
                (HashMap<String, String>) data.get("properties")
        );

        // Init dependencies
        this.dependencies = new HashMap<>();

        // Get dependencies from data
        this.resolveProperties(
                this.dependencies,
                new HashMap(((LinkedHashMap<String, ArrayList<HashMap<String, String>>>) data.get("dependencies"))
                        .get("dependency")
                        .stream().collect(
                                Collectors.toMap(
                                        d -> d.get("artifactId"),
                                        d -> d,
                                        (d1, d2) -> {
                                            log(LOG_TYPE.WARN, String.format("Duplicate key found: %s", d2.get("artifactId")));
                                            return d2;
                                        }
                                )
                        )
                )
        );

        final LinkedHashMap build = (LinkedHashMap) data.get("build");

        // Get plugins from data
        final ArrayList<LinkedHashMap<String, String>> plugins =
                ((LinkedHashMap<String, ArrayList<LinkedHashMap<String, String>>>) build.get("plugins")).get("plugin");

        PackageManagerParser.buildURLs(components, this, "maven");
        queryURLs(this.queryWorkers);
    }

    //#endregion
}
