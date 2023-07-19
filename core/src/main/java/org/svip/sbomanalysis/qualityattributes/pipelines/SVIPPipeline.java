package org.svip.sbomanalysis.qualityattributes.pipelines;

import jregex.Matcher;
import jregex.Pattern;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.SVIPComponentObject;
import org.svip.sbom.model.objects.SVIPSBOM;
import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.shared.util.LicenseCollection;
import org.svip.sbom.model.uids.Hash;
import org.svip.sbomanalysis.qualityattributes.pipelines.interfaces.schemas.CycloneDX14.CDX14Tests;
import org.svip.sbomanalysis.qualityattributes.pipelines.interfaces.schemas.SPDX23.SPDX23Tests;
import org.svip.sbomanalysis.qualityattributes.resultfactory.Result;
import org.svip.sbomanalysis.qualityattributes.resultfactory.ResultFactory;
import org.svip.sbomanalysis.qualityattributes.resultfactory.enumerations.INFO;
import org.svip.sbomanalysis.qualityattributes.tests.*;
import org.svip.sbomanalysis.qualityattributes.tests.enumerations.ATTRIBUTE;

import java.util.*;

/**
 * file: SVIPPipeline.java
 * Pipeline class to run tests for SVIP SBOMs
 *
 * @author Matthew Morrison
 */
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
        sbomResults.add(hasBomVersion("Bom Version", bomVersion,
                svipsbom.getName()));

        // test for SBOM's licenses
        var lt = new LicenseTest(svipsbom.getName(), ATTRIBUTE.LICENSING);
        if(svipsbom.getLicenses() != null){
            for(String l : svipsbom.getLicenses()){
                sbomResults.addAll(lt.test("License", l));
            }
        }

        // test SPDX/CDX specific metadata info
        //TODO data license can only hold one value, why is it a set of strings?
        Set<String> dataLicenses = svipsbom.getLicenses();
        sbomResults.add(hasDataLicense("Data License", dataLicenses,
                svipsbom.getName()));

        CreationData creationData = svipsbom.getCreationData();
        sbomResults.addAll(hasCreationInfo("Creation Data", creationData,
                svipsbom.getName()));

        String sbomUID = svipsbom.getUID();
        sbomResults.add(hasSPDXID("SBOM SPDXID", sbomUID,
                svipsbom.getName()));
        sbomResults.add(validSerialNumber("CDX Serial Number", sbomUID,
                svipsbom.getName()));

        //TODO add hasDocumentNamespace when implemented

        // add metadata results to the quality report
        qualityReport.addComponent("metadata", sbomResults);

        if(svipsbom.getComponents() != null){
            // test component info
            for(Component c : svipsbom.getComponents()){
                List<Result> componentResults = new ArrayList<>();
                SVIPComponentObject component = (SVIPComponentObject) c;

                String componentUID = component.getUID();
                componentResults.add(hasSPDXID("SPDXID", componentUID,
                        component.getName()));
                componentResults.add(hasBomRef("Bom-Ref", componentUID,
                        component.getName()));

                String downloadLocation = component.getDownloadLocation();
                componentResults.add(hasDownloadLocation("Download Location",
                        downloadLocation, component.getName()));

                boolean filesAnalyzed = component.getFilesAnalyzed();
                String verificationCode = component.getVerificationCode();
                componentResults.add(hasVerificationCode("Verification Code",
                        verificationCode, filesAnalyzed, component.getName()));

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
                var licenseTest = new LicenseTest(component.getName(),ATTRIBUTE.UNIQUENESS,
                        ATTRIBUTE.MINIMUM_ELEMENTS);
                LicenseCollection licenses = component.getLicenses();
                if (licenses != null) {
                    Set<String> declaredLicenses = licenses.getDeclared();
                    for(String l: declaredLicenses){
                        componentResults.addAll(licenseTest.test("License", l));
                    }
                }

                // test component Hashes
                var hashTest = new HashTest(component.getName(), ATTRIBUTE.UNIQUENESS,
                        ATTRIBUTE.MINIMUM_ELEMENTS);
                Map<String, String> hashes = component.getHashes();
                if(hashes != null){
                    for(String hashAlgo : hashes.keySet()){
                        String hashValue = hashes.get(hashAlgo);
                        componentResults.addAll(hashTest.test(hashAlgo, hashValue));
                        componentResults.add(supportedHash("Supported CDX Hash",
                                hashAlgo, component.getName()));
                    }
                }

                // add the component and all its tests to the quality report
                qualityReport.addComponent(component.getName(), componentResults);
            }
        }

        return qualityReport;
    }

    /**
     * Test an SVIP SBOM for a bom version
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
     * Test an SVIP SBOM for a valid serial number UID
     * @param field the field that's tested
     * @param value the serial number tested
     * @param sbomName the sbom's name to product the result
     * @return the result of if the sbom has a valid serial number UID
     */
    @Override
    public Result validSerialNumber(String field, String value, String sbomName) {
        //Create a new Pattern with the CDX14 Regex
        Pattern cdx14UIDPattern = new Pattern(UID_REGEX, Pattern.DEFAULT);

        // set the attributes of this test to create a new ResultFactory
        String testName = "ValidSerialNumber";
        ResultFactory resultFactory = new ResultFactory(testName,
                ATTRIBUTE.CDX14, ATTRIBUTE.COMPLETENESS);

        // first check if the sbom uid is not a null or empty string
        if(value != null && !value.isEmpty()){
            Matcher matcher = cdx14UIDPattern.matcher(value);
            // if regex fails to match to the uid string
            if(!matcher.find()){
                return resultFactory.fail("SBOM Serial Number", INFO.INVALID, value, sbomName);
            }
            // regex matches to the uid string
            else{
                return resultFactory.pass("SBOM Serial Number", INFO.VALID, value, sbomName);
            }
        }
        // uid was null or an empty string
        else{
            return resultFactory.fail("SBOM Serial Number", INFO.MISSING, value, sbomName);
        }
    }

    /**
     * Test each component in a SVIP SBOM for a bom-ref
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
     * Check if a hash algorithm in the given SVIP SBOM is supported
     * within CycloneDX
     * @param field the field that's tested
     * @param value the bom ref tested
     * @param componentName the component's name to product the result
     * @return the result of if the hash algorithm is supported
     */
    @Override
    public Result supportedHash(String field, String value, String componentName) {
        String testName = "SupportedCDXHash";
        ResultFactory resultFactory = new ResultFactory(testName,
                ATTRIBUTE.CDX14, ATTRIBUTE.UNIQUENESS);

        // hash is unsupported, test fails
        if(Hash.isSPDXExclusive(Hash.Algorithm.valueOf(value))){
            return resultFactory.fail(field, INFO.INVALID, value,
                    componentName);
        }
        // hash is supported, test passes
        else{
            return resultFactory.pass(field, INFO.VALID, value,
                    componentName);
        }
    }

    /**
     * Test the SVIP SBOM Metadata to see if it contains a data license of
     * CC0-1.0
     * @param field the field that's tested
     * @param values the data licenses tested
     * @param sbomName the sbom's name to product the result
     * @return the result of checking for the CC0-1.0 data license
     */
    @Override
    public Result hasDataLicense(String field, Set<String> values, String sbomName) {
        String testName = "HasDataLicense";

        // set the attributes of this test to create a new ResultFactory
        ResultFactory resultFactory = new ResultFactory(testName,
                ATTRIBUTE.SPDX23, ATTRIBUTE.COMPLETENESS);

        // the required sbom license
        String requiredLicense = "CC0-1.0";
        // values is null or empty, test automatically fails
        if(values == null || values.isEmpty()){
            return resultFactory.fail(field, INFO.MISSING,
                    requiredLicense, sbomName);
        }
        // if the sbom's licenses contain the required license
        else if(values.size() == 1 && values.contains(requiredLicense)){
            return resultFactory.pass(field, INFO.HAS,
                    requiredLicense, sbomName);
        }
        // the sbom is missing the required license
        else{
            return resultFactory.fail(field, INFO.MISSING,
                    requiredLicense, sbomName);
        }
    }

    /**
     * Test every component in a given SVIP SBOM for a valid SPDXID
     * @param field the field that's tested
     * @param value the SPDXID tested
     * @param componentName the component's name to product the result
     * @return a set of results for each component in the sbom
     */
    @Override
    public Result hasSPDXID(String field, String value, String componentName) {
        String testName = "HasSPDXID";
        // set the attributes of this test to create a new ResultFactory
        ResultFactory resultFactory = new ResultFactory(testName,
                ATTRIBUTE.SPDX23, ATTRIBUTE.UNIQUENESS);

        // SPDXID is present and not a null or empty String
        if(value != null && !value.isEmpty()){
            // TODO Can we make this more thorough? Not just format?
            // check that SPDXID is a valid format
            // SPDXID starts with a valid format, test passes
            if(value.startsWith("SPDXRef-")){
                return resultFactory.pass(field, INFO.VALID,
                        value, componentName);
            }
            // SPDX starts with an invalid format, test fails
            else{
                return resultFactory.fail(field, INFO.INVALID,
                        value, componentName);
            }
        }
        // SPDXID is null or an empty value, test fails
        else{
            return resultFactory.fail(field, INFO.MISSING,
                    value, componentName);
        }
    }

    /**
     * Test the SVIP sbom's metadata for a valid document namespace
     * @param field the field that's tested
     * @param value the document namespace tested
     * @param sbomName the sbom's name to product the result
     * @return the result of if the sbom's metadata contains a valid
     * document namespace
     */
    //TODO is documentNamespace in SPDX23SBOM? How to access?
    @Override
    public Result hasDocumentNamespace(String field, String value, String sbomName) {
        return null;
    }

    /**
     * Given an SVIP SBOM, check that it has creator and created info
     * @param field the field that's tested
     * @param creationData the creation data of the SBOM to be tested
     * @param sbomName the sbom's name to product the result
     * @return the result of if the sbom has creation info
     */
    @Override
    public Set<Result> hasCreationInfo(String field, CreationData creationData,
                                       String sbomName) {
        Set<Result> results = new HashSet<>();

        // create a new EmptyOrNullTest and ResultFactory
        var emptyNullTest = new EmptyOrNullTest(ATTRIBUTE.SPDX23,
                ATTRIBUTE.COMPLETENESS);
        ResultFactory resultFactory = new ResultFactory("HasCreationInfo",
                ATTRIBUTE.SPDX23, ATTRIBUTE.COMPLETENESS);

        // creation data is null, test automatically fails and ends
        if(creationData == null){
            results.add(resultFactory.error(field, INFO.NULL,
                    "Creation Data", sbomName));
            return results;
        }

        //first check for creator info and if it is null
        Organization creator = creationData.getManufacture();
        if(creator == null){
            results.add(resultFactory.fail(field, INFO.MISSING,
                    "Creator Name", sbomName));
        }
        else{
            // check for the creator's name
            String creatorName = creator.getName();
            results.add(emptyNullTest.test(field, creatorName, sbomName));
            // then check for creation time info
            String creationTime = creationData.getCreationTime();
            results.add(emptyNullTest.test(field, creationTime, sbomName));
        }

        return results;
    }

    /**
     * Test every component in the SVIP SBOM for the
     * PackageDownloadLocation field and that it has a value
     * @param field the field that's tested
     * @param value the download location tested
     * @param componentName the component's name to product the result
     * @return the result of if the component has a download location
     */
    @Override
    public Result hasDownloadLocation(String field, String value, String componentName) {
        // create a new EmptyOrNullTest
        // TODO check for NOASSERTION or NONE?
        var emptyNullTest = new EmptyOrNullTest(ATTRIBUTE.SPDX23,
                ATTRIBUTE.COMPLETENESS);

        return emptyNullTest.test(field, value, componentName);
    }

    /**
     * Test all components in a given SVIP SBOM for their verification
     * code based on FilesAnalyzed
     * @param field the field that's tested
     * @param value the verification code tested
     * @param filesAnalyzed if the component's files were analyzed
     * @param componentName the component's name to product the result
     * @return the result of the component's verification code based on its
     * files analyzed field
     */
    @Override
    public Result hasVerificationCode(String field, String value, boolean filesAnalyzed, String componentName) {
        String testName = "HasVerificationCode";
        // set the attributes of this test to create a new ResultFactory
        ResultFactory resultFactory = new ResultFactory(testName,
                ATTRIBUTE.SPDX23, ATTRIBUTE.COMPLETENESS);

        // if files were analyzed, check if the verification code is present
        if(filesAnalyzed){
            if(value == null || value.equals("")){
                return resultFactory.fail(field, INFO.MISSING,
                        value, componentName);
            }
            // verification code is not null and is present, test passes
            else{
                return resultFactory.pass(field, INFO.HAS,
                        value, componentName);
            }
        }
        // files were not analyzed, check if the verification code is null
        else{
            if(value == null || value.equals("")){
                return resultFactory.pass(field, INFO.MISSING,
                        value, componentName);
            }
            // verification code is not null and is present, test passes
            else{
                return resultFactory.fail(field, INFO.HAS,
                        value, componentName);
            }
        }
    }
}
