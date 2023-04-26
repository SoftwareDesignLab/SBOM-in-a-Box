package svip;

import svip.sbomanalysis.comparison.Merger;
import svip.sbomvex.VEXFactory.*;
import svip.sbomfactory.osi.OSI;
import svip.sbomfactory.translators.*;
import svip.sbom.model.SBOM;
import svip.visualizer.NodeFactory;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
@CrossOrigin
@RestController
@RequestMapping("SVIP")
public class SVIPCoreController {
    /**
     * NVIP Login Credentials username
     */
    private static String NVIPUsername;
    /**
     * NVIP Login Credentials password
     */
    private static String NVIPPassword;
    /**
     * NVIP Endpoint (Entire URL without resource and arguments)
     */
    private static String NVIPEndpoint;

    /**
     * SVIP VexFactory instance
     */
    private VEXFactory factory;

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
    private static String dockerPath = "/src/main/java/com/svip/osi/core/Dockerfile";

    /**
     * buildOSI runs on startup to build the OSI container independent of the front-end.
     */
    @PostConstruct
    public void buildOSI() {
        // TODO: For SVIP v3, refactor to move OSI building operations into another class
        osiContainer = new OSI(osiBoundDir, dockerPath);
    }

    public SVIPCoreController() {
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
        ArrayList<SBOM> SBOMs;
        try{
            SBOMs = Translator.toReport(osiBoundDir + "/sboms");
        } catch (Exception e){
            throw new Exception("Unable to Translate SBOMs");
        }

        // 3. Merge SBOMs
        Merger merger = new Merger();
        SBOM master = merger.merge(SBOMs);

        if(master == null)
            throw new Exception("Failed to merge SBOMs");

        // 4. Apply Vex
        if (NVIPUsername == null || NVIPPassword == null)
            System.err.println("NVIP Login Credentials not set. Call setNVIPLogin() first. No VEX will be applied.");
        else if (NVIPEndpoint == null)
            System.err.println("NVIP Endpoint not set. Call setNVIPEndpoint() first. No VEX will be applied.");
        else {
            try {
                factory.applyVex(master);
             } catch (Exception e) {
                e.printStackTrace();
            }
        }

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

    /**
     * Sets the NVIP Login Credentials for the VEXFactory
     *
     * @param username - NVIP Username
     * @param password - NVIP Password in plain text
     */
    @PostMapping(value = "/login", params = {"username", "password"})
    public ResponseEntity<Boolean> login(@RequestParam("username") String username, @RequestParam("password") String password){
        NVIPUsername = username;
        NVIPPassword = password;

        try {
            factory = new VEXFactory(NVIPEndpoint, username, password);
            return new ResponseEntity<>(true, headers, HttpStatus.OK);
        } catch (InvalidLoginException e) {
            return new ResponseEntity<>(false, headers, HttpStatus.OK);
        }
    }

    /**
     * Sets the NVIP Endpoint for the VEXFactory. This is the bit of the URL before the specific resource.
     * For example if the URL is http://localhost:8080/nvip_ui_war_exploded/searchServelet then the endpoint is http://localhost:8080/nvip_ui_war_exploded
     * If you are getting errors ensure
     *      A. The endpoint is correct (you can connect to it in a browser)
     *      B. You have logged in to NVIP and have a valid token
     *      C. You have specified the protocol (http:// or https://)
     *
     * @param endpoint - NVIP Endpoint
     */
    @PostMapping(value = "/endpoint", params = {"endpoint"})
    public ResponseEntity<Boolean> setNVIPEndpoint(@RequestParam("endpoint") String endpoint){
        NVIPEndpoint = endpoint;
        return new ResponseEntity<>(true, headers, HttpStatus.OK);
    }
}

