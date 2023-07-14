package org.svip.api.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Derek Garcia
 **/

public class GetSBOMAPITest extends APITest{

    private final static int CDX14_JSON_INDEX = 6;
    private final static int CDX14_XML_INDEX = 4;
    private final static int SPDX23_TAGVALUE_INDEX = 0;
    private final static int SPDX23_JSON_INDEX = 9;

    @Test
    @DisplayName("Parse Valid CDX14 JSON")
    public void get_valid_CDX_14_SBOM_JSON() throws IOException {
        var foo = getTestFileMap();
        //gradleSBOM
    }

    @Test
    @DisplayName("Parse Valid SPDX23 Tag Value")
    public void get_valid_SPDX_23_SBOM_TAGVALUE(){
        //dockerspdx
    }

    @Test
    @DisplayName("Parse Valid SPDX23 JSON")
    public void get_valid_SPDX23_SBOM_JSON(){

    }

    @Test
    @DisplayName("Parse Unsupported SBOM")
    public void get_unsupported_SBOM_format(){
        // alpine cdx
    }

    @Test
    @DisplayName("Request SBOM with invalid ID value")
    public void get_invalid_ID_SBOM(){

    }

    @Test
    @DisplayName("Request SBOM with valid, but missing ID")
    public void get_missing_ID_SBOM(){

    }

}
