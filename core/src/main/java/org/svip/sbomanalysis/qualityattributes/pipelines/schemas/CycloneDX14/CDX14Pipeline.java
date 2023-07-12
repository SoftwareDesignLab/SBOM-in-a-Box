package org.svip.sbomanalysis.qualityattributes.pipelines.schemas.CycloneDX14;

import jregex.Matcher;
import jregex.Pattern;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbomanalysis.qualityattributes.newtests.enumerations.ATTRIBUTE;
import org.svip.sbomanalysis.qualityattributes.pipelines.QualityReport;
import org.svip.sbomanalysis.qualityattributes.pipelines.interfaces.schemas.CycloneDX14.CDX14Tests;
import org.svip.sbomanalysis.qualityattributes.resultfactory.Result;
import org.svip.sbomanalysis.qualityattributes.resultfactory.ResultFactory;
import org.svip.sbomanalysis.qualityattributes.resultfactory.enumerations.INFO;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * file: CDX14Pipeline.java
 * Pipeline class to run tests for CycloneDX 1.4 specific SBOMs
 *
 * @author Matthew Morrison
 */
public class CDX14Pipeline implements CDX14Tests {

    /**UID Regex used for validSerialNumber test*/
    private static final String CDX14_UID_REGEX = "^urn:uuid:[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$";

    /**
     * Process the tests for the SBOM
     * @param uid Unique filename used to ID the SBOM
     * @param sbom the SBOM to run tests against
     * @return a Quality report for the component and every test
     */
    //TODO implement
    @Override
    public QualityReport process(String uid, SBOM sbom) {
        CDX14SBOM cdx14SBOM = (CDX14SBOM) sbom;
        String sbomUID = cdx14SBOM.getUID();

        QualityReport qualityReport = new QualityReport(sbomUID);





        return qualityReport;
    }

    /**
     * Test a CycloneDX 1.4 sbom for a bom version
     * @param field the field that's tested
     * @param value the bom version tested
     * @return the result of if the sbom has a bom version
     */
    @Override
    public Set<Result> hasBomVersion(String field, String value) {
        String testName = "HasBomVersion";
        Set<Result> result = new HashSet<>();

        // set the attributes of this test to create a new ResultFactory
        List<ATTRIBUTE> attributes = new ArrayList<>(List.of(
                ATTRIBUTE.COMPLETENESS
        ));
        ResultFactory resultFactory = new ResultFactory(attributes, testName);
        Result r;

        // check if the version is a null or empty value
        if(value != null && !value.isEmpty()){
            r = resultFactory.pass(field, INFO.HAS, value);
        }
        else{
            r = resultFactory.fail(field, INFO.MISSING, value);
        }

        result.add(r);
        return result;
    }

    /**
     * Test a CycloneDX 1.4 SBOM for a valid serial number UID
     * @param field the field that's tested
     * @param value the serial number tested
     * @return the result of if the sbom has a valid serial number UID
     */
    @Override
    public Set<Result> validSerialNumber(String field, String value) {
        String testName = "ValidSerialNumber";
        Set<Result> result = new HashSet<>();
        //Create a new Pattern with the CDX14 Regex
        Pattern cdx14UIDPattern = new Pattern(CDX14_UID_REGEX, Pattern.DEFAULT);

        // set the attributes of this test to create a new ResultFactory
        List<ATTRIBUTE> attributes = new ArrayList<>(List.of(
                ATTRIBUTE.CDX14, ATTRIBUTE.COMPLETENESS
        ));
        ResultFactory resultFactory = new ResultFactory(attributes, testName);
        Result r;

        // first check if the sbom uid is not a null or empty string
        if(value != null && !value.isEmpty()){
            Matcher matcher = cdx14UIDPattern.matcher(value);
            // if regex fails to match to the uid string
            if(!matcher.find()){
                r = resultFactory.fail(field, INFO.INVALID, value);
            }
            // regex matches to the uid string
            else{
                r = resultFactory.pass(field, INFO.VALID, value);
            }
        }
        // uid was null or an empty string
        else{
            r = resultFactory.fail(field, INFO.MISSING, value);
        }

        result.add(r);
        return result;
    }

    /**
     * Test each component in a CycloneDX 1.4 SBOM for a bom-ref
     * @param field the field that's tested
     * @param value the bom-ref tested
     * @return a set of results for each component in the sbom
     */
    @Override
    public Set<Result> hasBomRef(String field, String value) {
        String testName = "HasBomRef";
        Set<Result> results = new HashSet<>();

        // set  the attributes associated with the test
        List<ATTRIBUTE> attributes = new ArrayList<>(List.of(
                ATTRIBUTE.CDX14, ATTRIBUTE.UNIQUENESS
        ));
        // create a new ResultFactory to create results
        ResultFactory resultFactory = new ResultFactory(attributes, testName);

            Result r;

            if(value != null && !value.isEmpty()){
                r = resultFactory.pass(field, INFO.HAS, value);
            }
            else{
                r = resultFactory.fail(field , INFO.MISSING, value);
            }
            results.add(r);


        return results;
    }
}
