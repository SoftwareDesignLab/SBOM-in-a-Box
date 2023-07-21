package org.svip.sbomvex.database;



/**
 * file: NVDClient.java
 * Client class for the NVD Database
 *
 * @author Matthew Morrison
 */
import org.cyclonedx.CycloneDxSchema;
import org.json.JSONArray;
import org.json.JSONObject;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.interfaces.generics.SBOMPackage;
import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23File;
import org.svip.sbomvex.model.VEX;
import org.svip.sbomvex.model.VEXType;
import org.svip.sbomvex.vexstatement.VEXStatement;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class NVDClient {
    private static final String ENDPOINT = "https://services.nvd.nist.gov/rest/json/cves/2.0";
    private HttpClient httpClient = HttpClient.newHttpClient();
    private String buildURL(String cpe){
        return ENDPOINT.concat("?cpeName=").concat(cpe);
    }
    private String vexFields(String cpe) throws ExecutionException, InterruptedException {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(buildURL(cpe)))
                .GET()
                .build();

        CompletableFuture<HttpResponse<String>> apiResponse = httpClient
                .sendAsync(request, HttpResponse.BodyHandlers.ofString());
        return apiResponse.get().body();
    }

    public VEX generateVEX(SBOM sbom) throws ExecutionException, InterruptedException {
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
            // check that component is a CycloneDXSchema
            if(!(c instanceof CycloneDxSchema)){
                // cast to SBOMPackage to check for cpes
                SBOMPackage component = (SBOMPackage) c;
                Set<String> cpes = component.getCPEs();

                //need to change this to check if resultsPerPage == 0
                // if component has no cpes continue as we can only search if there are cpes
                if(cpes == null || cpes.isEmpty()){
                   continue;
                }
                else{
                    // use the cpes to search for vulnerabilities
                    ArrayList<String> cpesList = new ArrayList<>(cpes);
                    String cpesString = cpesList.get(0);
                    response = vexFields(cpesString);
                }

                // if jsonResponse did not have an error and is not empty,
                // create a vex statement for every vulnerability in response
//                if(response != null && !response.equals("{}")){
//                    JSONArray vulns = new JSONArray(response);
//                    for(int i=0; i<vulns.length(); i++){
//                        // get the singular vulnerability and create a
//                        // VEXStatement for it
//                        JSONObject vulnerability = vulns.getJSONObject(i);
//                        VEXStatement vexStatement =
//                                generateVEXStatement(vulnerability, component);
//                        vexBuilder.addVEXStatement(vexStatement);
//                    }
//                }
            }
        }

        return vexBuilder.build();
    }
    public void run(){
    }
}
