package org.svip.merge;

import org.svip.manipulation.Conversion;
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

        // for simplicity
        SVIPSBOM SBOMA;
        SVIPSBOM SBOMB;

        String name = (A.getName() == null || A.getName().length() == 0) ? B.getName() : A.getName();

        SBOMA = (SVIPSBOM) standardizeSBOM(A);
        SBOMB = (SVIPSBOM) standardizeSBOM(B);

        Set<Component> componentsA = SBOMA.getComponents();
        Set<Component> componentsB = SBOMB.getComponents();

        // declare SBOM A as the main SBOM
        SVIPSBOM mainSBOM = SBOMA;

        // Create a new builder for the new SBOM
        SVIPSBOMBuilder builder = new SVIPSBOMBuilder();

        return MergerUtils.mergeToSchema(SBOMA, SBOMB, componentsA, componentsB, mainSBOM, builder, SVIP, name);

    }

    /**
     * Standardizes the SBOM to an SVIP SBOM based upon its original schema
     *
     * @param sbom Internal SBOM Object to be standardized
     * @return SVIP SBOM
     * @throws Exception
     */
    public SBOM standardizeSBOM(SBOM sbom) throws Exception {
        if(sbom instanceof CDX14SBOM) {
            return Conversion.convertSBOM(sbom, SVIP, CDX14);
        } else if(sbom instanceof SPDX23SBOM) {
            return Conversion.convertSBOM(sbom, SVIP, SPDX23);
        } else if(sbom instanceof SVIPSBOM) {
            return sbom;
        } else {
            return null;
        }
    }

}
