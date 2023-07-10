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
    private List<ATTRIBUTE> attributes;

    /**Test name*/
    private String test;

    /**
     * Constructor to create a new ResultFactory
     * @param attributes list of attributes
     * @param test name of test
     */
    public ResultFactory(List<ATTRIBUTE> attributes, String test){
        this.attributes = attributes;
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
        //TODO add context? What is context?
        Text text = new Text(null, field);
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
        //TODO add context? What is context?
        Text text = new Text(null, field);
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
        //TODO add context? What is context?
        Text text = new Text(null, field);
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
        //TODO add context? What is context?
        Text text = new Text(null, field);
        String message = text.getMessage(info, value);
        String details = text.getDetails(info, value);
        return new Result(this.attributes, this.test, message,
                details, STATUS.FAIL);
    }
}
