package org.svip.sbomanalysis.qualityattributes.pipelines;

import jregex.Matcher;
import jregex.Pattern;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.SVIPComponentObject;
import org.svip.sbom.model.objects.SVIPSBOM;
import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.uids.Hash;
import org.svip.sbomanalysis.qualityattributes.newtests.*;
import org.svip.sbomanalysis.qualityattributes.newtests.enumerations.ATTRIBUTE;
import org.svip.sbomanalysis.qualityattributes.pipelines.interfaces.schemas.CycloneDX14.CDX14Tests;
import org.svip.sbomanalysis.qualityattributes.pipelines.interfaces.schemas.SPDX23.SPDX23Tests;
import org.svip.sbomanalysis.qualityattributes.resultfactory.Result;
import org.svip.sbomanalysis.qualityattributes.resultfactory.ResultFactory;
import org.svip.sbomanalysis.qualityattributes.resultfactory.enumerations.INFO;

import java.util.*;

public class SVIPPipeline implements CDX14Tests, SPDX23Tests {

    /**UID Regex used for validSerialNumber test*/
    private static final String UID_REGEX = "^urn:uuid:[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$";

    /**
     * Process the tests for the SBOM
     * @param uid Unique filename used to ID the SBOM
     * @param sbom the SBOM to run tests against
     * @return a Quality report for the sbom, its components and every test
     */
    @Override
    public QualityReport process(String uid, SBOM sbom) {
        // cast sbom to SVIPSBOM
        SVIPSBOM svipsbom = (SVIPSBOM) sbom;
        // build a new quality report
        QualityReport qualityReport = new QualityReport(uid);

        // Set to hold all the results
        List<Result> sbomResults = new ArrayList<>();

        // test SBOM metadata
        String bomVersion = svipsbom.getVersion();
        sbomResults.addAll(hasBomVersion("Bom Version", bomVersion));

        // test for SBOM's licenses
        var lt = new LicenseTest(ATTRIBUTE.LICENSING);
        for(String l : svipsbom.getLicenses()){
            sbomResults.addAll(lt.test("License", l));
        }

        // test SPDX/CDX specific metadata info
        //TODO data license can only hold one value, why is it a set of strings?
        Set<String> dataLicenses = svipsbom.getLicenses();
        sbomResults.addAll(hasDataLicense("Data License", dataLicenses));

        CreationData creationData = svipsbom.getCreationData();
        sbomResults.addAll(hasCreationInfo("Creation Data", creationData));

        String sbomUID = svipsbom.getUID();
        sbomResults.addAll(hasSPDXID("SBOM SPDXID", sbomUID));
        sbomResults.addAll(validSerialNumber("CDX Serial Number", sbomUID));

        //TODO add hasDocumentNamespace when implemented

        // add metadata results to the quality report
        qualityReport.addComponent("metadata", sbomResults);


        // test component info
        for(Component c : svipsbom.getComponents()){
            List<Result> componentResults = new ArrayList<>();
            SVIPComponentObject component = (SVIPComponentObject) c;

            String componentUID = component.getUID();
            componentResults.addAll(hasSPDXID("SPDXID", componentUID));
            componentResults.addAll(hasBomRef("Bom-Ref", componentUID));

            String downloadLocation = component.getDownloadLocation();
            componentResults.addAll(hasDownloadLocation("Download Location", downloadLocation));

            boolean filesAnalyzed = component.getFilesAnalyzed();
            String verificationCode = component.getVerificationCode();
            componentResults.addAll(hasVerificationCode("Verification Code", verificationCode, filesAnalyzed));

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
     * Test an SVIP SBOM for a bom version
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
     * Test an SVIP SBOM for a valid serial number UID
     * @param field the field that's tested
     * @param value the serial number tested
     * @return the result of if the sbom has a valid serial number UID
     */
    @Override
    public Set<Result> validSerialNumber(String field, String value) {
        String testName = "ValidSerialNumber";
        Set<Result> result = new HashSet<>();
        //Create a new Pattern with the CDX14 Regex
        Pattern cdx14UIDPattern = new Pattern(UID_REGEX, Pattern.DEFAULT);

        // set the attributes of this test to create a new ResultFactory
        ResultFactory resultFactory = new ResultFactory(testName, ATTRIBUTE.CDX14, ATTRIBUTE.COMPLETENESS);
        Result r;

        // first check if the sbom uid is not a null or empty string
        if(value != null && !value.isEmpty()){
            Matcher matcher = cdx14UIDPattern.matcher(value);
            // if regex fails to match to the uid string
            if(!matcher.find()){
                r = resultFactory.fail("SBOM Serial Number", INFO.INVALID, value);
            }
            // regex matches to the uid string
            else{
                r = resultFactory.pass("SBOM Serial Number", INFO.VALID, value);
            }
        }
        // uid was null or an empty string
        else{
            r = resultFactory.fail("SBOM Serial Number", INFO.MISSING, value);
        }

        result.add(r);
        return result;
    }

    /**
     * Test each component in a SVIP SBOM for a bom-ref
     * @param field the field that's tested
     * @param value the bom-ref tested
     * @return a set of results for each component in the sbom
     */
    @Override
    public Set<Result> hasBomRef(String field, String value) {
        Set<Result> results = new HashSet<>();

        var emptyNullTest = new EmptyOrNullTest(ATTRIBUTE.CDX14, ATTRIBUTE.UNIQUENESS);
        Result r = emptyNullTest.test(field, value);

        results.add(r);
        return results;
    }

    /**
     * Check if a hash algorithm in the given SVIP SBOM is supported
     * within CycloneDX
     * @param field the field that's tested
     * @param value the bom ref tested
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

    /**
     * Test the SVIP SBOM Metadata to see if it contains a data license of
     * CC0-1.0
     * @param field the field that's tested
     * @param values the data licenses tested
     * @return the result of checking for the CC0-1.0 data license
     */
    @Override
    public Set<Result> hasDataLicense(String field, Set<String> values) {
        String testName = "HasDataLicense";
        Set<Result> result = new HashSet<>();

        // set the attributes of this test to create a new ResultFactory
        ResultFactory resultFactory = new ResultFactory(testName,
                ATTRIBUTE.SPDX23, ATTRIBUTE.COMPLETENESS);
        Result r;

        // the required sbom license
        String requiredLicense = "CC0-1.0";

        // if the sbom's licenses contain the required license
        if(values.contains(requiredLicense)){
            r = resultFactory.pass(field, INFO.HAS,
                    requiredLicense);
        }
        // the sbom is missing the required license
        else{
            r = resultFactory.fail(field, INFO.MISSING,
                    requiredLicense);
        }

        result.add(r);
        return result;
    }

    /**
     * Test every component in a given SVIP SBOM for a valid SPDXID
     * @param field the field that's tested
     * @param value the SPDXID tested
     * @return a set of results for each component in the sbom
     */
    @Override
    public Set<Result> hasSPDXID(String field, String value) {
        String testName = "HasSPDXID";
        Set<Result> results = new HashSet<>();

        // set the attributes of this test to create a new ResultFactory
        ResultFactory resultFactory = new ResultFactory(testName,
                ATTRIBUTE.SPDX23, ATTRIBUTE.UNIQUENESS);

        Result r;

        // SPDXID is present and not a null or empty String
        if(value != null && !value.isEmpty()){
            // TODO Can we make this more thorough? Not just format?
            // check that SPDXID is a valid format
            // SPDXID starts with a valid format, test passes
            if(value.startsWith("SPDXRef-")){
                r = resultFactory.pass(field, INFO.VALID, value);
            }
            // SPDX starts with an invalid format, test fails
            else{
                r = resultFactory.fail(field, INFO.INVALID, value);
            }
        }
        // SPDXID is null or an empty value, test fails
        else{
            r = resultFactory.fail(field, INFO.MISSING, value);
        }
        results.add(r);

        return results;
    }

    /**
     * Test the SVIP sbom's metadata for a valid document namespace
     * @param field the field that's tested
     * @param value the document namespace tested
     * @return the result of if the sbom's metadata contains a valid
     * document namespace
     */
    //TODO is documentNamespace in SPDX23SBOM? How to access?
    @Override
    public Set<Result> hasDocumentNamespace(String field, String value) {
        return null;
    }
    /**
     * Given an SVIP SBOM, check that it has creator and created info
     * @param field the field that's tested
     * @param creationData the creation data of the SBOM to be tested
     * @return the result of if the sbom has creation info
     */
    @Override
    public Set<Result> hasCreationInfo(String field, CreationData creationData) {
        Set<Result> results = new HashSet<>();

        // create a new EmptyOrNullTest
        var emptyNullTest = new EmptyOrNullTest(ATTRIBUTE.SPDX23,
                ATTRIBUTE.COMPLETENESS);
        Result r;

        //first check for creator info
        Organization creator = creationData.getManufacture();
        String creatorName = creator.getName();
        r = emptyNullTest.test(field, creatorName);

        results.add(r);


        // then check for creation time info
        String creationTime = creationData.getCreationTime();
        r = emptyNullTest.test(field, creationTime);
        results.add(r);

        return results;
    }

    /**
     * Test every component in the SVIP SBOM for the
     * PackageDownloadLocation field and that it has a value
     * @param field the field that's tested
     * @param value the download location tested
     * @return a set of results for each component tested
     */
    @Override
    public Set<Result> hasDownloadLocation(String field, String value) {
        Set<Result> results = new HashSet<>();

        // create a new EmptyOrNullTest
        // TODO check for NOASSERTION or NONE?
        var emptyNullTest = new EmptyOrNullTest(ATTRIBUTE.SPDX23,
                ATTRIBUTE.COMPLETENESS);
        Result r = emptyNullTest.test(field, value);

        results.add(r);


        return results;
    }

    /**
     * Test all components in a given SVIP SBOM for their verification
     * code based on FilesAnalyzed
     * @param field the field that's tested
     * @param value the verification code tested
     * @param filesAnalyzed if the component's files were analyzed
     * @return a set of results for each component tested
     */
    @Override
    public Set<Result> hasVerificationCode(String field, String value, boolean filesAnalyzed) {
        String testName = "HasVerificationCode";
        Set<Result> results = new HashSet<>();

        // set the attributes of this test to create a new ResultFactory
        ResultFactory resultFactory = new ResultFactory(testName,
                ATTRIBUTE.SPDX23, ATTRIBUTE.COMPLETENESS);

        Result r;

        // if files were analyzed, check if the verification code is present
        if(filesAnalyzed){
            if(value == null || value.equals("")){
                r = resultFactory.fail(field, INFO.MISSING, value);
            }
            // verification code is not null and is present, test passes
            else{
                r = resultFactory.pass(field, INFO.HAS, value);
            }
        }
        // files were not analyzed, check if the verification code is null
        else{
            if(value == null || value.equals("")){
                r = resultFactory.pass(field, INFO.MISSING, value);
            }
            // verification code is not null and is present, test passes
            else{
                r = resultFactory.fail(field, INFO.HAS, value);
            }
        }

        results.add(r);


        return results;
    }
}
