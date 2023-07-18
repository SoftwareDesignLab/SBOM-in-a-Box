package org.svip.sbomvex.database;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OSVClientTest {

    OSVClient client = new OSVClient();

    @Test
    public void test_purl_api_request_not_null(){
        String response = client.getOSVByPURLPost("pkg:maven/com.google.guava/guava@19.0");
        System.out.println(response);
        assertNotNull(response);
    }

    @Test
    public void test_component_name_version_api_request_not_null(){
        String response = client.getOSVByNameVersionPost("com.google.guava:guava", "19.0");
        assertNotNull(response);
    }

    @Test
    public void test_component_name_version_ecosystem_api_request_not_null(){
        String response = client.getOSVByNameVersionEcosystemPost("jinja2", "2.4.1", "PyPI");
        assertNotNull(response);
    }

    @Test
    public void test_component_commit_api_request_not_null(){
        String response = client.getOSVByCommitPost("6879efc2c1596d11a6a6ad296f80063b558d5e0f");
        assertNotNull(response);
    }

    @Test
    public void test_osv_id_api_request_not_null(){
        String response = client.getVulnByIdGet("OSV-2020-111");
        assertNotNull(response);
    }

}