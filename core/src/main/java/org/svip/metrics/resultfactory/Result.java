package org.svip.metrics.resultfactory;

import org.svip.metrics.resultfactory.enumerations.STATUS;
import org.svip.metrics.tests.enumerations.ATTRIBUTE;

import java.util.List;

/**
 * File: Result.java
 * Storage for the result of a Test
 *
 * @author Derek Garcia
 * @author Matthew Morrison
 */
public class Result {

    private List<ATTRIBUTE> attributes;

    private String test;

    private String message;

    private String details;

    private STATUS status;

    /**
     * Create a new Result
     * @param attributes list of attributes associated with result
     * @param test test name
     * @param message message about the result
     * @param details details about the result
     * @param status the status of the result
     */
    public Result(List<ATTRIBUTE> attributes, String test,
                  String message, String details, STATUS status){
        this.attributes = attributes;
        this.test = test;
        this.message = message;
        this.details = details;
        this.status = status;
    }

    /**Getters*/

    public List<ATTRIBUTE> getAttributes() {
        return this.attributes;
    }

    public String getTest(){
        return this.test;
    }

    public String getMessage(){
        return this.message;
    }

    public String getDetails(){
        return this.details;
    }

    public STATUS getStatus() {
        return this.status;
    }
}
