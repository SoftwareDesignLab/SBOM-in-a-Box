package org.svip.sbomfactory.translators;

import org.svip.sbom.model.old.Component;
import org.svip.sbom.model.old.SBOM;

/**
 * File: Utils.java
 * Utility class for Translator tests
 *
 * @author Juan Francisco Patino
 */
public class Utils {

    /**
     * Helper method to check that SBOM metadata does not contain app tools
     * @param sbom to check
     * @return whether metadata contains AppTools or not
     */
    public static boolean checkForAppTools(SBOM sbom) {
        for (String m: sbom.getMetadata().values()
        ) {
            if(sbom.checkForTool(m) != null)
                return true;
        }
        return false;
    }

    /**
     * Helper method to check that SBOMs that should
     * have no licenses do not have that field with any licenses added
     * @param sbom to check
     * @return true if SBOM contains no licenses throughout its components
     */
    public static boolean noLicensesCheck(SBOM sbom){
        for (Component c: sbom.getAllComponents())
            if(c.getLicenses().size() > 0)
                return false;
        return true;
    }

}
