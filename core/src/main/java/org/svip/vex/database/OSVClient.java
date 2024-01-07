/ **
* Copyright 2021 Rochester Institute of Technology (RIT). Developed with
* government support under contract 70RCSA22C00000008 awarded by the United
* States Department of Homeland Security for Cybersecurity and Infrastructure Security Agency.
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the “Software”), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
* /

package org.svip.vex.database;

import org.json.JSONArray;
import org.json.JSONObject;
import org.svip.sbom.model.interfaces.generics.SBOMPackage;
import org.svip.vex.database.interfaces.VulnerabilityDBClient;
import org.svip.vex.vexstatement.Product;
import org.svip.vex.vexstatement.VEXStatement;
import org.svip.vex.vexstatement.Vulnerability;
import org.svip.vex.vexstatement.status.Justification;
import org.svip.vex.vexstatement.status.Status;
import org.svip.vex.vexstatement.status.VulnStatus;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * file: OSVClient.java
 * Client class for OSV database
 *
 * @author Matthew Morrison
 */
public class OSVClient implements VulnerabilityDBClient {

    /**
     * url endpoint to access OSV database POST methods
     */
    private final String POST_ENDPOINT = "https://api.osv.dev/v1/query";

    private final HttpClient httpClient = HttpClient.newHttpClient();

    /**
     * Build the API Request, then get the OSV database's response
     *
     * @param jsonBody the string body of the request or OSV ID for GET
     * @return OSV APIs response to the HttpRequest
     * @throws Exception if an error occurs with getting OSV's response
     */
    private String getOSVResponse(String jsonBody) throws Exception {
        HttpRequest request;
        // build the post method
        request = HttpRequest.newBuilder()
                .uri(URI.create(POST_ENDPOINT))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        // send the response and get the APIs response
        CompletableFuture<HttpResponse<String>> apiResponse = httpClient
                .sendAsync(request, HttpResponse.BodyHandlers.ofString());
        String responseBody = apiResponse.get().body();

        // check if error code appeared with response and throw error if true
        JSONObject jsonObject = new JSONObject(responseBody);
        if (jsonObject.has("code") && jsonObject.getInt("code") == 3) {
            throw new Exception("Invalid call to OSV API");
        }
        return responseBody.replace("{\"vulns\":", "");
    }


    /**
     * Generate the json body for the API request using the component's
     * name and version
     *
     * @param componentName    the component's name
     * @param componentVersion the component's version
     * @return the response from the OSV API request
     */
    private String getOSVByNameVersionPost(String componentName, String componentVersion) throws Exception {
        JSONObject body = new JSONObject();
        body.put("version", componentVersion);
        JSONObject packageJSON = new JSONObject();
        packageJSON.put("name", componentName);
        body.put("package", packageJSON);
        return getOSVResponse(body.toString());
    }


    /**
     * Generate the json body for the API request with the component's PURL
     *
     * @param purlString the component's purl
     * @return the response from the OSV API request
     */
    private String getOSVByPURLPost(String purlString) throws Exception {
        JSONObject body = new JSONObject();
        JSONObject packageJSON = new JSONObject();
        packageJSON.put("purl", purlString);
        body.put("package", packageJSON);
        return getOSVResponse(body.toString());
    }

    /**
     * Get all vulnerabilities of a component using the OSV API
     *
     * @param s the SBOM package to create the VEX Statement for
     * @return a List of VEX Statements if there are vulnerabilities or
     * throw a general Exception if none are found
     * @throws Exception if no vulnerabilities are found or an error occurs
     */
    @Override
    public List<VEXStatement> getVEXStatements(SBOMPackage s) throws Exception {
        List<VEXStatement> vexStatements = new ArrayList<>();
        String response;
        // check that component is not an SPDX23File, as it does not
        // have the necessary fields to search for vulnerabilities
        // cast to SBOMPackage to check for purls
        Set<String> purls = s.getPURLs();
        // check if the component has purls to test
        if (purls != null && purls.size() >= 1) {
            // use the purl for the component instead to search for
            // vulnerabilities
            ArrayList<String> purlList = new ArrayList<>(purls);
            String purlString = purlList.get(0);
            response = getOSVByPURLPost(purlString);
        }
        // if component has no purls, construct API request with
        // name and version
        else if (s.getName() != null && s.getVersion() != null) {
            String name = s.getName();
            String version = s.getVersion();
            response = getOSVByNameVersionPost(name, version);
            // some components require its group and name to search
            // for vulnerabilities
            if (response.equals("{}")) {
                name = s.getAuthor() + ":" + s.getName();
                response = getOSVByNameVersionPost(name, version);
            }
        } else {
            throw new Exception("Component does not have necessary fields " +
                    "to test with OSV API");
        }
        // if jsonResponse did not have an error and is not empty,
        // create a vex statement for every vulnerability in response
        if (!response.equals("{}")) {
            JSONArray vulns = new JSONArray(response);
            for (int i = 0; i < vulns.length(); i++) {
                // get the singular vulnerability and create a
                // VEXStatement for it
                JSONObject vulnerability = vulns.getJSONObject(i);
                VEXStatement vexStatement =
                        generateVEXStatement(vulnerability, s);
                vexStatements.add(vexStatement);
            }
        }
        return vexStatements;
    }

    //Not used in OSV, so it is ignored and not used
    @Override
    public List<VEXStatement> getVEXStatements(SBOMPackage s, String key) throws Exception {
        return null;
    }


    /**
     * Build a new VEX Statement for a VEX Document
     *
     * @param vulnerabilityBody the vulnerability body from the APIs
     *                          response to turn into a VEXStatement
     * @param c                 component to extract info for the VEX Statement
     * @return a new VEXStatement
     */
    private VEXStatement generateVEXStatement(JSONObject vulnerabilityBody, SBOMPackage c) {
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
        // check if summary key is in json object
        // if not default to using details key
        if (!vulnerabilityBody.has("summary")) {
            vulnDesc = vulnerabilityBody.getString("details");
        } else {
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
        // for every package in the JSONArray
        for (int i = 0; i < packages.length(); i++) {
            JSONObject vulnPackage = packages.getJSONObject(i);
            // extract the package's info and create a new Product
            JSONObject packageInfo = vulnPackage.getJSONObject("package");
            String packageID = packageInfo.getString("name")
                    + ":" + packageInfo.getString("ecosystem")
                    + ":" + c.getVersion();
            statement.addProduct(new Product(packageID, supplier));
        }
        return statement.build();
    }
}
