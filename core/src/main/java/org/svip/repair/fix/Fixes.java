package org.svip.repair.fix;

import org.svip.metrics.resultfactory.Result;
import org.svip.sbom.model.interfaces.generics.SBOM;

import java.util.List;

/**
 * For each Metric test, there exists a fix
 *
 * @author Juan Francisco Patino
 * @author Justin Jantzi
 */
public interface Fixes<T> {

    /**
     * @param result        object from quality report
     * @param sbom          sbom from quality report
     * @param componentName key from quality report map most directly relating to the component or metadata
     * @return list of potential fixes
     */
    List<Fix<T>> fix(Result result, SBOM sbom, String componentName) throws Exception;
}
