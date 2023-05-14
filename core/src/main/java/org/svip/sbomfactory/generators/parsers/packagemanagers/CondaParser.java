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

//        // get dependencies
//        this.resolveProperties(
//                this.dependencies,
//                new HashMap(((LinkedHashMap<String, ArrayList<HashMap<String, String>>>) data.get("dependencies")))
//                        .stream().collect(
//                                Collectors.toMap(
//                                        d -> d.split("=")[0].trim(),
//                                        d -> d,
//                                        (d1, d2) -> {
//                                            return d2;
//                                        }
//                                )
//        );
    }
}
