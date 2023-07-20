package org.svip.sbomanalysis.comparison.merger;

import org.svip.sbom.builder.objects.schemas.SPDX23.SPDX23Builder;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.sbomgeneration.serializers.SerializerFactory;

import java.util.Set;

public class MergerCrossSchema extends Merger{

    private SPDX23SBOM SBOMA;
    private CDX14SBOM SBOMB;

    public MergerCrossSchema(){}

    /**
     * @param A
     * @param B
     * @return
     */
    @Override
    public SBOM mergeSBOM(SBOM A, SBOM B) {

        // for simplicity
        if(A instanceof SPDX23SBOM spdx23SBOM){
            SBOMA = (SPDX23SBOM) A;
            SBOMB = (CDX14SBOM) B;
        }
        else{
            SBOMA = (SPDX23SBOM) B;
            SBOMB = (CDX14SBOM) A;
        }

        Set<Component> componentsA = A.getComponents();
        Set<Component> componentsB = B.getComponents();

        // declare SBOM A as the main SBOM, cast it back to SPDX14SBOM
        SPDX23SBOM mainSBOM = (SPDX23SBOM) A;

        // Create a new builder for the new SBOM
        SPDX23Builder builder = new SPDX23Builder();

        /** Assign all top level data for the new SBOM **/

        // Format
        builder.setFormat(mainSBOM.getFormat());

        // Name
        builder.setName(mainSBOM.getName());

        // UID (In this case, bom-ref)
        builder.setUID(mainSBOM.getUID());

        // SBOM Version
        builder.setVersion(mainSBOM.getVersion());

        // Spec Version (1.0-a)
        builder.setSpecVersion("1.0-a");

        // Licenses
        for(String license : mainSBOM.getLicenses()) { builder.addLicense(license); }

        // Creation Data
        if(A.getCreationData() != null && B.getCreationData() != null) {
            builder.setCreationData(mergeCreationData(A.getCreationData(), B.getCreationData()));
        } else if (A.getCreationData() != null) {
            builder.setCreationData(A.getCreationData());
        } else if (B.getCreationData() != null) {
            builder.setCreationData(B.getCreationData());
        } else { builder.setCreationData(null); }

        // Document Comment
        builder.setDocumentComment(mainSBOM.getDocumentComment());

        // Root Component
        builder.setRootComponent(mainSBOM.getRootComponent());

        // Components
        Set<Component> mergedComponents = mergeComponents(componentsA, componentsB, SerializerFactory.Schema.SVIP);
        for(Component mergedComponent : mergedComponents) {
            builder.addComponent(mergedComponent);
        }

        // Relationships TODO: Add merging of relationships in future sprint

        // External References
        mergeExternalReferences(
                A.getExternalReferences(), B.getExternalReferences()
        ).forEach(x -> builder.addExternalReference(x));

        // Return the newly built merged SBOM
        return builder.Build();
    }

    /**
     * @param A
     * @param B
     * @return
     */
    @Override
    protected Set<Component> mergeComponents(Set<Component> A, Set<Component> B, SerializerFactory.Schema schema) {
        return null;
    }

    /**
     * @param A
     * @param B
     * @return
     */
    @Override
    protected Component mergeComponent(Component A, Component B) {
        return null;
    }
}
