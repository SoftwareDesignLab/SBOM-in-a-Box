package org.svip.repair;

import org.svip.repair.repair.Repair;
import org.svip.repair.repair.RepairCDX14;
import org.svip.repair.repair.RepairSPDX23;
import org.svip.repair.fix.Fix;
import org.svip.repair.statements.RepairStatement;
import org.svip.repair.statements.RepairStatementCDX14;
import org.svip.repair.statements.RepairStatementSPDX23;
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

    public RepairController(){}


    public Map<String, Map<String, List<Fix<?>>>> generateStatement(SBOM sbom, String uid) {
        RepairStatement rs = getStatement(sbom);
        return rs.generateRepairStatement(uid, sbom);
    }

    public SBOM repairSBOM(SBOM sbom, String uid, Map<String, Map<String, String>> repairs) {
        Repair r = getRepair(sbom);
        return r.repairSBOM(uid, sbom, repairs);
    }

    public RepairStatement getStatement(SBOM sbom) {

        switch(sbom.getFormat()) {
            case "SPDX" -> {
                return new RepairStatementSPDX23();
            }
            case "CycloneDX" -> {
                return new RepairStatementCDX14();
            }
            default -> {
                return null;
            }
        }

    }

    public Repair getRepair(SBOM sbom) {

        switch(sbom.getFormat()) {
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
