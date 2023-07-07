package org.svip.builders.component;

import org.junit.jupiter.api.Test;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.shared.util.Description;
import org.svip.sbom.model.shared.util.ExternalReference;
import org.svip.sbom.model.shared.util.LicenseCollection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * file: CDX14PackageBuilderTest.java
 * File to test CDX14PackageBuilder
 */
class CDX14PackageBuilderTest {
    CDX14PackageBuilder test_packageBuilder;

    String test_mimeType = "image/jpeg";

    String test_publisher = "The Python Foundation";

    String test_scope = "required";

    String test_group = "org.python";

    String test_extRef_url = "https://www.python.org";

    String test_extTef_type = "website";

    String test_property_name = "local";

    String test_property_value = "team_responsible";

    String test_type = "library";

    String test_uid = "749d4a17-1074-4b78-a968-fafc67378f75";

    String test_author = "Guido van Rossum";

    String test_name = "Python";

    String test_license1 = "PSFL";

    String test_license2 = "GPL";

    String test_copyright = "Test Copyright";

    String test_hash_algo = "SHA1";

    String test_hash_value = "da39a3ee5e6b4b0d3255bfef95601890afd80709";

    String test_supplier = "Python Foundation";

    String test_version = "1.23.1";

    String test_description = "This is a test description";

    String test_random_cpe = "cpe:2.3:a:random_test_cpe:random:3.11.2:*:*:*:*:*:*:*";

    String test_random_purl = "pkg:random/test@2.0.0";

    @Test
    void setMimeType() {
        test_packageBuilder.setMimeType(test_mimeType);
    }

    @Test
    void setPublisher() {
        test_packageBuilder.setPublisher(test_publisher);
    }

    @Test
    void setScope() {
        test_packageBuilder.setScope(test_scope);
    }

    @Test
    void setGroup() {
        test_packageBuilder.setGroup(test_group);
    }

    @Test
    void addExternalReferences() {
        ExternalReference test_extRef = new ExternalReference(
                test_extRef_url, test_extTef_type);
        test_packageBuilder.addExternalReferences(test_extRef);
    }

    @Test
    void addProperty() {
        test_packageBuilder.addProperty(test_property_name, test_property_value);
    }

    @Test
    void setType() {
        test_packageBuilder.setType(test_type);
    }

    @Test
    void setUID() {
        test_packageBuilder.setUID(test_uid);
    }

    @Test
    void setAuthor() {
        test_packageBuilder.setAuthor(test_author);
    }

    @Test
    void setName() {
        test_packageBuilder.setName(test_name);
    }

    @Test
    void setLicenses() {
        LicenseCollection licenseCollection = new LicenseCollection();
        licenseCollection.addDeclaredLicense(test_license1);
        test_packageBuilder.setLicenses(licenseCollection);
    }

    @Test
    void setCopyright() {
        test_packageBuilder.setCopyright(test_copyright);
    }

    @Test
    void addHash() {
        test_packageBuilder.addHash(test_hash_algo, test_hash_value);
    }

    @Test
    void setSupplier() {
        Organization supplier = new Organization(test_supplier, "www.python.com");
        test_packageBuilder.setSupplier(supplier);
    }

    @Test
    void setVersion() {
        test_packageBuilder.setVersion(test_version);
    }

    @Test
    void setDescription() {
        Description description = new Description(test_description);
        test_packageBuilder.setDescription(description);
    }

    @Test
    void addCPE() {
        test_packageBuilder.addCPE(test_random_cpe);
    }

    @Test
    void addPURL() {
        test_packageBuilder.addPURL(test_random_purl);
    }

    @Test
    void addExternalReference() {
        ExternalReference externalReference = new ExternalReference(test_extRef_url, test_extTef_type);
        test_packageBuilder.addExternalReference(externalReference);
    }

    @Test
    void build() {
    }

    @Test
    void buildAndFlush() {
    }
}