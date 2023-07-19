package org.svip.sbomvex.database;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.objects.CycloneDX14.CDX14ComponentObject;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.shared.metadata.Organization;

import org.svip.sbomvex.model.VEX;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class OSVClientTest {
    OSVClient client = new OSVClient();

    @Test
    public void test_purl_api_request_not_null(){
        String response = client.getOSVByPURLPost("pkg:maven/com.google.guava/guava@19.0");
        assertNotNull(response);
        assertNotEquals("{}", response);
    }

    @Test
    public void test_component_name_version_api_request_not_null(){
        String response = client.getOSVByNameVersionPost("com.google.guava:guava", "19.0");
        assertNotNull(response);
        assertNotEquals("{}", response);
    }

    @Test
    public void test_component_name_version_ecosystem_api_request_not_null(){
        String response = client.getOSVByNameVersionEcosystemPost("jinja2", "2.4.1", "PyPI");
        assertNotNull(response);
        assertNotEquals("{}", response);
    }

    @Test
    public void test_component_commit_api_request_not_null(){
        String response = client.getOSVByCommitPost("6879efc2c1596d11a6a6ad296f80063b558d5e0f");
        assertNotNull(response);
        assertNotEquals("{}", response);
    }

    @Test
    public void test_osv_id_api_request_not_null(){
        String response = client.getVulnByIdGet("OSV-2020-111");
        assertNotNull(response);
        assertNotEquals("{}", response);
    }

    @Test
    public void test_generateVEX_valid_VEX_object_CDX_json_sbom() throws IOException {
        Organization test_org1 = new Organization("com.google.guava", "google.com");

        Organization test_org2 = new Organization("The Pallets Projects", "google.com");

        CDX14ComponentObject test_component1 = new CDX14ComponentObject(
                null, null, "com.google.guava", "guava", null, null,
                null, test_org1, "19.0", null, null, null,
                null, null, null, null, null, null);
        CDX14ComponentObject test_component2 = new CDX14ComponentObject(
                null, null, null, "jinja2", null, null,
                null, test_org2, "2.4.1", null, null, null,
                null, null, null, null, null, null);
        Set<Component> test_components = new HashSet<>(List.of(
                test_component1,  test_component2)
        );

        CDX14SBOM test_sbom = new CDX14SBOM("CycloneDX 1.4", "test", null,
                "1.0", "1.4", null, null, null, null,
                test_components, null, null);

        VEX testVEX = client.generateVEX(test_sbom);

        ObjectMapper objectMapper = new ObjectMapper();
        // pretty print
        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(testVEX);
        System.out.println(json);
    }

}