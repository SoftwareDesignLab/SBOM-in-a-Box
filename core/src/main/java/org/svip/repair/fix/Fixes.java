package org.svip.repair.fix;

import org.svip.metrics.resultfactory.Result;
import org.svip.sbom.model.interfaces.generics.SBOM;

import java.util.List;

/**
 * For each Metric test, there exists a fix
 *
 * @author Juan Francisco Patino
 */
public interface Fixes {

    /**
     * @param result        object from quality report
     * @param sbom          sbom from quality report
     * @param repairSubType key from quality report map most directly relating to the repair type
     * @return list of potential fixes
     */
    List<Fix<?>> fix(Result result, SBOM sbom, String repairSubType);
}
