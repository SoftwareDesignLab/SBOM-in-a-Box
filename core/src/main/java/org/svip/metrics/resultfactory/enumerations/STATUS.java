package org.svip.metrics.resultfactory.enumerations;

/**
 * file: STATUS.java
 * Enumeration that holds the status of a Result
 *
 * @author Matthew Morrison
 */
public enum STATUS {
    PASS(1),
    FAIL(0),
    ERROR(-1);

    /*
    Utilities to convert to ints
     */
    private final int code;

    STATUS(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }
}
