package org.svip.sbomanalysis.old;


import org.svip.sbom.model.old.SBOM;

import java.util.HashSet;
import java.util.Set;

/**
 * Conflict between two SBOMs and their trivial information
 *
 * @author Matt London
 */
public class SBOMConflict {
    /** Snapshot of the two SBOMs that have a conflict */
    private SBOM aSBOM;
    private SBOM bSBOM;

    /** The types of conflicts found in these SBOMs */
    private Set<SBOMConflictType> conflictTypes;

    /**
     * Constructor to copy over trivial data
     *
     * @param aSBOM First SBOM to compare
     * @param bSBOM Second SBOM to compare
     */
    public SBOMConflict(SBOM aSBOM, SBOM bSBOM) {
        this.aSBOM = new SBOM(aSBOM);
        this.bSBOM = new SBOM(bSBOM);

        this.conflictTypes = new HashSet<>();

        assignConflictTypes();
    }

    /**
     * Detect conflicts within the two sboms and add them to the set
     */
    public void assignConflictTypes() {
        if (aSBOM.getOriginFormat() != null && !aSBOM.getOriginFormat().equals(bSBOM.getOriginFormat())) {
            conflictTypes.add(SBOMConflictType.ORIGIN_FORMAT_MISMATCH);
        }
        if (aSBOM.getSpecVersion() != null && !aSBOM.getSpecVersion().equals(bSBOM.getSpecVersion())) {
            conflictTypes.add(SBOMConflictType.SCHEMA_VERSION_MISMATCH);
        }
        if (aSBOM.getSerialNumber() != null && !aSBOM.getSerialNumber().equals(bSBOM.getSerialNumber())) {
            conflictTypes.add(SBOMConflictType.SERIAL_NUMBER_MISMATCH);
        }
        if (aSBOM.getTimestamp() != null && !aSBOM.getTimestamp().equals(bSBOM.getTimestamp())) {
            conflictTypes.add(SBOMConflictType.TIMESTAMP_MISMATCH);
        }
        if (aSBOM.getSupplier() != null && !aSBOM.getSupplier().equals(bSBOM.getSupplier())) {
            conflictTypes.add(SBOMConflictType.SUPPLIER_MISMATCH);
        }
        if (aSBOM.getSbomVersion() != null && !aSBOM.getSbomVersion().equals(bSBOM.getSbomVersion())) {
            conflictTypes.add(SBOMConflictType.SBOM_VERSION_MISMATCH);
        }
    }

    /**
     * Generate a string summary of the conflict
     *
     * @return String representation of a conflict
     */
    public String getConflictString(SBOMConflictType conflictType) {
        StringBuilder conflictString = new StringBuilder();
        switch (conflictType) {
            case SUPPLIER_MISMATCH:
                conflictString.append("Supplier Mismatch:\n");
                conflictString.append("+ ").append(aSBOM.getSupplier()).append("\n");
                conflictString.append("- ").append(bSBOM.getSupplier()).append("\n");
                break;
            case AUTHOR_MISMATCH:
                // TODO author currently not implemented
                conflictString.append("Author Mismatch:\n");

                break;
            case TIMESTAMP_MISMATCH:
                conflictString.append("Timestamp Mismatch:\n");
                conflictString.append("+ ").append(aSBOM.getTimestamp()).append("\n");
                conflictString.append("- ").append(bSBOM.getTimestamp()).append("\n");
                break;
            case ORIGIN_FORMAT_MISMATCH:
                conflictString.append("Origin Format Mismatch:\n");
                conflictString.append("+ ").append(aSBOM.getOriginFormat()).append("\n");
                conflictString.append("- ").append(bSBOM.getOriginFormat()).append("\n");
                break;
            case SCHEMA_VERSION_MISMATCH:
                conflictString.append("Schema Version Mismatch:\n");
                conflictString.append("+ ").append(aSBOM.getSpecVersion()).append("\n");
                conflictString.append("- ").append(bSBOM.getSpecVersion()).append("\n");
                break;
            case SBOM_VERSION_MISMATCH:
                conflictString.append("SBOM Version Mismatch:\n");
                conflictString.append("+ ").append(aSBOM.getSbomVersion()).append("\n");
                conflictString.append("- ").append(bSBOM.getSbomVersion()).append("\n");
                break;
            case SERIAL_NUMBER_MISMATCH:
                conflictString.append("Serial Number Mismatch:\n");
                conflictString.append("+ ").append(aSBOM.getSerialNumber()).append("\n");
                conflictString.append("- ").append(bSBOM.getSerialNumber()).append("\n");
                break;
        }

        return conflictString.toString();
    }

    ///
    /// Getters and Setters
    ///

    public Set<SBOMConflictType> getConflicts() {
        return this.conflictTypes;
    }

    ///
    /// Overrides
    ///

    // Stringify this entire object
    @Override
    public String toString() {
        StringBuilder conflictString = new StringBuilder("SBOM Conflicts:\n");
        for (SBOMConflictType conflictType : conflictTypes) {
            conflictString.append(getConflictString(conflictType));
        }

        return conflictString.toString();
    }

}
