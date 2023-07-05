package org.svip.sbomvex;

import org.svip.sbom.model.interfaces.generics.SBOMPackage;
import org.svip.sbomvex.database.interfaces.VulnerabilityDBClient;
import org.svip.sbomvex.model.VEX;

/**
 * file: APIController.java
 * API Controller class to generate a VEX Document
 *
 * @author Matthew Morrison
 */
public class APIController implements VulnerabilityDBClient {

    /**
     * Generate a new VEX Package
     * @param sbomPackage the SBOMPackage to create the VEX Document
     * @return a VEX object
     */
    @Override
    public VEX generateVEX(SBOMPackage sbomPackage) {
        return null;
    }
}
