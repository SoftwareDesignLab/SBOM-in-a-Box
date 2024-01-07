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

package org.svip.sbom.builder.interfaces.schemas.CycloneDX14;

import org.svip.sbom.builder.interfaces.generics.PackageBuilder;
import org.svip.sbom.model.shared.util.ExternalReference;

/**
 * file: CDX14PackageBuilder_I.java
 * Generic Package Builder interface for CycloneDX 1.4
 * SBOM components
 *
 * @author Matthew Morrison
 */
public interface CDX14PackageBuilder_I extends PackageBuilder {

    /**
     * Set the mime type of the package
     *
     * @param mimeType the package's mime type
     * @return a CDX14PackageBuilder_I
     */
    CDX14PackageBuilder_I setMimeType(String mimeType);

    /**
     * Set the publisher of the package
     *
     * @param publisher the package's publisher
     * @return a CDX14PackageBuilder_I
     */
    CDX14PackageBuilder_I setPublisher(String publisher);

    /**
     * Set the scope of the package
     *
     * @param scope the package's scope
     * @return a CDX14PackageBuilder_I
     */
    CDX14PackageBuilder_I setScope(String scope);

    /**
     * Set the group for the package
     *
     * @param group the package's group
     * @return a CDX14PackageBuilder_I
     */
    CDX14PackageBuilder_I setGroup(String group);

    /**
     * Add an external reference to the package
     *
     * @param externalReference a package's external reference
     * @return a CDX14PackageBuilder_I
     */
    CDX14PackageBuilder_I addExternalReferences(ExternalReference externalReference);


    /**
     * Add a property to the package
     *
     * @param name  the name of the property
     * @param value the value of the property
     * @return a CDX14PackageBuilder_I
     */
    CDX14PackageBuilder_I addProperty(String name, String value);
}
