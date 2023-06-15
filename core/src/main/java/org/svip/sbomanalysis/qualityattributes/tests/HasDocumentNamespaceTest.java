package org.svip.sbomanalysis.qualityattributes.tests;

import org.nvip.plugfest.tooling.sbom.SBOM;

import java.util.ArrayList;
import java.util.List;

/**
 * file: HasDataLicenseSPDXTest.java
 *
 * For an SPDX SBOM, test that the SBOM Metadata contains a
 * valid document namespace
 * A valid namespace is a unique absolute URI, excluding the '#' delimiter,
 * and follows scheme "https:"
 * @author Matthew Morrison
 */
public class HasDocumentNamespaceTest extends MetricTest{

    public static final String TEST_NAME = "HasDocumentNamespace";

    /**
     * Test the sbom's metadata for a valid document namespace
     * @param sbom SBOM to test
     * @return the result of if the sbom's metadata contains a valid
     * document namespace
     */
    @Override
    public List<Result> test(SBOM sbom) {
        List<Result> result = new ArrayList<>();
        // TODO designate DocumentNamespace instead of being in serialNumber?
        // DocumentNamespace is stored in serialNumber
        String documentNamespace = sbom.getSerialNumber();
        Result r;

        // documentNamespace is null, so metadata did not include it,
        // test fails
        if(isEmptyOrNull(documentNamespace)) {
            r = new Result(TEST_NAME, Result.STATUS.FAIL, "SPDX SBOM " +
                    "does not contain Document Namespace");
            r.updateInfo(Result.Context.STRING_VALUE,
                    "DocumentNamespace is empty and missing");

        }
        // documentNamespace has a value, check if it follows teh valid format
        else{
            r = validDocumentNamespace(documentNamespace);
        }
        r.addContext(sbom, "Document Namespace");
        result.add(r);
        return result;
    }

    /**
     * Test the document namespace and check it follows the SPDX format
     * @param documentNamespace the document namespace to test
     * @return a result of if the document namespace follows SPDX format
     */
    private Result validDocumentNamespace(String documentNamespace){
        Result r;
        // hold what failed in the document namespace, if any
        StringBuilder failMessage = new StringBuilder();
        // holds if the namespace is valid after all checks
        boolean validNamespace = true;

        // if the document namespace does not start with "https:" it is
        // not valid
        if(!documentNamespace.startsWith("https:")){
            validNamespace = false;
            failMessage.append("Does not start with \"https:\". ");
        }

        // if the document namespace contains "#" it is not valid
        if(documentNamespace.contains("#")){
            validNamespace = false;
            failMessage.append("Contains \"#\" delimiter. ");
        }



        // one or more checks failed, so the test fails
        if(!validNamespace){
            r = new Result(TEST_NAME, Result.STATUS.FAIL,
                    "DocumentNamespace is invalid.");
            r.updateInfo(Result.Context.STRING_VALUE, "Failed checks: " +
            failMessage);
        }
        // all checks passes, so the test passes
        else{
            r = new Result(TEST_NAME, Result.STATUS.PASS,
                    "DocumentNamespace is valid");
            r.updateInfo(Result.Context.STRING_VALUE, documentNamespace);
        }
        // add context for the result
        r.updateInfo(Result.Context.FIELD_NAME, "DocumentNamespace");
        return r;
    }

}
