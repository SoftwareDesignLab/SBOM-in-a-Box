package org.svip.sbomvex.vexstatement.status;

/**
 * file: Justification.java
 * An enumeration that holds all the possible justifications
 * of a VEX statement with a NOT_AFFECTED status
 *
 * @author Matthew Morrison
 */
public enum Justification {
    COMPONENT_NOT_PRESENT,
    VULNERABLE_CODE_NOT_PRESENT,
    VULNERABLE_CODE_NOT_IN_EXECUTE_PATH,
    VULNERABLE_CODE_CANNOT_BE_CONTROLLED_BY_ADVERSARY,
    INLINE_MITIGATIONS_ALREADY_EXIST,
    NOT_APPLICABLE
}
