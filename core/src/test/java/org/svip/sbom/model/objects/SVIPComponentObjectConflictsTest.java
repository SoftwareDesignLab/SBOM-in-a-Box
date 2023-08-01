package org.svip.sbom.model.objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.svip.sbom.builder.objects.SVIPComponentBuilder;
import org.svip.sbom.factory.objects.SVIPSBOMComponentFactory;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.shared.util.Description;
import org.svip.sbom.model.shared.util.LicenseCollection;
import org.svip.sbomanalysis.comparison.conflicts.Conflict;
import org.svip.sbomanalysis.comparison.conflicts.MismatchType;
import org.svip.sbomanalysis.qualityattributes.resultfactory.Text;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * file: SVIPComponentObjectConflictsTest.java
 * File to test the individual comparison methods in the SVIP Component Object
 *
 * @author Thomas Roman
 * @author Kevin Laporte
 */
public class SVIPComponentObjectConflictsTest {
    static SVIPSBOMComponentFactory packageBuilderFactory = new SVIPSBOMComponentFactory();
    static SVIPComponentBuilder packageBuilder = packageBuilderFactory.createBuilder();
    static SVIPComponentObject controlPackage = packageBuilder.buildAndFlush();
    static Component conflictPackage;

    @Test
    public void Type_is_Conflicting_between_testPackage_and_controlPackage_test() throws JsonProcessingException {
        packageBuilder.setType("control");
        controlPackage = packageBuilder.buildAndFlush();
        packageBuilder.setType("Type");
        conflictPackage = packageBuilder.buildAndFlush();

        List<Conflict> conflictList = controlPackage.compare(conflictPackage);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.MISC_MISMATCH, conflict.GetType());
        assertEquals("Type doesn't match", conflict.GetMessage());
    }

    @Test
    public void UID_is_conflicting_between_testPackage_and_controlPackage_test() throws JsonProcessingException {
        packageBuilder.setUID("control");
        controlPackage = packageBuilder.buildAndFlush();
        packageBuilder.setUID("123");
        conflictPackage = packageBuilder.buildAndFlush();

        List<Conflict> conflictList = controlPackage.compare(conflictPackage);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.MISC_MISMATCH, conflict.GetType());
        assertEquals("UID doesn't match", conflict.GetMessage());
    }

    @Test
    public void name_is_conflicting_between_testPackage_and_controlPackage_test() throws JsonProcessingException {
        packageBuilder.setName("control");
        controlPackage = packageBuilder.buildAndFlush();
        packageBuilder.setName("name");
        conflictPackage = packageBuilder.buildAndFlush();

        List<Conflict> conflictList = controlPackage.compare(conflictPackage);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.NAME_MISMATCH, conflict.GetType());
        assertEquals("Name doesn't match", conflict.GetMessage());
    }

    @Test
    public void author_is_conflicting_between_testPackage_and_controlPackage_test() throws JsonProcessingException {
        packageBuilder.setAuthor("control");
        controlPackage = packageBuilder.buildAndFlush();
        packageBuilder.setAuthor("author");
        conflictPackage = packageBuilder.buildAndFlush();

        List<Conflict> conflictList = controlPackage.compare(conflictPackage);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.AUTHOR_MISMATCH, conflict.GetType());
        assertEquals("Author doesn't match", conflict.GetMessage());
    }

    // TODO This is still breaking due to casting
    @Test
    public void license_is_conflicting_between_testPackage_and_controlPackage_test() throws JsonProcessingException {
        LicenseCollection licenseCollection = new LicenseCollection();
        licenseCollection.addDeclaredLicense("control license");
        packageBuilder.setLicenses(licenseCollection);
        controlPackage = packageBuilder.buildAndFlush();
        LicenseCollection licenseCollectionTwo = new LicenseCollection();
        licenseCollectionTwo.addDeclaredLicense("license");
        packageBuilder.setLicenses(licenseCollectionTwo);
        conflictPackage = packageBuilder.buildAndFlush();

        List<Conflict> conflictList = controlPackage.compare(conflictPackage);
        boolean c1 = false;
        boolean c2 = false;

        assertEquals(2, conflictList.size());

        // Construct Text to use for diff report conflict messages
        Text text = new Text("Conflict", "License");

        for(Conflict c : conflictList)
        {
            if (c.GetType() == MismatchType.MISSING && Objects.equals(c.GetMessage(), "License is missing")) {
                if(Objects.equals(c.GetTarget(), "control license") && Objects.equals(c.GetOther(), text.getNullItemInSetResponse()))
                    c1 = true;
                else if(Objects.equals(c.GetTarget(), text.getNullItemInSetResponse()) && Objects.equals(c.GetOther(), "license"))
                    c2 = true;
            }
        }

        assertTrue(c1);
        assertTrue(c2);
    }

    @Test
    public void copyright_is_conflicting_between_testPackage_and_controlPackage_test() throws JsonProcessingException {
        packageBuilder.setCopyright("control");
        controlPackage = packageBuilder.buildAndFlush();
        packageBuilder.setCopyright("copyright");
        conflictPackage = packageBuilder.buildAndFlush();

        List<Conflict> conflictList = controlPackage.compare(conflictPackage);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.MISC_MISMATCH, conflict.GetType());
        assertEquals("Copyright doesn't match", conflict.GetMessage());
    }

    // TODO Breaks because of licenses (but why?)
    @Test
    public void componentHash_is_conflicting_between_testPackage_and_controlPackage_test() throws JsonProcessingException {
        packageBuilder.addHash("SHA1", "control");
        controlPackage = packageBuilder.buildAndFlush();
        packageBuilder.addHash("SHA2", "hash");
        conflictPackage = packageBuilder.buildAndFlush();

        List<Conflict> conflictList = controlPackage.compare(conflictPackage);
        boolean c1 = false;
        boolean c2 = false;

        assertEquals(2, conflictList.size());

        // Construct Text to use for diff report conflict messages
        Text text = new Text("Conflict", "Component Hash");

        for(Conflict c : conflictList)
        {
            if(c.GetType() == MismatchType.MISSING && Objects.equals(c.GetMessage(), "Component Hash is missing"))
                if(Objects.equals(c.GetTarget(),"SHA1, control") && Objects.equals(c.GetOther(), text.getNullResponse()))
                    c1 = true;
                else if(Objects.equals(c.GetTarget(), text.getNullResponse()) && Objects.equals(c.GetOther(), "SHA2, hash"))
                    c2 = true;
        }

        assertTrue(c1);
        assertTrue(c2);
    }

    @Test
    public void supplier_is_conflicting_between_testPackage_and_controlPackage_test() throws JsonProcessingException {
        Organization controlOrg = new Organization("Control Org.","www.control.com");
        Organization testOrg = new Organization("Test Org.", "www.test.com");
        packageBuilder.setSupplier(controlOrg);
        controlPackage = packageBuilder.buildAndFlush();
        packageBuilder.setSupplier(testOrg);
        conflictPackage = packageBuilder.buildAndFlush();

        List<Conflict> conflictList = controlPackage.compare(conflictPackage);
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
    public void version_is_conflicting_between_testPackage_and_controlPackage_test() throws JsonProcessingException {
        packageBuilder.setVersion("control");
        controlPackage = packageBuilder.buildAndFlush();
        packageBuilder.setVersion("version");
        conflictPackage = packageBuilder.buildAndFlush();

        List<Conflict> conflictList = controlPackage.compare(conflictPackage);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.VERSION_MISMATCH, conflict.GetType());
        assertEquals("Version doesn't match", conflict.GetMessage());
    }

    @Test
    public void description_is_conflicting_between_testPackage_and_controlPackage_test() throws JsonProcessingException {
        Description controlDesc = new Description("control");
        Description testDesc = new Description("description");
        packageBuilder.setDescription(controlDesc);
        controlPackage = packageBuilder.buildAndFlush();
        packageBuilder.setDescription(testDesc);
        conflictPackage = packageBuilder.buildAndFlush();

        List<Conflict> conflictList = controlPackage.compare(conflictPackage);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.MISC_MISMATCH, conflict.GetType());
        assertEquals("Summary doesn't match", conflict.GetMessage());
    }

    @Test
    public void PURL_is_conflicting_between_testPackage_and_controlPackage_test() throws JsonProcessingException {
        packageBuilder.addPURL("control");
        controlPackage = packageBuilder.buildAndFlush();
        packageBuilder.addPURL("purl");
        conflictPackage = packageBuilder.buildAndFlush();

        List<Conflict> conflictList = controlPackage.compare(conflictPackage);
        boolean c1 = false;
        boolean c2 = false;

        assertEquals(2, conflictList.size());

        // Construct Text to use for diff report conflict messages
        Text text = new Text("Conflict", "PURL");

        for(Conflict c : conflictList)
        {
            if (c.GetType() == MismatchType.MISSING && Objects.equals(c.GetMessage(), "PURL is missing")) {
                if(Objects.equals(c.GetTarget(), "control") && Objects.equals(c.GetOther(), text.getNullItemInSetResponse()))
                    c1 = true;
                else if(Objects.equals(c.GetTarget(), text.getNullItemInSetResponse()) && Objects.equals(c.GetOther(), "purl"))
                    c2 = true;
            }
        }

        assertTrue(c1);
        assertTrue(c2);
    }

    @Test
    public void CPE_is_conflicting_between_testPackage_and_controlPackage_test() throws JsonProcessingException {
        packageBuilder.addCPE("control");
        controlPackage = packageBuilder.buildAndFlush();
        packageBuilder.addCPE("cpe");
        conflictPackage = packageBuilder.buildAndFlush();

        List<Conflict> conflictList = controlPackage.compare(conflictPackage);
        boolean c1 = false;
        boolean c2 = false;

        assertEquals(2, conflictList.size());

        // Construct Text to use for diff report conflict messages
        Text text = new Text("Conflict", "CPE");

        for(Conflict c : conflictList)
        {
            if (c.GetType() == MismatchType.MISSING && Objects.equals(c.GetMessage(), "CPE is missing")) {
                if(Objects.equals(c.GetTarget(), "control") && Objects.equals(c.GetOther(), text.getNullItemInSetResponse()))
                    c1 = true;
                else if(Objects.equals(c.GetTarget(), text.getNullItemInSetResponse()) && Objects.equals(c.GetOther(), "cpe"))
                    c2 = true;
            }
        }

        assertTrue(c1);
        assertTrue(c2);
    }

    @Test
    public void mimeType_is_conflicting_between_testPackage_and_controlPackage_test() throws JsonProcessingException {
        packageBuilder.setMimeType("control");
        controlPackage = packageBuilder.buildAndFlush();
        packageBuilder.setMimeType("mime type");
        conflictPackage = packageBuilder.buildAndFlush();

        List<Conflict> conflictList = controlPackage.compare(conflictPackage);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.MISC_MISMATCH, conflict.GetType());
        assertEquals("Mime Type doesn't match", conflict.GetMessage());
    }

    @Test
    public void publisher_is_conflicting_between_testPackage_and_controlPackage_test() throws JsonProcessingException {
        packageBuilder.setPublisher("control");
        controlPackage = packageBuilder.buildAndFlush();
        packageBuilder.setPublisher("publisher");
        conflictPackage = packageBuilder.buildAndFlush();

        List<Conflict> conflictList = controlPackage.compare(conflictPackage);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.PUBLISHER_MISMATCH, conflict.GetType());
        assertEquals("Publisher doesn't match", conflict.GetMessage());
    }

    @Test
    public void scope_is_conflicting_between_testPackage_and_controlPackage_test() throws JsonProcessingException {
        packageBuilder.setScope("control");
        controlPackage = packageBuilder.buildAndFlush();
        packageBuilder.setScope("scope");
        conflictPackage = packageBuilder.buildAndFlush();

        List<Conflict> conflictList = controlPackage.compare(conflictPackage);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.MISC_MISMATCH, conflict.GetType());
        assertEquals("Scope doesn't match", conflict.GetMessage());
    }

    @Test
    public void group_is_conflicting_between_testPackage_and_controlPackage_test() throws JsonProcessingException {
        packageBuilder.setGroup("control");
        controlPackage = packageBuilder.buildAndFlush();
        packageBuilder.setGroup("group");
        conflictPackage = packageBuilder.buildAndFlush();

        List<Conflict> conflictList = controlPackage.compare(conflictPackage);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.MISC_MISMATCH, conflict.GetType());
        assertEquals("Group doesn't match", conflict.GetMessage());
    }
    @Test
    public void verificationCode_is_conflicting_between_testPackage_and_controlPackage_test() throws JsonProcessingException {
        packageBuilder.setVerificationCode("control");
        controlPackage = packageBuilder.buildAndFlush();
        packageBuilder.setVerificationCode("verification code");
        conflictPackage = packageBuilder.buildAndFlush();

        List<Conflict> conflictList = controlPackage.compare(conflictPackage);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.MISC_MISMATCH, conflict.GetType());
        assertEquals("Verification Code doesn't match", conflict.GetMessage());
    }

    @Test
    public void downloadLocation_is_conflicting_between_testPackage_and_controlPackage_test() throws JsonProcessingException {
        packageBuilder.setDownloadLocation("control");
        controlPackage = packageBuilder.buildAndFlush();
        packageBuilder.setDownloadLocation("download location");
        conflictPackage = packageBuilder.buildAndFlush();

        List<Conflict> conflictList = controlPackage.compare(conflictPackage);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.MISC_MISMATCH, conflict.GetType());
        assertEquals("Download Location doesn't match", conflict.GetMessage());
    }

    @Test
    public void fileName_is_conflicting_between_testPackage_and_controlPackage_test() throws JsonProcessingException {
        packageBuilder.setFileName("control");
        controlPackage = packageBuilder.buildAndFlush();
        packageBuilder.setFileName("file name");
        conflictPackage = packageBuilder.buildAndFlush();

        List<Conflict> conflictList = controlPackage.compare(conflictPackage);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.MISC_MISMATCH, conflict.GetType());
        assertEquals("File Name doesn't match", conflict.GetMessage());
    }

    @Test
    public void filesAnalyzed_is_conflicting_between_testPackage_and_controlPackage_test() throws JsonProcessingException {
        packageBuilder.setFilesAnalyzed(false);
        controlPackage = packageBuilder.buildAndFlush();
        packageBuilder.setFilesAnalyzed(true);
        conflictPackage = packageBuilder.buildAndFlush();

        List<Conflict> conflictList = controlPackage.compare(conflictPackage);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.MISC_MISMATCH, conflict.GetType());
        assertEquals("Files Analyzed doesn't match", conflict.GetMessage());
    }

    @Test
    public void homePage_is_conflicting_between_testPackage_and_controlPackage_test() throws JsonProcessingException {
        packageBuilder.setHomePage("control");
        controlPackage = packageBuilder.buildAndFlush();
        packageBuilder.setHomePage("homepage");
        conflictPackage = packageBuilder.buildAndFlush();

        List<Conflict> conflictList = controlPackage.compare(conflictPackage);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.MISC_MISMATCH, conflict.GetType());
        assertEquals("Home Page doesn't match", conflict.GetMessage());
    }

    @Test
    public void sourceInfo_is_conflicting_between_testPackage_and_controlPackage_test() throws JsonProcessingException {
        packageBuilder.setSourceInfo("control");
        controlPackage = packageBuilder.buildAndFlush();
        packageBuilder.setSourceInfo("source info");
        conflictPackage = packageBuilder.buildAndFlush();

        List<Conflict> conflictList = controlPackage.compare(conflictPackage);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.MISC_MISMATCH, conflict.GetType());
        assertEquals("Source Info doesn't match", conflict.GetMessage());
    }

    @Test
    public void releaseDate_is_conflicting_between_testPackage_and_controlPackage_test() throws JsonProcessingException {
        packageBuilder.setReleaseDate("control");
        controlPackage = packageBuilder.buildAndFlush();
        packageBuilder.setReleaseDate("release date");
        conflictPackage = packageBuilder.buildAndFlush();

        List<Conflict> conflictList = controlPackage.compare(conflictPackage);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.TIMESTAMP_MISMATCH, conflict.GetType());
        assertEquals("Release Date doesn't match", conflict.GetMessage());
    }

    @Test
    public void buildDate_is_conflicting_between_testPackage_and_controlPackage_test() throws JsonProcessingException {
        packageBuilder.setBuildDate("control");
        controlPackage = packageBuilder.buildAndFlush();
        packageBuilder.setBuildDate("build date");
        conflictPackage = packageBuilder.buildAndFlush();

        List<Conflict> conflictList = controlPackage.compare(conflictPackage);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.TIMESTAMP_MISMATCH, conflict.GetType());
        assertEquals("Built Date doesn't match", conflict.GetMessage());
    }

    @Test
    public void validUntilDate_is_conflicting_between_testPackage_and_controlPackage_test() throws JsonProcessingException {
        packageBuilder.setValidUntilDate("control");
        controlPackage = packageBuilder.buildAndFlush();
        packageBuilder.setValidUntilDate("valid until date");
        conflictPackage = packageBuilder.buildAndFlush();

        List<Conflict> conflictList = controlPackage.compare(conflictPackage);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.TIMESTAMP_MISMATCH, conflict.GetType());
        assertEquals("Valid Until Date doesn't match", conflict.GetMessage());
    }

    @Test
    public void fileNotice_is_conflicting_between_testPackage_and_controlPackage_test() throws JsonProcessingException {
        packageBuilder.setFileNotice("control");
        controlPackage = packageBuilder.buildAndFlush();
        packageBuilder.setFileNotice("notice");
        conflictPackage = packageBuilder.buildAndFlush();

        List<Conflict> conflictList = controlPackage.compare(conflictPackage);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.MISC_MISMATCH, conflict.GetType());
        assertEquals("File Notice doesn't match", conflict.GetMessage());
    }
}
