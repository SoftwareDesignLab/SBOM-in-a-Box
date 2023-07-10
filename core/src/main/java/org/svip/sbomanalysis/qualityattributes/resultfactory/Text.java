package org.svip.sbomanalysis.qualityattributes.resultfactory;

import org.svip.sbomanalysis.qualityattributes.resultfactory.enumerations.INFO;

import java.util.Collection;

/**
 * file: Text.java
 * Class that holds message details about test results
 *
 * @author Matthew Morrison
 */
public class Text {

    /**Holds the context of the result*/
    private final String context;

    /**The field that is correlated to the result*/
    private final String field;

    /**
     * Constructor to build a new Text object
     * @param context the context message
     * @param field the field
     */
    public Text(String context, String field){
        this.context = context;
        this.field = field;
    }

    /**
     * Get the message of the result of multiple values
     * @param info the info
     * @param values a collection of values to get messages for
     * @return a String of messages
     */
    //TODO implement
    public String getMessage(INFO info, Collection<String> values){
        return null;
    }

    /**
     * Get the message of the result of a single value
     * @param info the info
     * @param value a value to get messages for
     * @return a String message
     */
    //TODO implement
    public String getMessage(INFO info, String value){
        return null;
    }

    /**
     * Get the details of multiple values
     * @param info the info
     * @param values a collection of values to get details for
     * @return a String of details
     */
    //TODO implement
    public String getDetails(INFO info, Collection<String> values){
        return null;
    }

    /**
     * Get the details of a single value
     * @param info the info
     * @param value a value to get details for
     * @return a String of details
     */
    //TODO implement
    public String getDetails(INFO info, String value){
        return null;
    }
}
