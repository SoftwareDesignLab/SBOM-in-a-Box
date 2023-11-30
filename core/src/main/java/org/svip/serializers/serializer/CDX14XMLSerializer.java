package org.svip.serializers.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.objects.SVIPComponentObject;
import org.svip.sbom.model.objects.SVIPSBOM;
import org.svip.sbom.model.shared.Relationship;
import org.svip.sbom.model.shared.metadata.Contact;
import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbom.model.shared.metadata.CreationTool;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.shared.util.ExternalReference;
import org.svip.sbom.model.shared.util.LicenseCollection;

import org.svip.serializers.Metadata;

import java.io.IOException;
import java.util.*;

/**
 * Name: CDX14XMLSerializer.java
 * Description: A Serializer for CycloneDX 1.4 XML SBOMs. This class is extended
 * off of the Jackson StdSerializer and uses the Jackson XmlMapper and ToXmlGenerator
 * class to write in XML instead of JSON.
 *
 * @author Tyler Drake
 */
public class CDX14XMLSerializer extends StdSerializer<SVIPSBOM> implements Serializer {

    // Pretty Print state
    private boolean prettyPrint = false;

    public CDX14XMLSerializer() {
        super(SVIPSBOM.class);
    }

    protected CDX14XMLSerializer(Class<SVIPSBOM> t) {
        super(t);
    }

    /**
     * Writes XML to the SBOM
     *
     * @param sbom The SBOM to serialize.
     * @return Object mapper writing an XML string
     * @throws JsonProcessingException
     */
    @Override
    public String writeToString(SVIPSBOM sbom) throws JsonProcessingException {
        if(prettyPrint)
            return getObjectMapper().writer().with(SerializationFeature.INDENT_OUTPUT).writeValueAsString(sbom);
        else return getObjectMapper().writer().writeValueAsString(sbom);
    }

    /**
     * Initializes the XmlMapper and Serializer
     *
     * @return mapper XmlMapper
     */
    @Override
    public ObjectMapper getObjectMapper() {
        XmlMapper mapper = new XmlMapper();
        mapper.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true);
        SimpleModule module = new SimpleModule();
        module.addSerializer(SVIPSBOM.class, this);
        mapper.registerModule(module);
        return mapper;
    }

    /**
     * Changes the state of Pretty Print.
     *
     * @param prettyPrint True to pretty-print, false otherwise.
     */
    @Override
    public void setPrettyPrinting(boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
    }

    /**
     * Serializes the SBOM to XML.
     *
     * @param sbom The SBOM to serialize.
     * @param gen The JSON Generator (Will be cast to an ToXmlGenerator).
     * @param serializers Serializer Provider
     * @throws IOException
     */
    @Override
    public void serialize(SVIPSBOM sbom, JsonGenerator gen, SerializerProvider serializers) throws IOException {

        // Cast the JsonGenerator to an ToXmlGenerator
        ToXmlGenerator xmlGenerator = (ToXmlGenerator) gen;

        // Start XML bom object
        xmlGenerator.writeStartObject();

        //
        // Set top level attributes
        //
        xmlGenerator.setNextIsAttribute(true);

        // Schema and SpecVersion
        xmlGenerator.writeFieldName("xmlns");
        xmlGenerator.writeString("http://cyclonedx.org/schema/bom/1.4");

        // Serial Number
        xmlGenerator.writeFieldName("serialNumber");
        xmlGenerator.writeString("urn:uuid:" + (sbom.getUID() == null ? UUID.randomUUID() : sbom.getUID()));

        // Version
        xmlGenerator.writeFieldName("version");
        xmlGenerator.writeString(sbom.getVersion());

        xmlGenerator.setNextIsAttribute(false);

        //
        // Metadata
        //
        xmlGenerator.writeFieldName("metadata");
        xmlGenerator.writeStartObject();

        // Write the metadata
        if(sbom.getCreationData() != null)
            writeMetadata(xmlGenerator, sbom.getCreationData());

        // Write the root component into the metadata
        if(sbom.getRootComponent() != null) {
            writeComponent(xmlGenerator, sbom.getRootComponent());
        }

        // End metadata xml object
        xmlGenerator.writeEndObject();

        //
        // Components
        //



        if(sbom.getComponents() != null) {
            // Write the components
            xmlGenerator.writeFieldName("components");
            xmlGenerator.writeStartObject();

            // Cycle through each component
            for (Component component : sbom.getComponents()) {

                // If the component isn't null
                if(component != null) {

                    // Cast the component to an SVIPComponent and write the component
                    SVIPComponentObject svipComponent = (SVIPComponentObject) component;
                    writeComponent(xmlGenerator, svipComponent);

                }

            }

            // End components object
            xmlGenerator.writeEndObject();
        }

        //
        // Dependencies
        //

        xmlGenerator.writeFieldName("dependencies");
        xmlGenerator.writeStartObject();

        // Write the dependencies
        writeDependencies(xmlGenerator, sbom.getRelationships());

        // End the dependencies xml object
        xmlGenerator.writeEndObject();

        // End the xml bom object
        xmlGenerator.writeEndObject();

    }

    /**
     * Writes the metadata as XML.
     *
     * @param xmlGenerator The XML Generator to write the metadata.
     * @param data The metadata to be written.
     * @throws IOException
     */
    public void writeMetadata(ToXmlGenerator xmlGenerator, CreationData data) throws IOException {

        // Create a new timestamp xml object
        xmlGenerator.writeFieldName("timestamp");
        xmlGenerator.writeStartObject();

        // Add the timestamp
        xmlGenerator.writeRaw(data.getCreationTime());

        // End timestamp xml object
        xmlGenerator.writeEndObject();

        // If creation tools exists
        if(data.getCreationTools() != null) {

            // Start tools xml object
            xmlGenerator.writeFieldName("tools");
            xmlGenerator.writeStartObject();

            // Go through each tools
            for(CreationTool tool : data.getCreationTools()) {

                // Start new tool xml object
                xmlGenerator.writeFieldName("tool");
                xmlGenerator.writeStartObject();

                if(tool.getVendor() != null){
                    // Add vendor xml object
                    xmlGenerator.writeFieldName("vendor");
                    xmlGenerator.writeStartObject();
                    xmlGenerator.writeRaw(tool.getVendor());
                    xmlGenerator.writeEndObject();
                }

                if(tool.getName() != null){
                    // Add name xml object
                    xmlGenerator.writeFieldName("name");
                    xmlGenerator.writeStartObject();
                    xmlGenerator.writeRaw(tool.getName());
                    xmlGenerator.writeEndObject();
                }


                if(tool.getVersion() != null){
                    // Add version xml object
                    xmlGenerator.writeFieldName("version");
                    xmlGenerator.writeStartObject();
                    xmlGenerator.writeRaw(tool.getVersion());
                    xmlGenerator.writeEndObject();
                }

                // If the tool has hashes
                if(tool.getHashes() != null) {

                    // Start new hashes xml object
                    xmlGenerator.writeFieldName("hashes");
                    xmlGenerator.writeStartObject();

                    // Iterate through each hash in the tool
                    for(Map.Entry<String, String> hash : tool.getHashes().entrySet()) {

                        // Start a new hash xml object
                        xmlGenerator.writeFieldName("hash");
                        xmlGenerator.writeStartObject();

                        // Add the algorithm to the hash xml object as an attribute
                        xmlGenerator.setNextIsAttribute(true);
                        xmlGenerator.writeFieldName("alg");
                        xmlGenerator.writeString(hash.getKey());
                        xmlGenerator.setNextIsAttribute(false);

                        // Write the hash value in between the xml hash object
                        xmlGenerator.writeRaw(hash.getValue());

                        // End hash xml object
                        xmlGenerator.writeEndObject();

                    }

                    // End hashes xml object
                    xmlGenerator.writeEndObject();


                }

                // End tool xml object
                xmlGenerator.writeEndObject();

            }

            // End tools xml object
            xmlGenerator.writeEndObject();

        }

        // If Authors exist for creation data
        if(data.getAuthors() != null && data.getAuthors().size() > 0) {

            // Start new contacts xml object
            xmlGenerator.writeFieldName("contacts");
            xmlGenerator.writeStartObject();

            // Start new contact xml array
            xmlGenerator.writeFieldName("contact");
            xmlGenerator.writeStartArray();

            // Go through each contact
            for(Contact author : data.getAuthors()) {

                // Write the contact
                writeContact(xmlGenerator, author);

            }

            // End the contact xml array
            xmlGenerator.writeEndArray();

            // End the contacts xml object
            xmlGenerator.writeEndObject();

        }

        // Write manufacturer
        if(data.getManufacture() != null) {
            xmlGenerator.writeFieldName("manufacturer");
            xmlGenerator.writeStartObject();
            writeOrganization(xmlGenerator, data.getManufacture());
            xmlGenerator.writeEndObject();
        }

        // Write Supplier
        if(data.getSupplier() != null) {
            xmlGenerator.writeFieldName("supplier");
            xmlGenerator.writeStartObject();
            writeOrganization(xmlGenerator, data.getSupplier());
            xmlGenerator.writeEndObject();
        }

        // Write Licenses
        Set<String> licenses = data.getLicenses();
        if (licenses != null) {
            writeLicenses(xmlGenerator, data.getLicenses());
        }

        // Write properties
        if (data.getProperties() != null) {

            // If no creator comment exists or if it doesn't contain SVIP comment
            if (data.getProperties().get("creatorComment") == null ||
                    !data.getProperties().get("creatorComment").contains(Metadata.SERIALIZED_COMMENT)) {
                // Add the SVIP serialized comment
                data.addProperty("creatorComment", Metadata.SERIALIZED_COMMENT);
            }

            // If the creator comment isn't empty and isn't null
            if (data.getCreatorComment() != null && !data.getCreatorComment().isEmpty()) {
                // Add the creator comment
                data.addProperty("creatorComment", data.getCreatorComment());
            }

            // Write the properties
            writeProperties(xmlGenerator, data.getProperties());

        }

    }

    /**
     * Writes the component as XML.
     *
     * @param xmlGenerator The XML Generator to write the component.
     * @param svipComponentObject The component to be written.
     * @throws IOException
     */
    public void writeComponent(ToXmlGenerator xmlGenerator, SVIPComponentObject svipComponentObject) throws IOException {

        // Create new component xml object
        xmlGenerator.writeFieldName("component");
        xmlGenerator.writeStartObject();

        // Set the top level info for the component: bom-ref ID and type
        xmlGenerator.setNextIsAttribute(true);
        xmlGenerator.writeFieldName("bom-ref");
        xmlGenerator.writeString(svipComponentObject.getUID());
        xmlGenerator.writeFieldName("type");
        xmlGenerator.writeString(svipComponentObject.getType());
        xmlGenerator.setNextIsAttribute(false);

        // Write basic component information
        xmlGenerator.writeStringField("name", svipComponentObject.getName());
        xmlGenerator.writeStringField("version", svipComponentObject.getVersion());
        xmlGenerator.writeStringField("scope", svipComponentObject.getScope());
        xmlGenerator.writeStringField("mime-type", svipComponentObject.getMimeType());
        xmlGenerator.writeStringField("group", svipComponentObject.getGroup());
        xmlGenerator.writeStringField("copyright", svipComponentObject.getCopyright());

        // Write component supplier
        if (svipComponentObject.getSupplier() != null) {
            xmlGenerator.writeFieldName("supplier");
            xmlGenerator.writeStartObject();
            writeOrganization(xmlGenerator, svipComponentObject.getSupplier());
            xmlGenerator.writeEndObject();
        }

        // Write component author and publisher
        xmlGenerator.writeStringField("author", svipComponentObject.getAuthor());
        xmlGenerator.writeStringField("publisher", svipComponentObject.getPublisher());

        // Write component description
        if (svipComponentObject.getDescription() != null)
            xmlGenerator.writeStringField(
                    "description",
                    "Summary: " + svipComponentObject.getDescription().getSummary() +
                            " | Details: " + svipComponentObject.getDescription().getDescription()
            );

        // Write the component's hashes
        if(svipComponentObject.getHashes() != null) writeHashes(xmlGenerator, svipComponentObject.getHashes());

        // Write the component's licenses
        if (svipComponentObject.getLicenses() != null) {
            LicenseCollection licenses = svipComponentObject.getLicenses();
            Set<String> allLicenses = new HashSet<>();
            allLicenses.addAll(licenses.getConcluded());
            allLicenses.addAll(licenses.getDeclared());
            allLicenses.addAll(licenses.getInfoFromFiles());
            writeLicenses(xmlGenerator, allLicenses);
        }

        // Write the component's CPEs
        if (svipComponentObject.getCPEs() != null)
            xmlGenerator.writeStringField("cpe", String.join(", ", svipComponentObject.getCPEs()));

        // Write the component's PURLs
        if (svipComponentObject.getPURLs() != null)
            xmlGenerator.writeStringField("purl", String.join(", ", svipComponentObject.getPURLs()));

        // Write External References
        if(svipComponentObject.getExternalReferences() != null)
            writeExternalReferences(xmlGenerator, svipComponentObject.getExternalReferences());

        // Write Properties
        if(svipComponentObject.getProperties() != null)
            writeProperties(xmlGenerator, svipComponentObject.getProperties());

        // Write release notes
        xmlGenerator.writeStringField("releaseNotes", "Release Date: " + svipComponentObject.getReleaseDate());

        // End component xml object
        xmlGenerator.writeEndObject();

    }

    /**
     * Writes the dependencies as XML.
     *
     * @param xmlGenerator The XML Generator to write the dependencies.
     * @param relationships The dependencies to be written.
     * @throws IOException
     */
    public void writeDependencies(ToXmlGenerator xmlGenerator,Map<String, Set<Relationship>> relationships) throws IOException {

        if(relationships.containsKey(null) && relationships.size() == 1)
            return;

        // Cycle through each dependency set
        for(Map.Entry<String, Set<Relationship>> parent : relationships.entrySet()) {

            // Start new dependency xml object
            xmlGenerator.writeFieldName("dependency");
            xmlGenerator.writeStartObject();

            // Add the parent key as an attribute
            xmlGenerator.setNextIsAttribute(true);
            xmlGenerator.writeFieldName("ref");
            xmlGenerator.writeString(parent.getKey());
            xmlGenerator.setNextIsAttribute(false);

            // Cycle through each dependency of the parent
            for(Relationship dependency : parent.getValue()) {

                // Start a new dependency xml object
                xmlGenerator.writeFieldName("dependency");
                xmlGenerator.writeStartObject();

                // Add the dependency key as an attribute
                xmlGenerator.setNextIsAttribute(true);
                xmlGenerator.writeFieldName("ref");
                xmlGenerator.writeString(dependency.getOtherUID());
                xmlGenerator.setNextIsAttribute(false);

                // End the dependency xml object
                xmlGenerator.writeEndObject();

            }

            // End the dependency xml object
            xmlGenerator.writeEndObject();

        }

    }

    /**
     * Sub-Field Writers
     */

    /**
     * Writes the organization as XML.
     *
     * @param xmlGenerator The XML Generator to write the organization.
     * @param organization The organization to be written.
     * @throws IOException
     */
    public void writeOrganization(ToXmlGenerator xmlGenerator, Organization organization) throws IOException {

        // Write the name for the organization
        if(organization.getUrl() != null)
            xmlGenerator.writeStringField("name", organization.getName());

        // Write the url for the organization
        if (organization.getUrl() != null)
            xmlGenerator.writeStringField("url", organization.getUrl());

        // Write the contacts for the organization
        xmlGenerator.writeFieldName("contact");
        xmlGenerator.writeStartArray();

        // Add each contact
        if(organization.getContacts() != null) {

            // Add each contact
            for (Contact contact : organization.getContacts()) {

                // If a contact exists write the contact
                if(contact != null) writeContact(xmlGenerator, contact);

            }

        }

        // End xml array
        xmlGenerator.writeEndArray();

    }

    /**
     * Writes the contact as XML.
     *
     * @param xmlGenerator The XML Generator to write the contact.
     * @param contact The contact to be written.
     * @throws IOException
     */
    public void writeContact(ToXmlGenerator xmlGenerator, Contact contact) throws IOException {

        // Create new xml Object
        xmlGenerator.writeStartObject();

        // Write the name of the contact
        xmlGenerator.writeStringField("name", contact.getName());

        // Write the contact email
        if (contact.getEmail() != null) xmlGenerator.writeStringField("email", contact.getEmail());

        // Write the contact phone
        if (contact.getPhone() != null) xmlGenerator.writeStringField("phone", contact.getPhone());

        // End xml Object
        xmlGenerator.writeEndObject();

    }

    /**
     * Writes the hashes as XML.
     *
     * @param xmlGenerator The XML Generator to write the hashes.
     * @param hashes The hashes to be written.
     * @throws IOException
     */
    public void writeHashes(ToXmlGenerator xmlGenerator, Map<String, String> hashes) throws IOException {

        // Start the new hashes xml object
        xmlGenerator.writeFieldName("hashes");
        xmlGenerator.writeStartObject();

        // Loop through hashes and print the algorithm and content
        for (Map.Entry<String, String> hash : hashes.entrySet()) {

            // Create the new hash xml object
            xmlGenerator.writeFieldName("hash");
            xmlGenerator.writeStartObject();

            // Add the algorithm to the header as an attribute
            xmlGenerator.setNextIsAttribute(true);
            xmlGenerator.writeFieldName("alg");
            xmlGenerator.writeString(hash.getKey());
            xmlGenerator.setNextIsAttribute(false);

            // Add the hash value as the body
            xmlGenerator.writeRaw(hash.getValue());

            // End the hash xml object
            xmlGenerator.writeEndObject();

        }

        // End hashes xml object
        xmlGenerator.writeEndObject();

    }

    /**
     * Writes the licenses as XML.
     *
     * @param xmlGenerator The XML Generator to write the licenses.
     * @param licenses The metadata to be written.
     * @throws IOException
     */
    public void writeLicenses(ToXmlGenerator xmlGenerator, Set<String> licenses) throws IOException {

        // Start the new licenses xml object
        xmlGenerator.writeFieldName("licenses");
        xmlGenerator.writeStartObject();

        // Start a new xml array to denote each license
        xmlGenerator.writeFieldName("license");
        xmlGenerator.writeStartArray();

        // Add each license
        for(String license : licenses) {
            xmlGenerator.writeStartObject();
            xmlGenerator.writeStringField("name", license);
            xmlGenerator.writeEndObject();
        }

        // End the xml Array
        xmlGenerator.writeEndArray();

        // End the xml Object
        xmlGenerator.writeEndObject();

    }

    /**
     * Writes the external references as XML.
     *
     * @param xmlGenerator The XML Generator to write the external references.
     * @param externalReferences The metadata to be written.
     * @throws IOException
     */
    public void writeExternalReferences(ToXmlGenerator xmlGenerator, Set<ExternalReference> externalReferences) throws IOException {

        // Start a new xml object for external references
        xmlGenerator.writeFieldName("externalReferences");
        xmlGenerator.writeStartObject();

        // Add each external reference
        for(ExternalReference ref : externalReferences) {

            // Start a new xml array of references
            xmlGenerator.writeFieldName("reference");
            xmlGenerator.writeStartObject();

            // Add the algorithm to the header as an attribute
            xmlGenerator.setNextIsAttribute(true);
            xmlGenerator.writeFieldName("type");
            xmlGenerator.writeString(ref.getType());
            xmlGenerator.setNextIsAttribute(false);

            // Write the url and comment
            xmlGenerator.writeStringField("url", ref.getUrl());
            xmlGenerator.writeStringField("comment", "Category: " + ref.getCategory());

            // End xml object
            xmlGenerator.writeEndObject();

        }

        // End xml Object
        xmlGenerator.writeEndObject();

    }

    /**
     * Writes the properties as XML.
     *
     * @param xmlGenerator The XML Generator to write the properties.
     * @param properties The metadata to be written.
     * @throws IOException
     */
    public void writeProperties(ToXmlGenerator xmlGenerator, Map<String, Set<String>> properties) throws IOException {

        // Start a new xml object for properties
        xmlGenerator.writeFieldName("properties");
        xmlGenerator.writeStartObject();

        for(Map.Entry<String, Set<String>> property : properties.entrySet()) {

            for(String value : property.getValue()) {

                // Start new xml object property
                xmlGenerator.writeFieldName("property");
                xmlGenerator.writeStartObject();

                // Add the name of the property as an attribute
                xmlGenerator.setNextIsAttribute(true);
                xmlGenerator.writeFieldName("name");
                xmlGenerator.writeString(property.getKey());
                xmlGenerator.setNextIsAttribute(false);

                // Write the value of the property
                xmlGenerator.writeRaw(value);

                // End xml object property
                xmlGenerator.writeEndObject();

            }

        }

        xmlGenerator.writeEndObject();

    }

}
