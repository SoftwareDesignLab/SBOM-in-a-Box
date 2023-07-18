package org.svip.sbomanalysis.comparison.merger;

import org.svip.builders.component.SPDX23PackageBuilder;
import org.svip.sbom.builder.objects.schemas.SPDX23.SPDX23Builder;

import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;

import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23Package;
import org.svip.sbom.model.objects.SPDX23.SPDX23PackageObject;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.sbom.model.shared.util.LicenseCollection;

import java.util.*;

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
        mergeExternalReferences(
                A.getExternalReferences(), B.getExternalReferences()
        ).forEach(x -> builder.addExternalReference(x));

        // Return the newly built merged SBOM
        return builder.Build();

    }

    @Override
    protected Set<Component> mergeComponents(Set<Component> A, Set<Component> B) {

        // New Components collection
        Set<Component> mergedComponents = new HashSet<>();

        Set<Component> removeB = new HashSet<>();

        // For every component in the first SBOM
        for(Component componentA : A) {

            // Checks to see if component A was merged with another component
            boolean merged = false;

            // Cast the generic component from SBOM A back to a SPDX component
            SPDX23PackageObject componentA_SPDX = (SPDX23PackageObject) componentA;

            // For every component in the second SBOM
            for(Component componentB : B) {

                // Cast the generic component from SBOM B back to a SPDX component
                SPDX23Package componentB_SPDX = (SPDX23Package) componentB;

                // If the components are the same by Name and Version, merge then add them to the SBOM
                if(componentA_SPDX.getName() == componentB_SPDX.getName() && componentA_SPDX.getVersion() == componentB_SPDX.getVersion()) {

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
        SPDX23PackageBuilder compBuilder = new SPDX23PackageBuilder();

        SPDX23PackageObject componentA_SPDX = (SPDX23PackageObject) A;
        SPDX23PackageObject componentB_SPDX = (SPDX23PackageObject) B;

        // Type : If A Type isn't empty or null, Merged Component uses A, otherwise use
        if(componentA_SPDX.getType() != null && !componentA_SPDX.getType().isEmpty())
            compBuilder.setType(componentA_SPDX.getType());
        else compBuilder.setType(componentB_SPDX.getType());

        // UID : If A UID isn't empty or null, Merged Component uses A, otherwise use B
        if(componentA_SPDX.getUID() != null && !componentA_SPDX.getUID().isEmpty())
            compBuilder.setUID(componentA_SPDX.getUID());
        else compBuilder.setUID(componentB_SPDX.getUID());

        // Author : If A 'Author' isn't empty or null, Merged Component uses A, otherwise use B
        if(componentA_SPDX.getAuthor() != null && !componentA_SPDX.getAuthor().isEmpty())
            compBuilder.setAuthor(componentA_SPDX.getAuthor());
        else compBuilder.setAuthor(componentB_SPDX.getAuthor());

        // Name : If A 'Name' isn't empty or null, Merged Component uses A, otherwise use B
        if(componentA_SPDX.getName() != null && !componentA_SPDX.getName().isEmpty())
            compBuilder.setName(componentA_SPDX.getName());
        else compBuilder.setName(componentB_SPDX.getName());

        // Licenses : Merge Licenses of A and B together
        LicenseCollection mergedLicenses = new LicenseCollection();

        Set<String> concludedA = componentA_SPDX.getLicenses().getConcluded();

        if(!concludedA.isEmpty() && concludedA != null) {
            concludedA.stream().forEach(
                    x -> mergedLicenses.addConcludedLicenseString(x)
            );
        }

        Set<String> declaredA = componentA_SPDX.getLicenses().getDeclared();

        if(!declaredA.isEmpty() && !declaredA.equals(null)) {
            declaredA.stream().forEach(
                    x -> mergedLicenses.addDeclaredLicense(x)
            );
        }

        Set<String> fileA = componentA_SPDX.getLicenses().getInfoFromFiles();

        if(!fileA.isEmpty() && !fileA.equals(null)) {
            fileA.stream().forEach(
                    x -> mergedLicenses.addLicenseInfoFromFile(x)
            );
        }

        Set<String> concludedB = componentB_SPDX.getLicenses().getConcluded();

        if(!concludedB.isEmpty() && !concludedB.equals(null)) {
            concludedB.stream().forEach(
                    x -> mergedLicenses.addConcludedLicenseString(x)
            );
        }

        Set<String> declaredB = componentB_SPDX.getLicenses().getDeclared();

        if(!declaredB.isEmpty() && !declaredB.equals(null)) {
            declaredB.stream().forEach(
                    x -> mergedLicenses.addDeclaredLicense(x)
            );
        }

        Set<String> fileB = componentB_SPDX.getLicenses().getInfoFromFiles();

        if(!fileB.isEmpty() && !fileB.equals(null)) {
            fileB.stream().forEach(
                    x -> mergedLicenses.addLicenseInfoFromFile(x)
            );
        }

        compBuilder.setLicenses(mergedLicenses);

        // Copyright
        String copyright = "";
        if(componentA_SPDX.getCopyright()!=null && !componentA_SPDX.getCopyright().isEmpty())
            copyright += "1) " + componentA_SPDX.getCopyright();
        if(componentB_SPDX.getCopyright()!=null && !componentB_SPDX.getCopyright().isEmpty() && !copyright.isEmpty())
            copyright += "\n2) " + componentB_SPDX.getCopyright();
        else if(componentB_SPDX.getCopyright()!=null && !componentB_SPDX.getCopyright().isEmpty() && copyright.isEmpty())
            copyright += "1) " + componentB_SPDX.getCopyright();

        compBuilder.setCopyright(copyright);

        // Hashes
        Map<String, String> hashesA = componentA_SPDX.getHashes();
        Map<String, String> hashesB = componentB_SPDX.getHashes();

        for(String keyB : hashesB.keySet()) { compBuilder.addHash(keyB, hashesB.get(keyB)); }
        for(String keyA : hashesA.keySet()) { compBuilder.addHash(keyA, hashesA.get(keyA)); }

        // Comment
        String comment = "";
        if(componentA_SPDX.getComment()!=null && !componentA_SPDX.getComment().isEmpty())
            comment += "1) " + componentA_SPDX.getComment();
        if(componentB_SPDX.getComment()!=null && !componentB_SPDX.getComment().isEmpty() && !comment.isEmpty())
            comment += "\n2) " + componentB_SPDX.getComment();
        else if(componentB_SPDX.getComment()!=null && !componentB_SPDX.getComment().isEmpty() && comment.isEmpty())
            comment += "1) " + componentB_SPDX.getComment();

        compBuilder.setComment(comment);

        // Attribution Text
        if(componentA_SPDX.getAttributionText() != null && !componentA_SPDX.getAttributionText().isEmpty())
            compBuilder.setAttributionText(componentA_SPDX.getAttributionText());
        else compBuilder.setAttributionText(componentB_SPDX.getAttributionText());

        // Download Location
        if(componentA_SPDX.getDownloadLocation() != null && !componentA_SPDX.getDownloadLocation().isEmpty())
            compBuilder.setDownloadLocation(componentA_SPDX.getDownloadLocation());
        else compBuilder.setDownloadLocation(componentB_SPDX.getDownloadLocation());

        // FileName
        if(componentA_SPDX.getFileName() != null && !componentA_SPDX.getFileName().isEmpty())
            compBuilder.setFileName(componentA_SPDX.getFileName());
        else compBuilder.setFileName(componentB_SPDX.getFileName());

        // Files Analyzed
        // TODO: determine if a FilesAnalzyed mistmatch should return true or false
        if(componentA_SPDX.getFilesAnalyzed() == true && componentA_SPDX.getFilesAnalyzed() == true)
            compBuilder.setFilesAnalyzed(true);
        else compBuilder.setFilesAnalyzed(false);

        // Verification Code
        String verificationCode = "";
        if(componentA_SPDX.getVerificationCode()!=null && !componentA_SPDX.getVerificationCode().isEmpty())
            verificationCode += "1) " + componentA_SPDX.getVerificationCode();
        if(componentB_SPDX.getVerificationCode()!=null && !componentB_SPDX.getVerificationCode().isEmpty() && !verificationCode.isEmpty())
            verificationCode += "\n2) " + componentB_SPDX.getVerificationCode();
        else if(componentB_SPDX.getVerificationCode()!=null && !componentB_SPDX.getVerificationCode().isEmpty() && verificationCode.isEmpty())
            verificationCode += "1) " + componentB_SPDX.getVerificationCode();

        compBuilder.setVerificationCode(verificationCode);

        // Homepage
        String homepage = "";
        if(componentA_SPDX.getHomePage()!=null && !componentA_SPDX.getHomePage().isEmpty())
            homepage += "1) " + componentA_SPDX.getHomePage();
        if(componentB_SPDX.getHomePage()!=null && !componentB_SPDX.getHomePage().isEmpty() && !homepage.isEmpty())
            homepage += "\n2) " + componentB_SPDX.getVerificationCode();
        else if(componentB_SPDX.getHomePage()!=null && !componentB_SPDX.getHomePage().isEmpty() && homepage.isEmpty())
            homepage += "1) " + componentB_SPDX.getVerificationCode();

        compBuilder.setHomePage(homepage);

        // Source Info
        String sourceInfo = "";
        if(componentA_SPDX.getSourceInfo()!=null && !componentA_SPDX.getSourceInfo().isEmpty())
            sourceInfo += "1) " + componentA_SPDX.getSourceInfo();
        if(componentB_SPDX.getSourceInfo()!=null && !componentB_SPDX.getSourceInfo().isEmpty() && !sourceInfo.isEmpty())
            sourceInfo += "\n2) " + componentB_SPDX.getSourceInfo();
        else if(componentB_SPDX.getSourceInfo()!=null && !componentB_SPDX.getSourceInfo().isEmpty() && sourceInfo.isEmpty())
            sourceInfo += "1) " + componentB_SPDX.getSourceInfo();

        compBuilder.setSourceInfo(sourceInfo);

        // Release Date
        if(componentA_SPDX.getReleaseDate() != null && !componentA_SPDX.getReleaseDate().isEmpty())
            compBuilder.setReleaseDate(componentA_SPDX.getReleaseDate());
        else compBuilder.setReleaseDate(componentB_SPDX.getReleaseDate());

        // Built Date
        if(componentA_SPDX.getBuiltDate() != null && !componentA_SPDX.getBuiltDate().isEmpty())
            compBuilder.setBuildDate(componentA_SPDX.getBuiltDate());
        else compBuilder.setBuildDate(componentB_SPDX.getBuiltDate());

        // Valid Until Date
        if(componentA_SPDX.getValidUntilDate() != null && !componentA_SPDX.getValidUntilDate().isEmpty())
            compBuilder.setValidUntilDate(componentA_SPDX.getValidUntilDate());
        else compBuilder.setValidUntilDate(componentB_SPDX.getValidUntilDate());

        // Supplier
        compBuilder.setSupplier(mergeOrganization(componentA_SPDX.getSupplier(), componentB_SPDX.getSupplier()));

        // Version
        if(componentA_SPDX.getVersion() != null && !componentA_SPDX.getVersion().isEmpty())
            compBuilder.setVersion(componentA_SPDX.getVersion());
        else compBuilder.setVersion(componentB_SPDX.getVersion());

        // CPEs
        for(String cpeA : componentA_SPDX.getCPEs()) { compBuilder.addCPE(cpeA); }
        for(String cpeB : componentB_SPDX.getCPEs()) { compBuilder.addCPE(cpeB); }

        // PURLs
        for(String purlA : componentA_SPDX.getPURLs()) { compBuilder.addPURL(purlA); }
        for(String purlB : componentB_SPDX.getPURLs()) { compBuilder.addPURL(purlB); }

        // External References
        mergeExternalReferences(
                componentA_SPDX.getExternalReferences(), componentB_SPDX.getExternalReferences()
        ).forEach(x -> compBuilder.addExternalReference(x));

        // Return the newly merged component
        return compBuilder.build();

    }

}
