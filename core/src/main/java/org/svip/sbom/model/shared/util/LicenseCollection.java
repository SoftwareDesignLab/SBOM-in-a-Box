package org.svip.sbom.model.shared.util;

import org.svip.sbom.model.shared.metadata.CreationTool;
import org.svip.sbomanalysis.comparison.conflicts.Comparable;
import org.svip.sbomanalysis.comparison.conflicts.Conflict;
import org.svip.sbomanalysis.comparison.conflicts.ConflictFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.svip.sbomanalysis.comparison.conflicts.MismatchType.LICENSE_MISMATCH;

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
    public void addDeclaredLicense(String license){
        this.declared.add(license);
    }

    /**
     * @param license License
     */
    public void addLicenseInfoFromFile(String license){
        this.infoFromFiles.add(license);
    }

    /**
     * @param license License
     */
    public void addConcludedLicenseString(String license){
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
        if(!(o instanceof LicenseCollection other))
            return null;

        ConflictFactory cf = new ConflictFactory();
        cf.compareStringSets("License", LICENSE_MISMATCH, this.getConcluded(), other.getConcluded());
        cf.compareStringSets("License", LICENSE_MISMATCH, this.getDeclared(), other.getDeclared());
        cf.compareStringSets("License", LICENSE_MISMATCH, this.getInfoFromFiles(), other.getInfoFromFiles());
        return cf.getConflicts();
    }

    /**
     * Test if Object is equal to current
     * @param o Object
     * @return List of Conflicts
     */
    @Override
    public boolean equals(Object o){
        // LC is just collection of licenses, if a collection then it is equal
        return o != null && getClass() == o.getClass();
    }
}

