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

package org.svip.sbom.builder.interfaces.schemas.SPDX23;

import org.svip.sbom.builder.interfaces.generics.PackageBuilder;

/**
 * file: SPDX23PackageBuilder_I.java
 * Generic Package Builder interface for SPDX 2.3
 * SBOM components
 *
 * @author Matthew Morrison
 */
public interface SPDX23PackageBuilder_I extends SPDX23ComponentBuilder, PackageBuilder {

    /**
     * Set the download location of the package
     *
     * @param downloadLocation the package's download location
     * @return an SPDX23PackageBuilder_I
     */
    SPDX23PackageBuilder_I setDownloadLocation(String downloadLocation);

    /**
     * Set the file name of the package
     *
     * @param fileName the package's file name
     * @return an SPDX23PackageBuilder_I
     */
    SPDX23PackageBuilder_I setFileName(String fileName);

    /**
     * Set if the package's files were analyzed or not
     *
     * @param filesAnalyzed a boolean if the files were analyzed
     * @return an SPDX23PackageBuilder_I
     */
    SPDX23PackageBuilder_I setFilesAnalyzed(Boolean filesAnalyzed);

    /**
     * Set the verification code of the package
     *
     * @param verificationCode the package's verification code
     * @return an SPDX23PackageBuilder_I
     */
    SPDX23PackageBuilder_I setVerificationCode(String verificationCode);

    /**
     * Set the home page of the package
     *
     * @param homePage the package's home page
     * @return an SPDX23PackageBuilder_I
     */
    SPDX23PackageBuilder_I setHomePage(String homePage);

    /**
     * Set the source information for the package
     *
     * @param sourceInfo the package's source information
     * @return an SPDX23PackageBuilder_I
     */
    SPDX23PackageBuilder_I setSourceInfo(String sourceInfo);

    /**
     * Set the release date of the package
     *
     * @param releaseDate the package's release date
     * @return an SPDX23PackageBuilder_I
     */
    SPDX23PackageBuilder_I setReleaseDate(String releaseDate);

    /**
     * Set the build date of the package
     *
     * @param buildDate the package's build date
     * @return an SPDX23PackageBuilder_I
     */
    SPDX23PackageBuilder_I setBuildDate(String buildDate);

    /**
     * Set the valid until date for the package
     *
     * @param validUntilDate the package's valid until date
     * @return an SPDX23PackageBuilder_I
     */
    SPDX23PackageBuilder_I setValidUntilDate(String validUntilDate);
}
