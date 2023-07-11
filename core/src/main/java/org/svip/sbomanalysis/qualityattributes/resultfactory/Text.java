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
        switch (info){
            case HAS -> message.append(context).append(" has ").append(field).append("s");
            case MISSING -> message.append(context).append(" has missing ").append(field).append("s");
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
        switch (info){
            case HAS -> message.append(context).append(" has a ").append(field);
            case MISSING -> message.append(context).append(" is a missing ").append(field);
            case VALID -> message.append(value).append(" is a valid ").append(field);
            case INVALID -> message.append(value).append(" is an invalid ").append(field);
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
        String valuesString = String.join(",", values);
        switch (info){
            case HAS -> details.append(context).append(" has ").append(values.size())
                    .append(field).append("s: ").append(valuesString);
            // TODO implement for MISSING
            case MISSING -> details.append("TODO");
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
            case HAS -> details.append(field).append(": ").append(value);
            //TODO implement for MISSING, VALID, INVALID
            case MISSING -> details.append("TODO");
            case VALID -> details.append("TODO");
            case INVALID -> details.append("TODO");
        }
        return details.toString();
    }
}
