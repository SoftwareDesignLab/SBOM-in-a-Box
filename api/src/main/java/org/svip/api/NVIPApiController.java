package org.svip.api;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.svip.sbomvex.VEXFactory;

/**
 * API Controller for handling requests to NVIP
 *
 * @author Derek Garcia
 **/

public class NVIPApiController {

    // todo remove values to make API not stateful
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
     * Sets the NVIP Login Credentials for the VEXFactory
     *
     * @param username - NVIP Username
     * @param password - NVIP Password in plain text
     */
    @PostMapping(value = "/login", params = {"username", "password"})
    public ResponseEntity<Boolean> login(@RequestParam("username") String username, @RequestParam("password") String password){
        NVIPUsername = username;
        NVIPPassword = password;
        // set headers todo temp
        HttpHeaders headers = new HttpHeaders();
        headers.add("AccessControlAllowOrigin", "http://localhost:4200");
        try {

            factory = new VEXFactory(NVIPEndpoint, username, password);
            return new ResponseEntity<>(true, headers, HttpStatus.OK);
        } catch (VEXFactory.InvalidLoginException e) {
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
        HttpHeaders headers = new HttpHeaders();
        headers.add("AccessControlAllowOrigin", "http://localhost:4200");
        return new ResponseEntity<>(true, headers, HttpStatus.OK);
    }
}
