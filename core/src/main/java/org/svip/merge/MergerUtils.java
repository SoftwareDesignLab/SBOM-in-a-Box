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
import org.svip.sbom.model.shared.util.LicenseCollection;
import org.svip.serializers.SerializerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Utility class to prevent creating large modules
 *
 * @author Juan Francisco Patino
 * @author Tyler Drake
 */
public abstract class MergerUtils extends Merger {

    protected static SBOM mergeToSchema(SBOM A, SBOM B, Set<Component> componentsA, Set<Component> componentsB, SBOM mainSBOM,
                                        SBOMBuilder builder, SerializerFactory.Schema targetSchema, String newName) throws MergerException {

        /** Assign all top level data for the new SBOM **/

        // Format
        builder.setFormat(mainSBOM.getFormat());

        // Name
        if (targetSchema == SerializerFactory.Schema.SVIP)
            builder.setName(newName);
        else if (mainSBOM.getName() == null || mainSBOM.getName().length() == 0)
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
            default -> specVersion = "1.0-a";
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
        Set<Component> mergedComponents = mergeComponentsToSchema(componentsA, componentsB, targetSchema);
        for (Component mergedComponent : mergedComponents) {
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

    protected static Component mergeComponentToSchema(Component A, Component B, SerializerFactory.Schema targetSchema) {


        // New builder for the merged component
        SVIPComponentBuilder compBuilder = new SVIPComponentBuilder();

        Component componentA = A;
        Component componentB = B;

        // Type : If A Type isn't empty or null, Merged Component uses A, otherwise use
        if (componentA.getType() != null && !componentA.getType().isEmpty())
            compBuilder.setType(componentA.getType());
        else compBuilder.setType(componentB.getType());

        // UID : If A UID isn't empty or null, Merged Component uses A, otherwise use B
        if (componentA.getUID() != null && !componentA.getUID().isEmpty())
            compBuilder.setUID(componentA.getUID());
        else compBuilder.setUID(componentB.getUID());

        // Author : If A 'Author' isn't empty or null, Merged Component uses A, otherwise use B
        if (componentA.getAuthor() != null && !componentA.getAuthor().isEmpty())
            compBuilder.setAuthor(componentA.getAuthor());
        else compBuilder.setAuthor(componentB.getAuthor());

        // Name : If A 'Name' isn't empty or null, Merged Component uses A, otherwise use B
        if (componentA.getName() != null && !componentA.getName().isEmpty())
            compBuilder.setName(componentA.getName());
        else compBuilder.setName(componentB.getName());

        // Licenses : Merge Licenses of A and B together
        LicenseCollection mergedLicenses = new LicenseCollection();

        Set<String> concludedA = componentA.getLicenses().getConcluded();

        if (!concludedA.isEmpty()) {
            concludedA.forEach(
                    mergedLicenses::addConcludedLicenseString
            );
        }

        Utils.addLicenses(componentA, mergedLicenses);

        Set<String> concludedB = componentB.getLicenses().getConcluded();

        if (!concludedB.isEmpty()) {
            concludedB.forEach(
                    mergedLicenses::addConcludedLicenseString
            );
        }

        Utils.addLicenses(componentB, mergedLicenses);

        compBuilder.setLicenses(mergedLicenses);

        /*
            Target schema differences
         */

        // Hashes
        Map<String, String> hashesA = componentA.getHashes();
        Map<String, String> hashesB = componentB.getHashes();

        for (String keyB : hashesB.keySet()) {
            compBuilder.addHash(keyB, hashesB.get(keyB));
        }
        for (String keyA : hashesA.keySet()) {
            compBuilder.addHash(keyA, hashesA.get(keyA));
        }

        switch (targetSchema) {
            case SPDX23 -> {
                // Copyright
                String copyright = "";
                if (componentA.getCopyright() != null && !componentA.getCopyright().isEmpty())
                    copyright += "1) " + componentA.getCopyright();
                if (componentB.getCopyright() != null && !componentB.getCopyright().isEmpty() && !copyright.isEmpty())
                    copyright += "\n2) " + componentB.getCopyright();
                else if (componentB.getCopyright() != null && !componentB.getCopyright().isEmpty() && copyright.isEmpty())
                    copyright += "1) " + componentB.getCopyright();

                compBuilder.setCopyright(copyright);

                SPDX23PackageObject spdx23PackageObjectA = (SPDX23PackageObject) componentA;
                SPDX23PackageObject spdx23PackageObjectB = (SPDX23PackageObject) componentB;

                // Comment
                String comment = "";
                if (spdx23PackageObjectA.getComment() != null && !spdx23PackageObjectA.getComment().isEmpty())
                    comment += "1) " + spdx23PackageObjectA.getComment();
                if (spdx23PackageObjectB.getComment() != null && !spdx23PackageObjectB.getComment().isEmpty() && !comment.isEmpty())
                    comment += "\n2) " + spdx23PackageObjectB.getComment();
                else if (spdx23PackageObjectB.getComment() != null && !spdx23PackageObjectB.getComment().isEmpty() && comment.isEmpty())
                    comment += "1) " + spdx23PackageObjectB.getComment();

                compBuilder.setComment(comment);

                // Attribution Text
                if (spdx23PackageObjectA.getAttributionText() != null && !spdx23PackageObjectA.getAttributionText().isEmpty())
                    compBuilder.setAttributionText(spdx23PackageObjectA.getAttributionText());
                else compBuilder.setAttributionText(spdx23PackageObjectB.getAttributionText());

                // Download Location
                if (spdx23PackageObjectA.getDownloadLocation() != null && !spdx23PackageObjectA.getDownloadLocation().isEmpty())
                    compBuilder.setDownloadLocation(spdx23PackageObjectA.getDownloadLocation());
                else compBuilder.setDownloadLocation(spdx23PackageObjectB.getDownloadLocation());

                // FileName
                if (spdx23PackageObjectA.getFileName() != null && !spdx23PackageObjectA.getFileName().isEmpty())
                    compBuilder.setFileName(spdx23PackageObjectA.getFileName());
                else compBuilder.setFileName(spdx23PackageObjectB.getFileName());

                // Files Analyzed
                // TODO: determine if a FilesAnalzyed mistmatch should return true or false
                compBuilder.setFilesAnalyzed(spdx23PackageObjectA.getFilesAnalyzed() && spdx23PackageObjectA.getFilesAnalyzed());

                // Verification Code
                String verificationCode = "";
                if (spdx23PackageObjectA.getVerificationCode() != null && !spdx23PackageObjectA.getVerificationCode().isEmpty())
                    verificationCode += "1) " + spdx23PackageObjectA.getVerificationCode();
                if (spdx23PackageObjectB.getVerificationCode() != null && !spdx23PackageObjectB.getVerificationCode().isEmpty() && !verificationCode.isEmpty())
                    verificationCode += "\n2) " + spdx23PackageObjectB.getVerificationCode();
                else if (spdx23PackageObjectB.getVerificationCode() != null && !spdx23PackageObjectB.getVerificationCode().isEmpty() && verificationCode.isEmpty())
                    verificationCode += "1) " + spdx23PackageObjectB.getVerificationCode();

                compBuilder.setVerificationCode(verificationCode);

                // Homepage
                String homepage = "";
                if (spdx23PackageObjectA.getHomePage() != null && !spdx23PackageObjectA.getHomePage().isEmpty())
                    homepage += "1) " + spdx23PackageObjectA.getHomePage();
                if (spdx23PackageObjectB.getHomePage() != null && !spdx23PackageObjectB.getHomePage().isEmpty() && !homepage.isEmpty())
                    homepage += "\n2) " + spdx23PackageObjectB.getVerificationCode();
                else if (spdx23PackageObjectB.getHomePage() != null && !spdx23PackageObjectB.getHomePage().isEmpty() && homepage.isEmpty())
                    homepage += "1) " + spdx23PackageObjectB.getVerificationCode();

                compBuilder.setHomePage(homepage);

                // Source Info
                String sourceInfo = "";
                if (spdx23PackageObjectA.getSourceInfo() != null && !spdx23PackageObjectA.getSourceInfo().isEmpty())
                    sourceInfo += "1) " + spdx23PackageObjectA.getSourceInfo();
                if (spdx23PackageObjectB.getSourceInfo() != null && !spdx23PackageObjectB.getSourceInfo().isEmpty() && !sourceInfo.isEmpty())
                    sourceInfo += "\n2) " + spdx23PackageObjectB.getSourceInfo();
                else if (spdx23PackageObjectB.getSourceInfo() != null && !spdx23PackageObjectB.getSourceInfo().isEmpty() && sourceInfo.isEmpty())
                    sourceInfo += "1) " + spdx23PackageObjectB.getSourceInfo();

                compBuilder.setSourceInfo(sourceInfo);

                // Release Date
                if (spdx23PackageObjectA.getReleaseDate() != null && !spdx23PackageObjectA.getReleaseDate().isEmpty())
                    compBuilder.setReleaseDate(spdx23PackageObjectA.getReleaseDate());
                else compBuilder.setReleaseDate(spdx23PackageObjectB.getReleaseDate());

                // Built Date
                if (spdx23PackageObjectA.getBuiltDate() != null && !spdx23PackageObjectA.getBuiltDate().isEmpty())
                    compBuilder.setBuildDate(spdx23PackageObjectA.getBuiltDate());
                else compBuilder.setBuildDate(spdx23PackageObjectB.getBuiltDate());

                // Valid Until Date
                if (spdx23PackageObjectA.getValidUntilDate() != null && !spdx23PackageObjectA.getValidUntilDate().isEmpty())
                    compBuilder.setValidUntilDate(spdx23PackageObjectA.getValidUntilDate());
                else compBuilder.setValidUntilDate(spdx23PackageObjectB.getValidUntilDate());

                // Supplier
                compBuilder.setSupplier(mergeOrganization(spdx23PackageObjectA.getSupplier(), spdx23PackageObjectB.getSupplier()));

                // Version
                if (spdx23PackageObjectA.getVersion() != null && !spdx23PackageObjectA.getVersion().isEmpty())
                    compBuilder.setVersion(spdx23PackageObjectA.getVersion());
                else compBuilder.setVersion(spdx23PackageObjectB.getVersion());

                // CPEs
                for (String cpeA : spdx23PackageObjectA.getCPEs()) {
                    compBuilder.addCPE(cpeA);
                }
                for (String cpeB : spdx23PackageObjectB.getCPEs()) {
                    compBuilder.addCPE(cpeB);
                }

                // PURLs
                for (String purlA : spdx23PackageObjectA.getPURLs()) {
                    compBuilder.addPURL(purlA);
                }
                for (String purlB : spdx23PackageObjectB.getPURLs()) {
                    compBuilder.addPURL(purlB);
                }

                // External References
                mergeExternalReferences(
                        spdx23PackageObjectA.getExternalReferences(), spdx23PackageObjectB.getExternalReferences()
                ).forEach(compBuilder::addExternalReference);

            }
            case CDX14 -> {

                CDX14ComponentObject componentA_CDX = (CDX14ComponentObject) componentA;
                CDX14ComponentObject componentB_CDX = (CDX14ComponentObject) componentB;

                compBuilder.setCopyright("1) " + componentA_CDX.getCopyright() + "\n2) " + componentB_CDX.getCopyright());

                // Supplier
                compBuilder.setSupplier(mergeOrganization(componentA_CDX.getSupplier(), componentB_CDX.getSupplier()));

                // Version : Since they already match, default it to component A version
                compBuilder.setVersion(componentA_CDX.getVersion());

                // Description
                compBuilder.setDescription(componentA_CDX.getDescription());

                // CPEs
                for (String cpeA : componentA_CDX.getCPEs()) {
                    compBuilder.addCPE(cpeA);
                }
                for (String cpeB : componentB_CDX.getCPEs()) {
                    compBuilder.addCPE(cpeB);
                }

                // PURLs
                for (String purlA : componentA_CDX.getPURLs()) {
                    compBuilder.addPURL(purlA);
                }
                for (String purlB : componentB_CDX.getPURLs()) {
                    compBuilder.addPURL(purlB);
                }

                // External References
                mergeExternalReferences(
                        componentA_CDX.getExternalReferences(), componentB_CDX.getExternalReferences()
                ).forEach(compBuilder::addExternalReference);

                // Mime Type
                compBuilder.setMimeType(componentA_CDX.getMimeType());

                // Publisher
                compBuilder.setPublisher(componentA_CDX.getPublisher());

                // Scope
                compBuilder.setScope(componentA_CDX.getScope());

                // Group
                compBuilder.setGroup(componentA_CDX.getGroup());

                // Properties
                if (componentB_CDX.getProperties() != null) componentB_CDX.getProperties().keySet().forEach(
                        x -> componentB_CDX.getProperties().get(x).forEach(y -> compBuilder.addProperty(x, y))
                );
                if (componentA_CDX.getProperties() != null) componentA_CDX.getProperties().keySet().forEach(
                        x -> componentA_CDX.getProperties().get(x).forEach(y -> compBuilder.addProperty(x, y))
                );

            }
            default -> { // SVIP

                SPDX23PackageObject spdx23PackageObjectA = null;
                SPDX23FileObject spdx23FileObject = null;

                try {
                    spdx23PackageObjectA = (SPDX23PackageObject) componentA;
                } catch (ClassCastException e) {
                    spdx23FileObject = (SPDX23FileObject) componentA; // leaving this in case at some point SPDX
                    // file objects can merge with CDXComponentObjects
                }
                CDX14ComponentObject componentB_CDX = (CDX14ComponentObject) componentB;

                /*
                    SPDX component A
                 */

                String comment = "";
                if (spdx23FileObject == null) {
                    // Download Location
                    if (spdx23PackageObjectA.getDownloadLocation() != null && !spdx23PackageObjectA.getDownloadLocation().isEmpty())
                        compBuilder.setDownloadLocation(spdx23PackageObjectA.getDownloadLocation());

                    // FileName
                    if (spdx23PackageObjectA.getFileName() != null && !spdx23PackageObjectA.getFileName().isEmpty())
                        compBuilder.setFileName(spdx23PackageObjectA.getFileName());

                    // Files Analyzed
                    // TODO: determine if a FilesAnalzyed mistmatch should return true or false
                    compBuilder.setFilesAnalyzed(spdx23PackageObjectA.getFilesAnalyzed() && spdx23PackageObjectA.getFilesAnalyzed());

                    // Verification Code
                    String verificationCode = "";
                    if (spdx23PackageObjectA.getVerificationCode() != null && !spdx23PackageObjectA.getVerificationCode().isEmpty())
                        verificationCode += "1) " + spdx23PackageObjectA.getVerificationCode();
                    compBuilder.setVerificationCode(verificationCode);

                    // Homepage
                    String homepage = "";
                    if (spdx23PackageObjectA.getHomePage() != null && !spdx23PackageObjectA.getHomePage().isEmpty())
                        homepage += "1) " + spdx23PackageObjectA.getHomePage();
                    compBuilder.setHomePage(homepage);

                    // Source Info
                    String sourceInfo = "";
                    if (spdx23PackageObjectA.getSourceInfo() != null && !spdx23PackageObjectA.getSourceInfo().isEmpty())
                        sourceInfo += "1) " + spdx23PackageObjectA.getSourceInfo();

                    compBuilder.setSourceInfo(sourceInfo);

                    // Release Date
                    if (spdx23PackageObjectA.getReleaseDate() != null && !spdx23PackageObjectA.getReleaseDate().isEmpty())
                        compBuilder.setReleaseDate(spdx23PackageObjectA.getReleaseDate());

                    // Built Date
                    if (spdx23PackageObjectA.getBuiltDate() != null && !spdx23PackageObjectA.getBuiltDate().isEmpty())
                        compBuilder.setBuildDate(spdx23PackageObjectA.getBuiltDate());

                    // Valid Until Date
                    if (spdx23PackageObjectA.getValidUntilDate() != null && !spdx23PackageObjectA.getValidUntilDate().isEmpty())
                        compBuilder.setValidUntilDate(spdx23PackageObjectA.getValidUntilDate());

                    // Supplier
                    compBuilder.setSupplier(spdx23PackageObjectA.getSupplier());

                    // Version // default to comp A version
                    if (spdx23PackageObjectA.getVersion() != null && !spdx23PackageObjectA.getVersion().isEmpty())
                        compBuilder.setVersion(spdx23PackageObjectA.getVersion());

                    // CPEs
                    for (String cpeA : spdx23PackageObjectA.getCPEs()) {
                        compBuilder.addCPE(cpeA);
                    }

                    // PURLs
                    for (String purlA : spdx23PackageObjectA.getPURLs()) {
                        compBuilder.addPURL(purlA);
                    }

                    // Comment
                    if (spdx23PackageObjectA.getComment() != null && !spdx23PackageObjectA.getComment().isEmpty())
                        comment += "1) " + spdx23PackageObjectA.getComment();

                    compBuilder.setComment(comment);

                    // Attribution Text
                    if (spdx23PackageObjectA.getAttributionText() != null && !spdx23PackageObjectA.getAttributionText().isEmpty())
                        compBuilder.setAttributionText(spdx23PackageObjectA.getAttributionText());

                                    /*
                    Both (NOT FILE OBJECT)
                 */

                    // External References
                    mergeExternalReferences(
                            spdx23PackageObjectA.getExternalReferences(), componentB_CDX.getExternalReferences()
                    ).forEach(compBuilder::addExternalReference);

                } else {

                    // Comment
                    if (spdx23FileObject.getComment() != null && !spdx23FileObject.getComment().isEmpty())
                        comment += "1) " + spdx23FileObject.getComment();

                    compBuilder.setComment(comment);

                    // Attribution Text
                    if (spdx23FileObject.getAttributionText() != null && !spdx23FileObject.getAttributionText().isEmpty())
                        compBuilder.setAttributionText(spdx23FileObject.getAttributionText());

                }


                /*
                    CDX specific
                 */

                // Description
                compBuilder.setDescription(componentB_CDX.getDescription());

                // CPEs
                for (String cpeB : componentB_CDX.getCPEs()) {
                    compBuilder.addCPE(cpeB);
                }

                // PURLs
                for (String purlB : componentB_CDX.getPURLs()) {
                    compBuilder.addPURL(purlB);
                }

                // Mime Type
                compBuilder.setMimeType(componentB_CDX.getMimeType());

                // Publisher
                compBuilder.setPublisher(componentB_CDX.getPublisher());

                // Scope
                compBuilder.setScope(componentB_CDX.getScope());

                // Group
                compBuilder.setGroup(componentB_CDX.getGroup());

                // Properties
                if (componentB_CDX.getProperties() != null) componentB_CDX.getProperties().keySet().forEach(
                        x -> componentB_CDX.getProperties().get(x).forEach(y -> compBuilder.addProperty(x, y))
                );


            }
        }

        // Return the newly merged component
        return compBuilder.build();

    }


    protected static Set<Component> mergeComponentsToSchema(Set<Component> A, Set<Component> B, SerializerFactory.Schema targetSchema) throws MergerException {

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
                            if (componentA_SPDX.getName() == componentB_SPDX.getName() &&
                                    componentA_SPDX.getVersion() == componentB_SPDX.getVersion()) {

                                mergedComponents.add(mergeComponentToSchema(componentA, componentB, targetSchema));
                                removeB.add(componentB);
                                merged = true;

                            } else if (componentA_SPDXFile != null && componentB_SPDXFile != null) // if both Files then merge if appropriate
                                if (componentA_SPDXFile.getName() == componentB_SPDXFile.getName()) {

                                    mergedComponents.add(mergeComponentToSchema(componentA, componentB, targetSchema));
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
                        if (componentA_CDX.getName() == componentB_CDX.getName() && componentA_CDX.getVersion() == componentB_CDX.getVersion()) {

                            mergedComponents.add(mergeComponentToSchema(componentA, componentB, targetSchema));
                            removeB.add(componentB);
                            merged = true;

                        }

                    }
                    default -> { // SVIP
                        // Cast the generic component from SBOM A back to a CDX component
                        SVIPComponentObject componentA_SVIP = (SVIPComponentObject) componentA;
                        SVIPComponentObject componentB_SVIP = (SVIPComponentObject) componentB;

                        // If the components are the same by Name and Version, merge then add them to the SBOM
                        if (componentA_SVIP.getName() == componentB_SVIP.getName() && componentA_SVIP.getVersion() == componentB_SVIP.getVersion()) {

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
        for (Component componentB : B) {
            mergedComponents.add(componentB);
        }

        // Return the merged components set
        return mergedComponents;

    }

}
