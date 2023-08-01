package org.svip.compare.conflicts;

public enum MismatchType {
    /**
     * Types of conflicts that can occur in SBOM or Component comparison
     *
     * @author Matt London
     * @author Thomas Roman
     */
    // Special case
    MISSING,
    /** Component found in both SBOMs, but has different versions */
    VERSION_MISMATCH,
    /** Component found in both SBOMs, but has different licenses */
    LICENSE_MISMATCH,
    /** When the publisher of the code are not the same */
    PUBLISHER_MISMATCH,
    /** When the supplier of the code are not the same */
    SUPPLIER_MISMATCH,
    /** Component found in both SBOMs, but has different name */
    NAME_MISMATCH,
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
    /** Component found in both SBOMs, but has different PURL */
    PURL_MISMATCH,
    /** Component found in both SBOMs, but has different CPE */
    CPE_MISMATCH,
    /** Component found in both SBOMs, but has different SWID */
    SWID_MISMATCH,
    /** Component found in both SBOMs, but has different SPDX ID */
    SPDXID_MISMATCH,
    /** Component found in both SBOMs, but has different Hashes */
    HASH_MISMATCH,
    /** Component found in both SBOMs, conflict was called, but cannot be determined */
    COMPONENT_UNKNOWN_MISMATCH,
    // todo replace with more detailed field
    MISC_MISMATCH
}
