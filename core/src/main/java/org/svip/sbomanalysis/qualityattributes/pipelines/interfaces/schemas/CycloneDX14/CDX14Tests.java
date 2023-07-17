package org.svip.sbomanalysis.qualityattributes.pipelines.interfaces.schemas.CycloneDX14;

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
     * @param sbomName the sbom's name to product the result
     * @return a Result of if the serial number is valid or not
     */
    Result validSerialNumber(String field, String value, String sbomName);

    /**
     * Check if each component in the given CycloneDX 1.4 SBOM contains
     * a bom-ref value
     * @param field the field that's tested
     * @param value the bom ref tested
     * @param componentName the component's name to product the result
     * @return the result of if the component has a bom-ref
     */
    Result hasBomRef(String field, String value, String componentName);

    /**
     * Check if a hash algorithm in the given CycloneDX 1.4 SBOM is supported
     * within CycloneDX
     * @param field the field that's tested
     * @param value the bom ref tested
     * @param componentName the component's name to product the result
     * @return the result of if the hash algorithm is supported
     */
    Result supportedHash(String field, String value, String componentName);


}
