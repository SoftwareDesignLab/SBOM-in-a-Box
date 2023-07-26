package org.svip.sbomvex.database;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.objects.CycloneDX14.CDX14ComponentObject;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbomvex.model.VEX;
import org.svip.sbomvex.model.VEXType;
import org.svip.sbomvex.vexstatement.VEXStatement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class NVDClientTest {
    NVDClient nvdClient = new NVDClient();

    @Test
    public void test_generateVEX_valid_VEX_object_CDX_json_sbom() throws Exception {
        Organization test_org1 = new Organization("com.iframe_project", null);
        Organization test_org2 = new Organization("org.bouncycastle", null);
        Set<String> test_cpe1 = new HashSet<>(List.of(
                "cpe:2.3:a:iframe_project:iframe:3.0:*:*:*:*:wordpress:*:*"
        ));
        Set<String> test_cpe2 = new HashSet<>(List.of(
                "cpe:2.3:a:bouncycastle:fips_java_api:1.0.2:*:*:*:*:*:*:*"
        ));

        CDX14ComponentObject test_component1 = new CDX14ComponentObject(
                null, null, "com.iframe_project", "iframe", null, null,
                null, test_org1, "3.0", null, test_cpe1, null,
                null, null, null, null, null, null);
        CDX14ComponentObject test_component2 = new CDX14ComponentObject(
                null, null, null, "fips_java_api", null, null,
                null, test_org2, "2.4.1", null, test_cpe2, null,
                null, null, null, null, null, null);
        Set<Component> test_components = new HashSet<>(List.of(
                test_component1,  test_component2)
        );

        CDX14SBOM test_sbom = new CDX14SBOM("CycloneDX 1.4", "test", null,
                "1.0", "1.4", null, null, null, null,
                test_components, null, null);

        VEX.Builder testVEX = new VEX.Builder();
        String creationTime = String.valueOf(java.time.LocalDateTime.now());
        testVEX.setVEXIdentifier(test_sbom.getName());
        testVEX.setOriginType(VEXType.CYCLONE_DX);
        testVEX.setSpecVersion("1.4");
        testVEX.setDocVersion("1.0");
        testVEX.setTimeFirstIssued(creationTime);
        testVEX.setTimeLastUpdated(creationTime);

        List<VEXStatement> statements = new ArrayList<>();
        statements.addAll(nvdClient.getVEXStatements(test_component1));
        statements.addAll(nvdClient.getVEXStatements(test_component2));

        for(VEXStatement v :statements){
            testVEX.addVEXStatement(v);
        }

        VEX vex = testVEX.build();

        ObjectMapper objectMapper = new ObjectMapper();
        // pretty print
        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(vex);
        System.out.println(json);
    }

    @Test
    public void test_generateVEX_valid_VEX_object_unknown_supplier_CDX_json_sbom() throws Exception {
        Set<String> test_cpe1 = new HashSet<>(List.of(
                "cpe:2.3:a:iframe_project:iframe:3.0:*:*:*:*:wordpress:*:*"
        ));
        Set<String> test_cpe2 = new HashSet<>(List.of(
                "cpe:2.3:a:bouncycastle:fips_java_api:1.0.2:*:*:*:*:*:*:*"
        ));

        CDX14ComponentObject test_component1 = new CDX14ComponentObject(
                null, null, "com.iframe_project", "iframe", null, null,
                null, null, "3.0", null, test_cpe1, null,
                null, null, null, null, null, null);
        CDX14ComponentObject test_component2 = new CDX14ComponentObject(
                null, null, null, "fips_java_api", null, null,
                null, null, "2.4.1", null, test_cpe2, null,
                null, null, null, null, null, null);
        Set<Component> test_components = new HashSet<>(List.of(
                test_component1,  test_component2)
        );

        CDX14SBOM test_sbom = new CDX14SBOM("CycloneDX 1.4", "test", null,
                "1.0", "1.4", null, null, null, null,
                test_components, null, null);

        VEX.Builder testVEX = new VEX.Builder();
        String creationTime = String.valueOf(java.time.LocalDateTime.now());
        testVEX.setVEXIdentifier(test_sbom.getName());
        testVEX.setOriginType(VEXType.CYCLONE_DX);
        testVEX.setSpecVersion("1.4");
        testVEX.setDocVersion("1.0");
        testVEX.setTimeFirstIssued(creationTime);
        testVEX.setTimeLastUpdated(creationTime);

        List<VEXStatement> statements = new ArrayList<>();
        statements.addAll(nvdClient.getVEXStatements(test_component1));
        statements.addAll(nvdClient.getVEXStatements(test_component2));

        for(VEXStatement v :statements){
            testVEX.addVEXStatement(v);
        }

        VEX vex = testVEX.build();
        ObjectMapper objectMapper = new ObjectMapper();
        // pretty print
        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(vex);
        System.out.println(json);
    }

    @Test
    public void test_generateVEX_null_cpes_CDX_json_sbom() {
        Organization test_org1 = new Organization("com.iframe_project", null);
        Organization test_org2 = new Organization("org.bouncycastle", null);

        CDX14ComponentObject test_component1 = new CDX14ComponentObject(
                null, null, "com.iframe_project", "iframe", null, null,
                null, test_org1, "3.0", null, null, null,
                null, null, null, null, null, null);
        CDX14ComponentObject test_component2 = new CDX14ComponentObject(
                null, null, null, "fips_java_api", null, null,
                null, test_org2, "2.4.1", null, null, null,
                null, null, null, null, null, null);

        assertThrows(Exception.class, () -> nvdClient.getVEXStatements(test_component1));
        assertThrows(Exception.class, () -> nvdClient.getVEXStatements(test_component2));
    }


    @Test
    public void test_generateVEX_empty_cpe_list_CDX_json_sbom() {
        Organization test_org1 = new Organization("com.iframe_project", null);
        Organization test_org2 = new Organization("org.bouncycastle", null);
        Set<String> test_cpe1 = new HashSet<>();
        Set<String> test_cpe2 = new HashSet<>();

        CDX14ComponentObject test_component1 = new CDX14ComponentObject(
                null, null, "com.iframe_project", "iframe", null, null,
                null, test_org1, "3.0", null, test_cpe1, null,
                null, null, null, null, null, null);
        CDX14ComponentObject test_component2 = new CDX14ComponentObject(
                null, null, null, "fips_java_api", null, null,
                null, test_org2, "2.4.1", null, test_cpe2, null,
                null, null, null, null, null, null);

        assertThrows(Exception.class, () -> nvdClient.getVEXStatements(test_component1));
        assertThrows(Exception.class, () -> nvdClient.getVEXStatements(test_component2));
    }

    @Test
    public void test_no_vulnerabilities_with_cpe_test() throws Exception {
        Set<String> test_cpe1 = new HashSet<>(List.of(
                "cpe:2.3:a:apollo-client:apollo-client:1.9.1:*:*:*:*:*:*:*"
        ));

        CDX14ComponentObject test_component1 = new CDX14ComponentObject(
                null, null, "apollo-client", "apollo-client", null, null,
                null, null, "1.9.1", null, test_cpe1, null,
                null, null, null, null, null, null);

        List<VEXStatement> statements = new ArrayList<>(nvdClient.getVEXStatements(test_component1));
        assert(statements.isEmpty());
    }
}