/**
 * @file VulnerabilityTest.java
 *
 * Test set for Vulnerability class
 *
 * @author Tyler Drake
 */

package org.svip.sbomvex.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.svip.sbomvex.model.VEX;

public class VEXTest {

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
    VEX test_vulnerability;


    /**
     * Set-up/Teardown Methods
     */

    // Test for vulnerability standard constructor
    @BeforeEach
    public void create_vulnerability() {
        test_vulnerability = new VEX(
                vulnId, cveId, description, platform, introducedDate, publishedDate,
                createdDate, lastModifiedDate, fixedDate, existsAtMitre, existsAtNvd,
                timeGapNvd, timeGapMitre, statusId, vexFormatIdentifier, vexAuthor,
                vexAuthorRole, productIdentifier, productStatusDetails
        );
    }

    @AfterEach
    public void delete_vulnerability() {
        test_vulnerability = null;
    }

    /**
     * Tests
     */

    @Test
    public void create_vulnerability_test() {
        assertNotNull(test_vulnerability);
    }

    // Test for getVulnId
    @Test
    public void getVulnId_test() {
        assertEquals("test_vulnerability_id_1234567890", test_vulnerability.getVulnId());
    }

    // Test for getCveId
    @Test
    public void getCveId_test() {
        assertEquals("test_cve_id_1234567890", test_vulnerability.getCveId());
    }

    // Test for getDescription
    @Test
    public void getDescription_test() {
        assertEquals("a test vulnerability", test_vulnerability.getDescription());
    }

    // Test for getPlatform
    @Test
    public void getPlatform_test() {
        assertEquals("test platform", test_vulnerability.getPlatform());
    }

    // Test for getIntroducedDate
    @Test
    public void getIntroducedDate_test() {
        assertEquals("01/01/2000", test_vulnerability.getIntroducedDate());
    }

    // Test for getPublishedDate
    @Test
    public void getPublishedDate_test() {
        assertEquals("01/02/2000", test_vulnerability.getPublishedDate());
    }

    // Test for getCreatedDate
    @Test
    public void getCreatedDate_test() {
        assertEquals("01/03/2000", test_vulnerability.getCreatedDate());
    }

    // Test for getLastModifiedDate
    @Test
    public void getLastModifiedDate_test() {
        assertEquals("01/04/2000", test_vulnerability.getLastModifiedDate());
    }

    // Test for getFixedDate
    @Test
    public void getFixedDate_test() {
        assertEquals("01/05/2000", test_vulnerability.getFixedDate());
    }

    // Test for isExistsAtMitre
    @Test
    public void isExistsAtMitre_test() {
        assertFalse(test_vulnerability.isExistsAtMitre());
    }

    // Test for isExistsAtNvd
    @Test
    public void isExistsAtNvd_test() {
        assertTrue(test_vulnerability.isExistsAtNvd());
    }

    // Test for getTimeGapNvd
    @Test
    public void getTimeGapNvd_test() {
        assertEquals(1, test_vulnerability.getTimeGapNvd());
    }

    // Test for getTimeGapMitre
    @Test
    public void getTimeGapMitre_test() {
        assertEquals(0, test_vulnerability.getTimeGapMitre());
    }

    // Test for getStatusId
    @Test
    public void getStatusId_test() {
        assertEquals(1234567890, test_vulnerability.getStatusId());
    }

    // Test for getVexFormatIdentifier
    @Test
    public void getVexFormatIdentifier_test() {
        assertEquals("test_vex_format", test_vulnerability.getVexFormatIdentifier());
    }

    // Test for getVexAuthor
    @Test
    public void getVexAuthor_test() {
        assertEquals("test@test.org", test_vulnerability.getVexAuthor());
    }

    // Test for getVexAuthorRole
    @Test
    public void getVexAuthorRole_test() {
        assertEquals("author", test_vulnerability.getVexAuthorRole());
    }

    // Test for getProductIdentifier
    @Test
    public void getProductIdentifier_test() {
        assertEquals("test", test_vulnerability.getProductIdentifier());
    }

    // Test for getProductStatusDetails
    @Test
    public void getProductStatusDetails_test() {
        assertEquals("testing", test_vulnerability.getProductStatusDetails());
    }
}
