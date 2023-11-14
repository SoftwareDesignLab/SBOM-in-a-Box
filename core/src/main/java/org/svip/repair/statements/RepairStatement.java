package org.svip.repair.statements;

import org.svip.metrics.pipelines.QualityReport;
import org.svip.repair.fix.Fix;
import org.svip.sbom.model.interfaces.generics.SBOM;

import java.util.List;
import java.util.Map;

/**
 * Name: RepairStatement.java
 * Description: Interface for RepairStatement.
 *
 * @author Tyler Drake
 * @author  Justin Jantzi
 */
public interface RepairStatement {

    /**
     * Generates a repair statement for an SBOM of a specified schema.
     * This function will generate a QualityReport using one of the
     * Metrics Pipelines, then use that report to find which fields on
     * an SBOM are 'damaged'. Potential repair options will be appended and
     * then sent to the front end.
     *
     * @param sbom  The SBOM Object.
     * @return      Quality Report with fixes appended
     */
    QualityReport generateRepairStatement(SBOM sbom) throws Exception;

}
