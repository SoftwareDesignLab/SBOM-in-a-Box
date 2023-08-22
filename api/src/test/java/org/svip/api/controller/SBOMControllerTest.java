package org.svip.api.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.svip.api.entities.SBOM;
import org.svip.api.requests.UploadSBOMFileInput;
import org.svip.api.services.SBOMFileService;
import org.svip.serializers.SerializerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * File: SBOMControllerTest.java
 * Description: Unit test for SBOM Controller
 *
 * @author Derek Garcia
 * @author Thomas Roman
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SBOM Controller Test")
public class SBOMControllerTest {

    @Mock
    private SBOMFileService sbomFileService;      // Mock service

    @InjectMocks
    private SBOMController sbomController;    // Instance of controller for testing

    // Test SBOMs
    private static final String CDX_JSON_SBOM_FILE = "./src/test/resources/sample_sboms/cdx-gomod-1.4.0-bin.json";
    private static final String SPDX_JSON_SBOM_FILE = "./src/test/resources/sample_sboms/syft-0.80.0-source-spdx-json.json";
    private static final String SPDX_TAG_VALUE_SBOM_FILE = "./src/test/resources/sample_sboms/sbom.alpine-compare.2-3.spdx";


    ///
    /// Upload
    ///
    @Test
    @DisplayName("Upload valid SBOM")
    void upload_valid_sbom() throws Exception {
        // Given
        UploadSBOMFileInput input = new UploadSBOMFileInput("CDX14_JSON", fileToContents(CDX_JSON_SBOM_FILE));
        SBOM sbom = input.toSBOMFile();
        // When
        when(this.sbomFileService.upload(any(SBOM.class))).thenAnswer(i -> i.getArgument(0));   // echo
        ResponseEntity<Long> response =  this.sbomController.upload(input);
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode() );
        assertEquals(sbom.getId(), response.getBody());
    }

    @Test
    @DisplayName("Upload invalid SBOM")
    void upload_invalid_sbom(){
        // Given
        UploadSBOMFileInput input = new UploadSBOMFileInput("CDX14_JSON", "SBOM CONTENTS");
        // When
        ResponseEntity<Long> response = this.sbomController.upload(input);
        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    ///
    /// Merge
    ///
    @Test
    @DisplayName("Merge 2 SBOMs")
    void merge_two_sboms() throws Exception {
        // Given
        Long[] ids = new Long[2];
        ids[0] = 0L;
        ids[1] = 1L;
        // When
        // TODO different types of exceptions?
        when(this.sbomFileService.merge(ids)).thenThrow(Exception.class);

        ResponseEntity<Long> response =  this.sbomController.merge(ids);
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2L, response.getBody());
    }

    @Test
    @DisplayName("Merge 1 SBOM")
    void merge_one_sbom() throws Exception {
        // Given
        Long[] ids = new Long[1];
        ids[0] = 0L;

        // When
        // TODO different types of exceptions?
        when(this.sbomFileService.merge(ids)).thenThrow(Exception.class);
        ResponseEntity<Long> response = this.sbomController.merge(ids);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Fail to parse merge sbom")
    void fail_to_parse_merge_sbom() throws Exception {
        // Given
        Long[] ids = new Long[2];
        ids[0] = 0L;
        ids[1] = 1L;

        // When
        // TODO different types of exceptions?
        when(this.sbomFileService.merge(ids)).thenThrow(Exception.class);
        ResponseEntity<Long> response = this.sbomController.merge(ids);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @DisplayName("missing merge sbom")
    void missing_merge_sbom() throws Exception {
        // Given
        Long[] ids = new Long[2];
        ids[0] = 0L;
        ids[1] = 1L;

        // When
        // TODO different types of exceptions?
        when(this.sbomFileService.merge(ids)).thenThrow(Exception.class);
        ResponseEntity<Long> response = this.sbomController.merge(ids);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Convert sbom")
    void convert_sbom() throws Exception {
        // Given
        Long id = 1L;
        SerializerFactory.Schema schema = SerializerFactory.Schema.CDX14;
        SerializerFactory.Format format = SerializerFactory.Format.JSON;
        Boolean overwrite = true;
        Long convertedID = 2L;

        // When
        when(sbomFileService.convert(id, schema, format, overwrite)).thenReturn(convertedID);
        ResponseEntity<?> response = sbomController.convert(id, schema, format, overwrite);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(convertedID, response.getBody());
        verify(sbomFileService).convert(id, schema, format, overwrite);
    }

    @Test
    @DisplayName("Get SBOM Object as JSON")
    void get_SBOM_object_as_JSON() throws Exception {
        // Given
        Long id = 1L;
        String jsonContent = "{\"format\":\"CycloneDX\",\"uid\":\"urn:uuid:c7c5960d-02ce-450e-8854-9a93dec23884\",\"version\":\"1\",\"specVersion\":\"1.4\",\"creationData\":{\"creationTime\":\"2023-05-10T15:28:43-04:00\",\"properties\":{\"cdx:gomod:binary:name\":[\"main.exe\"],\"cdx:gomod:build:env:GOARCH\":[\"amd64\"],\"cdx:gomod:build:env:GOOS\":[\"windows\"],\"cdx:gomod:binary:hash:MD5\":[\"9f4e58091cc6f5d091135b405f3009f7\"],\"cdx:gomod:build:env:GOVERSION\":[\"go1.20.4\"],\"cdx:gomod:build:compiler\":[\"gc\"],\"cdx:gomod:build:env:CGO_ENABLED\":[\"0\"],\"cdx:gomod:binary:hash:SHA-384\":[\"74d0bdbc65693f04a384625e59aa66e9116b75f6d1db6c6e9d16d7e617349084917fd661dd4ec75a787379785ddcce23\"],\"cdx:gomod:binary:hash:SHA-1\":[\"76849b6376c5892c98b1ac95471ff3c1238cf6e2\"],\"cdx:gomod:binary:hash:SHA-256\":[\"4e6aea3eac75a3a0a3c7b616e0250de48a4d4e384dec2a5c7063b6924a3f4061\"],\"cdx:gomod:binary:hash:SHA-512\":[\"9e60800b726795ae0286547780f46fc959a5355e9a491ae260f1bbcd82e71bb4bc47d3f29c0accc961ac9bd10df780d8f5eebbbd3ca040b1dcd99b7f573f2368\"]},\"creationTools\":[{\"vendor\":\"CycloneDX\",\"name\":\"cyclonedx-gomod\",\"version\":\"v1.4.0\",\"hashes\":{\"SHA-1\":\"e2f6a39c3f17dc8ff945a47f082837055e57560d\",\"SHA-384\":\"4e5125bee6348f1e5dd7deb4a286366562717c9888ba2263cbd122d1df46f61fde3fa8c6ba818bf3603b53426c343d2e\",\"SHA-256\":\"d20b0eeaebca24a0eb27c01b8395b5dab89a0538394155cce3d47504cca173fb\",\"SHA-512\":\"2c566675699111ca5821dbbbb828da2da61307a6c72cc4f28b2af57df67285f2405065ff3e26da78bd7623091d345e5cbde40e89f275949034c201034b89ae5f\",\"MD5\":\"169a92227d363c0d4f92be133c5c233b\"}}]},\"rootComponent\":{\"type\":\"application\",\"uid\":\"pkg:golang/example.com/main?type=module\",\"name\":\"example.com/main\",\"licenses\":{},\"purls\":[\"pkg:golang/example.com/main?type=module&goos=windows&goarch=amd64\"]},\"components\":[{\"type\":\"library\",\"uid\":\"pkg:golang/golang.org/x/text@v0.0.0-20170915032832-14c0d48ead0c?type=module\",\"name\":\"golang.org/x/text\",\"licenses\":{},\"hashes\":{\"SHA-256\":\"aa0398e9681939a4e4208322563050730f77111044e26df48819b4d2790bd22f\"},\"version\":\"v0.0.0-20170915032832-14c0d48ead0c\",\"purls\":[\"pkg:golang/golang.org/x/text@v0.0.0-20170915032832-14c0d48ead0c?type=module&goos=windows&goarch=amd64\"],\"scope\":\"required\"},{\"type\":\"library\",\"uid\":\"pkg:golang/rsc.io/quote@v1.5.2?type=module\",\"name\":\"rsc.io/quote\",\"licenses\":{},\"hashes\":{\"SHA-256\":\"c397dccac8ebc7bcaab43fda3be43046361938a9da33d521d9be34b44953b376\"},\"version\":\"v1.5.2\",\"purls\":[\"pkg:golang/rsc.io/quote@v1.5.2?type=module&goos=windows&goarch=amd64\"],\"scope\":\"required\"},{\"type\":\"library\",\"uid\":\"pkg:golang/rsc.io/sampler@v1.3.0?type=module\",\"name\":\"rsc.io/sampler\",\"licenses\":{},\"hashes\":{\"SHA-256\":\"eee56420599e06a1df7630fe819c2d5d723e44e0c9d967383bb30f121fd0896e\"},\"version\":\"v1.3.0\",\"purls\":[\"pkg:golang/rsc.io/sampler@v1.3.0?type=module&goos=windows&goarch=amd64\"],\"scope\":\"required\"},{\"type\":\"library\",\"uid\":\"pkg:golang/std@go1.20.4?type=module\",\"name\":\"std\",\"licenses\":{},\"version\":\"go1.20.4\",\"purls\":[\"pkg:golang/std@go1.20.4?type=module&goos=windows&goarch=amd64\"],\"scope\":\"required\"},{\"type\":\"library\",\"uid\":\"pkg:golang/github.com/markcheno/go-quote@v0.0.0-20220624214117-555891babbf1?type=module\",\"name\":\"github.com/markcheno/go-quote\",\"licenses\":{},\"hashes\":{\"SHA-256\":\"49f482fb6c7c07bd60e4ff3ff855684a2fe3cdf4bd65a3b8d3a2e5511327443d\"},\"version\":\"v0.0.0-20220624214117-555891babbf1\",\"purls\":[\"pkg:golang/github.com/markcheno/go-quote@v0.0.0-20220624214117-555891babbf1?type=module&goos=windows&goarch=amd64\"],\"scope\":\"required\",\"externalReferences\":[{\"url\":\"https://github.com/markcheno/go-quote\",\"type\":\"vcs\"}]}],\"relationships\":{\"pkg:golang/example.com/main?type=module\":[{\"otherUID\":\"pkg:golang/rsc.io/sampler@v1.3.0?type=module\",\"relationshipType\":\"DEPENDS_ON\"},{\"otherUID\":\"pkg:golang/github.com/markcheno/go-quote@v0.0.0-20220624214117-555891babbf1?type=module\",\"relationshipType\":\"DEPENDS_ON\"},{\"otherUID\":\"pkg:golang/golang.org/x/text@v0.0.0-20170915032832-14c0d48ead0c?type=module\",\"relationshipType\":\"DEPENDS_ON\"},{\"otherUID\":\"pkg:golang/rsc.io/quote@v1.5.2?type=module\",\"relationshipType\":\"DEPENDS_ON\"}]}}";
        SBOM sbom = buildMockSBOMFile(CDX_JSON_SBOM_FILE);

        // When
        when(sbomFileService.getSBOMFile(id)).thenReturn(sbom);
        ResponseEntity<String> response = sbomController.getSBOMObjectAsJSON(id);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(jsonContent, response.getBody());
        verify(sbomFileService).getSBOMFile(id);
    }

    @Test
    @DisplayName("Get SBOM Object as JSON no content")
    void get_SBOM_object_as_JSON_no_content() throws Exception {
        // Given
        Long id = 1L;

        // When
        when(sbomFileService.getSBOMFile(id)).thenReturn(null);
        ResponseEntity<String> response = sbomController.getSBOMObjectAsJSON(id);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Get SBOM content test")
    void get_SBOM_content() throws Exception {
        // Given
        Long id = 1L;
        SBOM sbom = buildMockSBOMFile(CDX_JSON_SBOM_FILE);

        // When
        when(sbomFileService.getSBOMFile(id)).thenReturn(sbom);
        ResponseEntity<SBOM> response = sbomController.getContent(id);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("./src/test/resources/sample_sboms/cdx-gomod-1.4.0-bin.json", response.getBody().getName());
        verify(sbomFileService).getSBOMFile(id);
    }

    @Test
    @DisplayName("Get SBOM no content test")
    void get_SBOM_no_content() throws Exception {
        // Given
        Long id = 1L;

        // When
        when(sbomFileService.getSBOMFile(id)).thenReturn(null);
        ResponseEntity<SBOM> response = sbomController.getContent(id);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("get all ids")
    public void get_all_ids() {
        // Given
        Long[] ids = {1L, 2L, 3L}; // Set the expected array of IDs

        // When
        when(sbomFileService.getAllIDs()).thenReturn(ids);
        ResponseEntity<Long[]> response = sbomController.getAllIds();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertArrayEquals(ids, response.getBody());
        verify(sbomFileService).getAllIDs();
    }

    @Test
    @DisplayName("get all ids no content")
    public void get_all_ids_no_content() {
        // Given
        Long[] emptyIds = {};

        // When
        when(sbomFileService.getAllIDs()).thenReturn(emptyIds);
        ResponseEntity<Long[]> response = sbomController.getAllIds();

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(sbomFileService).getAllIDs();
    }

    @Test
    @DisplayName("Delete SBOM")
    public void delete_SBOM() throws Exception {
        // Given
        Long id = 1L;
        SBOM sbom = buildMockSBOMFile(CDX_JSON_SBOM_FILE);

        // When
        when(sbomFileService.getSBOMFile(id)).thenReturn(sbom);
        ResponseEntity<Long> response = sbomController.delete(id);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(id, response.getBody());
        verify(sbomFileService).getSBOMFile(id);
        verify(sbomFileService).deleteSBOMFile(sbom);
    }

    @Test
    @DisplayName("Delete SBOM no content")
    public void delete_SBOM_no_content() throws Exception {
        // Given
        Long id = 1L;

        // When
        when(sbomFileService.getSBOMFile(id)).thenReturn(null);
        ResponseEntity<Long> response = sbomController.delete(id);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    ///
    /// Helper Methods
    ///
    private String fileToContents(String filepath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filepath)));
    }

    /**
     * Generate SBOM File from a filepath
     *
     * @param filepath path of the sbom
     * @return Valid Mock SBOM file
     * @throws IOException failed to open file
     */
    private SBOM buildMockSBOMFile(String filepath) throws IOException {
        // Get file contents
        String content = new String(Files.readAllBytes(Paths.get(filepath)));
        // Create SBOM
        return new UploadSBOMFileInput(filepath, content).toSBOMFile();
    }


}
