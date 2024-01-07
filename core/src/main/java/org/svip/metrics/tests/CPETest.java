/** Copyright 2021 Rochester Institute of Technology (RIT). Developed with
* government support under contract 70RCSA22C00000008 awarded by the United
* States Department of Homeland Security for Cybersecurity and Infrastructure Security Agency.
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the “Software”), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
 */

package org.svip.metrics.tests;

import org.svip.metrics.resultfactory.Result;
import org.svip.metrics.resultfactory.ResultFactory;
import org.svip.metrics.resultfactory.enumerations.INFO;
import org.svip.metrics.tests.enumerations.ATTRIBUTE;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOMPackage;
import org.svip.sbom.model.uids.CPE;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * file: CPETest.java
 * Series of tests for CPE strings and objects
 *
 * @author Matthew Morrison
 * @author Derek Garcia
 */
public class CPETest extends MetricTest {

    private final ResultFactory resultFactory;

    private final SBOMPackage component;


    /**
     * Constructor to create a new MetricTest
     *
     * @param attributes the list of attributes used
     */
    public CPETest(Component component, ATTRIBUTE... attributes) {
        super(attributes);
        this.component = (SBOMPackage) component;
        String TEST_NAME = "CPETest";
        resultFactory = new ResultFactory(TEST_NAME, attributes);
    }

    /**
     * Perform the tests for a CPE
     *
     * @param field the field to test
     * @param value the value to test
     * @return a set of results as a result of each test
     */
    @Override
    public Set<Result> test(String field, String value) {
        Set<Result> results = new HashSet<>();
        // cpe is not a null value and does exist, tests can run
        if (value != null) {
            results.add(validCPEResult(field, value));
            results.addAll(accurateCPEResults(field, value));
        }
        // cpe is a null value and does not exist, tests cannot be run
        // return missing Result
        else {
            Result r = resultFactory.error(field, INFO.ERROR, value, component.getName());
            results.add(r);
        }

        return results;
    }

    /**
     * Test if a given CPE value is valid or not
     *
     * @param field the field that's tested (cpe)
     * @param value the cpe value to be tested
     * @return a result of if the cpe value is valid or not
     */
    private Result validCPEResult(String field, String value) {
        var rf = new ResultFactory("Valid CPE", ATTRIBUTE.COMPLETENESS, ATTRIBUTE.UNIQUENESS, ATTRIBUTE.MINIMUM_ELEMENTS);
        try {
            new CPE(value);    // throws error if given cpe string is invalid
            return rf.pass(field, INFO.VALID, value, component.getName());
        } catch (Exception e) {
            return rf.fail(field, INFO.ERROR, value, component.getName());
        }
    }

    /**
     * Test if the CPE value matches the component's stored data
     *
     * @param field the field that's tested (cpe)
     * @param value the cpe value to be tested
     * @return a list of Results of if the cpe matches that component's stored data
     */
    private List<Result> accurateCPEResults(String field, String value) {
        var rf = new ResultFactory("Accurate CPE", ATTRIBUTE.COMPLETENESS, ATTRIBUTE.UNIQUENESS, ATTRIBUTE.MINIMUM_ELEMENTS);
        try {
            // try to create a cpe object and then call match method
            CPE cpeObj = new CPE(value);
            return match(cpeObj);
        }
        // failed to create a new cpe object, test automatically fails
        catch (Exception e) {
            List<Result> result = new ArrayList<>();
            result.add(rf.fail(field, INFO.ERROR,
                    value, component.getName()));
            return result;
        }
    }

    /**
     * Helper function checks if CPE and Component match
     *
     * @param cpe the cpe to be tested against
     * @return Result with the findings
     */
    private List<Result> match(CPE cpe) {
        var rf = new ResultFactory("Matching CPE", ATTRIBUTE.COMPLETENESS, ATTRIBUTE.UNIQUENESS, ATTRIBUTE.MINIMUM_ELEMENTS);
        List<Result> results = new ArrayList<>();
        // test cpe and component name
        String cpeName = cpe.getProduct();
        if (!cpeName.equals(component.getName())) {
            results.add(rf.fail("CPE Name", INFO.NOT_MATCHING,
                    cpeName, component.getName()));
        } else {
            results.add(rf.pass("CPE Name", INFO.MATCHING,
                    cpeName, component.getName()));
        }

        // test cpe and component version
        String cpeVersion = cpe.getVersion();
        if (!cpeVersion.equals(component.getVersion())) {
            results.add(rf.fail("CPE Version", INFO.NOT_MATCHING,
                    cpeVersion, component.getVersion()));
        } else {
            results.add(rf.pass("CPE Version", INFO.MATCHING,
                    cpeVersion, component.getVersion()));
        }

        // test cpe vendor to component author
        String cpeVendor = cpe.getVendor();
        if (!cpeVendor.equals(component.getAuthor())) {
            results.add(rf.fail("CPE Vendor", INFO.NOT_MATCHING,
                    cpeVendor, component.getAuthor()));
        } else {
            results.add(rf.pass("CPE Vendor", INFO.MATCHING,
                    cpeVendor, component.getAuthor()));
        }

        return results;
    }

}
