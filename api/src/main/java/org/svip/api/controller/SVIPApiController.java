package org.svip.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.svip.api.model.SBOMFile;
import org.svip.api.repository.SBOMFileRepository;
import org.svip.api.utils.Converter;
import org.svip.api.utils.Utils;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.SVIPSBOM;
import org.svip.sbomanalysis.comparison.merger.*;
import org.svip.sbomgeneration.parsers.ParserController;
import org.svip.sbomgeneration.serializers.SerializerFactory;
import org.svip.sbomgeneration.serializers.deserializer.Deserializer;
import org.svip.sbomgeneration.serializers.serializer.Serializer;
import org.svip.utils.VirtualPath;

import java.util.*;

/**
 * Spring API Controller for handling requests to the SVIP backend.
 *
 * @author Derek Garcia
 * @author Kevin Laporte
 * @author Asa Horn
 * @author Justin Jantzi
 * @author Matt London
 * @author Ian Dunn
 * @author Juan Francisco Patino
 */
@RestController
@RequestMapping("/svip")
public class SVIPApiController {

    /**
     * Spring-configured logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SVIPApiController.class);

    /**
     * Http headers of Spring boot application
     */
    private HttpHeaders headers;

    /**
     * MySQL server interface
     */
    private final SBOMFileRepository sbomFileRepository;

    //#region OSI (unused)

    /**
     * OSI docker container representation
     */
//    private OSI osiContainer;

    /**
     * Default OSI Bound Directory location
     */
//    private static String osiBoundDir = "src/main/java/com/svip/osi/core/bound_dir";

    /**
     * Default path to where dockerfile is located
     */
//    private static String dockerPath = "/core/src/main/java/org/svip/sbomfactory/osi/Dockerfile";

    /**
     * Current working directory
     */
//    private static String pwd = "/src/test/java/org/svip/api";

    /** TODO OSI
     * buildOSI runs on startup to build the OSI container independent of the front-end.
     */
//    @PostConstruct
//    public void buildOSI() {
//        // TODO: For SVIP v3, refactor to move OSI building operations into another class
//        osiContainer = new OSI(osiBoundDir, dockerPath);
//    }

    /** TODO OSI
     * To be called when the object is released by the garbage collector. DO NOT CALL MANUALLY
     */
    //    @PreDestroy
    //    public void close() {
    //        // Close the osi container so that we delete the instance
    //        osiContainer.close();
    //    }

    //#endregion

    @Autowired
    public SVIPApiController(final SBOMFileRepository sbomFileRepository) {
        headers = new HttpHeaders();
        headers.add("AccessControlAllowOrigin", "http://localhost:4200");

//        files = new HashMap<>();
        this.sbomFileRepository = sbomFileRepository;
    }

    /**
     * USAGE. Send POST request to /upload with one SBOM file.
     *   The SBOM file is made up of 2 JSON key-value pairs in the request body: fileName and contents.
     *
     * The API will respond with an HTTP 200 and the ID used to identify the SBOM file.
     *
     * @param sbomFile 2 JSON key-value pairs in the request body: fileName and contents.
     * @return The uploaded filename used to identify the SBOM file.
     */
    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestBody SBOMFile sbomFile) {
        // Validate
        if (sbomFile.hasNullProperties())
            return new ResponseEntity<>("SBOM filename and/or contents may not be empty", HttpStatus.BAD_REQUEST);

        String errorMsg = "Error processing file: " + sbomFile.getFileName();
        try {
            Deserializer d = SerializerFactory.createDeserializer(sbomFile.getContents());
            d.readFromString(sbomFile.getContents());
        } catch (IllegalArgumentException | JsonProcessingException e) {
            LOGGER.info("POST /svip/upload - " + errorMsg);
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(errorMsg, HttpStatus.BAD_REQUEST);
        }

        // Upload
        sbomFileRepository.save(sbomFile);
        LOGGER.info("POST /svip/upload - Uploaded SBOM with ID " + sbomFile.getId() + ": " + sbomFile.getFileName());

        // Return ID
        return Utils.encodeResponse(sbomFile.getId());
    }

    /**
     * USAGE. Send GET request to /view with a URL parameter id to get the contents of the SBOM with the specified ID.
     *
     * The API will respond with an HTTP 200 and the contents of the SBOM file.
     *
     * @param id The id of the SBOM contents to retrieve.
     * @return The contents of the SBOM file.
     */
    @GetMapping("/view")
    public ResponseEntity<String> view(@RequestParam("id") Long id) {
        // Get SBOM
        Optional<SBOMFile> sbomFile = sbomFileRepository.findById(id);

        // Check if it exists
        ResponseEntity<String> NOT_FOUND = Utils.checkIfExists(id, sbomFile, "view");
        if (NOT_FOUND != null) return NOT_FOUND;

        // Log
        LOGGER.info("GET /svip/view?id=" + id + " - File: " + sbomFile.get().getFileName());

        return Utils.encodeResponse(sbomFile.get().getContents());
    }

    /**
     * USAGE. Send GET request to /viewFiles.
     * The API will respond with an HTTP 200 and a JSON array of all IDs of currently uploaded SBOM files.
     *
     * @return A JSON array of IDs of all currently uploaded SBOM files.
     */
    @GetMapping("/viewFiles")
    public ResponseEntity<Long[]> viewFiles() {
        // Get file names
//        String[] fileNames = files.keySet().toArray(new String[0]);
        List<SBOMFile> sbomFiles = sbomFileRepository.findAll();

        // Log
        LOGGER.info("GET /svip/viewFiles - Found " + sbomFiles.size() + " file(s).");

        if (sbomFiles.isEmpty())
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        // Return file names
        return Utils.encodeResponse(sbomFiles.stream().map(SBOMFile::getId).toList().toArray(new Long[0]));
    }

    /**
     * USAGE. Send GET request to /getSBOM with a URL parameter id to get the deserialized SBOM.
     *
     * The API will respond with an HTTP 200 and the SBOM object json
     * todo: better ways to add more support?
     *
     * @param id The id of the SBOM contents to retrieve.
     * @return The contents of the SBOM file.
     */
    @GetMapping("/getSBOM")
    public ResponseEntity<?> getSBOM(@RequestParam("id") Long id){

        String urlMsg = "GET /svip/getSBOM?id=" + id;    // for logging

        // Get SBOM
        Optional<SBOMFile> sbomFile = sbomFileRepository.findById(id);

        // Return SBOM or invalid ID
        if (sbomFile.isEmpty()) {
            LOGGER.warn(urlMsg + " - FILE NOT FOUND");
            return new ResponseEntity<>("Invalid SBOM ID", HttpStatus.NOT_FOUND);
        }

        // Deserialize SBOM into JSON Object
        SBOM sbom;
        try{
            Deserializer d = SerializerFactory.createDeserializer(sbomFile.get().getContents());
            sbom = d.readFromString(sbomFile.get().getContents());
        } catch (JsonProcessingException e ){
            return new ResponseEntity<>("Failed to deserialize SBOM content, may be an unsupported format", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e){
            return new ResponseEntity<>("Deserialization Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Log
        LOGGER.info(urlMsg + " - File: " + sbomFile.get().getFileName());

        return Utils.encodeResponse(sbom);
    }


    /**
     * USAGE. Send DELETE request to /delete with a URL parameter id to get the contents of the SBOM with the specified
     * ID.
     *
     * The API will respond with an HTTP 200 and the ID of the deleted SBOM file (if found).
     *
     * @param id The id of the SBOM contents to retrieve.
     * @return The ID of the deleted file.
     */
    @DeleteMapping("/delete")
    public ResponseEntity<Long> delete(@RequestParam("id") Long id) {
        // Get SBOM to be deleted
        Optional<SBOMFile> sbomFile = sbomFileRepository.findById(id);

        // Check if it exists
        if (sbomFile.isEmpty()) {
            LOGGER.info("DELETE /svip/delete?id=" + id + " - FILE NOT FOUND");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Delete
        sbomFileRepository.delete(sbomFile.get());

        // Log
        LOGGER.info("DELETE /svip/delete?id=" + id + " - File: " + sbomFile.get().getFileName());

        // Return deleted ID as confirmation
        return Utils.encodeResponse(sbomFile.get().getId());
    }

    /**
     * USAGE. Send CONVERT request to /convert an existing SBOM on the backend to a desired schema and format
     *
     * @param id of the SBOM
     * @param schema to convert to
     * @param format to convert to
     * @param overwrite whether to overwrite original
     * @return converted SBOM
     */

    @GetMapping("/convert")
    public ResponseEntity<String> convert(@RequestParam("id") long id, @RequestParam("schema") String schema,
                                          @RequestParam("format") String format,
                                          @RequestParam("overwrite") Boolean overwrite){
        // Get SBOM
        Optional<SBOMFile> sbomFile = sbomFileRepository.findById(id);

        // Check if it exists
        ResponseEntity<String> NOT_FOUND = Utils.checkIfExists(id, sbomFile, "convert");
        if (NOT_FOUND != null) return NOT_FOUND;

        // Get and convert SBOM
        SBOMFile toConvert = sbomFile.get();
        HashMap<SBOMFile, String> conversionResult = Converter.convert(toConvert, schema, format);
        String error = (String) conversionResult.values().toArray()[0];
        SBOMFile converted = (SBOMFile) conversionResult.keySet().toArray()[0];

        // Error message if needed
        String defaultErrorMessage = "CONVERT /svip/convert?id=" + id + " - ERROR IN CONVERSION TO " + schema
                + ((error.length() != 0) ? (": " + error) : "");

        // bad request errors
        if(error.toLowerCase().contains("not valid") && (
                error.toLowerCase().contains("schema") || error.toLowerCase().contains("format"))){
            LOGGER.error(defaultErrorMessage);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else if (schema.toLowerCase().contains("cdx") && format.equals("TAGVALUE")) {
            LOGGER.error("CONVERT /svip/convert?id=" + id + "TAGVALUE unsupported by CDX14");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // if anything went wrong, an SBOMFILE with a blank name and contents will be returned,
        // paired with the message String
        if (converted.hasNullProperties()) {
            LOGGER.error(defaultErrorMessage);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // assign appropriate id and name
        converted.setId(id);
        converted.setFileName(toConvert.getFileName());

        // overwrite
        if(overwrite){
            sbomFileRepository.deleteById(id);
            sbomFileRepository.save(converted);
        }

        return Utils.encodeResponse(converted.getContents());
    }

    /**
     * USAGE. Send GENERATE request to /generate an SBOM from source file(s)
     *
     * @param projectName of project to be converted to SBOM
     * @param files from project source
     * @param schema to convert to
     * @param format to convert to
     * @return generated SBOM
     */
    @PostMapping("/generators/parsers")
    public ResponseEntity<String> generateParsers(@RequestBody SBOMFile[] files,
                                           @RequestParam("projectName") String projectName,
                                           @RequestParam("schema") SerializerFactory.Schema schema, // todo implement this schema + format params in /convert
                                           @RequestParam("format") SerializerFactory.Format format){

        ParserController parserController = new ParserController(projectName, new HashMap<>());

        String urlMsg = "GENERATE /svip/generate?projectName=" + projectName;

        if(schema.equals(SerializerFactory.Schema.CDX14) && format.equals(SerializerFactory.Format.TAGVALUE)){
            LOGGER.error(urlMsg + " cannot parse into " + schema + " with incompatible format " + format);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        for (SBOMFile f: files
             ) {
            if(f.hasNullProperties()){
                LOGGER.error(urlMsg + "/fileName=" + f.getFileName() + "has null properties");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            parserController.addFile(new VirtualPath(f.getFileName()), f.getContents());
        }

        SVIPSBOM parsed;
        try{
            parserController.parseAll();
            parsed = parserController.buildSBOM(schema);
        } catch (Exception e) {
            String error = "Error parsing into SBOM: " + e.getMessage();
            LOGGER.error(urlMsg + " " + error);
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }

        Serializer s;
        String contents;
        try{
            s = SerializerFactory.createSerializer(schema,format, true);
            contents = s.writeToString(parsed);
        } catch (IllegalArgumentException | JsonProcessingException e) {
            String error = "Error serializing parsed SBOM: " + Arrays.toString(e.getStackTrace());
            LOGGER.error(urlMsg + " " + error);
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }

        return Utils.encodeResponse(contents);

    }

    @PostMapping("/generators/osi")
    public ResponseEntity<String> generateOSI(@RequestBody SBOMFile[] files,
                                           @RequestParam("projectName") String projectName,
                                           @RequestParam("schema") SerializerFactory.Schema schema, // todo implement this schema + format params in /convert
                                           @RequestParam("format") SerializerFactory.Format format){
        // TODO (separate branch)
        return null;
    }

    /**
     * Merge two existing SBOMs
     * @param ids of the two SBOMs
     * @return a merged sbomFile
     */
    @GetMapping("/merge")
    public ResponseEntity<String> merge(@RequestParam("ids") long[] ids){

        ArrayList<SBOM> sboms = new ArrayList<>();

        String urlMsg = "DELETE /svip/merge?id=";

        long idSum = 0L;

        // check for bad files
        for (Long id: ids
        ) {

            // Get SBOM
            Optional<SBOMFile> sbomFile = sbomFileRepository.findById(id);

            // Check if it exists
            ResponseEntity<String> NOT_FOUND = Utils.checkIfExists(id, sbomFile, "merge");
            if (NOT_FOUND != null) return NOT_FOUND;
            SBOMFile sbom = sbomFile.get();

            if(sbom.hasNullProperties()){
                LOGGER.info(urlMsg + sbomFile.get().getId() + " - ERROR IN MERGE - HAS NULL PROPERTIES");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            // deserialize into SBOM object
            Deserializer d;
            SVIPSBOM deserialized;

            try{
                d = SerializerFactory.createDeserializer(sbom.getContents());
                deserialized = (SVIPSBOM) d.readFromString(sbom.getContents());
            }catch (Exception e){
                LOGGER.info(urlMsg + sbomFile.get().getId() + "DURING DESERIALIZATION: " +
                        e.getMessage());
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }

            sboms.add(deserialized);
            idSum += id;
        }


        // todo, merging more than two SBOMs is not supported right now
        SBOM merged;
        try{
            MergerController mergerController = new MergerController();
            merged = mergerController.merge(sboms.get(0), sboms.get(1));
        } catch (MergerException e) {
            String error = "Error merging SBOMs: " + e.getMessage();
            LOGGER.error(urlMsg + " " + error);
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }

        Serializer s;
        String contents;
        try{
            s = SerializerFactory.createSerializer(SerializerFactory.Schema.SPDX23, SerializerFactory.Format.TAGVALUE, // todo is this default?
                    true);
            contents = s.writeToString((SVIPSBOM) merged);
        } catch (IllegalArgumentException | JsonProcessingException e) {
            String error = "Error serializing merged SBOM: " + Arrays.toString(e.getStackTrace());
            LOGGER.error(urlMsg + " " + error);
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }

        // SBOMFile
        SBOMFile result = new SBOMFile(merged.getName(), contents);
        Random rand = new Random();

        // assign new id and name
        while(!sbomFileRepository.existsById(idSum)){ // todo check if frontend are okay with this
            idSum += (rand.nextLong() + idSum) % idSum;
        }
        result.setId(idSum);
        sbomFileRepository.save(result);

        return Utils.encodeResponse(result.toString());

    }
}
