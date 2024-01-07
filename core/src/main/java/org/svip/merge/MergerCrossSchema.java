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

package org.svip.merge;

import org.svip.conversion.Conversion;
import org.svip.sbom.builder.objects.SVIPSBOMBuilder;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.sbom.model.objects.SVIPSBOM;

import java.util.Set;

import static org.svip.serializers.SerializerFactory.Schema.*;

/**
 * Name: MergerCrossSchema.java
 * Description: Merges two SBOMs of different formats together into one.
 *
 * @author Tyler Drake
 */
public class MergerCrossSchema extends Merger {

    /**
     * Cross-Schema Merger Constructor
     */
    public MergerCrossSchema() {
    }

    /**
     * Takes in two SBOMs and merged them together
     *
     * @param A The Primary SBOM
     * @param B Secondary SBOM
     * @return An SVIP SBOM made from both SBOMs
     */
    @Override
    public SBOM mergeSBOM(SBOM A, SBOM B) throws Exception {

        // Initialize both SVIP SBOMs
        SVIPSBOM SBOMA;
        SVIPSBOM SBOMB;

        // Set the name
        String name = (A.getName() == null || A.getName().length() == 0) ? B.getName() : A.getName();

        // Cast both SBOMs to the SVIP SBOMs
        SBOMA = (SVIPSBOM) standardizeSBOM(A);
        SBOMB = (SVIPSBOM) standardizeSBOM(B);

        // Get components from both the SBOMs
        Set<Component> componentsA = SBOMA.getComponents();
        Set<Component> componentsB = SBOMB.getComponents();

        // declare SBOM A as the main SBOM
        SVIPSBOM mainSBOM = SBOMA;

        // Create a new builder for the new SBOM
        SVIPSBOMBuilder builder = new SVIPSBOMBuilder();

        // Merge the SBOMs together and return
        return MergerUtils.mergeToSchema(SBOMA, SBOMB, componentsA, componentsB, mainSBOM, builder, SVIP, name);

    }

    /**
     * Standardizes the SBOM to an SVIP SBOM based upon its original schema
     *
     * @param sbom Internal SBOM Object to be standardized
     * @return SVIP SBOM
     * @throws Exception
     */
    public SBOM standardizeSBOM(SBOM sbom) {

        if(sbom instanceof CDX14SBOM) {

            // Convert the CDX 1.4 SBOM to an SVIPSBOM and return it
            return Conversion.convert(sbom, CDX14, SVIP);

        } else if(sbom instanceof SPDX23SBOM) {

            // Convert the SPDX 2.3 SBOM to an SVIPSBOM and return it
            return Conversion.convert(sbom, SPDX23, SVIP);

        } else if(sbom instanceof SVIPSBOM) {

            // If it's already and SVIPSBOM, return it
            return sbom;

        } else {

            // Default - return null
            return null;

        }

    }

}
