package org.svip.sbomanalysis.qualityattributes.pipelines.interfaces.schemas.CycloneDX14;

import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbomanalysis.qualityattributes.pipelines.interfaces.generics.QAPipeline;
import org.svip.sbomanalysis.qualityattributes.resultfactory.Result;


import java.util.Set;

/**
 * file: CDX14Tests.java
 * An interface that contains a collection of tests that are specific to
 * CycloneDX 1.4 SBOMs
 *
 * @author Matthew Morrison
 */
public interface CDX14Tests extends QAPipeline {

    /**
     * Check if the CycloneDX 1.4 SBOM contains a valid Serial Number value
     * @param field the field that's tested
     * @param value the serial number tested
     * @return a Set<Result> of if the serial number is valid or not
     */
    Set<Result> validSerialNumber(String field, String value);

    /**
     * Check if each component in the given CycloneDX 1.4 SBOM contains
     * a bom-ref value
     * @param field the field that's tested
     * @param value the bom ref tested
     * @return a collection of results from each component in the sbom
     */
    Set<Result> hasBomRef(String field, String value);


}
