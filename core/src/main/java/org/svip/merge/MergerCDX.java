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

package org.svip.merge;

import org.svip.sbom.builder.objects.schemas.CDX14.CDX14Builder;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;

import java.util.Set;

import static org.svip.serializers.SerializerFactory.Schema.CDX14;

/**
 * File: MergerCDX.java
 * Description: Main class for merging two CycloneDX 1.4 SBOMs together.
 *
 * @author Tyler Drake
 */
public class MergerCDX extends Merger {

    /**
     * CycloneDX Merger Constructor
     */
    public MergerCDX() {
        super();
    }

    /**
     * Merges two CycloneDX 1.4 SBOMs together
     *
     * @param A First SBOM
     * @param B Second SBOM
     * @return Merged CycloneDX SBOM
     * @throws MergerException
     */
    @Override
    public SBOM mergeSBOM(SBOM A, SBOM B) throws MergerException {

        // Get all components from both SBOMs
        Set<Component> componentsA = A.getComponents();
        Set<Component> componentsB = B.getComponents();

        // declare SBOM A as the main SBOM, cast it back to SPDX14SBOM
        CDX14SBOM mainSBOM = (CDX14SBOM) A;

        // Create a new builder for the new SBOM
        CDX14Builder builder = new CDX14Builder();

        // Merge the SBOMs and return the merged SBOM
        return MergerUtils.mergeToSchema(A, B, componentsA, componentsB, mainSBOM, builder, CDX14, "");

    }

}