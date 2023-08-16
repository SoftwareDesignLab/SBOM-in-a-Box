package org.svip.repair.statements;

import org.svip.sbom.model.interfaces.generics.SBOM;

import java.util.Map;

/**
 * Name: RepairStatement.java
 * Description: Interface for RepairStatement.
 *
 * @authors Tyler Drake
 */
public interface RepairStatement {

    /**
     * Generates a repair statement for an SBOM of a specified schema.
     * This function will generate a QualityReport using one of the
     * Metrics Pipelines, then use that report to find which fields on
     * an SBOM are 'damaged'. Potential repair options will be generated
     * then sent to the front end.
     *
     * @param uid   UID of the SBOM.
     * @param sbom  The SBOM Object.
     * @return      A nested map of potential fixes.
     */
    Map<String, Map<String, String>> generateRepairStatement(String uid, SBOM sbom);

}
