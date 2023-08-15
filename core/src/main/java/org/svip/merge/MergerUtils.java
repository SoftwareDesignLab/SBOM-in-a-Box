package org.svip.merge;

import org.svip.compare.utils.Utils;
import org.svip.sbom.builder.interfaces.generics.SBOMBuilder;
import org.svip.sbom.builder.objects.SVIPComponentBuilder;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.CycloneDX14.CDX14ComponentObject;
import org.svip.sbom.model.objects.SPDX23.SPDX23FileObject;
import org.svip.sbom.model.objects.SPDX23.SPDX23PackageObject;
import org.svip.sbom.model.objects.SVIPComponentObject;
import org.svip.sbom.model.shared.Relationship;
import org.svip.serializers.SerializerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static org.svip.merge.ComponentMerger.mergeComponentToSchema;

/**
 * Utility class to prevent creating large modules
 *
 * @author Juan Francisco Patino
 * @author Tyler Drake
 */
public abstract class MergerUtils extends Merger {

    /**
     * @param A            SBOM A
     * @param B            SBOM B
     * @param componentsA  collection of SBOM A's components
     * @param componentsB  collection of SBOM B's components
     * @param mainSBOM     primary SBOM to merge from
     * @param builder      SBOMBuilder to build with
     * @param targetSchema schema to merge to
     * @param newName      name of newly merged SBOM
     * @return merged SBOM
     */
    protected static SBOM mergeToSchema(SBOM A, SBOM B, Set<Component> componentsA, Set<Component> componentsB, SBOM mainSBOM,
                                        SBOMBuilder builder, SerializerFactory.Schema targetSchema, String newName) throws MergerException {

        /** Assign all top level data for the new SBOM **/

        // Format
        builder.setFormat(String.valueOf(targetSchema));

        // Name
        if (targetSchema == SerializerFactory.Schema.SVIP)
            builder.setName(newName);
        else if (mainSBOM.getName() == null || mainSBOM.getName().isEmpty())
            builder.setName(B.getName());
        else
            builder.setName(mainSBOM.getName());

        // UID (In this case, bom-ref)
        builder.setUID(mainSBOM.getUID());

        // SBOM Version
        builder.setVersion(mainSBOM.getVersion());

        String specVersion;
        // Spec Version
        switch (targetSchema) {
            case SPDX23 -> specVersion = "2.3";
            case CDX14 -> specVersion = "1.4";
            default -> specVersion = "1.0-a"; // SVIP
        }
        builder.setSpecVersion(specVersion);

        // Licenses
        if (mainSBOM.getLicenses() != null)
            for (String license : mainSBOM.getLicenses()) {
                builder.addLicense(license);
            }

        // Creation Data
        if (A.getCreationData() != null && B.getCreationData() != null) {
            builder.setCreationData(mergeCreationData(A.getCreationData(), B.getCreationData()));
        } else if (A.getCreationData() != null) {
            builder.setCreationData(A.getCreationData());
        } else if (B.getCreationData() != null) {
            builder.setCreationData(B.getCreationData());
        } else {
            builder.setCreationData(null);
        }

        // Document Comment
        builder.setDocumentComment(mainSBOM.getDocumentComment());

        // Root Component
        if (targetSchema == SerializerFactory.Schema.SVIP) {
            try {
                Component rootComponent = B.getRootComponent();
                SVIPComponentBuilder compBuilder = new SVIPComponentBuilder();
                Utils.buildSVIPComponentObject(rootComponent, compBuilder);
                builder.setRootComponent(compBuilder.buildAndFlush()); // second sbom is cdx, which has a root component
            } catch (ClassCastException e) {
                builder.setRootComponent(mainSBOM.getRootComponent());
            }
        } else
            builder.setRootComponent(mainSBOM.getRootComponent());

        // Components
        Set<Component> mergedComponents = null;
        if(componentsA != null && componentsB != null) {
            mergedComponents = mergeComponentsToSchema(componentsA, componentsB, targetSchema);
        } else if(componentsA != null && componentsB == null) {
            mergedComponents = componentsA;
        } else if(componentsA == null && componentsB != null) {
            mergedComponents = componentsB;
        }

        if(mergedComponents != null) mergedComponents.forEach(x -> builder.addComponent(x));


        // Relationships
        if(A.getRelationships() != null) {
            Map<String, Set<Relationship>> relationshipsA = A.getRelationships();
            relationshipsA.keySet().forEach(x -> relationshipsA.get(x).forEach(y -> builder.addRelationship(x, y)));
        }

        if(B.getRelationships() != null) {
            Map<String, Set<Relationship>> relationshipsB = B.getRelationships();
            relationshipsB.keySet().forEach(x -> relationshipsB.get(x).forEach(y -> builder.addRelationship(x, y)));
        }

        // External References
        mergeExternalReferences(
                A.getExternalReferences(), B.getExternalReferences()
        ).forEach(x -> builder.addExternalReference(x));

        // Return the newly built merged SBOM
        return builder.Build();
    }

    /**
     * @param A            collection of SBOM A's components
     * @param B            collection of SBOM B's components
     * @param targetSchema schema to merge to
     * @return set of merged components
     */
    protected static Set<Component> mergeComponentsToSchema(Set<Component> A, Set<Component> B, SerializerFactory.Schema targetSchema) {

        // New Components collection
        Set<Component> mergedComponents = new HashSet<>();

        Set<Component> removeB = new HashSet<>();

        // For every component in the first SBOM
        for (Component componentA : A) {

            // Checks to see if component A was merged with another component
            boolean merged = false;

            // For every component in the second SBOM
            for (Component componentB : B) {

                switch (targetSchema) { // todo make this neater
                    case SPDX23 -> {

                        // Cast the generic component from SBOM A back to a SPDX component

                        SPDX23PackageObject componentA_SPDX = null;
                        SPDX23FileObject componentA_SPDXFile = null;
                        try {
                            componentA_SPDX = (SPDX23PackageObject) componentA;
                        } catch (ClassCastException e) {
                            componentA_SPDXFile = (SPDX23FileObject) componentA;
                        }

                        SPDX23PackageObject componentB_SPDX = null;
                        SPDX23FileObject componentB_SPDXFile = null;
                        try {
                            componentB_SPDX = (SPDX23PackageObject) componentB;
                        } catch (ClassCastException e) {
                            componentB_SPDXFile = (SPDX23FileObject) componentB;
                        }

                        // If the components are the same by Name and Version, merge then add them to the SBOM

                        if (componentA_SPDX != null && componentB_SPDX != null) // if both packages then merge if appropriate
                            if (Objects.equals(componentA_SPDX.getName(), componentB_SPDX.getName()) &&
                                    Objects.equals(componentA_SPDX.getVersion(), componentB_SPDX.getVersion())
                            || versionsCanBeMerged(componentA_SPDX.getVersion(), componentB_SPDX.getVersion())) {

                                mergedComponents.add(ComponentMerger.mergeComponentToSchema(componentA, componentB, targetSchema));
                                removeB.add(componentB);
                                merged = true;

                            } else if (componentA_SPDXFile != null && componentB_SPDXFile != null) // if both Files then merge if appropriate
                                if (Objects.equals(componentA_SPDXFile.getName(), componentB_SPDXFile.getName())) {

                                    mergedComponents.add(ComponentMerger.mergeComponentToSchema(componentA, componentB, targetSchema));
                                    removeB.add(componentB);
                                    merged = true;

                                }
                        // otherwise don't merge
                    }
                    case CDX14 -> {

                        // Cast the generic component from SBOM A back to a CDX component
                        CDX14ComponentObject componentA_CDX = (CDX14ComponentObject) componentA;
                        CDX14ComponentObject componentB_CDX = (CDX14ComponentObject) componentB;

                        // If the components are the same by Name and Version, merge then add them to the SBOM
                        if (Objects.equals(componentA_CDX.getName(), componentB_CDX.getName()) &&
                                Objects.equals(componentA_CDX.getVersion(), componentB_CDX.getVersion())
                        || versionsCanBeMerged(componentA_CDX.getVersion(), componentB_CDX.getVersion())) {

                            mergedComponents.add(ComponentMerger.mergeComponentToSchema(componentA, componentB, targetSchema));
                            removeB.add(componentB);
                            merged = true;

                        }

                    }
                    default -> { // SVIP
                        // Cast the generic component from SBOM A back to a CDX component
                        SVIPComponentObject componentA_SVIP = (SVIPComponentObject) componentA;
                        SVIPComponentObject componentB_SVIP = (SVIPComponentObject) componentB;

                        // If the components are the same by Name and Version, merge then add them to the SBOM
                        if (Objects.equals(componentA_SVIP.getName(), componentB_SVIP.getName()) &&
                                (Objects.equals(componentA_SVIP.getVersion(), componentB_SVIP.getVersion())
                                || versionsCanBeMerged(componentA_SVIP.getVersion(), componentB_SVIP.getVersion()))) {

                            mergedComponents.add(mergeComponentToSchema(componentA, componentB, targetSchema));
                            removeB.add(componentB);
                            merged = true;

                        }
                    }
                }

                // Cast the generic component from SBOM B back to a SPDX component


            }
            B.removeAll(removeB);
            // If component A was not merged with anything, add it to the new components
            if (!merged) mergedComponents.add(componentA);
        }

        // Merge remaining components from SBOM B that were not merged with any components from A
        mergedComponents.addAll(B);

        // Return the merged components set
        return mergedComponents;

    }

    /**
     * Helper method to reduce code repetitiveness in merging two SVIP component objects
     * (used to configure verification code, home page, and source info)
     *
     * @param aString string from SVIPComponent A
     * @param bString string from SVIPComponent B
     * @return String configured from between the two
     */
    protected static String configureComponentString(String aString, String bString) {
        String string = "";
        if (aString != null && !aString.isEmpty())
            string += "1) " + aString;
        else if (bString != null && !bString.isEmpty())
            string += "1) " + bString;
        return string;
    }

    /**
     * Helper function to avoid duplicate components in merging
     *
     * @param vA version of component A
     * @param vB version of component B
     * @return whether these components can be merged, given their names are the same and only one of their
     * versions are corrupt
     */
    private static boolean versionsCanBeMerged(String vA, String vB) {

        // vA is bad, return true if vB isn't null nor an empty string
        if (vA == null || vA.isEmpty()) {
            return vB != null && !vB.isEmpty();
        }
        // vA is good, return true if vB is null or empty
        else return vB == null || vB.isEmpty();
    }

}
