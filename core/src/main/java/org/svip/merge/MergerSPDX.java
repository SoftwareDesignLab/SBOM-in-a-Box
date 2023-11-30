package org.svip.merge;

import org.svip.sbom.builder.objects.schemas.SPDX23.SPDX23Builder;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;

import java.util.Set;

import static org.svip.serializers.SerializerFactory.Schema.SPDX23;

/**
 * File: MergerSPDX.java
 * Description: Main class for merging two SPDX 2.3 SBOMs together.
 *
 * @author Tyler Drake
 */
public class MergerSPDX extends Merger {

    /**
     * SPDX Merger Controller
     */
    public MergerSPDX() {
        super();
    }

    /**
     * Merges two SPDX 2.3 SBOMs together
     *
     * @param A First SBOM
     * @param B Second SBOM
     * @return Merges SPDX SBOM
     * @throws MergerException
     */
    @Override
    public SBOM mergeSBOM(SBOM A, SBOM B) throws MergerException {

        // Get components from both SBOMs
        Set<Component> componentsA = A.getComponents();
        Set<Component> componentsB = B.getComponents();

        // declare SBOM A as the main SBOM, cast it back to SPDX14SBOM
        SPDX23SBOM mainSBOM = (SPDX23SBOM) A;

        // Create a new builder for the new SBOM
        SPDX23Builder builder = new SPDX23Builder();

        // Merge the SPDX SBOMs and return
        return MergerUtils.mergeToSchema(A, B, componentsA, componentsB, mainSBOM, builder, SPDX23, "");

    }

}
