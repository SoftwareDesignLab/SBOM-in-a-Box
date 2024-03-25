/** Copyright 2021 Rochester Institute of Technology (RIT). Developed with
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
 */

package org.svip.sbom.model.objects.SPDX23;

import org.junit.jupiter.api.Test;
import org.svip.sbom.builder.objects.schemas.SPDX23.SPDX23Builder;
import org.svip.sbom.factory.objects.SPDX23.SPDX23SBOMBuilderFactory;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.shared.metadata.Contact;
import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbom.model.shared.metadata.CreationTool;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.compare.conflicts.Conflict;
import org.svip.compare.conflicts.MismatchType;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * file: SPDX23SBOMConflictsTest.java
 * File to test the individual comparison methods in the SPDX23 SBOM Object
 *
 * @author Thomas Roman
 */
public class SPDX23SBOMConflictsTest {
    static SPDX23SBOMBuilderFactory sbomBuilderFactory = new SPDX23SBOMBuilderFactory();
    static SBOM controlSBOM;
    static SBOM conflictSBOM;

    @Test
    public void Format_is_Conflicting_between_testPackage_and_controlPackage_test()
    {
        SPDX23Builder sbomBuilder = sbomBuilderFactory.createBuilder();
        sbomBuilder.setFormat("control");
        controlSBOM = sbomBuilder.buildSPDX23SBOM();
        sbomBuilder.setFormat("format");
        conflictSBOM = sbomBuilder.buildSPDX23SBOM();

        List<Conflict> conflictList = controlSBOM.compare(conflictSBOM);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.ORIGIN_FORMAT_MISMATCH, conflict.getType());
        assertEquals("Format doesn't match", conflict.getMessage());
    }

    @Test
    public void Name_is_Conflicting_between_testPackage_and_controlPackage_test()
    {
        SPDX23Builder sbomBuilder = sbomBuilderFactory.createBuilder();
        sbomBuilder.setName("control");
        controlSBOM = sbomBuilder.buildSPDX23SBOM();
        sbomBuilder.setName("name");
        conflictSBOM = sbomBuilder.buildSPDX23SBOM();

        List<Conflict> conflictList = controlSBOM.compare(conflictSBOM);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.NAME_MISMATCH, conflict.getType());
        assertEquals("Name doesn't match", conflict.getMessage());
    }

    @Test
    public void UID_is_Conflicting_between_testPackage_and_controlPackage_test()
    {
        SPDX23Builder sbomBuilder = sbomBuilderFactory.createBuilder();
        sbomBuilder.setUID("control");
        controlSBOM = sbomBuilder.buildSPDX23SBOM();
        sbomBuilder.setUID("uid");
        conflictSBOM = sbomBuilder.buildSPDX23SBOM();

        List<Conflict> conflictList = controlSBOM.compare(conflictSBOM);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.MISC_MISMATCH, conflict.getType());
        assertEquals("UID doesn't match", conflict.getMessage());
    }

    @Test
    public void Version_is_Conflicting_between_testPackage_and_controlPackage_test()
    {
        SPDX23Builder sbomBuilder = sbomBuilderFactory.createBuilder();
        sbomBuilder.setVersion("control");
        controlSBOM = sbomBuilder.buildSPDX23SBOM();
        sbomBuilder.setVersion("version");
        conflictSBOM = sbomBuilder.buildSPDX23SBOM();

        List<Conflict> conflictList = controlSBOM.compare(conflictSBOM);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.VERSION_MISMATCH, conflict.getType());
        assertEquals("Version doesn't match", conflict.getMessage());
    }

    @Test
    public void SpecVersion_is_Conflicting_between_testPackage_and_controlPackage_test()
    {
        SPDX23Builder sbomBuilder = sbomBuilderFactory.createBuilder();
        sbomBuilder.setSpecVersion("control");
        controlSBOM = sbomBuilder.buildSPDX23SBOM();
        sbomBuilder.setSpecVersion("spec version");
        conflictSBOM = sbomBuilder.buildSPDX23SBOM();

        List<Conflict> conflictList = controlSBOM.compare(conflictSBOM);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.SCHEMA_VERSION_MISMATCH, conflict.getType());
        assertEquals("Spec Version doesn't match", conflict.getMessage());
    }

    @Test
    public void License_is_Conflicting_between_testPackage_and_controlPackage_test()
    {
        SPDX23Builder sbomBuilder = sbomBuilderFactory.createBuilder();
        sbomBuilder.addLicense("control");
        controlSBOM = sbomBuilder.buildSPDX23SBOM();
        SPDX23Builder sbomBuilder2 = sbomBuilderFactory.createBuilder();
        sbomBuilder2.addLicense("license");
        conflictSBOM = sbomBuilder2.buildSPDX23SBOM();

        List<Conflict> conflictList = controlSBOM.compare(conflictSBOM);
        Conflict conflict = conflictList.get(0);

        assertEquals(2, conflictList.size());
        assertEquals(MismatchType.MISSING, conflict.getType());
        assertEquals("License is missing", conflict.getMessage());
    }

    @Test
    public void DocumentComment_is_Conflicting_between_testPackage_and_controlPackage_test()
    {
        SPDX23Builder sbomBuilder = sbomBuilderFactory.createBuilder();
        sbomBuilder.setDocumentComment("control");
        controlSBOM = sbomBuilder.buildSPDX23SBOM();
        sbomBuilder.setDocumentComment("doc comment");
        conflictSBOM = sbomBuilder.buildSPDX23SBOM();

        List<Conflict> conflictList = controlSBOM.compare(conflictSBOM);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.MISC_MISMATCH, conflict.getType());
        assertEquals("Document Comment doesn't match", conflict.getMessage());
    }

    @Test
    public void SPDXLicenseListVersion_is_Conflicting_between_testPackage_and_controlPackage_test()
    {
        SPDX23Builder sbomBuilder = sbomBuilderFactory.createBuilder();
        sbomBuilder.setSPDXLicenseListVersion("control");
        controlSBOM = sbomBuilder.buildSPDX23SBOM();
        sbomBuilder.setSPDXLicenseListVersion("license list version");
        conflictSBOM = sbomBuilder.buildSPDX23SBOM();

        List<Conflict> conflictList = controlSBOM.compare(conflictSBOM);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.LICENSE_MISMATCH, conflict.getType());
        assertEquals("SPDX License List Version doesn't match", conflict.getMessage());
    }

    @Test
    public void CreationTime_is_Conflicting_between_testPackage_and_controlPackage_test()
    {
        SPDX23Builder sbomBuilder = sbomBuilderFactory.createBuilder();
        CreationData creationData = new CreationData();
        creationData.setCreationTime("control");
        sbomBuilder.setCreationData(creationData);
        controlSBOM = sbomBuilder.buildSPDX23SBOM();
        CreationData creationData2 = new CreationData();
        creationData2.setCreationTime("creation time");
        sbomBuilder.setCreationData(creationData2);
        conflictSBOM = sbomBuilder.buildSPDX23SBOM();

        List<Conflict> conflictList = controlSBOM.compare(conflictSBOM);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.TIMESTAMP_MISMATCH, conflict.getType());
        assertEquals("Timestamp doesn't match", conflict.getMessage());
    }

    @Test
    public void CreatorComment_is_Conflicting_between_testPackage_and_controlPackage_test()
    {
        SPDX23Builder sbomBuilder = sbomBuilderFactory.createBuilder();
        CreationData creationData = new CreationData();
        creationData.setCreatorComment("control");
        sbomBuilder.setCreationData(creationData);
        controlSBOM = sbomBuilder.buildSPDX23SBOM();
        CreationData creationData2 = new CreationData();
        creationData2.setCreatorComment("creator comment");
        sbomBuilder.setCreationData(creationData2);
        conflictSBOM = sbomBuilder.buildSPDX23SBOM();

        List<Conflict> conflictList = controlSBOM.compare(conflictSBOM);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.MISC_MISMATCH, conflict.getType());
        assertEquals("Creator Comment doesn't match", conflict.getMessage());
    }

    @Test
    public void Supplier_is_Conflicting_between_testPackage_and_controlPackage_test()
    {
        SPDX23Builder sbomBuilder = sbomBuilderFactory.createBuilder();
        CreationData creationData = new CreationData();
        Organization supplier = new Organization("control", "control");
        creationData.setSupplier(supplier);
        sbomBuilder.setCreationData(creationData);
        controlSBOM = sbomBuilder.buildSPDX23SBOM();
        CreationData creationData2 = new CreationData();
        Organization supplier2 = new Organization("name", "url");
        creationData2.setSupplier(supplier2);
        sbomBuilder.setCreationData(creationData2);
        conflictSBOM = sbomBuilder.buildSPDX23SBOM();

        List<Conflict> conflictList = controlSBOM.compare(conflictSBOM);

        boolean c1 = false;
        boolean c2 = false;

        assertEquals(2, conflictList.size());

        for(Conflict c : conflictList)
        {
            if(c.getType() == MismatchType.NAME_MISMATCH && Objects.equals(c.getMessage(), "Organization: Name doesn't match"))
                c1 = true;
            else if(c.getType() == MismatchType.MISC_MISMATCH && Objects.equals(c.getMessage(), "Organization: URL doesn't match"))
                c2 = true;
        }

        assertTrue(c1);
        assertTrue(c2);
    }

    @Test
    public void Manufacture_is_Conflicting_between_testPackage_and_controlPackage_test()
    {
        SPDX23Builder sbomBuilder = sbomBuilderFactory.createBuilder();
        CreationData creationData = new CreationData();
        Organization manufacture = new Organization("control", "control");
        creationData.setManufacture(manufacture);
        sbomBuilder.setCreationData(creationData);
        controlSBOM = sbomBuilder.buildSPDX23SBOM();
        CreationData creationData2 = new CreationData();
        Organization manufacture2 = new Organization("name", "url");
        creationData2.setManufacture(manufacture2);
        sbomBuilder.setCreationData(creationData2);
        conflictSBOM = sbomBuilder.buildSPDX23SBOM();

        List<Conflict> conflictList = controlSBOM.compare(conflictSBOM);

        boolean c1 = false;
        boolean c2 = false;

        assertEquals(2, conflictList.size());

        for(Conflict c : conflictList)
        {
            if(c.getType() == MismatchType.NAME_MISMATCH && Objects.equals(c.getMessage(), "Organization: Name doesn't match"))
                c1 = true;
            else if(c.getType() == MismatchType.MISC_MISMATCH && Objects.equals(c.getMessage(), "Organization: URL doesn't match"))
                c2 = true;
        }

        assertTrue(c1);
        assertTrue(c2);
    }

    @Test
    public void Author_is_Conflicting_between_testPackage_and_controlPackage_test()
    {
        SPDX23Builder sbomBuilder = sbomBuilderFactory.createBuilder();
        CreationData creationData = new CreationData();
        Contact author = new Contact("control", "control", "control");
        creationData.addAuthor(author);
        sbomBuilder.setCreationData(creationData);
        controlSBOM = sbomBuilder.buildSPDX23SBOM();
        CreationData creationData2 = new CreationData();
        Contact author2 = new Contact("name", "email", "phone");
        creationData2.addAuthor(author2);
        sbomBuilder.setCreationData(creationData2);
        conflictSBOM = sbomBuilder.buildSPDX23SBOM();

        List<Conflict> conflictList = controlSBOM.compare(conflictSBOM);
        Conflict conflict = conflictList.get(0);

        assertEquals(2, conflictList.size());
        assertEquals(MismatchType.MISSING, conflict.getType());
        assertEquals("Creation Data: Author is missing", conflict.getMessage());
    }

    @Test
    public void CreationTool_is_Conflicting_between_testPackage_and_controlPackage_test()
    {
        SPDX23Builder sbomBuilder = sbomBuilderFactory.createBuilder();
        CreationData creationData = new CreationData();
        CreationTool creationTool = new CreationTool();
        creationTool.setVendor("Control Vendor");
        creationTool.setVersion("Control Version");
        creationTool.setName("Control Name");
        creationTool.addHash("Control Algorithm", "Control Hash");
        creationData.addCreationTool(creationTool);
        sbomBuilder.setCreationData(creationData);
        controlSBOM = sbomBuilder.buildSPDX23SBOM();
        CreationData creationData2 = new CreationData();
        CreationTool creationTool2 = new CreationTool();
        creationTool2.setVendor("Vendor");
        creationTool2.setVersion("Version");
        creationTool2.setName("Name");
        creationTool2.addHash("Algorithm", "Hash");
        creationData2.addCreationTool(creationTool2);
        sbomBuilder.setCreationData(creationData2);
        conflictSBOM = sbomBuilder.buildSPDX23SBOM();

        List<Conflict> conflictList = controlSBOM.compare(conflictSBOM);
        Conflict conflict = conflictList.get(0);

        assertEquals(2, conflictList.size());
        assertEquals(MismatchType.MISSING, conflict.getType());
        assertEquals("Creation Data: Tool is missing", conflict.getMessage());
    }
}
