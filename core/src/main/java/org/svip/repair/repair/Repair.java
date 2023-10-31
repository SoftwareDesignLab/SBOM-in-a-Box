package org.svip.repair.repair;

import org.svip.repair.fix.Fix;
import org.svip.sbom.model.interfaces.generics.SBOM;

import java.util.List;
import java.util.Map;

/**
 * Name: Repair.java
 * Description: Interface for Repair.
 *
 * @author Tyler Drake
 * @author Justin Jantzi
 */
public interface Repair {

    /**
     * Repairs the given SBOM of a specified schema. The function
     * received a JSON block from the frontend that contains fixes
     * that have been requested for the SBOM. These fixes will then
     * be applied to that SBOM and returned.
     *
     * @param sbom      The SBOM Object.
     * @param repairs   The fixes to make
     * @return          The repaired SBOM Object.
     */
    SBOM repairSBOM(SBOM sbom, Map<Integer, List<Fix<?>>> repairs);

}
