/ **
* Copyright 2021 Rochester Institute of Technology (RIT). Developed with
* government support under contract 70RCSA22C00000008 awarded by the United
* States Department of Homeland Security for Cybersecurity and Infrastructure Security Agency.
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the “Software”), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
* /

package org.svip.metrics.pipelines.interfaces.schemas.CycloneDX14;

import org.svip.metrics.pipelines.interfaces.generics.QAPipeline;
import org.svip.metrics.resultfactory.Result;
import org.svip.sbom.model.uids.Hash;

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
     *
     * @param field    the field that's tested
     * @param value    the serial number tested
     * @param sbomName the sbom's name to product the result
     * @return a Result of if the serial number is valid or not
     */
    Result validSerialNumber(String field, String value, String sbomName);

    /**
     * Check if each component in the given CycloneDX 1.4 SBOM contains
     * a bom-ref value
     *
     * @param field         the field that's tested
     * @param value         the bom ref tested
     * @param componentName the component's name to product the result
     * @return the result of if the component has a bom-ref
     */
    Result hasBomRef(String field, String value, String componentName);

    /**
     * Check if a hash algorithm in the given CycloneDX 1.4 SBOM is supported
     * within CycloneDX
     *
     * @param field         the field that's tested
     * @param hash          the hash to be tested
     * @param componentName the component's name to product the result
     * @return the result of if the hash algorithm is supported
     */
    Result supportedHash(String field, Hash hash, String componentName);


}
