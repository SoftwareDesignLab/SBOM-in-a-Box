package generators.spdx;

import generators.License;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import utils.ParserComponent;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * File: DocumentSerializer.java
 * <p>
 * A custom serializer for the <code>Document</code> class extended from the Jackson library's <code>STDSerializer</code>
 * class to convert the data of <code>Document</code> to an SPDX v2.3 document.
 * </p>
 * @author Ian Dunn
 */
public class DocumentSerializer extends StdSerializer<Document> {

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
     * The default serializer constructor that takes in no arguments and serializes a null Document class.
     */
    protected DocumentSerializer() { super((Class<Document>) null); }

    /**
     * A serializer constructor that takes in an Document class to serialize.
     *
     * @param t The Document class object.
     */
    protected DocumentSerializer(Class<Document> t) {
        super(t);
    }

    //#endregion

    //#region Overrides

    /**
     * The default serialize method called by Jackson ObjectMappers to serialize the Document class to an SPDX
     * document.
     *
     * @param document The Document instance with the document data.
     * @param jsonGenerator The JsonGenerator used by Jackson to serialize to a file.
     * @param serializerProvider The SerializerProvider used by Jackson to serialize to a file.
     * @throws IOException If an error writing to the file occurs.
     */
    @Override
    public void serialize(Document document, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {

        jsonGenerator.writeStartObject(); // {

        //
        // Document Creation Info (ID, SPDX version, and creator info)
        //

        jsonGenerator.writeStringField("SPDXID", Document.SPDXID);
        jsonGenerator.writeStringField("spdxVersion", document.getVersion());

        /* Creator info */
        jsonGenerator.writeFieldName("creationInfo");

        jsonGenerator.writeStartObject(); // {
        jsonGenerator.writeStringField("created", document.getCreationDate()); // Creation date

        jsonGenerator.writeFieldName("creators");
        // Write all creators as an array representation
        jsonGenerator.writeArray(document.getCreators().toArray(new String[0]), 0,
                document.getCreators().size());

        jsonGenerator.writeEndObject(); // }

        //
        // Internal document info (name and license)
        //

        jsonGenerator.writeStringField("name", document.getName());
        jsonGenerator.writeStringField("dataLicense", document.getDataLicense());

        //
        // Extracted licensing info (invalid licenses)
        //

        jsonGenerator.writeFieldName("hasExtractedLicensingInfos");
        jsonGenerator.writeStartArray();

        for(License license : document.getExternalLicenses()) {
            writeExtractedLicensingInfo(jsonGenerator, license);
        }

        jsonGenerator.writeEndArray();

        //
        // List of SPDX identifiers that the document describes
        //

        jsonGenerator.writeFieldName("documentDescribes");
        jsonGenerator.writeArray(document.getDocumentDescribes().toArray(new String[0]), 0,
                document.getDocumentDescribes().size());

        //
        // Unique document URI
        //

        jsonGenerator.writeStringField("documentNamespace", document.getDocumentNamespace());

        //
        // List of ALL packages from the Document as an array of objects
        //

        jsonGenerator.writeFieldName("packages");
        jsonGenerator.writeStartArray(); // [

        for(ParserComponent pkg : document.getPackages())
            writePackage(jsonGenerator, document, pkg); // For each package in the Document, write its data as an object

        jsonGenerator.writeEndArray(); // ]

        //
        // Files
        //

        if(document.getFiles().size() > 0) {
            jsonGenerator.writeFieldName("files");
            jsonGenerator.writeStartArray();

            for(Map.Entry<String, String> entry : document.getFiles().entrySet()) {
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

        if(document.getRelationships().size() > 0) { // If at least one Relationship exists, write this field
            jsonGenerator.writeFieldName("relationships");
            jsonGenerator.writeStartArray(); // [

            // Loop through all SPDXRelationships and write each as an object
            for(Relationship relationship : document.getRelationships())
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
    private void writePackage(JsonGenerator jsonGenerator, Document document, ParserComponent pkg) throws IOException {
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
        if(pkg.getCPE().size() + pkg.getPURL().size() + pkg.getSWID().size() > 0) {
            jsonGenerator.writeFieldName("externalRefs");
            jsonGenerator.writeStartArray(); // [

            //
            // Add PURLs
            //

            // If any PURLs exist
            if(pkg.getPURL().size() > 0) { // TODO Do this for CPEs and SWIDs once we support them
                for(String purl : pkg.getPURL())
                    // Write the external reference data of the PURL to an object
                    writeExternalRef(jsonGenerator, REFERENCE_CATEGORY.SECURITY, "purl", purl);
            }

            jsonGenerator.writeEndArray(); // ]
        }

        //
        // File analysis
        //

        if(pkg.getFile() != null) {
            jsonGenerator.writeBooleanField("filesAnalyzed", true);
            jsonGenerator.writeFieldName("hasFiles");
            jsonGenerator.writeStartArray();

            // TODO we can have multiple file references in here
            // Write current package file reference ID (files will be generated after packages)
            jsonGenerator.writeString(document.getFiles().get(pkg.getFile()));

            jsonGenerator.writeEndArray();
        } else {
            jsonGenerator.writeBooleanField("filesAnalyzed", false);
        }

        //
        // Licenses
        //

        if(pkg.getResolvedLicenses().size() > 0) {
            Set<String> shortStrings = pkg.getResolvedLicenses().stream().map(License::getSpdxLicense)
                    .collect(Collectors.toSet());

            String licenseList = String.join(" AND ", shortStrings); // Join with AND for multiple licenses
            jsonGenerator.writeStringField("licenseConcluded", licenseList);
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
        if(pkg.getPublisher() != null && pkg.getPublisher().length() > 0)
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
    private void writeExternalRef(JsonGenerator jsonGenerator, REFERENCE_CATEGORY category, String refType, String refLocator) throws IOException {
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
