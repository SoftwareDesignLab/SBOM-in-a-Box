package org.svip.sbomvex.database;

import org.json.JSONArray;
import org.json.JSONObject;
import org.svip.sbom.model.interfaces.generics.SBOMPackage;
import org.svip.sbomvex.database.interfaces.VulnerabilityDBClient;
import org.svip.sbomvex.vexstatement.Product;
import org.svip.sbomvex.vexstatement.VEXStatement;
import org.svip.sbomvex.vexstatement.status.Justification;
import org.svip.sbomvex.vexstatement.Vulnerability;
import org.svip.sbomvex.vexstatement.status.Status;
import org.svip.sbomvex.vexstatement.status.VulnStatus;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * file: NVDClient.java
 * Client class for the NVD Database
 *
 * @author Matthew Morrison
 * @author Henry Lu
 */
public class NVDClient implements VulnerabilityDBClient {
    private static final String ENDPOINT = "https://services.nvd.nist.gov/rest/json/cves/2.0";
    private final HttpClient httpClient = HttpClient.newHttpClient();

    /**
     * build the url for the api request using the component's cpe
     * @param cpe the cpe string
     * @return a String of the concatenated URL string
     */
    private String buildURLWithCPE(String cpe){
        return ENDPOINT.concat("?cpeName=").concat(cpe);
    }

    /**
     * run the api request and returns the response
     * @param cpe the cpe string
     * @return the response from the NVD API
     * @throws Exception if an error occurs with getting NVD's response
     */
    private String vexFieldsWithCPE(String cpe) throws Exception{
        var request = HttpRequest.newBuilder()
                .uri(URI.create(buildURLWithCPE(cpe)))
                .GET()
                .build();

        CompletableFuture<HttpResponse<String>> apiResponse = httpClient
                .sendAsync(request, HttpResponse.BodyHandlers.ofString());
        return apiResponse.get().body();
    }

    /**
     * Get all vulnerabilities of a component using the NVD API
     * @param s the SBOM package to create the VEX Statement for
     * @return a List of VEX Statements if there are vulnerabilities or
     * throw a general Exception if none are found
     * @throws Exception if no vulnerabilities are found or an error occurs
     */
    @Override
    public List<VEXStatement> getVEXStatements(SBOMPackage s) throws Exception{
        List<VEXStatement> vexStatements = new ArrayList<>();
        String response;
        String componentID;
        Set<String> cpes = s.getCPEs();

        // if component has no cpes continue as we can only search if there are cpes
        if(cpes != null && !cpes.isEmpty()){
            // use the cpes to search for vulnerabilities
            ArrayList<String> cpesList = new ArrayList<>(cpes);
            String cpeString = cpesList.get(0);
            componentID = cpeString;
            response = vexFieldsWithCPE(cpeString);
            TimeUnit.SECONDS.sleep(6);

        }
        else{
            throw new Exception("Component does not have CPEs " +
                    "to test with NVD API");
        }

        // check that the response was not null to find vulnerabilities
        if(response != null){
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray vulns = new JSONArray(
                    jsonResponse.getJSONArray("vulnerabilities"));
            for (int i = 0; i < vulns.length(); i++) {
                // get the singular vulnerability and create a
                // VEXStatement for it
                JSONObject vulnerability = vulns.getJSONObject(i)
                        .getJSONObject("cve");
                VEXStatement statement =
                        generateVEXStatement(vulnerability,
                                s, componentID);
                vexStatements.add(statement);
            }
        }
        return vexStatements;
    }

    /**
     * Build a new VEX Statement for a VEX Document
     * @param vulnerabilityBody the vulnerability body from the APIs
     * response to turn into a VEXStatement
     * @param c component to extract info for the VEX Statement
     * @param id string used to identify VEX Statement
     * @return a new VEXStatement
     */
    private VEXStatement generateVEXStatement(JSONObject vulnerabilityBody,
                                              SBOMPackage c, String id) {
        VEXStatement.Builder statement = new VEXStatement.Builder();
        // add general fields to the statement
        statement.setStatementID(id);
        statement.setStatementVersion("1.0");
        statement.setStatementFirstIssued(vulnerabilityBody
                .getString("published"));
        statement.setStatementLastUpdated(vulnerabilityBody
                .getString("lastModified"));

        // Set the statement's vulnerability
        String vulnDesc;
        JSONArray descriptions = new JSONArray
                (vulnerabilityBody.getJSONArray("descriptions"));
        //TODO check lang field?
        JSONObject firstDesc = descriptions.getJSONObject(0);
        vulnDesc = firstDesc.getString("value");
        statement.setVulnerability(new Vulnerability(
                vulnerabilityBody.getString("id"), vulnDesc));

        //TODO no action statement provided
        //set the statement's affected status
        statement.setStatus(new Status(VulnStatus.AFFECTED,
                Justification.NOT_APPLICABLE, vulnDesc, "N/A"));

        // add component as the product of the statement
        String productID = c.getName() + ":" + c.getVersion();
        String supplier;
        if(c.getSupplier() != null){
            supplier = c.getSupplier().getName();
        }
        else{
            supplier = "Unknown";
        }
        Product product = new Product(productID, supplier);
        statement.addProduct(product);

        return statement.build();
    }
}
