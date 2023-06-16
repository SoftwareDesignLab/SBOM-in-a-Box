// pliu package org.nvip.plugfest.tooling.qa.tests;
package org.svip.sbomanalysis.qualityattributes.tests;

// pliu import org.nvip.plugfest.tooling.sbom.SBOM;
import org.svip.sbom.model.SBOM;


import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * file: MetricTest0.java
 *
 * Template for MetricTests
 *
 * @author Derek Garcia
 */
public abstract class MetricTest {

    /**
     * Utility function that checks if an object is null, a string is blank or a collection is empty
     *
     * todo maybe remove?
     * @param o Object to test
     * @return true if empty, false otherwise
     */
    protected boolean isEmptyOrNull(Object o){
        // Object is null
        if(o == null)
            return true;

        // Check for empty string
        if(o instanceof String)
            return o.equals("");

        // Check for empty collection
        if(o instanceof Collection<?>)
            return ((Collection<?>) o).isEmpty();

        // Object that isn't null
        return false;
    }

    /**
     * Test the given SBOM
     *
     * @param sbom SBOM to test
     * @return Collection of Results
     */
    public abstract List<Result> test(SBOM sbom);
}
