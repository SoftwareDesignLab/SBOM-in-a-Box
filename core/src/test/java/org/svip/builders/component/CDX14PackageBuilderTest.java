package org.svip.builders.component;

import org.junit.jupiter.api.Test;
import org.svip.sbom.model.interfaces.schemas.CycloneDX14.CDX14Package;
import org.svip.sbom.model.objects.CycloneDX14.CDX14ComponentObject;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.shared.util.Description;
import org.svip.sbom.model.shared.util.ExternalReference;
import org.svip.sbom.model.shared.util.LicenseCollection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * file: CDX14PackageBuilderTest.java
 * File to test CDX14PackageBuilder
 *
 * @author Matthew Morrison
 * @author Kevin Laporte
 */
class CDX14PackageBuilderTest {
    CDX14PackageBuilder test_packageBuilder = new CDX14PackageBuilder();
    CDX14ComponentObject test_componentObj;

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
        test_componentObj = test_packageBuilder.build();
        assertEquals(test_mimeType, test_componentObj.getMimeType());
    }

    @Test
    void setPublisher() {
        test_packageBuilder.setPublisher(test_publisher);
        test_componentObj = test_packageBuilder.buildAndFlush();
        assertEquals(test_publisher, test_componentObj.getPublisher());
    }

    @Test
    void setScope() {
        test_packageBuilder.setScope(test_scope);
        test_componentObj = test_packageBuilder.buildAndFlush();
        assertEquals(test_scope, test_componentObj.getScope());
    }

    @Test
    void setGroup() {
        test_packageBuilder.setGroup(test_group);
        test_componentObj = test_packageBuilder.buildAndFlush();
        assertEquals(test_group, test_componentObj.getGroup());
    }

    @Test
    void addExternalReferences() {
        ExternalReference test_extRef = new ExternalReference(
                test_extRef_url, test_extTef_type);
        test_packageBuilder.addExternalReferences(test_extRef);

        test_componentObj = test_packageBuilder.buildAndFlush();
        assertTrue(test_componentObj.getExternalReferences().contains(test_extRef));
    }

    @Test
    void addProperty() {
        test_packageBuilder.addProperty(test_property_name, test_property_value);

        test_componentObj = test_packageBuilder.buildAndFlush();

        HashMap<String, Set<String>> test_propertyMap = new HashMap<String, Set<String>>();
        test_propertyMap.put(test_property_name, new HashSet<String>());
        test_propertyMap.get(test_property_name).add(test_property_value);

        assertEquals(test_propertyMap, test_componentObj.getProperties());
    }

    @Test
    void setType() {
        test_packageBuilder.setType(test_type);

        test_componentObj = test_packageBuilder.buildAndFlush();
        assertEquals(test_type, test_componentObj.getType());
    }

    @Test
    void setUID() {
        test_packageBuilder.setUID(test_uid);

        test_componentObj = test_packageBuilder.buildAndFlush();
        assertEquals(test_uid, test_componentObj.getUID());
    }

    @Test
    void setAuthor() {
        test_packageBuilder.setAuthor(test_author);

        test_componentObj = test_packageBuilder.buildAndFlush();
        assertEquals(test_author, test_componentObj.getAuthor());

    }

    @Test
    void setName() {
        test_packageBuilder.setName(test_name);

        test_componentObj = test_packageBuilder.buildAndFlush();
        assertEquals(test_name, test_componentObj.getName());
    }

    @Test
    void setLicenses() {
        LicenseCollection licenseCollection = new LicenseCollection();
        licenseCollection.addDeclaredLicense(test_license1);
        test_packageBuilder.setLicenses(licenseCollection);

        test_componentObj = test_packageBuilder.buildAndFlush();
        assertEquals(licenseCollection, test_componentObj.getLicenses());
    }

    @Test
    void setCopyright() {
        test_packageBuilder.setCopyright(test_copyright);

        test_componentObj = test_packageBuilder.buildAndFlush();
        assertEquals(test_copyright, test_componentObj.getCopyright());
    }

    @Test
    void addHash() {
        test_packageBuilder.addHash(test_hash_algo, test_hash_value);

        test_componentObj = test_packageBuilder.buildAndFlush();

        HashMap<String, String> test_map = new HashMap<String, String>();
        test_map.put(test_hash_algo, test_hash_value);

        assertEquals(test_map, test_componentObj.getHashes());
    }

    @Test
    void setSupplier() {
        Organization supplier = new Organization(test_supplier, "www.python.com");
        test_packageBuilder.setSupplier(supplier);

        test_componentObj = test_packageBuilder.buildAndFlush();
        assertEquals(supplier, test_componentObj.getSupplier());
    }

    @Test
    void setVersion() {
        test_packageBuilder.setVersion(test_version);

        test_componentObj = test_packageBuilder.buildAndFlush();
        assertEquals(test_version, test_componentObj.getVersion());
    }

    @Test
    void setDescription() {
        Description description = new Description(test_description);
        test_packageBuilder.setDescription(description);

        test_componentObj = test_packageBuilder.buildAndFlush();
        assertEquals(description, test_componentObj.getDescription());
    }

    @Test
    void addCPE() {
        test_packageBuilder.addCPE(test_random_cpe);

        test_componentObj = test_packageBuilder.buildAndFlush();
        assertTrue(test_componentObj.getCPEs().contains(test_random_cpe));
    }

    @Test
    void addPURL() {
        test_packageBuilder.addPURL(test_random_purl);

        test_componentObj = test_packageBuilder.buildAndFlush();
        assertTrue(test_componentObj.getPURLs().contains(test_random_purl));
    }

    @Test
    void addExternalReference() {
        ExternalReference externalReference = new ExternalReference(test_extRef_url, test_extTef_type);
        test_packageBuilder.addExternalReference(externalReference);

        test_componentObj = test_packageBuilder.buildAndFlush();
        assertTrue(test_componentObj.getExternalReferences().contains(externalReference));
    }
}