/** Copyright 2021 Rochester Institute of Technology (RIT). Developed with
* government support under contract 70RCSA22C00000008 awarded by the United
* States Department of Homeland Security for Cybersecurity and Infrastructure Security Agency.
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the “Software”), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
 */

package org.svip.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.svip.api.entities.SBOMFile;
import org.svip.api.requests.UploadSBOMFileInput;
import org.svip.api.services.SBOMFileService;
import org.svip.generation.parsers.ParserManager;
import org.svip.generation.parsers.utils.VirtualPath;
import org.svip.sbom.model.objects.SVIPSBOM;
import org.svip.serializers.SerializerFactory;
import org.svip.serializers.serializer.Serializer;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * REST API Controller for generating SBOMs using Parsers
 *
 * @author Derek Garcia
 **/
@RestController
@RequestMapping("/svip/generators/parsers")
public class ParserController {
    /**
     * Spring-configured logger
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(SBOMController.class);
    private final SBOMFileService sbomService;

    /**
     * Create new Controller with services
     *
     * @param sbomService Service for handling SBOM queries
     */
    public ParserController(SBOMFileService sbomService){
        this.sbomService = sbomService;
    }

    /**
     * USAGE. Send GENERATE request to /generate an SBOM from source file(s)
     *
     * @param projectName of project to be converted to SBOM
     * @param zipFile     path to zip file
     * @param schema      to convert to
     * @param format      to convert to
     * @return generated SBOM
     */
    @PostMapping(value = "", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> generateParsers(@RequestParam("zipFile") MultipartFile zipFile,
                                             @RequestParam("projectName") String projectName,
                                             @RequestParam("schema") SerializerFactory.Schema schema,
                                             @RequestParam("format") SerializerFactory.Format format) {
        // Ensure schema has a valid serializer
        try {
            schema.getSerializer(format);
        } catch (IllegalArgumentException e) {
            LOGGER.error("POST /svip/generators/parsers - " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        // Unzip multipart file
        Map<String, String> unZipped;
        try {
            unZipped = SBOMFileService.unZip(zipFile);
        } catch (IOException e) {
            LOGGER.error("POST /svip/generators/parsers - " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        // Add each file to a hashmap to then pass into parserManager
        HashMap<VirtualPath, String> virtualPathStringHashMap = new HashMap<>();
        for (Map.Entry<String, String> file : unZipped.entrySet())
            virtualPathStringHashMap.put(new VirtualPath(file.getKey()), file.getValue());

        ParserManager parserManager = new ParserManager(projectName, virtualPathStringHashMap);

        // Parse all files using parserManager
        SVIPSBOM parsed;
        try {
            parserManager.parseAll();
            parsed = parserManager.buildSBOM(schema);
        } catch (Exception e) {
            String error = "Error parsing into SBOM: " + Arrays.toString(e.getStackTrace());
            LOGGER.error("POST /svip/generators/parsers - " + error);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Serialize SVIPSBOM to string
        String contents;
        try {
            Serializer s = SerializerFactory.createSerializer(schema, format, true);
            contents = s.writeToString(parsed);
        } catch (IllegalArgumentException | JsonProcessingException e) {
            String error = "Error serializing parsed SBOM: " + Arrays.toString(e.getStackTrace());
            LOGGER.error("POST /svip/generators/parsers - " + error);
            return null;
        }

        // Convert & save according to overwrite boolean
        SBOMFile converted;
        try {
            // convert result sbomfile to sbom
            UploadSBOMFileInput u = new UploadSBOMFileInput(projectName + ((format == SerializerFactory.Format.JSON)
                    ? ".json" : ".spdx"), contents);
            converted = u.toSBOMFile();
            sbomService.upload(converted);
        } catch (JsonProcessingException e) {
            LOGGER.error("POST /svip/generators/parsers - Error converting generated SBOM");
            return new ResponseEntity<>("Error converting generated SBOM", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            LOGGER.error("POST /svip/generators/parsers - Error uploading converted SBOM");
            return new ResponseEntity<>("Error uploading converted SBOM", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Return final merged ID
        return new ResponseEntity<>(converted.getId(), HttpStatus.OK);
    }
}