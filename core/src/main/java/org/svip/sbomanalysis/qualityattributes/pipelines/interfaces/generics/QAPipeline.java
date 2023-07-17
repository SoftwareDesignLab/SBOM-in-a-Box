package org.svip.sbomanalysis.qualityattributes.pipelines.interfaces.generics;

import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbomanalysis.qualityattributes.pipelines.QualityReport;
import org.svip.sbomanalysis.qualityattributes.resultfactory.Result;

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
     * @param field the field that's tested
     * @param value the bom version tested
     * @param sbomName the sbom's name to product the result
     * @return the result of checking for the sbom's version number
     */
    Set<Result> hasBomVersion(String field, String value, String sbomName);
}
