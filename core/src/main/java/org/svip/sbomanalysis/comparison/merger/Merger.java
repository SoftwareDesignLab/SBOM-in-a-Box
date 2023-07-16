package org.svip.sbomanalysis.comparison.merger;

import org.checkerframework.checker.units.qual.C;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.CycloneDX14.CDX14ComponentObject;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.objects.SVIPComponentObject;
import org.svip.sbom.model.shared.metadata.Contact;
import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbom.model.shared.metadata.CreationTool;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.shared.util.ExternalReference;
import org.svip.sbom.model.shared.util.LicenseCollection;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class Merger {

    SBOM A;

    SBOM B;

    public Merger() {

    }

    public abstract SBOM mergeSBOM(SBOM A, SBOM B);

    protected abstract Set<Component> mergeComponents(Set<Component> A, Set<Component> B);

    protected abstract Component mergeComponent(Component A, Component B);

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


    protected Set<CreationTool> mergeCreationTools(Set<CreationTool> toolsA, Set<CreationTool> toolsB) {

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

    protected Organization mergeOrganization(Organization organizationA, Organization organizationB) {

        // New organization
        Organization organizationNew;

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

        // return new Organization
        return organizationNew;

    }

    protected Set<Contact> mergeAuthors(Set<Contact> authorsA, Set<Contact> authorsB) {

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

    protected Set<ExternalReference> mergeExternalReferences(Set<ExternalReference> refA, Set<ExternalReference> refB) {

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

}
