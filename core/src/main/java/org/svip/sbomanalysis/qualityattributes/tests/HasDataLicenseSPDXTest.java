package org.svip.sbomanalysis.qualityattributes.tests;

import org.nvip.plugfest.tooling.sbom.SBOM;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * file: HasDataLicenseSPDXTest.java
 *
 * For an SPDX SBOM, test that the SBOM Metadata contains the
 * DataLicense field with a license of CC0-1.0
 * @author Matthew Morrison
 */
public class HasDataLicenseSPDXTest extends MetricTest{

    private static final String TEST_NAME = "HasDataLicenseSPDX";

    /**
     * Test the SPDX SBOM Metadata to see if it contains a data license of
     * CC0-1.0
     * @param sbom SBOM to test
     * @return The result of checking for the SBOM's data license
     */
    @Override
    public List<Result> test(SBOM sbom) {
        List<Result> result = new ArrayList<>();
        Result r;

        // get the metadata from the sbom
        Map<String, String> metadata = sbom.getMetadata();

        // if metadata is present in the SBOM
        if(metadata.size() >= 1){
            // continue to hasDataLicense and get a result
            r = hasDataLicense(metadata);
            r.addContext(sbom, "DataLicense");
            result.add(r);
        }
        // metadata is not present in the SBOM, test automatically fails
        else{
            r = new Result(TEST_NAME, Result.STATUS.FAIL, "SBOM does not " +
                    "contain metadata, so no DataLicense included in SBOM");
            r.addContext(sbom, "Metadata DataLicense");
            r.updateInfo(Result.Context.STRING_VALUE,
                    "No DataLicense is in SBOM");
            result.add(r);
        }
        return result;
    }

    /**
     * Helper function to check for Data License in SBOM when metadata is
     * confirmed to be present
     * @param metadata the map of values in the SBOM's metadata
     * @return the result of if the metadata contains the correct
     * DataLicense of CC0-1.0
     */
    private Result hasDataLicense(Map<String, String> metadata){
        // the DataLicense that is expected
        String license = "CC0-1.0";
        Result r;

        // if the data license is within the metadata, continue the test
        if(metadata.containsKey("datalicense")){
            // extract the value of the data license in metadata
            String DataLicense = metadata.get("datalicense");

            // if the DataLicense contains the CC0-1.0
            if(DataLicense.contains(license)){
                r = new Result(TEST_NAME, Result.STATUS.PASS, "DataLicense " +
                        "is present and is CC0-1.0");
            }
            else{
                r = new Result(TEST_NAME, Result.STATUS.FAIL, "DataLicense " +
                        "is present but is not CC0-1.0");
            }
            r.updateInfo(Result.Context.FIELD_NAME, "DataLicense");
            r.updateInfo(Result.Context.STRING_VALUE, DataLicense);
        }
        // data license is not in the metadata, test fails
        else{
            r = new Result(TEST_NAME, Result.STATUS.FAIL, "Metadata does " +
                    "not contain a DataLicense");
            r.updateInfo(Result.Context.FIELD_NAME, "DataLicense");
            r.updateInfo(Result.Context.STRING_VALUE, "DataLicense is Missing");
        }
        return r;
    }
}
