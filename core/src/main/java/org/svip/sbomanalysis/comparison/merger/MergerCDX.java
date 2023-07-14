package org.svip.sbomanalysis.comparison.merger;

import org.svip.builders.component.CDX14PackageBuilder;
import org.svip.sbom.builder.objects.schemas.CDX14.CDX14Builder;

import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;

import org.svip.sbom.model.objects.CycloneDX14.CDX14ComponentObject;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.objects.SVIPComponentObject;
import org.svip.sbom.model.shared.Relationship;
import org.svip.sbom.model.shared.metadata.Contact;
import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbom.model.shared.metadata.CreationTool;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.shared.util.ExternalReference;
import org.svip.sbom.model.shared.util.LicenseCollection;
import org.svip.sbom.model.uids.Hash;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

public class MergerCDX extends Merger {

    public MergerCDX(CDX14SBOM A, CDX14SBOM B) {
        super(A, B);
    }

    @Override
    protected SBOM mergeSBOM() {

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
        if(!A.getCreationData().equals(null) && !B.getCreationData().equals(null)) {
            builder.setCreationData(mergeCreationData(A.getCreationData(), B.getCreationData())); /** A new object should be created for this. Will do later.**/
        } else if (!A.getCreationData().equals(null)) {
            builder.setCreationData(A.getCreationData());
        } else if (!B.getCreationData().equals(null)) {
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

        // Return the newly built merged SBOM
        return builder.Build();

    }

    @Override
    protected Set<Component> mergeComponents(Set<Component> A, Set<Component> B) {
        for(Component componentA : A) {
            CDX14ComponentObject componentA_CDX = (CDX14ComponentObject) componentA;
            for(Component componentB : B) {
                CDX14ComponentObject componentB_CDX = (CDX14ComponentObject) componentB;
                if(componentA_CDX.getName() == componentB_CDX.getName() && componentA_CDX.getVersion() == componentB_CDX.getVersion()) {
                    mergeComponent(componentA, componentB);
                }
            }
        }
        return null;
    }

    @Override
    protected Component mergeComponent(Component A, Component B) {

        // New builder for the merged component
        CDX14PackageBuilder compBuilder = new CDX14PackageBuilder();

        CDX14ComponentObject componentA_CDX = (CDX14ComponentObject) A;
        CDX14ComponentObject componentB_CDX = (CDX14ComponentObject) B;

        // Type : If A Type isn't empty or null, Merged Component uses A, otherwise use
        if(!componentA_CDX.getType().isEmpty() && !componentA_CDX.getType().equals(null))
            compBuilder.setType(componentA_CDX.getType());
        else compBuilder.setType(componentB_CDX.getType());

        // UID : If A UID isn't empty or null, Merged Component uses A, otherwise use B
        if(!componentA_CDX.getUID().isEmpty() && !componentA_CDX.getUID().equals(null))
            compBuilder.setUID(componentA_CDX.getUID());
        else compBuilder.setUID(componentB_CDX.getUID());

        // Author : If A 'Author' isn't empty or null, Merged Component uses A, otherwise use B
        if(!componentA_CDX.getAuthor().isEmpty() && !componentA_CDX.getAuthor().equals(null))
            compBuilder.setAuthor(componentA_CDX.getAuthor());
        else compBuilder.setAuthor(componentB_CDX.getAuthor());

        // Name : If A 'Name' isn't empty or null, Merged Component uses A, otherwise use B
        if(!componentA_CDX.getName().isEmpty() && !componentA_CDX.getName().equals(null))
            compBuilder.setName(componentA_CDX.getName());
        else compBuilder.setName(componentB_CDX.getName());

        // Licenses : Merge Licenses of A and B together
        LicenseCollection mergedLicenses = new LicenseCollection();

        Set<String> concludedA = componentA_CDX.getLicenses().getConcluded();

        if(!concludedA.isEmpty() && !concludedA.equals(null)) {
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
        ).stream().forEach(x -> compBuilder.addExternalReference(x));

        // Mime Type
        compBuilder.setMimeType(componentA_CDX.getMimeType());

        // Publisher
        compBuilder.setPublisher(componentA_CDX.getPublisher());

        // Scope
        compBuilder.setScope(componentA_CDX.getScope());

        // Group
        compBuilder.setGroup(componentA_CDX.getGroup());



        // Build the merged component and return it
        return compBuilder.build();

    }

    @Override
    protected CreationData mergeCreationData(CreationData A, CreationData B) {

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
        for(String keyA : propertiesA.keySet()) {
            for (String entryA : propertiesA.get(keyA)) {
                mergedCreationData.addProperty(keyA, entryA);
            }
        }
        for(String keyB : propertiesB.keySet()) {
            for (String entryB : propertiesA.get(keyB)) {
                mergedCreationData.addProperty(keyB, entryB);
            }
        }

        // Creation Tools
        Set<CreationTool> mergedTools = mergeCreationTools(A.getCreationTools(), B.getCreationTools());
        for(CreationTool mergedTool : mergedTools) { mergedCreationData.addCreationTool(mergedTool); }

        // Document Comment
        mergedCreationData.setCreatorComment("1) " + A.getCreatorComment() + "\n2) " + B.getCreatorComment());

        // Return Creation Data
        return mergedCreationData;
    }

    private Organization mergeOrganization(Organization organizationA, Organization organizationB) {

        // New organization
        Organization organizationNew = null;

        // Based on the contents of organization A and organization B, attempt to merge them
        if(organizationA.getName().equals(organizationB.getName()) && organizationA.getUrl().equals(organizationB.getUrl())) {

            // Condition one: both have same name and url: merge their authors into one
            Set<Contact> manufactureAuthorsNew = mergeAuthors(organizationA.getContacts(), organizationB.getContacts());
            organizationNew = new Organization(organizationA.getName(), organizationA.getUrl());
            for(Contact manufacturer : manufactureAuthorsNew) { organizationNew.addContact(manufacturer); }
            return organizationNew;

        } else if(!organizationA.equals(null) && !organizationB.equals(null)) {

            // Condition two: both exist: default to Organization A
            return organizationA;

        } else if(!organizationA.equals(null) && organizationB.equals(null)) {

            // Condition three: Only A exists: merged Organization becomes Organization A
            return organizationA;

        } else if (!organizationB.equals(null) && organizationA.equals(null)) {

            // condition four: Only B exists: merged Organization becomes Organization B
            return organizationB;

        }

        // return new Organization
        return organizationNew;

    }

    private Set<Contact> mergeAuthors(Set<Contact> authorsA, Set<Contact> authorsB) {

        Set<Contact> authorsNew = new HashSet<>();

        // Cycle through each primary SBOM author
        for(Contact authorA : authorsA) {
            // Cycle through all authors from sbom B and compare them against current primary SBOM author
            for(Contact authorB : authorsB) {
                if(
                        authorA.getName().contains(authorB.getName()) &&
                                authorA.getEmail().contains(authorB.getEmail()) &&
                                authorA.getPhone().contains(authorB.getPhone())
                ) {
                    // If a duplicate is found remove it from second SBOM authors
                    authorsB.remove(authorB);
                }
            }
            // After duplicates have been removed, add the author from the primary SBOM in
            authorsNew.add(authorA);
        }
        // Add all remaining authors from second SBOM into new authors
        for(Contact authorB : authorsB) {
            authorsNew.add(authorB);
        }

        // Return the merged Authors
        return authorsNew;

    }


    private Set<CreationTool> mergeCreationTools(Set<CreationTool> toolsA, Set<CreationTool> toolsB) {

        // new creation tools set
        Set<CreationTool> mergedTools = new HashSet<>();

        for(CreationTool toolA : toolsA) {
            boolean merged = false;
            for(CreationTool toolB : toolsB) {
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
            if(!merged) mergedTools.add(toolA);
        }
        for(CreationTool toolB : toolsB) { if(!toolB.equals(null)) mergedTools.add(toolB); }

        // Returned the merged tools
        return mergedTools;

    }

    private Set<ExternalReference> mergeExternalReferences(Set<ExternalReference> refA, Set<ExternalReference> refB) {

        // New set for merged External References
        Set<ExternalReference> mergedExternalReferences = new HashSet<>();

        for(ExternalReference a : refA) {
            boolean merged = false;
            for (ExternalReference b : refB) {
                if (a.getType() == b.getType() && a.getUrl() == b.getUrl()) {
                    ExternalReference mergedExRef = new ExternalReference(a.getUrl(), a.getType());
                    if (!b.getHashes().isEmpty() && !b.getHashes().equals(null)) {
                        b.getHashes().keySet().forEach(x -> mergedExRef.addHash(x, b.getHashes().get(x)));
                    }
                    if (!a.getHashes().isEmpty() && !a.getHashes().equals(null)) {
                        a.getHashes().keySet().forEach(x -> mergedExRef.addHash(x, a.getHashes().get(x)));
                    }
                    merged = true;
                    refB.remove(b);
                    mergedExternalReferences.add(mergedExRef);
                }
            }
            if(!merged) mergedExternalReferences.add(a);
        }
        for(ExternalReference b : refB) {
            if (!b.equals(null)) {
                mergedExternalReferences.add(b);
            }
        }

        // Return the newly merged External References
        return mergedExternalReferences;

    }

}
