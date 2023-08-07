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
import org.springframework.web.multipart.MultipartFile;
import org.svip.api.entities.SBOMFile;
import org.svip.api.repository.SBOMFileRepository;
import org.svip.api.utils.Converter;
import org.svip.api.utils.Utils;
import org.svip.sbom.builder.objects.SVIPSBOMBuilder;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.interfaces.generics.SBOMPackage;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.sbom.model.objects.SVIPSBOM;
import org.svip.compare.DiffReport;
import org.svip.merge.MergerController;
import org.svip.merge.MergerException;
import org.svip.metrics.pipelines.QualityReport;
import org.svip.metrics.pipelines.interfaces.generics.QAPipeline;
import org.svip.metrics.pipelines.schemas.CycloneDX14.CDX14Pipeline;
import org.svip.metrics.pipelines.schemas.SPDX23.SPDX23Pipeline;
import org.svip.generation.osi.OSI;
import org.svip.generation.parsers.ParserController;
import org.svip.serializers.SerializerFactory;
import org.svip.serializers.deserializer.Deserializer;
import org.svip.serializers.serializer.Serializer;
import org.svip.vex.VEXResult;
import org.svip.vex.database.NVDClient;
import org.svip.vex.database.OSVClient;
import org.svip.vex.database.interfaces.VulnerabilityDBClient;
import org.svip.vex.model.VEX;
import org.svip.vex.model.VEXType;
import org.svip.vex.vexstatement.VEXStatement;
import org.svip.generation.parsers.utils.VirtualPath;

import java.io.IOException;
import java.util.*;
import java.util.zip.ZipException;

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
 * @author Thomas Roman
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
     * @param startWithOSI       Whether to start with OSI enabled. If false, the OSI endpoint will be disabled.
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
     * Public method to check if OSI is enabled on this instance of the API controller.
     *
     * @return True if OSI is enabled, false otherwise.
     */
    public boolean isOSIEnabled() {
        return osiContainer != null;
    }


    /**
     * USAGE. Send PUT request to /sboms an existing SBOM on the backend to a desired schema and format
     *
     * @param id        of the SBOM
     * @param schema    to convert to
     * @param format    to convert to
     * @param overwrite whether to overwrite original
     * @return converted SBOM
     */
    @PutMapping("/sboms")
    public ResponseEntity<Long> convert(@RequestParam("id") long id, @RequestParam("schema") SerializerFactory.Schema schema,
                                        @RequestParam("format") SerializerFactory.Format format,
                                        @RequestParam("overwrite") Boolean overwrite) {
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
        converted.setFileName(toConvert.getFileName());

        // overwrite
        if (overwrite) {
            sbomFileRepository.delete(sbomFile.get());

            assert sbomFileRepository.findById(id).isEmpty(); // todo this assertion fails

            converted.setId(id);
            sbomFileRepository.save(converted);

            assert sbomFileRepository.findById(id).isPresent();
        } else {
            long newId = Utils.generateNewId(id, new Random(), sbomFileRepository);
            converted.setId(newId);

            assert converted.getId() == newId;
            sbomFileRepository.save(converted);

            boolean caught = false;

            try {
                sbomFileRepository.findById(newId).isEmpty();
            } catch (NullPointerException e) {
                caught = true;
            }

            assert caught;

        }


        return Utils.encodeResponse(converted.getId());
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
    @PostMapping("/generators/parsers")
    public ResponseEntity<?> generateParsers(@RequestParam("zipFile") MultipartFile zipFile,
                                             @RequestParam("projectName") String projectName,
                                             @RequestParam("schema") SerializerFactory.Schema schema,
                                             @RequestParam("format") SerializerFactory.Format format) throws IOException {

        String urlMsg = "GENERATE /svip/generate?projectName=" + projectName;

        // Ensure schema has a valid serializer
        try {
            schema.getSerializer(format);
        } catch (IllegalArgumentException e) {
            LOGGER.error(urlMsg + ": " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        ArrayList<HashMap<SBOMFile, Integer>> unZipped;
        try {
            unZipped = (ArrayList<HashMap<SBOMFile, Integer>>)
                    Utils.unZip(Objects.requireNonNull(Utils.convertMultipartToZip(zipFile)));
        } catch (ZipException e) {
            LOGGER.error(urlMsg + ":" + e.getMessage());
            return new ResponseEntity<>("Make sure attachment is a zip file (.zip): " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        HashMap<VirtualPath, String> virtualPathStringHashMap = new HashMap<>();

        for (HashMap<SBOMFile, Integer> h : unZipped
        ) {

            SBOMFile f = (SBOMFile) h.keySet().toArray()[0];

            if (!f.hasNullProperties()) // project files that are empty should just be ignored
                    virtualPathStringHashMap.put(new VirtualPath(f.getFileName()), f.getContents());

        }

        ParserController parserController = new ParserController(projectName, virtualPathStringHashMap);


        SVIPSBOM parsed;
        try {
            parserController.parseAll();
            parsed = parserController.buildSBOM(schema);
        } catch (Exception e) {
            String error = "Error parsing into SBOM: " + Arrays.toString(e.getStackTrace());
            LOGGER.error(urlMsg + " " + error);
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Serializer s;
        String contents;
        try {
            s = SerializerFactory.createSerializer(schema, format, true);
            contents = s.writeToString(parsed);
        } catch (IllegalArgumentException | JsonProcessingException e) {
            String error = "Error serializing parsed SBOM: " + Arrays.toString(e.getStackTrace());
            LOGGER.error(urlMsg + " " + error);
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        SBOMFile result = new SBOMFile(projectName + ((format == SerializerFactory.Format.JSON)
                ? ".json" : ".spdx"), contents);
        Random rand = new Random();
        result.setId(Utils.generateNewId(rand.nextLong(), rand, sbomFileRepository));
        sbomFileRepository.save(result);

        return Utils.encodeResponse(result.getId());

    }

    @PostMapping("/generators/osi")
    public ResponseEntity<?> generateOSI(@RequestParam("zipFile") MultipartFile zipFile,
                                         @RequestParam("projectName") String projectName,
                                         @RequestParam("schema") SerializerFactory.Schema schema,
                                         @RequestParam("format") SerializerFactory.Format format) {
        if (osiContainer == null)
            return new ResponseEntity<>("OSI has been disabled for this instance.", HttpStatus.NOT_FOUND);

        String urlMsg = "POST /svip/generators/osi";

        // Ensure schema has a valid serializer
        try {
            schema.getSerializer(format);
        } catch (IllegalArgumentException e) {
            LOGGER.error(urlMsg + ": " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        ArrayList<HashMap<SBOMFile, Integer>> unZipped;
        try {
            unZipped = (ArrayList<HashMap<SBOMFile, Integer>>)
                    Utils.unZip(Objects.requireNonNull(Utils.convertMultipartToZip(zipFile)));
        } catch (IOException e) {
            LOGGER.error(urlMsg + ":" + e.getMessage());
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
                if (sbom.getValue().isEmpty()) continue; // Skip blank SBOMs if any returned
                d = SerializerFactory.createDeserializer(sbom.getValue());
                deserialized.add(d.readFromString(sbom.getValue()));
            } catch (JsonProcessingException e) {
                LOGGER.warn(urlMsg + ": Exception occurred while deserializing SBOMs: " + e.getMessage());
                return new ResponseEntity<>("Exception occurred while deserializing SBOMs.",
                        HttpStatus.NOT_FOUND);
            } catch (IllegalArgumentException ignored) {
            } // TODO Skip any XML SBOMs, we don't support deserialization

        if (deserialized.size() == 0) {
            LOGGER.warn(urlMsg + ": No SBOMs generated by OSI container.");
            return new ResponseEntity<>("No SBOMs generated for these files.", HttpStatus.NO_CONTENT);
        }

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
     * USAGE. Compares two or more given SBOMs (split into filename and contents), with the first one used as the baseline, and returns a comparison report.
     *
     * @param targetIndex the index of the target SBOM
     * @param ids         the ids of the SBOM files
     * @return generated diff report
     * @throws JsonProcessingException
     */
    @PostMapping("/sboms/compare")
    public ResponseEntity<DiffReport> compare(@RequestParam("targetIndex") int targetIndex, @RequestBody Long[] ids) throws JsonProcessingException {
        // Get Target SBOM
        Optional<SBOMFile> sbomFile = sbomFileRepository.findById(ids[targetIndex]);
        // Check if it exists
        ResponseEntity<Long> NOT_FOUND = Utils.checkIfExists(ids[targetIndex], sbomFile, "/sboms/compare");
        if (NOT_FOUND != null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        // create the Target SBOM object using the deserializer
        Deserializer d = SerializerFactory.createDeserializer(sbomFile.get().getContents());
        SBOM targetSBOM = d.readFromString(sbomFile.get().getContents());
        // create diff report
        DiffReport diffReport = new DiffReport(targetSBOM.getUID(), targetSBOM);
        // comparison sboms
        for (int i = 0; i < ids.length; i++) {
            if (i == targetIndex) continue;
            // Get SBOM
            sbomFile = sbomFileRepository.findById(ids[i]);
            // Check if it exists
            NOT_FOUND = Utils.checkIfExists(ids[i], sbomFile, "/sboms/compare");
            if (NOT_FOUND != null)
                continue; // sbom not found, continue to next ID TODO check with front end what to do if 1 sbom is missing
            // create an SBOM object using the deserializer
            d = SerializerFactory.createDeserializer(sbomFile.get().getContents());
            SBOM sbom = d.readFromString(sbomFile.get().getContents());
            // add the comparison to diff report
            diffReport.compare(sbom.getUID(), sbom);
        }
        return Utils.encodeResponse(diffReport);
    }

    /**
     * Merge two existing SBOMs
     *
     * @param ids of the two SBOMs
     * @return a merged sbomFile
     */
    @PostMapping("sboms/merge")
    public ResponseEntity<?> merge(@RequestBody long[] ids) {

        ArrayList<SBOM> sboms = new ArrayList<>();

        String urlMsg = "MERGE /svip/merge?id=";

        long idSum = 0L;

        // check for bad files
        for (Long id : ids
        ) {

            // Get SBOM
            Optional<SBOMFile> sbomFile = sbomFileRepository.findById(id);

            // Check if it exists
            ResponseEntity<Long> NOT_FOUND = Utils.checkIfExists(id, sbomFile, "merge");
            if (NOT_FOUND != null) return NOT_FOUND;
            SBOMFile sbom = sbomFile.get();

            if (sbom.hasNullProperties()) {
                LOGGER.info(urlMsg + sbomFile.get().getId() + " - ERROR IN MERGE - HAS NULL PROPERTIES");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            // deserialize into SBOM object
            Deserializer d;
            SBOM deserialized;

            try {
                d = SerializerFactory.createDeserializer(sbom.getContents());
                deserialized = d.readFromString(sbom.getContents());
            } catch (Exception e) {
                LOGGER.info(urlMsg + sbomFile.get().getId() + "DURING DESERIALIZATION: " +
                        e.getMessage());
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }

            sboms.add(deserialized);
            idSum += id;
        }

        // todo, merging more than two SBOMs is not supported right now
        SBOM merged;
        try {
            MergerController mergerController = new MergerController();
            merged = mergerController.merge(sboms.get(0), sboms.get(1));
        } catch (MergerException e) {
            String error = "Error merging SBOMs: " + e.getMessage();
            LOGGER.error(urlMsg + " " + error);
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }

        Serializer s;
        String contents;
        try {
            s = SerializerFactory.createSerializer(SerializerFactory.Schema.SVIP, SerializerFactory.Format.JSON, true);
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


}
