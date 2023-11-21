package org.svip.serializers.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonValueFormatVisitor;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.objects.SVIPComponentObject;
import org.svip.sbom.model.objects.SVIPSBOM;
import org.svip.sbom.model.shared.metadata.Contact;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.shared.util.ExternalReference;
import org.svip.sbom.model.shared.util.LicenseCollection;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class CDX14XMLSerializer extends StdSerializer<SVIPSBOM> implements Serializer {

    private boolean prettyPrint = false;

    public CDX14XMLSerializer() {
        super(SVIPSBOM.class);
    }

    protected CDX14XMLSerializer(Class<SVIPSBOM> t) {
        super(t);
    }

    @Override
    public String writeToString(SVIPSBOM sbom) throws JsonProcessingException {
        if(prettyPrint)
            return getObjectMapper().writer().with(SerializationFeature.INDENT_OUTPUT).writeValueAsString(sbom);
        else return getObjectMapper().writer().writeValueAsString(sbom);
    }

    @Override
    public ObjectMapper getObjectMapper() {
        XmlMapper mapper = new XmlMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(SVIPSBOM.class, this);
        mapper.registerModule(module);
        return mapper;
    }

    @Override
    public void setPrettyPrinting(boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
    }

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
        xmlGenerator.writeString("urn:uuid:" + UUID.randomUUID());

        // Version
        xmlGenerator.writeFieldName("version");
        xmlGenerator.writeString(sbom.getVersion());

        xmlGenerator.setNextIsAttribute(false);

        //
        // Metadata
        //

        //
        // Components
        //

        xmlGenerator.writeFieldName("components");
        xmlGenerator.writeStartObject();

        //xmlGenerator.writeFieldName("component");
        //xmlGenerator.writeStartArray("component");


        for (Component component : sbom.getComponents()) {
            if (component == null) continue;
            SVIPComponentObject svipComponent = (SVIPComponentObject) component;
            writeComponent(xmlGenerator, svipComponent);
        }


        // End components object
        xmlGenerator.writeEndObject();

        // End the bom object
        xmlGenerator.writeEndObject();

    }

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
            writeOrganization(xmlGenerator, svipComponentObject.getSupplier());
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

        if(svipComponentObject.getExternalReferences() != null)
            writeExternalReferences(xmlGenerator, svipComponentObject.getExternalReferences());

        // End component xml object
        xmlGenerator.writeEndObject();

    }

    /**
     * sub-field write helpers
     */

    public void writeOrganization(ToXmlGenerator xmlGenerator, Organization organization) throws IOException {

        // Start the new organization xml object
        xmlGenerator.writeFieldName("supplier");
        xmlGenerator.writeStartObject();

        // Write the name for the organization
        xmlGenerator.writeStringField("name", organization.getName());

        // Write the url for the organization
        if (organization.getUrl() != null) xmlGenerator.writeStringField("url", organization.getUrl());

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

        // End xml Object
        xmlGenerator.writeEndObject();

    }

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

}
