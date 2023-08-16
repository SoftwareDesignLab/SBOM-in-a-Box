package org.svip.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.svip.api.entities.SBOM;
import org.svip.api.entities.SBOMFile;
import org.svip.api.requests.UploadSBOMFileInput;
import org.svip.api.services.SBOMFileService;
import org.svip.conversion.ConversionException;
import org.svip.generation.osi.OSI;
import org.svip.sbom.builder.SBOMBuilderException;
import org.svip.serializers.SerializerFactory;
import org.svip.serializers.exceptions.DeserializerException;
import org.svip.serializers.exceptions.SerializerException;

import java.io.IOException;
import java.util.*;

/**
 * REST API Controller for generating SBOMs with OSI
 *
 * @author Derek Garcia
 **/
@RestController
@RequestMapping("/svip/generators")
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
    public boolean isOSIEnabled() {
        return container != null;
    }

    /**
     * USAGE. Send GET request to /generators/osi/getTools to get a list of valid tool names that can be used to
     * generate an SBOM from source file(s).
     *
     * @return A list of string tool names.
     */
    @GetMapping("/generators/osi/tools")
    public ResponseEntity<?> getOSITools() {
        if (!isOSIEnabled())
            return new ResponseEntity<>("OSI has been disabled for this instance.", HttpStatus.NOT_FOUND);

        String urlMsg = "POST /svip/generators/osi";

        List<String> tools = container.getAllTools();
        if (tools == null) {
            LOGGER.error(urlMsg + ": " + "Error getting tool list from Docker container.");
            return new ResponseEntity<>("Error getting tool list from Docker container.", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(tools, HttpStatus.OK);
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
    @PostMapping("/generators/osi")
    public ResponseEntity<?> generateOSI(@RequestParam("zipFile") MultipartFile zipFile,
                                         @RequestParam("projectName") String projectName,
                                         @RequestParam("schema") SerializerFactory.Schema schema,
                                         @RequestParam("format") SerializerFactory.Format format,
                                         @RequestBody String[] toolNames) {
        if (!isOSIEnabled())
            return new ResponseEntity<>("OSI has been disabled for this instance.", HttpStatus.NOT_FOUND);

        ArrayList<HashMap<SBOMFile, Integer>> unZipped;
        try {
            unZipped = (ArrayList<HashMap<SBOMFile, Integer>>)
                    SBOMFileService.unZip(Objects.requireNonNull(SBOMFileService.convertMultipartToZip(zipFile)));
        } catch (IOException e) {
            LOGGER.error("POST /svip/generators/osi - " + e.getMessage());
            return new ResponseEntity<>("Make sure attachment is a zip file (.zip): " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        // Validate & add files
        for (HashMap<SBOMFile, Integer> h : unZipped) {

            SBOMFile srcFile = (SBOMFile) h.keySet().toArray()[0];

            if (!srcFile.hasNullProperties()) // project files that are empty should just be ignored
                try {
                    // Remove any directories, causes issues with OSI paths (unless we take in a root directory?)
                    String fileName = srcFile.getFileName();
                    fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
                    container.addSourceFile(fileName, srcFile.getContents());
                } catch (IOException e) {
                    LOGGER.error("POST /svip/generators/osi - Error adding source file");
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
        }

        // Generate SBOMs
        Map<String, String> generatedSBOMFiles;
        try {
            List<String> tools = null;
            if (toolNames != null && toolNames.length > 0) tools = List.of(toolNames);

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
                SBOM sbom = input.toSBOMFile();
                sbom.toSBOMObject();
                this.sbomService.upload(sbom);
                uploaded.add(sbom.getId());

                // Log
                LOGGER.info("POST /svip/generators/osi - Generated SBOM with ID " + sbom.getId() + ": " + sbom.getName());
            } catch (JsonProcessingException e) {
                // Problem with parsing
                LOGGER.error("POST /svip/sboms - " + e.getMessage());
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            } catch (Exception e) {
                // Problem with uploading
                LOGGER.error("POST /svip/sboms - " + e.getMessage());
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        if (uploaded.size() < 1) {
            LOGGER.warn("POST /svip/generators/osi - No SBOMs generated by OSI container.");
            return new ResponseEntity<>("No SBOMs generated for these files.", HttpStatus.NO_CONTENT);
        }

        // Merge SBOMs into one SBOM
        Long merged = this.sbomService.merge(uploaded.toArray(new Long[0]));

        // todo should probably throw errors instead of returning null if error occurs
        if (merged == null)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        else if (merged == -1)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        else if (merged < 0)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        // Convert
        Long converted;
        try {
            converted = this.sbomService.convert(merged, schema, format, true);
        } catch (DeserializerException | JsonProcessingException | SerializerException | SBOMBuilderException |
                 ConversionException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Save and return
        return new ResponseEntity<>(converted, HttpStatus.OK);
    }
}