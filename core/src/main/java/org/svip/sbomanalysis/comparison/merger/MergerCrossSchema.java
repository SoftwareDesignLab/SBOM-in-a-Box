package org.svip.sbomanalysis.comparison.merger;

import org.svip.sbom.builder.objects.SVIPSBOMBuilder;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;

import java.util.Set;

import static org.svip.sbomgeneration.serializers.SerializerFactory.Schema.SVIP;

public class MergerCrossSchema extends Merger {

    public MergerCrossSchema() {
    }

    /**
     * @param A
     * @param B
     * @return
     */
    @Override
    public SBOM mergeSBOM(SBOM A, SBOM B) {

        // for simplicity
        SPDX23SBOM SBOMA;
        CDX14SBOM SBOMB;

        String name = A.getName();

        if (A instanceof SPDX23SBOM) {
            SBOMA = (SPDX23SBOM) A;
            SBOMB = (CDX14SBOM) B;
        } else {
            SBOMA = (SPDX23SBOM) B;
            SBOMB = (CDX14SBOM) A;
        }

        Set<Component> componentsA = SBOMA.getComponents();
        Set<Component> componentsB = SBOMB.getComponents();

        // declare SBOM A as the main SBOM, cast it back to SPDX14SBOM
        SPDX23SBOM mainSBOM = SBOMA;

        // Create a new builder for the new SBOM
        SVIPSBOMBuilder builder = new SVIPSBOMBuilder();

        return MergerUtils.mergeToSchema(SBOMA, SBOMB, componentsA, componentsB, mainSBOM, builder, SVIP, name);

    }

}
