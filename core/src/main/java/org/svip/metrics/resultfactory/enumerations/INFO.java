package org.svip.metrics.resultfactory.enumerations;

/**
 * file: INFO.java
 * Enumeration that holds the types of information a result could have
 *
 * @author Matthew Morrison
 * @author Thomas Roman
 */
public enum INFO {
    MISSING,
    HAS,
    VALID,
    INVALID,
    NULL,
    ERROR,
    MATCHING,
    NOT_MATCHING,
    DIFF_NULL_VALUE,
    DIFF_NULL_VALUE_IN_SET,
    DIFF_HASH_ALG,
    DIFF_NULL_HASH
}
