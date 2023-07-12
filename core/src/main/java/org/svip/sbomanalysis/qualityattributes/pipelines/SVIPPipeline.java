package org.svip.sbomanalysis.qualityattributes.pipelines;

import jregex.Matcher;
import jregex.Pattern;
import org.svip.sbom.model.interfaces.generics.SBOM;
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
        List<ATTRIBUTE> attributes = new ArrayList<>(List.of(
                ATTRIBUTE.SPDX23, ATTRIBUTE.COMPLETENESS
        ));
        ResultFactory resultFactory = new ResultFactory(attributes, testName);
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
        List<ATTRIBUTE> attributes = new ArrayList<>(List.of(
                ATTRIBUTE.SPDX23, ATTRIBUTE.UNIQUENESS
        ));
        ResultFactory resultFactory = new ResultFactory(attributes, testName);

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
        String testName = "HasDataLicense";
        Set<Result> results = new HashSet<>();

        // set the attributes of this test to create a new ResultFactory
        List<ATTRIBUTE> attributes = new ArrayList<>(List.of(
                ATTRIBUTE.SPDX23, ATTRIBUTE.COMPLETENESS
        ));
        ResultFactory resultFactory = new ResultFactory(attributes, testName);
        Result r;

        //first check for creator info
        Organization creator = creationData.getManufacture();
        String creatorName = creator.getName();
        // creator is not null, is a valid object, test passes
        if(creatorName != null && !creatorName.isEmpty()){
            r = resultFactory.pass(field, INFO.HAS,
                    creatorName);
        }
        else{
            r = resultFactory.pass(field, INFO.MISSING,
                    creatorName);
        }

        results.add(r);


        // then check for creation time info
        String creationTime = creationData.getCreationTime();
        // creation time has a value, test passes
        if(creationTime != null && !creationTime.isEmpty()){
            r = resultFactory.pass(field, INFO.HAS,
                    creationTime);
        }
        // creation time is null or an empty string, test fails
        else{
            r = resultFactory.fail(field, INFO.MISSING,
                    creationTime);
        }

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
        String testName = "HasDownloadLocation";
        Set<Result> results = new HashSet<>();

        // set the attributes of this test to create a new ResultFactory
        List<ATTRIBUTE> attributes = new ArrayList<>(List.of(
                ATTRIBUTE.SPDX23, ATTRIBUTE.COMPLETENESS
        ));
        ResultFactory resultFactory = new ResultFactory(attributes, testName);

        Result r;

        // TODO check for NOASSERTION or NONE?
        // downloadLocation is not null or an empty string
        // test passes
        if(value != null && !value.isEmpty()){
            r = resultFactory.pass(field, INFO.HAS,
                    value);
        }
        // downloadLocation is null or an empty string, test fails
        else{
            r = resultFactory.fail(field, INFO.MISSING,
                    value);
        }
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
        List<ATTRIBUTE> attributes = new ArrayList<>(List.of(
                ATTRIBUTE.SPDX23, ATTRIBUTE.COMPLETENESS
        ));
        ResultFactory resultFactory = new ResultFactory(attributes, testName);

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
