package org.svip.sbomfactory.translators;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.svip.sbom.model.Component;
import org.svip.sbom.model.PURL;
import org.svip.sbom.model.SBOM;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * file: TranslatorCDXXML.java
 * Coverts CycloneDX SBOMs into internal SBOM objects
 *
 * @author Tyler Drake
 */
public class TranslatorCDXXML {

    /**
     * Translates a CycloneDX XML file into an SBOM object from the contents of an SBOM
     *
     * @param contents String contents of the SBOM file
     * @param file_path String path to the SBOM file
     * @return SBOM object
     * @throws ParserConfigurationException if the DocumentBuilder cannot be created
     */
    public static SBOM translatorCDXXMLContents(String contents, String file_path) throws ParserConfigurationException {

        // New SBOM object
        SBOM sbom;

        // Top component (component sbom is for) uuid
        Component top_component;
        UUID top_component_uuid = null;

        // Data for author
        String author = "";

        // Top level SBOM materials
        HashMap<String, String> header_materials = new HashMap<>();
        HashMap<String, String> sbom_materials = new HashMap<>();
        HashMap<String, String> sbom_component = new HashMap<>();

        // Dependencies
        // Key = unique id (bom-ref in this case), Value = unique id (bom-ref of the dependency)
        Multimap dependencies = ArrayListMultimap.create();

        // Components collections
        // Key = unique id (bom-ref), Value = Component Object
        HashMap<String, Component> components = new HashMap<>();

        // Collection of component names used by dependencyTree
        Set<String> components_left = new HashSet<>();

        // Initialize Document Builder
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        // Get parsed XML SBOM file and normalize
        Document sbom_xml_file;

        try {
            sbom_xml_file = documentBuilder.parse(new InputSource(new StringReader(contents)));
        } catch (SAXException saxException) {
            System.err.println("Error: SAXException found. File must be a properly formatted Cyclone-DX XML file: " + file_path);
            return null;
        } catch (IOException ioException) {
            System.err.println("Error: IOException found. File information could not be found in: " + file_path);
            return null;
        } catch (Exception e) {
            System.err.println("Error: Issue detected with file: " + file_path);
            e.printStackTrace();
            return null;
        }

        sbom_xml_file.getDocumentElement().normalize();

        // SBOM collections
        NamedNodeMap sbomHead;
        NodeList sbomMeta;
        NodeList sbomComp;
        NodeList sbomDependencies;

        // Get SBOM Metadata and Components
        try {
            sbomHead = sbom_xml_file.getElementsByTagName("bom").item(0).getAttributes();
        } catch (Exception e) {
            System.err.println("Error: Invalid format, 'bom' not found in: " + file_path);
            return null;
        }

        try {
            sbomMeta = ((Element) (sbom_xml_file.getElementsByTagName("metadata")).item(0)).getElementsByTagName("*");
        } catch (Exception e) {
            System.err.println("Error: Invalid format, 'metadata' not found in: " + file_path);
            return null;
        }

        try {
            sbomComp = ((Element) (sbom_xml_file.getElementsByTagName("components")).item(0)).getElementsByTagName("component");
        } catch (Exception e) {
            System.err.println(
                    "Warning: no components found. If this is not intended, please check file format. " +
                            "File: " + file_path
            );
            sbomComp = null;
        }

        try {
            sbomDependencies = ((Element) (sbom_xml_file.getElementsByTagName("dependencies")).item(0)).getElementsByTagName("dependency");
        } catch (Exception e) {
            System.err.println(
                    "Warning: No dependencies found. Dependency Tree may not build correctly. " +
                            "File: " + file_path
            );
            sbomDependencies = null;
        }

        // Get important SBOM items from header (schema, serial, version)
        for (int a = 0; a < sbomHead.getLength(); a++) {
            header_materials.put(
                    sbomHead.item(a).getNodeName(),
                    sbomHead.item(a).getTextContent()
            );
        }

        // Get important SBOM items from meta  (timestamp, tool info)
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
                if(author != "") { author += " , "; }
                author += sbomMeta.item(b).getTextContent();
            } else {
                sbom_materials.put(
                        sbomMeta.item(b).getNodeName(),
                        sbomMeta.item(b).getTextContent()
                );
            }
        }

        // Create the new SBOM Object with top level data
        try {
            sbom = new SBOM(
                    "cyclonedx",
                    header_materials.get("xmlns"),
                    header_materials.get("version"),
                    author == "" ? sbom_materials.get("vendor") : author,
                    header_materials.get("serialNumber"),
                    sbom_materials.get("timestamp"),
                    null);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Could not create SBOM object. File: " + file_path);
            return null;
        }

        // Add the top level component as the root component
        try {
            top_component = new Component(
                    sbom_component.get("name"),
                    sbom_component.get("publisher") == null
                            ? sbom_materials.get("author")
                            : sbom_component.get("publisher"),
                    sbom_component.get("version"),
                    sbom_component.get("bom-ref")
            );
            sbom.addComponent(null, top_component);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Cannot find top level component in metadata. File: " + file_path);
            top_component = null;
        }

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

                    Set<PURL> purls = new HashSet<>();
                    Set<String> cpes = new HashSet<>();

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
                            purls.add(new PURL(component_elements.item(j).getTextContent()));
                        }
                        else {
                            component_items.put(
                                    component_elements.item(j).getNodeName(),
                                    component_elements.item(j).getTextContent()

                            );
                        }
                    }

                    // Create a new component with required information
                    Component component = new Component(
                            component_items.get("name"),
                            component_items.get("publisher"),
                            component_items.get("version"),
                            component_items.get("bom-ref")
                    );

                    // Set CPEs and PURLs
                    component.setCpes(cpes);
                    component.setPurls(purls);

                    // Set licenses for component
                    component.setLicenses(component_licenses);

                    components.put(component.getUniqueID(), component);
                    components_left.add(component.getUniqueID());

                }

            }

        }

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
                        dependencies.put(
                                parent,
                                children.item(m).getAttributes().item(0).getTextContent().replaceAll("@", "")
                        );
                    }
                }
            }
        }

        // Create the top level component
        // Build the dependency tree using dependencyBuilder
        try {
            dependencyBuilder(dependencies, components, components_left, top_component, sbom, null);
        } catch (Exception e) {
            System.err.println("Error processing dependency tree.");
        }

        // This will take all the components that were not added in the dependencyTree through
        // dependencyBuilder and will tack each remaining component to the top component by default
        for(String remaining_component : components_left) {
            sbom.addComponent(top_component.getUUID(), components.get(remaining_component));
        }

        // Return complete SBOM object
        return sbom;

    }

    /**
     * Coverts CycloneDX SBOMs into internal SBOM object
     *
     * @param file_path Path to CycloneDX SBOM
     * @return internal SBOM object
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public static SBOM translatorCDXXML(String file_path) throws ParserConfigurationException {
        // Get file_path contents and save it into a string
        String file_contents = "";
        try {
            file_contents = new String(Files.readAllBytes(Paths.get(file_path)));
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error: Unable to read file: " + file_path);
            return null;
        }

        return translatorCDXXMLContents(file_contents, file_path);

    }

    /**
     * A simple recursive function to build a dependency tree out of the CDX XML SBOM
     *
     * @param dependencies      A map containing packaged components with their bom-ref IDs, pointing to dependencies
     * @param components        A map containing each Component with their bom-ref ID as a key
     * @param components_left   Components that haven't been added to the dependencyTree
     * @param parent            Parent component to have dependencies connected to
     * @param sbom              The SBOM object
     * @param visited           A collection of visited nodes
     */
    public static void dependencyBuilder(
            Multimap dependencies,
            HashMap components,
            Collection components_left,
            Component parent,
            SBOM sbom,
            Set<String> visited
    ) {

        // If top component is null, return. There is nothing to process.
        if (parent == null) { return; }

        // If this is the first time visiting this node
        if (visited != null) {
            // Add this parent to the visited set
            visited.add(parent.getUniqueID());
        }

        // Get the parent's dependencies as a list
        String parent_id = parent.getUniqueID();
        Collection<Object> children_bom_refs = dependencies.get(parent_id);

        // Cycle through each dependency the parent component has
        for (Object child_bom_ref : children_bom_refs) {

            // Retrieve the component the parent has a dependency for
            Component child = (Component) components.get(child_bom_ref);

            // If component is already in the dependency tree, add it as a child to the parent
            // Else, add it to the dependency tree while setting the parent
            if(sbom.hasComponent(child.getUUID())) {
                parent.addChild(child.getUUID());
            } else {
                sbom.addComponent(parent.getUUID(), child);
            }

            // If this is the first time this component is being added/referenced on dependencyTree
            // Remove the component from remaining component list
            if(components_left.contains(child_bom_ref)) {
                components_left.remove(child_bom_ref);
            }


            if (visited == null) {
                // This means we are in the top level component
                // Pass in a new hashset instead of the visited set
                visited = new HashSet<>();
                dependencyBuilder(dependencies, components, components_left, child, sbom, new HashSet<>());
            }
            else {
                // Only explore if we haven't already visited this component
                if (!visited.contains(child.getUniqueID())) {
                    // Pass the child component as the new parent into dependencyBuilder
                    dependencyBuilder(dependencies, components, components_left, child, sbom, visited);
                }
            }
        }
    }
}