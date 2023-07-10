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
    void setMimeType() {
        test_packageBuilder.setMimeType(test_mimeType);
        test_file = test_packageBuilder.buildAndFlush();
        assertEquals(test_mimeType, test_file.getMimeType());
    }

    @Test
    void setPublisher() {
        test_packageBuilder.setPublisher(test_publisher);
        test_file = test_packageBuilder.buildAndFlush();
        assertEquals(test_publisher, test_file.getPublisher());
    }

    @Test
    void setScope() {
        test_packageBuilder.setScope(test_scope);
        test_file = test_packageBuilder.buildAndFlush();
        assertEquals(test_scope, test_file.getScope());
    }

    @Test
    void setGroup() {
        test_packageBuilder.setGroup(test_group);
        test_file = test_packageBuilder.buildAndFlush();
        assertEquals(test_group, test_file.getGroup());
    }

    @Test
    void addExternalReferences() {
        ExternalReference test_extRef = new ExternalReference(
                test_extRef_url, test_extTef_type);
        test_packageBuilder.addExternalReferences(test_extRef);

        test_file = test_packageBuilder.buildAndFlush();
        assertTrue(test_file.getExternalReferences().contains(test_extRef));
    }

    @Test
    void addProperty() {
        test_packageBuilder.addProperty(test_property_name, test_property_value);

        test_file = test_packageBuilder.buildAndFlush();

        HashMap<String, Set<String>> test_propertyMap = new HashMap<String, Set<String>>();
        test_propertyMap.put(test_property_name, new HashSet<String>());
        test_propertyMap.get(test_property_name).add(test_property_value);

        assertEquals(test_propertyMap, test_file.getProperties());
    }

    @Test
    void setType() {
        test_packageBuilder.setType(test_type);

        test_file = test_packageBuilder.buildAndFlush();
        assertEquals(test_type, test_file.getType());
    }

    @Test
    void setUID() {
        test_packageBuilder.setUID(test_uid);

        test_file = test_packageBuilder.buildAndFlush();
        assertEquals(test_uid, test_file.getUID());
    }

    @Test
    void setAuthor() {
        test_packageBuilder.setAuthor(test_author);

        test_file = test_packageBuilder.buildAndFlush();
        assertEquals(test_author, test_file.getAuthor());

    }

    @Test
    void setName() {
        test_packageBuilder.setName(test_name);

        test_file = test_packageBuilder.buildAndFlush();
        assertEquals(test_name, test_file.getName());
    }

    @Test
    void setLicenses() {
        LicenseCollection licenseCollection = new LicenseCollection();
        licenseCollection.addDeclaredLicense(test_license1);
        licenseCollection.addDeclaredLicense(test_license2);
        test_packageBuilder.setLicenses(licenseCollection);

        test_file = test_packageBuilder.buildAndFlush();
        assertEquals(licenseCollection, test_file.getLicenses());
    }

    @Test
    void setCopyright() {
        test_packageBuilder.setCopyright(test_copyright);

        test_file = test_packageBuilder.buildAndFlush();
        assertEquals(test_copyright, test_file.getCopyright());
    }

    @Test
    void addHash() {
        test_packageBuilder.addHash(test_hash_algo, test_hash_value);

        test_file = test_packageBuilder.buildAndFlush();

        HashMap<String, String> test_map = new HashMap<String, String>();
        test_map.put(test_hash_algo, test_hash_value);

        assertEquals(test_map, test_file.getHashes());
    }

    @Test
    void setSupplier() {
        Organization supplier = new Organization(test_supplier, "www.python.com");
        test_packageBuilder.setSupplier(supplier);

        test_file = test_packageBuilder.buildAndFlush();
        assertEquals(supplier, test_file.getSupplier());
    }

    @Test
    void setVersion() {
        test_packageBuilder.setVersion(test_version);

        test_file = test_packageBuilder.buildAndFlush();
        assertEquals(test_version, test_file.getVersion());
    }

    @Test
    void setDescription() {
        Description description = new Description(test_description);
        test_packageBuilder.setDescription(description);

        test_file = test_packageBuilder.buildAndFlush();
        assertEquals(description, test_file.getDescription());
    }

    @Test
    void addCPE() {
        test_packageBuilder.addCPE(test_random_cpe);

        test_file = test_packageBuilder.buildAndFlush();
        assertTrue(test_file.getCPEs().contains(test_random_cpe));
    }

    @Test
    void addPURL() {
        test_packageBuilder.addPURL(test_random_purl);

        test_file = test_packageBuilder.buildAndFlush();
        assertTrue(test_file.getPURLs().contains(test_random_purl));
    }

    @Test
    void addExternalReference() {
        ExternalReference externalReference = new ExternalReference(test_extRef_url, test_extTef_type);
        test_packageBuilder.addExternalReference(externalReference);

        test_file = test_packageBuilder.buildAndFlush();
        assertTrue(test_file.getExternalReferences().contains(externalReference));
    }

    @Test
    void setFileNotice() {
        test_packageBuilder.setFileNotice(fileNotice);

        test_file = test_packageBuilder.buildAndFlush();

        assertEquals(fileNotice, test_file.getFileNotice());
    }

    @Test
    void setComment() {
        test_packageBuilder.setComment(test_comment);

        test_file = test_packageBuilder.buildAndFlush();

        assertEquals(test_comment, test_file.getComment());
    }

    @Test
    void setAttributionText() {
        test_packageBuilder.setAttributionText(test_attributionText);

        test_file = test_packageBuilder.buildAndFlush();

        assertEquals(test_attributionText, test_file.getAttributionText());
    }

    @Test
    void setDownloadLocation() {
        test_packageBuilder.setDownloadLocation(test_downloadLocation);
        test_file = test_packageBuilder.buildAndFlush();
        assertEquals(test_downloadLocation, test_file.getDownloadLocation());
    }

    @Test
    void setFileName() {
        test_packageBuilder.setFileName(test_fileName);
        test_file = test_packageBuilder.buildAndFlush();
        assertEquals(test_fileName, test_file.getFileName());
    }

    @Test
    void setFilesAnalyzed() {
        test_packageBuilder.setFilesAnalyzed(test_filesAnalyzed);
        test_file = test_packageBuilder.buildAndFlush();
        assertEquals(test_filesAnalyzed, test_file.getFilesAnalyzed());
    }

    @Test
    void setVerificationCode() {
        test_packageBuilder.setVerificationCode(test_verificationCode);
        test_file = test_packageBuilder.buildAndFlush();
        assertEquals(test_verificationCode, test_file.getVerificationCode());
    }

    @Test
    void setHomePage() {
        test_packageBuilder.setHomePage(test_homePage);
        test_file = test_packageBuilder.buildAndFlush();
        assertEquals(test_homePage, test_file.getHomePage());
    }

    @Test
    void setSourceInfo() {
        test_packageBuilder.setSourceInfo(test_sourceInfo);
        test_file = test_packageBuilder.buildAndFlush();
        assertEquals(test_sourceInfo, test_file.getSourceInfo());
    }

    @Test
    void setReleaseDate() {
        test_packageBuilder.setReleaseDate(test_releaseDate);
        test_file = test_packageBuilder.buildAndFlush();
        assertEquals(test_releaseDate, test_file.getReleaseDate());
    }

    @Test
    void setBuildDate() {
        test_packageBuilder.setBuildDate(test_builtDate);
        test_file = test_packageBuilder.buildAndFlush();
        assertEquals(test_builtDate, test_file.getBuiltDate());
    }

    @Test
    void setValidUntilDate() {
        test_packageBuilder.setValidUntilDate(test_validUntilDate);
        test_file = test_packageBuilder.buildAndFlush();
        assertEquals(test_validUntilDate, test_file.getValidUntilDate());
    }
}
