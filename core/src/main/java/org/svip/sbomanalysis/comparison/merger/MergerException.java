package org.svip.sbomanalysis.comparison.merger;

import org.svip.utils.Debug;

public class MergerException extends Exception {
    public MergerException(String message) {
        super(message);
        Debug.log(Debug.LOG_TYPE.ERROR, message);
    }
}