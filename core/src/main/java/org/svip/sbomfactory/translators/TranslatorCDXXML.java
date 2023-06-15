package org.svip.sbomfactory.translators;

import org.svip.sbom.model.AppTool;
import org.svip.sbom.model.Component;
import org.svip.sbom.model.SBOM;
import org.svip.sbom.model.uids.Hash;
import org.svip.sbomfactory.generators.utils.Debug;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * file: TranslatorCDXXML.java
 * Coverts CycloneDX SBOMs into internal SBOM objects
 *
 * @author Tyler Drake
 */
public class TranslatorCDXXML extends TranslatorCore {
    public TranslatorCDXXML() {
        super("xml");
    }

    /**
     * Translates a CycloneDX XML file into an SBOM object from the contents of an SBOM
     *
     * @param contents String contents of the SBOM file
     * @param file_path String path to the SBOM file
     * @return SBOM object
     * @throws ParserConfigurationException if the DocumentBuilder cannot be created
     */
    @Override
    protected SBOM translateContents(String contents, String file_path) throws TranslatorException {
        // Top level SBOM materials
        HashMap<String, String> header_materials = new HashMap<>();

        // Initialize Document Builder
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setIgnoringElementContentWhitespace(true);

        DocumentBuilder documentBuilder;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new TranslatorException(e.getMessage());
        }

        // Get parsed XML SBOM file and normalize
        Document sbom_xml_file;

        try {
            sbom_xml_file = documentBuilder.parse(new InputSource(new StringReader(contents)));
        } catch (NullPointerException nullPointerException) {
            Debug.log(Debug.LOG_TYPE.EXCEPTION, nullPointerException);
            throw new TranslatorException("File contents may be null." + nullPointerException.getMessage());
        } catch (SAXException saxException) {
            Debug.log(Debug.LOG_TYPE.EXCEPTION, saxException);
            throw new TranslatorException("File must be a properly formatted CycloneDX XML file: " + saxException.getMessage());
        } catch (IOException ioException) {
            Debug.log(Debug.LOG_TYPE.EXCEPTION, ioException);
            throw new TranslatorException("File information could not be found: " + ioException.getMessage());
        } catch (Exception e) {
            Debug.log(Debug.LOG_TYPE.ERROR, "Issue detected with file: " + file_path);
            Debug.log(Debug.LOG_TYPE.EXCEPTION, e);
            throw new TranslatorException("Issue detected with file.");
        }

        sbom_xml_file.getDocumentElement().normalize();

        // SBOM collections
        NamedNodeMap sbomHead;
        NodeList sbomMeta;
        NodeList sbomComp;
        NodeList sbomDependencies;
        NodeList appTools;

        // Get SBOM Metadata and Components
        try {
            sbomHead = sbom_xml_file.getElementsByTagName("bom").item(0).getAttributes();
        } catch (Exception e) {
            throw new TranslatorException("Invalid format, 'bom' not found in: " + file_path);
        }

        try {
            sbomMeta = ((Element) (sbom_xml_file.getElementsByTagName("metadata")).item(0)).getElementsByTagName("*");
        } catch (Exception e) {
            Debug.log(Debug.LOG_TYPE.WARN, "'metadata' not found in: " + file_path);
            sbomMeta = null;
        }

        try {
            sbomComp = ((Element) (sbom_xml_file.getElementsByTagName("components")).item(0)).getElementsByTagName("component");
        } catch (Exception e) {
            Debug.log(Debug.LOG_TYPE.WARN, "No components found. If this is not intended, please check file " +
                    "format. File: " + file_path);
            sbomComp = null;
        }

        try {
            sbomDependencies = ((Element) (sbom_xml_file.getElementsByTagName("dependencies")).item(0)).getElementsByTagName("dependency");
        } catch (Exception e) {
            Debug.log(Debug.LOG_TYPE.WARN, "No dependencies found. Dependency Tree may not build correctly. " +
                    "File: " + file_path);
            sbomDependencies = null;
        }

        try {
            appTools = ((Element) (sbom_xml_file.getElementsByTagName("tools")).item(0)).getElementsByTagName("tool");
        } catch (Exception e) {
            Debug.log(Debug.LOG_TYPE.WARN, "No tools found yet. Components with no author will be assumed as tools. " +
                    "File: " + file_path);
            appTools = null;
        }

        // Get important SBOM items from header (schema, serial, version)
        for (int a = 0; a < sbomHead.getLength(); a++) {
            header_materials.put(
                    sbomHead.item(a).getNodeName(),
                    sbomHead.item(a).getTextContent()
            );
        }

        // Get important SBOM items from meta  (timestamp, tool info)
        Map<String, String> resolvedMetadata = resolveMetadata(sbomMeta);
        Pattern specVersionPattern = Pattern.compile(".*/(\\d+\\.\\d+)");
        Matcher specVersionMathcher = specVersionPattern.matcher(header_materials.get("xmlns"));
        if(specVersionMathcher.matches()) {
            bom_data.put("specVersion", specVersionMathcher.group(1));
        }
        else{
            Debug.log(Debug.LOG_TYPE.WARN, "Invalid specVersion format.");
        }
        bom_data.put("format", "cyclonedx");
        bom_data.put("sbomVersion", header_materials.get("version"));
        bom_data.put("serialNumber", header_materials.get("serialNumber"));

        /*
         * Cycle through all components and correctly attach them to Java SBOM object
         */
        // Iterate through each component
        if (sbomComp!=null) {

            for (int i = 0; i < sbomComp.getLength(); i++) {

                // Next component
                Node compItem = sbomComp.item(i);

                // If the next node is an element node
                if (compItem.getNodeType() == Node.ELEMENT_NODE) {

                    // Temporary storage for component elements
                    HashMap<String, String> component_items = new HashMap<>();
                    HashSet<String> component_licenses = new HashSet<>();


                    // If component has attributes
                    if (compItem.hasAttributes()) {

                        NamedNodeMap compAttributes = compItem.getAttributes();

                        // Cycle through each attribute node for that component node
                        for (int z = 0; z < compAttributes.getLength(); z++) {


                            // If package id is found, set it as the component's identifier
                            if (compAttributes.item(z).getNodeName().equalsIgnoreCase("bom-ref")) {
                                component_items.put("bom-ref", compAttributes.item(z).getTextContent().replaceAll("@", ""));
                            }

                        }

                        // Add the information to the component
                    }

                    // Get all elements from that node
                    Element elem = (Element) compItem;
                    NodeList component_elements = elem.getElementsByTagName("*");

                    Set<String> purls = new HashSet<>();
                    Set<String> cpes = new HashSet<>();
                    Set<Hash> hashes = new HashSet<>();

                    // Iterate through each element in that component
                    for (int j = 0; j < component_elements.getLength(); j++) {

                        // If component is a license id put it into the license hashmap, if not, put it in the item hashmap
                        if (component_elements.item(j).getNodeName().equalsIgnoreCase("id")) {
                            component_licenses.add(component_elements.item(j).getTextContent());
                        }
                        // If this is a cpe or purl then we will add it to the component
                        // TODO look into how to best store SWIDs from CDX
                        else if (component_elements.item(j).getNodeName().equalsIgnoreCase("cpe")) {
                            cpes.add(component_elements.item(j).getTextContent());
                        }
                        else if (component_elements.item(j).getNodeName().equalsIgnoreCase("purl")) {
                            purls.add(component_elements.item(j).getTextContent());
                        }
                        else if (component_elements.item(j).getNodeName().equalsIgnoreCase("group")) {
                            component_items.put("group", component_elements.item(j).getTextContent());
                        }
                        else if (component_elements.item(j).getNodeName().equalsIgnoreCase("hash")) {
                            hashes.add(
                                    new Hash(
                                            component_elements.item(j).getAttributes().item(0).getTextContent(),
                                            component_elements.item(j).getTextContent()
                                    )
                            );
                        }
                        else {
                            component_items.put(
                                    component_elements.item(j).getNodeName(),
                                    component_elements.item(j).getTextContent()

                            );
                        }
                    }

                    // No apparent publisher means this is most likely an application tool
                    Component component;
                    if(component_items.containsKey("type") && component_items.get("type").equalsIgnoreCase("application")){
                        AppTool t = new AppTool();
                        t.setName(component_items.get("name"));
                        t.setVersion(component_items.get("version"));
                        sbom.addAppTool(t);
                        continue;}
                    else
                        // Create a new component with required information
                        component = new Component(
                                component_items.get("name"),
                                component_items.get("publisher"),
                                component_items.get("version"),
                                component_items.get("bom-ref")
                        );

                    // Set CPEs, PURLs, and Hashes
                    component.setCpes(cpes);
                    component.setPurls(purls);
                    component.setHashes(hashes);

                    // Set licenses for component
                    component.setLicenses(component_licenses);

                    this.loadComponent(component);

                    // If we don't have any product data to use for a topComponent,
                    // then default this component as the top component
                    this.topComponent = product_data.isEmpty() ? component : null;

                }

            }

        }

        if (this.product_data.isEmpty())
            this.topComponent = new Component(file_path, "Unknown");

        // Create the new SBOM Object with top level data
        this.createSBOM();
        if(resolvedMetadata != null)
            sbom.setMetadata(resolvedMetadata);

        if (sbomDependencies!=null) {

            // Loop through each dependency in the NodeList
            for (int i = 0; i < sbomDependencies.getLength(); i++) {

                // Next dependency set
                Node dependItem = sbomDependencies.item(i);

                // If this component in the dependency list has dependencies listed
                if (dependItem.hasChildNodes()) {

                    // Get the name of the parent component
                    String parent = dependItem.getAttributes().getNamedItem("ref").getTextContent().replaceAll("@", "");

                    // New element from parent Node
                    Element elem = (Element) dependItem;

                    // Get all children nodes
                    NodeList children = elem.getElementsByTagName("*");

                    // For each child node, add it to the Multimap with the parent as key
                    for (int m = 0 ; m < children.getLength() ; m++) {
                        addDependency(
                                parent,
                                children.item(m).getAttributes().item(0).getTextContent().replaceAll("@", "")
                        );
                    }
                }
            }
        } else {
            dependencies.put(
                    this.topComponent.getUniqueID(),
                    components.values().stream().map(x->x.getUniqueID()).collect(Collectors.toCollection(ArrayList::new))
            );
        }

        if(appTools != null){
            for(int i = 0; i < appTools.getLength(); i++){
                Node tool = appTools.item(i);
                // Get all elements from that node
                Element elem = (Element) tool;
                NodeList component_elements = elem.getElementsByTagName("*");

                AppTool t = new AppTool();

                // Iterate through each element in that component
                for (int j = 0; j < component_elements.getLength(); j++) {

                    if (component_elements.item(j).getNodeName().equalsIgnoreCase("vendor")) {
                        t.setVendor(component_elements.item(j).getTextContent());
                    }
                    else if (component_elements.item(j).getNodeName().equalsIgnoreCase("name")) {
                        t.setName(component_elements.item(j).getTextContent());
                    }
                    else if (component_elements.item(j).getNodeName().equalsIgnoreCase("version")) {
                        t.setVersion(component_elements.item(j).getTextContent());
                    }
                }

                sbom.addAppTool(t);

            }
        }

        // Create the top level component
        // Build the dependency tree using dependencyBuilder
        try {
            dependencyBuilder(components, this.topComponent,null);
        } catch (Exception e) {
            Debug.log(Debug.LOG_TYPE.WARN, "Error processing dependency tree.");
        }

        try {
            defaultDependencies(this.topComponent);
        } catch (Exception e) {
            Debug.log(Debug.LOG_TYPE.WARN, "Something went wrong with defaulting dependencies. A dependency tree may" +
                    " not exist.");
        }

        // Return complete SBOM object
        return this.sbom;
    }

    private Map<String, String> resolveMetadata(NodeList sbomMeta) {
        if(sbomMeta == null) return null;

        HashMap<String, String> result = new HashMap<>();

        // Collected data
        StringBuilder author = new StringBuilder();
        HashMap<String, String> sbom_materials = new HashMap<>();
        HashMap<String, String> sbom_component = new HashMap<>();

        for (int b = 0; b < sbomMeta.getLength(); b++) {

            if (sbomMeta.item(b).getNodeName().contains("component")) {
                // If component has attributes

                if (sbomMeta.item(b).hasAttributes()) {

                    NamedNodeMap topCompAttributes = sbomMeta.item(b).getAttributes();

                    // Cycle through each attribute node for that component node
                    for (int x = 0; x < topCompAttributes.getLength(); x++) {


                        // If package id is found, set it as the component's identifier
                        if (topCompAttributes.item(x).getNodeName().equalsIgnoreCase("bom-ref")) {
                            sbom_component.put("bom-ref", topCompAttributes.item(x).getTextContent().replaceAll("@", ""));
                        }

                    }

                }
                if(sbomMeta.item(b).hasChildNodes()) {

                    NodeList topCompNodes = sbomMeta.item(b).getChildNodes();

                    for(int y = 0; y < topCompNodes.getLength(); y++) {
                        sbom_component.put(
                                topCompNodes.item(y).getNodeName(),
                                topCompNodes.item(y).getTextContent()
                        );
                    }
                }
            } else if (sbomMeta.item(b).getParentNode().getNodeName().contains("author")) {
                if(!(sbomMeta.item(b).getParentNode().getNodeName().contains("authors"))) {
                    if (!author.toString().equals("")) {
                        author.append(", ");
                    }
                    if (sbomMeta.item(b).getNodeName().contains("name")){
                        author.append("[" + sbomMeta.item(b).getTextContent());
                    }
                    else{
                        author.append(sbomMeta.item(b).getTextContent() + "]");
                    }
                }
            } else {
                sbom_materials.put(
                        sbomMeta.item(b).getNodeName(),
                        sbomMeta.item(b).getTextContent()
                );
                String key = sbomMeta.item(b).getNodeName().replaceAll("\n", "");
                result.put(key, "["+ key + " - " +
                        sbomMeta.item(b).getTextContent().replaceAll("\n", "")+"]");
            }
        }


        // Update data used to construct SBOM

        bom_data.put("author", author.toString().equals("") ? sbom_materials.get("vendor") : author.toString());
        bom_data.put("timestamp", sbom_materials.get("timestamp"));

        if (sbom_component.isEmpty()) return result;

        product_data.put("name" , sbom_component.get("name"));
        product_data.put("publisher", sbom_component.get("publisher") == null
                ? sbom_materials.get("author") : sbom_component.get("publisher"));
        product_data.put("version", sbom_component.get("version"));
        product_data.put("id", sbom_component.get("bom-ref"));

        return result;
    }
}