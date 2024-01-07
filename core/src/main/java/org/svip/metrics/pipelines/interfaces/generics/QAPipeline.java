 /** Copyright 2021 Rochester Institute of Technology (RIT). Developed with
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
 */

package org.svip.metrics.pipelines.interfaces.generics;

import org.svip.metrics.pipelines.QualityReport;
import org.svip.metrics.resultfactory.Result;
import org.svip.sbom.model.interfaces.generics.SBOM;


/**
 * file: QAPipeline.java
 * Generic interface for quality attributes of any SBOM
 *
 * @author Matthew Morrison
 */
public interface QAPipeline {

    /**
     * Run a given sbom against all processor tests within the pipeline
     *
     * @param sbom the SBOM to run tests against
     * @return QualityReport containing all results of the tests run
     */
    QualityReport process(SBOM sbom);

    /**
     * Check if the SBOM contains a version number
     *
     * @param field    the field that's tested
     * @param value    the bom version tested
     * @param sbomName the sbom's name to product the result
     * @return the result of checking for the sbom's version number
     */
    Result hasBomVersion(String field, String value, String sbomName);
}
