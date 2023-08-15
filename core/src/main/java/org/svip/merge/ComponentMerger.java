package org.svip.merge;

import org.svip.merge.utils.Utils;
import org.svip.sbom.builder.objects.SVIPComponentBuilder;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.objects.CycloneDX14.CDX14ComponentObject;
import org.svip.sbom.model.objects.SPDX23.SPDX23FileObject;
import org.svip.sbom.model.objects.SPDX23.SPDX23PackageObject;
import org.svip.sbom.model.objects.SVIPComponentObject;
import org.svip.sbom.model.shared.util.LicenseCollection;
import org.svip.serializers.SerializerFactory;

import java.util.Map;
import java.util.Set;

/**
 * Static class to hold the logic for merging two components
 */
public class ComponentMerger {


    /**
     * @param A            component from SBOM A
     * @param B            component from SBOM B
     * @param targetSchema schema to merge components to type of
     * @return merged component object of desired schema/type
     */
    protected static Component mergeComponentToSchema(Component A, Component B, SerializerFactory.Schema targetSchema) {


        // New builder for the merged component
        SVIPComponentBuilder compBuilder = new SVIPComponentBuilder();

        // Type : If A Type isn't empty or null, Merged Component uses A, otherwise use
        if (A.getType() != null && !A.getType().isEmpty())
            compBuilder.setType(A.getType());
        else compBuilder.setType(B.getType());

        // UID : If A UID isn't empty or null, Merged Component uses A, otherwise use B
        if (A.getUID() != null && !A.getUID().isEmpty())
            compBuilder.setUID(A.getUID());
        else compBuilder.setUID(B.getUID());

        // Author : If A 'Author' isn't empty or null, Merged Component uses A, otherwise use B
        if (A.getAuthor() != null && !A.getAuthor().isEmpty())
            compBuilder.setAuthor(A.getAuthor());
        else compBuilder.setAuthor(B.getAuthor());

        // Name : If A 'Name' isn't empty or null, Merged Component uses A, otherwise use B
        if (A.getName() != null && !A.getName().isEmpty())
            compBuilder.setName(A.getName());
        else compBuilder.setName(B.getName());

        // Licenses : Merge Licenses of A and B together
        LicenseCollection mergedLicenses = new LicenseCollection();

        Set<String> concludedA = A.getLicenses().getConcluded();

        if (!concludedA.isEmpty()) {
            concludedA.forEach(
                    mergedLicenses::addConcludedLicenseString
            );
        }

        Utils.addLicenses(A, mergedLicenses);

        Set<String> concludedB = B.getLicenses().getConcluded();

        if (!concludedB.isEmpty()) {
            concludedB.forEach(
                    mergedLicenses::addConcludedLicenseString
            );
        }

        Utils.addLicenses(B, mergedLicenses);

        compBuilder.setLicenses(mergedLicenses);

        /*
            Target schema differences
         */

        // Hashes
        Map<String, String> hashesA = A.getHashes();
        Map<String, String> hashesB = B.getHashes();

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
                if (A.getCopyright() != null && !A.getCopyright().isEmpty())
                    copyright += "1) " + A.getCopyright();
                if (B.getCopyright() != null && !B.getCopyright().isEmpty() && !copyright.isEmpty())
                    copyright += "\n2) " + B.getCopyright();
                else if (B.getCopyright() != null && !B.getCopyright().isEmpty() && copyright.isEmpty())
                    copyright += "1) " + B.getCopyright();

                compBuilder.setCopyright(copyright);

                SPDX23PackageObject spdx23PackageObjectA = (SPDX23PackageObject) A;
                SPDX23PackageObject spdx23PackageObjectB = (SPDX23PackageObject) B;

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
                compBuilder.setSupplier(MergerUtils.mergeOrganization(spdx23PackageObjectA.getSupplier(), spdx23PackageObjectB.getSupplier()));

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
                MergerUtils.mergeExternalReferences(
                        spdx23PackageObjectA.getExternalReferences(), spdx23PackageObjectB.getExternalReferences()
                ).forEach(compBuilder::addExternalReference);

            }
            case CDX14 -> {

                CDX14ComponentObject componentA_CDX = (CDX14ComponentObject) A;
                CDX14ComponentObject componentB_CDX = (CDX14ComponentObject) B;

                compBuilder.setCopyright("1) " + componentA_CDX.getCopyright() + "\n2) " + componentB_CDX.getCopyright());

                // Supplier
                compBuilder.setSupplier(MergerUtils.mergeOrganization(componentA_CDX.getSupplier(), componentB_CDX.getSupplier()));

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
                MergerUtils.mergeExternalReferences(
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

                if (A instanceof SVIPComponentObject componentA_SVIP && // both components are of type
                        // SVIPComponentObject
                        B instanceof SVIPComponentObject componentB_SVIP) {

                    // Download Location
                    if (componentA_SVIP.getDownloadLocation() != null && !componentA_SVIP.getDownloadLocation().isEmpty())
                        compBuilder.setDownloadLocation(componentA_SVIP.getDownloadLocation());
                    else if (componentB_SVIP.getDownloadLocation() != null && !componentB_SVIP.getDownloadLocation().isEmpty())
                        compBuilder.setDownloadLocation(componentB_SVIP.getDownloadLocation());

                    // FileName
                    if (componentA_SVIP.getFileName() != null && !componentA_SVIP.getFileName().isEmpty())
                        compBuilder.setFileName(componentA_SVIP.getFileName());
                    else if (componentB_SVIP.getFileName() != null && !componentB_SVIP.getFileName().isEmpty())
                        compBuilder.setFileName(componentB_SVIP.getFileName());

                    // Files Analyzed
                    // TODO: determine if a FilesAnalzyed mistmatch should return true or false
                    if (componentA_SVIP.getFilesAnalyzed() != null)
                        compBuilder.setFilesAnalyzed(componentA_SVIP.getFilesAnalyzed());
                    else if (componentB_SVIP.getFilesAnalyzed() != null)
                        compBuilder.setFilesAnalyzed(componentB_SVIP.getFilesAnalyzed());

                    // Verification Code
                    String verificationCode = MergerUtils.configureComponentString
                            (componentA_SVIP.getVerificationCode(), componentB_SVIP.getVerificationCode());
                    compBuilder.setVerificationCode(verificationCode);

                    // Homepage
                    String homepage = MergerUtils.configureComponentString
                            (componentA_SVIP.getHomePage(), componentB_SVIP.getHomePage());
                    compBuilder.setHomePage(homepage);

                    // Source Info
                    String sourceInfo = MergerUtils.configureComponentString
                            (componentA_SVIP.getSourceInfo(), componentB_SVIP.getSourceInfo());
                    compBuilder.setSourceInfo(sourceInfo);

                    // Release Date
                    if (componentA_SVIP.getReleaseDate() != null && !componentA_SVIP.getReleaseDate().isEmpty())
                        compBuilder.setReleaseDate(componentA_SVIP.getReleaseDate());
                    else if (componentB_SVIP.getReleaseDate() != null && !componentB_SVIP.getReleaseDate().isEmpty())
                        compBuilder.setReleaseDate(componentB_SVIP.getReleaseDate());

                    // Built Date
                    if (componentA_SVIP.getBuiltDate() != null && !componentA_SVIP.getBuiltDate().isEmpty())
                        compBuilder.setBuildDate(componentA_SVIP.getBuiltDate());
                    else if (componentB_SVIP.getBuiltDate() != null && !componentB_SVIP.getBuiltDate().isEmpty())
                        compBuilder.setBuildDate(componentB_SVIP.getBuiltDate());

                    // Valid Until Date
                    if (componentA_SVIP.getValidUntilDate() != null && !componentA_SVIP.getValidUntilDate().isEmpty())
                        compBuilder.setValidUntilDate(componentA_SVIP.getValidUntilDate());
                    if (componentB_SVIP.getValidUntilDate() != null && !componentB_SVIP.getValidUntilDate().isEmpty())
                        compBuilder.setValidUntilDate(componentB_SVIP.getValidUntilDate());

                    // Supplier
                    if (componentA_SVIP.getSupplier() != null)
                        compBuilder.setSupplier(componentA_SVIP.getSupplier());
                    else if (componentB_SVIP.getSupplier() != null)
                        compBuilder.setSupplier(componentB_SVIP.getSupplier());

                    // Version // default to comp A version
                    if (componentA_SVIP.getVersion() != null && !componentA_SVIP.getVersion().isEmpty())
                        compBuilder.setVersion(componentA_SVIP.getVersion());
                    else if (componentB_SVIP.getVersion() != null && !componentB_SVIP.getVersion().isEmpty())
                        compBuilder.setVersion(componentB_SVIP.getVersion());

                    // CPEs
                    for (String cpeA : componentA_SVIP.getCPEs()) {
                        compBuilder.addCPE(cpeA);
                    }
                    for (String cpeB : componentB_SVIP.getCPEs()) {
                        compBuilder.addCPE(cpeB);
                    }
                    // PURLs
                    for (String purlA : componentA_SVIP.getPURLs()) {
                        compBuilder.addPURL(purlA);
                    }
                    // PURLs
                    for (String purlB : componentB_SVIP.getPURLs()) {
                        compBuilder.addPURL(purlB);
                    }

                    // comment
                    String comment = MergerUtils.configureComponentString
                            (componentA_SVIP.getComment(), componentB_SVIP.getComment());
                    compBuilder.setComment(comment);

                    // Attribution Text
                    if (componentA_SVIP.getAttributionText() != null && !componentA_SVIP.getAttributionText().isEmpty())
                        compBuilder.setAttributionText(componentA_SVIP.getAttributionText());
                    else if (componentB_SVIP.getAttributionText() != null && !componentB_SVIP.getAttributionText().isEmpty())
                        compBuilder.setAttributionText(componentB_SVIP.getAttributionText());

                    // External References
                    MergerUtils.mergeExternalReferences(
                            componentA_SVIP.getExternalReferences(), componentB_SVIP.getExternalReferences()
                    ).forEach(compBuilder::addExternalReference);

                    // Attribution Text
                    if (componentA_SVIP.getAttributionText() != null && !componentA_SVIP.getAttributionText().isEmpty())
                        compBuilder.setAttributionText(componentA_SVIP.getAttributionText());
                    else if (componentB_SVIP.getAttributionText() != null && !componentB_SVIP.getAttributionText().isEmpty())
                        compBuilder.setAttributionText(componentB_SVIP.getAttributionText());

                    // Description
                    if (componentA_SVIP.getDescription() != null)
                        compBuilder.setDescription(componentA_SVIP.getDescription());
                    else if (componentB_SVIP.getDescription() != null)
                        compBuilder.setDescription(componentB_SVIP.getDescription());

                    // Mime Type
                    if (componentA_SVIP.getMimeType() != null && !componentA_SVIP.getMimeType().isEmpty())
                        compBuilder.setMimeType(componentA_SVIP.getMimeType());
                    else if (componentB_SVIP.getMimeType() != null && !componentB_SVIP.getMimeType().isEmpty())
                        compBuilder.setMimeType(componentB_SVIP.getMimeType());

                    // Publisher
                    if (componentA_SVIP.getPublisher() != null && !componentA_SVIP.getPublisher().isEmpty())
                        compBuilder.setPublisher(componentA_SVIP.getPublisher());
                    else if (componentB_SVIP.getPublisher() != null && !componentB_SVIP.getPublisher().isEmpty())
                        compBuilder.setPublisher(componentB_SVIP.getPublisher());

                    //
                    if (componentA_SVIP.getScope() != null && !componentA_SVIP.getScope().isEmpty())
                        compBuilder.setScope(componentA_SVIP.getScope());
                    else if (componentB_SVIP.getScope() != null && !componentB_SVIP.getScope().isEmpty())
                        compBuilder.setScope(componentB_SVIP.getScope());

                    // Group
                    if (componentA_SVIP.getGroup() != null && !componentA_SVIP.getGroup().isEmpty())
                        compBuilder.setGroup(componentA_SVIP.getGroup());
                    else if (componentB_SVIP.getGroup() != null && !componentB_SVIP.getGroup().isEmpty())
                        compBuilder.setGroup(componentB_SVIP.getGroup());

                    // Properties
                    if (componentA_SVIP.getProperties() != null) componentA_SVIP.getProperties().keySet().forEach(
                            x -> componentA_SVIP.getProperties().get(x).forEach(y -> compBuilder.addProperty(x, y))
                    );
                    else if (componentB_SVIP.getProperties() != null) componentB_SVIP.getProperties().keySet().forEach(
                            x -> componentB_SVIP.getProperties().get(x).forEach(y -> compBuilder.addProperty(x, y))
                    );

                } else {
                    SPDX23PackageObject spdx23PackageObjectA = null;
                    SPDX23FileObject spdx23FileObject = null;

                    try {
                        spdx23PackageObjectA = (SPDX23PackageObject) A;
                    } catch (ClassCastException e) {
                        spdx23FileObject = (SPDX23FileObject) A; // leaving this in case at some point SPDX
                        // file objects can merge with CDXComponentObjects
                    }
                    CDX14ComponentObject componentB_CDX = (CDX14ComponentObject) B;

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
                        MergerUtils.mergeExternalReferences(
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
        }

        // Return the newly merged component
        return compBuilder.build();

    }

}
