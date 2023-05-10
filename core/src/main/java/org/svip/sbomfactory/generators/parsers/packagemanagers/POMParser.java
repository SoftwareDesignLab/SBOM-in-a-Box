package org.svip.sbomfactory.generators.parsers.packagemanagers;

import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import org.svip.sbom.model.CPE;
import org.svip.sbomfactory.generators.utils.ParserComponent;
import org.svip.sbomfactory.generators.utils.QueryWorker;
import org.svip.sbom.model.PURL;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        this.resolveProperties((LinkedHashMap<String, String>) data.get("properties"));

        // Get dependencies from data
        this.dependencies = ((LinkedHashMap<String, ArrayList<LinkedHashMap<String, String>>>)
                data.get("dependencies")).get("dependency");

        final LinkedHashMap build = (LinkedHashMap) data.get("build");

        // Get plugins from data
        final ArrayList<LinkedHashMap<String, String>> plugins =
                ((LinkedHashMap<String, ArrayList<LinkedHashMap<String, String>>>) build.get("plugins")).get("plugin");

        // Iterate and build URLs
        for (final LinkedHashMap<String, String> d : this.dependencies) {
            // Format all property keys -> values
            final String groupId = this.resolveProperty(d.get("groupId"), null);
            final String artifactId = this.resolveProperty(d.get("artifactId"), null);
            final String version = this.resolveProperty(d.get("version"), null);

            // Skip dependency if no name
            if(artifactId == null) continue;

            final ParserComponent c = new ParserComponent(artifactId);
            if(groupId != null) c.setGroup(groupId);
            if(version != null) c.setVersion(version);


            // TODO: Find this PURL regex a home (Translator?): https://regex101.com/r/sbFd7Z/2
            //  "^pkg:([^/]+)/([^#\n@]+)(?:@([^\?\n]+))?(?:\?([^#\n]+))?(?:#([^\n]+))?"


            // Build PURL String
            final HashMap<String, String> PURLData = new HashMap<>();
            PURLData.put("type", "maven");
            PURLData.put("name", artifactId);
            if(groupId != null) PURLData.put("namespace", groupId);
            if(version != null) PURLData.put("version", version);
            String PURLString = PackageManagerParser.buildPURL(PURLData);

            // Add built PURL
            c.addPURL(new PURL(PURLString)); // TODO: Use PURL class y/n?
            log(LOG_TYPE.DEBUG, String.format("Dependency Found with PURL: %s", PURLString));

            // Build CPE
            CPE cpe = new CPE("maven", artifactId, version);
            String cpeFormatString = cpe.bindToFS();
            c.addCPE(cpeFormatString);
            log(LOG_TYPE.DEBUG, String.format("Dependency Found with CPE: %s", cpeFormatString));

            // Build URL and worker object
            if(groupId != null) {
                String url = this.STD_LIB_URL;
                url += groupId;
                url += "/" + artifactId;
                if(version != null) url += "/" + version;
                // Create and add QueryWorker with Component reference and URL
                this.queryWorkers.add(new QueryWorker(c, url) {
                    @Override
                    public void run() {
                        // Get page contents
                        final String contents = getUrlContents(queryURL(this.url, false));

                        // Parse license(s)
                        // Regex101: https://regex101.com/r/FUOPSK/1
                        final Matcher m = Pattern.compile("<li data-test=\\\"license\\\">(.*?)</li>", Pattern.MULTILINE).matcher(contents);

                        // Add all found licenses
                        while(m.find()) {
                            this.component.addLicense(m.group(1).trim());
                        }
                    }
                });
            }

            // Add ParserComponent to components
            components.add(c);
        }

        // Query all found URLs and store any relevant data
        queryURLs(this.queryWorkers);
    }

    //#endregion
}
