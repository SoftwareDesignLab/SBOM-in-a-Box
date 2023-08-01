package org.svip.sbom.model.shared.metadata;

import org.svip.sbomanalysis.comparison.conflicts.Comparable;
import org.svip.sbomanalysis.comparison.conflicts.Conflict;
import org.svip.sbomanalysis.comparison.conflicts.ConflictFactory;
import org.svip.sbomanalysis.comparison.conflicts.MismatchType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.svip.sbomanalysis.comparison.conflicts.MismatchType.MISC_MISMATCH;
import static org.svip.sbomanalysis.comparison.conflicts.MismatchType.NAME_MISMATCH;

/**
 * File: Organization
 *
 * Represents an Organization
 *
 * @author Derek Garcia
 */
public class Organization implements Comparable {
    private final String name;
    private final String url;
    private final Set<Contact> contacts = new HashSet<>();

    /**
     * Create new Organization
     *
     * @param name Name of the organization
     * @param url url of the organization
     */
    public Organization(String name, String url){
        this.name = name;
        this.url = url;
    }

    /**
     * Add a new contact at the organization
     *
     * @param contact Contact details
     */
    public void addContact(Contact contact){
        this.contacts.add(contact);
    }

    ///
    /// Getters
    ///

    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @return url
     *
     */
    public String getUrl() {
        return url;
    }

    /**
     * @return Contacts
     */
    public Set<Contact> getContacts() {
        return contacts;
    }

    ///
    /// Utils
    ///


    @Override
    public List<Conflict> compare(Comparable o) {
        // Don't compare if not instance of same object
        if(!(o instanceof Organization other))
            return null;

        ConflictFactory cf = new ConflictFactory();

        cf.addConflict("Organization: Name", NAME_MISMATCH, this.name, other.getName());
        cf.addConflict("Organization: URL", MISC_MISMATCH, this.url, other.getUrl());

        cf.compareComparableSets("Contacts", new HashSet<>(this.contacts), new HashSet<>(other.getContacts()));

        return cf.getConflicts();
    }

    /**
     * Compare based if any fields match since Organization can be the same just incomplete
     * Don't compare Contacts since the same organization can have different contacts
     *
     * @param o Other object
     * @return True if they share any fields
     */
    @Override
    public boolean equals(Object o) {
        // Test if correct class
        if (o == null || getClass() != o.getClass()) return false;
        Organization other = (Organization) o;

        // Check if name equivalent
        if(this.name.equals(other.getName()))
            return true;

        // no fields match if url doesn't match
        return this.url.equals(other.getUrl());
    }
}
