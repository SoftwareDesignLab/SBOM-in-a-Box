package org.svip.sbom.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * File: SBOMTest.java
 * Tests for SBOM
 *
 * @author Tyler Drake
 */
public class SBOMTest {

    /**
     * Test Objects
     */

    SBOM test_sbom;

    Component test_component_one;

    Component test_component_two;

    Signature test_signature;


    /**
     * Basic test SBOM items: Set 1
     */
    SBOM.Type test_originFormat = SBOM.Type.CYCLONE_DX;

    String test_specVersion = "1";

    String test_sbomVersion = "1";

    String test_supplier = "supplier";

    String test_serialNumber = "urn:uuid:1b53623d-b96b-4660-8d25-f84b7f617c54";

    String test_timestamp = "2023-02-17T02:36:00-05:00";

    Set<Signature> test_signatures = new HashSet<>();

    DependencyTree test_dependencytree = new DependencyTree();

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
     * Basic test components items: 2
     */
    String test_name_two = "nohtyp";

    String test_publisher_two = "Testing Tester <testing@test.org>";

    String test_version_two = "three.eleven.two";

    Set<String> test_cpe_two = new HashSet<>(List.of(new String[]{"cpe:2.3:a:nohtyp_software_foundation:nohtyp:3.11.2:*:*:*:*:*:*:*"}));

    Set<String> test_purl_two = new HashSet<>(List.of("pkg:generic/nohtyp@3.11.2"));

    Set<String> test_swid_two = new HashSet<>(List.of(new String[]{"nohtyp_software_identification_number"}));

    String test_spdx_two = "SPDXRef-Package-binary-nohtyp-7b7aaa0803d10db8";


    /**
     * Basic test signature items: Set 1
     */
    Signature.Algorithm test_signature_algorithm = Signature.Algorithm.ES256;

    String keyId = "test_key_id";

    Signature.KTY test_signature_kty = Signature.KTY.EC;

    Signature.CRV test_signature_crv = Signature.CRV.P_256;

    String test_x = "5";

    String test_y = "10";

    List<String> test_certificates = Arrays.asList("test1", "test2", "test3");

    Set<String> test_excludes = new HashSet<String>() {
        {
            add("one");
            add("two");
            add("three");
        }
    };

    String test_value = "example_data";


    /**
     * Extra test values
     */
    String test_random_specVersion = "1234567890";

    String test_random_sbomVersion = "0987654321";

    String test_random_serial_number = "urn:uuid:1b53623d-8d25-4660-b96b-f84b7f617c54";

    String test_random_timestamp = "2023-02-16T02:36:00-15:00";

    String test_expected_to_string = "";

    int test_hash_code = 770939883;

    /**
     * Set-up/Teardown Methods
     */
    @BeforeEach
    public void create_sbom() {
        test_signature = new Signature(
                test_signature_algorithm, keyId, test_signature_kty, test_signature_crv,
                test_x, test_y, test_certificates, test_excludes, test_value
        );
        test_signatures.add(test_signature);

        test_component_one = new Component(
                test_name, test_publisher, test_version, test_cpe, test_purl, test_swid
        );
        test_component_two = new Component(
                test_name_two, test_publisher_two, test_version_two, test_cpe_two, test_purl_two, test_swid_two
        );

//        test_dependencytree.addComponent(null, test_component_one);
//        test_dependencytree.addComponent(test_component_one.getUUID(), test_component_two);

        test_expected_to_string = "\nSBOM Information\n" +
                "  + Origin Format: " + test_originFormat + "\n" +
                "  + Specification Version: " + test_specVersion + "\n" +
                "  + SBOM Version: " + test_sbomVersion + "\n" +
                "  + Serial Number: " + test_serialNumber + "\n" +
                "  + Supplier: " + test_publisher + "\n" +
                "  + Time Stamp: " + test_timestamp + "\n" +
                "  + Dependency Tree: " + test_dependencytree + "\n";

        test_sbom = new SBOM(
                test_originFormat, test_specVersion, test_sbomVersion, test_publisher,
                test_serialNumber, test_timestamp, test_signatures, test_dependencytree
        );
    }

    @AfterEach
    public void delete_sbom() {
        test_signature = null;
        test_signatures = null;
        test_component_one = null;
        test_sbom = null;
    }

    /**
     * Tests
     */

    @Test
    public void addComponent_test() {
        assertTrue(test_sbom.addComponent(null, test_component_one) instanceof UUID);
    }

    @Test
    public void getAllComponents_test() {
        assertEquals(0, test_sbom.getAllComponents().size());
        test_sbom.addComponent(null, test_component_one);
        assertEquals(1, test_sbom.getAllComponents().size());
    }

    @Test
    public void getHeadUUID_test() {
        test_sbom.addComponent(null, test_component_one);
        UUID parent = test_sbom.getHeadUUID();
        assertEquals(test_component_one, test_sbom.getComponent(parent));
    }

    @Test
    public void getComponent_test() {
        test_sbom.addComponent(null, test_component_one);
        UUID parent = test_sbom.getHeadUUID();
        assertEquals(test_component_one, test_sbom.getComponent(parent));
    }

    @Test
    public void getComponentChildren_test() {
        test_sbom.addComponent(null, test_component_one);
        UUID parent = test_sbom.getHeadUUID();
        test_sbom.addComponent(parent, test_component_two);
        assertEquals(1, test_sbom.getComponentChildren(parent).size());
        assertEquals(test_component_two, test_sbom.getComponentChildren(parent).iterator().next());
    }

    @Test
    public void getChildrenUUIDs_test() {
        test_sbom.addComponent(null, test_component_one);
        UUID parent = test_sbom.getHeadUUID();
        UUID child = test_sbom.addComponent(parent, test_component_two);
        assertTrue(test_sbom.getChildrenUUIDs(parent) instanceof Set<UUID>);
        assertEquals(child, test_sbom.getChildrenUUIDs(parent).iterator().next());
    }

    @Test
    public void assignOriginFormat_is_cyclone_dx_test() {
        assertEquals(SBOM.Type.CYCLONE_DX, test_sbom.assignOriginFormat("cyclonedx"));
    }

    @Test
    public void assignOriginFormat_is_SPDX_test() {
        assertEquals(SBOM.Type.SPDX, test_sbom.assignOriginFormat("spdx"));
    }

    @Test
    public void assignOriginFormat_is_other_when_null_test() {
        assertEquals(SBOM.Type.Other, test_sbom.assignOriginFormat(null));
    }

    @Test
    public void assignOriginFormat_is_not_cyclone_dx_when_null_test() {
        assertNotEquals(SBOM.Type.CYCLONE_DX, test_sbom.assignOriginFormat(null));
    }

    @Test
    public void assignOriginFormat_is_not_SPDX_when_null_test() {
        assertNotEquals(SBOM.Type.SPDX, test_sbom.assignOriginFormat(null));
    }

    @Test
    public void assignOriginFormat_is_other_when_empty_test() {
        assertEquals(SBOM.Type.Other, test_sbom.assignOriginFormat(""));
    }

    @Test
    public void assignOriginFormat_is_not_cyclone_dx_when_empty_test() {
        assertNotEquals(SBOM.Type.CYCLONE_DX, test_sbom.assignOriginFormat(""));

    }

    @Test
    public void assignOriginFormat_is_not_SPDX_when_empty_test() {
        assertNotEquals(SBOM.Type.SPDX, test_sbom.assignOriginFormat(""));
    }

    @Test
    public void setOriginFormat_getOriginFormat_cyclone_dx_test() {
        test_sbom.setOriginFormat(SBOM.Type.CYCLONE_DX);
        assertEquals(SBOM.Type.CYCLONE_DX, test_sbom.getOriginFormat());
    }

    @Test
    public void setOriginFormat_getOriginFormat_SPDX_test() {
        test_sbom.setOriginFormat(SBOM.Type.SPDX);
        assertEquals(SBOM.Type.SPDX, test_sbom.getOriginFormat());
    }

    @Test
    public void setOriginFormat_getOriginFormat_other_test() {
        test_sbom.setOriginFormat(SBOM.Type.Other);
        assertEquals(SBOM.Type.Other, test_sbom.getOriginFormat());
    }

    @Test
    public void getSpecVersion_test() {
        assertEquals("1", test_sbom.getSpecVersion());
    }

    @Test
    public void setSpecVersion_test() {
        test_sbom.setSpecVersion(test_random_specVersion);
        assertEquals("1234567890", test_sbom.getSpecVersion());
    }

    @Test
    public void getSbomVersion_test() {
        assertEquals("1", test_sbom.getSbomVersion());
    }

    @Test
    public void setSbomVersion_test() {
        test_sbom.setSbomVersion(test_random_sbomVersion);
        assertEquals("0987654321", test_sbom.getSbomVersion());
    }

    @Test
    public void getSerialNumber_test() {
        assertEquals("urn:uuid:1b53623d-b96b-4660-8d25-f84b7f617c54", test_sbom.getSerialNumber());
    }

    @Test
    public void setSerialNumber_test() {
        test_sbom.setSerialNumber(test_random_serial_number);
        assertEquals("urn:uuid:1b53623d-8d25-4660-b96b-f84b7f617c54", test_sbom.getSerialNumber());
    }

    @Test
    public void getTimestamp_test() {
        assertEquals("2023-02-17T02:36:00-05:00", test_sbom.getTimestamp());
    }

    @Test
    public void setTimestamp_test() {
        test_sbom.setTimestamp(test_random_timestamp);
        assertEquals("2023-02-16T02:36:00-15:00", test_sbom.getTimestamp());
    }

    @Test
    public void toString_test() {
        assertEquals(test_expected_to_string, test_sbom.toString());
    }

    @Test
    public void hashCode_test() {
        assertEquals(test_hash_code, test_sbom.hashCode());
    }

}
