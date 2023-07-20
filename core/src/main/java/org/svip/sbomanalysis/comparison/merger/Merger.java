package org.svip.sbomanalysis.comparison.merger;

import org.svip.sbom.builder.interfaces.generics.SBOMBuilder;
import org.svip.sbom.builder.objects.SVIPComponentBuilder;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;

import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23Package;
import org.svip.sbom.model.objects.CycloneDX14.CDX14ComponentObject;
import org.svip.sbom.model.objects.SPDX23.SPDX23PackageObject;
import org.svip.sbom.model.shared.metadata.Contact;
import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbom.model.shared.metadata.CreationTool;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.shared.util.ExternalReference;
import org.svip.sbom.model.shared.util.LicenseCollection;
import org.svip.sbomgeneration.serializers.SerializerFactory;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class Merger {

    public Merger() {

    }

    public abstract SBOM mergeSBOM(SBOM A, SBOM B);

    protected static CreationData mergeCreationData(CreationData A, CreationData B) {

        CreationData mergedCreationData = new CreationData();

        // Create a new timestamp and add it to the new SBOM, rather than using one of previous timestamps
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        mergedCreationData.setCreationTime(timeFormat.format(timestamp));

        // Authors
        Set<Contact> authorsNew = mergeAuthors(A.getAuthors(), B.getAuthors());
        for(Contact author : authorsNew) { mergedCreationData.addAuthor(author); }

        // Manufacturer
        mergedCreationData.setManufacture(mergeOrganization(A.getManufacture(), B.getManufacture()));

        // Supplier
        mergedCreationData.setSupplier(mergeOrganization(A.getSupplier(), B.getSupplier()));

        // Licenses
        Set<String> mergedLicenses = A.getLicenses();
        mergedLicenses.addAll(B.getLicenses());
        for(String license : mergedLicenses) {  mergedCreationData.addLicense(license); }

        // Properties
        Map<String, Set<String>> propertiesA = A.getProperties();
        Map<String, Set<String>> propertiesB = B.getProperties();
        for(String keyA : propertiesA.keySet())
            if(propertiesA.get(keyA) != null)
                for (String entryA : propertiesA.get(keyA))
                    mergedCreationData.addProperty(keyA, entryA);


        for(String keyB : propertiesB.keySet())
            if(propertiesB.get(keyB) != null && propertiesA.get(keyB) != null)
                for (String entryB : propertiesA.get(keyB))
                    mergedCreationData.addProperty(keyB, entryB);


        // Creation Tools
        Set<CreationTool> mergedTools = mergeCreationTools(A.getCreationTools(), B.getCreationTools());
        for(CreationTool mergedTool : mergedTools) { mergedCreationData.addCreationTool(mergedTool); }

        // Document Comment
        mergedCreationData.setCreatorComment("1) " + A.getCreatorComment() + "\n2) " + B.getCreatorComment());

        // Return Creation Data
        return mergedCreationData;
    }


    protected static Set<CreationTool> mergeCreationTools(Set<CreationTool> toolsA, Set<CreationTool> toolsB) {

        // new creation tools set
        Set<CreationTool> mergedTools = new HashSet<>();

        if(toolsA != null && !toolsA.isEmpty()) {
            for (CreationTool toolA : toolsA) {
                boolean merged = false;
                if(toolsB != null && !toolsB.isEmpty()) {
                    for (CreationTool toolB : toolsB) {
                        if (toolA.getName() == toolB.getName() && toolA.getVendor() == toolB.getVendor() && toolA.getVersion() == toolB.getVersion()) {

                            // New tool to hold merged CreationTool data
                            CreationTool newTool = new CreationTool();

                            // Set the name, vendor, and version
                            newTool.setName(toolA.getName());
                            newTool.setVendor(toolA.getVendor());
                            newTool.setVersion(toolA.getVersion());

                            // Merge A hashes
                            for (String hashA : toolA.getHashes().keySet()) {
                                newTool.addHash(hashA, toolA.getHashes().get(hashA));
                            }
                            // Merge B hashes
                            for (String hashB : toolB.getHashes().keySet()) {
                                // Make sure it doesn't already exist from the A hashes
                                if (!newTool.getHashes().containsKey(hashB)) {
                                    newTool.addHash(hashB, toolB.getHashes().get(hashB));
                                }
                            }

                            mergedTools.add(newTool);
                            toolsB.remove(toolB);
                            merged = true;

                        }
                    }
                }
                if (!merged) mergedTools.add(toolA);
            }
        }
        if(toolsB != null && !toolsB.isEmpty()) {
            for (CreationTool toolB : toolsB) {
                if (toolB != null) mergedTools.add(toolB);
            }
        }

        // Returned the merged tools
        return mergedTools;

    }

    protected static Organization mergeOrganization(Organization organizationA, Organization organizationB) {

        // New organization
        Organization organizationNew;

        try{
            // Based on the contents of organization A and organization B, attempt to merge them
            if(organizationA != null && organizationB == null) {

                // Condition three: Only A exists: merged Organization becomes Organization A
                return organizationA;

            } else if (organizationB != null && organizationA == null) {

                // condition four: Only B exists: merged Organization becomes Organization B
                return organizationB;

            } else if (organizationA == null && organizationB == null) {

                return null;

            } else if(organizationA.getName().equals(organizationB.getName()) && organizationA.getUrl().equals(organizationB.getUrl())) {

                // Condition one: both have same name and url: merge their authors into one
                Set<Contact> manufactureAuthorsNew = mergeAuthors(organizationA.getContacts(), organizationB.getContacts());
                organizationNew = new Organization(organizationA.getName(), organizationA.getUrl());
                for(Contact manufacturer : manufactureAuthorsNew) { organizationNew.addContact(manufacturer); }

            } else { return organizationA; }
        }catch (NullPointerException e){
            return new Organization("", "");
        }

        // return new Organization
        return organizationNew;

    }

    protected static Set<Contact> mergeAuthors(Set<Contact> authorsA, Set<Contact> authorsB) {

        Set<Contact> authorsNew = new HashSet<>();

        // Cycle through each primary SBOM author
        if(authorsA != null && !authorsA.isEmpty()) {
            for (Contact authorA : authorsA) {
                // Cycle through all authors from sbom B and compare them against current primary SBOM author
                if(authorsB != null && !authorsB.isEmpty()) {
                    for (Contact authorB : authorsB) {
                        if (
                                authorA.getName().contains(authorB.getName()) &&
                                        authorA.getEmail().contains(authorB.getEmail()) &&
                                        authorA.getPhone().contains(authorB.getPhone())
                        ) {
                            // If a duplicate is found remove it from second SBOM authors
                            authorsB.remove(authorB);
                        }
                    }
                }
                // After duplicates have been removed, add the author from the primary SBOM in
                authorsNew.add(authorA);
            }
        }
        // Add all remaining authors from second SBOM into new authors
        if(authorsB != null && !authorsB.isEmpty()) {
            for (Contact authorB : authorsB) {
                authorsNew.add(authorB);
            }
        }

        // Return the merged Authors
        return authorsNew;

    }

    protected static Set<ExternalReference> mergeExternalReferences(Set<ExternalReference> refA, Set<ExternalReference> refB) {

        // New set for merged External References
        Set<ExternalReference> mergedExternalReferences = new HashSet<>();

        if(refA != null && !refA.isEmpty()) {
            for (ExternalReference a : refA) {
                boolean merged = false;
                if(refB != null && !refB.isEmpty()) {
                    for (ExternalReference b : refB) {
                        if (a.getType() == b.getType() && a.getUrl() == b.getUrl()) {
                            ExternalReference mergedExRef = new ExternalReference(a.getUrl(), a.getType());
                            if (!b.getHashes().isEmpty() && b.getHashes() != null) {
                                b.getHashes().keySet().forEach(x -> mergedExRef.addHash(x, b.getHashes().get(x)));
                            }
                            if (!a.getHashes().isEmpty() && a.getHashes() != null) {
                                a.getHashes().keySet().forEach(x -> mergedExRef.addHash(x, a.getHashes().get(x)));
                            }
                            merged = true;
                            refB.remove(b);
                            mergedExternalReferences.add(mergedExRef);
                        }
                    }
                }
                if (!merged) mergedExternalReferences.add(a);
            }
        }
        if(refB != null) {
            for (ExternalReference b : refB) {
                if (b != null) {
                    mergedExternalReferences.add(b);
                }
            }
        }

        // Return the newly merged External References
        return mergedExternalReferences;

    }


    protected Set<Component> mergeComponentsToSchema(Set<Component> A, Set<Component> B, SerializerFactory.Schema targetSchema) {

        // todo or extract and move below code to Utils

        // New Components collection
        Set<Component> mergedComponents = new HashSet<>();

        Set<Component> removeB = new HashSet<>();

        // For every component in the first SBOM
        for(Component componentA : A) {

            // Checks to see if component A was merged with another component
            boolean merged = false;

            // For every component in the second SBOM
            for(Component componentB : B) {

                switch (targetSchema){ // todo make this neater
                    case SPDX23 -> {

                        // Cast the generic component from SBOM A back to a SPDX component
                        SPDX23PackageObject componentA_SPDX = (SPDX23PackageObject) componentA;
                        SPDX23Package componentB_SPDX = (SPDX23Package) componentB;

                        // If the components are the same by Name and Version, merge then add them to the SBOM
                        if(componentA_SPDX.getName() == componentB_SPDX.getName() && componentA_SPDX.getVersion() == componentB_SPDX.getVersion()) {

                            mergedComponents.add(mergeComponentToSchema(componentA, componentB, targetSchema));
                            removeB.add(componentB);
                            merged = true;

                        }
                    }
                    case CDX14 -> {

                        // Cast the generic component from SBOM A back to a CDX component
                        CDX14ComponentObject componentA_CDX = (CDX14ComponentObject) componentA;
                        CDX14ComponentObject componentB_CDX = (CDX14ComponentObject) componentB;

                        // If the components are the same by Name and Version, merge then add them to the SBOM
                        if(componentA_CDX.getName() == componentB_CDX.getName() && componentA_CDX.getVersion() == componentB_CDX.getVersion()) {

                            mergedComponents.add(mergeComponentToSchema(componentA, componentB, targetSchema));
                            removeB.add(componentB);
                            merged = true;

                        }

                    }
                    default -> { // SVIP
                        // Cast the generic component from SBOM A back to a SPDX component
                        SPDX23PackageObject componentA_SPDX = (SPDX23PackageObject) componentA;
                        CDX14ComponentObject componentB_CDX = (CDX14ComponentObject) componentB;

                        // If the components are the same by Name and Version, merge then add them to the SBOM
                        if(componentA_SPDX.getName() == componentB_CDX.getName() && componentA_SPDX.getVersion() == componentB_CDX.getVersion()) {

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
            if(!merged) mergedComponents.add(componentA);
        }

        // Merge remaining components from SBOM B that were not merged with any components from A
        for(Component componentB : B) { mergedComponents.add(componentB); }

        // Return the merged components set
        return mergedComponents;

    }


    protected SBOM mergeToSchema(SBOM A, SBOM B, Set<Component> componentsA, Set<Component> componentsB, SBOM mainSBOM,
                                 SBOMBuilder builder, SerializerFactory.Schema targetSchema) {

        /** Assign all top level data for the new SBOM **/

        // Format
        builder.setFormat(mainSBOM.getFormat());

        // Name
        builder.setName(mainSBOM.getName());

        // UID (In this case, bom-ref)
        builder.setUID(mainSBOM.getUID());

        // SBOM Version
        builder.setVersion(mainSBOM.getVersion());

        String specVersion;
        // Spec Version
        switch (targetSchema){
            case SPDX23 -> specVersion = "2.3";
            case CDX14 -> specVersion = "1.4";
            default -> specVersion = "1.0-a";
        }
        builder.setSpecVersion(specVersion);

        // Licenses
        if(mainSBOM.getLicenses() != null)
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
        Set<Component> mergedComponents = mergeComponentsToSchema(componentsA, componentsB, targetSchema);
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

    protected Component mergeComponentToSchema(Component A, Component B, SerializerFactory.Schema targetSchema) {


        // New builder for the merged component
        SVIPComponentBuilder compBuilder = new SVIPComponentBuilder();

        Component componentA = A;
        Component componentB = B;

        // Type : If A Type isn't empty or null, Merged Component uses A, otherwise use
        if(componentA.getType() != null && !componentA.getType().isEmpty())
            compBuilder.setType(componentA.getType());
        else compBuilder.setType(componentB.getType());

        // UID : If A UID isn't empty or null, Merged Component uses A, otherwise use B
        if(componentA.getUID() != null && !componentA.getUID().isEmpty())
            compBuilder.setUID(componentA.getUID());
        else compBuilder.setUID(componentB.getUID());

        // Author : If A 'Author' isn't empty or null, Merged Component uses A, otherwise use B
        if(componentA.getAuthor() != null && !componentA.getAuthor().isEmpty())
            compBuilder.setAuthor(componentA.getAuthor());
        else compBuilder.setAuthor(componentB.getAuthor());

        // Name : If A 'Name' isn't empty or null, Merged Component uses A, otherwise use B
        if(componentA.getName() != null && !componentA.getName().isEmpty())
            compBuilder.setName(componentA.getName());
        else compBuilder.setName(componentB.getName());

        // Licenses : Merge Licenses of A and B together
        LicenseCollection mergedLicenses = new LicenseCollection();

        Set<String> concludedA = componentA.getLicenses().getConcluded();

        if(!concludedA.isEmpty() && concludedA != null) {
            concludedA.stream().forEach(
                    x -> mergedLicenses.addConcludedLicenseString(x)
            );
        }

        Set<String> declaredA = componentA.getLicenses().getDeclared();

        if(!declaredA.isEmpty() && !declaredA.equals(null)) {
            declaredA.stream().forEach(
                    x -> mergedLicenses.addDeclaredLicense(x)
            );
        }

        Set<String> fileA = componentA.getLicenses().getInfoFromFiles();

        if(!fileA.isEmpty() && !fileA.equals(null)) {
            fileA.stream().forEach(
                    x -> mergedLicenses.addLicenseInfoFromFile(x)
            );
        }

        Set<String> concludedB = componentB.getLicenses().getConcluded();

        if(!concludedB.isEmpty() && !concludedB.equals(null)) {
            concludedB.stream().forEach(
                    x -> mergedLicenses.addConcludedLicenseString(x)
            );
        }

        Set<String> declaredB = componentB.getLicenses().getDeclared();

        if(!declaredB.isEmpty() && !declaredB.equals(null)) {
            declaredB.stream().forEach(
                    x -> mergedLicenses.addDeclaredLicense(x)
            );
        }

        Set<String> fileB = componentB.getLicenses().getInfoFromFiles();

        if(!fileB.isEmpty() && !fileB.equals(null)) {
            fileB.stream().forEach(
                    x -> mergedLicenses.addLicenseInfoFromFile(x)
            );
        }

        compBuilder.setLicenses(mergedLicenses);

        /*
            Target schema differences
         */

        // Hashes
        Map<String, String> hashesA = componentA.getHashes();
        Map<String, String> hashesB = componentB.getHashes();

        for(String keyB : hashesB.keySet()) { compBuilder.addHash(keyB, hashesB.get(keyB)); }
        for(String keyA : hashesA.keySet()) { compBuilder.addHash(keyA, hashesA.get(keyA)); }

        switch (targetSchema){
            case SPDX23 -> {
                // Copyright
                String copyright = "";
                if(componentA.getCopyright()!=null && !componentA.getCopyright().isEmpty())
                    copyright += "1) " + componentA.getCopyright();
                if(componentB.getCopyright()!=null && !componentB.getCopyright().isEmpty() && !copyright.isEmpty())
                    copyright += "\n2) " + componentB.getCopyright();
                else if(componentB.getCopyright()!=null && !componentB.getCopyright().isEmpty() && copyright.isEmpty())
                    copyright += "1) " + componentB.getCopyright();

                compBuilder.setCopyright(copyright);

                SPDX23PackageObject spdx23PackageObjectA = (SPDX23PackageObject) componentA;
                SPDX23PackageObject spdx23PackageObjectB = (SPDX23PackageObject) componentB;

                // Comment
                String comment = "";
                if(spdx23PackageObjectA.getComment()!=null && !spdx23PackageObjectA.getComment().isEmpty())
                    comment += "1) " + spdx23PackageObjectA.getComment();
                if(spdx23PackageObjectB.getComment()!=null && !spdx23PackageObjectB.getComment().isEmpty() && !comment.isEmpty())
                    comment += "\n2) " + spdx23PackageObjectB.getComment();
                else if(spdx23PackageObjectB.getComment()!=null && !spdx23PackageObjectB.getComment().isEmpty() && comment.isEmpty())
                    comment += "1) " + spdx23PackageObjectB.getComment();

                compBuilder.setComment(comment);

                // Attribution Text
                if(spdx23PackageObjectA.getAttributionText() != null && !spdx23PackageObjectA.getAttributionText().isEmpty())
                    compBuilder.setAttributionText(spdx23PackageObjectA.getAttributionText());
                else compBuilder.setAttributionText(spdx23PackageObjectB.getAttributionText());

                // Download Location
                if(spdx23PackageObjectA.getDownloadLocation() != null && !spdx23PackageObjectA.getDownloadLocation().isEmpty())
                    compBuilder.setDownloadLocation(spdx23PackageObjectA.getDownloadLocation());
                else compBuilder.setDownloadLocation(spdx23PackageObjectB.getDownloadLocation());

                // FileName
                if(spdx23PackageObjectA.getFileName() != null && !spdx23PackageObjectA.getFileName().isEmpty())
                    compBuilder.setFileName(spdx23PackageObjectA.getFileName());
                else compBuilder.setFileName(spdx23PackageObjectB.getFileName());

                // Files Analyzed
                // TODO: determine if a FilesAnalzyed mistmatch should return true or false
                if(spdx23PackageObjectA.getFilesAnalyzed() == true && spdx23PackageObjectA.getFilesAnalyzed() == true)
                    compBuilder.setFilesAnalyzed(true);
                else compBuilder.setFilesAnalyzed(false);

                // Verification Code
                String verificationCode = "";
                if(spdx23PackageObjectA.getVerificationCode()!=null && !spdx23PackageObjectA.getVerificationCode().isEmpty())
                    verificationCode += "1) " + spdx23PackageObjectA.getVerificationCode();
                if(spdx23PackageObjectB.getVerificationCode()!=null && !spdx23PackageObjectB.getVerificationCode().isEmpty() && !verificationCode.isEmpty())
                    verificationCode += "\n2) " + spdx23PackageObjectB.getVerificationCode();
                else if(spdx23PackageObjectB.getVerificationCode()!=null && !spdx23PackageObjectB.getVerificationCode().isEmpty() && verificationCode.isEmpty())
                    verificationCode += "1) " + spdx23PackageObjectB.getVerificationCode();

                compBuilder.setVerificationCode(verificationCode);

                // Homepage
                String homepage = "";
                if(spdx23PackageObjectA.getHomePage()!=null && !spdx23PackageObjectA.getHomePage().isEmpty())
                    homepage += "1) " + spdx23PackageObjectA.getHomePage();
                if(spdx23PackageObjectB.getHomePage()!=null && !spdx23PackageObjectB.getHomePage().isEmpty() && !homepage.isEmpty())
                    homepage += "\n2) " + spdx23PackageObjectB.getVerificationCode();
                else if(spdx23PackageObjectB.getHomePage()!=null && !spdx23PackageObjectB.getHomePage().isEmpty() && homepage.isEmpty())
                    homepage += "1) " + spdx23PackageObjectB.getVerificationCode();

                compBuilder.setHomePage(homepage);

                // Source Info
                String sourceInfo = "";
                if(spdx23PackageObjectA.getSourceInfo()!=null && !spdx23PackageObjectA.getSourceInfo().isEmpty())
                    sourceInfo += "1) " + spdx23PackageObjectA.getSourceInfo();
                if(spdx23PackageObjectB.getSourceInfo()!=null && !spdx23PackageObjectB.getSourceInfo().isEmpty() && !sourceInfo.isEmpty())
                    sourceInfo += "\n2) " + spdx23PackageObjectB.getSourceInfo();
                else if(spdx23PackageObjectB.getSourceInfo()!=null && !spdx23PackageObjectB.getSourceInfo().isEmpty() && sourceInfo.isEmpty())
                    sourceInfo += "1) " + spdx23PackageObjectB.getSourceInfo();

                compBuilder.setSourceInfo(sourceInfo);

                // Release Date
                if(spdx23PackageObjectA.getReleaseDate() != null && !spdx23PackageObjectA.getReleaseDate().isEmpty())
                    compBuilder.setReleaseDate(spdx23PackageObjectA.getReleaseDate());
                else compBuilder.setReleaseDate(spdx23PackageObjectB.getReleaseDate());

                // Built Date
                if(spdx23PackageObjectA.getBuiltDate() != null && !spdx23PackageObjectA.getBuiltDate().isEmpty())
                    compBuilder.setBuildDate(spdx23PackageObjectA.getBuiltDate());
                else compBuilder.setBuildDate(spdx23PackageObjectB.getBuiltDate());

                // Valid Until Date
                if(spdx23PackageObjectA.getValidUntilDate() != null && !spdx23PackageObjectA.getValidUntilDate().isEmpty())
                    compBuilder.setValidUntilDate(spdx23PackageObjectA.getValidUntilDate());
                else compBuilder.setValidUntilDate(spdx23PackageObjectB.getValidUntilDate());

                // Supplier
                compBuilder.setSupplier(mergeOrganization(spdx23PackageObjectA.getSupplier(), spdx23PackageObjectB.getSupplier()));

                // Version
                if(spdx23PackageObjectA.getVersion() != null && !spdx23PackageObjectA.getVersion().isEmpty())
                    compBuilder.setVersion(spdx23PackageObjectA.getVersion());
                else compBuilder.setVersion(spdx23PackageObjectB.getVersion());

                // CPEs
                for(String cpeA : spdx23PackageObjectA.getCPEs()) { compBuilder.addCPE(cpeA); }
                for(String cpeB : spdx23PackageObjectB.getCPEs()) { compBuilder.addCPE(cpeB); }

                // PURLs
                for(String purlA : spdx23PackageObjectA.getPURLs()) { compBuilder.addPURL(purlA); }
                for(String purlB : spdx23PackageObjectB.getPURLs()) { compBuilder.addPURL(purlB); }

                // External References
                mergeExternalReferences(
                        spdx23PackageObjectA.getExternalReferences(), spdx23PackageObjectB.getExternalReferences()
                ).forEach(x -> compBuilder.addExternalReference(x));

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

            }
            default -> { // SVIP

                SPDX23PackageObject spdx23PackageObjectA = (SPDX23PackageObject) componentA;
                CDX14ComponentObject componentB_CDX = (CDX14ComponentObject) componentB;


                /*
                    SPDX component A
                 */

                // Comment
                String comment = "";
                if(spdx23PackageObjectA.getComment()!=null && !spdx23PackageObjectA.getComment().isEmpty())
                    comment += "1) " + spdx23PackageObjectA.getComment();

                compBuilder.setComment(comment);

                // Attribution Text
                if(spdx23PackageObjectA.getAttributionText() != null && !spdx23PackageObjectA.getAttributionText().isEmpty())
                    compBuilder.setAttributionText(spdx23PackageObjectA.getAttributionText());

                // Download Location
                if(spdx23PackageObjectA.getDownloadLocation() != null && !spdx23PackageObjectA.getDownloadLocation().isEmpty())
                    compBuilder.setDownloadLocation(spdx23PackageObjectA.getDownloadLocation());

                // FileName
                if(spdx23PackageObjectA.getFileName() != null && !spdx23PackageObjectA.getFileName().isEmpty())
                    compBuilder.setFileName(spdx23PackageObjectA.getFileName());

                // Files Analyzed
                // TODO: determine if a FilesAnalzyed mistmatch should return true or false
                if(spdx23PackageObjectA.getFilesAnalyzed() == true && spdx23PackageObjectA.getFilesAnalyzed() == true)
                    compBuilder.setFilesAnalyzed(true);
                else compBuilder.setFilesAnalyzed(false);

                // Verification Code
                String verificationCode = "";
                if(spdx23PackageObjectA.getVerificationCode()!=null && !spdx23PackageObjectA.getVerificationCode().isEmpty())
                    verificationCode += "1) " + spdx23PackageObjectA.getVerificationCode();
                compBuilder.setVerificationCode(verificationCode);

                // Homepage
                String homepage = "";
                if(spdx23PackageObjectA.getHomePage()!=null && !spdx23PackageObjectA.getHomePage().isEmpty())
                    homepage += "1) " + spdx23PackageObjectA.getHomePage();
                compBuilder.setHomePage(homepage);

                // Source Info
                String sourceInfo = "";
                if(spdx23PackageObjectA.getSourceInfo()!=null && !spdx23PackageObjectA.getSourceInfo().isEmpty())
                    sourceInfo += "1) " + spdx23PackageObjectA.getSourceInfo();

                compBuilder.setSourceInfo(sourceInfo);

                // Release Date
                if(spdx23PackageObjectA.getReleaseDate() != null && !spdx23PackageObjectA.getReleaseDate().isEmpty())
                    compBuilder.setReleaseDate(spdx23PackageObjectA.getReleaseDate());

                // Built Date
                if(spdx23PackageObjectA.getBuiltDate() != null && !spdx23PackageObjectA.getBuiltDate().isEmpty())
                    compBuilder.setBuildDate(spdx23PackageObjectA.getBuiltDate());

                // Valid Until Date
                if(spdx23PackageObjectA.getValidUntilDate() != null && !spdx23PackageObjectA.getValidUntilDate().isEmpty())
                    compBuilder.setValidUntilDate(spdx23PackageObjectA.getValidUntilDate());

                // Supplier
                compBuilder.setSupplier(spdx23PackageObjectA.getSupplier());

                // Version // default to comp A version
                if(spdx23PackageObjectA.getVersion() != null && !spdx23PackageObjectA.getVersion().isEmpty())
                    compBuilder.setVersion(spdx23PackageObjectA.getVersion());

                // CPEs
                for(String cpeA : spdx23PackageObjectA.getCPEs()) { compBuilder.addCPE(cpeA); }

                // PURLs
                for(String purlA : spdx23PackageObjectA.getPURLs()) { compBuilder.addPURL(purlA); }

                /*
                    CDX specific
                 */

                // Description
                compBuilder.setDescription(componentB_CDX.getDescription());

                // CPEs
                for(String cpeB : componentB_CDX.getCPEs()) { compBuilder.addCPE(cpeB); }

                // PURLs
                for(String purlB : componentB_CDX.getPURLs()) { compBuilder.addPURL(purlB); }

                // Mime Type
                compBuilder.setMimeType(componentB_CDX.getMimeType());

                // Publisher
                compBuilder.setPublisher(componentB_CDX.getPublisher());

                // Scope
                compBuilder.setScope(componentB_CDX.getScope());

                // Group
                compBuilder.setGroup(componentB_CDX.getGroup());

                // Properties
                if(componentB_CDX.getProperties() != null) componentB_CDX.getProperties().keySet().stream().forEach(
                        x -> componentB_CDX.getProperties().get(x).stream().forEach(y -> compBuilder.addProperty(x, y))
                );

                /*
                    Both
                 */

                // External References
                mergeExternalReferences(
                        spdx23PackageObjectA.getExternalReferences(), componentB_CDX.getExternalReferences()
                ).forEach(x -> compBuilder.addExternalReference(x));

            }
        }

        // Return the newly merged component
        return compBuilder.build();

    }

}
