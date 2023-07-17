package org.svip.sbomanalysis.qualityattributes.pipelines.schemas.SPDX23;

import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.SPDX23.SPDX23PackageObject;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.shared.util.LicenseCollection;
import org.svip.sbomanalysis.qualityattributes.newtests.*;
import org.svip.sbomanalysis.qualityattributes.newtests.enumerations.ATTRIBUTE;
import org.svip.sbomanalysis.qualityattributes.pipelines.QualityReport;
import org.svip.sbomanalysis.qualityattributes.pipelines.interfaces.schemas.SPDX23.SPDX23Tests;
import org.svip.sbomanalysis.qualityattributes.resultfactory.Result;
import org.svip.sbomanalysis.qualityattributes.resultfactory.ResultFactory;
import org.svip.sbomanalysis.qualityattributes.resultfactory.enumerations.INFO;

import java.util.*;

/**
 * file: SPDX23Pipeline.java
 * Pipeline class to run tests for SPDX 2.3 specific SBOMs
 *
 * @author Matthew Morrison
 */
public class SPDX23Pipeline implements SPDX23Tests {

    /**
     * Process the tests for the SBOM
     * @param uid Unique filename used to ID the SBOM
     * @param sbom the SBOM to run tests against
     * @return a Quality report for the sbom, its components and every test
     */
    @Override
    public QualityReport process(String uid, SBOM sbom) {
        // cast sbom to SPDX23SBOM
        SPDX23SBOM spdx23SBOM = (SPDX23SBOM) sbom;
        // build a new quality report
        QualityReport qualityReport = new QualityReport(uid);

        // Set to hold all the results
        List<Result> sbomResults = new ArrayList<>();

        // test SBOM metadata
        String bomVersion = spdx23SBOM.getVersion();
        sbomResults.addAll(hasBomVersion("Bom Version", bomVersion, spdx23SBOM.getName()));

        // test for SBOM's licenses
        var lt = new LicenseTest(spdx23SBOM.getName(), ATTRIBUTE.LICENSING);
        if(spdx23SBOM.getLicenses() != null){
            for(String l : spdx23SBOM.getLicenses()){
                sbomResults.addAll(lt.test("License", l));
            }
        }

        // test SPDX specific metadata info
        //TODO data license can only hold one value, why is it a set of strings?
        Set<String> dataLicenses = spdx23SBOM.getLicenses();
        sbomResults.addAll(hasDataLicense("Data License", dataLicenses, spdx23SBOM.getName()));

        CreationData creationData = spdx23SBOM.getCreationData();
        sbomResults.addAll(hasCreationInfo("Creation Data", creationData, spdx23SBOM.getName()));

        String sbomSPDXID = spdx23SBOM.getUID();
        sbomResults.addAll(hasSPDXID("SBOM SPDXID", sbomSPDXID, spdx23SBOM.getName()));

        //TODO add hasDocumentNamespace when implemented

        // add metadata results to the quality report
        qualityReport.addComponent("metadata", sbomResults);
        if(spdx23SBOM.getComponents() != null){
            // test component info
            for(Component c : spdx23SBOM.getComponents()){
                List<Result> componentResults = new ArrayList<>();
                SPDX23PackageObject component = (SPDX23PackageObject) c;

                String spdxID = component.getUID();
                componentResults.addAll(hasSPDXID("SPDXID", spdxID, component.getName()));

                String downloadLocation = component.getDownloadLocation();
                componentResults.addAll(hasDownloadLocation("Download Location", downloadLocation,
                        component.getName()));

                boolean filesAnalyzed = component.getFilesAnalyzed();
                String verificationCode = component.getVerificationCode();
                componentResults.addAll(hasVerificationCode("Verification Code",
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
                var hashTest = new HashTest(component.getName(), ATTRIBUTE.UNIQUENESS,
                        ATTRIBUTE.MINIMUM_ELEMENTS);
                Map<String, String> hashes = component.getHashes();
                if(hashes != null){
                    for(String hashAlgo : hashes.keySet()){
                        String hashValue = hashes.get(hashAlgo);
                        componentResults.addAll(hashTest.test(hashAlgo, hashValue));
                    }
                }

                // add the component and all its tests to the quality report
                qualityReport.addComponent(component.getName(), componentResults);
            }
        }

        return qualityReport;
    }

    /**
     * Test an SPDX 2.3 sbom for a bom version
     * @param field the field that's tested
     * @param value the bom version tested
     * @param sbomName the sbom's name to product the result
     * @return the result of if the sbom has a bom version
     */
    @Override
    public Set<Result> hasBomVersion(String field, String value, String sbomName) {
        Set<Result> result = new HashSet<>();

        // create a new EmptyOrNullTest
        var emptyNullTest = new EmptyOrNullTest(ATTRIBUTE.COMPLETENESS);
        Result r = emptyNullTest.test(field, value, sbomName);

        result.add(r);
        return result;
    }

    /**
     * Test the SPDX SBOM Metadata to see if it contains a data license of
     * CC0-1.0
     * @param field the field that's tested
     * @param values the licenses tested
     * @param sbomName the sbom's name to product the result
     * @return the result of checking for the CC0-1.0 data license
     */
    @Override
    public Set<Result> hasDataLicense(String field, Set<String> values, String sbomName) {
        String testName = "HasDataLicense";
        Set<Result> result = new HashSet<>();

        // set the attributes of this test to create a new ResultFactory
        ResultFactory resultFactory = new ResultFactory(testName,
                ATTRIBUTE.SPDX23, ATTRIBUTE.COMPLETENESS);
        Result r;

        // the required sbom license
        String requiredLicense = "CC0-1.0";
        // values is null or empty, test automatically fails
        if(values == null || values.isEmpty()){
            r = resultFactory.fail(field, INFO.MISSING,
                    requiredLicense, sbomName);
        }
        // if the sbom's licenses contain the required license
        else if(values.size() == 1 && values.contains(requiredLicense)){
            r = resultFactory.pass(field, INFO.HAS,
                    requiredLicense, sbomName);
        }
        // the sbom is missing the required license
        else{
            r = resultFactory.fail(field, INFO.MISSING,
                    requiredLicense, sbomName);
        }

        result.add(r);
        return result;
    }

    /**
     * Test every component in a given SPDX 2.3 SBOM for a valid SPDXID
     * @param field the field that's tested
     * @param value the SPDXID tested
     * @param componentName the component's name to product the result
     * @return a set of results for each component in the sbom
     */
    @Override
    public Set<Result> hasSPDXID(String field, String value, String componentName) {
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
                r = resultFactory.pass(field, INFO.VALID, value, componentName);
            }
            // SPDX starts with an invalid format, test fails
            else{
                r = resultFactory.fail(field, INFO.INVALID, value, componentName);
            }
        }
        // SPDXID is null or an empty value, test fails
        else{
            r = resultFactory.fail(field, INFO.MISSING, value, componentName);
        }
        results.add(r);

        return results;
    }

    /**
     * Test the SPDX 2.3 sbom's metadata for a valid document namespace
     * @param field the field that's tested
     * @param value the document namespace tested
     * @param sbomName the sbom's name to product the result
     * @return the result of if the sbom's metadata contains a valid
     * document namespace
     */
    //TODO is documentNamespace in SPDX23SBOM? How to access?
    @Override
    public Set<Result> hasDocumentNamespace(String field, String value, String sbomName) {
        return null;
    }

    /**
     * Given an SPDX 2.3 SBOM, check that it has creator and created info
     * @param field the field that's tested
     * @param creationData the creation data of the SBOM to be tested
     * @param sbomName the sbom's name to product the result
     * @return the result of if the sbom has creation info
     */
    @Override
    public Set<Result> hasCreationInfo(String field,
                                       CreationData creationData,
                                       String sbomName) {
        Set<Result> results = new HashSet<>();

        // create a new EmptyOrNullTest
        var emptyNullTest = new EmptyOrNullTest(ATTRIBUTE.SPDX23,
                ATTRIBUTE.COMPLETENESS);
        Result r;
        ResultFactory resultFactory = new ResultFactory("HasCreationInfo",
                ATTRIBUTE.SPDX23, ATTRIBUTE.COMPLETENESS);

        // creation data is null, test automatically fails and ends
        if(creationData == null){
            r = resultFactory.error(field, INFO.NULL, "Creation Data", sbomName);
            results.add(r);
            return results;
        }

        //first check for creator info
        Organization creator = creationData.getManufacture();
        if(creator == null){
            r = resultFactory.fail(field, INFO.MISSING, "Creator Name", sbomName);
            results.add(r);
        }
        else{
            String creatorName = creator.getName();
            r = emptyNullTest.test(field, creatorName, sbomName);
            results.add(r);
            // then check for creation time info
            String creationTime = creationData.getCreationTime();
            r = emptyNullTest.test(field, creationTime, sbomName);
            results.add(r);
        }


        return results;
    }

    /**
     * Test a component in the SPDX 2.3 SBOM for the
     * PackageDownloadLocation field and that it has a value
     * @param field the field that's tested
     * @param value the download location tested
     * @param componentName the component's name to product the result
     * @return a set of results for each component tested
     */
    @Override
    public Set<Result> hasDownloadLocation(String field, String value, String componentName) {
        Set<Result> results = new HashSet<>();

        // create a new EmptyOrNullTest
        var emptyNullTest = new EmptyOrNullTest(ATTRIBUTE.SPDX23,
                ATTRIBUTE.COMPLETENESS);

        // TODO check for NOASSERTION or NONE?
        Result r = emptyNullTest.test(field, value, componentName);

        results.add(r);

        return results;
    }

    /**
     * Test a components in a given SPDX 2.3 SBOM for their verification
     * code based on FilesAnalyzed
     * @param field the field that's tested
     * @param value the verification code tested
     * @param filesAnalyzed if the component's files were analyzed
     * @param componentName the component's name to product the result
     * @return a set of results for each component tested
     */
    @Override
    public Set<Result> hasVerificationCode(String field, String value,
                                           boolean filesAnalyzed,
                                           String componentName) {
        String testName = "HasVerificationCode";
        Set<Result> results = new HashSet<>();

        // set the attributes of this test to create a new ResultFactory
        ResultFactory resultFactory = new ResultFactory(testName,
                ATTRIBUTE.SPDX23, ATTRIBUTE.COMPLETENESS);

        Result r;

        // if files were analyzed, check if the verification code is present
        if(filesAnalyzed){
            if(value == null || value.equals("")){
                r = resultFactory.fail(field, INFO.MISSING, value, componentName);
            }
            // verification code is not null and is present, test passes
            else{
                r = resultFactory.pass(field, INFO.HAS, value, componentName);
            }
        }
        // files were not analyzed, check if the verification code is null
        else{
            if(value == null || value.equals("")){
                r = resultFactory.pass(field, INFO.MISSING, value, componentName);
            }
            // verification code is not null and is present, test passes
            else{
                r = resultFactory.fail(field, INFO.HAS, value, componentName);
            }
        }

        results.add(r);


        return results;
    }

}
