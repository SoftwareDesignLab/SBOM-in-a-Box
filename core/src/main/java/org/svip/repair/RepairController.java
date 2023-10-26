package org.svip.repair;

import org.svip.repair.repair.Repair;
import org.svip.repair.fix.Fix;
import org.svip.repair.repair.RepairSPDX23CDX14;
import org.svip.repair.statements.RepairStatement;
import org.svip.repair.statements.RepairStatementSPDX23CDX14;
import org.svip.sbom.model.interfaces.generics.SBOM;

import java.util.List;
import java.util.Map;

/**
 * Name: RepairController.java
 * Description: Main driver class for SBOM repair
 *
 * @author Tyler Drake
 */
public class RepairController {

    public RepairController() {
    }


    /**
     * Generate a repair statement
     *
     * @param sbom sbom to repair
     * @param uid  UID of sbom
     * @return repair statement
     */
    public Map<String, Map<String, List<Fix<?>>>> generateStatement(SBOM sbom, String uid) throws Exception {
        RepairStatement rs = getStatement(sbom);
        return rs.generateRepairStatement(uid, sbom);
    }

    /**
     * Repair this SBOM with chosen repairs
     *
     * @param sbom    sbom to repair
     * @param repairs chosen repairs from SBOM
     * @return repaired SBOM
     */
    public SBOM repairSBOM(SBOM sbom, Map<String, Map<String, List<Fix<?>>>> repairs) {
        Repair r = getRepair(sbom);
        return r.repairSBOM(sbom, repairs);
    }

    /**
     * Get repair statement class for this SBOM
     *
     * @param sbom sbom to repair
     * @return repair statement class
     */
    public RepairStatement getStatement(SBOM sbom) {

        // Get the correct RepairStatement model based on the SBOM's format
        switch (sbom.getFormat()) {

            // For SPDX 2.3 and CycloneDX 1.4
            case "SPDX", "CycloneDX" -> {
                return new RepairStatementSPDX23CDX14();
            }
            // For none found
            default -> {
                return null;
            }
        }

    }

    /**
     * Get repair class for this SBOM
     *
     * @param sbom sbom to repair
     * @return repair class
     */
    public Repair getRepair(SBOM sbom) {

        // Get the correct Repair class based on the format
        switch (sbom.getFormat()) {
            // For SPDX 2.3 and CycloneDX 1.4
            case "SPDX", "CycloneDX" -> {
                return new RepairSPDX23CDX14();
            }
            // For none found
            default -> {
                return null;
            }
        }

    }

}
