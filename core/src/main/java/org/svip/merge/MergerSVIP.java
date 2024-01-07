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

import org.svip.sbom.builder.objects.SVIPSBOMBuilder;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.SVIPSBOM;

import java.util.Set;

import static org.svip.serializers.SerializerFactory.Schema.SVIP;

/**
 * File: MergerSPDX.java
 * Description: Main class for merging two SVIP SBOMs together.
 *
 * @author Tyler Drake
 * @author Juan Francisco Patino
 */
public class MergerSVIP extends Merger  {

    /**
     * SVIP Merger Constructor
     */
    public MergerSVIP() {
        super();
    }

    /**
     * Merges two SVIP SBOMs together
     *
     * @param A First SBOM
     * @param B Second SBOM
     * @return Merged SVIP SBOM
     * @throws MergerException
     */
    @Override
    public SBOM mergeSBOM(SBOM A, SBOM B) throws MergerException {

        // Get components from both SBOMs
        Set<Component> componentsA = A.getComponents();
        Set<Component> componentsB = B.getComponents();

        // declare SBOM A as the main SBOM, cast it back to SPDX14SBOM
        SVIPSBOM mainSBOM = (SVIPSBOM) A;

        // Create a new builder for the new SBOM
        SVIPSBOMBuilder builder = new SVIPSBOMBuilder();

        // Merge SVIP SBOMs together and return
        return MergerUtils.mergeToSchema(A, B, componentsA, componentsB, mainSBOM, builder, SVIP, "");

    }

}
