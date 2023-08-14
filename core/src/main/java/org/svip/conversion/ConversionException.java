package org.svip.conversion;

import org.svip.utils.Debug;

public class ConversionException extends Exception {
    public ConversionException(String message) {
        super(message);
        Debug.log(Debug.LOG_TYPE.ERROR, message);
    }
}
