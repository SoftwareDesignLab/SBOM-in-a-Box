package org.svip.sbomfactory.generators.generators.spdx;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import org.svip.sbomfactory.generators.utils.ParserComponent;
import org.svip.sbomfactory.generators.utils.generators.License;
import org.svip.sbomfactory.generators.utils.generators.LicenseManager;
import org.svip.sbomfactory.generators.utils.generators.Tool;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * File: SPDXSerializer.java
 * <p>
 * A custom serializer for the <code>SPDXStore</code> class extended from the Jackson library's <code>STDSerializer</code>
 * class to convert the data of <code>SPDXStore</code> to an SPDX v2.3 document.
 * </p>
 * @author Ian Dunn
 */
public class SPDXSerializer extends StdSerializer<SPDXStore> {

    //#region Enums

    /**
     * A list of valid SPDX external reference types from the
     * <a href="https://spdx.github.io/spdx-spec/v2.3/package-information/#721-external-reference-field">
     *     v2.3 documentation</a>
     */
    private enum REFERENCE_CATEGORY {
        SECURITY,
        PACKAGE_MANAGER,
        PERSISTENT_ID,
        OTHER
    }

    //#endregion

    //#region Constructors

    /**
     * The default serializer constructor that takes in no arguments and serializes a null SPDXStore class.
     */
    public SPDXSerializer() { super((Class<SPDXStore>) null); }

    /**
     * A serializer constructor that takes in an SPDXStore class to serialize.
     *
     * @param t The SPDXStore class object.
     */
    protected SPDXSerializer(Class<SPDXStore> t) {
        super(t);
    }

    //#endregion

    //#region Overrides

    /**
     * The default serialize method called by Jackson ObjectMappers to serialize the spdxStore class to an SPDX
     * spdxStore.
     *
     * @param spdxStore The spdxStore instance with the spdxStore data.
     * @param jsonGenerator The JsonGenerator used by Jackson to serialize to a file.
     * @param serializerProvider The SerializerProvider used by Jackson to serialize to a file.
     * @throws IOException If an error writing to the file occurs.
     */
    @Override
    public void serialize(SPDXStore spdxStore, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {

        // If we are serializing to XML, specify the root element as "Document"
        if(jsonGenerator instanceof ToXmlGenerator xmlGenerator) {
            xmlGenerator.initGenerator();
            xmlGenerator.setNextName(new QName("Document"));
        }

        jsonGenerator.writeStartObject(); // {

        //
        // spdxStore Creation Info (ID, SPDX version, and creator info)
        //

        jsonGenerator.writeStringField("SPDXID", spdxStore.getDocumentId());
        jsonGenerator.writeStringField("spdxVersion", spdxStore.getSpecVersion());

        /* Creator info */
        jsonGenerator.writeFieldName("creationInfo");

        jsonGenerator.writeStartObject(); // {
        jsonGenerator.writeStringField("created", spdxStore.getTimestamp()); // Creation date

        jsonGenerator.writeFieldName("creators");
        // Write all creator strings as an array representation
        List<String> creators = spdxStore.getTools().stream().map(Tool::getToolInfo).toList();
        jsonGenerator.writeArray(creators.toArray(new String[0]), 0, creators.size());
        jsonGenerator.writeEndObject(); // }

        //
        // Internal spdxStore info (name and license)
        //

        jsonGenerator.writeStringField("name", spdxStore.getHeadComponent().getName());


        // Get all licenses from tools
        jsonGenerator.writeStringField("dataLicense",
                LicenseManager.getConcatenatedLicenseString(spdxStore.getToolLicenses()));

        //
        // Extracted licensing info (invalid licenses)
        //

        jsonGenerator.writeFieldName("hasExtractedLicensingInfos");
        jsonGenerator.writeStartArray();

        for(License license : spdxStore.getExternalLicenses()) {
            writeExtractedLicensingInfo(jsonGenerator, license);
        }

        jsonGenerator.writeEndArray();

        //
        // List of SPDX identifiers that the spdxStore describes
        //

        jsonGenerator.writeFieldName("documentDescribes");
        jsonGenerator.writeArray(spdxStore.getDocumentDescribes().toArray(new String[0]), 0,
                spdxStore.getDocumentDescribes().size());

        //
        // Unique spdxStore URI
        //

        jsonGenerator.writeStringField("documentNamespace", spdxStore.getSerialNumber());

        //
        // List of ALL packages from the spdxStore as an array of objects
        //

        jsonGenerator.writeFieldName("packages");
        jsonGenerator.writeStartArray(); // [

        for(ParserComponent pkg : spdxStore.getPackages())
            writePackage(jsonGenerator, spdxStore, pkg); // For each package in the spdxStore, write its data as an object

        jsonGenerator.writeEndArray(); // ]

        //
        // Files
        //

        if(spdxStore.getFiles().size() > 0) {
            jsonGenerator.writeFieldName("files");
            jsonGenerator.writeStartArray();

            for(Map.Entry<String, String> entry : spdxStore.getFiles().entrySet()) {
                jsonGenerator.writeStartObject();

                jsonGenerator.writeStringField("SPDXID", entry.getValue());
                jsonGenerator.writeStringField("fileName", entry.getKey());
                jsonGenerator.writeFieldName("fileTypes");
                jsonGenerator.writeStartArray();
                // SPDX filetypes https://spdx.github.io/spdx-spec/v2.3/file-information/
                jsonGenerator.writeString("SOURCE"); // TODO we currently only analyze source files??
                jsonGenerator.writeEndArray();

                jsonGenerator.writeEndObject();
            }

            jsonGenerator.writeEndArray();
        }

        //
        // Relationships that exist between packages (if any)
        //

        if(spdxStore.getRelationships().size() > 0) { // If at least one Relationship exists, write this field
            jsonGenerator.writeFieldName("relationships");
            jsonGenerator.writeStartArray(); // [

            // Loop through all SPDXRelationships and write each as an object
            for(Relationship relationship : spdxStore.getRelationships())
                writeRelationship(jsonGenerator, relationship);

            jsonGenerator.writeEndArray(); // ]
        }

        jsonGenerator.writeEndObject(); // }
    }

    //#endregion

    //#region Helper Methods

    /**
     * Private helper method to wite the data of a single package as an object using the provided JsonGenerator.
     *
     * @param jsonGenerator The JsonGenerator to use to write the package to the file.
     * @param pkg The package represented as a ParserComponent.
     * @throws IOException If an error writing to the file occurs.
     */
    private void writePackage(JsonGenerator jsonGenerator, SPDXStore spdxStore, ParserComponent pkg) throws IOException {
        jsonGenerator.writeStartObject(); // {

        //
        // Unique SPDX identifier of the package
        //

        jsonGenerator.writeStringField("SPDXID", pkg.getSPDXID());

        //
        // Copyright text - NOASSERTION means this is not something our generator currently covers
        //

        jsonGenerator.writeStringField("copyrightText", "NOASSERTION");

        //
        // External Identifiers
        //

        // If any external identifiers exist, write all types into one array
        if(pkg.getCpes().size() + pkg.getPurls().size() + pkg.getSwids().size() > 0) {
            jsonGenerator.writeFieldName("externalRefs");
            jsonGenerator.writeStartArray(); // [

            // If any PURLs exist
            if(pkg.getPurls().size() > 0) { // TODO Do this for SWIDs once we support them
                for(String purl : pkg.getPurls())
                    // Write the external reference data of the PURL to an object
                    writeExternalRef(jsonGenerator, REFERENCE_CATEGORY.SECURITY, "purl", purl.toString());
            }

            // If any CPEs exist
            if(pkg.getCpes().size() > 0) {
                for(String cpe : pkg.getCpes())
                    // Write the external reference data of the PURL to an object
                    writeExternalRef(jsonGenerator, REFERENCE_CATEGORY.SECURITY, "cpe", cpe);
            }

            jsonGenerator.writeEndArray(); // ]
        }

        //
        // File analysis
        //

        if(pkg.getFiles().size() > 0) {
            jsonGenerator.writeBooleanField("filesAnalyzed", true);
            jsonGenerator.writeFieldName("hasFiles");
            jsonGenerator.writeStartArray();

            // Write current package file reference ID (files will be generated after packages)
            for(String file : pkg.getFiles()) {
                jsonGenerator.writeString(spdxStore.getFiles().get(file));
            }

            jsonGenerator.writeEndArray();
        } else {
            jsonGenerator.writeBooleanField("filesAnalyzed", false);
        }

        //
        // Checksums
        //

        jsonGenerator.writeFieldName("checksums");
        jsonGenerator.writeStartArray();
        jsonGenerator.writeStartObject();

        jsonGenerator.writeStringField("algorithm", "SHA256");
        jsonGenerator.writeStringField("checksumValue", pkg.generateHash());

        jsonGenerator.writeEndObject();
        jsonGenerator.writeEndArray();

        //
        // Licenses
        //

        if(pkg.getResolvedLicenses().size() > 0) {
            jsonGenerator.writeStringField("licenseConcluded",
                    LicenseManager.getConcatenatedLicenseString(pkg.getResolvedLicenses()));
        } else {
            jsonGenerator.writeStringField("licenseConcluded", "NONE"); // Otherwise, no license found
        }

        // We currently do not check for licenses declared inside any packages
        jsonGenerator.writeStringField("licenseDeclared", "NOASSERTION");

        //
        // Other package information
        //

        writeFieldIfExists(jsonGenerator, "name", pkg.getName()); // Package name
        writeFieldIfExists(jsonGenerator, "packageFileName", pkg.getGroup()); // Filename found in the package
        writeFieldIfExists(jsonGenerator, "versionInfo", pkg.getVersion()); // Version info of the package

        // Supplier of the package
        if(pkg.getPublisher() != null && pkg.getPublisher().length() > 0 && !pkg.getPublisher().equals("Unknown"))
            jsonGenerator.writeStringField("supplier", "Organization: " + pkg.getPublisher());

        jsonGenerator.writeEndObject(); // }
    }

    /**
     * Private helper method to write the data of a single relationship as an object using the provided JsonGenerator.
     *
     * @param jsonGenerator The JsonGenerator to use to write the relationship to the file.
     * @param relationship The relationship between two packages represented as an Relationship
     * @throws IOException If an error writing to the file occurs.
     */
    private void writeRelationship(JsonGenerator jsonGenerator, Relationship relationship) throws IOException {
        jsonGenerator.writeStartObject(); // {

        // Write all relationship data according to the Relationship class
        jsonGenerator.writeStringField("spdxElementId", relationship.getElementId());
        jsonGenerator.writeStringField("relatedSpdxElement", relationship.getRelatedElement());
        jsonGenerator.writeStringField("relationshipType", relationship.getRelationshipType().name());

        jsonGenerator.writeEndObject(); // }
    }

    /**
     * Private helper method to write the data of a single field if the String value provided is not null and is not
     * empty. If one of those is true, the value will instead be written as "NOASSERTION".
     *
     * @param jsonGenerator The JsonGenerator to use to write the field to the file.
     * @param fieldName The name of the field to write.
     * @param value The value of the field to write. If null or empty, the field will be written as "NOASSERTION".
     * @throws IOException If an error writing to the file occurs.
     */
    private void writeFieldIfExists(JsonGenerator jsonGenerator, String fieldName, String value) throws IOException {
        if (value != null && value.length() > 0) { // Check if field exists
            jsonGenerator.writeStringField(fieldName, value); // Write key value pair to the file
        } else {
            jsonGenerator.writeStringField(fieldName, "NOASSERTION"); // If no value, dont assert anything
        }
    }

    /**
     * Private helper method to write the data of a single external reference as an object using the provided
     * JsonGenerator.
     *
     * @param jsonGenerator The JsonGenerator to use to write the external reference to the file.
     * @param category The category of the external reference (generally security).
     * @param refType The type of the external reference (i.e. cpe2.3, purl, swid).
     * @param refLocator The locator, or URI of the reference.
     * @throws IOException If an error writing to the file occurs.
     */
    private void writeExternalRef(JsonGenerator jsonGenerator, REFERENCE_CATEGORY category, String refType,
                                  String refLocator) throws IOException {
        jsonGenerator.writeStartObject(); // {
        jsonGenerator.writeStringField("referenceCategory", category.name());
        jsonGenerator.writeStringField("referenceLocator", refLocator);
        jsonGenerator.writeStringField("referenceType", refType);
        jsonGenerator.writeEndObject(); // }
    }

    private void writeExtractedLicensingInfo(JsonGenerator jsonGenerator, License license) throws IOException {
        jsonGenerator.writeStartObject(); // {
        jsonGenerator.writeStringField("licenseId", license.getSpdxLicense());
        jsonGenerator.writeStringField("name", license.getLicenseName());
        jsonGenerator.writeEndObject(); // }
    }

    //#endregion
}
