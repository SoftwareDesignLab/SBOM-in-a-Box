package org.svip.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.svip.api.entities.SBOMFile;
import org.svip.api.requests.UploadSBOMFileInput;
import org.svip.api.services.OSIService;
import org.svip.api.services.SBOMFileService;
import org.svip.conversion.ConversionException;
import org.svip.sbom.builder.SBOMBuilderException;
import org.svip.serializers.SerializerFactory;
import org.svip.serializers.exceptions.DeserializerException;
import org.svip.serializers.exceptions.SerializerException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipInputStream;

/**
 * File: OSIController.java
 * REST API Controller for generating SBOMs with OSI
 *
 * @author Derek Garcia
 * @author Ian Dunn
 **/
@RestController
@RequestMapping("/svip/generators/osi")
public class OSIController {
    /**
     * Spring-configured logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(OSIController.class);

    // Services
    private final SBOMFileService sbomService;
    private final OSIService osiService;

    /**
     * Create new Controller with services
     *
     * @param sbomService Service for handling SBOM queries
     */
    public OSIController(SBOMFileService sbomService){
        this.sbomService = sbomService;
        this.osiService = new OSIService();

        if(this.osiService.isEnabled()){
            LOGGER.info("OSI ENDPOINT ENABLED");
        } else {
            LOGGER.warn("OSI ENDPOINT DISABLED -- Unable to communicate with OSI container; Is the container running?");
        }
    }

    ///
    /// GET
    ///

    /**
     * USAGE. Send GET request to /generators/osi/tools to get a list of valid tool names that can be used to
     * generate an SBOM from source file(s).
     *
     * @param list Optional argument, either "all" (default) or "project",
     *             all gets all tools installed in OSI
     *             project gets all applicable tools installed for the project in the bound directory
     * @return A list of string tool names.
     */
    @GetMapping("/tools")
    public ResponseEntity<?> getOSITools(@RequestParam("list") Optional<String> list) {
        // Check if OSI is running
        if (!this.osiService.isEnabled())
            return new ResponseEntity<>("OSI has been disabled for this instance.", HttpStatus.NOT_FOUND);

        // No param default to all tools
        String listTypeArg = list.orElse("all");

        // Get tools
        List<String> tools = this.osiService.getTools(listTypeArg);
        if (tools == null) {
            LOGGER.error("POST /svip/generators/osi?list=" + listTypeArg + ": " + "Error getting tool list from Docker container.");
            return new ResponseEntity<>("Error getting tool list from Docker container.", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(tools.toArray(new String[0]), HttpStatus.OK);
    }


    ///
    /// POST
    ///


    /**
     * USAGE. Send POST request to /generators/osi/project
     * Upload project to be run OSI against
     *
     * @param project Zip File of project
     * @return List of applicable tools for the project
     */
    @PostMapping(value = "/project", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> uploadProject(@RequestPart("project") MultipartFile project){
        // Check if OSI is running
        if (!this.osiService.isEnabled())
            return new ResponseEntity<>("OSI has been disabled for this instance.", HttpStatus.NOT_FOUND);

        // Open zip
        try (ZipInputStream inputStream = new ZipInputStream(project.getInputStream())) {
            this.osiService.addProject(inputStream);        // Upload Project
            List<String> tools = this.osiService.getTools("project");   // get applicable tools
            return new ResponseEntity<>(tools, HttpStatus.OK);
        } catch (IOException e) {
            LOGGER.error("POST /svip/generators/osi/project - " + e.getMessage());
            return new ResponseEntity<>("Make sure attachment is a zip file (.zip): " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    /**
     * USAGE. Send POST request to /generators/osi to generate an SBOM from source file(s).
     *
     * @param projectName The name of the project.
     * @param schema The schema of the desired SBOM.
     * @param format The file format of the desired SBOM.
     * @param toolNames An optional list of tool names to use when running OSI. If not provided or empty, all
     *                  possible tools will be used.
     * @return The ID of the uploaded SBOM.
     */
    @PostMapping(value = "")
    public ResponseEntity<?> generateWithOSI(@RequestParam("projectName") String projectName,
                                             @RequestParam("schema") SerializerFactory.Schema schema,
                                             @RequestParam("format") SerializerFactory.Format format,
                                             @RequestParam(value = "toolNames", required = false) String[] toolNames) throws IOException {


        List<String> generatedSBOMFilePaths;
        try {
            // Run with requested tools, default to relevant ones
            List<String> tools;
            if (toolNames != null && toolNames.length > 0) {
                tools = List.of(toolNames);
            } else {
                tools = this.osiService.getTools("project");
            }

            // Generate SBOMs in the bound SBOM Directory
            LOGGER.info("POST /svip/generators/osi - Running with tool names: " + tools);
            generatedSBOMFilePaths = this.osiService.generateSBOMs(tools);
        } catch (Exception e) {
            LOGGER.warn("POST /svip/generators/osi - Exception occurred while running OSI container: " + e.getMessage());
            return new ResponseEntity<>("Exception occurred while running OSI container.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // No SBOMs generated
        if(generatedSBOMFilePaths.isEmpty())
            return new ResponseEntity<>("No SBOMs were generated", HttpStatus.NO_CONTENT);

        // Upload SBOMs to SB
        List<SBOMFile> uploaded = new ArrayList<>();
        for (String path: generatedSBOMFilePaths) {
            // Try to upload new SBOM to DB
            try {
                UploadSBOMFileInput input =
                        new UploadSBOMFileInput(path, Files.readString(Path.of(path), StandardCharsets.UTF_8));
                SBOMFile sbomFile = input.toSBOMFile();
                this.sbomService.upload(sbomFile);
                uploaded.add(sbomFile);

                LOGGER.info("POST /svip/generators/osi - Generated SBOM with ID " + sbomFile.getId() + ": " + sbomFile.getName());
            } catch (IllegalArgumentException e) {
                // Parsing error / unsupported format
                LOGGER.error("POST /svip/generators/osi - Failed to parse " + path + " : " + e.getMessage() );
            } catch (Exception e) {
                // Problem with uploading/parsing
                LOGGER.error("POST /svip/generators/osi - " + e.getMessage());
            }
        }

        // All SBOMs failed to parse
        if (uploaded.isEmpty()) {
            LOGGER.warn("POST /svip/generators/osi - No SBOMs generated by OSI container.");
            return new ResponseEntity<>("No SBOMs generated for these files.", HttpStatus.NO_CONTENT);
        }

        LOGGER.info("POST /svip/generators/osi - Parsed " + uploaded.size() + " SBOMs successfully" );

        // Merge SBOMs
        Long mergedID;
        if(uploaded.size() >= 2){
            LOGGER.info("POST /svip/generators/osi - Beginning Merging");
            try {
                // Merge SBOMs into one SBOM
                List<Long> uploadedIDs = uploaded.stream().map(SBOMFile::getId).toList();   // get all sbom ids into list
                mergedID = this.sbomService.merge(uploadedIDs.toArray(new Long[0]));

            } catch (Exception e) {
                // Failed to merge
                LOGGER.error("POST /svip/generators/osi - Unable to merge, no content: " + e.getMessage());
                return new ResponseEntity<>("Unable to merge, no content: " + e.getMessage(), HttpStatus.NO_CONTENT);
            } finally {
                // todo param to delete or not?
                // Delete any temp SBOM from database
                for(SBOMFile sbomFile : uploaded)
                    this.sbomService.deleteSBOMFile(sbomFile);
            }
            LOGGER.info("POST /svip/generators/osi - Successfully merged SBOMs to SBOM with id " + mergedID);
        } else {
            // Only 1 SBOM generated, no need to merge
            LOGGER.info("POST /svip/generators/osi - Only 1 SBOM uploaded, skipping merging");
            mergedID = uploaded.get(0).getId();
        }

        // Convert
        Long convertedID;
        try {
            LOGGER.info("POST /svip/generators/osi - Converting SBOM to " + schema + " " + format);
            convertedID = this.sbomService.convert(mergedID, schema, format, true);
            LOGGER.info("POST /svip/generators/osi - Successfully merged SBOMs to SBOM with id " + convertedID);
        } catch (DeserializerException | JsonProcessingException | SerializerException | SBOMBuilderException |
                 ConversionException e) {
            // Failed to convert
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // todo how to set file name using projectName

        // Return ID
        return new ResponseEntity<>(convertedID, HttpStatus.OK);
    }

}