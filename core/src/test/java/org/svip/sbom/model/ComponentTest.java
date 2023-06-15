package org.svip.sbom.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * File: ComponentTest.java
 * Tests for Component
 *
 * @author Tyler Drake
 */
public class ComponentTest {

    /**
     * Test components
     */

    Component test_component;


    /**
     * Basic test component items: Set 1
     */
    String test_name = "python";

    String test_publisher = "Test Tester <test@test.org>";

    String test_version = "3.11.2";

    Set<String> test_cpe = new HashSet<>(List.of(new String[]{"cpe:2.3:a:python_software_foundation:python:3.11.2:*:*:*:*:*:*:*"}));

    Set<String> test_purl = new HashSet<>(List.of("pkg:generic/python@3.11.2"));

    Set<String> test_swid = new HashSet<>(List.of(new String[]{"python_software_identification_number"}));

    String test_spdx = "SPDXRef-Package-binary-python-7b7aaa0803d10db8";


    /**
     * Basic test component items: Set 2
     */

    String test_name_two = "nohtyp";

    String test_version_two = "three.eleven.two";

    Set<String> test_cpe_two = new HashSet<>(List.of(new String[]{"cpe:2.3:a:nohtyp_software_foundation:nohtyp:3.11.2:*:*:*:*:*:*:*"}));

    Set<String> test_purl_two = new HashSet<>(List.of("pkg:generic/nohtyp@3.11.2"));

    Set<String> test_swid_two = new HashSet<>(List.of(new String[]{"nohtyp_software_identification_number"}));

    String test_spdx_two = "SPDXRef-Package-binary-nohtyp-7b7aaa0803d10db8";


    /**
     * Extra test items
     */
    HashSet<String> test_licenses = new HashSet<>();

    String test_license_one = "test_license_one";

    String test_license_two = "test_license_two";

    String test_license_three = "test_license_three";

    UUID test_uuid_one = UUID.fromString("749d4a17-1074-4b78-a968-fafc67378f75");

    UUID test_uuid_two = UUID.fromString("7facc5f3-aef2-4327-9313-247aea23455c");

    /**
     * Test CPEs, PURLs, SWIDs
     */

    String test_random_cpe = "cpe:2.3:a:random_test_cpe:random:3.11.2:*:*:*:*:*:*:*";

    String test_random_purl = "pkg:random/test@2.0.0";

    String test_random_swid = "random_test_identification_number";


    /**
     * Test values for test vulnerability: Set 1
     */
    String vulnId = "test_vulnerability_id_1234567890";
    String cveId = "test_cve_id_1234567890";
    String description = "a test vulnerability";
    String platform = "test platform";
    String introducedDate = "01/01/2000";
    String publishedDate = "01/02/2000";
    String createdDate = "01/03/2000";
    String lastModifiedDate = "01/04/2000";
    String fixedDate = "01/05/2000";
    boolean existsAtMitre = false;
    boolean existsAtNvd = true;
    int timeGapNvd = 1;
    int timeGapMitre = 0;
    int statusId = 1234567890;
    String vexFormatIdentifier = "test_vex_format";
    String vexAuthor = "test@test.org";
    String vexAuthorRole = "author";
    String productIdentifier = "test";
    String productStatusDetails = "testing";

    /**
     * Set-up/Tear down Methods
     */

    @BeforeEach
    public void create_component() {
        test_component = new Component(test_name, test_publisher, test_version, test_cpe, test_purl, test_swid);
    }

    @AfterEach
    public void delete_component() {
        test_component = null;
    }

    /**
     * Tests
     */

    @Test
    public void getName_test() {
        assertEquals("python", test_component.getName());
    }

    @Test
    public void setName_test() {
        test_component.setName(test_name_two);
        assertEquals("nohtyp", test_component.getName());
    }


    @Test
    public void getPublisher_test() {
        assertEquals("Test Tester <test@test.org>", test_component.getPublisher());
    }

    @Test
    public void getVersion_test() {
        assertEquals("3.11.2", test_component.getVersion());
    }

    @Test
    public void setVersion_test() {
        test_component.setVersion(test_version_two);
        assertEquals("three.eleven.two", test_component.getVersion());
    }

    @Test
    public void setLicenses_test() {
        test_licenses.add(test_license_one);
        test_licenses.add(test_license_two);
        test_licenses.add(test_license_three);

        test_component.setLicenses(test_licenses);
    }

    @Test
    public void getLicense_test() {
        test_licenses.add(test_license_one);
        test_licenses.add(test_license_two);
        test_licenses.add(test_license_three);

        test_component.setLicenses(test_licenses);

        assertEquals("test_license_one", test_component.getLicense(test_license_one));

    }

    @Test
    public void getLicense_should_not_get_wrong_license_test() {
        test_licenses.add(test_license_one);
        test_licenses.add(test_license_two);
        test_licenses.add(test_license_three);

        test_component.setLicenses(test_licenses);

        assertNotEquals("test_license_one", test_component.getLicense(test_license_two));
    }

    @Test
    public void getLicense_should_not_get_non_existing_license_test() {
        test_licenses.add(test_license_one);
        test_licenses.add(test_license_two);

        test_component.setLicenses(test_licenses);

        assertNull(test_component.getLicense(test_license_three));
    }

    @Test
    public void getLicenses_test() {
        test_licenses.add(test_license_one);
        test_licenses.add(test_license_two);

        test_component.setLicenses(test_licenses);

        assertEquals(2, test_component.getLicenses().size());
    }

    @Test
    public void addLicense_test() {
        test_licenses.add(test_license_one);
        test_licenses.add(test_license_two);

        test_component.setLicenses(test_licenses);
        assertEquals(2, test_component.getLicenses().size());

        test_component.addLicense(test_license_three);
        assertEquals(3, test_component.getLicenses().size());
    }

//    @Test
//    public void addLicense_should_get_null_exception_without_existing_license_list() {
//        assertThrows(NullPointerException.class, () -> {
//            test_component.addLicense(test_license_three);
//        });
//    }

    @Test
    public void addChild_test() {
        test_component.addChild(test_uuid_one);
    }

    @Test
    public void getChildren_test() {
        assertEquals(0, test_component.getChildren().size());

        test_component.addChild(test_uuid_one);
        test_component.addChild(test_uuid_two);

        assertEquals(2, test_component.getChildren().size());
    }

    @Test
    public void removeChildren_test() {
        test_component.addChild(test_uuid_one);
        test_component.addChild(test_uuid_two);

        assertEquals(2, test_component.getChildren().size());

        test_component.removeChild(test_uuid_one);

        assertEquals(1, test_component.getChildren().size());
    }

    @Test
    public void getCPE_test() {
        assertEquals(new HashSet<>(List.of(new String[]{"cpe:2.3:a:python_software_foundation:python:3.11.2:*:*:*:*:*:*:*"})), test_component.getCpes());
    }

    @Test
    public void getPurl_test() {
        assertEquals(new HashSet<>(List.of("pkg:generic/python@3.11.2")), test_component.getPurls());
    }

    @Test
    public void getSWID_test() {
        assertEquals(new HashSet<>(List.of(new String[]{"python_software_identification_number"})), test_component.getSwids());
    }

    @Test
    public void setCPE_test() {
        test_component.addCPE(test_random_cpe);
        assertTrue(test_component.getCpes().contains(test_random_cpe));
    }

    @Test
    public void setPurl_test() {
        test_component.addPURL(test_random_purl);
        assertTrue(test_component.getPurls().contains(test_random_purl));
    }

    @Test
    public void setSWID_test() {
        test_component.addSWID(test_random_swid);
        assertTrue(test_component.getSwids().contains(test_random_swid));
    }

    @Test
    public void create_spdx_component_test() {
        Component test_spdx_component = new Component(test_name, test_publisher, test_version, test_spdx);
        assertNotNull(test_spdx_component);
    }

    @Test
    public void getSPDXID_test() {
        Component test_spdx_component = new Component(test_name, test_publisher, test_version, test_spdx);
        assertEquals("SPDXRef-Package-binary-python-7b7aaa0803d10db8", test_spdx_component.getUniqueID());
    }

    @Test
    public void setSPDXID_test() {
        Component test_spdx_component = new Component(test_name, test_publisher, test_version, test_spdx);
        test_spdx_component.setUniqueID(test_spdx_two);
        assertEquals("SPDXRef-Package-binary-nohtyp-7b7aaa0803d10db8", test_spdx_component.getUniqueID());
    }

    @Test
    public void addVulnerability_test() {
        Vulnerability test_vulnerability = new Vulnerability(
                vulnId, cveId, description, platform, introducedDate, publishedDate,
                createdDate, lastModifiedDate, fixedDate, existsAtMitre, existsAtNvd,
                timeGapNvd, timeGapMitre, statusId, vexFormatIdentifier, vexAuthor,
                vexAuthorRole, productIdentifier, productStatusDetails
        );

        test_component.addVulnerability(test_vulnerability);
    }

    @Test
    public void getVulnerabilities_test() {
        Vulnerability test_vulnerability = new Vulnerability(
                vulnId, cveId, description, platform, introducedDate, publishedDate,
                createdDate, lastModifiedDate, fixedDate, existsAtMitre, existsAtNvd,
                timeGapNvd, timeGapMitre, statusId, vexFormatIdentifier, vexAuthor,
                vexAuthorRole, productIdentifier, productStatusDetails
        );
        assertEquals(0, test_component.getVulnerabilities().size());
        test_component.addVulnerability(test_vulnerability);
        assertEquals(1, test_component.getVulnerabilities().size());
    }

    @Test
    public void toString_test() {
        assertEquals("Test Tester <test@test.org> python:3.11.2", test_component.toString());
    }
}
