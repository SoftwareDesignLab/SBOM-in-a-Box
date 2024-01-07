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

package org.svip.sbom.builder.interfaces.generics;

import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.shared.Relationship;
import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbom.model.shared.util.ExternalReference;

/**
 * file: SBOMBuilder.java
 * Interface for SBOM Builder
 *
 * @author Thomas Roman
 */
public interface SBOMBuilder {
    /**
     * Set the SBOM's format
     *
     * @param format
     * @return an SBOMBuilder
     */
    SBOMBuilder setFormat(String format);

    /**
     * Set the SBOMBuilder's name
     *
     * @param name
     * @return an SBOMBuilder
     */
    SBOMBuilder setName(String name);

    /**
     * Set the unique identifier for the SBOMBuilder
     *
     * @param uid
     * @return an SBOMBuilder
     */
    SBOMBuilder setUID(String uid);

    /**
     * Set the SBOMBuilder's version
     *
     * @param version
     * @return an SBOMBuilder
     */
    SBOMBuilder setVersion(String version);

    /**
     * Set the SBOMBuilder's specVersion
     *
     * @param specVersion
     * @return an SBOMBuilder
     */
    SBOMBuilder setSpecVersion(String specVersion);

    /**
     * Add a license to the SBOMBuilder
     *
     * @param license
     * @return an SBOMBuilder
     */
    SBOMBuilder addLicense(String license);

    /**
     * Set the SBOMBuilder's creation data
     *
     * @param creationData
     * @return an SBOMBuilder
     */
    SBOMBuilder setCreationData(CreationData creationData);

    /**
     * Set a document comment for the SBOMBuilder
     *
     * @param documentComment
     * @return an SBOMBuilder
     */
    SBOMBuilder setDocumentComment(String documentComment);

    /**
     * Set the root component of the SBOMBuilder
     *
     * @param rootComponent
     * @return an SBOMBuilder
     */
    SBOMBuilder setRootComponent(Component rootComponent);

    /**
     * Add a component to the SBOMBuilder
     *
     * @param component
     * @return an SBOMBuilder
     */
    SBOMBuilder addComponent(Component component);

    /**
     * Add relationships between SBOMBuilder components
     *
     * @param componentName
     * @param relationship
     * @return an SBOMBuilder
     */
    SBOMBuilder addRelationship(String componentName, Relationship relationship);

    /**
     * Add external reference data to the SBOMBuilder
     *
     * @param externalReference
     * @return an SBOMBuilder
     */
    SBOMBuilder addExternalReference(ExternalReference externalReference);

    /**
     * Build an SBOM from the data in the SBOMBUILDER
     *
     * @return SBOM
     */
    SBOM Build();
}
