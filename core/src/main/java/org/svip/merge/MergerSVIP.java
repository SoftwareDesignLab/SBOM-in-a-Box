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
