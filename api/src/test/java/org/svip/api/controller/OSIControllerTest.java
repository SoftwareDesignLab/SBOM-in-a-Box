package org.svip.api.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.svip.api.services.SBOMFileService;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * File: OSIControllerTest.java
 * Description: OSI controller unit tests
 *
 * @author Ian Dunn
 */
@WebMvcTest(OSIController.class)
@DisplayName("OSI Controller Test")
public class OSIControllerTest {

    @MockBean
    private SBOMFileService sbomFileService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    static void setup() {
        assumeTrue(OSIController.isOSIEnabled()); // Only run tests if container API is accessible
    }

    @Test
    @DisplayName("/tools")
    void should_return_tool_list() throws Exception {
        mockMvc.perform(get("/svip/generators/osi/tools"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(11)));
    }
}
