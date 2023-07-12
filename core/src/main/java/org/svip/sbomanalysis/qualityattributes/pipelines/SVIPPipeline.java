package org.svip.sbomanalysis.qualityattributes.pipelines;

import jregex.Matcher;
import jregex.Pattern;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.SVIPComponentObject;
import org.svip.sbom.model.objects.SVIPSBOM;
import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbomanalysis.qualityattributes.newtests.enumerations.ATTRIBUTE;
import org.svip.sbomanalysis.qualityattributes.pipelines.interfaces.schemas.CycloneDX14.CDX14Tests;
import org.svip.sbomanalysis.qualityattributes.pipelines.interfaces.schemas.SPDX23.SPDX23Tests;
import org.svip.sbomanalysis.qualityattributes.resultfactory.Result;
import org.svip.sbomanalysis.qualityattributes.resultfactory.ResultFactory;
import org.svip.sbomanalysis.qualityattributes.resultfactory.enumerations.INFO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SVIPPipeline implements CDX14Tests, SPDX23Tests {

    /**UID Regex used for validSerialNumber test*/
    private static final String UID_REGEX = "^urn:uuid:[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$";

    //TODO implement
    @Override
    public QualityReport process(String uid, SBOM sbom) {
        return null;
    }

    /**
     * Test an SVIP SBOM for a bom version
     * @param sbom the SBOM to test
     * @return the result of if the sbom has a bom version
     */
    @Override
    public Set<Result> hasBomVersion(SBOM sbom) {
        String testName = "HasBomVersion";
        Set<Result> result = new HashSet<>();

        // set the attributes of this test to create a new ResultFactory
        List<ATTRIBUTE> attributes = new ArrayList<>(List.of(
                ATTRIBUTE.COMPLETENESS
        ));
        ResultFactory resultFactory = new ResultFactory(attributes, testName);
        Result r;

        SVIPSBOM svipsbom = (SVIPSBOM) sbom;
        String version = svipsbom.getVersion();

        // check if the version is a null or empty value
        if(version != null && !version.isEmpty()){
            r = resultFactory.pass("BomVersion", INFO.HAS, version,
                    sbom.getName());
        }
        else{
            r = resultFactory.fail("BomVersion", INFO.MISSING, version,
                    sbom.getName());
        }

        result.add(r);
        return result;
    }

    /**
     * Test an SVIP SBOM for a valid serial number UID
     * @param sbom the SVIP SBOM
     * @return the result of if the sbom has a valid serial number UID
     */
    @Override
    public Set<Result> validSerialNumber(SBOM sbom) {
        String testName = "ValidSerialNumber";
        Set<Result> result = new HashSet<>();
        //Create a new Pattern with the CDX14 Regex
        Pattern cdx14UIDPattern = new Pattern(UID_REGEX, Pattern.DEFAULT);

        // set the attributes of this test to create a new ResultFactory
        List<ATTRIBUTE> attributes = new ArrayList<>(List.of(
                ATTRIBUTE.CDX14, ATTRIBUTE.COMPLETENESS
        ));
        ResultFactory resultFactory = new ResultFactory(attributes, testName);
        Result r;

        SVIPSBOM svipsbom = (SVIPSBOM) sbom;
        String sbomUID = svipsbom.getUID();

        // first check if the sbom uid is not a null or empty string
        if(sbomUID != null && !sbomUID.isEmpty()){
            Matcher matcher = cdx14UIDPattern.matcher(sbomUID);
            // if regex fails to match to the uid string
            if(!matcher.find()){
                r = resultFactory.fail("SBOM Serial Number", INFO.INVALID, sbomUID,
                        sbom.getName());
            }
            // regex matches to the uid string
            else{
                r = resultFactory.pass("SBOM Serial Number", INFO.VALID, sbomUID,
                        sbom.getName());
            }
        }
        // uid was null or an empty string
        else{
            r = resultFactory.fail("SBOM Serial Number", INFO.MISSING, sbomUID,
                    sbom.getName());
        }

        result.add(r);
        return result;
    }

    /**
     * Test each component in a SVIP SBOM for a bom-ref
     * @param sbom the SVIP SBOM
     * @return a set of results for each component in the sbom
     */
    @Override
    public Set<Result> hasBomRef(SBOM sbom) {
        String testName = "HasBomRef";
        Set<Result> results = new HashSet<>();

        // set  the attributes associated with the test
        List<ATTRIBUTE> attributes = new ArrayList<>(List.of(
                ATTRIBUTE.CDX14, ATTRIBUTE.UNIQUENESS
        ));
        // create a new ResultFactory to create results
        ResultFactory resultFactory = new ResultFactory(attributes, testName);

        // cast sbom to SVIPSBOM
        SVIPSBOM svipsbom = (SVIPSBOM) sbom;

        for (Component c : svipsbom.getComponents()){
            Result r;
            SVIPComponentObject svipComponentObject = (SVIPComponentObject) c;
            String bomRef = svipComponentObject.getUID();

            if(bomRef != null && !bomRef.isEmpty()){
                r = resultFactory.pass("BomRef", INFO.HAS, bomRef,
                        svipComponentObject.getName());
            }
            else{
                r = resultFactory.fail("BomRef", INFO.MISSING, bomRef,
                        svipComponentObject.getName());
            }
            results.add(r);
        }

        return results;
    }

    /**
     * Test the SVIP SBOM Metadata to see if it contains a data license of
     * CC0-1.0
     * @param sbom SVIP SBOM to test
     * @return the result of checking for the CC0-1.0 data license
     */
    @Override
    public Set<Result> hasDataLicense(SBOM sbom) {
        String testName = "HasDataLicense";
        Set<Result> result = new HashSet<>();

        // set the attributes of this test to create a new ResultFactory
        List<ATTRIBUTE> attributes = new ArrayList<>(List.of(
                ATTRIBUTE.SPDX23, ATTRIBUTE.COMPLETENESS
        ));
        ResultFactory resultFactory = new ResultFactory(attributes, testName);
        Result r;

        // get the sbom's licenses
        SVIPSBOM svipsbom = (SVIPSBOM) sbom;
        Set<String> sbomLicenses = svipsbom.getLicenses();

        // the required sbom license
        String requiredLicense = "CC0-1.0";

        // if the sbom's licenses contain the required license
        if(sbomLicenses.contains(requiredLicense)){
            r = resultFactory.pass("SBOM Data License", INFO.HAS,
                    requiredLicense, svipsbom.getName());
        }
        // the sbom is missing the required license
        else{
            r = resultFactory.fail("SBOM Data License", INFO.MISSING,
                    requiredLicense, svipsbom.getName());
        }

        result.add(r);
        return result;
    }

    /**
     * Test every component in a given SVIP SBOM for a valid SPDXID
     * @param sbom SVIP SBOM to test
     * @return a set of results for each component in the sbom
     */
    @Override
    public Set<Result> hasSPDXID(SBOM sbom) {
        String testName = "HasSPDXID";
        Set<Result> results = new HashSet<>();

        // set the attributes of this test to create a new ResultFactory
        List<ATTRIBUTE> attributes = new ArrayList<>(List.of(
                ATTRIBUTE.SPDX23, ATTRIBUTE.UNIQUENESS
        ));
        ResultFactory resultFactory = new ResultFactory(attributes, testName);

        // cast sbom to SVIPSBOM
        SVIPSBOM svipsbom = (SVIPSBOM) sbom;

        // for every component in the sbom
        for(Component c : svipsbom.getComponents()){
            Result r;

            // cast component to SVIPComponentObject
            SVIPComponentObject componentObject = (SVIPComponentObject) c;

            // check that the component has an SPDXID
            String spdxID = componentObject.getUID();

            // SPDXID is present and not a null or empty String
            if(spdxID != null && !spdxID.isEmpty()){
                // TODO Can we make this more thorough? Not just format?
                // check that SPDXID is a valid format
                // SPDXID starts with a valid format, test passes
                if(spdxID.startsWith("SPDXRef-")){
                    r = resultFactory.pass("SPDXID", INFO.VALID, spdxID,
                            componentObject.getName());
                }
                // SPDX starts with an invalid format, test fails
                else{
                    r = resultFactory.fail("SPDXID", INFO.INVALID, spdxID,
                            componentObject.getName());
                }
            }
            // SPDXID is null or an empty value, test fails
            else{
                r = resultFactory.fail("SPDXID", INFO.MISSING, spdxID,
                        componentObject.getName());
            }
            results.add(r);
        }

        return results;
    }

    /**
     * Test the SVIP sbom's metadata for a valid document namespace
     * @param sbom SVIP SBOM to test
     * @return the result of if the sbom's metadata contains a valid
     * document namespace
     */
    //TODO is documentNamespace in SPDX23SBOM? How to access?
    @Override
    public Set<Result> hasDocumentNamespace(SBOM sbom) {
        return null;
    }

    /**
     * Given an SVIP SBOM, check that it has creator and created info
     * @param sbom SVIP SBOM to test
     * @return the result of if the sbom has creation info
     */
    @Override
    public Set<Result> hasCreationInfo(SBOM sbom) {
        String testName = "HasDataLicense";
        Set<Result> results = new HashSet<>();

        // set the attributes of this test to create a new ResultFactory
        List<ATTRIBUTE> attributes = new ArrayList<>(List.of(
                ATTRIBUTE.SPDX23, ATTRIBUTE.COMPLETENESS
        ));
        ResultFactory resultFactory = new ResultFactory(attributes, testName);
        Result r;

        // cast sbom to SVIPSBOM
        SVIPSBOM svipsbom = (SVIPSBOM) sbom;
        CreationData creationData = svipsbom.getCreationData();

        //first check for creator info
        Organization creator = creationData.getManufacture();
        String creatorName = creator.getName();
        // creator is not null, is a valid object, test passes
        if(creatorName != null && !creatorName.isEmpty()){
            r = resultFactory.pass("Creator", INFO.HAS,
                    creatorName, svipsbom.getName());
        }
        else{
            r = resultFactory.pass("Creator", INFO.MISSING,
                    creatorName, svipsbom.getName());
        }

        // then check for creation time info
        String creationTime = creationData.getCreationTime();
        // creation time has a value, test passes
        if(creationTime != null && !creationTime.isEmpty()){
            r = resultFactory.pass("Creation Time", INFO.HAS,
                    creationTime, svipsbom.getName());
        }
        // creation time is null or an empty string, test fails
        else{
            r = resultFactory.fail("Creation Time", INFO.MISSING,
                    creationTime, svipsbom.getName());
        }

        results.add(r);

        return results;
    }

    /**
     * Test every component in the SVIP SBOM for the
     * PackageDownloadLocation field and that it has a value
     * @param sbom SVIP SBOM to test
     * @return a set of results for each component tested
     */
    @Override
    public Set<Result> hasDownloadLocation(SBOM sbom) {
        String testName = "HasDownloadLocation";
        Set<Result> results = new HashSet<>();

        // set the attributes of this test to create a new ResultFactory
        List<ATTRIBUTE> attributes = new ArrayList<>(List.of(
                ATTRIBUTE.SPDX23, ATTRIBUTE.COMPLETENESS
        ));
        ResultFactory resultFactory = new ResultFactory(attributes, testName);

        // cast sbom to SVIPSBOM
        SVIPSBOM svipsbom = (SVIPSBOM) sbom;

        // for every component in the sbom
        for(Component c : svipsbom.getComponents()){
            Result r;

            // cast component to SPDX23PackageObject
            SVIPComponentObject componentObject = (SVIPComponentObject) c;

            String downloadLocation = componentObject.getDownloadLocation();

            // TODO check for NOASSERTION or NONE?
            // downloadLocation is not null or an empty string
            // test passes
            if(downloadLocation != null && !downloadLocation.isEmpty()){
                r = resultFactory.pass("Download Location", INFO.HAS,
                        downloadLocation, componentObject.getName());
            }
            // downloadLocation is null or an empty string, test fails
            else{
                r = resultFactory.fail("Download Location", INFO.MISSING,
                        downloadLocation, componentObject.getName());
            }
            results.add(r);
        }

        return results;
    }

    /**
     * Test all components in a given SVIP SBOM for their verification
     * code based on FilesAnalyzed
     * @param sbom SVIP SBOM to test
     * @return a set of results for each component tested
     */
    @Override
    public Set<Result> hasVerificationCode(SBOM sbom) {
        String testName = "HasVerificationCode";
        Set<Result> results = new HashSet<>();

        // set the attributes of this test to create a new ResultFactory
        List<ATTRIBUTE> attributes = new ArrayList<>(List.of(
                ATTRIBUTE.SPDX23, ATTRIBUTE.COMPLETENESS
        ));
        ResultFactory resultFactory = new ResultFactory(attributes, testName);

        // cast sbom to SVIPSBOM
        SVIPSBOM svipsbom = (SVIPSBOM) sbom;

        // for every component in the sbom
        for(Component c : svipsbom.getComponents()){
            Result r;

            SVIPComponentObject componentObject = (SVIPComponentObject) c;

            boolean filesAnalyzed = componentObject.getFilesAnalyzed();
            String verificationCode = componentObject.getVerificationCode();

            // if files were analyzed, check if the verification code is present
            if(filesAnalyzed){
                if(verificationCode == null || verificationCode.equals("")){
                    r = resultFactory.fail("Verification Code, Files " +
                                    "Analyzed", INFO.MISSING, verificationCode,
                            componentObject.getName());
                }
                // verification code is not null and is present, test passes
                else{
                    r = resultFactory.pass("Verification Code, Files " +
                                    "Analyzed", INFO.HAS, verificationCode,
                            componentObject.getName());
                }
            }
            // files were not analyzed, check if the verification code is null
            else{
                if(verificationCode == null || verificationCode.equals("")){
                    r = resultFactory.pass("Verification Code, Files Not " +
                                    "Analyzed", INFO.MISSING, verificationCode,
                            componentObject.getName());
                }
                // verification code is not null and is present, test passes
                else{
                    r = resultFactory.fail("Verification Code, Files Not " +
                                    "Analyzed", INFO.HAS, verificationCode,
                            componentObject.getName());
                }
            }

            results.add(r);
        }

        return results;
    }

    /**
     * Check all components in a given SVIP SBOM for extracted licenses
     * not on the SPDX license list
     * @param sbom SVIP SBOM to test
     * @return the result of if there are any extracted licenses
     */
    //TODO how to obtain extracted licenses? Not a designated variable in SPDX23PackageObject or SPDX23SBOM
    @Override
    public Set<Result> hasExtractedLicenses(SBOM sbom) {
        return null;
    }

    /**
     * Check all components in a given SVIP SBOM for extracted licenses
     * not on the SPDX license list. If an extracted license is present, check
     * for the following fields: LicenseName, LicenseID, LicenseCrossReference
     * @param sbom SVIP SBOM to test
     * @return a set of results for each extracted license tested
     */
    //TODO how to obtain extracted licenses? Not a designated variable in SPDX23PackageObject or SPDX23SBOM
    @Override
    public Set<Result> extractedLicenseMinElements(SBOM sbom) {
        return null;
    }
}
