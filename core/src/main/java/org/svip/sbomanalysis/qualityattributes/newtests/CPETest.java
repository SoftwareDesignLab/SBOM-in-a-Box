package org.svip.sbomanalysis.qualityattributes.newtests;

import org.springframework.http.server.DelegatingServerHttpResponse;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.interfaces.generics.SBOMPackage;
import org.svip.sbom.model.uids.CPE;
import org.svip.sbomanalysis.qualityattributes.newtests.enumerations.ATTRIBUTE;
import org.svip.sbomanalysis.qualityattributes.resultfactory.Result;
import org.svip.sbomanalysis.qualityattributes.resultfactory.ResultFactory;
import org.svip.sbomanalysis.qualityattributes.resultfactory.Text;
import org.svip.sbomanalysis.qualityattributes.resultfactory.enumerations.INFO;
import org.svip.sbomanalysis.qualityattributes.resultfactory.enumerations.STATUS;
import org.svip.sbomfactory.generators.utils.Debug;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CPETest extends MetricTest{

    private final String TEST_NAME = "CPETest";

    private ResultFactory resultFactory;

    private SBOMPackage component;

    /**
     * Constructor to create a new MetricTest
     *
     * @param attributes the list of attributes used
     */
    public CPETest(Component component, List<ATTRIBUTE> attributes) {
        super(attributes);
        this.component = (SBOMPackage) component;
    }

    /**
     * Perform the tests for a CPE
     * @param field the field to test
     * @param value the value to test
     * @return a set of results as a result of each test
     */
    @Override
    public Set<Result> test(String field, String value) {
        Set<Result> results = new HashSet<>();
        // cpe is not a null value and does exist, tests can run
        if(value != null) {
            resultFactory = new ResultFactory(super.attributes, this.TEST_NAME);
            results.add(isValidCPE(field, value));
            results.addAll(isAccurateCPE(field, value));
        }
        // cpe is a null value and does not exist, tests cannot be run
        // return missing Result
        else {
            Text text = new Text(null, field);
            String message = text.getMessage(INFO.MISSING, field);
            String details = text.getDetails(INFO.MISSING, field);
            Result r = new Result(attributes, TEST_NAME, message, details, STATUS.ERROR);
            results.add(r);
        }

        return results;
    }

    /**
     * Test if a given CPE value is valid or not
     * @param field the field that's tested (cpe)
     * @param value the cpe value to be tested
     * @return a result of if the cpe value is valid or not
     */
    private Result isValidCPE(String field, String value){
        try{
            new CPE(value);    // throws error if given purl string is invalid
            return resultFactory.pass(field, INFO.VALID, value);
        } catch (Exception e){
            return resultFactory.fail(field, INFO.INVALID, value);
        }
    }

    /**
     * Test if the CPE value matches the component's stored data
     * @param field the field that's tested (cpe)
     * @param value the cpe value to be tested
     * @return a result of if the cpe matches that component's stored data
     */
    private Set<Result> isAccurateCPE(String field, String value){
        Set<Result> results = new HashSet<>();
        Result r;
        try{
            CPE cpeObj = new CPE(value);

            // test the cpe name, names need to be present
            results.add(isEqual("CPE Name", cpeObj.getProduct(), component.getName()));
            // test version information, an optional field
            results.add(isEqual("CPE Version", cpeObj.getVersion(), component.getVersion()));
            // test vendor information, an optional field
            results.add(isEqual("CPE Vendor", cpeObj.getVendor(), component.getAuthor()));


            // TODO other elements to test? Any relevant info in CPE to test in component?

        }
        // failed to create a new CPE object, test automatically fails
        catch (Exception e){
            r = resultFactory.fail(field, INFO.INVALID, value);
            results.add(r);
        }
        return results;
    }

    /**
     * Helper function checks if 2 fields are equal
     *
     * @param cpeValue Value stored in the CPE string
     * @param componentValue Value stored in the Component
     * @return Result with the findings
     */
    private Result isEqual(String field, String cpeValue, String componentValue){
        Result r;
        // Check if cpe value is different, if so, test fails
        if(!CPE.isEqualWildcard(cpeValue, componentValue)){
            r = resultFactory.fail(field, INFO.INVALID, cpeValue);
            // Else they both match, test passes
        } else {
           r = resultFactory.pass(field, INFO.VALID, cpeValue);
        }

        return r;
    }
}
