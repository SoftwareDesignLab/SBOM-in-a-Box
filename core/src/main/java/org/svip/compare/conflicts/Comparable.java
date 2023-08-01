package org.svip.compare.conflicts;

import java.util.List;

/**
 * Comparable interface for comparing composite SBOM fields that cannot use a hashcode
 *
 * @author Derek Garcia
 **/

public interface Comparable {
    /**
     * Compare to another Comparable Object
     *
     * @param o Object
     * @return List of Conflicts
     */
    List<Conflict> compare(Comparable o);

    /**
     * Test if Object is equal to current
     *
     * @param o Object
     * @return List of Conflicts
     */
    boolean equals(Object o);
}
