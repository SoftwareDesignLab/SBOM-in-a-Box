package org.svip.metrics.pipelines.schemas.CycloneDX14;

import jregex.Matcher;
import jregex.Pattern;
import org.svip.metrics.tests.*;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.CycloneDX14.CDX14ComponentObject;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.shared.util.LicenseCollection;
import org.svip.sbom.model.uids.Hash;
import org.svip.metrics.pipelines.QualityReport;
import org.svip.metrics.pipelines.interfaces.schemas.CycloneDX14.CDX14Tests;
import org.svip.metrics.resultfactory.Result;
import org.svip.metrics.resultfactory.ResultFactory;
import org.svip.metrics.resultfactory.enumerations.INFO;
import org.svip.metrics.tests.enumerations.ATTRIBUTE;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        sbomResults.add(hasBomVersion("Bom Version", bomVersion, cdx14SBOM.getName()));

        // test for SBOM's licenses
        var lt = new LicenseTest(cdx14SBOM.getName(), ATTRIBUTE.LICENSING);
        if(cdx14SBOM.getLicenses() != null){
            for(String l : cdx14SBOM.getLicenses()){
                sbomResults.addAll(lt.test("License", l));
            }
        }

        // test CycloneDX 1.4 specific metadata information
        String serialNumber = cdx14SBOM.getUID();
        sbomResults.add(validSerialNumber("Bom Serial Number", serialNumber, cdx14SBOM.getName()));

        // add metadata results to the quality report
        qualityReport.addComponent("metadata", sbomResults);
        if(cdx14SBOM.getComponents() != null){
            // test component info
            for(Component c : cdx14SBOM.getComponents()){
                List<Result> componentResults = new ArrayList<>();
                CDX14ComponentObject component = (CDX14ComponentObject) c;

                String bomRef = component.getUID();
                componentResults.add(hasBomRef("Bom-Ref", bomRef,
                        component.getName()));

                // test component CPEs
                var cpeTest = new CPETest(component, ATTRIBUTE.UNIQUENESS,
                        ATTRIBUTE.MINIMUM_ELEMENTS);
                Set<String> cpes = component.getCPEs();
                if(cpes != null){
                    for(String cpe: cpes){
                        componentResults.addAll(cpeTest.test("cpe", cpe));
                    }
                }

                // test component PURLs
                var purlTest = new PURLTest(component, ATTRIBUTE.UNIQUENESS,
                        ATTRIBUTE.MINIMUM_ELEMENTS);
                Set<String> purls =  component.getPURLs();
                if(purls != null){
                    for(String purl: purls){
                        componentResults.addAll(purlTest.test("purl", purl));
                    }
                }

                // test component Licenses
                var licenseTest = new LicenseTest(component.getName(), ATTRIBUTE.UNIQUENESS,
                        ATTRIBUTE.MINIMUM_ELEMENTS);
                LicenseCollection licenses = component.getLicenses();
                if (licenses != null) {
                    Set<String> declaredLicenses = licenses.getDeclared();
                    for(String l: declaredLicenses){
                        componentResults.addAll(licenseTest.test("License", l));
                    }
                }

                // test component Hashes
                var hashTest = new HashTest(component.getName(),
                        ATTRIBUTE.UNIQUENESS, ATTRIBUTE.MINIMUM_ELEMENTS);
                Map<String, String> hashes = component.getHashes();
                if(hashes != null){
                    for(String hashAlgo : hashes.keySet()){
                        String hashValue = hashes.get(hashAlgo);
                        Hash hash = new Hash(hashAlgo, hashValue);
                        componentResults.addAll(hashTest.test(hashAlgo, hashValue));
                        componentResults.add(supportedHash("Supported CDX Hash",
                                hash, component.getName()));
                    }
                }

                // add the component and all its tests to the quality report
                qualityReport.addComponent(component.getName(), componentResults);
            }
        }

        return qualityReport;
    }

    /**
     * Test a CycloneDX 1.4 sbom for a bom version
     * @param field the field that's tested
     * @param value the bom version tested
     * @param sbomName the sbom's name to product the result
     * @return the result of if the sbom has a bom version
     */
    @Override
    public Result hasBomVersion(String field, String value, String sbomName) {
        // create a new EmptyOrNullTest
        var emptyNullTest = new EmptyOrNullTest(ATTRIBUTE.COMPLETENESS);
        return emptyNullTest.test(field, value, sbomName);
    }

    /**
     * Test a CycloneDX 1.4 SBOM for a valid serial number UID
     * @param field the field that's tested
     * @param value the serial number tested
     * @param sbomName the sbom's name to product the result
     * @return the result of if the sbom has a valid serial number UID
     */
    @Override
    public Result validSerialNumber(String field, String value, String sbomName) {
        //Create a new Pattern with the CDX14 Regex
        Pattern cdx14UIDPattern = new Pattern(CDX14_UID_REGEX, Pattern.DEFAULT);

        // set the attributes of this test to create a new ResultFactory
        String testName = "ValidSerialNumber";
        ResultFactory resultFactory = new ResultFactory(testName,
                ATTRIBUTE.CDX14, ATTRIBUTE.COMPLETENESS);

        // first check if the sbom uid is not a null or empty string
        if(value != null && !value.isEmpty()){
            Matcher matcher = cdx14UIDPattern.matcher(value);
            // if regex fails to match to the uid string
            if(!matcher.find()){
                return resultFactory.failCustom("SBOM Serial Number",
                        INFO.INVALID, value, sbomName, "UID does not follow " +
                                "CycloneDX's regex pattern");
            }
            // regex matches to the uid string
            else{
                return resultFactory.passCustom("SBOM Serial Number",
                        INFO.VALID, value, sbomName, "UID follows " +
                                "CycloneDX's regex pattern");
            }
        }
        // uid was null or an empty string
        else{
            return resultFactory.fail("SBOM Serial Number", INFO.MISSING, value, sbomName);
        }
    }

    /**
     * Test each component in a CycloneDX 1.4 SBOM for a bom-ref
     * @param field the field that's tested
     * @param value the bom-ref tested
     * @param componentName the component's name to product the result
     * @return the result of if the component has a bom-ref
     */
    @Override
    public Result hasBomRef(String field, String value, String componentName) {
        // create a new EmptyOrNullTest
        var emptyNullTest = new EmptyOrNullTest(ATTRIBUTE.CDX14, ATTRIBUTE.UNIQUENESS);
        return emptyNullTest.test(field, value, componentName);
    }

    /**
     * Check if a hash algorithm in the given CycloneDX 1.4 SBOM is supported
     * within CycloneDX
     * @param field the field that's tested
     * @param hash the hash to be tested
     * @param componentName the component's name to product the result
     * @return the result of if the hash algorithm is supported
     */
    @Override
    public Result supportedHash(String field, Hash hash, String componentName) {
        String testName = "SupportedCDXHash";
        ResultFactory resultFactory = new ResultFactory(testName,
                ATTRIBUTE.CDX14, ATTRIBUTE.UNIQUENESS);

        String algorithm = hash.getAlgorithm().toString();
        // hash is unsupported, test fails
        if(Hash.isSPDXExclusive(hash.getAlgorithm())){
            return resultFactory.failCustom(field, INFO.INVALID, algorithm,
                    componentName, "Hash Algorithm is not supported " +
                            "within CycloneDX: " + algorithm);
        }
        // hash is supported, test passes
        else{
            return resultFactory.passCustom(field, INFO.VALID, algorithm,
                    componentName, "Hash Algorithm is supported " +
                            "within CycloneDX: " + algorithm);
        }
    }


}
