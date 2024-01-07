/ **
* Copyright 2021 Rochester Institute of Technology (RIT). Developed with
* government support under contract 70RCSA22C00000008 awarded by the United
* States Department of Homeland Security for Cybersecurity and Infrastructure Security Agency.
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the “Software”), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
* /

package org.svip.merge;

import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.shared.metadata.Contact;
import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbom.model.shared.metadata.CreationTool;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.shared.util.ExternalReference;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Name: Merger.java
 * Description: Main class for merging SBOMs. Merges
 * top level objects from the internal SBOM Objects.
 *
 * @author Tyler Drake
 */
public abstract class Merger {

    /**
     * Basic constructor for a new Merger object
     */
    public Merger() {}

    /**
     * Merges two SBOMs together.
     *
     * @param A First SBOM
     * @param B Second SBOM
     * @return A single merged SBOM.
     * @throws Exception general exception
     */
    public abstract SBOM mergeSBOM(SBOM A, SBOM B) throws Exception;

    /**
     * Merges two Creation Data objects.
     *
     * @param A First CreationData object
     * @param B Second CreationData object
     * @return A single merged CreationData object
     */
    protected static CreationData mergeCreationData(CreationData A, CreationData B) {

        // Make a new CreationData object to merge the two objects into
        CreationData mergedCreationData = new CreationData();

        // Create a new timestamp and add it to the new SBOM, rather than using one of previous timestamps
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        mergedCreationData.setCreationTime(timeFormat.format(timestamp));

        // Merge the authors together into a Set, then add the authors to the new CreationData object
        Set<Contact> authorsNew = mergeAuthors(A.getAuthors(), B.getAuthors());
        for (Contact author : authorsNew) {
            mergedCreationData.addAuthor(author);
        }

        // Merge Manufacturers into one Manufacturer
        mergedCreationData.setManufacture(mergeOrganization(A.getManufacture(), B.getManufacture()));

        // Merge Suppliers into one Supplier
        mergedCreationData.setSupplier(mergeOrganization(A.getSupplier(), B.getSupplier()));

        // Merge Licenses into one Licenses
        Set<String> mergedLicenses = A.getLicenses();
        mergedLicenses.addAll(B.getLicenses());
        for (String license : mergedLicenses) {
            mergedCreationData.addLicense(license);
        }

        // Get the properties
        Map<String, Set<String>> propertiesA = A.getProperties();
        Map<String, Set<String>> propertiesB = B.getProperties();

        // Add properties A to the merged Properties
        for (String keyA : propertiesA.keySet())
            if (propertiesA.get(keyA) != null)
                for (String entryA : propertiesA.get(keyA))
                    mergedCreationData.addProperty(keyA, entryA);

        // Add properties B to the merged Properties
        for (String keyB : propertiesB.keySet())
            if (propertiesB.get(keyB) != null && propertiesA.get(keyB) != null)
                for (String entryB : propertiesA.get(keyB))
                    mergedCreationData.addProperty(keyB, entryB);


        // Merge CreationTools into one CreationTools
        Set<CreationTool> mergedTools = mergeCreationTools(A.getCreationTools(), B.getCreationTools());
        for (CreationTool mergedTool : mergedTools) {
            mergedCreationData.addCreationTool(mergedTool);
        }

        // Merge the creator comments into one
        mergedCreationData.setCreatorComment(mergeCreatorComments(A.getCreatorComment(), B.getCreatorComment()));

        // Return newly merged CreationData
        return mergedCreationData;
    }

    /**
     * Merges two Creator Comments together into one.
     *
     * @param commentA First Creator Comment
     * @param commentB Second Creator Comment
     * @return One merged Creator Comment
     */
    protected static String mergeCreatorComments(String commentA, String commentB) {

        // Create a new list of creator comments
        List<String> creatorComments = new ArrayList<>();

        // If creator comment A exists
        if (!(commentA == null || commentA.isEmpty())) {

            // And if it ends with a period, concatenate it
            if (commentA.endsWith(".")) {
                commentA = commentA.replace("\\.$", "");
            }

            // Add the creator comment A to the merged creator comment
            creatorComments.add(commentA);
        }

        // If creator comment B exists
        if (!(commentB == null || commentB.isEmpty())) {

            // And if it ends with a period, concatenate it
            if (commentB.endsWith(".")) {
                commentB = commentB.replace("\\.$", "");
            }

            // Add creator comment B to the merged creator comment
            creatorComments.add(commentB);
        }

        // Return the new creator comment
        return !creatorComments.isEmpty() ? String.join(". ", creatorComments) + "." : "";
    }

    /**
     * Merges two CreationTools together into one.
     *
     * @param toolsA First CreationTools
     * @param toolsB Second CreationTools
     * @return One merged CreationTools object
     */
    protected static Set<CreationTool> mergeCreationTools(Set<CreationTool> toolsA, Set<CreationTool> toolsB) {

        // new creation tools set
        Set<CreationTool> mergedTools = new HashSet<>();

        // If CreationTools A exists
        if (toolsA != null && !toolsA.isEmpty()) {

            // Loop through each tool in CreationTools A
            for (CreationTool toolA : toolsA) {

                // Merged status
                // Note: This isn't good practice, should find a different way to indicate merge status in the future
                boolean merged = false;

                // If CreationTools B exists
                if (toolsB != null && !toolsB.isEmpty()) {

                    // Loop through each tool in CreationTools B
                    for (CreationTool toolB : toolsB) {

                        // If the two tools are a match
                        if (toolA.getName() == toolB.getName() &&
                                toolA.getVendor() == toolB.getVendor() &&
                                toolA.getVersion() == toolB.getVersion()
                        ) {

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

                            // Add the newly merged tool to the merged tools list
                            mergedTools.add(newTool);

                            // Remove tool B from CreationTools B
                            toolsB.remove(toolB);

                            // Merged is true
                            merged = true;

                        }

                    }

                }

                // If there was no merge, add the tool from CreationTools A to the merged tools list
                if (!merged) mergedTools.add(toolA);

            }

        }

        // If CreationTools B exists
        if (toolsB != null && !toolsB.isEmpty()) {

            // Loop through each tool
            for (CreationTool toolB : toolsB) {

                // Add each remaining tool from CreationTools B if it isn't null
                if (toolB != null) mergedTools.add(toolB);

            }

        }

        // Returned the merged tools
        return mergedTools;

    }

    /**
     * Merges two Organization objects together.
     *
     * @param organizationA First Organization
     * @param organizationB Second Organization
     * @return One merged Organization object
     */
    protected static Organization mergeOrganization(Organization organizationA, Organization organizationB) {

        // New organization
        Organization organizationNew;

        try {
            // Based on the contents of organization A and organization B, attempt to merge them
            if (organizationA != null && organizationB == null) {

                // Only A exists: merged Organization becomes Organization A
                return organizationA;

            } else if (organizationB != null && organizationA == null) {

                // Only B exists: merged Organization becomes Organization B
                return organizationB;

            } else if (organizationA == null && organizationB == null) {

                // Neither A nor B exist: No Organization
                return null;

            } else if (organizationA.getName().equals(organizationB.getName()) && organizationA.getUrl().equals(organizationB.getUrl())) {

                // Both have same name and url: merge their authors into one
                Set<Contact> manufactureAuthorsNew = mergeAuthors(organizationA.getContacts(), organizationB.getContacts());

                // Create the new Organization
                organizationNew = new Organization(organizationA.getName(), organizationA.getUrl());

                // For each manufacturer, add it to the new Organizations contacts
                for (Contact manufacturer : manufactureAuthorsNew) {
                    organizationNew.addContact(manufacturer);
                }

            } else {

                // If all else fails, default to Organization A
                return organizationA;

            }

        } catch (NullPointerException e) {

            // If creating a new Organization fails, create a new blank Organization
            return new Organization("", "");

        }

        // return new Organization
        return organizationNew;

    }

    /**
     * Merges two Authors objects together.
     *
     * @param authorsA First Authors object
     * @param authorsB Second Authors object
     * @return One merged Authors object
     */
    protected static Set<Contact> mergeAuthors(Set<Contact> authorsA, Set<Contact> authorsB) {

        // Create new Authors
        Set<Contact> authorsNew = new HashSet<>();

        // If Authors A exists
        if (authorsA != null && !authorsA.isEmpty()) {

            // Loop through each author in Authors A
            for (Contact authorA : authorsA) {

                // If Authors B exists
                if (authorsB != null && !authorsB.isEmpty()) {

                    // Loop through each author in Authors B
                    for (Contact authorB : authorsB) {

                        // If there is a match
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
        if (authorsB != null && !authorsB.isEmpty()) {
            for (Contact authorB : authorsB) {
                authorsNew.add(authorB);
            }
        }

        // Return the merged Authors
        return authorsNew;

    }

    /**
     * Merges two ExternalReferences objects into one.
     *
     * @param refA First ExternalReferences
     * @param refB Second ExternalReferences
     * @return One merged ExternalReferences object
     */
    protected static Set<ExternalReference> mergeExternalReferences(Set<ExternalReference> refA, Set<ExternalReference> refB) {

        // New set for merged External References
        Set<ExternalReference> mergedExternalReferences = new HashSet<>();

        // If ExternalReferences A exists
        if (refA != null && !refA.isEmpty()) {

            // For each ExternalReference in ExternalReferences A
            for (ExternalReference a : refA) {

                // Merged status
                // Note: This isn't good practice, should find a different way to indicate merge status in the future
                boolean merged = false;

                // If ExternalReferences B exists
                if (refB != null && !refB.isEmpty()) {

                    // Loop through each ExternalReference in ExternalReferences B
                    for (ExternalReference b : refB) {

                        // If there is a match
                        if (a.getType() == b.getType() && a.getUrl() == b.getUrl()) {

                            // Create a new ExternalReference using ExternalReference A for top level data
                            ExternalReference mergedExRef = new ExternalReference(a.getUrl(), a.getType());

                            // If ExternalReference B has hashes, add them to the new merged ExternalReference
                            if (!b.getHashes().isEmpty() && b.getHashes() != null) {
                                b.getHashes().keySet().forEach(x -> mergedExRef.addHash(x, b.getHashes().get(x)));
                            }

                            // If ExternalReference A has hashes, add them to the new merged ExternalReference
                            if (!a.getHashes().isEmpty() && a.getHashes() != null) {
                                a.getHashes().keySet().forEach(x -> mergedExRef.addHash(x, a.getHashes().get(x)));
                            }

                            // Merged is true
                            merged = true;

                            // Remove ExternalReference B from ExternalReferences B
                            refB.remove(b);

                            // Add the newly merged ExternalReference to the merged ExternalReferences collection
                            mergedExternalReferences.add(mergedExRef);

                        }

                    }

                }

                // If there was no merge, add ExternalReference A to the merged ExternalReferences
                if (!merged) mergedExternalReferences.add(a);
            }

        }

        // If ExternalReferences B exists
        if (refB != null) {

            // Loop through each remaining ExternalReference in ExternalReferences B
            for (ExternalReference b : refB) {

                // If the ExternalReference isn't null, add it to the merged ExternalReferences list
                if (b != null) {
                    mergedExternalReferences.add(b);
                }

            }

        }

        // Return the newly merged External References
        return mergedExternalReferences;

    }

}
