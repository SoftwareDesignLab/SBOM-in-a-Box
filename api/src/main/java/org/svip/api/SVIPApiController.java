package org.svip.api;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.svip.sbom.model.SBOM;
import org.svip.sbomanalysis.comparison.Merger;
import org.svip.sbomfactory.osi.OSI;
import org.svip.sbomfactory.translators.Translator;
import org.svip.sbomvex.VEXFactory;
import org.svip.visualizer.NodeFactory;

import java.util.ArrayList;

/**
 * file: SVIP.java
 * <p>
 * Driver to launch SVIP and acts as Master Controller
 *
 * @author Derek Garcia
 * @author Kevin Laporte
 * @author Asa Horn
 * @author Justin Jantzi
 * @author Matt London
 * @author Ian Dunn
 */

@RestController
@RequestMapping("/svip")
public class SVIPApiController {


    /**
     * Http headers of Spring boot application
     */
    private HttpHeaders headers;

    /**
     * OSI docker container representation
     */
    private OSI osiContainer;

    /**
     * Default OSI Bound Directory location
     */
    private static String osiBoundDir = "src/main/java/com/svip/osi/core/bound_dir";

    /**
     * Default path to where dockerfile is located
     */
    private static String dockerPath = "/core/src/main/java/org/svip/sbomfactory/osi/Dockerfile";

    /**
     * buildOSI runs on startup to build the OSI container independent of the front-end.
     */
    @PostConstruct
    public void buildOSI() {
        // TODO: For SVIP v3, refactor to move OSI building operations into another class
        osiContainer = new OSI(osiBoundDir, dockerPath);
    }

    public SVIPApiController() {
        headers = new HttpHeaders();
        headers.add("AccessControlAllowOrigin", "http://localhost:4200");
    }

    /**
     * To be called when the object is released by the garbage collector. DO NOT CALL MANUALLY
     */
    @PreDestroy
    public void close() {
        // Close the osi container so that we delete the instance
        osiContainer.close();
    }

    /**
     * Generator a report for the given filePath
     *
     * @param filePath Path to target Directory
     * @return JSON String
     * @throws Exception Error when generating the report
     */
    public String generateReport(String filePath) throws Exception {
        // 1. osi container generates SBOMs
        if(osiContainer.generateSBOMs(filePath) != 0)
            throw new Exception("Failed to Generate SBOMs with OSI");

        // 2. call translators
        ArrayList<SBOM> SBOMs = new ArrayList<>(); // TODO: Remove redundant init
        try{
            // TODO: Add controller for file driving possibly?
//            SBOMs = Translator.parseSBOMs(osiBoundDir + "/sboms");
        } catch (Exception e){
            throw new Exception("Unable to Translate SBOMs");
        }

        // 3. Merge SBOMs
        Merger merger = new Merger();
        SBOM master = merger.merge(SBOMs);

        if(master == null)
            throw new Exception("Failed to merge SBOMs");

//        // 4. Apply Vex
//        if (NVIPUsername == null || NVIPPassword == null)
//            System.err.println("NVIP Login Credentials not set. Call setNVIPLogin() first. No VEX will be applied.");
//        else if (NVIPEndpoint == null)
//            System.err.println("NVIP Endpoint not set. Call setNVIPEndpoint() first. No VEX will be applied.");
//        else {
//            try {
//                factory.applyVex(master);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

        // 5. Convert to D3 and return result
        return new NodeFactory().CreateNodeGraphJSON(master);
    }


    /**
     * Creates a Node Graph from the master SBOM
     * and returns a JSON String representation of the Node Graph.
     *
     * @return JSON String representation of the Node Graph
     */
    @GetMapping("/sbom-node-graph")
    public ResponseEntity<String> getNodeGraph(@RequestParam String filePath) {
        try {
            String data = generateReport(filePath);
            return new ResponseEntity<>(data, headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
