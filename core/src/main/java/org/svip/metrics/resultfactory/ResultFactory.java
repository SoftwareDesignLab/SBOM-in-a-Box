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

package org.svip.metrics.resultfactory;

import org.svip.metrics.resultfactory.enumerations.INFO;
import org.svip.metrics.resultfactory.enumerations.STATUS;
import org.svip.metrics.tests.enumerations.ATTRIBUTE;

import java.util.Collection;
import java.util.List;

/**
 * file: ResultFactory.java
 * Factory class to create new Results
 *
 * @author Matthew Morrison
 */
public class ResultFactory {

    /**
     * List of attributes associated with result
     */
    private final List<ATTRIBUTE> attributes;

    /**
     * Test name
     */
    private final String test;

    /**
     * Constructor to create a new ResultFactory
     *
     * @param test       name of test
     * @param attributes list of attributes
     */
    public ResultFactory(String test, ATTRIBUTE... attributes) {
        this.attributes = List.of(attributes);
        this.test = test;
    }

    /**
     * Create a new Result that has passed the test with multiple values
     *
     * @param field   the field that was tested
     * @param info    info about the result
     * @param values  the values of the test
     * @param context the name of the component that is being passed through
     * @return a new Result with a pass status
     */
    public Result pass(String field, INFO info, Collection<String> values, String context) {
        Text text = new Text(context, field);
        String message = text.getMessage(info, values);
        String details = text.getDetails(info, values);
        return new Result(this.attributes, this.test, message,
                details, STATUS.PASS);
    }

    /**
     * Create a new Result that has passed the test with a single value
     *
     * @param field   the field that was tested
     * @param info    info about the result
     * @param value   the value of the test
     * @param context the name of the component that is being passed through
     * @return a new Result with a pass status
     */
    public Result pass(String field, INFO info, String value, String context) {
        Text text = new Text(context, field);
        String message = text.getMessage(info, value);
        String details = text.getDetails(info, value);
        return new Result(this.attributes, this.test, message,
                details, STATUS.PASS);
    }

    /**
     * Create a new Result that has passed the test with a single value
     * with custom details
     *
     * @param field   the field that was tested
     * @param info    info about the result
     * @param value   the value of the test
     * @param context the name of the component that is being passed through
     * @param details the custom details that does not fit any INFO value
     */
    public Result passCustom(String field, INFO info, String value, String context, String details) {
        Text text = new Text(context, field);
        String message = text.getMessage(info, value);
        return new Result(this.attributes, this.test, message,
                details, STATUS.PASS);
    }

    /**
     * Create a new Result that has passed the test with multiple values
     *
     * @param field   the field that was tested
     * @param info    info about the result
     * @param values  the values of the test
     * @param context the name of the component that is being passed through
     * @return a new Result with a pass status
     */
    public Result fail(String field, INFO info, Collection<String> values, String context) {
        Text text = new Text(context, field);
        String message = text.getMessage(info, values);
        String details = text.getDetails(info, values);
        return new Result(this.attributes, this.test, message,
                details, STATUS.FAIL);
    }

    /**
     * Create a new Result that has failed the test with a single value
     *
     * @param field   the field that was tested
     * @param info    info about the result
     * @param value   the value of the test
     * @param context the name of the component that is being passed through
     * @return a new Result with a fail status
     */
    public Result fail(String field, INFO info, String value, String context) {
        Text text = new Text(context, field);
        String message = text.getMessage(info, value);
        String details = text.getDetails(info, value);
        return new Result(this.attributes, this.test, message,
                details, STATUS.FAIL);
    }

    /**
     * Create a new Result that has failed the test with a single value
     * with custom details
     *
     * @param field   the field that was tested
     * @param info    info about the result
     * @param value   the value of the test
     * @param context the name of the component that is being passed through
     * @param details the custom details that does not fit any INFO value
     * @return a new Result with a fail status
     */
    public Result failCustom(String field, INFO info, String value, String context, String details) {
        Text text = new Text(context, field);
        String message = text.getMessage(info, value);
        return new Result(this.attributes, this.test, message,
                details, STATUS.FAIL);
    }

    /**
     * Create a new Result that had an error in the test
     *
     * @param field   the field that was tested
     * @param info    info about the result
     * @param value   the value of the test
     * @param context the name of the component that is being passed through
     * @return a new Result with a fail status
     */
    public Result error(String field, INFO info, String value, String context) {
        Text text = new Text(context, field);
        String message = text.getMessage(info, value);
        String details = text.getDetails(info, value);
        return new Result(this.attributes, this.test, message,
                details, STATUS.ERROR);
    }

    /**
     * Create a new Result that had an error in the test
     *
     * @param field   the field that was tested
     * @param info    info about the result
     * @param values  the values of the test
     * @param context the name of the component that is being passed through
     * @return a new Result with a fail status
     */
    public Result error(String field, INFO info, Collection<String> values, String context) {
        Text text = new Text(context, field);
        String message = text.getMessage(info, values);
        String details = text.getDetails(info, values);
        return new Result(this.attributes, this.test, message,
                details, STATUS.ERROR);
    }
}
