package org.svip.sbomanalysis.comparison.conflicts;

import java.util.List;

/**
 * Comparable interface for comparing SBOM fields
 *
 * @author Derek Garcia
 **/

public interface Comparable {
    List<Conflict> compare(Comparable o);
    boolean equals(Object o);
}
