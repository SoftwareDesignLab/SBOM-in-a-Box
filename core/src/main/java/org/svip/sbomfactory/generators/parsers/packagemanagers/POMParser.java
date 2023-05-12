package org.svip.sbomfactory.generators.parsers.packagemanagers;

import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import org.svip.sbom.model.CPE;
import org.svip.sbomfactory.generators.utils.ParserComponent;
import org.svip.sbomfactory.generators.utils.QueryWorker;
import org.svip.sbom.model.PURL;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.svip.sbomfactory.generators.utils.Debug.*;

/**
 * file: POMParser.java
 * Description: Package-manager specific implementation of the PackageManagerParser (Maven/pom.xml)
 *
 * @author Dylan Mulligan
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
    protected void parseData(ArrayList<ParserComponent> components, HashMap<String, Object> data) {
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
