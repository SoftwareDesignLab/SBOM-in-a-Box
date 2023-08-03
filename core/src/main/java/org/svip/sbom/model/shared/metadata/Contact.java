package org.svip.sbom.model.shared.metadata;

import org.svip.compare.conflicts.Comparable;
import org.svip.compare.conflicts.Conflict;
import org.svip.compare.conflicts.ConflictFactory;

import java.util.List;

import static org.svip.compare.conflicts.MismatchType.MISC_MISMATCH;

/**
 * File: Contact.java
 * <p>
 * Represents contact information
 *
 * @author Derek Garcia
 * @author Thomas Roman
 */
public class Contact implements Comparable {
    private final String name;
    private final String email;
    private final String phone;


    /**
     * Create new Contact
     *
     * @param name  Name of contact
     * @param email Email of contact
     * @param phone Phone number of contact
     */
    public Contact(String name, String email, String phone) {
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

    /**
     * Compare against other Contact
     *
     * @param o Other contact
     * @return list of conflicts
     */
    @Override
    public List<Conflict> compare(Comparable o) {
        // Don't compare if not instance of same object
        if (!(o instanceof Contact other))
            return null;

        ConflictFactory cf = new ConflictFactory();
        cf.addConflict("Contact Name", MISC_MISMATCH, this.name, other.getName());
        cf.addConflict("Contact Email", MISC_MISMATCH, this.email, other.getEmail());
        cf.addConflict("Contact Phone", MISC_MISMATCH, this.phone, other.getPhone());
        return cf.getConflicts();
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
        if (this.name.equals(other.getName()))
            return true;

        // Check if email equivalent
        if (this.email.equals(other.getEmail()))
            return true;

        // no fields match if phone doesn't match
        return this.phone.equals(other.getPhone());
    }

    @Override
    public String toString() {
        return "Organization: " + this.name + " (" + this.email + ")";
    }
}
