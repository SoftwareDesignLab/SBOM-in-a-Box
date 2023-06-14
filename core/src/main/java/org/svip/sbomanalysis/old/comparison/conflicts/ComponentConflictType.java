package org.svip.sbomanalysis.old.comparison.conflicts;

/**
 * Enum to represent the type of conflict between two components
 *
 * @author Matt London
 */
public enum ComponentConflictType {
    /** Component only found in one SBOM */
    COMPONENT_NOT_FOUND,
    /** Component found in both SBOMs, but has different versions */
    COMPONENT_VERSION_MISMATCH,
    /** Component found in both SBOMs, but has different licenses */
    COMPONENT_LICENSE_MISMATCH,
    /** Component found in both SBOMs, but has different publisher */
    COMPONENT_PUBLISHER_MISMATCH,
    /** Component found in both SBOMs, but has different name
     * NOTE: This should never occur because we compare components by name */
    COMPONENT_NAME_MISMATCH,
    /** Component found in both SBOMs, but has different CPE */
    COMPONENT_CPE_MISMATCH,
    /** Component found in both SBOMs, but has different PURL */
    COMPONENT_PURL_MISMATCH,
    /** Component found in both SBOMs, but has different SWID */
    COMPONENT_SWID_MISMATCH,
    /** Component found in both SBOMs, but has different SPDX ID */
    COMPONENT_SPDXID_MISMATCH,
    /** Component found in both SBOMs, conflict was called, but cannot be determined */
    COMPONENT_UNKNOWN_MISMATCH
}
