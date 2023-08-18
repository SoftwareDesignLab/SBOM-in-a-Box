package org.svip.repair.fix;

import org.svip.metrics.resultfactory.Result;

import java.util.List;

/**
 * For each Metric test, there exists a fix
 *
 * @author Juan Francisco Patino
 */
public interface Fixes {

    /**
     * @param result object from quality report
     * @return list of potential fixes
     */
    List<Fix<?>> fix(Result result);
}
