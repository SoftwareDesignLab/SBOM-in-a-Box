package org.svip.manipulation.manipulate;

import org.svip.sbom.builder.objects.SVIPComponentBuilder;
import org.svip.sbom.builder.objects.SVIPSBOMBuilder;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.objects.SVIPComponentObject;
import org.svip.sbom.model.objects.SVIPSBOM;

import java.util.UUID;

/**
 * Name: ManipulateSVIP.java
 * Description: Creates a new SVIP SBOM and 'manipulates'
 * the fields based on the desired schema and format.
 *
 * @author Tyler Drake
 */
public class ManipulateSVIP {

    public static SVIPSBOM modify(SVIPSBOM sbom, SchemaManipulationMap manipulationMap) {

        // Create new SVIP SBOM Builder
        SVIPSBOMBuilder builder = new SVIPSBOMBuilder();

        // Format
        builder.setFormat(sbom.getFormat());

        // Name
        builder.setName(sbom.getName());

        // UID
        builder.setUID(sbom.getUID());

        // Version
        builder.setVersion(sbom.getVersion());

        // Spec Version
        builder.setSpecVersion(manipulationMap.getVersion());

        // Stream licenses into new SBOM
        if(sbom.getLicenses() != null)
            sbom.getLicenses().stream().forEach(x -> builder.addLicense(x));

        // Creation Data
        builder.setCreationData(sbom.getCreationData());

        // Document Comment
        builder.setDocumentComment(sbom.getDocumentComment());

        // Document Comment
        builder.setDocumentComment(sbom.getDocumentComment());

        // Root Component
        builder.setRootComponent(modifyComponent(sbom.getRootComponent()));

        // Stream components from SVIP SBOM, convert them, then put into CDX SBOM
        if(sbom.getComponents() != null)
            sbom.getComponents().stream().forEach(x -> builder.addComponent(modifyComponent(x)));

        // Stream Relationship data into new SBOM
        if(sbom.getRelationships() != null) {
            sbom.getRelationships().keySet().forEach(
                    x -> sbom.getRelationships().get(x).stream().forEach(
                            y -> builder.addRelationship(x, y)
                    )
            );
        }

        // Stream External References into new SBOM
        if(sbom.getExternalReferences() != null) {
            sbom.getExternalReferences().stream().forEach(
                    x -> builder.addExternalReference(x)
            );
        }

        // Set SPDX License List Version
        builder.setSPDXLicenseListVersion(sbom.getSPDXLicenseListVersion());


        // Build the SBOM and return it
        return builder.Build();

    }

    public static SVIPComponentObject modifyComponent(Component originalComponent) {

        SVIPComponentBuilder builder = new SVIPComponentBuilder();

        return builder.buildAndFlush();

    }

    public static String generateID(String oldID, SVIPComponentObject component, SchemaManipulationMap manipulationMap) {
        switch (manipulationMap) {
            case SPDX23 -> {
                return manipulationMap.getComponentIDType() + component.getName() + "-" + component.getVersion();
            }
            default -> {
                return manipulationMap.getComponentIDType() + UUID.randomUUID();
            }
        }
    }

}
