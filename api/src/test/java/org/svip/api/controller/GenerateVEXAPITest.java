package org.svip.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.svip.api.model.SBOMFile;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class GenerateVEXAPITest extends APITest{

    private final static long CDX14_JSON_ID = 6;
    private final static long VULNERABLE_SBOM_NVD_ID = 10;
    private final static long VULNERABLE_SBOM_OSV_ID = 11;

    private static Map<Long, SBOMFile> fileMap;

    @BeforeAll
    static void setupFileMap(){
        try{
            fileMap = getTestFileMap();
        } catch (Exception e){
            fail(e);
        }
    }

    @Test
    @DisplayName("Invalid SBOM Test")
    public void test_invalid_sbom_id_test() throws IOException {
        assertEquals(HttpStatus.NOT_FOUND, controller.vex(
                CDX14_JSON_ID, "CSAF", "OSV"
        ).getStatusCode());
    }


    @Test
    @DisplayName("Invalid Format Test")
    public void test_invalid_vex_format_test() throws IOException {

        // Get CDX14 JSON SBOM when requested
        when(repository.findById(CDX14_JSON_ID)).thenAnswer(i -> Optional.of(fileMap.get(CDX14_JSON_ID)));

        assertEquals(HttpStatus.BAD_REQUEST, controller.vex(
                CDX14_JSON_ID, "NotARealFormat", "OSV"
        ).getStatusCode());
    }

    @Test
    @DisplayName("Invalid API Database Test")
    public void test_invalid_api_database_test() throws IOException {

        // Get CDX14 JSON SBOM when requested
        when(repository.findById(CDX14_JSON_ID)).thenAnswer(i -> Optional.of(fileMap.get(CDX14_JSON_ID)));

        assertEquals(HttpStatus.BAD_REQUEST, controller.vex(
                CDX14_JSON_ID, "CSAF", "NotARealClient"
        ).getStatusCode());
    }

    @Test
    @DisplayName("Generate VEX Test CSAF Format NVD API")
    public void test_generate_valid_vex_csaf_nvd_test() throws IOException{
        // Get CDX14 JSON SBOM when requested
        when(repository.findById(VULNERABLE_SBOM_NVD_ID)).thenAnswer(i ->
                Optional.of(fileMap.get(VULNERABLE_SBOM_NVD_ID)));


        ResponseEntity<?> response = controller.vex(VULNERABLE_SBOM_NVD_ID,
                "CSAF", "NVD");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        ObjectMapper objectMapper = new ObjectMapper();
        // pretty print
        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response);
        System.out.println(json);
    }

    @Test
    @DisplayName("Generate VEX Test CDX Format NVD API")
    public void test_generate_valid_vex_cdx_nvd_test() throws IOException{
        // Get CDX14 JSON SBOM when requested
        when(repository.findById(VULNERABLE_SBOM_NVD_ID)).thenAnswer(i ->
                Optional.of(fileMap.get(VULNERABLE_SBOM_NVD_ID)));


        ResponseEntity<?> response = controller.vex(VULNERABLE_SBOM_NVD_ID,
                "CycloneDX", "NVD");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        ObjectMapper objectMapper = new ObjectMapper();
        // pretty print
        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response);
        System.out.println(json);
    }

    @Test
    @DisplayName("Generate VEX Test CDX Format OSV API")
    public void test_generate_valid_vex_cdx_osv_test() throws IOException{
        // Get CDX14 JSON SBOM when requested
        when(repository.findById(VULNERABLE_SBOM_OSV_ID)).thenAnswer(i ->
                Optional.of(fileMap.get(VULNERABLE_SBOM_OSV_ID)));


        ResponseEntity<?> response = controller.vex(VULNERABLE_SBOM_OSV_ID,
                "CycloneDX", "OSV");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        ObjectMapper objectMapper = new ObjectMapper();
        // pretty print
        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response);
        System.out.println(json);
    }

    @Test
    @DisplayName("Generate VEX Test CSAF Format OSV API")
    public void test_generate_valid_vex_csaf_osv_test() throws IOException{
        // Get CDX14 JSON SBOM when requested
        when(repository.findById(VULNERABLE_SBOM_OSV_ID)).thenAnswer(i ->
                Optional.of(fileMap.get(VULNERABLE_SBOM_OSV_ID)));


        ResponseEntity<?> response = controller.vex(VULNERABLE_SBOM_OSV_ID,
                "CSAF", "OSV");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        ObjectMapper objectMapper = new ObjectMapper();
        // pretty print
        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response);
        System.out.println(json);
    }

}
