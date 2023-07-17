package org.svip.sbomanalysis.qualityattributes.oldtests;

import org.svip.sbom.model.old.Component;
import org.svip.sbom.model.old.SBOM;

import java.util.ArrayList;
import java.util.List;

/**
 * file: HasVerificationCodeTest.java
 *
 * For an SPDX SBOM, test that each component has a package verification code
 * if FilesAnalyzed is true
 * If FilesAnalyzed is false, test that the verification code is omitted
 * @author Matthew Morrison
 */
public class HasVerificationCodeTest extends MetricTest {
    public static final String TEST_NAME = "HasVerificationCode";

    /**
     * Test all components in a given SBOM for their verification code
     * based on FilesAnalyzed
     * @param sbom SBOM to test
     * @return a collection of results for each component in the SBOM
     */
    @Override
    public List<Result> test(SBOM sbom) {
        List<Result> results = new ArrayList<>();

        // for every component, check for verification code based
        // in FilesAnalyzed
        for(Component c : sbom.getAllComponents()){
            results.add(testComponentVerifyCode(c));
        }
        return results;
    }


    /**
     * Given a component, test if its files were analyzed or not
     * If FilesAnalyzed is true, check if verification code is present
     * If FilesAnalyzed is false, check if verification code is null/omitted
     * @param c the component to test
     * @return the result of the verification code of the component based on
     * FilesAnalyzed
     */
    private Result testComponentVerifyCode(Component c){
        Result r;

        // if files were analyzed, check if the verification code is present
        if(c.areFilesAnalyzed()){
            r = isCodePresent(c.getVerificationCode());
        }
        // files were not analyzed, check if the verification code is null
        else{
            r = isCodeNull(c.getVerificationCode());
        }
        r.addContext(c, "Verification Code");
        return r;
    }

    /**
     * Given that a component's files were analyzed, check that its
     * verification code is present and not null/omitted
     * @param verificationCode the component's verification code
     * @return a result of if the verification code was present or not
     */
    private Result isCodePresent(String verificationCode){
        Result r;
        // if the verification code is null, then it is not present, test fails
        if(isEmptyOrNull(verificationCode)){
            r = new Result(TEST_NAME, Result.STATUS.FAIL, "Component's " +
                    "files were analyzed but does not contain verification code");
        }
        // if the verification code is not null and is present, test passes
        else{
            r = new Result(TEST_NAME, Result.STATUS.PASS, "Component's " +
                    "files were analyzed and contains verification code");
            r.updateInfo(Result.Context.STRING_VALUE, verificationCode);
        }
        r.updateInfo(Result.Context.FIELD_NAME, "verificationcode");
        return r;
    }

    /**
     * Given that a component's files were not analyzed, check that its
     * verification code null/omitted and not present
     * @param verificationCode the component's verification code
     * @return a result of if the verification code is omitted or not
     */
    private Result isCodeNull(String verificationCode){
        Result r;
        // if the verification code is null, then it is not present, test passes
        if(isEmptyOrNull(verificationCode)){
            r = new Result(TEST_NAME, Result.STATUS.PASS, "Component's " +
                    "files were not analyzed and verification code " +
                    "was omitted");
        }
        // if the verification code is not null and is present, test fails
        else{
            r = new Result(TEST_NAME, Result.STATUS.FAIL, "Component's " +
                    "files were not analyzed but contains verification code");
            r.updateInfo(Result.Context.STRING_VALUE, verificationCode);
        }
        r.updateInfo(Result.Context.FIELD_NAME, "verificationcode");
        return r;
    }

}
