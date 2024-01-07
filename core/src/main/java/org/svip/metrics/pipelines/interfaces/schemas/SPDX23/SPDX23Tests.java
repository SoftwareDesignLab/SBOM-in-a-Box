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

package org.svip.metrics.pipelines.interfaces.schemas.SPDX23;

import org.svip.metrics.pipelines.interfaces.generics.QAPipeline;
import org.svip.metrics.resultfactory.Result;
import org.svip.sbom.model.shared.metadata.CreationData;

import java.util.Set;

/**
 * file: SPDX23Tests.java
 * An interface that contains a collection of tests specific to
 * SPDX 2.3 SBOMs
 *
 * @author Matthew Morrison
 */
public interface SPDX23Tests extends QAPipeline {

    /**
     * Test the SPDX SBOM Metadata to see if it contains a data license of
     * CC0-1.0
     *
     * @param field    the field that's tested
     * @param values   the data licenses tested
     * @param sbomName the sbom's name to product the result
     * @return The result of checking for the SBOM's data license
     */
    Result hasDataLicense(String field, Set<String> values, String sbomName);

    /**
     * Test every component in a given SPDX 2.3 SBOM for a valid SPDXID
     *
     * @param field         the field that's tested
     * @param value         the SPDXID tested
     * @param componentName the component's name to product the result
     * @return a collection of results for every component in the SBOM
     */
    Result hasSPDXID(String field, String value, String componentName);

    /**
     * Test the SPDX 2.3 sbom's metadata for a valid document namespace
     *
     * @param field    the field that's tested
     * @param value    the document namespace tested
     * @param sbomName the sbom's name to product the result
     * @return the result of if the sbom's metadata contains a valid
     * document namespace
     */
    Result hasDocumentNamespace(String field, String value, String sbomName);

    /**
     * Given an SPDX 2.3 SBOM, check that it has creator and created info
     *
     * @param field        the field that's tested
     * @param creationData the creation data of the SBOM to be tested
     * @param sbomName     the sbom's name to product the result
     * @return a collection of results of if the sbom contains creator and
     * created time info
     */
    Set<Result> hasCreationInfo(String field, CreationData creationData, String sbomName);

    /**
     * Test every component in the SPDX 2.3 SBOM for the PackageDownloadLocation field
     * and that it has a value
     *
     * @param field         the field that's tested
     * @param value         the download location tested
     * @param componentName the component's name to product the result
     * @return the result of if the component has a valid download location
     */
    Result hasDownloadLocation(String field, String value, String componentName);

    /**
     * Test all components in a given SPDX 2.3 SBOM for their verification code
     * based on FilesAnalyzed
     *
     * @param field         the field that's tested
     * @param value         the verification code tested
     * @param filesAnalyzed if the component's files were analyzed
     * @param componentName the component's name to product the result
     * @return a collection of results for each component in the SBOM
     */
    Result hasVerificationCode(String field, String value, boolean filesAnalyzed, String componentName);


    //TODO hasExtractedLicenses? extractedLicenseMinElements? Implement and how to access?
}
