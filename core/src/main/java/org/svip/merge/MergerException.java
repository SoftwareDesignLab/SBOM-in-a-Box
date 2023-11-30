package org.svip.merge;

import org.svip.utils.Debug;

/**
 * Name: MergerException.java
 * Description: Exception class for logging errors in the Merge feature
 *
 * @author Tyler Drake
 */
public class MergerException extends Exception {

    /**
     * Handles Merger Exceptions
     *
     * @param message
     */
    public MergerException(String message) {
        super(message);
        Debug.log(Debug.LOG_TYPE.ERROR, message);
    }

}