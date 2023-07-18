package org.svip.builders.component;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.svip.sbom.factory.objects.SPDX23.SPDX23PackageBuilderFactory;
import org.svip.sbom.builder.objects.schemas.SPDX23.SPDX23PackageBuilder;
import org.svip.sbom.model.objects.SPDX23.SPDX23PackageObject;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.shared.util.Description;
import org.svip.sbom.model.shared.util.ExternalReference;
import org.svip.sbom.model.shared.util.LicenseCollection;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * file: SPDX23PackageBuilderTest.java
 * File to test SPDX23PackageBuilder
 *
 * @author Matthew Morrison
 * @author Kevin Laporte
 */
public class SPDX23PackageBuilderTest {
    SPDX23PackageBuilder test_packageBuilder;
    SPDX23PackageObject test_file;
    String test_type = "library";

    String test_uid = "749d4a17-1074-4b78-a968-fafc67378f75";

    String test_author = "Guido van Rossum";

    String test_name = "Python";

    String test_license1 = "PSFL";

    String test_license2 = "GPL";

    String test_copyright = "Test Copyright";

    String test_hash_algo = "SHA1";

    String test_hash_value = "da39a3ee5e6b4b0d3255bfef95601890afd80709";

    String test_comment = "This is a test comment";

    String test_attributionText = "All advertising materials mentioning features or use of this software" +
            "must display this test acknowledgement";

    String test_downloadLocation = "https://www.python.org";

    String test_fileName = "Python3.9";

    boolean test_filesAnalyzed = true;

    String test_verificationCode = "d6a770ba38583ed4bb4525bd96e50461655d2758";

    String test_homePage = "www.python.org";

    String test_sourceInfo = "This is a test for Python 3.9";

    String test_releaseDate = "1/1/97";

    String test_builtDate = "6/12/23";

    String test_validUntilDate = "6/12/25";

    String test_supplier = "Python Foundation";

    String test_version = "1.23.1";

    String test_description = "This is a test description";

    String test_random_cpe = "cpe:2.3:a:random_test_cpe:random:3.11.2:*:*:*:*:*:*:*";

    String test_random_purl = "pkg:random/test@2.0.0";

    String test_extRef_url = "https://www.python.org";

    String test_extTef_type = "website";
    @BeforeEach
    void create_test_packageBuilder(){
        SPDX23PackageBuilderFactory test_SPDX23PackageBuilderFactory = new SPDX23PackageBuilderFactory();
        test_packageBuilder = test_SPDX23PackageBuilderFactory.createBuilder();
    }

    @Test
    void setType_is_library_value_test() {
        test_packageBuilder.setType(test_type);
        test_file = test_packageBuilder.buildAndFlush();
        assertEquals(test_type, test_file.getType());
    }

    @Test
    void setUID_is_test_UID_value_test() {
        test_packageBuilder.setUID(test_uid);
        test_file = test_packageBuilder.buildAndFlush();
        assertEquals(test_uid, test_file.getUID());
    }

    @Test
    void setAuthor_is_test_author_value_test() {
        test_packageBuilder.setAuthor(test_author);
        test_file = test_packageBuilder.buildAndFlush();
        assertEquals(test_author, test_file.getAuthor());
    }

    @Test
    void setName_is_test_name_value_test() {
        test_packageBuilder.setName(test_name);
        test_file = test_packageBuilder.buildAndFlush();
        assertEquals(test_name, test_file.getName());
    }

    @Test
    void setLicenses_has_test_licenses_test() {
        LicenseCollection licenseCollection = new LicenseCollection();
        licenseCollection.addDeclaredLicense(test_license1);
        licenseCollection.addDeclaredLicense(test_license2);
        test_packageBuilder.setLicenses(licenseCollection);

        test_file = test_packageBuilder.buildAndFlush();

        assertEquals(licenseCollection, test_file.getLicenses());
    }

    @Test
    void setCopyright_is_test_copyright_value_test() {
        test_packageBuilder.setCopyright(test_copyright);
        test_file = test_packageBuilder.buildAndFlush();
        assertEquals(test_copyright, test_file.getCopyright());
    }

    @Test
    void addHash_contains_test_hash_info_in_hashmap_test() {
        test_packageBuilder.addHash(test_hash_algo, test_hash_value);

        test_file = test_packageBuilder.buildAndFlush();

        HashMap<String, String> test_map = new HashMap<>();
        test_map.put(test_hash_algo, test_hash_value);

        assertEquals(test_map, test_file.getHashes());
    }

    @Test
    void setComment_is_test_comment_value_test() {
        test_packageBuilder.setComment(test_comment);

        test_file = test_packageBuilder.buildAndFlush();

        assertEquals(test_comment, test_file.getComment());
    }

    @Test
    void setAttributionText_is_test_attribution_text_value_test() {
        test_packageBuilder.setAttributionText(test_attributionText);

        test_file = test_packageBuilder.buildAndFlush();

        assertEquals(test_attributionText, test_file.getAttributionText());
    }

    @Test
    void setDownloadLocation_is_test_download_location_value_test() {
        test_packageBuilder.setDownloadLocation(test_downloadLocation);
        test_file = test_packageBuilder.buildAndFlush();
        assertEquals(test_downloadLocation, test_file.getDownloadLocation());
    }

    @Test
    void setFileName_is_file_name_value_test() {
        test_packageBuilder.setFileName(test_fileName);
        test_file = test_packageBuilder.buildAndFlush();
        assertEquals(test_fileName, test_file.getFileName());
    }

    @Test
    void setFilesAnalyzed_is_true_test() {
        test_packageBuilder.setFilesAnalyzed(test_filesAnalyzed);
        test_file = test_packageBuilder.buildAndFlush();
        assertEquals(test_filesAnalyzed, test_file.getFilesAnalyzed());
    }

    @Test
    void setVerificationCode_is_test_verification_code_value_test() {
        test_packageBuilder.setVerificationCode(test_verificationCode);
        test_file = test_packageBuilder.buildAndFlush();
        assertEquals(test_verificationCode, test_file.getVerificationCode());
    }

    @Test
    void setHomePage_is_test_home_page_value_test() {
        test_packageBuilder.setHomePage(test_homePage);
        test_file = test_packageBuilder.buildAndFlush();
        assertEquals(test_homePage, test_file.getHomePage());
    }

    @Test
    void setSourceInfo_is_test_source_info_value_test() {
        test_packageBuilder.setSourceInfo(test_sourceInfo);
        test_file = test_packageBuilder.buildAndFlush();
        assertEquals(test_sourceInfo, test_file.getSourceInfo());
    }

    @Test
    void setReleaseDate_is_test_release_date_value_test() {
        test_packageBuilder.setReleaseDate(test_releaseDate);
        test_file = test_packageBuilder.buildAndFlush();
        assertEquals(test_releaseDate, test_file.getReleaseDate());
    }

    @Test
    void setBuildDate_is_test_built_date_value_test() {
        test_packageBuilder.setBuildDate(test_builtDate);
        test_file = test_packageBuilder.buildAndFlush();
        assertEquals(test_builtDate, test_file.getBuiltDate());
    }

    @Test
    void setValidUntilDate_is_test_valid_until_date_value_test() {
        test_packageBuilder.setValidUntilDate(test_validUntilDate);
        test_file = test_packageBuilder.buildAndFlush();
        assertEquals(test_validUntilDate, test_file.getValidUntilDate());
    }

    @Test
    void setSupplier_is_test_supplier_value_test() {
        Organization supplier = new Organization(test_supplier, "www.python.com");
        test_packageBuilder.setSupplier(supplier);

        test_file = test_packageBuilder.buildAndFlush();
        assertEquals(supplier, test_file.getSupplier());
    }

    @Test
    void setVersion_is_test_version_value_test() {
        test_packageBuilder.setVersion(test_version);

        test_file = test_packageBuilder.buildAndFlush();
        assertEquals(test_version, test_file.getVersion());
    }

    @Test
    void setDescription_is_test_description_test() {
        Description description = new Description(test_description);
        test_packageBuilder.setDescription(description);

        test_file = test_packageBuilder.buildAndFlush();
        assertEquals(description, test_file.getDescription());
    }

    @Test
    void addCPE_has_test_random_cpe_value_test() {
        test_packageBuilder.addCPE(test_random_cpe);

        test_file = test_packageBuilder.buildAndFlush();
        assertTrue(test_file.getCPEs().contains(test_random_cpe));
    }

    @Test
    void addPURL_has_test_random_purl_value_test() {
        test_packageBuilder.addPURL(test_random_purl);

        test_file = test_packageBuilder.buildAndFlush();
        assertTrue(test_file.getPURLs().contains(test_random_purl));
    }

    @Test
    void addExternalReference_has_test_external_reference_test() {
        ExternalReference externalReference = new ExternalReference(test_extRef_url, test_extTef_type);
        test_packageBuilder.addExternalReference(externalReference);

        test_file = test_packageBuilder.buildAndFlush();
        assertTrue(test_file.getExternalReferences().contains(externalReference));
    }
}
