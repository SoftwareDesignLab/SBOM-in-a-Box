package org.svip.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.svip.api.entities.SBOM;
import org.svip.api.entities.SBOMFile;
import org.svip.api.repository.SBOMRepository;
import org.svip.api.requests.UploadSBOMFileInput;
import org.svip.api.services.SBOMFileService;
import org.svip.generation.parsers.utils.VirtualPath;
import org.svip.sbom.model.objects.SVIPSBOM;
import org.svip.serializers.SerializerFactory;
import org.svip.serializers.serializer.Serializer;

import java.io.IOException;
import java.util.*;
import java.util.zip.ZipException;

import static org.svip.api.controller.SBOMController.LOGGER;

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
                                             @RequestParam("format") SerializerFactory.Format format) throws Exception {

        Long parseId = this.parseSBOM(zipFile, projectName, schema, format);
        // todo convert should probably throw errors instead of returning null if error occurs
        if (parseId == null)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        // Return final merged ID
        return new ResponseEntity<>(parseId, HttpStatus.OK);
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
    public Long parseSBOM (MultipartFile zipFile, String projectName, SerializerFactory.Schema schema, SerializerFactory.Format format) throws Exception {
        String urlMsg = "GENERATE /svip/generate?projectName=" + projectName;

        // Ensure schema has a valid serializer
        try {
            schema.getSerializer(format);
        } catch (IllegalArgumentException e) {
            LOGGER.error(urlMsg + ": " + e.getMessage());
            return null;
        }
        ArrayList<HashMap<SBOMFile, Integer>> unZipped;
        try {
            unZipped = (ArrayList<HashMap<SBOMFile, Integer>>)
                    SBOMFileService.unZip(Objects.requireNonNull(SBOMFileService.convertMultipartToZip(zipFile)));
        } catch (ZipException e) {
            LOGGER.error(urlMsg + ":" + e.getMessage());
            return null;
        }

        HashMap<VirtualPath, String> virtualPathStringHashMap = new HashMap<>();

        for (HashMap<SBOMFile, Integer> h : unZipped
        ) {
            SBOMFile f = (SBOMFile) h.keySet().toArray()[0];
            if (!f.hasNullProperties()) // project files that are empty should just be ignored
                virtualPathStringHashMap.put(new VirtualPath(f.getFileName()), f.getContents());
        }

        org.svip.generation.parsers.ParserController parserController = new org.svip.generation.parsers.ParserController(projectName, virtualPathStringHashMap);


        SVIPSBOM parsed;
        try {
            parserController.parseAll();
            parsed = parserController.buildSBOM(schema);
        } catch (Exception e) {
            String error = "Error parsing into SBOM: " + Arrays.toString(e.getStackTrace());
            LOGGER.error(urlMsg + " " + error);
            return null;
        }


        Serializer s;
        String contents;

        try {
            s = SerializerFactory.createSerializer(schema, format, true);
            contents = s.writeToString(parsed);
        } catch (IllegalArgumentException | JsonProcessingException e) {
            String error = "Error serializing parsed SBOM: " + Arrays.toString(e.getStackTrace());
            LOGGER.error(urlMsg + " " + error);
            return null;
        }


        SBOMFile result = new SBOMFile(projectName + ((format == SerializerFactory.Format.JSON)
                ? ".json" : ".spdx"), contents);
        Random rand = new Random();
        result.setId(this.generateNewId(rand.nextLong(), rand));

        // convert result sbomfile to sbom
        UploadSBOMFileInput u = new UploadSBOMFileInput(result.getFileName(), result.getContents());

        // Save according to overwrite boolean
        SBOM converted = u.toSBOMFile();

        try {
            sbomService.upload(converted);
        } catch (Exception e) {
            // todo custom exception instead of generic
            throw new Exception("Failed to upload to Database: " + e.getMessage());
        }

        return converted.getId();
    }

    /**
     * Generates new ID given old one
     *
     * @param id                 old ID
     * @param rand               Random class
     * @return new ID
     */
    public long generateNewId(long id, Random rand) {
        // assign new id
        try {
            id += (Math.abs(rand.nextLong()) + id) % (id < 0 ? id : Long.MAX_VALUE);
        } catch (NullPointerException e) {
            return id;
        }

        return id;
    }
}