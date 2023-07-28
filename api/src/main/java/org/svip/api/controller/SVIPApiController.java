package org.svip.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.svip.api.model.SBOMFile;
import org.svip.api.repository.SBOMFileRepository;
import org.svip.api.utils.Converter;
import org.svip.api.utils.Utils;
import org.svip.sbom.builder.interfaces.generics.SBOMBuilder;
import org.svip.sbom.builder.objects.SVIPSBOMBuilder;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.interfaces.generics.SBOMPackage;
import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23File;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.sbom.model.objects.SVIPSBOM;
import org.svip.sbomanalysis.comparison.merger.Merger;
import org.svip.sbomanalysis.comparison.merger.MergerController;
import org.svip.sbomanalysis.comparison.merger.MergerException;
import org.svip.sbomgeneration.parsers.ParserController;
import org.svip.sbomanalysis.qualityattributes.pipelines.QualityReport;
import org.svip.sbomanalysis.qualityattributes.pipelines.interfaces.generics.QAPipeline;
import org.svip.sbomanalysis.qualityattributes.pipelines.schemas.CycloneDX14.CDX14Pipeline;
import org.svip.sbomanalysis.qualityattributes.pipelines.schemas.SPDX23.SPDX23Pipeline;
import org.svip.sbomgeneration.osi.OSI;
import org.svip.sbomgeneration.parsers.ParserController;
import org.svip.sbomgeneration.serializers.SerializerFactory;
import org.svip.sbomgeneration.serializers.deserializer.Deserializer;
import org.svip.sbomgeneration.serializers.serializer.Serializer;
import org.svip.sbomvex.VEXResult;
import org.svip.sbomvex.database.NVDClient;
import org.svip.sbomvex.database.OSVClient;
import org.svip.sbomvex.database.interfaces.VulnerabilityDBClient;
import org.svip.sbomvex.model.VEX;
import org.svip.sbomvex.model.VEXType;
import org.svip.sbomvex.vexstatement.VEXStatement;
import org.svip.utils.VirtualPath;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.Arrays;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipFile;

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

    /**
     * OSI docker container representation
     */
    private final OSI osiContainer;

    /**
     * Autowired constructor. Initializes the API controller with a configured SBOMFileRepository instance and OSI
     * enabled.
     *
     * @param sbomFileRepository The SBOMFileRepository to interact with the MySQL database server.
     * @param startWithOSI Whether to start with OSI enabled. If false, the OSI endpoint will be disabled.
     */
    @Autowired
    public SVIPApiController(final SBOMFileRepository sbomFileRepository, @Value("true") boolean startWithOSI) {
        this.headers = new HttpHeaders();
        this.headers.add("AccessControlAllowOrigin", "http://localhost:4200");

        this.sbomFileRepository = sbomFileRepository;

        // If starting with OSI is enabled, try starting the container
        OSI container = null; // Disabled state
        String error = "OSI ENDPOINT DISABLED -- ";
        if (startWithOSI)
            try {
                container = new OSI();
                LOGGER.info("OSI ENDPOINT ENABLED");
            } catch (Exception e) {
                // If we can't construct the OSI container for any reason, log and disable OSI.
                LOGGER.warn(error + "Unable to setup OSI container.");
                LOGGER.error("OSI Docker API response: " + e.getMessage());
            }
        else LOGGER.warn(error + "Disabled starting with OSI.");
        this.osiContainer = container;
    }

    /**
     * USAGE. Send POST request to /sboms with one SBOM file.
     *   The SBOM file is made up of 2 JSON key-value pairs in the request body: fileName and contents.
     *
     * The API will respond with an HTTP 200 and the ID used to identify the SBOM file.
     *
     * @param sbomFile 2 JSON key-value pairs in the request body: fileName and contents.
     * @return The uploaded filename used to identify the SBOM file.
     */
    @PostMapping("/sboms")
    public ResponseEntity<?> upload(@RequestBody SBOMFile sbomFile) {
        // Validate
        if (sbomFile.hasNullProperties())
            return new ResponseEntity<>("SBOM filename and/or contents may not be empty", HttpStatus.BAD_REQUEST);

        String errorMsg = "Error processing file: " + sbomFile.getFileName();
        try {
            Deserializer d = SerializerFactory.createDeserializer(sbomFile.getContents());
            d.readFromString(sbomFile.getContents());
        } catch (IllegalArgumentException | JsonProcessingException e) {
            LOGGER.info("POST /svip/sboms - " + errorMsg);
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage() + " " + errorMsg, HttpStatus.BAD_REQUEST);
        }

        // Upload
        sbomFileRepository.save(sbomFile);
        LOGGER.info("POST /svip/sboms - Uploaded SBOM with ID " + sbomFile.getId() + ": " + sbomFile.getFileName());

        // Return ID
        return Utils.encodeResponse(sbomFile.getId());
    }

    /**
     * USAGE. Send GET request to /sboms/content with a URL parameter id to get the contents of the SBOM with the specified ID.
     *
     * The API will respond with an HTTP 200 and the contents of the SBOM file.
     *
     * @param id The id of the SBOM contents to retrieve.
     * @return The contents of the SBOM file.
     */
    @GetMapping("/sboms/content")
    public ResponseEntity<String> view(@RequestParam("id") Long id) {
        // Get SBOM
        Optional<SBOMFile> sbomFile = sbomFileRepository.findById(id);

        // Return SBOM or invalid ID
        if (sbomFile.isEmpty()) {
            LOGGER.info("GET /svip/sboms/content?id=" + id + " - FILE NOT FOUND");
            return new ResponseEntity<>("Invalid SBOM ID.", HttpStatus.NOT_FOUND);
        }

        // Log
        LOGGER.info("GET /svip/sboms/content?id=" + id + " - File: " + sbomFile.get().getFileName());

        return Utils.encodeResponse(sbomFile.get().getContents());
    }

    /**
     * USAGE. Send GET request to /sboms.
     * The API will respond with an HTTP 200 and a JSON array of all IDs of currently uploaded SBOM files.
     *
     * @return A JSON array of IDs of all currently uploaded SBOM files.
     */
    @GetMapping("/sboms")
    public ResponseEntity<Long[]> viewFiles() {
        // Get file names
        List<SBOMFile> sbomFiles = sbomFileRepository.findAll();

        // Log
        LOGGER.info("GET /svip/sboms - Found " + sbomFiles.size() + " file(s).");

        if (sbomFiles.isEmpty())
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        // Return file names
        return Utils.encodeResponse(sbomFiles.stream().map(SBOMFile::getId).toList().toArray(new Long[0]));
    }

    /**
     * USAGE. Send GET request to /sboms with a URL parameter id to get the deserialized SBOM.
     *
     * The API will respond with an HTTP 200 and the SBOM object json
     * todo: better ways to add more support?
     *
     * @param id The id of the SBOM contents to retrieve.
     * @return The contents of the SBOM file.
     */
    @GetMapping("/sbom")
    public ResponseEntity<?> getSBOM(@RequestParam("id") Long id){

        String urlMsg = "GET /svip/sbom?id=" + id;    // for logging

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
    @DeleteMapping("/sboms")
    public ResponseEntity<Long> delete(@RequestParam("id") Long id) {
        // Get SBOM to be deleted
        Optional<SBOMFile> sbomFile = sbomFileRepository.findById(id);

        // Check if it exists
        if (sbomFile.isEmpty()) {
            LOGGER.info("DELETE /svip/sboms?id=" + id + " - FILE NOT FOUND");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Delete
        sbomFileRepository.delete(sbomFile.get());

        // Log
        LOGGER.info("DELETE /svip/sboms?id=" + id + " - File: " + sbomFile.get().getFileName());

        // Return deleted ID as confirmation
        return Utils.encodeResponse(sbomFile.get().getId());
    }

    /**
     * USAGE. Send PUT request to /sboms an existing SBOM on the backend to a desired schema and format
     *
     * @param id of the SBOM
     * @param schema to convert to
     * @param format to convert to
     * @param overwrite whether to overwrite original
     * @return converted SBOM
     */
    @PutMapping("/sboms")
    public ResponseEntity<Long> convert(@RequestParam("id") long id, @RequestParam("schema") SerializerFactory.Schema schema,
                                          @RequestParam("format") SerializerFactory.Format format,
                                          @RequestParam("overwrite") Boolean overwrite){
        // Get SBOM
        Optional<SBOMFile> sbomFile = sbomFileRepository.findById(id);

        // Check if it exists
        ResponseEntity<Long> NOT_FOUND = Utils.checkIfExists(id, sbomFile, "convert");
        if (NOT_FOUND != null) return NOT_FOUND;

        // Get and convert SBOM
        SBOMFile toConvert = sbomFile.get();
        SBOMFile converted;

        // Error message if needed
        String urlMsg = "CONVERT /svip/sboms?id=" + id;

        // Ensure schema has a valid serializer
        try {
            converted = Converter.convert(toConvert, schema, format);
            schema.getSerializer(format);
        } catch (Exception e) {
            LOGGER.error(urlMsg + ": " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // if anything went wrong, an SBOMFILE with a blank name and contents will be returned,
        // paired with the message String
        if (converted.hasNullProperties()) {
            LOGGER.error(urlMsg + ": SBOM has null properties");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // assign appropriate id and name
        converted.setId(id);
        converted.setFileName(toConvert.getFileName());

        // overwrite
        if(overwrite)
            sbomFileRepository.deleteById(id);
        else
            converted.setId(Utils.generateNewId(id, new Random(), sbomFileRepository));

        sbomFileRepository.save(converted);

        return Utils.encodeResponse(converted.getId());
    }

    /** USAGE Send GET request to /qa with a URL parameter id to conduct a quality assessment on the SBOM with
     * the specified ID.
     *
     * The API will respond with an HTTP 200 and a JSON string of the Quality Report (if SBOM was found).
     *
     * @param id The id of the SBOM contents to retrieve.
     * @return A JSON string of the Quality Report file.
     */
    @GetMapping("/sboms/qa")
    public ResponseEntity<String> qa(@RequestParam("id") long id) throws IOException {

        SBOM sbom;
        Deserializer d;
        QAPipeline qaPipeline;

        // Get the SBOM to be tested
        Optional<SBOMFile> sbomFile = sbomFileRepository.findById(id);

        // Check if it exists
        if (sbomFile.isEmpty()) {
            LOGGER.info("QA /svip/sboms/qa?id=" + id + " - FILE NOT FOUND");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Deserialize SBOM into JSON Object
        try{
            d = SerializerFactory.createDeserializer(sbomFile.get().getContents());
            sbom = d.readFromString(sbomFile.get().getContents());
        } catch (JsonProcessingException e ){
            return new ResponseEntity<>("Failed to deserialize SBOM content, may be an unsupported format", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e){
            return new ResponseEntity<>("Deserialization Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Determine what QA Pipeline to use based on
        if (sbom instanceof CDX14SBOM) {
            qaPipeline = new CDX14Pipeline();
        } else if (sbom instanceof SPDX23SBOM) {
            qaPipeline = new SPDX23Pipeline();
        } else {
            return new ResponseEntity<>("Deserialization Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // QA test SBOM
        QualityReport qualityReport = qaPipeline.process(String.valueOf(id), sbom);

        // Log
        LOGGER.info("QA /svip/sboms/?id=" + id + " - TESTED: " + sbomFile.get().getFileName());

        // Return Quality Report as JSON to Frontend
        ObjectMapper mapper = new ObjectMapper();
        return new ResponseEntity<>(mapper.writeValueAsString(qualityReport), HttpStatus.OK);
    }

    //#region Deprecated Endpoints

    /**
     * USAGE. Send GENERATE request to /generate an SBOM from source file(s)
     *
     * @param projectName of project to be converted to SBOM
     * @param zipPath path to zip file
     * @param schema to convert to
     * @param format to convert to
     * @return generated SBOM
     */
    @PostMapping("/generators/parsers")
    public ResponseEntity<?> generateParsers(@RequestParam("zipPath") String zipPath,
                                             @RequestParam("projectName") String projectName,
                                             @RequestParam("schema") SerializerFactory.Schema schema,
                                             @RequestParam("format") SerializerFactory.Format format) throws IOException {

        String urlMsg = "GENERATE /svip/generate?projectName=" + projectName;

        // Ensure schema has a valid serializer
        try { schema.getSerializer(format); } catch (IllegalArgumentException e) {
            LOGGER.error(urlMsg + ": " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        ArrayList<HashMap<SBOMFile, Integer>> unZipped = (ArrayList<HashMap<SBOMFile, Integer>>) Utils.unZip(zipPath);

        HashMap<VirtualPath, String> virtualPathStringHashMap = new HashMap<>();

        for (HashMap<SBOMFile, Integer> h: unZipped
             ) {

            SBOMFile f = (SBOMFile) h.keySet().toArray()[0];

            if(f.hasNullProperties()){
                LOGGER.error(urlMsg + "/fileName=" + f.getFileName() + " has null properties");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            virtualPathStringHashMap.put(new VirtualPath(f.getFileName()), f.getContents());

        }

        ParserController parserController = new ParserController(projectName, virtualPathStringHashMap);


        SVIPSBOM parsed;
        try{
            parserController.parseAll();
            parsed = parserController.buildSBOM(schema);
        } catch (Exception e) {
            String error = "Error parsing into SBOM: " + e.getMessage();
            LOGGER.error(urlMsg + " " + error);
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Serializer s;
        String contents;
        try{
            s = SerializerFactory.createSerializer(schema,format, true);
            contents = s.writeToString(parsed);
        } catch (IllegalArgumentException | JsonProcessingException e) {
            String error = "Error serializing parsed SBOM: " + Arrays.toString(e.getStackTrace());
            LOGGER.error(urlMsg + " " + error);
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        SBOMFile result = new SBOMFile(projectName + ((format == SerializerFactory.Format.JSON)
                 ? ".json" : ".spdx"),contents);
        Random rand = new Random();
        result.setId(Utils.generateNewId(rand.nextLong(), rand, sbomFileRepository));
        sbomFileRepository.save(result);

        return Utils.encodeResponse(result.getId());

    }

    @PostMapping("/generators/osi")
    public ResponseEntity<?> generateOSI(@RequestBody SBOMFile[] files,
                                           @RequestParam("projectName") String projectName,
                                           @RequestParam("schema") SerializerFactory.Schema schema,
                                           @RequestParam("format") SerializerFactory.Format format) {
        if (osiContainer == null)
            return new ResponseEntity<>("OSI has been disabled for this instance.", HttpStatus.NOT_FOUND);

        String urlMsg = "POST /svip/generators/osi";

        // Ensure schema has a valid serializer
        try { schema.getSerializer(format); } catch (IllegalArgumentException e) {
            LOGGER.error(urlMsg + ": " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        // Validate & add files
        for (SBOMFile srcFile : files) {
            if(srcFile.hasNullProperties()){
                LOGGER.error(urlMsg + ": file " + srcFile.getFileName() + " has null properties");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            try {
                // Remove any directories, causes issues with OSI paths (unless we take in a root directory?)
                String fileName = srcFile.getFileName();
                fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
                osiContainer.addSourceFile(fileName, srcFile.getContents());
            } catch (IOException e) {
                LOGGER.error(urlMsg + ": Error adding source file");
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }

        Map<String, String> sboms;
        // Generate SBOMs
        try {
            sboms = osiContainer.generateSBOMs();
        } catch (Exception e) {
            LOGGER.warn(urlMsg + ": Exception occurred while running OSI container: " + e.getMessage());
            return new ResponseEntity<>("Exception occurred while running OSI container.",
                    HttpStatus.NOT_FOUND);
        }


        // Deserialize SBOMs into list of SBOMs
        List<SBOM> deserialized = new ArrayList<>();
        Deserializer d;
        for (Map.Entry<String, String> sbom : sboms.entrySet())
            try {
                d = SerializerFactory.createDeserializer(sbom.getValue());
                deserialized.add(d.readFromString(sbom.getValue()));
            } catch (JsonProcessingException e) {
                LOGGER.warn(urlMsg + ": Exception occurred while deserializing SBOMs: " + e.getMessage());
                return new ResponseEntity<>("Exception occurred while deserializing SBOMs.",
                        HttpStatus.NOT_FOUND);
            } catch (IllegalArgumentException ignored) {} // TODO Skip any XML SBOMs, we don't support deserialization

        if (deserialized.size() == 0)
            return new ResponseEntity<>("No SBOMs generated for these files.", HttpStatus.NOT_FOUND);

        // Merge SBOMs into one SBOM
        MergerController merger = new MergerController();
        SBOM osiMerged;
        try {
            osiMerged = merger.mergeAll(deserialized);
        } catch (MergerException e) {
            LOGGER.warn(urlMsg + ": Exception occurred while merging SBOMs: " + e.getMessage());
            return new ResponseEntity<>("Exception occurred while merging SBOMs.",
                    HttpStatus.NOT_FOUND);
        }

        // Convert final SBOM into SVIPSBOM
        SVIPSBOMBuilder builder = new SVIPSBOMBuilder();
        SerializerFactory.Schema oldSchema;
        switch (osiMerged.getFormat().toLowerCase()) {
            case "cyclonedx" -> oldSchema = SerializerFactory.Schema.CDX14;
            case "spdx" -> oldSchema = SerializerFactory.Schema.SPDX23;
            default -> { // TODO We don't support SVIP SBOM merging
                LOGGER.warn(urlMsg + ": Error converting final merged SBOM: SVIP schema unsupported.");
                return new ResponseEntity<>("Error converting final merged SBOM: SVIP schema unsupported.",
                        HttpStatus.NOT_FOUND);
            }
        }
        Converter.buildSBOM(builder, osiMerged, schema, oldSchema);
        builder.setName(projectName); // Set SBOM name to specified project name TODO should this be done in OSI class?

        // Serialize SVIPSBOM to given schema and format
        Serializer serializer = SerializerFactory.createSerializer(schema, format, true);
        SBOMFile serializedSBOM;
        try {
            serializedSBOM = new SBOMFile(projectName,
                    serializer.writeToString(builder.Build()));
        } catch (JsonProcessingException e) {
            LOGGER.warn(urlMsg + "Exception occurred while merging SBOMs: " + e.getMessage());
            return new ResponseEntity<>("Exception occurred while merging SBOMs.",
                    HttpStatus.NOT_FOUND);
        }

        // Save and return
        SBOMFile saved = sbomFileRepository.save(serializedSBOM);
        return Utils.encodeResponse(Long.toString(saved.getId()));
    }

    /**
     * Merge two existing SBOMs
     * @param ids of the two SBOMs
     * @return a merged sbomFile
     */
    @GetMapping("/merge")
    public ResponseEntity<?> merge(@RequestParam("ids") long[] ids){

        ArrayList<SBOM> sboms = new ArrayList<>();

        String urlMsg = "MERGE /svip/merge?id=";

        long idSum = 0L;

        // check for bad files
        for (Long id: ids
        ) {

            // Get SBOM
            Optional<SBOMFile> sbomFile = sbomFileRepository.findById(id);

            // Check if it exists
            ResponseEntity<Long> NOT_FOUND = Utils.checkIfExists(id, sbomFile, "merge");
            if (NOT_FOUND != null) return NOT_FOUND;
            SBOMFile sbom = sbomFile.get();

            if(sbom.hasNullProperties()){
                LOGGER.info(urlMsg + sbomFile.get().getId() + " - ERROR IN MERGE - HAS NULL PROPERTIES");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            // deserialize into SBOM object
            Deserializer d;
            SBOM deserialized;

            try{
                d = SerializerFactory.createDeserializer(sbom.getContents());
                deserialized = d.readFromString(sbom.getContents());
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
            s = SerializerFactory.createSerializer(SerializerFactory.Schema.SVIP, SerializerFactory.Format.JSON,true);
            SVIPSBOMBuilder builder = new SVIPSBOMBuilder();
            builder.setSpecVersion("1.0-a");
            Converter.buildSBOM(builder, merged, SerializerFactory.Schema.SVIP, null);
            contents = s.writeToString(builder.Build());
        } catch (IllegalArgumentException | JsonProcessingException e) {
            String error = "Error serializing merged SBOM: " + e.getMessage();
            LOGGER.error(urlMsg + " " + error);
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }

        // SBOMFile
        SBOMFile result = new SBOMFile(merged.getName(), contents);
        Random rand = new Random();

        idSum = Utils.generateNewId(idSum, rand, sbomFileRepository);

        result.setId(idSum);
        sbomFileRepository.save(result);

        return Utils.encodeResponse(idSum);

    }

    /** USAGE Send GET request to /vex to generate a VEX Document for an SBOM
     * The API will respond with an HTTP 200 a VEX object, and a hashmap of
     * and errors that occurred
     *
     * @param id The id of the SBOM contents to retrieve.
     * @param format the format of teh VEX Document
     * @param client the api client to use (currently NVD or OSV)
     * @return A new VEXResult of the VEX document and any errors that occurred
     */
    @GetMapping("/sboms/vex")
    public ResponseEntity<VEXResult> vex(@RequestHeader(value = "apiKey", required = false) String apiKey,
                                         @RequestParam("id") long id,
                                         @RequestParam("format") String format,
                                         @RequestParam("client") String client){
        SBOM sbom;
        Deserializer d;

        // Get the SBOM to be tested
        Optional<SBOMFile> sbomFile = sbomFileRepository.findById(id);

        // Check if it exists
        if (sbomFile.isEmpty()) {
            LOGGER.info("VEX /svip/sboms/vex?id=" + id + " - FILE NOT FOUND");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Deserialize SBOM into JSON object
        try{
            d = SerializerFactory.createDeserializer(sbomFile.get().getContents());
            sbom = d.readFromString(sbomFile.get().getContents());
        } catch (JsonProcessingException e ){
            LOGGER.info("Failed to deserialize SBOM content, may be an unsupported format");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e){
            LOGGER.info("Deserialization Error");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        VulnerabilityDBClient vc;
        // check that user entered a valid database client
        switch(client.toLowerCase()){
            case "osv" -> vc = new OSVClient();
            case "nvd" -> vc = new NVDClient();
            default -> {
                LOGGER.info("VEX /svip/sboms/vex?client=" + client + " - INVALID CLIENT");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }

        // create new VEX builder and add sbom fields
        VEX.Builder vb = new VEX.Builder();
        String creationTime = String.valueOf(java.time.LocalDateTime.now());
        vb.setVEXIdentifier(sbom.getName());
        vb.setDocVersion("1.0");
        vb.setTimeFirstIssued(creationTime);
        vb.setTimeLastUpdated(creationTime);

        // check that user entered a valid format
        switch(format.toLowerCase()){
            case "cyclonedx" -> {
                vb.setOriginType(VEXType.CYCLONE_DX);
                vb.setSpecVersion("1.4");
            }
            case "csaf" -> {
                vb.setOriginType(VEXType.CSAF);
                vb.setSpecVersion("2.0");
            }
            default -> {
                LOGGER.info("VEX /svip/sboms/vex?format=" + format + " - INVALID VEX FORMAT");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }

        HashMap<String, String> error = new HashMap<>();
        // add vex statements and/or errors for every component
        for(Component c : sbom.getComponents()){
            if(c instanceof SBOMPackage){
                try{
                    List<VEXStatement> statements;
                    if(client.equalsIgnoreCase("nvd") && apiKey != null)
                        statements = vc.getVEXStatements((SBOMPackage) c, apiKey);
                    else
                        statements = vc.getVEXStatements((SBOMPackage) c);

                    if(!statements.isEmpty())
                        for(VEXStatement vs : statements)
                            vb.addVEXStatement(vs);
                } catch (Exception e) {
                    error.put(c.getName(), e.getMessage());
                }
            }
        }

        VEX vex = vb.build();

        // Log
        LOGGER.info("VEX /svip/sboms/vex?id=" + id + " - VEX CREATED: " + sbomFile.get().getFileName());

        // Return VEXResult
        return new ResponseEntity<>(
                new VEXResult(vex, error), HttpStatus.OK);
    }

}
