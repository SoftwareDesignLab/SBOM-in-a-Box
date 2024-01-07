/ **
* Copyright 2021 Rochester Institute of Technology (RIT). Developed with
* government support under contract 70RCSA22C00000008 awarded by the United
* States Department of Homeland Security for Cybersecurity and Infrastructure Security Agency.
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the “Software”), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
* /

package org.svip.generation.serializers.serializer;

import org.junit.jupiter.api.BeforeEach;
import org.svip.sbom.builder.objects.SVIPComponentBuilder;
import org.svip.sbom.builder.objects.SVIPSBOMBuilder;
import org.svip.sbom.factory.objects.SVIPSBOMBuilderFactory;
import org.svip.sbom.factory.objects.SVIPSBOMComponentFactory;
import org.svip.sbom.model.objects.SVIPComponentObject;
import org.svip.sbom.model.objects.SVIPSBOM;
import org.svip.sbom.model.shared.Relationship;
import org.svip.sbom.model.shared.metadata.Contact;
import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbom.model.shared.metadata.CreationTool;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.shared.util.Description;
import org.svip.sbom.model.shared.util.ExternalReference;
import org.svip.sbom.model.shared.util.LicenseCollection;
import org.svip.serializers.Metadata;
import org.svip.serializers.serializer.Serializer;
import org.svip.utils.Debug;

import java.util.function.Function;

public class SerializerTest {
    private final Serializer serializer;

    private final SVIPSBOMBuilderFactory sbomFactory;

    private final SVIPSBOMComponentFactory componentFactory;

    private final SVIPSBOM testSBOM;

    public SerializerTest(Serializer serializer) {
        this.serializer = serializer;
        this.sbomFactory = new SVIPSBOMBuilderFactory();
        this.componentFactory = new SVIPSBOMComponentFactory();

        this.testSBOM = buildTestSBOM();

        Debug.enableDebug();
    }

    private SVIPSBOM buildTestSBOM() {
        SVIPSBOMBuilder sbomBuilder = sbomFactory.createBuilder();

        // Creation Data
        CreationData creationData = new CreationData();
        creationData.addAuthor(new Contact("Test Author", "author@publisher.xyz", "123-456-7890"));
        creationData.addLicense("MIT");
        creationData.addProperty("testProperty", "testValue");
        creationData.setCreationTime("TEST TIMESTAMP");
        creationData.setCreatorComment("This SBOM was created for serializer testing");

        // Tool
        CreationTool tool = new CreationTool();
        tool.setName(Metadata.NAME);
        tool.setVersion(Metadata.VERSION);
        tool.setVendor(Metadata.VENDOR);
        tool.addHash("SHA256", "hash");
        creationData.addCreationTool(tool);

        // Supplier
        Organization supplier = new Organization("Supplier", "svip.xyz");
        supplier.addContact(new Contact("Supplier", "supplier@svip.xyz", "123-456-7890"));
        creationData.setSupplier(supplier);

        // Manufacturer
        Organization manufacturer = new Organization("Manufacturer", "svip.xyz");
        manufacturer.addContact(new Contact("SVIP", "manufacturer@svip.xyz", "123-456-7890"));
        creationData.setManufacture(manufacturer);

        // SBOM Data
        sbomBuilder.setCreationData(creationData);
        sbomBuilder.setFormat("CycloneDX");
        sbomBuilder.setName("Test SBOM");
        sbomBuilder.setUID("12345678");
        sbomBuilder.setRootComponent(buildTestComponent(0 ,false));
        sbomBuilder.setDocumentComment("Test Document Comment");
        sbomBuilder.setSPDXLicenseListVersion("0.0");
        sbomBuilder.setSpecVersion("1.4");
        sbomBuilder.setVersion("1.0.0");
        sbomBuilder.addLicense("MIT");

        ExternalReference ref = new ExternalReference("testCategory", "svip.xyz", "testRef");
        ref.addHash("SHA256", "hash");
        sbomBuilder.addExternalReference(ref);

        sbomBuilder.addComponent(buildTestComponent(1, false));
        sbomBuilder.addComponent(buildTestComponent(2, true));
        sbomBuilder.addComponent(buildTestComponent(3, false));

        Relationship relationship = new Relationship("uid3", "DESCRIBES");
        relationship.setComment("Test Relationship Comment");
        sbomBuilder.addRelationship("uid1", relationship);

        return (SVIPSBOM) sbomBuilder.Build();
    }

    private SVIPComponentObject buildTestComponent(int id, boolean file) {
        SVIPComponentBuilder componentBuilder = componentFactory.createBuilder();

        Function<String, String> nameAttribute = s -> s + id;

        if (file) componentBuilder.setFileNotice(nameAttribute.apply("fileNotice"));

        // Primitive/string properties
        componentBuilder.setMimeType(nameAttribute.apply("mimeType"));
        componentBuilder.setPublisher(nameAttribute.apply("publisher"));
        componentBuilder.setScope(nameAttribute.apply("scope"));
        componentBuilder.setGroup(nameAttribute.apply("group"));
        componentBuilder.setType(nameAttribute.apply("type"));
        componentBuilder.setUID(nameAttribute.apply("uid"));
        componentBuilder.setAuthor(nameAttribute.apply("author"));
        componentBuilder.setName(nameAttribute.apply("COMPONENT "));
        componentBuilder.setCopyright(nameAttribute.apply("copyright"));
        componentBuilder.setVersion(nameAttribute.apply("version"));
        componentBuilder.addCPE(nameAttribute.apply("cpe"));
        componentBuilder.addPURL(nameAttribute.apply("purl"));
        componentBuilder.setComment(nameAttribute.apply("comment"));
        componentBuilder.setAttributionText(nameAttribute.apply("attributionText"));
        componentBuilder.setDownloadLocation(nameAttribute.apply("downloadLocation"));
        componentBuilder.setFileName(nameAttribute.apply("fileName"));
        componentBuilder.setFilesAnalyzed(true);
        componentBuilder.setVerificationCode(nameAttribute.apply("verificationCode"));
        componentBuilder.setHomePage(nameAttribute.apply("homePage"));
        componentBuilder.setSourceInfo(nameAttribute.apply("sourceInfo"));
        componentBuilder.setReleaseDate(nameAttribute.apply("releaseDate"));
        componentBuilder.setBuildDate(nameAttribute.apply("buildDate"));
        componentBuilder.setValidUntilDate(nameAttribute.apply("validUntilDate"));
        componentBuilder.setComment(nameAttribute.apply("comment"));

        componentBuilder.addProperty(nameAttribute.apply("property"), nameAttribute.apply("value"));
        componentBuilder.addHash("SHA256", nameAttribute.apply("hash"));

        // Complex SVIP object properties
        // Licenses
        LicenseCollection licenses = new LicenseCollection();
        licenses.addConcludedLicenseString(nameAttribute.apply("concluded"));
        licenses.addDeclaredLicense(nameAttribute.apply("declared"));
        licenses.addLicenseInfoFromFile(nameAttribute.apply("licenseFileText"));
        licenses.setComment(nameAttribute.apply("comment"));
        componentBuilder.setLicenses(licenses);

        // Description
        Description description = new Description(nameAttribute.apply("summary"));
        description.setDescription(nameAttribute.apply("extendedDescription"));
        componentBuilder.setDescription(description);

        // Supplier
        Organization supplier = new Organization(nameAttribute.apply("SVIP"), id + ".svip.xyz");
        supplier.addContact(new Contact("SVIP", "svip@svip.xyz", "123-456-7890"));
        componentBuilder.setSupplier(supplier);

        // External Ref
        ExternalReference ref = new ExternalReference("testCategory", id + ".svip.xyz", nameAttribute.apply("testRef"));
        ref.addHash("SHA256", nameAttribute.apply("hash"));
        componentBuilder.addExternalReference(ref);

        return componentBuilder.build();
    }

    @BeforeEach
    public void setup() {
        serializer.setPrettyPrinting(true);
    }

    public Serializer getSerializer() {
        return serializer;
    }

    public SVIPSBOM getTestSBOM() {
        return testSBOM;
    }
}
