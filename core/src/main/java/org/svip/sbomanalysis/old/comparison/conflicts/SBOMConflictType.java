package org.svip.sbomanalysis.old.comparison.conflicts;

/**
 * Types of conflicts that can occur in an SBOM
 *
 * @author Matt London
 */
public enum SBOMConflictType {
    /** When the supplier of the code are not the same (publisher) */
    SUPPLIER_MISMATCH,
    /** When the SBOMs have different authors */
    AUTHOR_MISMATCH,
    /** When the SBOMs have different timestamps */
    TIMESTAMP_MISMATCH,
    /** When the SBOMs have different origin formats */
    ORIGIN_FORMAT_MISMATCH,
    /** SBOMs have different schema versions */
    SCHEMA_VERSION_MISMATCH,
    /** SBOMs have different versions */
    SBOM_VERSION_MISMATCH,
    /** When SBOMs have different serial numbers */
    SERIAL_NUMBER_MISMATCH,

}
