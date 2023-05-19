package org.svip.sbomfactory.translators;

import org.cyclonedx.exception.ParseException;
import org.cyclonedx.model.Bom;
import org.cyclonedx.model.BomReference;
import org.cyclonedx.model.Dependency;
import org.cyclonedx.model.Hash;
import org.cyclonedx.parsers.JsonParser;
import org.svip.sbom.model.Component;
import org.svip.sbom.model.PURL;
import org.svip.sbom.model.SBOM;
import org.svip.sbomfactory.generators.generators.SBOMGenerator;
import org.svip.sbomfactory.generators.generators.utils.GeneratorException;
import org.svip.sbomfactory.generators.generators.utils.License;
import org.svip.sbomfactory.generators.generators.utils.Tool;
import org.svip.sbomfactory.generators.utils.Debug;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * file: TranslatorCDXJSON.java
 * Coverts SPDX SBOMs into internal SBOM objects.
 * Compatible with CDX 1.4 JSON SBOMs
 *
 * @author Tyler Drake
 */
public class TranslatorCDXJSON extends TranslatorCore {
    public TranslatorCDXJSON() {
        super("json");
    }

    /**
     * Converts a file into an internal SBOM object
     *
     * @param fileContents String of file contents
     * @param file_path Path to file
     * @return internal SBOM object
     * @throws ParseException If file is not valid JSON
     */
    @Override
    protected SBOM translateContents(String fileContents, String file_path) throws ParseException {

        // Initialize JSON Parser
        JsonParser parser = new JsonParser();

        // Use JSON Parser to parse cdx.json file and store into cyclonedx Bom Object
        Bom json_sbom = parser.parse(fileContents.getBytes());

        bom_data.put("format", json_sbom.getBomFormat());
        bom_data.put("specVersion", json_sbom.getSpecVersion());
        bom_data.put("sbomVersion", String.valueOf(json_sbom.getVersion()));

        if(json_sbom.getMetadata().getAuthors() == null) {
            org.cyclonedx.model.Tool sbomTool = json_sbom.getMetadata().getTools().get(0);
            Tool tool = new Tool(sbomTool.getVendor(), sbomTool.getName(), sbomTool.getVersion());

            // TODO this may not be necessary
//            try {
//                tool.addHash(sbomTool.getHashes().get(0).getValue(), sbomTool.getHashes().get(0).getAlgorithm());
//            } catch (GeneratorException e) {
//                Debug.log(Debug.LOG_TYPE.ERROR, "Error adding hash to SBOM tool");
//            }
//
//            // If this SBOM is generated by our tool, apply the top component license to the tool
//            if(json_sbom.getMetadata().getComponent().getLicenseChoice() != null
//                    && tool.getName().equals(SBOMGenerator.TOOL_NAME)) {
//                for(org.cyclonedx.model.License license : json_sbom.getMetadata().getComponent().getLicenseChoice().getLicenses())
//                    tool.addLicense(new License(license.getId()));
//            }

            bom_data.put("author", tool.getToolInfo());
        } else {
            // TODO do the same thing as tool above, currently returns empty sbom
            bom_data.put("author", json_sbom.getMetadata().getAuthors().toString());
        }

        bom_data.put("serialNumber", json_sbom.getSerialNumber());

        Date timestamp = json_sbom.getMetadata().getTimestamp();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        bom_data.put("timestamp" , format.format(timestamp));

        org.cyclonedx.model.Component top_component_meta = json_sbom.getMetadata().getComponent();

        product_data.put("name", top_component_meta.getName());
        product_data.put("publisher", top_component_meta.getPublisher());
        product_data.put("version", top_component_meta.getVersion());
        product_data.put("id", top_component_meta.getBomRef());

        this.createSBOM();

        // Create new collection of components
        HashMap<String, Component> components = new HashMap<>();

        // Loop through all components in cyclonedx component list
        for(org.cyclonedx.model.Component cdx_component : json_sbom.getComponents()) {

            if( cdx_component != null ) {

                // Get CPE, PURL, and SWIDs
                String cpe = cdx_component.getCpe() == null ? null : cdx_component.getCpe();
                PURL purl = cdx_component.getPurl() == null ? null : new PURL(cdx_component.getPurl());
                String swid = cdx_component.getSwid() == null ? null : String.valueOf(cdx_component.getSwid());

                // Create new component with a name, publisher, version along with CPEs/PURLs/SWIDs
                Component new_component = new Component(
                        cdx_component.getName(),
                        cdx_component.getPublisher(),
                        cdx_component.getVersion(),
                        Collections.singleton(cpe),
                        Collections.singleton(purl),
                        Collections.singleton(swid)
                );

                // Attempt to get licenses. If no licenses found put out error message and continue.
                try {
                    new_component.setLicenses(new HashSet<>(Arrays.asList(cdx_component.getLicenseChoice().getExpression())));
                } catch (NullPointerException e) {
                    // Getting a NullPointerException on licenses is fine. It just means the component had none.
                } catch (Exception e) {
                    // This may be an actual error
                    System.err.println("An error occurred while getting licenses: \n");
                    e.printStackTrace();
                }

                // Set the component's unique ID
                new_component.setUniqueID(cdx_component.getBomRef());

                // Add component to component list
                components.put(new_component.getUniqueID(), new_component);

                // If a top component doesn't exist, make this new component the top component
                this.product = product == null ? new_component : product;

            }

        }

        // Add the top component to the sbom
        if(this.sbom.getAllComponents().size() == 0) { this.sbom.addComponent(null, product); }

        // Create dependency collection
        //Map<String, List<String>> dependencies;
        try {
            // Attempt to get all dependencies from CycloneDX Object
            dependencies = json_sbom.getDependencies()
                    .stream()
                    .collect(
                            Collectors.toMap(
                                    Dependency::getRef,
                                    x -> {
                                        // Returns dependencies as strings
                                        return x.getDependencies().stream().map(
                                                y -> y.getRef()).collect(
                                                        Collectors.toCollection(ArrayList::new));
                                    },
                                    (x,y) -> y,
                                    HashMap::new
                            )
                    );
        } catch (NullPointerException nullPointerException) {
            // If dependencies fail, default
            System.err.println("Could not find dependencies from CycloneDX Object. " +
                    "Defaulting all components to point to head component. File: " + file_path);
            dependencies.put(
                    this.product.getUniqueID(),
                    components.values().stream().map(x->x.getUniqueID()).collect(Collectors.toCollection(ArrayList::new))
            );
        }

        // If the dependency list isn't empty, call dependencyBuilder to construct dependencyTree
        // Otherwise, default the dependencyTree by adding all subcomponents as children to the top component
        if( dependencies != null ) {
            try {
                this.dependencyBuilder(components, this.product, null);
            } catch (Exception e) {
                System.out.println("Error building dependency tree. Dependency tree may be incomplete for: " + file_path);
            }
        }

        this.defaultDependencies(components, this.product);

        return this.sbom;

    }

}
