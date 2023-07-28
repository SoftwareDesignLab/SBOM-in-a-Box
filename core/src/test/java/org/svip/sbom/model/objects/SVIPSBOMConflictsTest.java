package org.svip.sbom.model.objects;

import org.junit.jupiter.api.Test;
import org.svip.sbom.builder.objects.SVIPSBOMBuilder;
import org.svip.sbom.builder.objects.schemas.SPDX23.SPDX23Builder;
import org.svip.sbom.factory.objects.SVIPSBOMBuilderFactory;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.shared.metadata.Contact;
import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbom.model.shared.metadata.CreationTool;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbomanalysis.comparison.conflicts.Conflict;
import org.svip.sbomanalysis.comparison.conflicts.MismatchType;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * file: SVIPSBOMConflictsTest.java
 * File to test the individual comparison methods in the SVIP SBOM Object
 *
 * @author Thomas Roman
 */
public class SVIPSBOMConflictsTest {
    static SVIPSBOMBuilderFactory sbomBuilderFactory = new SVIPSBOMBuilderFactory();
    static SBOM controlSBOM;
    static SBOM conflictSBOM;

    @Test
    public void Format_is_Conflicting_between_testPackage_and_controlPackage_test()
    {
        SVIPSBOMBuilder sbomBuilder = sbomBuilderFactory.createBuilder();
        sbomBuilder.setFormat("control");
        controlSBOM = sbomBuilder.Build();
        sbomBuilder.setFormat("format");
        conflictSBOM = sbomBuilder.Build();

        List<Conflict> conflictList = controlSBOM.compare(conflictSBOM);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.ORIGIN_FORMAT_MISMATCH, conflict.GetType());
        assertEquals("Format doesn't match", conflict.GetMessage());
    }

    @Test
    public void Name_is_Conflicting_between_testPackage_and_controlPackage_test()
    {
        SVIPSBOMBuilder sbomBuilder = sbomBuilderFactory.createBuilder();
        sbomBuilder.setName("control");
        controlSBOM = sbomBuilder.Build();
        sbomBuilder.setName("name");
        conflictSBOM = sbomBuilder.Build();

        List<Conflict> conflictList = controlSBOM.compare(conflictSBOM);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.NAME_MISMATCH, conflict.GetType());
        assertEquals("Name doesn't match", conflict.GetMessage());
    }

    @Test
    public void UID_is_Conflicting_between_testPackage_and_controlPackage_test()
    {
        SVIPSBOMBuilder sbomBuilder = sbomBuilderFactory.createBuilder();
        sbomBuilder.setUID("control");
        controlSBOM = sbomBuilder.Build();
        sbomBuilder.setUID("uid");
        conflictSBOM = sbomBuilder.Build();

        List<Conflict> conflictList = controlSBOM.compare(conflictSBOM);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.MISC_MISMATCH, conflict.GetType());
        assertEquals("UID doesn't match", conflict.GetMessage());
    }

    @Test
    public void Version_is_Conflicting_between_testPackage_and_controlPackage_test()
    {
        SVIPSBOMBuilder sbomBuilder = sbomBuilderFactory.createBuilder();
        sbomBuilder.setVersion("control");
        controlSBOM = sbomBuilder.Build();
        sbomBuilder.setVersion("version");
        conflictSBOM = sbomBuilder.Build();

        List<Conflict> conflictList = controlSBOM.compare(conflictSBOM);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.VERSION_MISMATCH, conflict.GetType());
        assertEquals("Version doesn't match", conflict.GetMessage());
    }

    @Test
    public void SpecVersion_is_Conflicting_between_testPackage_and_controlPackage_test()
    {
        SVIPSBOMBuilder sbomBuilder = sbomBuilderFactory.createBuilder();
        sbomBuilder.setSpecVersion("control");
        controlSBOM = sbomBuilder.Build();
        sbomBuilder.setSpecVersion("spec version");
        conflictSBOM = sbomBuilder.Build();

        List<Conflict> conflictList = controlSBOM.compare(conflictSBOM);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.SCHEMA_VERSION_MISMATCH, conflict.GetType());
        assertEquals("Spec Version doesn't match", conflict.GetMessage());
    }

    @Test
    public void License_is_Conflicting_between_testPackage_and_controlPackage_test()
    {
        SVIPSBOMBuilder sbomBuilder = sbomBuilderFactory.createBuilder();
        sbomBuilder.addLicense("control");
        controlSBOM = sbomBuilder.Build();
        SVIPSBOMBuilder sbomBuilder2 = sbomBuilderFactory.createBuilder();
        sbomBuilder2.addLicense("license");
        conflictSBOM = sbomBuilder2.Build();

        List<Conflict> conflictList = controlSBOM.compare(conflictSBOM);
        Conflict conflict = conflictList.get(0);

        assertEquals(2, conflictList.size());
        assertEquals(MismatchType.MISSING, conflict.GetType());
        assertEquals("License is missing", conflict.GetMessage());
    }

    @Test
    public void DocumentComment_is_Conflicting_between_testPackage_and_controlPackage_test()
    {
        SVIPSBOMBuilder sbomBuilder = sbomBuilderFactory.createBuilder();
        sbomBuilder.setDocumentComment("control");
        controlSBOM = sbomBuilder.Build();
        sbomBuilder.setDocumentComment("doc comment");
        conflictSBOM = sbomBuilder.Build();

        List<Conflict> conflictList = controlSBOM.compare(conflictSBOM);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.MISC_MISMATCH, conflict.GetType());
        assertEquals("Document Comment doesn't match", conflict.GetMessage());
    }

    @Test
    public void SPDXLicenseListVersion_is_Conflicting_between_testPackage_and_controlPackage_test()
    {
        SVIPSBOMBuilder sbomBuilder = sbomBuilderFactory.createBuilder();
        sbomBuilder.setSPDXLicenseListVersion("control");
        controlSBOM = sbomBuilder.buildSPDX23SBOM();
        sbomBuilder.setSPDXLicenseListVersion("license list version");
        conflictSBOM = sbomBuilder.buildSPDX23SBOM();

        List<Conflict> conflictList = controlSBOM.compare(conflictSBOM);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.LICENSE_MISMATCH, conflict.GetType());
        assertEquals("SPDX License List Version doesn't match", conflict.GetMessage());
    }

    @Test
    public void CreationTime_is_Conflicting_between_testPackage_and_controlPackage_test()
    {
        SVIPSBOMBuilder sbomBuilder = sbomBuilderFactory.createBuilder();
        CreationData creationData = new CreationData();
        creationData.setCreationTime("control");
        sbomBuilder.setCreationData(creationData);
        controlSBOM = sbomBuilder.Build();
        CreationData creationData2 = new CreationData();
        creationData2.setCreationTime("creation time");
        sbomBuilder.setCreationData(creationData2);
        conflictSBOM = sbomBuilder.Build();

        List<Conflict> conflictList = controlSBOM.compare(conflictSBOM);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.TIMESTAMP_MISMATCH, conflict.GetType());
        assertEquals("Timestamp doesn't match", conflict.GetMessage());
    }

    @Test
    public void CreatorComment_is_Conflicting_between_testPackage_and_controlPackage_test()
    {
        SVIPSBOMBuilder sbomBuilder = sbomBuilderFactory.createBuilder();
        CreationData creationData = new CreationData();
        creationData.setCreatorComment("control");
        sbomBuilder.setCreationData(creationData);
        controlSBOM = sbomBuilder.Build();
        CreationData creationData2 = new CreationData();
        creationData2.setCreatorComment("creator comment");
        sbomBuilder.setCreationData(creationData2);
        conflictSBOM = sbomBuilder.Build();

        List<Conflict> conflictList = controlSBOM.compare(conflictSBOM);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.MISC_MISMATCH, conflict.GetType());
        assertEquals("Creator Comment doesn't match", conflict.GetMessage());
    }

    @Test
    public void Supplier_is_Conflicting_between_testPackage_and_controlPackage_test()
    {
        SVIPSBOMBuilder sbomBuilder = sbomBuilderFactory.createBuilder();
        CreationData creationData = new CreationData();
        Organization supplier = new Organization("control", "control");
        creationData.setSupplier(supplier);
        sbomBuilder.setCreationData(creationData);
        controlSBOM = sbomBuilder.Build();
        CreationData creationData2 = new CreationData();
        Organization supplier2 = new Organization("name", "url");
        creationData2.setSupplier(supplier2);
        sbomBuilder.setCreationData(creationData2);
        conflictSBOM = sbomBuilder.Build();

        List<Conflict> conflictList = controlSBOM.compare(conflictSBOM);

        boolean c1 = false;
        boolean c2 = false;

        assertEquals(2, conflictList.size());

        for(Conflict c : conflictList)
        {
            if(c.GetType() == MismatchType.NAME_MISMATCH && Objects.equals(c.GetMessage(), "Organization: Name doesn't match"))
                c1 = true;
            else if(c.GetType() == MismatchType.MISC_MISMATCH && Objects.equals(c.GetMessage(), "Organization: URL doesn't match"))
                c2 = true;
        }

        assertTrue(c1);
        assertTrue(c2);
    }

    @Test
    public void Manufacture_is_Conflicting_between_testPackage_and_controlPackage_test()
    {
        SVIPSBOMBuilder sbomBuilder = sbomBuilderFactory.createBuilder();
        CreationData creationData = new CreationData();
        Organization manufacture = new Organization("control", "control");
        creationData.setManufacture(manufacture);
        sbomBuilder.setCreationData(creationData);
        controlSBOM = sbomBuilder.Build();
        CreationData creationData2 = new CreationData();
        Organization manufacture2 = new Organization("name", "url");
        creationData2.setManufacture(manufacture2);
        sbomBuilder.setCreationData(creationData2);
        conflictSBOM = sbomBuilder.Build();

        List<Conflict> conflictList = controlSBOM.compare(conflictSBOM);

        boolean c1 = false;
        boolean c2 = false;

        assertEquals(2, conflictList.size());

        for(Conflict c : conflictList)
        {
            if(c.GetType() == MismatchType.NAME_MISMATCH && Objects.equals(c.GetMessage(), "Organization: Name doesn't match"))
                c1 = true;
            else if(c.GetType() == MismatchType.MISC_MISMATCH && Objects.equals(c.GetMessage(), "Organization: URL doesn't match"))
                c2 = true;
        }

        assertTrue(c1);
        assertTrue(c2);
    }

    @Test
    public void Author_is_Conflicting_between_testPackage_and_controlPackage_test()
    {
        SVIPSBOMBuilder sbomBuilder = sbomBuilderFactory.createBuilder();
        CreationData creationData = new CreationData();
        Contact author = new Contact("control", "control", "control");
        creationData.addAuthor(author);
        sbomBuilder.setCreationData(creationData);
        controlSBOM = sbomBuilder.Build();
        CreationData creationData2 = new CreationData();
        Contact author2 = new Contact("name", "email", "phone");
        creationData2.addAuthor(author2);
        sbomBuilder.setCreationData(creationData2);
        conflictSBOM = sbomBuilder.Build();

        List<Conflict> conflictList = controlSBOM.compare(conflictSBOM);
        Conflict conflict = conflictList.get(0);

        assertEquals(2, conflictList.size());
        assertEquals(MismatchType.MISSING, conflict.GetType());
        assertEquals("Creation Data: Author is missing", conflict.GetMessage());
    }

    @Test
    public void CreationTool_is_Conflicting_between_testPackage_and_controlPackage_test()
    {
        SVIPSBOMBuilder sbomBuilder = sbomBuilderFactory.createBuilder();
        CreationData creationData = new CreationData();
        CreationTool creationTool = new CreationTool();
        creationTool.setVendor("Control Vendor");
        creationTool.setVersion("Control Version");
        creationTool.setName("Control Name");
        creationTool.addHash("Control Algorithm", "Control Hash");
        creationData.addCreationTool(creationTool);
        sbomBuilder.setCreationData(creationData);
        controlSBOM = sbomBuilder.Build();
        CreationData creationData2 = new CreationData();
        CreationTool creationTool2 = new CreationTool();
        creationTool2.setVendor("Vendor");
        creationTool2.setVersion("Version");
        creationTool2.setName("Name");
        creationTool2.addHash("Algorithm", "Hash");
        creationData2.addCreationTool(creationTool2);
        sbomBuilder.setCreationData(creationData2);
        conflictSBOM = sbomBuilder.Build();

        List<Conflict> conflictList = controlSBOM.compare(conflictSBOM);
        Conflict conflict = conflictList.get(0);

        assertEquals(2, conflictList.size());
        assertEquals(MismatchType.MISSING, conflict.GetType());
        assertEquals("Creation Data: Tool is missing", conflict.GetMessage());
    }
}
