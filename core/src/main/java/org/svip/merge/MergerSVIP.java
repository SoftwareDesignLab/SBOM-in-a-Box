package org.svip.merge;

import org.svip.sbom.builder.objects.SVIPSBOMBuilder;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.SVIPSBOM;

import java.util.Set;

import static org.svip.serializers.SerializerFactory.Schema.SVIP;

/**
 * File: MergerSPDX.java
 * <p>
 * Merges two SVIP SBOMs together.
 *
 * @author Juan Francisco Patino
 */
public class MergerSVIP extends Merger  {
    public MergerSVIP() {
        super();
    }
    @Override
    public SBOM mergeSBOM(SBOM A, SBOM B) {

        Set<Component> componentsA = A.getComponents();
        Set<Component> componentsB = B.getComponents();

        // declare SBOM A as the main SBOM, cast it back to SPDX14SBOM
        SVIPSBOM mainSBOM = (SVIPSBOM) A;

        // Create a new builder for the new SBOM
        SVIPSBOMBuilder builder = new SVIPSBOMBuilder();

        return MergerUtils.mergeToSchema(A, B, componentsA, componentsB, mainSBOM, builder, SVIP, "");

    }
}
