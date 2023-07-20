package org.svip.sbomanalysis.comparison.merger;

import org.svip.sbom.builder.objects.schemas.CDX14.CDX14Builder;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;

import java.util.Set;

import static org.svip.sbomgeneration.serializers.SerializerFactory.Schema.CDX14;

/**
 * File: MergerCDX.java
 * <p>
 * Merges two CDX SBOMs together.
 *
 * @author tyler_drake
 */
public class MergerCDX extends Merger {

    public MergerCDX() {
        super();
    }

    @Override
    public SBOM mergeSBOM(SBOM A, SBOM B) {

        Set<Component> componentsA = A.getComponents();
        Set<Component> componentsB = B.getComponents();

        // declare SBOM A as the main SBOM, cast it back to SPDX14SBOM
        CDX14SBOM mainSBOM = (CDX14SBOM) A;

        // Create a new builder for the new SBOM
        CDX14Builder builder = new CDX14Builder();

        return MergerUtils.mergeToSchema(A, B, componentsA, componentsB, mainSBOM, builder, CDX14, "");

    }

}