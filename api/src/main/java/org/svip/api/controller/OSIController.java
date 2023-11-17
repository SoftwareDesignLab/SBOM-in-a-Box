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
import org.svip.api.services.SBOMFileService;
import org.svip.conversion.ConversionException;
import org.svip.generation.osi.OSI;
import org.svip.generation.osi.OSIClient;
import org.svip.generation.osi.exceptions.DockerNotAvailableException;
import org.svip.sbom.builder.SBOMBuilderException;
import org.svip.serializers.SerializerFactory;
import org.svip.serializers.exceptions.DeserializerException;
import org.svip.serializers.exceptions.SerializerException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    private final OSI container;

    /**
     * Create new Controller with services
     *
     * @param sbomService Service for handling SBOM queries
     */
    public OSIController(SBOMFileService sbomService){
        this.sbomService = sbomService;

        OSI container = null; // Disabled state
        String error = "OSI ENDPOINT DISABLED -- ";

        try {
            container = new OSI();
            LOGGER.info("OSI ENDPOINT ENABLED");
        } catch (Exception e) {
            // If we can't construct the OSI container for any reason, log and disable OSI.
            LOGGER.warn(error + "Unable to setup OSI container.");
            LOGGER.error("OSI Docker API response: " + e.getMessage());
        }

        this.container = container;
    }

    /**
     * Public method to check if OSI is enabled on this instance of the controller.
     *
     * @return True if OSI is enabled, false otherwise.
     */
    public static boolean isOSIEnabled() {
        try {
            return OSIClient.isOSIContainerAvailable();
        } catch(DockerNotAvailableException e) {
            return false;
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
        if (!isOSIEnabled())
            return new ResponseEntity<>("OSI has been disabled for this instance.", HttpStatus.NOT_FOUND);

        // No param default to all tools
        String listTypeArg = list.orElse("all");

        String urlMsg = "POST /svip/generators/osi?list=" + listTypeArg;

        List<String> tools = container.getTools(listTypeArg);
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
        if (!isOSIEnabled())
            return new ResponseEntity<>("OSI has been disabled for this instance.", HttpStatus.NOT_FOUND);

        try (ZipInputStream inputStream = new ZipInputStream(projectZip.getInputStream())) {
            this.container.addProject(inputStream);
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
                                         @RequestParam(value = "toolNames", required = false) String[] toolNames) {
        // TODO temp to keep behavior
        uploadProject(zipFile);

        // Generate SBOMs
        Map<String, String> generatedSBOMFiles;
        try {
            List<String> tools = null;
            if (toolNames != null && toolNames.length > 0) {
                tools = List.of(toolNames);
                LOGGER.info("POST /svip/generators/osi - Running with tool names: " + tools);
            } else LOGGER.info("POST /svip/generators/osi - Running with default tools");

            generatedSBOMFiles = container.generateSBOMs(tools);
        } catch (Exception e) {
            LOGGER.warn("POST /svip/generators/osi - Exception occurred while running OSI container: " + e.getMessage());
            return new ResponseEntity<>("Exception occurred while running OSI container.", HttpStatus.NOT_FOUND);
        }

        // Upload SBOMs
        List<Long> uploaded = new ArrayList<>();
        for (Map.Entry<String, String> sbomFile : generatedSBOMFiles.entrySet()) {
            UploadSBOMFileInput input = new UploadSBOMFileInput(sbomFile.getKey(), sbomFile.getValue());
            try {
                SBOMFile sbom = input.toSBOMFile();
                sbom.toSBOMObject();
                this.sbomService.upload(sbom);
                uploaded.add(sbom.getId());

                // Log
                LOGGER.info("POST /svip/generators/osi - Generated SBOM with ID " + sbom.getId() + ": " + sbom.getName());
            } catch (IllegalArgumentException ignored) {
                // TODO ignore any illegal files until XML deserialization exists
            } catch (Exception e) {
                // Problem with uploading/parsing
                LOGGER.error("POST /svip/generators/osi - " + e.getMessage());
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        if (uploaded.size() < 1) {
            LOGGER.warn("POST /svip/generators/osi - No SBOMs generated by OSI container.");
            return new ResponseEntity<>("No SBOMs generated for these files.", HttpStatus.NO_CONTENT);
        }

        // todo poor hotfix, need a better solution
        Long merged;
        // Only merge if 2 or more
        if(uploaded.size() >= 2){
            // Merge SBOMs into one SBOM
            try {
                merged = this.sbomService.merge(uploaded.toArray(new Long[0]));
            } catch (Exception e) {
                LOGGER.error("POST /svip/generators/osi - Unable to merge, no content: " + e.getMessage());
                return new ResponseEntity<>("Unable to merge, no content: " + e.getMessage(), HttpStatus.NO_CONTENT);
            }
        } else {
            merged = uploaded.get(0);
        }


        // Convert
        Long converted;
        try {
            converted = this.sbomService.convert(merged, schema, format, true);
        } catch (DeserializerException | JsonProcessingException | SerializerException | SBOMBuilderException |
                 ConversionException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // todo how to set file name using projectName

        // Save and return
        return new ResponseEntity<>(converted, HttpStatus.OK);
    }
}