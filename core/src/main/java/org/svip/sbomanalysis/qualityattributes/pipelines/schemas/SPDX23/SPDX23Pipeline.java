package org.svip.sbomanalysis.qualityattributes.pipelines.schemas.SPDX23;

import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.SPDX23.SPDX23FileObject;
import org.svip.sbom.model.objects.SPDX23.SPDX23PackageObject;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.shared.util.LicenseCollection;
import org.svip.sbomanalysis.qualityattributes.pipelines.QualityReport;
import org.svip.sbomanalysis.qualityattributes.pipelines.interfaces.schemas.SPDX23.SPDX23Tests;
import org.svip.sbomanalysis.qualityattributes.resultfactory.Result;
import org.svip.sbomanalysis.qualityattributes.resultfactory.ResultFactory;
import org.svip.sbomanalysis.qualityattributes.resultfactory.enumerations.INFO;
import org.svip.sbomanalysis.qualityattributes.tests.*;
import org.svip.sbomanalysis.qualityattributes.tests.enumerations.ATTRIBUTE;

import java.util.*;

/**
 * file: SPDX23Pipeline.java
 * Pipeline class to run tests for SPDX 2.3 specific SBOMs
 *
 * @author Matthew Morrison
 * @author Kevin Laporte
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
        sbomResults.add(hasBomVersion("Bom Version", bomVersion,
                spdx23SBOM.getName()));

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
        sbomResults.add(hasDataLicense("Data License", dataLicenses,
                spdx23SBOM.getName()));

        CreationData creationData = spdx23SBOM.getCreationData();
        sbomResults.addAll(hasCreationInfo("Creation Data", creationData, spdx23SBOM.getName()));

        String sbomSPDXID = spdx23SBOM.getUID();
        sbomResults.add(hasSPDXID("SBOM SPDXID", sbomSPDXID,
                spdx23SBOM.getName()));

        //TODO add hasDocumentNamespace when implemented

        // add metadata results to the quality report
        qualityReport.addComponent("metadata", sbomResults);

        // test each component's info and add its results to the quality report
        if(spdx23SBOM.getComponents() != null){
            for(Component c : spdx23SBOM.getComponents()){
                // Check what type of SPDX component it is
                if(c instanceof SPDX23PackageObject) {
                    qualityReport.addComponent(c.getName(), TestSPDX23Package(c));
                }
                else if(c instanceof SPDX23FileObject)
                {
                    qualityReport.addComponent(c.getName(), TestSPDX23File(c));
                }
            }
        }

        return qualityReport;
    }

    /**
     * Tests specific to an SPDX 2.3 Package
     * @param c the component to be tested
     * @return a list of results from the component tests
     */
    private List<Result> TestSPDX23Package(Component c)
    {
        SPDX23PackageObject component = (SPDX23PackageObject) c;
        List<Result> componentResults = new ArrayList<>();

        String spdxID = component.getUID();
        componentResults.add(hasSPDXID("SPDXID", spdxID,
                component.getName()));

        String downloadLocation = component.getDownloadLocation();
        componentResults.add(hasDownloadLocation("Download Location",
                downloadLocation, component.getName()));

        boolean filesAnalyzed;
        if (component.getFilesAnalyzed() != null) {
            filesAnalyzed = component.getFilesAnalyzed();
        } else {
            filesAnalyzed = false;
        }

        String verificationCode = component.getVerificationCode();
        componentResults.add(hasVerificationCode("Verification Code",
                verificationCode, filesAnalyzed, component.getName()));

        // test component CPEs
        var cpeTest = new CPETest(component, ATTRIBUTE.UNIQUENESS,
                ATTRIBUTE.MINIMUM_ELEMENTS);
        Set<String> cpes = component.getCPEs();
        if (cpes != null) {
            for (String cpe : cpes) {
                componentResults.addAll(cpeTest.test("cpe", cpe));
            }
        }

        // test component PURLs
        var purlTest = new PURLTest(component, ATTRIBUTE.UNIQUENESS,
                ATTRIBUTE.MINIMUM_ELEMENTS);
        Set<String> purls = component.getPURLs();
        if (purls != null) {
            for (String purl : purls) {
                componentResults.addAll(purlTest.test("purl", purl));
            }
        }

        // test component Licenses
        var licenseTest = new LicenseTest(component.getName(), ATTRIBUTE.UNIQUENESS,
                ATTRIBUTE.MINIMUM_ELEMENTS);
        LicenseCollection licenses = component.getLicenses();
        if (licenses != null) {
            Set<String> declaredLicenses = licenses.getDeclared();
            for (String l : declaredLicenses) {
                componentResults.addAll(licenseTest.test("License", l));
            }
        }

        // test component Hashes
        var hashTest = new HashTest(component.getName(), ATTRIBUTE.UNIQUENESS,
                ATTRIBUTE.MINIMUM_ELEMENTS);
        Map<String, String> hashes = component.getHashes();
        if (hashes != null) {
            for (String hashAlgo : hashes.keySet()) {
                String hashValue = hashes.get(hashAlgo);
                componentResults.addAll(hashTest.test(hashAlgo, hashValue));
            }
        }

        return componentResults;
    }

    /**
     * Tests specific to an SPDX 2.3 File
     * @param c the component to be tested
     * @return a list of results from the component tests
     */
    private List<Result> TestSPDX23File(Component c)
    {
        SPDX23FileObject component = (SPDX23FileObject) c;
        List<Result> componentResults = new ArrayList<>();

        // TODO COMMENT
        String comment = component.getComment();

        componentResults.add(hasDownloadLocation("Comment",
                comment, component.getName()));

        // TODO ATTRIBUTION TEXT
        String attributionText = component.getAttributionText();

        componentResults.add(hasDownloadLocation("Attribution Text",
                attributionText, component.getName()));

        // TODO GET FILE NOTICE
        String fileNotice = component.getFileNotice();

        componentResults.add(hasDownloadLocation("File Notice",
                fileNotice, component.getName()));

        String spdxID = component.getUID();
        componentResults.add(hasSPDXID("SPDXID", spdxID,
                component.getName()));

        // TODO GET AUTHOR
        String author = component.getAuthor();

        componentResults.add(hasDownloadLocation("Author",
                author, component.getName()));

        // TODO GET NAME?


        // test component Licenses
        var licenseTest = new LicenseTest(component.getName(), ATTRIBUTE.UNIQUENESS,
                ATTRIBUTE.MINIMUM_ELEMENTS);
        LicenseCollection licenses = component.getLicenses();
        if (licenses != null) {
            Set<String> declaredLicenses = licenses.getDeclared();
            for (String l : declaredLicenses) {
                componentResults.addAll(licenseTest.test("License", l));
            }
        }

        // TODO GET COPYRIGHT
        String copyright = component.getCopyright();
        componentResults.add(hasDownloadLocation("Copyright",
                copyright, component.getName()));

        // test component Hashes
        var hashTest = new HashTest(component.getName(), ATTRIBUTE.UNIQUENESS,
                ATTRIBUTE.MINIMUM_ELEMENTS);
        Map<String, String> hashes = component.getHashes();
        if (hashes != null) {
            for (String hashAlgo : hashes.keySet()) {
                String hashValue = hashes.get(hashAlgo);
                componentResults.addAll(hashTest.test(hashAlgo, hashValue));
            }
        }

        return componentResults;
    }


    /**
     * Test an SPDX 2.3 sbom for a bom version
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
     * Test the SPDX SBOM Metadata to see if it contains a data license of
     * CC0-1.0
     * @param field the field that's tested
     * @param values the licenses tested
     * @param sbomName the sbom's name to product the result
     * @return the result of checking for the CC0-1.0 data license
     */
    @Override
    public Result hasDataLicense(String field, Set<String> values, String sbomName) {
        String testName = "Has Data License";

        // set the attributes of this test to create a new ResultFactory
        var resultFactory = new ResultFactory(testName,
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
     * Test every component in a given SPDX 2.3 SBOM for a valid SPDXID
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

        // SPDXID is null or an empty value, test fails
        if(value == null || value.isEmpty())
            return resultFactory.fail(field, INFO.MISSING,
                    value, componentName);

        // SPDXID is present and not a null or empty String
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
    public Result hasDocumentNamespace(String field, String value, String sbomName) {
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
    public Set<Result> hasCreationInfo(String field, CreationData creationData,
                                       String sbomName) {
        Set<Result> results = new HashSet<>();

        // create a new EmptyOrNullTest and ResultFactory
        var emptyNullTest = new EmptyOrNullTest(ATTRIBUTE.SPDX23,
                ATTRIBUTE.COMPLETENESS);
        var resultFactory = new ResultFactory("Has Creation Info",
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
            return results;
        }

        // check for the creator's name
        String creatorName = creator.getName();
        results.add(emptyNullTest.test(field, creatorName, sbomName));
        // then check for creation time info
        String creationTime = creationData.getCreationTime();
        results.add(emptyNullTest.test(field, creationTime, sbomName));

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
    public Result hasDownloadLocation(String field, String value, String componentName) {
        // create a new EmptyOrNullTest
        // TODO check for NOASSERTION or NONE?
        var emptyNullTest = new EmptyOrNullTest(ATTRIBUTE.SPDX23,
                ATTRIBUTE.COMPLETENESS);

        return emptyNullTest.test(field, value, componentName);
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
    public Result hasVerificationCode(String field, String value, boolean filesAnalyzed, String componentName) {
        String testName = "Has Verification Code";
        // set the attributes of this test to create a new ResultFactory
        var resultFactory = new ResultFactory(testName,
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
