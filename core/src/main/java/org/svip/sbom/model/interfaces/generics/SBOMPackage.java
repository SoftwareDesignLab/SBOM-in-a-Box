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

package org.svip.sbom.model.interfaces.generics;

import org.svip.compare.conflicts.Conflict;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.shared.util.Description;
import org.svip.sbom.model.shared.util.ExternalReference;

import java.util.List;
import java.util.Set;

/**
 * File: SBOMPackage.java
 * Generic SBOM Package details that many SBOM packages share
 *
 * @author Derek Garcia
 */
public interface SBOMPackage extends Component {
    /**
     * @return Supplier of the package
     */
    Organization getSupplier();

    /**
     * @return version of the package
     */
    String getVersion();

    /**
     * @return Description of the package
     */
    Description getDescription();

    /**
     * @return CPEs of the package
     */
    Set<String> getCPEs();

    /**
     * @return PURLs of the package
     */
    Set<String> getPURLs();

    /**
     * @return External References from the package
     */
    Set<ExternalReference> getExternalReferences();

    /**
     * Compare against another generic SBOM Package
     *
     * @param other Other SBOM Package to compare against
     * @return List of conflicts
     */
    List<Conflict> compare(SBOMPackage other);
}
