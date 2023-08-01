package org.svip.sbom.model.objects.CycloneDX14;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.svip.sbom.builder.objects.schemas.CDX14.CDX14Builder;
import org.svip.sbom.factory.objects.CycloneDX14.CDX14SBOMBuilderFactory;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.shared.metadata.Contact;
import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbom.model.shared.metadata.CreationTool;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.shared.util.Description;
import org.svip.sbom.model.shared.util.ExternalReference;
import org.svip.compare.conflicts.Conflict;
import org.svip.compare.conflicts.MismatchType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * file: CDX14SBOMTest.java
 * File to test the comparison of a CDX14 SBOM
 *
 * @author Thomas Roman
 */
public class CDX14SBOMTest {
    static CDX14SBOMBuilderFactory sbomBuilderFactory = new CDX14SBOMBuilderFactory();
    static SBOM controlSBOM;
    static SBOM equalSBOM;

    @BeforeAll
    public static void createTargetPackage(){
        // Build Control SBOM
        Organization organization = new Organization("Control Inc.", "www.control.io");
        Description description = new Description("This is the control component.");
        Contact contact = new Contact("Control Name", "Control Email", "Control Phone");
        ExternalReference externalReferenceOne = new ExternalReference("url","www.refOne.com", "controlRef");
        //ExternalReference externalReferenceTwo = new ExternalReference("url","www.ref2.com", "controlRef");

        // TODO: Creation Data
        CreationData creationData = new CreationData();
        CreationTool creationTool = new CreationTool();
        creationTool.setVendor("Control Vendor");
        creationTool.setVersion("Control Version");
        creationTool.setName("Control Name");
        creationTool.addHash("Control Algorithm", "Control Hash");
        creationData.addCreationTool(creationTool);
        creationData.setCreationTime("Control Time");
        creationData.setCreatorComment("Control Comment");
        creationData.setSupplier(organization);
        creationData.setManufacture(organization);
        creationData.addAuthor(contact);
        creationData.addLicense("Control License");

        // TODO: Root Component
        // TODO: Relationships
        // TODO: Components?


        CDX14Builder sbomBuilder = sbomBuilderFactory.createBuilder();
        sbomBuilder.setFormat("CDX");
        sbomBuilder.setName("Control SBOM");
        sbomBuilder.setUID("123456789");
        sbomBuilder.setVersion("1.0");
        sbomBuilder.setSpecVersion("2.3");
        sbomBuilder.addLicense("Control License One");
        sbomBuilder.setDocumentComment("Control Document Comment");
        sbomBuilder.setCreationData(creationData);

        sbomBuilder.addExternalReference(externalReferenceOne);
        //packageBuilder.addExternalReference(externalReferenceTwo);
        controlSBOM = sbomBuilder.buildCDX14SBOM();
    }

    @Test
    public void conflicts_isEmpty_equals_true_when_testPackage_equals_controlPackage_test() {
        // Build Equal SBOM
        Organization organization = new Organization("Control Inc.", "www.control.io");
        Description description = new Description("This is the control component.");
        Contact contact = new Contact("Control Name", "Control Email", "Control Phone");
        ExternalReference externalReferenceOne = new ExternalReference("url","www.refOne.com", "controlRef");
        //ExternalReference externalReferenceTwo = new ExternalReference("url","www.ref2.com", "controlRef");

        // TODO: Creation Data
        CreationData creationData = new CreationData();
        CreationTool creationTool = new CreationTool();
        creationTool.setVendor("Control Vendor");
        creationTool.setVersion("Control Version");
        creationTool.setName("Control Name");
        creationTool.addHash("Control Algorithm", "Control Hash");
        creationData.addCreationTool(creationTool);
        creationData.setCreationTime("Control Time");
        creationData.setCreatorComment("Control Comment");
        creationData.setSupplier(organization);
        creationData.setManufacture(organization);
        creationData.addAuthor(contact);
        creationData.addLicense("Control License");

        // TODO: Root Component
        // TODO: Relationships
        // TODO: Components?

        CDX14Builder sbomBuilder = sbomBuilderFactory.createBuilder();
        sbomBuilder.setFormat("CDX");
        sbomBuilder.setName("Control SBOM");
        sbomBuilder.setUID("123456789");
        sbomBuilder.setVersion("1.0");
        sbomBuilder.setSpecVersion("2.3");
        sbomBuilder.addLicense("Control License One");
        sbomBuilder.setDocumentComment("Control Document Comment");
        sbomBuilder.setCreationData(creationData);

        sbomBuilder.addExternalReference(externalReferenceOne);
        //packageBuilder.addExternalReference(externalReferenceTwo);
        equalSBOM = sbomBuilder.buildCDX14SBOM();

        // Check there are no conflicts
        SBOM sbom = equalSBOM;
        List<Conflict> conflicts = controlSBOM.compare(sbom);
        assertEquals(0, conflicts.size());
    }

    @Test
    public void all_fields_conflict_when_unequalPackage_compared_to_controlPackage_test() {
        // Build Unequal SBOM
        Organization organization = new Organization("Inc.", "www.org.io");
        Description description = new Description("This is the unequal component.");
        Contact contact = new Contact("Name", "Email", "Phone");
        ExternalReference externalReferenceOne = new ExternalReference("ref","www.one.com", "ref");
        //ExternalReference externalReferenceTwo = new ExternalReference("url","www.ref2.com", "controlRef");

        // TODO: Creation Data
        CreationData creationData = new CreationData();
        CreationTool creationTool = new CreationTool();
        creationTool.setVendor("Vendor");
        creationTool.setVersion("Version");
        creationTool.setName("Name");
        creationTool.addHash("Algorithm", "Hash");
        creationData.addCreationTool(creationTool);
        creationData.setCreationTime("Time");
        creationData.setCreatorComment("Comment");
        creationData.setSupplier(organization);
        creationData.setManufacture(organization);
        creationData.addAuthor(contact);
        creationData.addLicense("License");

        // TODO: Root Component
        // TODO: Relationships
        // TODO: Components?

        CDX14Builder sbomBuilder = sbomBuilderFactory.createBuilder();
        sbomBuilder.setFormat("SPDX");
        sbomBuilder.setName("SBOM");
        sbomBuilder.setUID("0");
        sbomBuilder.setVersion("10");
        sbomBuilder.setSpecVersion("1.4");
        sbomBuilder.addLicense("License One");
        sbomBuilder.setDocumentComment("Document Comment");
        sbomBuilder.setCreationData(creationData);

        sbomBuilder.addExternalReference(externalReferenceOne);
        //packageBuilder.addExternalReference(externalReferenceTwo);
        SBOM unequalSbom = sbomBuilder.buildCDX14SBOM();

        List<Conflict> conflicts = controlSBOM.compare(unequalSbom);

        // TODO This is terribly inefficient

        for(Conflict c : conflicts)
        {

            switch(c.GetMessage())
            {
                case "Document Comment doesn't match", "UID doesn't match"
                        -> assertEquals(MismatchType.MISC_MISMATCH, c.GetType());
                case "Format doesn't match" -> assertEquals(MismatchType.ORIGIN_FORMAT_MISMATCH, c.GetType());
                case "Name doesn't match" -> assertEquals(MismatchType.NAME_MISMATCH, c.GetType());
                case "Version doesn't match" -> assertEquals(MismatchType.VERSION_MISMATCH, c.GetType());
                case "Spec Version doesn't match" -> assertEquals(MismatchType.SCHEMA_VERSION_MISMATCH, c.GetType());
                case "License doesn't match", "License List Version doesn't match" -> assertEquals(MismatchType.LICENSE_MISMATCH, c.GetType());
                case "Author doesn't match" -> assertEquals(MismatchType.AUTHOR_MISMATCH, c.GetType());
            }
        }

        // Unexpected number of results means something isn't working right
        assertEquals(conflicts.size(), 22);
    }
}
