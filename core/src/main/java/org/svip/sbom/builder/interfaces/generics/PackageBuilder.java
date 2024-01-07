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

package org.svip.sbom.builder.interfaces.generics;

import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.shared.util.Description;
import org.svip.sbom.model.shared.util.ExternalReference;

/**
 * file: SBOMComponent.java
 * Generic component details that many components would share
 * regardless of SBOM type
 *
 * @author Matthew Morrison
 */
public interface PackageBuilder extends ComponentBuilder {

    /**
     * Set the supplier of the component
     *
     * @param supplier the component's supplier
     * @return an SBOMComponentBuilder
     */
    PackageBuilder setSupplier(Organization supplier);

    /**
     * Set the version of the component
     *
     * @param version the component's version
     * @return an SBOMComponentBuilder
     */
    PackageBuilder setVersion(String version);

    /**
     * Set teh description of the component
     *
     * @param description the component's description
     * @return an SBOMComponentBuilder
     */
    PackageBuilder setDescription(Description description);

    /**
     * Add a CPE to the component
     *
     * @param cpe the cpe string to add
     * @return an SBOMComponentBuilder
     */
    PackageBuilder addCPE(String cpe);

    /**
     * Add a PURL to the component
     *
     * @param purl the purl string to add
     * @return an SBOMComponentBuilder
     */
    PackageBuilder addPURL(String purl);

    /**
     * Ann an external reference to the component
     *
     * @param externalReference the external component to add
     * @return an SBOMComponentBuilder
     */
    PackageBuilder addExternalReference(ExternalReference externalReference);
}
