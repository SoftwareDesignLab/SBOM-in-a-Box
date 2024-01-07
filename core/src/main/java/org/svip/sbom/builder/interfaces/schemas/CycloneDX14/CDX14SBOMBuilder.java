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

package org.svip.sbom.builder.interfaces.schemas.CycloneDX14;

import org.svip.sbom.builder.interfaces.generics.SBOMBuilder;
import org.svip.sbom.model.interfaces.schemas.CycloneDX14.CDX14Package;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;

/**
 * file: CDX14SBOMBuilder.java
 * Interface for Cyclone DX 1.4 SBOM Builder
 *
 * @author Thomas Roman
 */
public interface CDX14SBOMBuilder extends SBOMBuilder {

    /**
     * add a CDX 1.4 package to the CDX 1.4 SBOM builder
     *
     * @param cdx14Package the CDX 1.4 package
     * @return a cdx 1.4 SBOM builder
     */
    CDX14SBOMBuilder addCDX14Package(CDX14Package cdx14Package);

    /**
     * @return a CDX 1.4 SBOM
     */
    CDX14SBOM buildCDX14SBOM();
}
