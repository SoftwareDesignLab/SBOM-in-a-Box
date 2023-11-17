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
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.ZipInputStream;

/**
 * REST API Controller for generating SBOMs with OSI
 *
 * @author Derek Garcia
 **/
@RestController
@RequestMapping("/svip/generators/osi")
public class OSIController {
    /**
     * Spring-configured logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(OSIController.class);

    private final SBOMFileService sbomService;
    private OSIService osiService = null;

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
        if (!this.osiService.isEnabled())
            return new ResponseEntity<>("OSI has been disabled for this instance.", HttpStatus.NOT_FOUND);

        // No param default to all tools
        String listTypeArg = list.orElse("all");

        String urlMsg = "POST /svip/generators/osi?list=" + listTypeArg;

        List<String> tools = this.osiService.getTools(listTypeArg);
        if (tools == null) {
            LOGGER.error(urlMsg + ": " + "Error getting tool list from Docker container.");
            return new ResponseEntity<>("Error getting tool list from Docker container.", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(tools.toArray(new String[0]), HttpStatus.OK);
    }

    ///
    /// POST
    ///

    @PostMapping(value = "/project", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> uploadProject(@RequestPart("projectZip") MultipartFile projectZip){
        if (!this.osiService.isEnabled())
            return new ResponseEntity<>("OSI has been disabled for this instance.", HttpStatus.NOT_FOUND);

        try (ZipInputStream inputStream = new ZipInputStream(projectZip.getInputStream())) {
            this.osiService.addProject(inputStream);
        } catch (IOException e) {
            LOGGER.error("POST /svip/generators/osi/project - " + e.getMessage());
            return new ResponseEntity<>("Make sure attachment is a zip file (.zip): " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }


    /**
     * USAGE. Send POST request to /generators/osi to generate an SBOM from source file(s).
     *
     * @param zipFile The zip file of source files to generate an SBOM from.
     * @param projectName The name of the project.
     * @param schema The schema of the desired SBOM.
     * @param format The file format of the desired SBOM.
     * @param toolNames An optional list of tool names to use when running OSI. If not provided or empty, all
     *                  possible tools will be used.
     * @return The ID of the uploaded SBOM.
     */
    @PostMapping(value = "", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> generateOSI(@RequestPart("zipFile") MultipartFile zipFile,
                                         @RequestParam("projectName") String projectName,
                                         @RequestParam("schema") SerializerFactory.Schema schema,
                                         @RequestParam("format") SerializerFactory.Format format,
                                         @RequestParam(value = "toolNames", required = false) String[] toolNames) throws IOException {
        // TODO temp to keep behavior
//        uploadProject(zipFile);

        // Generate SBOMs
        List<String> generatedSBOMFilePaths;
        try {
            List<String> tools = null;
            if (toolNames != null && toolNames.length > 0) {
                tools = List.of(toolNames);
                LOGGER.info("POST /svip/generators/osi - Running with tool names: " + tools);
            } else LOGGER.info("POST /svip/generators/osi - Running with default tools");

            generatedSBOMFilePaths = this.osiService.generateSBOMs(null);
        } catch (Exception e) {
            LOGGER.warn("POST /svip/generators/osi - Exception occurred while running OSI container: " + e.getMessage());
            return new ResponseEntity<>("Exception occurred while running OSI container.", HttpStatus.NOT_FOUND);
        }

        // Upload SBOMs
        List<SBOMFile> uploaded = new ArrayList<>();
        for (String path: generatedSBOMFilePaths) {

            try {
                UploadSBOMFileInput input =
                        new UploadSBOMFileInput(path, Files.readString(Path.of(path), StandardCharsets.UTF_8));
                SBOMFile sbomFile = input.toSBOMFile();
                this.sbomService.upload(sbomFile);
                uploaded.add(sbomFile);

                // Log
                LOGGER.info("POST /svip/generators/osi - Generated SBOM with ID " + sbomFile.getId() + ": " + sbomFile.getName());
            } catch (IllegalArgumentException e) {
                LOGGER.error("POST /svip/generators/osi - Failed to parse " + path + " : " + e.getMessage() );
            } catch (Exception e) {
                // Problem with uploading/parsing
                LOGGER.error("POST /svip/generators/osi - " + e.getMessage());
            }
        }

        if (uploaded.isEmpty()) {
            LOGGER.warn("POST /svip/generators/osi - No SBOMs generated by OSI container.");
            return new ResponseEntity<>("No SBOMs generated for these files.", HttpStatus.NO_CONTENT);
        }


        Long mergedID;
        // Only merge if 2 or more
        if(uploaded.size() >= 2){
            // Merge SBOMs into one SBOM
            try {

                List<Long> uploadedIDs = uploaded.stream().map(SBOMFile::getId).toList();   // get all sbom ids into list
                mergedID = this.sbomService.merge(uploadedIDs.toArray(new Long[0]));

            } catch (Exception e) {
                LOGGER.error("POST /svip/generators/osi - Unable to merge, no content: " + e.getMessage());
                return new ResponseEntity<>("Unable to merge, no content: " + e.getMessage(), HttpStatus.NO_CONTENT);
            } finally {
                // todo param to delete or not?
                for(SBOMFile sbomFile : uploaded)
                    this.sbomService.deleteSBOMFile(sbomFile);
            }
        } else {
            mergedID = uploaded.get(0).getId();
        }


        // Convert
        Long converted;
        try {
            converted = this.sbomService.convert(mergedID, schema, format, true);
        } catch (DeserializerException | JsonProcessingException | SerializerException | SBOMBuilderException |
                 ConversionException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // todo how to set file name using projectName

        // Save and return
        return new ResponseEntity<>(converted, HttpStatus.OK);
    }

}