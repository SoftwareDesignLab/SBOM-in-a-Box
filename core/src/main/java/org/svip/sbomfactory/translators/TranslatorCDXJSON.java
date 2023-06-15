package org.svip.sbomfactory.translators;

import org.cyclonedx.exception.ParseException;
import org.cyclonedx.model.Bom;
import org.cyclonedx.model.Dependency;
import org.cyclonedx.model.Metadata;
import org.cyclonedx.model.OrganizationalContact;
import org.cyclonedx.model.Tool;
import org.cyclonedx.parsers.JsonParser;
import org.svip.sbom.model.AppTool;
import org.svip.sbom.model.Component;
import org.svip.sbom.model.SBOM;
import org.svip.sbom.model.uids.Hash;
import org.svip.sbomfactory.generators.utils.Debug;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


/**
 * file: TranslatorCDXJSON.java
 * Coverts SPDX SBOMs into internal SBOM objects.
 * Compatible with CDX 1.4 JSON SBOMs
 *
 * @author Tyler Drake
 * @author Ethan Numan
 */
public class TranslatorCDXJSON extends TranslatorCore {

    public static final String PLUGFEST_UID = "org.nvip.plugfest.tooling.sbom.uids";
    public TranslatorCDXJSON() {
        super("json");
    }

    /**
     * Converts a file into an internal SBOM object
     *
     * @param fileContents String of file contents
     * @param file_path Path to file
     * @return internal SBOM object
     */
    @Override
    protected SBOM translateContents(String fileContents, String file_path) throws TranslatorException {

        // Initialize JSON Parser
        JsonParser parser = new JsonParser();

        // Use JSON Parser to parse cdx.json file and store into cyclonedx Bom Object
        Bom json_sbom;
        try {
            json_sbom = parser.parse(fileContents.getBytes());
        } catch (ParseException e) {
            Debug.log(Debug.LOG_TYPE.EXCEPTION, e);
            throw new TranslatorException("Could not parse file.");
        }

        if (json_sbom.getBomFormat() == null) { // This is NOT a CDX SBOM
            throw new TranslatorException("Could not parse file. JSON SBOM is not CycloneDX.");
        }

        // TODO these are essential fields, throw an actual error if any of these are null
        bom_data.put("format", json_sbom.getBomFormat());
        bom_data.put("specVersion", json_sbom.getSpecVersion());
        bom_data.put("sbomVersion", String.valueOf(json_sbom.getVersion()));
        bom_data.put("serialNumber", json_sbom.getSerialNumber());

        String[] authorAndTimestamp = new String[2];

        // Ensure metadata is not null before we begin querying it
        Metadata metadata = json_sbom.getMetadata();
        if(metadata != null) {

            StringBuilder authorBuilder = new StringBuilder();
            if(json_sbom.getMetadata().getAuthors() != null) {
                for(OrganizationalContact publisher: json_sbom.getMetadata().getAuthors()) {
                    if(!authorBuilder.toString().equals("")) { authorBuilder.append(", "); }
                    authorBuilder.append("[");
                    authorBuilder.append(publisher.getName() + ", ");
                    authorBuilder.append(publisher.getEmail());
                    authorBuilder.append("]");
                }
                String authors = authorBuilder.toString();
                bom_data.put("author", authors);
                authorAndTimestamp[0] = authors ;
            }

            Date timestamp = json_sbom.getMetadata().getTimestamp();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss:SSS'Z'");
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            bom_data.put("timestamp" , format.format(timestamp));
            authorAndTimestamp[1] = "[" + timestamp + "]";

            // Top component analysis (check if not null as well)
            org.cyclonedx.model.Component topComponent = metadata.getComponent();
            if(topComponent != null) {
                product_data.put("name", topComponent.getName());
                product_data.put("publisher", topComponent.getPublisher());
                product_data.put("version", topComponent.getVersion());
                product_data.put("id", topComponent.getBomRef());
            }
        }

        this.createSBOM();

        if(metadata != null) {
            Set<AppTool> tools = new HashSet<>();
            for (Tool t: metadata.getTools())
                tools.add(new AppTool(t.getVendor(),t.getName(),t.getVersion()));

            sbom.setAppTools(tools);

            if (authorAndTimestamp[0] != null) {
                sbom.addMetadata("author", authorAndTimestamp[0]);
                sbom.setSupplier(authorAndTimestamp[0]);
            }

            if (sbom.getSupplier() == null && tools.size() > 0)
                sbom.setSupplier(tools.iterator().next().toString());

            sbom.addMetadata("timestamp", authorAndTimestamp[1]);
        }

        // Create new collection of components
        HashMap<String, Component> components = new HashMap<>();

        // Loop through all components in cyclonedx component list
        for(org.cyclonedx.model.Component cdx_component : json_sbom.getComponents()) {

            if( cdx_component != null ) {

                if(cdx_component.getType() == org.cyclonedx.model.Component.Type.APPLICATION){
                    sbom.addMetadata("tool","[Tool - " + cdx_component.getAuthor() + " " + // treat author as apptool vendor
                            cdx_component.getName() + " " + cdx_component.getVersion() + "]");
                    continue;
                }

                // Create new component with a name, publisher, version along with CPEs/PURLs/SWIDs
                Component new_component = new Component(
                        cdx_component.getName(),
                        cdx_component.getPublisher(),
                        cdx_component.getVersion());

                // Get group
                String group = cdx_component.getGroup();
                if (group != null) new_component.setGroup(group);

                // Get CPE, PURL, and SWIDs
                String cpe = cdx_component.getCpe();
                if (cpe != null) new_component.setCpes(Collections.singleton(cpe));

                String purl = cdx_component.getPurl();
                if (purl != null) new_component.setPurls(Collections.singleton(purl));

                String swid = String.valueOf(cdx_component.getSwid());
                if (swid != null) new_component.setSwids(Collections.singleton(swid));

                // get Hashes
                List<org.cyclonedx.model.Hash> raw_hashes = cdx_component.getHashes();
                if (raw_hashes != null) {
                    Set<Hash> hashes = new HashSet<>();
                    for (org.cyclonedx.model.Hash temp: raw_hashes){
                        Hash newHash = new Hash(temp.getAlgorithm(),temp.getValue());
                        hashes.add(newHash);
                    }
                    new_component.setHashes(hashes);
                }

                // Attempt to get licenses. If no licenses found put out error message and continue.
                try {
                    if(cdx_component.getLicenseChoice().getExpression() != null)
                        new_component.setLicenses(new HashSet<>(Arrays.asList(cdx_component.getLicenseChoice().getExpression())));
                } catch (NullPointerException e) {
                    // Getting a NullPointerException on licenses is fine. It just means the component had none.
                } catch (Exception e) {
                    // This may be an actual error
                    throw new TranslatorException("An error occurred while getting licenses: " + e.getMessage());
                    //                    e.printStackTrace();
                }

                // Set the component's unique ID
                new_component.setUniqueID(cdx_component.getBomRef());

                // Add component to component list
                this.loadComponent(new_component);

                // If a top component doesn't exist, make this new component the top component
                this.topComponent = topComponent == null ? new_component : topComponent;

            }

        }

        // Add the top component to the sbom
        if(this.sbom.getAllComponents().size() == 0) { this.sbom.addComponent(null, topComponent); }

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
            Debug.log(Debug.LOG_TYPE.WARN, "Could not find dependencies from CycloneDX Object. " +
                    "Defaulting all components to point to head component. File: " + file_path);
            dependencies.put(
                    this.topComponent.getUniqueID(),
                    components.values().stream().map(x->x.getUniqueID()).collect(Collectors.toCollection(ArrayList::new))
            );
        }

        // If the dependency list isn't empty, call dependencyBuilder to construct dependencyTree
        // Otherwise, default the dependencyTree by adding all subcomponents as children to the top component
        if( dependencies != null ) {
            try {
                this.dependencyBuilder(components, this.topComponent, null);
            } catch (Exception e) {
                Debug.log(Debug.LOG_TYPE.WARN, "Error building dependency tree. Dependency tree may be incomplete " +
                        "for: " + file_path);
            }
        }

        this.defaultDependencies(this.topComponent);

        return this.sbom;

    }

}