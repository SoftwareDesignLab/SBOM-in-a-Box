package org.svip.sbomvex;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.svip.sbom.model.*;
import org.svip.sbomvex.model.*;
import org.apache.http.HttpException;

import java.io.IOException;
import java.net.HttpRetryException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpConnectTimeoutException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;


/**
 * file: NVIPQuery
 * <p>
 * Generates API requests to NVIP (National Vulnerability Intelligence Program) to gather vulnerability information
 * associated with a CPE. This information is then translated into internal "Vulnerability" objects.
 *
 * @author Asa Horn
 **/
public class VEXFactory {

    /**
     * Holds the API used to preform searches
     */
    private String searchAPI = "searchServlet";

    /**
     * Holds the API used to log in
     */
    private String loginAPI = "loginServlet";

    /**
     * Holds the URL used to query the NVIPEndpoint
     */
    private final String NVIPEndpoint;

    /**
     * Holds the number of times to attempt to retry when receiving http retry exception or nonfatal http exception
     */
    private static final int CONNECTION_ATTEMPTS = 5;

    /**
     * Holds the client used to connect to NVIP
     */
    private HttpClient connection;

    /**
     * Holds the token used to authenticate with NVIP
     */
    private String token;

    /**
     * Holds the username associated with our token
     */
    private String username;

    /**
     * constructor which adds a dummy endpoint URL. For testing use only. Please use the other constructor elsewhere.
     */
    protected VEXFactory() {
        NVIPEndpoint = "https://thisWillNotResolve.invalid";
        //if calling this constructor, you must call doLogin() before using this class for any real lookups. It is
        //intended for testing
    }

    /**
     * constructor which allows for custom NVIP endpoint URL
     *
     * @param endpoint - URL queries are sent to.
     * @param username - username used to log in to NVIP
     * @param password - plain text password used to log in to NVIP
     */
    public VEXFactory(String endpoint, String username, String password) throws InvalidLoginException{
        NVIPEndpoint = endpoint;
         if (doLogin(username, password) == -1)
             System.err.println("Incorrect login:");
    }

    /**
     * constructor which allows for custom NVIP endpoint URL
     *
     * @param endpoint - URL queries are sent to.
     * @param username - username used to log in to NVIP
     * @param password - plain text password used to log in to NVIP
     * @param searchAPI - API used to preform searches
     * @param loginAPI - API used to log in
     */
    public VEXFactory(String endpoint, String username, String password, String searchAPI, String loginAPI) throws InvalidLoginException {
        NVIPEndpoint = endpoint;
        doLogin(username, password);

        if(searchAPI != null)
            this.searchAPI = searchAPI;
        if(loginAPI != null)
            this.loginAPI = loginAPI;
    }

    /**
     * Takes a SBOM and goes through every component, adding vulnerabilities in NVIP.
     * This version throws exceptions when an error occurs instead of printing them.
     *
     * @param sbom - the main SBOM to be populated with vulnerabilities
     */
    public void applyVexWithExceptions(SBOM sbom) throws HttpException{
        for (Component component : sbom.getAllComponents()) {
            try{
                applyVexToComponentWithExceptions(component);
            } catch (HttpException e) {
                //if we get a 500 try other things. Otherwise, assume something is wrong on our end and give up
                if(e.getMessage().contains("500")){
                    System.err.println("500 error occurred while looking up vulnerabilities for " + component.getCpes());
                    System.err.println("Skipping this component");
                } else {
                    throw e;
                }
            }
        }
    }

    /**
     * Takes a SBOM and goes through every component, adding vulnerabilities in NVIP.
     * This version does not throw errors, instead printing them to stderr.
     * Note that this method can still throw unchecked exceptions if it is used improperly, but should not halt for any HTTP weirdness.
     *
     * @param sbom - the main SBOM to be populated with vulnerabilities
     */
    public void applyVex(SBOM sbom){
        try {
            applyVexWithExceptions(sbom);
        } catch (HttpException e) {
            e.printStackTrace();
        }
    }

    /**
     * queries NVIP about the CPE associated with the component and adds vulnerabilities to the component
     * This version does not throw errors, instead printing them to stderr.
     * Note that this method can still throw unchecked exceptions if it is used improperly, but should not halt for any HTTP weirdness.
     *
     * @param component - the component to be populated with vulnerabilities
     */
    public void applyVexToComponent(Component component){
        try {
            applyVexToComponentWithExceptions(component);
        } catch (HttpException e) {
            e.printStackTrace();
        }
    }

    /**
     * method NVIPQuery: queries NVIP about the CPE associated with the component and adds vulnerabilities to the component
     *
     * @param component - the component to populate with vulnerabilities
     */
    public void applyVexToComponentWithExceptions(Component component) throws HttpException {
        ArrayList<Vulnerability> vulnerabilities;
        if(component.getCpes() == null)
            // TODO alert the user that no CPE could be found, but that does not mean there are no vulnerabilities
            // throwing an error here down the road would be useful and we can catch it and transmit it to the frontend
            return; //nothing to do if no CPEs

        for (String id : component.getCpes()) {
            try {
                vulnerabilities = lookupSingleId(id);
            } catch (HttpException e) {
                System.err.println("Error occurred while looking up vulnerabilities for " + component);
                throw e;
            }

            for(Vulnerability newVEX : vulnerabilities) {
                //add the CPEs
                boolean unique = true;
                if (newVEX.getCveId() != null) { //if we don't have a CVE ID, we can't check for duplicates. Just add it
                    for (Vulnerability vuln : component.getVulnerabilities()) {
                        if (vuln.getCveId().equals(newVEX.getCveId())) {
                            unique = false;
                            break;
                        }
                    }
                }
                if (unique)
                    component.addVulnerability(newVEX);
            }
        }
    }

    /**
     * Queries NVIP with the ID of the component to get all vulnerabilities of the component.
     *
     * @param id - id of the component
     * @return - arraylist of vulnerabilities
     * @throws HttpException - if error is returned from NVIP endpoint
     */
    private ArrayList<Vulnerability> lookupSingleId(String id) throws HttpException {
        //check that endpoint, username, and token are set
        if (NVIPEndpoint == null) {
            throw new InvalidNVIPSettingsException("NVIPEndpoint is not set");
        } else if (username == null){
            throw new InvalidNVIPSettingsException("username is not set. Call doLogin() first");
        } else if (token == null){
            throw new InvalidNVIPSettingsException("token is not set. Call doLogin() first");
        }

        //query the NVIP endpoint with the name of the manufacturer of the component
        ArrayList<String> params = new ArrayList<>();
        params.add("product"); params.add(id);
        params.add("limitCount"); params.add("300");

        HttpResponse<String> response = null;
        try {
            response = doHttpRequest(searchAPI, username, token, params);
        } catch (Exception e){
            System.err.println("Error occurred while looking up vulnerabilities for " + id);
            e.printStackTrace();
        }

        //basic error handling
        if (response == null) {
            //something bad happened and could not be resolved. give up
            throw new HttpException("Error occurred while looking up vulnerabilities for " + id + ". Empty response returned from NVIP");
        } else if (response.body().equals("")) {
            //no vulnerabilities were found. Just return, there is nothing to add
            return new ArrayList<>();
        } else if (response.statusCode() != 200) {
            //something went wrong
            throw new HttpException("Error occurred while looking up vulnerabilities for " + id + ". " + response.statusCode() + " returned from NVIP");
        }

        //we have vulnerabilities to add
        ObjectMapper jacksonMapper = new ObjectMapper();
        ArrayList<Vulnerability> VEXObjects = new ArrayList<>();
        try {
            //turn the JSON into an array list of hash maps (because the response should be an array of JSON objects)
            ArrayList<LinkedHashMap> vulnerabilityList = jacksonMapper.readValue(response.body(), ArrayList.class);

            vulnerabilityList.remove(vulnerabilityList.size() - 1); //remove the end element which is the size of JSON array not data
            for (LinkedHashMap vulnerabilityMap : vulnerabilityList) {
                Vulnerability newVEX =
                        new Vulnerability(
                                vulnerabilityMap.get("vulnId").toString(),
                                vulnerabilityMap.get("cveId").toString(),
                                vulnerabilityMap.get("description").toString(),
                                vulnerabilityMap.get("platform").toString(),
                                null, // not returned by NVIP SQL query
                                vulnerabilityMap.get("publishedDate").toString(),
                                null, //not returned by NVIP SQL query
                                vulnerabilityMap.get("lastModifiedDate").toString(),
                                vulnerabilityMap.get("fixDate").toString(),
                                (Boolean) vulnerabilityMap.get("existInMitre"),
                                (Boolean) vulnerabilityMap.get("existInNvd"),
                                (int) vulnerabilityMap.get("timeGapNvd"),
                                (int) vulnerabilityMap.get("timeGapMitre"),
                                -1, //(int)vulnerabilityMap.get("statusId"),
                                "SVIP-VEX", //VEX type
                                "SVIP Auto-Generated", //Author
                                "N/A", //authorRole
                                id,
                                vulnerabilityMap.get("status").toString()
                        );
                VEXObjects.add(newVEX);
            }
        } catch (JsonProcessingException e) {
            System.err.println("Error processing JSON response from NVIP (was the response valid JSON?)");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unknown error occurred while processing JSON response from NVIP");
            e.printStackTrace();
        }

        return VEXObjects;
    }

    /**
     * method doHttpRequest: sends a get request to the NVIP endpoint
     *
     * @param resource - what resource to request from the endpoint
     * @param username - username to use for authentication (can be null)
     * @param token    - token to use for authentication (can be null)
     * @param params   - parameters to add to the query as http get parameters (google.com?param1=value1&param2=value2)
     * @return - the response from the endpoint as a HttpResponse object
     */
    protected HttpResponse<String> doHttpRequest(String resource, String username, String token, ArrayList<String> params) throws HttpException {
        //Error checking and handling
        if (connection == null) {
            makeConnection();
        } else if(NVIPEndpoint.equals("https://thisWillNotResolve.invalid")){
            throw new InvalidNVIPSettingsException("NVIPEndpoint is set to debug URL. Use mock networking classes for tests, or the other constructor to set the endpoint");
        } else if(resource.equals(searchAPI) && token == null) {
            System.err.println("WARN: Unauthenticated use of searchServlet is not allowed. Call doLogin() first");
        }

        HttpRequest request = buildRequest(resource, username, token, params);

        //send the request
        HttpResponse<String> response = null;
        for(int i = 0; i< CONNECTION_ATTEMPTS; i++){
            try {
                response = connection.send(request, HttpResponse.BodyHandlers.ofString());
                break;
            } catch (HttpRetryException e) {
                if(i >= CONNECTION_ATTEMPTS -1){
                    throw new HttpException("NVIPQuery/doQuery: Error, retry limit reached", e);
                }
            } catch (HttpConnectTimeoutException e) {
                //is the URL valid?
                throw new HttpException("NVIPQuery/doQuery: Error, connection timed out. NVIP could not be contacted at the URL " + NVIPEndpoint, e);
            } catch (IOException e) {
                //do you have internet?
                throw new HttpException("NVIPQuery/doQuery: Error, connection failed. Check adapter settings", e);
            } catch (Exception e) {
                //something else went wrong
                if(i >= CONNECTION_ATTEMPTS -1) {
                    throw new HttpException("NVIPQuery/doQuery: Error, unknown error", e);
                }
            }
        }
        if(response == null) {
            throw new HttpException("NVIPQuery/doQuery: Error, NVIP did not respond with information");
        }

        //error handling
        doErrorHandling(response); //throws all kinds of http errors if you are looking for where those are coming from
        return response;
    }

    /**
     * method makeConnection: builds the http client used to connect to the NVIP endpoint and sets the connection attribute
     */
    protected void makeConnection() {
        //build the connection
        connection = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .build();
    }

    /**
     * @param response - Http response to check for errors
     *
     * @throws HttpException - if an error is found
     */
    protected void doErrorHandling(HttpResponse<String> response) throws HttpException {
        if(response.statusCode() / 100 == 2){
            //success
            return;
        }

        //something went wrong
        switch (response.statusCode()) {
            case 401:
                //authentication failed
                //this should probably be caught so the user can be asked for their password again. Then call doLogin and retry the query
                throw new HttpException ("NVIPQuery/doQuery: 401 Error, please log in");
            case 403:
                //forbidden
                throw new HttpException("NVIPQuery/doQuery: 403 Error, you do not have access to this resource");
            case 404:
                //resource not found
                throw new HttpException("NVIPQuery/doQuery: 404 Error, resource not found / invalid login"); //also caused by incorrect login
            case 500:
                //internal server error
                throw new HttpException("NVIPQuery/doQuery: 500 Error, NVIP internal server error");
            default:
                //something else went wrong
                throw new HttpException("NVIPQuery/doQuery: Error, unknown error " + response.statusCode() + " " + response.body());
        }
    }

    /**
     * method buildRequest: builds the http request to send to the NVIP endpoint
     * @param resource - what resource to request from the endpoint (eg /searchServlet)
     * @param username - username to use for authentication
     * @param token - authentication token
     * @param params - extra parameters to add to the query
     * @return - the URI
     */
    protected HttpRequest buildRequest(String resource, String username, String token, ArrayList<String> params) throws HttpException{
        //fix issues with /s in the URL
        if(resource.startsWith("/")){
            resource = resource.substring(1);
        }
        String uri;
        if(NVIPEndpoint.endsWith("/")){
            //this way is technically what I want, if that means anything, but I figure it should work with both.
            uri = NVIPEndpoint + resource;
        } else{
            uri = NVIPEndpoint + '/' + resource;
        }


        boolean firstKey = true;

        //build up the URI
        if (username != null) {
            uri += (firstKey? '?':'&') + "username=" + urlSafe(username);
            firstKey = false;
        }
        if (token != null) {
            uri += (firstKey? '?':'&') + "token=" + urlSafe(token);
            firstKey = false;
        }
        if (resource.equals(searchAPI)) {
            uri += (firstKey? '?':'&') + "keyword=" + ""; //for some reason the API needs keyword to be set to do anything
            firstKey = false;
        }


        //add the rest of the parameters in key value pairs
        if(params != null) {
            if(params.size() % 2 != 0) {
                throw new IllegalArgumentException("NVIPQuery/buildRequest: Error, params must be in key value pairs. An odd number of params was passed.");
            }

            for(int i=0; i<params.size(); i+=2){
                //if the parameter contains a & or = it is highly likely the user is trying to build their own URL parameter which will break this. This doesn't block because there is a chance there is just a & or = in the key or value.
                if(params.get(i).contains("=") || params.get(i).contains("&")) {
                    System.err.println("Warn: Passed parameter with & or = to NVIPQuery/buildRequest. The builder will handle adding URL parameters for you. Ignore this if a key or value actually contains & or =");
                    System.err.println("Parameter " + i + ": " + params.get(i));
                } else if(params.get(i+1).contains("=") || params.get(i+1).contains("&")) {
                    System.err.println("Warn: Passed parameter with & or = to NVIPQuery/buildRequest. The builder will handle adding URL parameters for you. Ignore this if a key or value actually contains & or =");
                    System.err.println("Parameter " + i+1 + ": " + params.get(i));
                }

                //actually do the building
                uri += (firstKey? '?':'&') + urlSafe(params.get(i)) + "=" + urlSafe(params.get(i+1));
                firstKey = false;
            }
        }

        //build the request
        try {
            return HttpRequest.newBuilder()
                    .uri(URI.create(uri))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();
        } catch (IllegalArgumentException e){
            if(e.getMessage().contains("URI with undefined scheme")){
                throw new HttpException("NVIPQuery/buildRequest: Invalid scheme. This means you forgot to specify your protocol (eg. http:// or https://)");
            }
            throw e;
        }
    }

    /**
     * method doLogin: sets the username and token to use for authentication
     *
     * @param username - username to use for authentication
     * @param password - password to use for authentication
     * @return - 0 if successful, 1 if the username or password is incorrect (or bad resource), -1 for unknown
     */
    public int doLogin(String username, String password) throws InvalidLoginException{
        //hash the password
        //In the current version of NVIP it says it wants a password hash but actually wants the plain text password
        //this might change in the future, and if it does: Hi! put the hashing here, they are currently using
        //PBKDF2WithHmacSHA512 and the code is in /src/main/java/dao/userDAO hashPassword() Not sure how you are going
        //to get the salt. Good luck with that...

        //query the login api
        ArrayList<String> params = new ArrayList<>();
        params.add("userName"); params.add(username); //need to do it this way because the two servlets take different capitalization of userN/name
        params.add("passwordHash"); params.add(password); //again not actually hashed because NVIP reasons

        HttpResponse<String> response = null;
        try {
            response = doHttpRequest(loginAPI, null, null, params);
        } catch (HttpException e) {
            if (e.getMessage().contains("404")) {
                throw new InvalidLoginException("NVIPQuery/doLogin: Error. Either credentials for user " + username + " were rejected by remote server @ " +
                        NVIPEndpoint + "/loginServlet or the login resource is unavailable. Check the URL and credentials"); //todo
            } else {
                //unknown error
                System.err.println("Unknown error occurred while logging in to NVIP");
                e.printStackTrace();
            }
        }

        String bodyString = response.body();
        int indexOfToken = bodyString.indexOf("token");
        token = bodyString.substring(indexOfToken + 8, indexOfToken + 136); //136 is 128 (length of token) + 8 (length of "token":")
        ObjectMapper jacksonMapper = new ObjectMapper();
        try {
            LinkedHashMap userList = jacksonMapper.readValue(response.body(), LinkedHashMap.class);

            token = userList.get("token").toString();

        } catch (JsonProcessingException e) {
            System.err.println("Error processing JSON response from NVIP (was the response valid JSON?)");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unknown error occurred while processing JSON response from NVIP");
            e.printStackTrace();
        }

        //set the username and token
        this.username = username;

        return 0;
    }

    /**
     * method setSearchAPI: sets the API to use for searching. This is just the resource on the endpoint not the entire URL
     *
     * @param searchAPI - string to set the searchAPI to
     */
    public void setSearchAPI(String searchAPI) {
        this.searchAPI = searchAPI;
    }

    /**
     * method setLoginAPI: sets the API to use for logging in. This is just the resource on the endpoint not the entire URL
     *
     * @param loginAPI - string to set the searchAPI to
     */
    public void setLoginAPI(String loginAPI) {
        this.loginAPI = loginAPI;
    }

    /**
     * method urlSafe: encodes a string to be safe to use in a URL
     *
     * @param dangerous - string to encode
     * @return - encoded string
     */
    protected String urlSafe(String dangerous) {
        return URLEncoder.encode(dangerous, StandardCharsets.UTF_8);
    }

    /**
     * method getToken: returns the token used for authentication. Intended for use in testing.
     *
     * @return token
     */
    protected String getToken() {
        return token;
    }

    /**
     * method setToken: sets the token used for authentication. Intended for use in testing.
     *
     * @param token - token to set
     */
    protected void setToken(String token) {
        this.token = token;
    }

    /**
     * method getUsername: returns the username used for authentication. Intended for use in testing.
     *
     * @return token
     */
    protected String getUsername() {
        return username;
    }

    /**
     * method setUsername: sets the username used for authentication. Intended for use in testing.
     *
     * @param username - username to set
     */
    protected void setUsername(String username) {
        this.username = username;
    }

    /**
     * Exception invalidLoginException
     *
     * Is thrown when a login attempt to NVIP is rejected by NVIP
     */
    public static class InvalidLoginException extends Exception {
        public InvalidLoginException(String message) {
            super(message);
        }
    }

    /**
     * unchecked exception for when parameters are not valid
     */
    public static class InvalidNVIPSettingsException extends RuntimeException {
        public InvalidNVIPSettingsException(String message) {
            super(message);
        }
    }
}

