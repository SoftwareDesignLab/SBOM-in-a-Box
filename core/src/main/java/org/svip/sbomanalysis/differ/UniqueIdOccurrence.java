package org.svip.sbomanalysis.differ;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * File: UniqueIdOccurrence.java
 * holds a CPE/PURL/SWID, and the IDs of every SBOM that identifier appears in.
 *
 * @author Juan Francisco Patino, Tyler Drake, Henry Orsagh
 */
public class UniqueIdOccurrence {

    /**
     * The PURL/CPE/SWID
     */
    private String uniqueIdentifier;

    /**
     * a set of SBOM IDs that this unique ID appears in
     */
    private Set<Integer> appearances;

    /**
     * the type of Unique ID
     */
    private UniqueIdentifierType uniqueIdType;

    public UniqueIdOccurrence (String uID, UniqueIdentifierType type) {
        this.uniqueIdentifier = uID;
        this.uniqueIdType = type;
        this.appearances = new HashSet<>();
    }

    // getters

    public String getUniqueId() {
        return this.uniqueIdentifier;
    }

    public UniqueIdentifierType getUniqueIdType() {
        return uniqueIdType;
    }

    public Set<Integer> getAppearances() {
        return this.appearances;
    }

    /**
     * adds an SBOM ID to the appearance Set.
     * @param a ID of the SBOM this unique ID appears in.
     */
    public void addAppearance(int a){
        appearances.add(a);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UniqueIdOccurrence that)) return false;
        return Objects.equals(uniqueIdentifier, that.uniqueIdentifier) && Objects.equals(getAppearances(), that.getAppearances()) && getUniqueIdType() == that.getUniqueIdType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(uniqueIdentifier, getAppearances(), getUniqueIdType());
    }
}
