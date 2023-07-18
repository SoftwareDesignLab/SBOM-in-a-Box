package org.svip.sbomvex.database;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.interfaces.generics.SBOMPackage;
import org.svip.sbomvex.database.interfaces.VulnerabilityDBClient;
import org.svip.sbomvex.model.VEX;
import org.svip.sbomvex.model.VEXType;
import org.svip.sbomvex.vexstatement.Product;
import org.svip.sbomvex.vexstatement.VEXStatement;
import org.svip.sbomvex.vexstatement.status.Justification;
import org.svip.sbomvex.vexstatement.status.Status;
import org.svip.sbomvex.vexstatement.Vulnerability;
import org.svip.sbomvex.vexstatement.status.VulnStatus;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Set;

/**
 * file: OSVClient.java
 * Client class for OSV database
 *
 * @author Matthew Morrison
 */
public class OSVClient implements VulnerabilityDBClient {

    /**url endpoint to access OSV database POST methods*/
    private final String POST_ENDPOINT = "https://api.osv.dev/v1/query";

    /**url endpoint to access OSV database GET method*/
    private final String GET_ENDPOINT = "https://api.osv.dev/v1/vulns/";

    HttpClient httpClient = HttpClient.newHttpClient();

    /**
     * Build the API Request, then get the OSV database's response
     * @param jsonBody the string body of the request or OSV ID for GET
     * @param requestMethod the type of request method to differentiate
     * the builder
     * @return OSV APIs response to the HttpRequest
     */
    private String getOSVResponse(String jsonBody, String requestMethod) {
        try {
            HttpRequest request;
            // build the post method, most requests will follow this
            if(requestMethod.equals("post")){
                request = HttpRequest.newBuilder()
                        .uri(URI.create(POST_ENDPOINT))
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody)).build();
            }
            // only one request uses get, specific to OSV ID
            else{
                String getVulnsByID = GET_ENDPOINT + jsonBody;
                request = HttpRequest.newBuilder()
                        .uri(URI.create(getVulnsByID)).GET().build();
            }
            // send the response and get the APIs response
            HttpResponse<String> apiResponse = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());
            String responseBody = apiResponse.body();

            if(apiResponse.statusCode() == 200){
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(responseBody);
            }
            else{
                return null;
            }
        }
        // error with the HttpRequest occurs
        catch (Exception e) {
            return null;
        }
    }


    /**
     * Generate the json body for the API request using the component's
     * name and version
     * @param componentName the component's name
     * @param componentVersion the component's version
     * @return the response from the OSV API request
     */
    public String getOSVByNameVersionPost(String componentName, String componentVersion) {
        String jsonBody = "{\"version\": \"" + componentVersion +
        "\", \"package\": {\"name\": \"" +
                componentName + "\"}}";
        return getOSVResponse(jsonBody, "post");
    }

    /**
     * Generate the json body for the API request using the component's
     * name, version, and ecosystem
     * @param componentName the component's name
     * @param componentVersion the component's version
     * @param ecosystem the component's ecosystem
     * @return the response from the OSV API request
     */
    public String getOSVByNameVersionEcosystemPost(
            String componentName, String componentVersion, String ecosystem) {
        String jsonBody = "{\"version\": \"" +
                componentVersion + "\", \"package\": {\"name\": \"" +
                componentName + "\", \"ecosystem\": " +   "\"" +
                ecosystem + "\"}}";
        return getOSVResponse(jsonBody, "post");
    }

    /**
     * Generate the json body for the API request with the component's PURL
     * @param purlString the component's purl
     * @return the response from the OSV API request
     */
    public String getOSVByPURLPost(String purlString) {
        String jsonBody = "{\"package\": {\"purl\": \"" +
                purlString + "\"}} ";
        return getOSVResponse(jsonBody, "post");
    }

    /**
     * Generate the json body for the API request with the most
     * recent commit of the component
     * @param commitString the commit string
     * @return the response from the OSV API request
     */
    public String getOSVByCommitPost(String commitString) {
        String jsonBody = "{\"commit\": \"" +
                commitString + "\"} ";
        return getOSVResponse(jsonBody, "post");
    }

    /**
     * Find a vulnerability through its OSV ID
     * @param osvID the OSV ID to search for
     * @return the response from the OSV API request
     */
    public String getVulnByIdGet(String osvID){
        return getOSVResponse(osvID, "get");
    }


    /**
     * Generate a new VEX document
     * @param sbom the SBOM to create the VEX Document
     * @return a new VEX object
     */
    @Override
    public VEX generateVEX(SBOM sbom) {
        // create vex builder to create VEX document
        VEX.Builder vexBuilder = new VEX.Builder();

        // initially get the necessary fields for the VEX Document builder
        String creationTime = String.valueOf(java.time.LocalDateTime.now());
        vexBuilder.setVEXIdentifier(sbom.getName());

        // TODO better way to determine origin type and spec version?
        String sbomFormat = sbom.getFormat().toLowerCase();
        if(sbomFormat.contains("cyclonedx")){
            vexBuilder.setOriginType(VEXType.CYCLONE_DX);
            vexBuilder.setSpecVersion("1.4");
        }
        else{
            vexBuilder.setOriginType(VEXType.CSAF);
            vexBuilder.setSpecVersion("2.0");
        }
        vexBuilder.setDocVersion("1.0");
        vexBuilder.setTimeFirstIssued(creationTime);
        vexBuilder.setTimeLastUpdated(creationTime);

        for(Component c : sbom.getComponents()){
            String jsonResponse;
            // cast to SBOMPackage to check for purls
            SBOMPackage component = (SBOMPackage) c;
            Set<String> purls = component.getPURLs();

            // if component has no purls, construct API request with
            // name and version
            if(purls.isEmpty()){
                String name = component.getName();
                String version = component.getVersion();
                jsonResponse = getOSVByNameVersionPost(name, version);
            }
            else{
                ArrayList<String> purlList = new ArrayList<>(purls);
                String purlString = purlList.get(0);
                jsonResponse = getOSVByPURLPost(purlString);
            }

            // if jsonResponse did not have an error and is not empty,
            // create a vex statement for every vulnerability in response
            if(jsonResponse != null && !jsonResponse.equals("{}")){
                //TODO extract info from response to get VEX Statements


                VEXStatement vexStatement = generateVEXStatement();
                vexBuilder.addVEXStatement(vexStatement);
            }

        }

        return vexBuilder.build();
    }


    public VEXStatement generateVEXStatement(){
        return null;
    }

    public Status createAffectedStatus(String actionStatement){
        return new Status(VulnStatus.AFFECTED, null, actionStatement,
                null);
    }

    public Status createNotAffectedStatus(Justification justification, String impactStatement){
        return new Status(VulnStatus.NOT_AFFECTED, justification, null,
                impactStatement);
    }

    public Product createProduct(String productID, String supplier){
        return new Product(productID, supplier);
    }

    public Vulnerability createVulnerability(String id, String description){
        return new Vulnerability(id, description);
    }
}
