package org.svip.repair.repair;

import org.svip.sbom.model.interfaces.generics.SBOM;

import java.util.Map;

/**
 * Name: Repair.java
 * Description: Interface for Repair.
 *
 * @authors Tyler Drake
 */
public interface Repair {

    /**
     * Repairs the given SBOM of a specified schema. The function
     * received a JSON block from the frontend that contains fixes
     * that have been requested for the SBOM. These fixes will then
     * be applied to that SBOM and returned.
     *
     * @param uid   UID of the SBOM.
     * @param sbom  The SBOM Object.
     * @return      The repaired SBOM Object.
     */
    SBOM repairSBOM(String uid, SBOM sbom, Map<String, Map<String, String>> repairs);

}
