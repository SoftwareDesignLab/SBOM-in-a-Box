package org.svip.vex.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.svip.vex.vexstatement.Product;
import org.svip.vex.vexstatement.VEXStatement;
import org.svip.vex.vexstatement.Vulnerability;
import org.svip.vex.vexstatement.status.Justification;
import org.svip.vex.vexstatement.status.Status;
import org.svip.vex.vexstatement.status.VulnStatus;

import java.io.IOException;


/**
 * file: VEXTest.java
 * File to test generating a VEX Object
 */
class VEXTest {
    private final VEXType testType = VEXType.CYCLONE_DX;


    /**Sample VEX Statement 1*/
    private final String testStatementID1 = "GHSA-5mg8-w23w-74h3";

    private final String testStateVersion = "3.1";
    private final String testStFirstIssue1 = "2021-03-25T17:04:19Z";
    private final String testStLastUpdate1 = "2023-06-06T19:03:04.529255Z";
    private final VulnStatus testStatus = VulnStatus.AFFECTED;
    private final String testActionStatement1 = "A temp directory creation " +
            "vulnerability exists in all Guava versions allowing an " +
            "attacker with access to the machine to potentially access " +
            "data in a temporary directory created by the Guava " +
            "`com.google.common.io.Files.createTempDir()`. " +
            "The permissions granted to the directory created default to " +
            "the standard unix-like /tmp ones, leaving the files open. We " +
            "recommend explicitly changing the permissions after the creation " +
            "of the directory, or removing uses of the vulnerable method";

    private final String testProduct1 = "Guava 10.0-rc1";
    private final String testProduct2 = "Guava 19.0";
    private final String testSupplier1 = "Google";
    private final String testVulnDesc = "Information Disclosure in Guava" ;
    private final String testVulnID1 = "CVE-2020-8908";

    /**Sample VEX Statement 2*/
    private final String testStatementID2 = "GHSA-462w-v97r-4m45";

    private final String testStateVersion2 = "2.0";
    private final String testStFirstIssue2 = "2019-04-10T14:30:24Z";
    private final String testStLastUpdate2 = "2023-04-11T01:32:48.206786Z";
    private final String testActionStatement2 = "Upgrade to version " +
            "2.10.1 or later.";

    private final String testProduct3 = "jinja2 2.0";
    private final String testProduct4 = "Guava 2.9.6f";
    private final String testSupplier2 = "The Pallets Projects";

    private final String testVulnDesc2 = "In Pallets Jinja before 2.10.1, " +
            "str.format_map allows a sandbox escape.";
    private final String testVulnID2 = "CVE-2019-10906";

    /**Sample VEX Statement 3*/
    private final String testStatementID3 = "CVE-2021-44228";
    private final String testStateVersion3 = "2.0";
    private final VulnStatus testStatus2 = VulnStatus.NOT_AFFECTED;
    private final String testStFirstIssue3 = "2022-03-03T11:00:00.000Z";
    private final String testStLastUpdate3 = "2022-03-03T11:00:00.000Z";
    private final Justification testJustification = Justification.COMPONENT_NOT_PRESENT;
    private final String testImpactStatement = "log4j-core is " +
            "not used in this project";

    private final String testProduct5 = "Example Company ABC 4.2 CSAFPID-0001";
    private final String testProduct6 = "log4cxx";
    private final String testSupplier3 = "Apache";

    private final String testVulnDesc3 = "Apache Log4j2 2.0-beta9 through 2.15.0 " +
            "(excluding security releases 2.12.2, 2.12.3, and 2.3.1) JNDI " +
            "features used in configuration, log messages, and parameters " +
            "do not protect against attacker controlled LDAP and other JNDI " +
            "related endpoints. An attacker who can control log messages or " +
            "log message parameters can execute arbitrary code loaded from LDAP" +
            " servers when message lookup substitution is enabled. From log4j " +
            "2.15.0, this behavior has been disabled by default. From version " +
            "2.16.0 (along with 2.12.2, 2.12.3, and 2.3.1), this functionality " +
            "has been completely removed. Note that this vulnerability is specific" +
            " to log4j-core and does not affect log4net, log4cxx, " +
            "or other Apache Logging Services projects.";
    private final String testVulnID3 = "CVE-2021-44228";

    @Test
    public void manual_VEX_Build_test() throws IOException {
        VEX.Builder builder = new VEX.Builder();
        String testVEXIdentifier = "TestAuthor/1234321";
        builder.setVEXIdentifier(testVEXIdentifier);
        builder.setOriginType(testType);
        String testSpecVersion = "1.4";
        builder.setSpecVersion(testSpecVersion);
        String testDocVersion = "1.0";
        builder.setDocVersion(testDocVersion);
        String testTimeFirstIssued = "2023-7-17T23:15:00Z";
        builder.setTimeFirstIssued(testTimeFirstIssued);
        String testTimeLastUpdated = "2023-7-18T23:15:00Z";
        builder.setTimeLastUpdated(testTimeLastUpdated);

        VEXStatement.Builder vSBuilder1 = new VEXStatement.Builder();
        vSBuilder1.setStatementID(testStatementID1);
        vSBuilder1.setStatementVersion(testStateVersion);
        vSBuilder1.setStatementFirstIssued(testStFirstIssue1);
        vSBuilder1.setStatementLastUpdated(testStLastUpdate1);

        Status status1 = new Status(testStatus, null, testActionStatement1, null);
        vSBuilder1.setStatus(status1);

        Product product1 = new Product(testProduct1, testSupplier1);
        Product product2 = new Product(testProduct2, testSupplier1);

        vSBuilder1.addProduct(product1);
        vSBuilder1.addProduct(product2);

        Vulnerability vuln1 = new Vulnerability(testVulnID1, testVulnDesc);
        vSBuilder1.setVulnerability(vuln1);

        VEXStatement vexStatement1 = vSBuilder1.build();

        VEXStatement.Builder vSBuilder2 = new VEXStatement.Builder();
        vSBuilder2.setStatementID(testStatementID2);
        vSBuilder2.setStatementVersion(testStateVersion2);
        vSBuilder2.setStatementFirstIssued(testStFirstIssue2);
        vSBuilder2.setStatementLastUpdated(testStLastUpdate2);

        Status status2 = new Status(testStatus, null, testActionStatement2, null);
        vSBuilder2.setStatus(status2);

        Product product3 = new Product(testProduct3, testSupplier2);
        Product product4 = new Product(testProduct4, testSupplier2);

        vSBuilder2.addProduct(product3);
        vSBuilder2.addProduct(product4);

        Vulnerability vuln2 = new Vulnerability(testVulnID2, testVulnDesc2);
        vSBuilder2.setVulnerability(vuln2);

        VEXStatement vexStatement2 = vSBuilder2.build();

        VEXStatement.Builder vSBuilder3 = new VEXStatement.Builder();
        vSBuilder3.setStatementID(testStatementID3);
        vSBuilder3.setStatementVersion(testStateVersion3);
        vSBuilder3.setStatementFirstIssued(testStFirstIssue3);
        vSBuilder3.setStatementLastUpdated(testStLastUpdate3);

        Status status3 = new Status(testStatus2, testJustification, null, testImpactStatement);
        vSBuilder3.setStatus(status3);

        Product product5 = new Product(testProduct5, testSupplier3);
        Product product6 = new Product(testProduct6, testSupplier3);

        vSBuilder3.addProduct(product5);
        vSBuilder3.addProduct(product6);

        Vulnerability vuln3 = new Vulnerability(testVulnID3, testVulnDesc3);
        vSBuilder3.setVulnerability(vuln3);

        VEXStatement vexStatement3 = vSBuilder3.build();

        builder.addVEXStatement(vexStatement1);
        builder.addVEXStatement(vexStatement2);
        builder.addVEXStatement(vexStatement3);
        VEX testVEX = builder.build();

        System.out.println(testVEX.toString());

        ObjectMapper objectMapper = new ObjectMapper();
        // pretty print
        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(testVEX);
        System.out.println(json);

    }
}