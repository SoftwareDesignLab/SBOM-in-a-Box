package org.svip.sbom.builder.objects.schemas.CDX14;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.svip.sbom.builder.objects.schemas.CDX14.CDX14PackageBuilder;
import org.svip.sbom.factory.objects.CycloneDX14.CDX14PackageBuilderFactory;
import org.svip.sbom.model.objects.CycloneDX14.CDX14ComponentObject;
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
 * file: CDX14PackageBuilderTest.java
 * File to test CDX14PackageBuilder
 *
 * @author Matthew Morrison
 * @author Kevin Laporte
 */
class CDX14PackageBuilderTest {
    CDX14PackageBuilder test_packageBuilder;
    CDX14ComponentObject test_package;

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

    @BeforeEach
    void create_test_packageBuilder(){
        CDX14PackageBuilderFactory test_CDX14PackageBuilderFactory = new CDX14PackageBuilderFactory();
        test_packageBuilder = test_CDX14PackageBuilderFactory.createBuilder();
    }

    @Test
    void getMimeType_is_test_mimeType_when_setMimeType_is_used_test() {
        test_packageBuilder.setMimeType(test_mimeType);
        test_package = test_packageBuilder.build();
        assertEquals(test_mimeType, test_package.getMimeType());
    }

    @Test
    void getPublisher_is_test_publisher_setPublisher_is_used_test() {
        test_packageBuilder.setPublisher(test_publisher);
        test_package = test_packageBuilder.buildAndFlush();
        assertEquals(test_publisher, test_package.getPublisher());
    }

    @Test
    void getScope_is_test_scope_when_setScope_is_used_test() {
        test_packageBuilder.setScope(test_scope);
        test_package = test_packageBuilder.buildAndFlush();
        assertEquals(test_scope, test_package.getScope());
    }

    @Test
    void getGroup_is_test_group_when_setGroup_is_used_test() {
        test_packageBuilder.setGroup(test_group);
        test_package = test_packageBuilder.buildAndFlush();
        assertEquals(test_group, test_package.getGroup());
    }

    @Test
    void getExternalReferences_contains_test_extRef_when_addExternalReferences_is_used_test() {
        ExternalReference test_extRef = new ExternalReference(
                test_extRef_url, test_extTef_type);
        test_packageBuilder.addExternalReferences(test_extRef);

        test_package = test_packageBuilder.buildAndFlush();
        assertTrue(test_package.getExternalReferences().contains(test_extRef));
    }

    @Test
    void getProperties_is_test_propertyMap_when_addProperty_is_used_test() {
        test_packageBuilder.addProperty(test_property_name, test_property_value);

        test_package = test_packageBuilder.buildAndFlush();

        HashMap<String, Set<String>> test_propertyMap = new HashMap<String, Set<String>>();
        test_propertyMap.put(test_property_name, new HashSet<String>());
        test_propertyMap.get(test_property_name).add(test_property_value);

        assertEquals(test_propertyMap, test_package.getProperties());
    }

    @Test
    void getType_is_test_type_when_setType_is_used_test() {
        test_packageBuilder.setType(test_type);

        test_package = test_packageBuilder.buildAndFlush();
        assertEquals(test_type, test_package.getType());
    }

    @Test
    void getUID_is_test_uid_when_setUID_is_used_test() {
        test_packageBuilder.setUID(test_uid);

        test_package = test_packageBuilder.buildAndFlush();
        assertEquals(test_uid, test_package.getUID());
    }

    @Test
    void getAuthor_is_test_author_when_setAuthor_is_used_test() {
        test_packageBuilder.setAuthor(test_author);

        test_package = test_packageBuilder.buildAndFlush();
        assertEquals(test_author, test_package.getAuthor());

    }

    @Test
    void getName_is_test_name_when_setName_is_used_test() {
        test_packageBuilder.setName(test_name);

        test_package = test_packageBuilder.buildAndFlush();
        assertEquals(test_name, test_package.getName());
    }

    @Test
    void getLicenses_is_licenseCollection_when_setLicenses_is_used_test() {
        LicenseCollection licenseCollection = new LicenseCollection();
        licenseCollection.addDeclaredLicense(test_license1);
        licenseCollection.addDeclaredLicense(test_license2);
        test_packageBuilder.setLicenses(licenseCollection);

        test_package = test_packageBuilder.buildAndFlush();
        assertEquals(licenseCollection, test_package.getLicenses());
    }

    @Test
    void getCopyright_is_test_copyright_when_setCopyright_is_used_test() {
        test_packageBuilder.setCopyright(test_copyright);

        test_package = test_packageBuilder.buildAndFlush();
        assertEquals(test_copyright, test_package.getCopyright());
    }

    @Test
    void getHashes_is_test_map_when_addHash_is_used_test() {
        test_packageBuilder.addHash(test_hash_algo, test_hash_value);

        test_package = test_packageBuilder.buildAndFlush();

        HashMap<String, String> test_map = new HashMap<String, String>();
        test_map.put(test_hash_algo, test_hash_value);

        assertEquals(test_map, test_package.getHashes());
    }

    @Test
    void getSupplier_is_test_supplier_when_setSupplier_is_used_test() {
        Organization supplier = new Organization(test_supplier, "www.python.com");
        test_packageBuilder.setSupplier(supplier);

        test_package = test_packageBuilder.buildAndFlush();
        assertEquals(supplier, test_package.getSupplier());
    }

    @Test
    void getVersion_is_test_version_when_setVersion_is_used_test() {
        test_packageBuilder.setVersion(test_version);

        test_package = test_packageBuilder.buildAndFlush();
        assertEquals(test_version, test_package.getVersion());
    }

    @Test
    void getDescription_is_test_description_when_setDescription_is_used_test() {
        Description description = new Description(test_description);
        test_packageBuilder.setDescription(description);

        test_package = test_packageBuilder.buildAndFlush();
        assertEquals(description, test_package.getDescription());
    }

    @Test
    void getCPEs_contains_test_random_cpe_when_addCPE_is_used_test() {
        test_packageBuilder.addCPE(test_random_cpe);

        test_package = test_packageBuilder.buildAndFlush();
        assertTrue(test_package.getCPEs().contains(test_random_cpe));
    }

    @Test
    void getPURLs_contains_test_random_purl_when_addPURL_is_used_test() {
        test_packageBuilder.addPURL(test_random_purl);

        test_package = test_packageBuilder.buildAndFlush();
        assertTrue(test_package.getPURLs().contains(test_random_purl));
    }

    @Test
    void getExternalReferences_contains_externalReference_when_addExternalReference_is_used_test() {
        ExternalReference externalReference = new ExternalReference(test_extRef_url, test_extTef_type);
        test_packageBuilder.addExternalReference(externalReference);

        test_package = test_packageBuilder.buildAndFlush();
        assertTrue(test_package.getExternalReferences().contains(externalReference));
    }
}