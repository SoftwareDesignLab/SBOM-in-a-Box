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

package org.svip.sbom.model.interfaces.schemas.SPDX23;

import org.svip.compare.conflicts.Conflict;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;

import java.util.List;

/**
 * File: SPDX23Schema.java
 * SPDX 2.3 specific fields
 * <p>
 * Source: <a href="https://spdx.github.io/spdx-spec/v2.3/">https://spdx.github.io/spdx-spec/v2.3/"</a>
 *
 * @author Derek Garcia
 */
public interface SPDX23Schema extends SBOM {

    /*todo
     + getSnippets(): Set<Snippet>
     + getAdditionalLicenseInformation(): Set<LicenseInfo>
     + getAnnotaionInformation(): Set<Annotation>
     */

    /**
     * @return Version of the SPDX License List used when the SPDX document was created
     */
    String getSPDXLicenseListVersion();

    /**
     * Compare a SPDX 2.3 SBOM against another SPDX 2.3 SBOM Metadata
     *
     * @param other other SPDX 2.3 SBOM
     * @return list of conflicts
     */
    List<Conflict> compare(SPDX23SBOM other);
}
