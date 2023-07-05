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
}
