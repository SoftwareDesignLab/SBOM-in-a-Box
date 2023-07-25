package org.svip.sbom.model.objects.SPDX23;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.svip.sbom.builder.objects.schemas.SPDX23.SPDX23PackageBuilder;
import org.svip.sbom.factory.objects.SPDX23.SPDX23PackageBuilderFactory;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23Package;
import org.svip.sbom.model.shared.util.LicenseCollection;
import org.svip.sbomanalysis.comparison.conflicts.Conflict;
import org.svip.sbomanalysis.comparison.conflicts.MismatchType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SPDX23PackageObjectConflictsTest {
    static SPDX23PackageBuilderFactory packageBuilderFactory = new SPDX23PackageBuilderFactory();
    static SPDX23PackageBuilder packageBuilder = packageBuilderFactory.createBuilder();
    static SPDX23Package controlPackage = packageBuilder.buildAndFlush();
    static Component conflictPackage;

    @Test
    public void Type_is_Conflicting_between_testPackage_and_controlPackage_test()
    {
        packageBuilder.setType("Type");
        conflictPackage = packageBuilder.buildAndFlush();

        List<Conflict> conflictList = controlPackage.compare(conflictPackage);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.MISSING, conflict.GetType());
        assertEquals("Type is missing", conflict.GetMessage());
    }

    @Test
    public void UID_is_conflicting_between_testPackage_and_controlPackage_test(){
        packageBuilder.setUID("123");
        conflictPackage = packageBuilder.buildAndFlush();

        List<Conflict> conflictList = controlPackage.compare(conflictPackage);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.MISSING, conflict.GetType());
        assertEquals("UID is missing", conflict.GetMessage());
    }

    @Test
    public void name_is_conflicting_between_testPackage_and_controlPackage_test(){
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
    public void author_is_conflicting_between_testPackage_and_controlPackage_test(){
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

    @Test
    public void license_is_conflicting_between_testPackage_and_controlPackage_test(){
        LicenseCollection licenseCollection = new LicenseCollection();
        licenseCollection.addDeclaredLicense("control license");
        packageBuilder.setLicenses(licenseCollection);
        controlPackage = packageBuilder.buildAndFlush();
        LicenseCollection licenseCollectionTwo = new LicenseCollection();
        licenseCollectionTwo.addDeclaredLicense("license");
        packageBuilder.setLicenses(licenseCollectionTwo);
        conflictPackage = packageBuilder.buildAndFlush();

        List<Conflict> conflictList = controlPackage.compare(conflictPackage);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.LICENSE_MISMATCH, conflict.GetType());
        assertEquals("Licenses doesn't match", conflict.GetMessage());
    }

    @Test
    public void copyright_is_conflicting_between_testPackage_and_controlPackage_test(){
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

    @Test
    public void componentHash_is_conflicting_between_testPackage_and_controlPackage_test(){

    }

//    @Test
//    public void supplier_is_conflicting_between_testPackage_and_controlPackage_test(){
//        packageBuilder.setSupplier(null); //TODO
//        controlPackage = packageBuilder.buildAndFlush();
//        packageBuilder.setSupplier(null);
//        conflictPackage = packageBuilder.buildAndFlush();
//
//        List<Conflict> conflictList = controlPackage.compare(conflictPackage);
//        Conflict conflict = conflictList.get(0);
//
//        assertEquals(1, conflictList.size());
//        assertEquals(MismatchType.SUPPLIER_MISMATCH, conflict.GetType());
//        assertEquals("Supplier doesn't match", conflict.GetMessage());
//    }

    @Test
    public void version_is_conflicting_between_testPackage_and_controlPackage_test(){
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

//    @Test
//    public void description_is_conflicting_between_testPackage_and_controlPackage_test(){
//        packageBuilder.setDescription(null); //TODO
//        controlPackage = packageBuilder.buildAndFlush();
//        packageBuilder.setDescription(null);
//        conflictPackage = packageBuilder.buildAndFlush();
//
//        List<Conflict> conflictList = controlPackage.compare(conflictPackage);
//        Conflict conflict = conflictList.get(0);
//
//        assertEquals(1, conflictList.size());
//        assertEquals(MismatchType.MISC_MISMATCH, conflict.GetType());
//        assertEquals("Description doesn't match", conflict.GetMessage());
//    }

    @Test
    public void PURL_is_conflicting_between_testPackage_and_controlPackage_test(){
        packageBuilder.addPURL("control");
        controlPackage = packageBuilder.buildAndFlush();
        packageBuilder.addPURL("purl");
        conflictPackage = packageBuilder.buildAndFlush();

        List<Conflict> conflictList = controlPackage.compare(conflictPackage);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.PURL_MISMATCH, conflict.GetType());
        assertEquals("PURL doesn't match", conflict.GetMessage());
    }

    @Test
    public void CPE_is_conflicting_between_testPackage_and_controlPackage_test(){
        packageBuilder.addCPE("control");
        controlPackage = packageBuilder.buildAndFlush();
        packageBuilder.addCPE("cpe");
        conflictPackage = packageBuilder.buildAndFlush();

        List<Conflict> conflictList = controlPackage.compare(conflictPackage);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.CPE_MISMATCH, conflict.GetType());
        assertEquals("CPE doesn't match", conflict.GetMessage());
    }

    @Test
    public void verificationCode_is_conflicting_between_testPackage_and_controlPackage_test(){
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
    public void downloadLocation_is_conflicting_between_testPackage_and_controlPackage_test(){
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
    public void fileName_is_conflicting_between_testPackage_and_controlPackage_test(){
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
    public void filesAnalyzed_is_conflicting_between_testPackage_and_controlPackage_test(){
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
    public void homePage_is_conflicting_between_testPackage_and_controlPackage_test(){
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
    public void sourceInfo_is_conflicting_between_testPackage_and_controlPackage_test(){
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
    public void releaseDate_is_conflicting_between_testPackage_and_controlPackage_test(){
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
    public void buildDate_is_conflicting_between_testPackage_and_controlPackage_test(){
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
    public void validUntilDate_is_conflicting_between_testPackage_and_controlPackage_test(){
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
}
