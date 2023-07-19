package org.svip.sbomvex.database;

import org.json.JSONArray;
import org.json.JSONObject;
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
import java.util.concurrent.CompletableFuture;

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
            CompletableFuture<HttpResponse<String>> apiResponse = httpClient.sendAsync(request,
                    HttpResponse.BodyHandlers.ofString());
            String responseBody = apiResponse.get().body();
            return responseBody.replace("{\"vulns\":", "");
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
            String response;
            // cast to SBOMPackage to check for purls
            SBOMPackage component = (SBOMPackage) c;
            Set<String> purls = component.getPURLs();

            // if component has no purls, construct API request with
            // name and version
            if(purls == null || purls.isEmpty()){
                String name = component.getName();
                String version = component.getVersion();
                response = getOSVByNameVersionPost(name, version);
                // some components require its group and name to search
                // for vulnerabilities
                if(response == null || response.equals("{}")){
                    name = component.getAuthor() + ":" + component.getName();
                    response = getOSVByNameVersionPost(name, version);
                }
            }
            else{
                // use the purl for the component instead to search for
                // vulnerabilities
                ArrayList<String> purlList = new ArrayList<>(purls);
                String purlString = purlList.get(0);
                response = getOSVByPURLPost(purlString);
            }

            // if jsonResponse did not have an error and is not empty,
            // create a vex statement for every vulnerability in response
            if(response != null && !response.equals("{}")){
                JSONArray vulns = new JSONArray(response);
                for(int i=0; i<vulns.length(); i++){
                    // get the singular vulnerability and create a
                    // VEXStatement for it
                    JSONObject vulnerability = vulns.getJSONObject(i);
                    VEXStatement vexStatement =
                            generateVEXStatement(vulnerability, component);
                    vexBuilder.addVEXStatement(vexStatement);
                }
            }
        }

        return vexBuilder.build();
    }

    /**
     * Build a new VEX Statement for a VEX Document
     * @param vulnerabilityBody the vulnerability body from the APIs
     * response to turn into a VEXStatement
     * @return a new VEXStatement
     */
    public VEXStatement generateVEXStatement(JSONObject vulnerabilityBody, SBOMPackage c){
        VEXStatement.Builder statement = new VEXStatement.Builder();
        // add general fields to the statement
        statement.setStatementID(vulnerabilityBody.getString("id"));
        statement.setStatementVersion("1.0");
        statement.setStatementFirstIssued(vulnerabilityBody
                .getString("published"));
        statement.setStatementLastUpdated(vulnerabilityBody
                .getString("modified"));

        // Set the statement's vulnerability
        JSONArray aliases = vulnerabilityBody.getJSONArray("aliases");
        String vulnID = aliases.getString(0);
        String vulnDesc;
        if(!vulnerabilityBody.has("summary")){
            vulnDesc = vulnerabilityBody.getString("details");
        }
        else{
            vulnDesc = vulnerabilityBody.getString("summary");
        }
        statement.setVulnerability(new Vulnerability(vulnID, vulnDesc));

        //set the statement's affected status
        statement.setStatus(new Status(VulnStatus.AFFECTED,
                Justification.NOT_APPLICABLE, vulnerabilityBody
                .getString("details"), "N/A"));

        //Get all products and add all to the VEX Statement
        String supplier = c.getSupplier().getName();
        JSONArray packages = vulnerabilityBody.getJSONArray("affected");
        for(int i = 0; i<packages.length(); i++){
            JSONObject vulnPackage = packages.getJSONObject(i);
            JSONObject packageInfo = vulnPackage.getJSONObject("package");
            String packageID = packageInfo.getString("name")
                    + ":" + packageInfo.getString("ecosystem")
            + ":" + c.getVersion();
            statement.addProduct(new Product(packageID, supplier));
        }
        return statement.build();
    }
}
