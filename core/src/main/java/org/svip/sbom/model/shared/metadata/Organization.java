package org.svip.sbom.model.shared.metadata;

import java.util.HashSet;
import java.util.Set;

/**
 * File: Organization
 *
 * Represents an Organization
 *
 * @author Derek Garcia
 */
public class Organization {
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
