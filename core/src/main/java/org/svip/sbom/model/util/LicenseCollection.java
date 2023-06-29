package org.svip.sbom.model.util;

import java.util.HashSet;
import java.util.Set;

/**
 * File: LicensesCollection.java
 * Utility object tot hold license data
 *
 * @author Derek Garcia
 */
public class LicenseCollection {
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
}

