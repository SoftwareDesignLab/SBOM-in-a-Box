package org.svip.sbomanalysis.qualityattributes.pipelines.schemas.SPDX23;

import org.svip.sbom.model.interfaces.generics.SBOM;
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
     * Test the SPDX SBOM Metadata to see if it contains a data license of
     *      * CC0-1.0
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
     * Test every component in a given SPDX 2.3 SBOM for a valid SPDXID
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
     * Test the SPDX 2.3 sbom's metadata for a valid document namespace
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
     * Given an SPDX 2.3 SBOM, check that it has creator and created info
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
     * Test a component in the SPDX 2.3 SBOM for the
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
     * Test a components in a given SPDX 2.3 SBOM for their verification
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
