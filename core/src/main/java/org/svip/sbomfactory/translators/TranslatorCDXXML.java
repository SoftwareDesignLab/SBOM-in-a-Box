package org.svip.sbomfactory.translators;

import org.svip.sbom.model.*;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


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

        // Get important SBOM items from header (schema, serial, version)
        for (int a = 0; a < sbomHead.getLength(); a++) {
            header_materials.put(
                    sbomHead.item(a).getNodeName(),
                    sbomHead.item(a).getTextContent()
            );
        }

        // Get important SBOM items from meta  (timestamp, tool info)
        for (int b = 0; b < sbomMeta.getLength(); b++) {
            if (sbomMeta.item(b).getParentNode().getNodeName().contains("component")) {
                sbom_component.put(
                        sbomMeta.item(b).getNodeName(),
                        sbomMeta.item(b).getTextContent()
                );
            } else if (sbomMeta.item(b).getParentNode().getNodeName().contains("author")) {
                if(author != "") { author += " ~ "; }
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
            top_component = new Component(sbom_component.get("name"), sbom_component.get("version"));
            top_component_uuid = sbom.addComponent(null, top_component);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Unable to set top level component. File: " + file_path);
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

                    // Get all elements from that node
                    Element elem = (Element) compItem;
                    NodeList component_elements = elem.getElementsByTagName("*");

                    // Temporary storage for component elements
                    HashMap<String, String> component_items = new HashMap<>();
                    HashSet<String> component_licenses = new HashSet<>();

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
                            component_items.get("version")
                    );

                    // Set CPEs and PURLs
                    component.setCpes(cpes);
                    component.setPurls(purls);

                    // Set licenses for component
                    component.setLicenses(component_licenses);

                    // Add component to SBOM object
                    UUID new_component = sbom.addComponent(top_component_uuid, component);

                    // If there was no top level component, try to make the new component the head component
                    top_component_uuid = top_component_uuid == null
                            ? new_component
                            : top_component_uuid;
                }

            }

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
}