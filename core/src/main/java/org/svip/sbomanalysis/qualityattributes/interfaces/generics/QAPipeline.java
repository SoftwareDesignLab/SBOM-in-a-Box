package org.svip.sbomanalysis.qualityattributes.interfaces.generics;

import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbomanalysis.qualityattributes.QualityReport;
import org.svip.sbomanalysis.qualityattributes.tests.Result;

import java.util.Set;

/**
 * file: QAPipeline.java
 * Generic interface for quality attributes of any SBOM
 *
 * @author Matthew Morrison
 */
public interface QAPipeline {

    /**
     * Run a given sbom against all processor tests within the pipeline
     * @param uid Unique filename used to ID the SBOM
     * @param sbom the SBOM to run tests against
     * @return QualityReport containing all results of the tests run
     */
    QualityReport process(String uid, SBOM sbom);

    /**
     * Check if the SBOM contains a version number
     * @param sbom the SBOM to test
     * @return the result of checking for the sbom's version number
     */
    Set<Result> hasBomVersion(SBOM sbom);
}
