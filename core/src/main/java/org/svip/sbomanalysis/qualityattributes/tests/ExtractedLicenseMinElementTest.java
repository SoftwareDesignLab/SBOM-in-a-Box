package org.svip.sbomanalysis.qualityattributes.tests;

import org.svip.sbom.model.*;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * file: HasCreationInfoTest.java
 *
 * For an SPDX SBOM, any contain extracted licenses for the required
 * minimum fields: LicenseName, LicenseID, LicenseCrossReference
 * @author Matthew Morrison
 */
public class ExtractedLicenseMinElementTest extends MetricTest{
    public static final String TEST_NAME = "ExtractedLicenseMinElements";

    /**
     * Check all components in a given SBOM for extracted licenses not on
     * the SPDX license list
     * If an extracted license is present, check for the following fields:
     * LicenseName, LicenseID, LicenseCrossReference
     * @param sbom SBOM to test
     * @return a collection of results if any extracted licenses exist
     * in the SBOM
     */
    @Override
    public List<Result> test(SBOM sbom) {
        List<Result> results = new ArrayList<>();
        Result r;

        // for every component, check for any extracted licenses
        for (Component c : sbom.getAllComponents()) {
            Map<String, Map<String, String>> extractedLicenses = c.getExtractedLicenses();
            // skip if no extracted licenses are found for the component
            if (!extractedLicenses.isEmpty()) {
                results.addAll(checkExtractedLicenses(extractedLicenses));
            }
        }

        if (results.isEmpty()) {
            r = new Result(TEST_NAME, Result.STATUS.PASS, "No Extracted " +
                    "Licenses found in SBOM");
            r.addContext(sbom, "Extracted Licenses Minimum Elements");
            r.updateInfo(Result.Context.STRING_VALUE,
                    "No Extracted Licenses to Test");
            results.add(r);
        }

        return results;
    }

    /**
     * Given a list of extracted licenses, check that each licenses contains
     * LicenseName, LicenseID, and LicenseCrossReference
     * @param extractedLicenses the list of extracted licenses
     * @return a collection of results for each extracted license and each
     * field checked
     */
    private List<Result> checkExtractedLicenses(
            Map<String, Map<String, String>> extractedLicenses){
        List<Result> results = new ArrayList<>();
        Result r;

        // for every license in the extracted licenses list
        for(String license : extractedLicenses.keySet()){
            // test for license name
            Map<String, String> licenseValues = extractedLicenses.get(license);
            r = hasLicenseName(licenseValues.get("name"));
            r.addContext(license, "Extracted License Name");
            results.add(r);

            // test for text description
            r = hasLicenseText(licenseValues.get("text"));
            r.addContext(license, "Extracted License Text Description");
            results.add(r);

            // test for cross-reference link
            r = hasCrossRef(licenseValues.get("crossRef"));
            r.addContext(license, "Extracted License Reference Link");
            results.add(r);
        }

        return results;
    }

    /**
     * Helper function to check if License name is present and not null
     * @param licenseName the license name of the extracted license
     * @return a result of if the name is present or not
     */
    private Result hasLicenseName(String licenseName){
        Result r;
        // license name is null, then test fails
        if(isEmptyOrNull(licenseName)){
            r = new Result(TEST_NAME, Result.STATUS.FAIL, "Extracted " +
                    "License did not include license name");
            r.updateInfo(Result.Context.STRING_VALUE,
                    "License Name is Missing");
        }
        // license name is present, test passes
        else{
            r = new Result(TEST_NAME, Result.STATUS.PASS, "Extracted " +
                    "License included license name");
            r.updateInfo(Result.Context.STRING_VALUE, licenseName);
        }
        r.updateInfo(Result.Context.FIELD_NAME, "LicenseName");
        return r;
    }

    /**
     * Helper function to check if a text description is present and not null
     * @param licenseText the text description of the extracted license
     * @return a result of if the name is present or not
     */
    private Result hasLicenseText(String licenseText){
        Result r;
        // text description is null, then test fails
        if(isEmptyOrNull(licenseText)){
            r = new Result(TEST_NAME, Result.STATUS.FAIL, "Extracted " +
                    "License did not include text description");
            r.updateInfo(Result.Context.STRING_VALUE,
                    "License Description is Missing");
        }
        // text description is present, test passes
        else{
            r = new Result(TEST_NAME, Result.STATUS.PASS, "Extracted " +
                    "License included text description");
            r.updateInfo(Result.Context.STRING_VALUE, licenseText);
        }
        r.updateInfo(Result.Context.FIELD_NAME, "ExtractedText");
        return r;
    }


    /**
     * Helper function to check if reference link is present and valid
     * @param crossRef the reference link of the extracted license
     * @return a result of if the name is present or not
     */
    private Result hasCrossRef(String crossRef){
        Result r;
        // cross-reference link is null, then test fails
        if(isEmptyOrNull(crossRef)){
            r = new Result(TEST_NAME, Result.STATUS.FAIL, "Extracted " +
                    "License did not include cross reference link");
            r.updateInfo(Result.Context.STRING_VALUE,
                    "License Reference Link is Missing");
        }
        // text description is present, check if valid link
        else{
            int responseCode;
            try{
                // Query page
                URL url = new URL (crossRef);
                HttpURLConnection huc = (HttpURLConnection) url.openConnection();
                // get the response code from this url
                responseCode = huc.getResponseCode();
                huc.disconnect();
            }
            // error occurred with reference link, test automatically fails
            catch(IOException e){
                r = new Result(TEST_NAME, Result.STATUS.FAIL, "Extracted " +
                        "License reference link had an error");
                r.updateInfo(Result.Context.STRING_VALUE, crossRef);
                r.updateInfo(Result.Context.FIELD_NAME,
                        "LicenseCrossReference");
                return r;
            }

            // if the response code is 200 (HTTP_OK), then
            // link is valid
            if(responseCode == HttpURLConnection.HTTP_OK){
                r = new Result(TEST_NAME, Result.STATUS.PASS, "Extracted " +
                        "License reference link is included and valid");
                r.updateInfo(Result.Context.STRING_VALUE, crossRef);
            }
            // any other response codes result in a test fail
            else{
                r = new Result(TEST_NAME, Result.STATUS.FAIL, "Extracted " +
                        "License reference link is included but not valid");
                r.updateInfo(Result.Context.STRING_VALUE, crossRef);
            }
        }
        r.updateInfo(Result.Context.FIELD_NAME,
                "LicenseCrossReference");
        return r;
    }
}
