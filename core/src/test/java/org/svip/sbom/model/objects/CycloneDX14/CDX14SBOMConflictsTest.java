package org.svip.sbom.model.objects.CycloneDX14;

import org.junit.jupiter.api.Test;
import org.svip.sbom.builder.objects.schemas.CDX14.CDX14Builder;
import org.svip.sbom.factory.objects.CycloneDX14.CDX14SBOMBuilderFactory;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbomanalysis.comparison.conflicts.Conflict;
import org.svip.sbomanalysis.comparison.conflicts.MismatchType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CDX14SBOMConflictsTest {
    static CDX14SBOMBuilderFactory sbomBuilderFactory = new CDX14SBOMBuilderFactory();
    static SBOM controlSBOM;
    static SBOM conflictSBOM;

    @Test
    public void Format_is_Conflicting_between_testPackage_and_controlPackage_test()
    {
        CDX14Builder sbomBuilder = sbomBuilderFactory.createBuilder();
        sbomBuilder.setFormat("control");
        controlSBOM = sbomBuilder.buildCDX14SBOM();
        sbomBuilder.setFormat("format");
        conflictSBOM = sbomBuilder.buildCDX14SBOM();

        List<Conflict> conflictList = controlSBOM.compare(conflictSBOM);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.ORIGIN_FORMAT_MISMATCH, conflict.GetType());
        assertEquals("Format doesn't match", conflict.GetMessage());
    }

    @Test
    public void Name_is_Conflicting_between_testPackage_and_controlPackage_test()
    {
        CDX14Builder sbomBuilder = sbomBuilderFactory.createBuilder();
        sbomBuilder.setName("control");
        controlSBOM = sbomBuilder.buildCDX14SBOM();
        sbomBuilder.setName("name");
        conflictSBOM = sbomBuilder.buildCDX14SBOM();

        List<Conflict> conflictList = controlSBOM.compare(conflictSBOM);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.NAME_MISMATCH, conflict.GetType());
        assertEquals("Name doesn't match", conflict.GetMessage());
    }

    @Test
    public void UID_is_Conflicting_between_testPackage_and_controlPackage_test()
    {
        CDX14Builder sbomBuilder = sbomBuilderFactory.createBuilder();
        sbomBuilder.setUID("control");
        controlSBOM = sbomBuilder.buildCDX14SBOM();
        sbomBuilder.setUID("uid");
        conflictSBOM = sbomBuilder.buildCDX14SBOM();

        List<Conflict> conflictList = controlSBOM.compare(conflictSBOM);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.MISC_MISMATCH, conflict.GetType());
        assertEquals("UID doesn't match", conflict.GetMessage());
    }

    @Test
    public void Version_is_Conflicting_between_testPackage_and_controlPackage_test()
    {
        CDX14Builder sbomBuilder = sbomBuilderFactory.createBuilder();
        sbomBuilder.setVersion("control");
        controlSBOM = sbomBuilder.buildCDX14SBOM();
        sbomBuilder.setVersion("version");
        conflictSBOM = sbomBuilder.buildCDX14SBOM();

        List<Conflict> conflictList = controlSBOM.compare(conflictSBOM);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.VERSION_MISMATCH, conflict.GetType());
        assertEquals("Version doesn't match", conflict.GetMessage());
    }

    @Test
    public void SpecVersion_is_Conflicting_between_testPackage_and_controlPackage_test()
    {
        CDX14Builder sbomBuilder = sbomBuilderFactory.createBuilder();
        sbomBuilder.setSpecVersion("control");
        controlSBOM = sbomBuilder.buildCDX14SBOM();
        sbomBuilder.setSpecVersion("spec version");
        conflictSBOM = sbomBuilder.buildCDX14SBOM();

        List<Conflict> conflictList = controlSBOM.compare(conflictSBOM);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.SCHEMA_VERSION_MISMATCH, conflict.GetType());
        assertEquals("Spec Version doesn't match", conflict.GetMessage());
    }

    @Test
    public void License_is_Conflicting_between_testPackage_and_controlPackage_test()
    {
        CDX14Builder sbomBuilder = sbomBuilderFactory.createBuilder();
        sbomBuilder.addLicense("control");
        controlSBOM = sbomBuilder.buildCDX14SBOM();
        CDX14Builder sbomBuilder2 = sbomBuilderFactory.createBuilder();
        sbomBuilder2.addLicense("license");
        conflictSBOM = sbomBuilder2.buildCDX14SBOM();

        List<Conflict> conflictList = controlSBOM.compare(conflictSBOM);
        Conflict conflict = conflictList.get(0);

        assertEquals(2, conflictList.size());
        assertEquals(MismatchType.MISSING, conflict.GetType());
        assertEquals("License is missing", conflict.GetMessage());
    }

    @Test
    public void DocumentComment_is_Conflicting_between_testPackage_and_controlPackage_test()
    {
        CDX14Builder sbomBuilder = sbomBuilderFactory.createBuilder();
        sbomBuilder.setDocumentComment("control");
        controlSBOM = sbomBuilder.buildCDX14SBOM();
        sbomBuilder.setDocumentComment("doc comment");
        conflictSBOM = sbomBuilder.buildCDX14SBOM();

        List<Conflict> conflictList = controlSBOM.compare(conflictSBOM);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.MISC_MISMATCH, conflict.GetType());
        assertEquals("Document Comment doesn't match", conflict.GetMessage());
    }

    @Test
    public void CreationTime_is_Conflicting_between_testPackage_and_controlPackage_test()
    {
        CDX14Builder sbomBuilder = sbomBuilderFactory.createBuilder();
        CreationData creationData = new CreationData();
        creationData.setCreationTime("control");
        sbomBuilder.setCreationData(creationData);
        controlSBOM = sbomBuilder.buildCDX14SBOM();
        CreationData creationData2 = new CreationData();
        creationData2.setCreationTime("creation time");
        sbomBuilder.setCreationData(creationData2);
        conflictSBOM = sbomBuilder.buildCDX14SBOM();

        List<Conflict> conflictList = controlSBOM.compare(conflictSBOM);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.TIMESTAMP_MISMATCH, conflict.GetType());
        assertEquals("Timestamp doesn't match", conflict.GetMessage());
    }

    @Test
    public void CreatorComment_is_Conflicting_between_testPackage_and_controlPackage_test()
    {
        CDX14Builder sbomBuilder = sbomBuilderFactory.createBuilder();
        CreationData creationData = new CreationData();
        creationData.setCreatorComment("control");
        sbomBuilder.setCreationData(creationData);
        controlSBOM = sbomBuilder.buildCDX14SBOM();
        CreationData creationData2 = new CreationData();
        creationData2.setCreatorComment("creator comment");
        sbomBuilder.setCreationData(creationData2);
        conflictSBOM = sbomBuilder.buildCDX14SBOM();

        List<Conflict> conflictList = controlSBOM.compare(conflictSBOM);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.MISC_MISMATCH, conflict.GetType());
        assertEquals("Creator Comment doesn't match", conflict.GetMessage());
    }

    @Test
    public void Supplier_is_Conflicting_between_testPackage_and_controlPackage_test()
    {

    }

    @Test
    public void Manufacture_is_Conflicting_between_testPackage_and_controlPackage_test()
    {

    }

    @Test
    public void Author_is_Conflicting_between_testPackage_and_controlPackage_test()
    {

    }

    @Test
    public void CreationTool_is_Conflicting_between_testPackage_and_controlPackage_test()
    {

    }
}
