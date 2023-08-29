package org.svip.repair;

import org.svip.repair.repair.Repair;
import org.svip.repair.repair.RepairCDX14;
import org.svip.repair.repair.RepairSPDX23;
import org.svip.repair.fix.Fix;
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
    public Map<String, Map<String, List<Fix<?>>>> generateStatement(SBOM sbom, String uid) {
        RepairStatement rs = getStatement(sbom);
        return rs.generateRepairStatement(uid, sbom);
    }

    /**
     * Repair this SBOM with chosen repairs
     *
     * @param sbom    sbom to repair
     * @param uid     uid of sbom
     * @param repairs chosen repairs from SBOM
     * @return repaired SBOM
     */
    public SBOM repairSBOM(SBOM sbom, String uid, Map<String, Map<String, List<Fix<?>>>> repairs) {
        Repair r = getRepair(sbom);
        return r.repairSBOM(uid, sbom, repairs);
    }

    /**
     * Get repair statement class for this SBOM
     *
     * @param sbom sbom to repair
     * @return repair statement class
     */
    public RepairStatement getStatement(SBOM sbom) {

        switch (sbom.getFormat()) {
            case "SPDX", "CycloneDX" -> {
                return new RepairStatementSPDX23CDX14();
            }
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

        switch (sbom.getFormat()) {
            case "SPDX" -> {
                return new RepairSPDX23();
            }
            case "CycloneDX" -> {
                return new RepairCDX14();
            }
            default -> {
                return null;
            }
        }

    }

}
