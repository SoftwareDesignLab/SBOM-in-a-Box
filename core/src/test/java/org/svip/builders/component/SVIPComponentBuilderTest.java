package org.svip.builders.component;

import org.junit.jupiter.api.Test;
import org.svip.sbom.model.objects.SVIPComponentObject;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.shared.util.Description;
import org.svip.sbom.model.shared.util.ExternalReference;
import org.svip.sbom.model.shared.util.LicenseCollection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * file: SVIPComponentBuilderTest
 * File to test SVIPComponentBuilder
 *
 * @author Matthew Morrison
 * @author Kevin Laporte
 */
public class SVIPComponentBuilderTest {

    SVIPComponentBuilder test_packageBuilder = new SVIPComponentBuilder();
    SVIPComponentObject test_file;
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

    String fileNotice = "This file is licensed under GPL";

    String test_mimeType = "image/jpeg";

    String test_publisher = "The Python Foundation";

    String test_scope = "required";

    String test_group = "org.python";

    String test_property_name = "local";

    String test_property_value = "team_responsible";

    @Test
    void getMimeType_is_test_mimeType_when_setMimeType_is_used_test() {
        test_packageBuilder.setMimeType(test_mimeType);
        test_file = test_packageBuilder.buildAndFlush();
        assertEquals(test_mimeType, test_file.getMimeType());
    }

    @Test
    void getPublisher_is_test_publisher_when_setPublisher_is_used_test() {
        test_packageBuilder.setPublisher(test_publisher);
        test_file = test_packageBuilder.buildAndFlush();
        assertEquals(test_publisher, test_file.getPublisher());
    }

    @Test
    void getScope_is_test_scope_when_setScope_is_used_test() {
        test_packageBuilder.setScope(test_scope);
        test_file = test_packageBuilder.buildAndFlush();
        assertEquals(test_scope, test_file.getScope());
    }

    @Test
    void getGroup_is_test_group_when_setGroup_is_used_test() {
        test_packageBuilder.setGroup(test_group);
        test_file = test_packageBuilder.buildAndFlush();
        assertEquals(test_group, test_file.getGroup());
    }

    @Test
    void getExternalReferences_contains_test_extRef_when_addExternalReferences_is_used_test() {
        ExternalReference test_extRef = new ExternalReference(
                test_extRef_url, test_extTef_type);
        test_packageBuilder.addExternalReferences(test_extRef);

        test_file = test_packageBuilder.buildAndFlush();
        assertTrue(test_file.getExternalReferences().contains(test_extRef));
    }

    @Test
    void getProperties_is_test_propertyMap_when_addProperty_is_used_test() {
        test_packageBuilder.addProperty(test_property_name, test_property_value);

        test_file = test_packageBuilder.buildAndFlush();

        HashMap<String, Set<String>> test_propertyMap = new HashMap<String, Set<String>>();
        test_propertyMap.put(test_property_name, new HashSet<String>());
        test_propertyMap.get(test_property_name).add(test_property_value);

        assertEquals(test_propertyMap, test_file.getProperties());
    }

    @Test
    void getType_is_test_type_when_setType_is_used_test() {
        test_packageBuilder.setType(test_type);

        test_file = test_packageBuilder.buildAndFlush();
        assertEquals(test_type, test_file.getType());
    }

    @Test
    void getUID_is_test_uid_when_setUID_is_used_test() {
        test_packageBuilder.setUID(test_uid);

        test_file = test_packageBuilder.buildAndFlush();
        assertEquals(test_uid, test_file.getUID());
    }

    @Test
    void getAuthor_is_test_author_when_setAuthor_is_used_test() {
        test_packageBuilder.setAuthor(test_author);

        test_file = test_packageBuilder.buildAndFlush();
        assertEquals(test_author, test_file.getAuthor());

    }

    @Test
    void getName_is_test_name_when_setName_is_used_test() {
        test_packageBuilder.setName(test_name);

        test_file = test_packageBuilder.buildAndFlush();
        assertEquals(test_name, test_file.getName());
    }

    @Test
    void getLicenses_is_licenseCollection_when_setLicenses_is_used_test() {
        LicenseCollection licenseCollection = new LicenseCollection();
        licenseCollection.addDeclaredLicense(test_license1);
        licenseCollection.addDeclaredLicense(test_license2);
        test_packageBuilder.setLicenses(licenseCollection);

        test_file = test_packageBuilder.buildAndFlush();
        assertEquals(licenseCollection, test_file.getLicenses());
    }

    @Test
    void getCopyright_is_test_copyright_when_setCopyright_is_used_test() {
        test_packageBuilder.setCopyright(test_copyright);

        test_file = test_packageBuilder.buildAndFlush();
        assertEquals(test_copyright, test_file.getCopyright());
    }

    @Test
    void getHashes_is_test_map_when_addHash_is_used_test() {
        test_packageBuilder.addHash(test_hash_algo, test_hash_value);

        test_file = test_packageBuilder.buildAndFlush();

        HashMap<String, String> test_map = new HashMap<String, String>();
        test_map.put(test_hash_algo, test_hash_value);

        assertEquals(test_map, test_file.getHashes());
    }

    @Test
    void getSupplier_is_test_supplier_when_setSupplier_is_used_test() {
        Organization supplier = new Organization(test_supplier, "www.python.com");
        test_packageBuilder.setSupplier(supplier);

        test_file = test_packageBuilder.buildAndFlush();
        assertEquals(supplier, test_file.getSupplier());
    }

    @Test
    void getVersion_is_test_version_when_setVersion_is_used_test() {
        test_packageBuilder.setVersion(test_version);

        test_file = test_packageBuilder.buildAndFlush();
        assertEquals(test_version, test_file.getVersion());
    }

    @Test
    void getDescription_is_test_description_when_setDescription_is_used_test() {
        Description description = new Description(test_description);
        test_packageBuilder.setDescription(description);

        test_file = test_packageBuilder.buildAndFlush();
        assertEquals(description, test_file.getDescription());
    }

    @Test
    void getCPEs_contains_test_random_cpe_when_addCPE_is_used_test() {
        test_packageBuilder.addCPE(test_random_cpe);

        test_file = test_packageBuilder.buildAndFlush();
        assertTrue(test_file.getCPEs().contains(test_random_cpe));
    }

    @Test
    void getPURLs_contains_test_random_purl_when_addPURL_is_used_test() {
        test_packageBuilder.addPURL(test_random_purl);

        test_file = test_packageBuilder.buildAndFlush();
        assertTrue(test_file.getPURLs().contains(test_random_purl));
    }

    @Test
    void getExternalReferences_contains_externalReference_when_addExternalReference_is_used_test() {
        ExternalReference externalReference = new ExternalReference(test_extRef_url, test_extTef_type);
        test_packageBuilder.addExternalReference(externalReference);

        test_file = test_packageBuilder.buildAndFlush();
        assertTrue(test_file.getExternalReferences().contains(externalReference));
    }

    @Test
    void getFileNotice_is_fileNotice_when_setFileNotice_is_used_test() {
        test_packageBuilder.setFileNotice(fileNotice);

        test_file = test_packageBuilder.buildAndFlush();

        assertEquals(fileNotice, test_file.getFileNotice());
    }

    @Test
    void getComment_is_test_comment_when_setComment_is_used_test() {
        test_packageBuilder.setComment(test_comment);

        test_file = test_packageBuilder.buildAndFlush();

        assertEquals(test_comment, test_file.getComment());
    }

    @Test
    void getAttributionText_is_test_attributionText_when_setAttributionText_is_used_test() {
        test_packageBuilder.setAttributionText(test_attributionText);

        test_file = test_packageBuilder.buildAndFlush();

        assertEquals(test_attributionText, test_file.getAttributionText());
    }

    @Test
    void getDownloadLocation_is_test_downloadLocation_when_setDownloadLocation_is_used_test() {
        test_packageBuilder.setDownloadLocation(test_downloadLocation);
        test_file = test_packageBuilder.buildAndFlush();
        assertEquals(test_downloadLocation, test_file.getDownloadLocation());
    }

    @Test
    void getFileName_is_test_fileName_when_setFileName_is_used_test() {
        test_packageBuilder.setFileName(test_fileName);
        test_file = test_packageBuilder.buildAndFlush();
        assertEquals(test_fileName, test_file.getFileName());
    }

    @Test
    void getFilesAnalyzed_is_test_filesAnalyzed_when_setFilesAnalyzed_is_used_test() {
        test_packageBuilder.setFilesAnalyzed(test_filesAnalyzed);
        test_file = test_packageBuilder.buildAndFlush();
        assertEquals(test_filesAnalyzed, test_file.getFilesAnalyzed());
    }

    @Test
    void getVerificationCode_is_test_verificationCode_when_setVerificationCode_is_used_test() {
        test_packageBuilder.setVerificationCode(test_verificationCode);
        test_file = test_packageBuilder.buildAndFlush();
        assertEquals(test_verificationCode, test_file.getVerificationCode());
    }

    @Test
    void getHomePage_is_test_homePage_when_setHomePage_is_used_test() {
        test_packageBuilder.setHomePage(test_homePage);
        test_file = test_packageBuilder.buildAndFlush();
        assertEquals(test_homePage, test_file.getHomePage());
    }

    @Test
    void getSourceInfo_is_test_sourceInfo_when_setSourceInfo_is_used_test() {
        test_packageBuilder.setSourceInfo(test_sourceInfo);
        test_file = test_packageBuilder.buildAndFlush();
        assertEquals(test_sourceInfo, test_file.getSourceInfo());
    }

    @Test
    void getReleaseDate_is_test_releaseDate_when_setReleaseDate_is_used_test() {
        test_packageBuilder.setReleaseDate(test_releaseDate);
        test_file = test_packageBuilder.buildAndFlush();
        assertEquals(test_releaseDate, test_file.getReleaseDate());
    }

    @Test
    void getBuiltDate_is_test_builtDate_when_setBuildDate_is_used_test() {
        test_packageBuilder.setBuildDate(test_builtDate);
        test_file = test_packageBuilder.buildAndFlush();
        assertEquals(test_builtDate, test_file.getBuiltDate());
    }

    @Test
    void getValidUntilDate_is_test_validUntilDate_when_setValidUntilDate_is_used_test() {
        test_packageBuilder.setValidUntilDate(test_validUntilDate);
        test_file = test_packageBuilder.buildAndFlush();
        assertEquals(test_validUntilDate, test_file.getValidUntilDate());
    }
}
