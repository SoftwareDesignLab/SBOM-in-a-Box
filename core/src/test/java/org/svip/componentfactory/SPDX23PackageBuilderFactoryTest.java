package org.svip.componentfactory;

import org.junit.jupiter.api.Test;
import org.svip.builders.component.SPDX23PackageBuilder;
import org.svip.sbom.model.objects.SPDX23.SPDX23PackageObject;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.shared.util.Description;
import org.svip.sbom.model.shared.util.ExternalReference;
import org.svip.sbom.model.shared.util.LicenseCollection;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * file: SPDX23PackageBuilderFactoryTest.java
 * File to test SPDX23PackageBuilderFactory
 *
 * @author Matthew Morrison
 */
public class SPDX23PackageBuilderFactoryTest {
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

    @Test
    void create_SPDX_package_builder_test() {
        SPDX23PackageBuilderFactory spdx23PackageBuilderFactory = new SPDX23PackageBuilderFactory();
        test_packageBuilder = spdx23PackageBuilderFactory.createBuilder();

        LicenseCollection licenseCollection = new LicenseCollection();
        licenseCollection.addDeclaredLicense(test_license1);
        licenseCollection.addDeclaredLicense(test_license2);

        HashMap<String, String> test_map = new HashMap<>();
        test_map.put(test_hash_algo, test_hash_value);

        Organization supplier = new Organization(test_supplier, "www.python.com");

        Description description = new Description(test_description);

        ExternalReference externalReference = new ExternalReference(test_extRef_url, test_extTef_type);

        test_packageBuilder.setType(test_type);
        test_packageBuilder.setUID(test_uid);
        test_packageBuilder.setAuthor(test_author);
        test_packageBuilder.setName(test_name);
        test_packageBuilder.setLicenses(licenseCollection);
        test_packageBuilder.setCopyright(test_copyright);
        test_packageBuilder.addHash(test_hash_algo, test_hash_value);
        test_packageBuilder.setComment(test_comment);
        test_packageBuilder.setAttributionText(test_attributionText);
        test_packageBuilder.setDownloadLocation(test_downloadLocation);
        test_packageBuilder.setFileName(test_fileName);
        test_packageBuilder.setFilesAnalyzed(test_filesAnalyzed);
        test_packageBuilder.setVerificationCode(test_verificationCode);
        test_packageBuilder.setHomePage(test_homePage);
        test_packageBuilder.setSourceInfo(test_sourceInfo);
        test_packageBuilder.setReleaseDate(test_releaseDate);
        test_packageBuilder.setBuildDate(test_builtDate);
        test_packageBuilder.setValidUntilDate(test_validUntilDate);
        test_packageBuilder.setSupplier(supplier);
        test_packageBuilder.setVersion(test_version);
        test_packageBuilder.setDescription(description);
        test_packageBuilder.addCPE(test_random_cpe);
        test_packageBuilder.addPURL(test_random_purl);
        test_packageBuilder.addExternalReference(externalReference);


        test_file = test_packageBuilder.buildAndFlush();

        assertEquals(test_type, test_file.getType());
        assertEquals(test_uid, test_file.getUID());
        assertEquals(test_author, test_file.getAuthor());
        assertEquals(test_name, test_file.getName());
        assertEquals(licenseCollection, test_file.getLicenses());
        assertEquals(test_copyright, test_file.getCopyright());
        assertEquals(test_map, test_file.getHashes());
        assertEquals(test_comment, test_file.getComment());
        assertEquals(test_attributionText, test_file.getAttributionText());
        assertEquals(test_downloadLocation, test_file.getDownloadLocation());
        assertEquals(test_fileName, test_file.getFileName());
        assertEquals(test_filesAnalyzed, test_file.getFilesAnalyzed());
        assertEquals(test_verificationCode, test_file.getVerificationCode());
        assertEquals(test_homePage, test_file.getHomePage());
        assertEquals(test_sourceInfo, test_file.getSourceInfo());
        assertEquals(test_releaseDate, test_file.getReleaseDate());
        assertEquals(test_builtDate, test_file.getBuiltDate());
        assertEquals(test_validUntilDate, test_file.getValidUntilDate());
        assertEquals(supplier, test_file.getSupplier());
        assertEquals(test_version, test_file.getVersion());
        assertEquals(description, test_file.getDescription());
        assertTrue(test_file.getCPEs().contains(test_random_cpe));
        assertTrue(test_file.getPURLs().contains(test_random_purl));
        assertTrue(test_file.getExternalReferences().contains(externalReference));
    }
}
