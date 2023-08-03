package org.svip.metrics.resultfactory;

import org.svip.metrics.resultfactory.enumerations.INFO;

import java.util.Collection;

/**
 * file: Text.java
 * Class that holds message details about test results
 *
 * @author Matthew Morrison
 * @author Thomas Roman
 */
public class Text {

    /**
     * Holds the context of the result
     */
    private final String context;

    /**
     * The field that is correlated to the result
     */
    private final String field;

    /**
     * Constructor to build a new Text object
     *
     * @param context the context message
     * @param field   the field
     */
    public Text(String context, String field) {
        this.context = context;
        this.field = field;
    }

    /**
     * Get the message of the result of multiple values
     *
     * @param info   the info
     * @param values a collection of values to get messages for
     * @return a String of messages
     */
    public String getMessage(INFO info, Collection<String> values) {
        StringBuilder message = new StringBuilder();
        switch (info) {
            case HAS -> message.append(context).append(" has ").append(field).append("s");
            case MISSING -> message.append(context).append(" has missing ").append(field).append("s");
            case NULL -> message.append(field).append(" was a null value");
        }

        return message.toString();
    }

    /**
     * Get the message of the result of a single value
     *
     * @param info  the info
     * @param value a value to get messages for
     * @return a String message
     */
    public String getMessage(INFO info, String value) {
        StringBuilder message = new StringBuilder();
        switch (info) {
            case HAS -> message.append(context).append(" has a ").append(field);
            case MISSING -> message.append(context).append(" is missing ").append(field);
            case VALID -> message.append(value).append(" is a valid ").append(field);
            case INVALID -> message.append(value).append(" is an invalid ").append(field);
            case NULL -> message.append(field).append(" was a null value");
            case ERROR -> message.append(field).append(" had an error");
            case MATCHING -> message.append(field).append(" matches Component value");
            case NOT_MATCHING -> message.append(field).append(" does not match Component value");
            case DIFF_HASH_ALG -> message.append(field).append(" ").append(value).append(" Hash");
        }

        return message.toString();
    }

    /**
     * Get details of multiple values from a result
     *
     * @param info   the info
     * @param values a collection of values to get details for
     * @return a String of details
     */
    public String getDetails(INFO info, Collection<String> values) {
        StringBuilder details = new StringBuilder();
        String valuesString;
        if (values != null) {
            valuesString = String.join(", ", values);
        } else {
            valuesString = "";
        }
        switch (info) {
            case HAS -> {
                assert values != null;
                details.append(context).append(" has ")
                        .append(values.size()).append(" ").append(field)
                        .append("s: ").append(valuesString);
            }
            case MISSING -> details.append(context)
                    .append(" is missing the following field: ")
                    .append(field);
            case NULL -> details.append(field).append(" was a null value");
        }

        return details.toString();
    }

    /**
     * Get details of a single value from a result
     *
     * @param info  the info
     * @param value a value to get details for
     * @return a String of details
     */
    public String getDetails(INFO info, String value) {
        StringBuilder details = new StringBuilder();

        switch (info) {
            case HAS -> details.append(field).append(": ").append(value);
            //TODO implement for MISSING
            case MISSING -> details.append(context)
                    .append(" is missing the following field: ")
                    .append(field);
            case VALID -> details.append(value).append(" is a valid ").append(field);
            case INVALID -> details.append(value).append(" is an invalid ").append(field);
            case NULL -> details.append(field).append(" was a null value");
            case ERROR -> details.append(value).append(" had an error producing " +
                    "the following object: ").append(field);
            case NOT_MATCHING, MATCHING -> details.append("Expected: ").append(value).append(" ")
                    .append("Actual: ").append(this.context);
        }
        return details.toString();
    }
}
