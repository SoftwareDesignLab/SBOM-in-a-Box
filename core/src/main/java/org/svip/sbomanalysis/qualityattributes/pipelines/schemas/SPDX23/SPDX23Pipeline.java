package org.svip.sbomanalysis.qualityattributes.pipelines.schemas.SPDX23;

import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.SPDX23.SPDX23PackageObject;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbomanalysis.qualityattributes.newtests.enumerations.ATTRIBUTE;
import org.svip.sbomanalysis.qualityattributes.pipelines.QualityReport;
import org.svip.sbomanalysis.qualityattributes.pipelines.interfaces.schemas.SPDX23.SPDX23Tests;
import org.svip.sbomanalysis.qualityattributes.resultfactory.Result;
import org.svip.sbomanalysis.qualityattributes.resultfactory.ResultFactory;
import org.svip.sbomanalysis.qualityattributes.resultfactory.enumerations.INFO;

import java.util.*;

public class SPDX23Pipeline implements SPDX23Tests {

    //TODO implement
    @Override
    public QualityReport process(String uid, SBOM sbom) {
        return null;
    }

    /**
     * Test an SPDX 2.3 sbom for a bom version
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

        SPDX23SBOM spdx23SBOM = (SPDX23SBOM) sbom;

        String version = spdx23SBOM.getVersion();

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
     * Test the SPDX SBOM Metadata to see if it contains a data license of
     *      * CC0-1.0
     * @param sbom SPDX 2.3 SBOM to test
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
        SPDX23SBOM spdx23SBOM = (SPDX23SBOM) sbom;
        Set<String> sbomLicenses = spdx23SBOM.getLicenses();

        // the required sbom license
        String requiredLicense = "CC0-1.0";

        // if the sbom's licenses contain the required license
        if(sbomLicenses.contains(requiredLicense)){
            r = resultFactory.pass("SBOM Data License", INFO.HAS,
                    requiredLicense, spdx23SBOM.getName());
        }
        // the sbom is missing the required license
        else{
            r = resultFactory.fail("SBOM Data License", INFO.MISSING,
                    requiredLicense, spdx23SBOM.getName());
        }

        result.add(r);
        return result;
    }

    /**
     * Test every component in a given SPDX 2.3 SBOM for a valid SPDXID
     * @param sbom SPDX 2.3 SBOM to test
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

        // cast sbom to SPDX23SBOM
        SPDX23SBOM spdx23SBOM = (SPDX23SBOM) sbom;

        // for every component in the sbom
        for(Component c : spdx23SBOM.getComponents()){
            Result r;

            // cast component to SPDX23PackageObject
            SPDX23PackageObject packageObject = (SPDX23PackageObject) c;

            // check that the component has an SPDXID
            String spdxID = packageObject.getUID();

            // SPDXID is present and not a null or empty String
            if(spdxID != null && !spdxID.isEmpty()){
                // TODO Can we make this more thorough? Not just format?
                // check that SPDXID is a valid format
                // SPDXID starts with a valid format, test passes
                if(spdxID.startsWith("SPDXRef-")){
                    r = resultFactory.pass("SPDXID", INFO.VALID, spdxID,
                            packageObject.getName());
                }
                // SPDX starts with an invalid format, test fails
                else{
                    r = resultFactory.fail("SPDXID", INFO.INVALID, spdxID,
                            packageObject.getName());
                }
            }
            // SPDXID is null or an empty value, test fails
            else{
                r = resultFactory.fail("SPDXID", INFO.MISSING, spdxID,
                        packageObject.getName());
            }
            results.add(r);
        }

        return results;
    }

    /**
     * Test the SPDX 2.3 sbom's metadata for a valid document namespace
     * @param sbom SPDX 2.3 SBOM to test
     * @return the result of if the sbom's metadata contains a valid
     * document namespace
     */
    //TODO is documentNamespace in SPDX23SBOM? How to access?
    @Override
    public Set<Result> hasDocumentNamespace(SBOM sbom) {
        return null;
    }

    /**
     * Given an SPDX 2.3 SBOM, check that it has creator and created info
     * @param sbom SPDX 2.3 SBOM to test
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

        // cast sbom to SPDX23SBOM
        SPDX23SBOM spdx23SBOM = (SPDX23SBOM) sbom;
        CreationData creationData = spdx23SBOM.getCreationData();

        //first check for creator info
        Organization creator = creationData.getManufacture();
        String creatorName = creator.getName();
        // creator is not null, is a valid object, test passes
        if(creatorName != null && !creatorName.isEmpty()){
            r = resultFactory.pass("Creator", INFO.HAS,
                    creatorName, spdx23SBOM.getName());
        }
        else{
            r = resultFactory.pass("Creator", INFO.MISSING,
                    creatorName, spdx23SBOM.getName());
        }


        // then check for creation time info
        String creationTime = creationData.getCreationTime();
        // creation time has a value, test passes
        if(creationTime != null && !creationTime.isEmpty()){
            r = resultFactory.pass("Creation Time", INFO.HAS,
                    creationTime, spdx23SBOM.getName());
        }
        // creation time is null or an empty string, test fails
        else{
            r = resultFactory.fail("Creation Time", INFO.MISSING,
                    creationTime, spdx23SBOM.getName());
        }

        results.add(r);

        return results;
    }

    /**
     * Test every component in the SPDX 2.3 SBOM for the
     * PackageDownloadLocation field and that it has a value
     * @param sbom SPDX 2.3 SBOM to test
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

        // cast sbom to SPDX23SBOM
        SPDX23SBOM spdx23SBOM = (SPDX23SBOM) sbom;

        // for every component in the sbom
        for(Component c : spdx23SBOM.getComponents()){
            Result r;

            // cast component to SPDX23PackageObject
            SPDX23PackageObject packageObject = (SPDX23PackageObject) c;

            String downloadLocation = packageObject.getDownloadLocation();

            // TODO check for NOASSERTION or NONE?
            // downloadLocation is not null or an empty string
            // test passes
            if(downloadLocation != null && !downloadLocation.isEmpty()){
                r = resultFactory.pass("Download Location", INFO.HAS,
                        downloadLocation, packageObject.getName());
            }
            // downloadLocation is null or an empty string, test fails
            else{
                r = resultFactory.fail("Download Location", INFO.MISSING,
                        downloadLocation, packageObject.getName());
            }
            results.add(r);
        }

        return results;
    }

    /**
     * Test all components in a given SPDX 2.3 SBOM for their verification
     * code based on FilesAnalyzed
     * @param sbom SPDX 2.3 SBOM to test
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

        // cast sbom to SPDX23SBOM
        SPDX23SBOM spdx23SBOM = (SPDX23SBOM) sbom;

        // for every component in the sbom
        for(Component c : spdx23SBOM.getComponents()){
            Result r;

            SPDX23PackageObject packageObject = (SPDX23PackageObject) c;

            boolean filesAnalyzed = packageObject.getFilesAnalyzed();
            String verificationCode = packageObject.getVerificationCode();

            // if files were analyzed, check if the verification code is present
            if(filesAnalyzed){
                if(verificationCode == null || verificationCode.equals("")){
                    r = resultFactory.fail("Verification Code, Files " +
                            "Analyzed", INFO.MISSING, verificationCode,
                            packageObject.getName());
                }
                // verification code is not null and is present, test passes
                else{
                    r = resultFactory.pass("Verification Code, Files " +
                                    "Analyzed", INFO.HAS, verificationCode,
                            packageObject.getName());
                }
            }
            // files were not analyzed, check if the verification code is null
            else{
                if(verificationCode == null || verificationCode.equals("")){
                    r = resultFactory.pass("Verification Code, Files Not " +
                                    "Analyzed", INFO.MISSING, verificationCode,
                            packageObject.getName());
                }
                // verification code is not null and is present, test passes
                else{
                    r = resultFactory.fail("Verification Code, Files Not " +
                                    "Analyzed", INFO.HAS, verificationCode,
                            packageObject.getName());
                }
            }

            results.add(r);
        }

        return results;
    }

    /**
     * Check all components in a given SPDX 2.3 SBOM for extracted licenses
     * not on the SPDX license list
     * @param sbom SPDX 2.3 SBOM to test
     * @return the result of if there are any extracted licenses
     */
    //TODO how to obtain extracted licenses? Not a designated variable in SPDX23PackageObject or SPDX23SBOM
    @Override
    public Set<Result> hasExtractedLicenses(SBOM sbom) {
        return null;
    }

    /**
     * Check all components in a given SPDX 2.3 SBOM for extracted licenses
     * not on the SPDX license list If an extracted license is present, check
     * for the following fields: LicenseName, LicenseID, LicenseCrossReference
     * @param sbom SPDX 2.3 SBOM to test
     * @return a set of results for each extracted license tested
     */
    //TODO how to obtain extracted licenses? Not a designated variable in SPDX23PackageObject or SPDX23SBOM
    @Override
    public Set<Result> extractedLicenseMinElements(SBOM sbom) {
        return null;
    }
}
