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
