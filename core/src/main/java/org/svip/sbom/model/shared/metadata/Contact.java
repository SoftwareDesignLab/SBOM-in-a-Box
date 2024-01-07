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
