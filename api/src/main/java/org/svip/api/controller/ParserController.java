package org.svip.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.svip.api.services.SBOMFileService;
import org.svip.serializers.SerializerFactory;
import java.io.IOException;

/**
 * REST API Controller for generating SBOMs using Parsers
 *
 * @author Derek Garcia
 **/
@RestController
@RequestMapping("/svip/generators")
public class ParserController {
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
    @PostMapping("/parsers")
    public ResponseEntity<?> generateParsers(@RequestParam("zipFile") MultipartFile zipFile,
                                             @RequestParam("projectName") String projectName,
                                             @RequestParam("schema") SerializerFactory.Schema schema,
                                             @RequestParam("format") SerializerFactory.Format format) throws IOException {

        Long parseId = this.sbomService.parseSBOM(zipFile, projectName, schema, format);
        // todo convert should probably throw errors instead of returning null if error occurs
        if (parseId == null)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        // Return final merged ID
        return new ResponseEntity<>(parseId, HttpStatus.OK);
    }
}