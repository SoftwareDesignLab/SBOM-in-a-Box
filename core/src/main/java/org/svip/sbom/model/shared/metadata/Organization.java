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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.svip.compare.conflicts.MismatchType.MISC_MISMATCH;
import static org.svip.compare.conflicts.MismatchType.NAME_MISMATCH;

/**
 * File: Organization
 * <p>
 * Represents an Organization
 *
 * @author Derek Garcia
 * @author Thomas Roman
 */
public class Organization implements Comparable {
    private final String name;
    private final String url;
    private final Set<Contact> contacts = new HashSet<>();

    /**
     * Create new Organization
     *
     * @param name Name of the organization
     * @param url  url of the organization
     */
    public Organization(String name, String url) {
        this.name = name;
        this.url = url;
    }

    /**
     * Add a new contact at the organization
     *
     * @param contact Contact details
     */
    public void addContact(Contact contact) {
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
        if (!(o instanceof Organization other))
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
        if (this.name.equals(other.getName()))
            return true;

        // no fields match if url doesn't match
        return this.url.equals(other.getUrl());
    }

    @Override
    public String toString() {
        return "Organization: " + this.name + " (" + this.url + ")";
    }
}
