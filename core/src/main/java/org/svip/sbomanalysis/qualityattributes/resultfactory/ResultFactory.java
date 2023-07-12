package org.svip.sbomanalysis.qualityattributes.resultfactory;

import org.svip.sbomanalysis.qualityattributes.newtests.enumerations.ATTRIBUTE;
import org.svip.sbomanalysis.qualityattributes.resultfactory.enumerations.INFO;
import org.svip.sbomanalysis.qualityattributes.resultfactory.enumerations.STATUS;

import java.util.Collection;
import java.util.List;

/**
 * file: ResultFactory.java
 * Factory class to create new Results
 *
 * @author Matthew Morrison
 */
public class ResultFactory {

    /**List of attributes associated with result*/
    private final List<ATTRIBUTE> attributes;

    /**Test name*/
    private final String test;

    /**
     * Constructor to create a new ResultFactory
     * @param attributes list of attributes
     * @param test name of test
     */
    public ResultFactory(String test, ATTRIBUTE... attributes){
        this.attributes = List.of(attributes);
        this.test = test;
    }

    /**
     * Create a new Result that has passed the test with multiple values
     * @param field the field that was tested
     * @param info info about the result
     * @param values the values of the test
     * @return a new Result with a pass status
     */
    public Result pass(String field, INFO info, Collection<String> values){
        //TODO context?
        Text text = new Text(values.toString(), field);
        String message = text.getMessage(info, values);
        String details = text.getDetails(info, values);
        return new Result(this.attributes, this.test, message,
                details, STATUS.PASS);
    }

    /**
     * Create a new Result that has passed the test with a single value
     * @param field the field that was tested
     * @param info info about the result
     * @param value the value of the test
     * @return a new Result with a pass status
     */
    public Result pass(String field, INFO info, String value){
        //TODO context?
        Text text = new Text(value, field);
        String message = text.getMessage(info, value);
        String details = text.getDetails(info, value);
        return new Result(this.attributes, this.test, message,
                details, STATUS.PASS);
    }

    /**
     * Create a new Result that has passed the test with multiple values
     * @param field the field that was tested
     * @param info info about the result
     * @param values the values of the test
     * @return a new Result with a pass status
     */
    public Result fail(String field, INFO info, Collection<String> values){
        //TODO context?
        Text text = new Text(values.toString(), field);
        String message = text.getMessage(info, values);
        String details = text.getDetails(info, values);
        return new Result(this.attributes, this.test, message,
                details, STATUS.FAIL);
    }

    /**
     * Create a new Result that has failed the test with a single value
     * @param field the field that was tested
     * @param info info about the result
     * @param value the value of the test
     * @return a new Result with a fail status
     */
    public Result fail(String field, INFO info, String value){
        //TODO context?
        Text text = new Text(value, field);
        String message = text.getMessage(info, value);
        String details = text.getDetails(info, value);
        return new Result(this.attributes, this.test, message,
                details, STATUS.FAIL);
    }

    /**
     * Create a new Result that had an error in the test
     * @param field the field that was tested
     * @param info info about the result
     * @param value the value of the test
     * @return a new Result with a fail status
     */
    public Result error(String field, INFO info, String value){
        Text text = new Text(value, field);
        String message = text.getMessage(info, value);
        String details = text.getDetails(info, value);
        return new Result(this.attributes, this.test, message,
                details, STATUS.ERROR);
    }

    /**
     * Create a new Result that had an error in the test
     * @param field the field that was tested
     * @param info info about the result
     * @param values the values of the test
     * @return a new Result with a fail status
     */
    public Result error(String field, INFO info, Collection<String> values){
        Text text = new Text(values.toString(), field);
        String message = text.getMessage(info, values);
        String details = text.getDetails(info, values);
        return new Result(this.attributes, this.test, message,
                details, STATUS.ERROR);
    }
}
