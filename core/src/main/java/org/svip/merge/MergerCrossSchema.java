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

public class MergerCrossSchema extends Merger {

    public MergerCrossSchema() {
    }

    /**
     * @param A
     * @param B
     * @return
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

        // declare SBOM A as the main SBOM, cast it back to SPDX14SBOM
        SVIPSBOM mainSBOM = SBOMA;

        // Create a new builder for the new SBOM
        SVIPSBOMBuilder builder = new SVIPSBOMBuilder();

        return MergerUtils.mergeToSchema(SBOMA, SBOMB, componentsA, componentsB, mainSBOM, builder, SVIP, name);

    }

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
