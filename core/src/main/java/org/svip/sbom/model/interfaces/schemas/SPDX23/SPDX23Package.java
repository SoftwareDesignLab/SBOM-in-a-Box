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

package org.svip.sbom.model.interfaces.schemas.SPDX23;

import org.svip.compare.conflicts.Conflict;
import org.svip.sbom.model.interfaces.generics.SBOMPackage;

import java.util.List;

/**
 * File: SPDX23Package.java
 * <p>
 * SPDX 2.3 specific fields
 * <p>
 * Source: <a href="https://spdx.github.io/spdx-spec/v2.3/package-information/">https://spdx.github.io/spdx-spec/v2.3/package-information/</a>
 *
 * @author Derek Garcia
 */
public interface SPDX23Package extends SBOMPackage, SPDX23Component {
    /**
     * @return Download location of package
     */
    String getDownloadLocation();

    /**
     * @return filename of the package as reported by the system
     */
    String getFileName();

    /**
     * @return Files have been analyzed
     */
    Boolean getFilesAnalyzed();

    /**
     * @return SPDX Verification code
     */
    String getVerificationCode();

    /**
     * @return Homepage of package
     */
    String getHomePage();

    /**
     * @return Information about the source of the package
     */
    String getSourceInfo();

    /**
     * @return When the package was release
     */
    String getReleaseDate();

    /**
     * @return When the package was built
     */
    String getBuiltDate();

    /**
     * @return When the package will no longer be supported
     */
    String getValidUntilDate();

    /**
     * Compare against another SPDX 2.3 Package
     *
     * @param other Other SPDX 2.3 Package to compare against
     * @return List of conflicts
     */
    List<Conflict> compare(SPDX23Package other);
}
