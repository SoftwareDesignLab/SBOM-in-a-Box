package org.svip.sbomanalysis.comparison.merger;

import org.svip.builders.component.CDX14PackageBuilder;
import org.svip.sbom.builder.objects.schemas.CDX14.CDX14Builder;

import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;

import org.svip.sbom.model.objects.CycloneDX14.CDX14ComponentObject;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;

import org.svip.sbom.model.shared.util.LicenseCollection;

import java.util.*;

/**
 * File: MergerCDX.java
 *
 * Merges two CDX SBOMs together.
 *
 * @author tyler_drake
 */
public class MergerCDX extends Merger {

    public MergerCDX() {
        super();
    }

    @Override
    public SBOM mergeSBOM(SBOM A, SBOM B){

        Set<Component> componentsA = A.getComponents();
        Set<Component> componentsB = B.getComponents();

        // declare SBOM A as the main SBOM, cast it back to CDX14SBOM
        CDX14SBOM mainSBOM = (CDX14SBOM) A;

        // Create a new builder for the new SBOM
        CDX14Builder builder = new CDX14Builder();

        /** Assign all top level data for the new SBOM **/

        // Format
        builder.setFormat(mainSBOM.getFormat());

        // Name
        builder.setName(mainSBOM.getName());

        // UID (In this case, bom-ref)
        builder.setUID(mainSBOM.getUID());

        // SBOM Version
        builder.setVersion(mainSBOM.getVersion());

        // Spec Version (1.4)
        builder.setSpecVersion(mainSBOM.getSpecVersion());

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
        Set<Component> mergedComponents = mergeComponents(componentsA, componentsB);
        for(Component mergedComponent : mergedComponents) {
            builder.addComponent(mergedComponent);
        }

        // Relationships TODO: Add merging of relationships in future sprint

        // External References
        this.mergeExternalReferences(
                A.getExternalReferences(), B.getExternalReferences()
        ).forEach(x -> builder.addExternalReference(x));

        // Return the newly built merged SBOM
        return builder.Build();

    }

    @Override
    protected Set<Component> mergeComponents(Set<Component> A, Set<Component> B) {

        // New collection for merged components
        Set<Component> mergedComponents = new HashSet<>();

        Set<Component> removeB = new HashSet<>();

        // For every component in the first SBOM
        for(Component componentA : A) {

            // Checks to see if component A was merged with another component
            boolean merged = false;

            // Cast the generic component from SBOM A back to a SPDX component
            CDX14ComponentObject componentA_CDX = (CDX14ComponentObject) componentA;

            // For every component in the second SBOM
            for(Component componentB : B) {

                // Cast the generic component from SBOM B back to a SPDX component
                CDX14ComponentObject componentB_CDX = (CDX14ComponentObject) componentB;

                // If the components are the same by Name and Version, merge then add them to the SBOM
                if(componentA_CDX.getName() == componentB_CDX.getName() && componentA_CDX.getVersion() == componentB_CDX.getVersion()) {

                    mergedComponents.add(mergeComponent(componentA, componentB));
                    removeB.add(componentB);
                    merged = true;

                }

            }

            B.removeAll(removeB);

            // If component A was not merged with anything, add it to the new components
            if(!merged) mergedComponents.add(componentA);
        }

        // Merge remaining components from SBOM B that were not merged with any components from A
        for(Component componentB : B) { mergedComponents.add(componentB); }

        // Return the merged components set
        return mergedComponents;

    }

    @Override
    protected Component mergeComponent(Component A, Component B) {

        // New builder for the merged component
        CDX14PackageBuilder compBuilder = new CDX14PackageBuilder();

        CDX14ComponentObject componentA_CDX = (CDX14ComponentObject) A;
        CDX14ComponentObject componentB_CDX = (CDX14ComponentObject) B;

        // Type : If A Type isn't empty or null, Merged Component uses A, otherwise use
        if(!componentA_CDX.getType().isEmpty() && componentA_CDX.getType() != null)
            compBuilder.setType(componentA_CDX.getType());
        else compBuilder.setType(componentB_CDX.getType());

        // UID : If A UID isn't empty or null, Merged Component uses A, otherwise use B
        if(!componentA_CDX.getUID().isEmpty() && componentA_CDX.getUID() != null)
            compBuilder.setUID(componentA_CDX.getUID());
        else compBuilder.setUID(componentB_CDX.getUID());

        // Author : If A 'Author' isn't empty or null, Merged Component uses A, otherwise use B
        if(!componentA_CDX.getAuthor().isEmpty() && componentA_CDX.getAuthor() != null)
            compBuilder.setAuthor(componentA_CDX.getAuthor());
        else compBuilder.setAuthor(componentB_CDX.getAuthor());

        // Name : If A 'Name' isn't empty or null, Merged Component uses A, otherwise use B
        if(!componentA_CDX.getName().isEmpty() && componentA_CDX.getName() != null)
            compBuilder.setName(componentA_CDX.getName());
        else compBuilder.setName(componentB_CDX.getName());

        // Licenses : Merge Licenses of A and B together
        LicenseCollection mergedLicenses = new LicenseCollection();

        Set<String> concludedA = componentA_CDX.getLicenses().getConcluded();

        if(!concludedA.isEmpty() && concludedA != null) {
            concludedA.stream().forEach(
                    x -> mergedLicenses.addConcludedLicenseString(x)
            );
        }

        Set<String> declaredA = componentA_CDX.getLicenses().getDeclared();

        if(!declaredA.isEmpty() && !declaredA.equals(null)) {
            declaredA.stream().forEach(
                    x -> mergedLicenses.addDeclaredLicense(x)
            );
        }

        Set<String> fileA = componentA_CDX.getLicenses().getInfoFromFiles();

        if(!fileA.isEmpty() && !fileA.equals(null)) {
            fileA.stream().forEach(
                    x -> mergedLicenses.addLicenseInfoFromFile(x)
            );
        }

        Set<String> concludedB = componentB_CDX.getLicenses().getConcluded();

        if(!concludedB.isEmpty() && !concludedB.equals(null)) {
            concludedB.stream().forEach(
                    x -> mergedLicenses.addConcludedLicenseString(x)
            );
        }

        Set<String> declaredB = componentB_CDX.getLicenses().getDeclared();

        if(!declaredB.isEmpty() && !declaredB.equals(null)) {
            declaredB.stream().forEach(
                    x -> mergedLicenses.addDeclaredLicense(x)
            );
        }

        Set<String> fileB = componentB_CDX.getLicenses().getInfoFromFiles();

        if(!fileB.isEmpty() && !fileB.equals(null)) {
            fileB.stream().forEach(
                    x -> mergedLicenses.addLicenseInfoFromFile(x)
            );
        }

        compBuilder.setLicenses(mergedLicenses);

        compBuilder.setCopyright("1) " + componentA_CDX.getCopyright() + "\n2) " + componentB_CDX.getCopyright());

        // Hashes
        Map<String, String> hashesA = componentA_CDX.getHashes();
        Map<String, String> hashesB = componentB_CDX.getHashes();

        for(String keyB : hashesB.keySet()) { compBuilder.addHash(keyB, hashesB.get(keyB)); }
        for(String keyA : hashesA.keySet()) { compBuilder.addHash(keyA, hashesA.get(keyA)); }

        // Supplier
        compBuilder.setSupplier(mergeOrganization(componentA_CDX.getSupplier(), componentB_CDX.getSupplier()));

        // Version : Since they already match, default it to component A version
        compBuilder.setVersion(componentA_CDX.getVersion());

        // Description
        compBuilder.setDescription(componentA_CDX.getDescription());

        // CPEs
        for(String cpeA : componentA_CDX.getCPEs()) { compBuilder.addCPE(cpeA); }
        for(String cpeB : componentB_CDX.getCPEs()) { compBuilder.addCPE(cpeB); }

        // PURLs
        for(String purlA : componentA_CDX.getPURLs()) { compBuilder.addPURL(purlA); }
        for(String purlB : componentB_CDX.getPURLs()) { compBuilder.addPURL(purlB); }

        // External References
        mergeExternalReferences(
                componentA_CDX.getExternalReferences(), componentB_CDX.getExternalReferences()
        ).forEach(x -> compBuilder.addExternalReference(x));

        // Mime Type
        compBuilder.setMimeType(componentA_CDX.getMimeType());

        // Publisher
        compBuilder.setPublisher(componentA_CDX.getPublisher());

        // Scope
        compBuilder.setScope(componentA_CDX.getScope());

        // Group
        compBuilder.setGroup(componentA_CDX.getGroup());

        // Properties
        if(componentB_CDX.getProperties() != null) componentB_CDX.getProperties().keySet().stream().forEach(
                x -> componentB_CDX.getProperties().get(x).stream().forEach(y -> compBuilder.addProperty(x, y))
        );
        if(componentA_CDX.getProperties() != null) componentA_CDX.getProperties().keySet().stream().forEach(
                x -> componentA_CDX.getProperties().get(x).stream().forEach(y -> compBuilder.addProperty(x, y))
        );

        // Build the merged component and return it
        return compBuilder.build();

    }

}