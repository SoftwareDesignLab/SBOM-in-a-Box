package org.svip.sbomanalysis.qualityattributes.pipelines.schemas.CycloneDX14;

import jregex.Matcher;
import jregex.Pattern;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.CycloneDX14.CDX14ComponentObject;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.uids.Hash;
import org.svip.sbomanalysis.qualityattributes.newtests.*;
import org.svip.sbomanalysis.qualityattributes.newtests.enumerations.ATTRIBUTE;
import org.svip.sbomanalysis.qualityattributes.pipelines.QualityReport;
import org.svip.sbomanalysis.qualityattributes.pipelines.interfaces.schemas.CycloneDX14.CDX14Tests;
import org.svip.sbomanalysis.qualityattributes.resultfactory.Result;
import org.svip.sbomanalysis.qualityattributes.resultfactory.ResultFactory;
import org.svip.sbomanalysis.qualityattributes.resultfactory.enumerations.INFO;


import java.util.*;

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
     * @return a Quality report for the sbom, its components and every test
     */
    @Override
    public QualityReport process(String uid, SBOM sbom) {
        // cast sbom to CDX14SBOM
        CDX14SBOM cdx14SBOM = (CDX14SBOM) sbom;
        // build a new quality report
        QualityReport qualityReport = new QualityReport(uid);

        // Set to hold all the results
        List<Result> sbomResults = new ArrayList<>();

        // test SBOM metadata
        String bomVersion = cdx14SBOM.getVersion();
        sbomResults.addAll(hasBomVersion("Bom Version", bomVersion));

        // test for SBOM's licenses
        var lt = new LicenseTest(ATTRIBUTE.LICENSING);
        for(String l : cdx14SBOM.getLicenses()){
            sbomResults.addAll(lt.test("License", l));
        }

        // test CycloneDX 1.4 specific metadata information
        String serialNumber = cdx14SBOM.getUID();
        sbomResults.addAll(validSerialNumber("Bom Serial Number", serialNumber));

        // add metadata results to the quality report
        qualityReport.addComponent("metadata", sbomResults);

        // test component info
        for(Component c : cdx14SBOM.getComponents()){
            List<Result> componentResults = new ArrayList<>();
            CDX14ComponentObject component = (CDX14ComponentObject) c;

            String bomRef = component.getUID();
            componentResults.addAll(hasBomRef("Bom-Ref", bomRef));

            // test component CPEs
            var cpeTest = new CPETest(component, ATTRIBUTE.UNIQUENESS,
                    ATTRIBUTE.MINIMUM_ELEMENTS);
            for(String cpe: component.getCPEs()){
                componentResults.addAll(cpeTest.test("cpe", cpe));
            }
            // test component PURLs
            var purlTest = new PURLTest(component, ATTRIBUTE.UNIQUENESS,
                    ATTRIBUTE.MINIMUM_ELEMENTS);
            for(String purl: component.getPURLs()){
                componentResults.addAll(purlTest.test("purl", purl));
            }

            // test component Licenses
            var licenseTest = new LicenseTest(ATTRIBUTE.UNIQUENESS,
                    ATTRIBUTE.MINIMUM_ELEMENTS);
            Set<String> licenses = component.getLicenses().getDeclared();
            for(String l: licenses){
                componentResults.addAll(licenseTest.test("License", l));
            }

            // test component Hashes
            var hashTest = new HashTest(ATTRIBUTE.UNIQUENESS,
                    ATTRIBUTE.MINIMUM_ELEMENTS);
            Map<String, String> hashes = component.getHashes();
            for(String hashAlgo : hashes.keySet()){
                String hashValue = hashes.get(hashAlgo);
                componentResults.addAll(hashTest.test(hashAlgo, hashValue));
                componentResults.addAll(supportedHash("Supported CDX Hash", hashAlgo));
            }

            // add the component and all its tests to the quality report
            qualityReport.addComponent(component.getName(), componentResults);
        }

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
        Set<Result> result = new HashSet<>();

        // create a new EmptyOrNullTest
        var emptyNullTest = new EmptyOrNullTest(ATTRIBUTE.COMPLETENESS);
        Result r = emptyNullTest.test(field, value);

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
        ResultFactory resultFactory = new ResultFactory(testName,
                ATTRIBUTE.CDX14, ATTRIBUTE.COMPLETENESS);
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
        Set<Result> results = new HashSet<>();

        var emptyNullTest = new EmptyOrNullTest(ATTRIBUTE.CDX14,
                ATTRIBUTE.UNIQUENESS);
        Result r = emptyNullTest.test(field, value);

        results.add(r);

        return results;
    }

    /**
     * Check if a hash algorithm in the given CycloneDX 1.4 SBOM is supported
     * within CycloneDX
     * @param field the field that's tested
     * @param value the hash algorithm tested
     * @return the result of if the hash algorithm is supported
     */
    @Override
    public Set<Result> supportedHash(String field, String value) {
        String testName = "SupportedCDXHash";
        Set<Result> result = new HashSet<>();
        Result r;
        ResultFactory resultFactory = new ResultFactory(testName,
                ATTRIBUTE.CDX14, ATTRIBUTE.UNIQUENESS);
        // hash is unsupported, test fails
        if(Hash.isSPDXExclusive(Hash.Algorithm.valueOf(value))){
            r = resultFactory.fail(field, INFO.INVALID, value);
        }
        // hash is supported, test passes
        else{
            r = resultFactory.pass(field, INFO.VALID, value);
        }
        result.add(r);
        return result;
    }


}
