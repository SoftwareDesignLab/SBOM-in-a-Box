package org.svip.sbom.model.shared.metadata;

import org.svip.sbomanalysis.comparison.conflicts.Conflict;

import java.util.List;
import java.util.Objects;

/**
 * File: Contact.java
 *
 * Represents contact information
 *
 * @author Derek Garcia
 */
public class Contact {
    private final String name;
    private final String email;
    private final String phone;


    /**
     * Create new Contact
     *
     * @param name Name of contact
     * @param email Email of contact
     * @param phone Phone number of contact
     */
    public Contact(String name, String email, String phone){
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    ///
    /// Getters
    ///

    /**
     * @return Name
     */
    public String getName() {
        return name;
    }

    /**
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @return phone
     */
    public String getPhone() {
        return phone;
    }

    ///
    /// Util
    ///
    public List<Conflict> compare(Contact other){
        return null;
    }

    /**
     * Compare based if any fields match since contact can be the same just incomplete
     *
     * @param o Other object
     * @return True if they share any fields
     */
    @Override
    public boolean equals(Object o) {
        // Test if correct class
        if (o == null || getClass() != o.getClass()) return false;
        Contact other = (Contact) o;

        // Check if name equivalent
        if(this.name.equals(other.getName()))
            return true;

        // Check if email equivalent
        if(this.email.equals(other.getEmail()))
            return true;

        // no fields match if phone doesn't match
        return this.phone.equals(other.getPhone());


    }
}
