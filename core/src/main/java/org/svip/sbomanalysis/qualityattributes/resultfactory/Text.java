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
    public String getMessage(INFO info, Collection<String> values){
        StringBuilder message = new StringBuilder();
        StringBuilder infoString = new StringBuilder();
        String valuesString = String.join(",", values);
        switch (info){
            case HAS -> infoString.append(" has the following values: ").append(valuesString);
            case MISSING -> infoString.append("Missing ").append(field).append(" value");
            case VALID -> infoString.append(" are valid: ").append(valuesString);
            case INVALID -> infoString.append(" are invalid: ").append(valuesString);
        }

        if(info.equals(INFO.MISSING)){
            message.append(infoString);
        }
        else{
            message.append(field).append(infoString);
        }
        return message.toString();
    }

    /**
     * Get the message of the result of a single value
     * @param info the info
     * @param value a value to get messages for
     * @return a String message
     */
    public String getMessage(INFO info, String value){
        StringBuilder message = new StringBuilder();
        StringBuilder infoString = new StringBuilder();
        switch (info){
            case HAS -> infoString.append(" has the value: ").append(value);
            case MISSING -> infoString.append("Missing ").append(field).append(" value");
            case VALID -> infoString.append(" is valid: ").append(value);
            case INVALID -> infoString.append(" is invalid: ").append(value);
        }

        if(info.equals(INFO.MISSING)){
            message.append(infoString);
        }
        else{
            message.append(field).append(infoString);
        }
        return message.toString();
    }

    /**
     * Get details of multiple values from a result
     * @param info the info
     * @param values a collection of values to get details for
     * @return a String of details
     */
    public String getDetails(INFO info, Collection<String> values){
        StringBuilder details = new StringBuilder();
        StringBuilder infoString = new StringBuilder();
        String valuesString = String.join(",", values);
        switch (info){
            case HAS -> infoString.append(" has the following values: ").append(valuesString);
            case MISSING -> infoString.append("Missing ").append(field).append(" value");
            case VALID -> infoString.append(" are valid: ").append(valuesString);
            case INVALID -> infoString.append(" are invalid: ").append(valuesString);
        }

        if(info.equals(INFO.MISSING)){
            details.append(infoString);
            details.append(" with the following context: ").append(context);
        }
        else{
            details.append(field).append(infoString);
            details.append(" with the following context: ").append(context);
        }
        return details.toString();
    }

    /**
     * Get details of a single value from a result
     * @param info the info
     * @param value a value to get details for
     * @return a String of details
     */
    public String getDetails(INFO info, String value){
        StringBuilder details = new StringBuilder();
        StringBuilder infoString = new StringBuilder();
        switch (info){
            case HAS -> infoString.append(" has the value: ").append(value);
            case MISSING -> infoString.append("Missing ").append(field).append(" value");
            case VALID -> infoString.append(" is valid: ").append(value);
            case INVALID -> infoString.append(" is invalid: ").append(value);
        }

        if(info.equals(INFO.MISSING)){
            details.append(infoString);
            details.append(" with the following context: ").append(context);
        }
        else{

            details.append(field).append(infoString);
            details.append(" with the following context: ").append(context);
        }
        return details.toString();
    }
}
