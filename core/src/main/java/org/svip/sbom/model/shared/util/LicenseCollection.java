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

package org.svip.sbom.model.shared.util;

import org.svip.compare.conflicts.Comparable;
import org.svip.compare.conflicts.Conflict;
import org.svip.compare.conflicts.ConflictFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.svip.compare.conflicts.MismatchType.LICENSE_MISMATCH;

/**
 * File: LicensesCollection.java
 * Utility object tot hold license data
 *
 * @author Derek Garcia
 * @author Thomas Roman
 */
public class LicenseCollection implements Comparable {
    private final Set<String> declared = new HashSet<>();
    private final Set<String> infoFromFiles = new HashSet<>();
    private final Set<String> concluded = new HashSet<>();
    private String comment;

    /**
     * @param license License
     */
    public void addDeclaredLicense(String license) {
        this.declared.add(license);
    }

    /**
     * @param license License
     */
    public void addLicenseInfoFromFile(String license) {
        this.infoFromFiles.add(license);
    }

    /**
     * @param license License
     */
    public void addConcludedLicenseString(String license) {
        this.concluded.add(license);
    }

    /**
     * @param comment Comment
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    ///
    /// getters
    ///

    /**
     * @return declared licenses
     */
    public Set<String> getDeclared() {
        return declared;
    }

    /**
     * @return info from files licenses
     */
    public Set<String> getInfoFromFiles() {
        return infoFromFiles;
    }

    /**
     * @return Concluded licenses
     */
    public Set<String> getConcluded() {
        return concluded;
    }

    /**
     * @return License Comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * Compare to another Comparable Object
     *
     * @param o Object
     * @return List of Conflicts
     */
    @Override
    public List<Conflict> compare(Comparable o) {
        // Don't compare if not instance of same object
        if (!(o instanceof LicenseCollection other))
            return null;

        ConflictFactory cf = new ConflictFactory();
        cf.compareStringSets("License", LICENSE_MISMATCH, this.getConcluded(), other.getConcluded());
        cf.compareStringSets("License", LICENSE_MISMATCH, this.getDeclared(), other.getDeclared());
        cf.compareStringSets("License", LICENSE_MISMATCH, this.getInfoFromFiles(), other.getInfoFromFiles());
        return cf.getConflicts();
    }

    /**
     * Test if Object is equal to current
     *
     * @param o Object
     * @return List of Conflicts
     */
    @Override
    public boolean equals(Object o) {
        // LC is just collection of licenses, if a collection then it is equal
        return o != null && getClass() == o.getClass();
    }

    @Override
    public String toString() {
        String concludedString = "";
        String declaredString = "";
        String infoFromFilesString = "";
        concludedString = String.join(", ", this.concluded);
        declaredString = String.join(", ", this.declared);
        infoFromFilesString = String.join(", ", this.infoFromFiles);
        return "LicenseConcluded: (" + concludedString + "), LicenseDeclared: (" + declaredString + "), LicenseInfoFromFiles: (" + infoFromFilesString + ")";
    }
}

