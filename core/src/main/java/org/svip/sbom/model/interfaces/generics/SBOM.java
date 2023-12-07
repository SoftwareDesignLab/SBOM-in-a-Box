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

package org.svip.sbom.model.interfaces.generics;

import org.svip.compare.conflicts.Conflict;
import org.svip.sbom.model.shared.Relationship;
import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbom.model.shared.util.ExternalReference;

import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * File: SBOM.java
 * Generic SBOM details that many SBOMs share
 *
 * @author Derek Garcia
 */
public interface SBOM {

    /**
     * @return Origin format of the SBOM
     */
    String getFormat();

    /**
     * @return Name of the SBOM
     */
    String getName();

    /**
     * @return Unique identifier of the SBOM
     */
    String getUID();

    /**
     * @return Version of the SBOM
     */
    String getVersion();

    /**
     * @return Specification Version of the SBOM
     */
    String getSpecVersion();

    /**
     * @return SBOM Licenses
     */
    Set<String> getLicenses();

    /**
     * @return Creation data about the SBOM
     */
    CreationData getCreationData();

    /**
     * @return Get SBOM comment
     */
    String getDocumentComment();

    /**
     * @return Get SBOM root component
     */
    Component getRootComponent();

    /**
     * @return Get SBOM components
     */
    Set<Component> getComponents();

    /**
     * @return Component relationship details
     */
    Map<String, Set<Relationship>> getRelationships();

    /**
     * @return External references from this SBOM
     */
    Set<ExternalReference> getExternalReferences();

    /**
     * Compare a Generic SBOM against another SBOM Metadata
     *
     * @param other Other SBOM to compare against
     * @return List of Metadata of conflicts
     */
    List<Conflict> compare(SBOM other);
}
