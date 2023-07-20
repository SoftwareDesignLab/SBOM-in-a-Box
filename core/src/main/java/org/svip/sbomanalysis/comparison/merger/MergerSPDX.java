package org.svip.sbomanalysis.comparison.merger;

import org.svip.sbom.builder.interfaces.generics.SBOMBuilder;
import org.svip.sbom.builder.objects.SVIPComponentBuilder;
import org.svip.sbom.builder.objects.schemas.SPDX23.SPDX23Builder;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23Package;
import org.svip.sbom.model.objects.CycloneDX14.CDX14ComponentObject;
import org.svip.sbom.model.objects.SPDX23.SPDX23PackageObject;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.sbom.model.shared.util.LicenseCollection;
import org.svip.sbomgeneration.serializers.SerializerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.svip.sbomgeneration.serializers.SerializerFactory.Schema.SPDX23;

/**
 * File: MergerSPDX.java
 *
 * Merges two SPDX SBOMs together.
 *
 * @author tyler_drake
 */
public class MergerSPDX extends Merger {

    public MergerSPDX() {
        super();
    }

    @Override
    public SBOM mergeSBOM(SBOM A, SBOM B){

        Set<Component> componentsA = A.getComponents();
        Set<Component> componentsB = B.getComponents();

        // declare SBOM A as the main SBOM, cast it back to SPDX14SBOM
        SPDX23SBOM mainSBOM = (SPDX23SBOM) A;

        // Create a new builder for the new SBOM
        SPDX23Builder builder = new SPDX23Builder();

        return mergeToSchema(A, B, componentsA, componentsB, mainSBOM, builder, SPDX23);

    }



}