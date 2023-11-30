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