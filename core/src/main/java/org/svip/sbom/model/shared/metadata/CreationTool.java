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
import org.svip.sbom.model.shared.util.ExternalReference;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.svip.compare.conflicts.MismatchType.MISC_MISMATCH;

/**
 * File: CreationTool.java
 * Represent a tool used to create the SBOM Data
 *
 * @author Derek Garcia
 * @author Thomas Roman
 */
public class CreationTool implements Comparable {
    private String vendor;
    private String name;
    private String version;
    private final Map<String, String> hashes = new HashMap<>();
    private final Set<ExternalReference> externalReferences = new HashSet<>();

    //
    // Setters
    //

    /**
     * @param vendor vendor name
     */
    public void setVendor(String vendor) {
        this.vendor = vendor;
    }


    /**
     * @param name tool name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param version tool version
     */
    public void setVersion(String version) {
        this.version = version;
    }

    //
    // Adders
    //

    /**
     * @param algorithm Hash Algorithm
     * @param hash      Hash value
     */
    public void addHash(String algorithm, String hash) {
        this.hashes.put(algorithm, hash);
    }

    /**
     * @param externalReference A CreationTool's external reference
     */
    public void addExternalReference(ExternalReference externalReference) {
        this.externalReferences.add(externalReference);
    }

    ///
    /// Getters
    ///

    /**
     * @return Vendor name
     */
    public String getVendor() {
        return vendor;
    }

    /**
     * @return Tool name
     */
    public String getName() {
        return name;
    }

    /**
     * @return Tool Version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @return Hashes
     */
    public Map<String, String> getHashes() {
        return hashes;
    }

    /**
     * @return External References
     */
    public Set<ExternalReference> getExternalReferences() {
        return externalReferences;
    }


    @Override
    public List<Conflict> compare(Comparable o) {
        // Don't compare if not instance of same object
        if (!(o instanceof CreationTool other))
            return null;

        ConflictFactory cf = new ConflictFactory();
        cf.addConflict("Tool Vendor", MISC_MISMATCH, this.vendor, other.getVendor());
        cf.addConflict("Tool Name", MISC_MISMATCH, this.name, other.getName());
        cf.addConflict("Tool Version", MISC_MISMATCH, this.version, other.getVersion());
        cf.compareComparableSets("Tool External References", new HashSet<>(this.externalReferences), new HashSet<>(other.getExternalReferences()));
        cf.compareHashes("Tool Hash", this.hashes, other.getHashes());

        return cf.getConflicts();
    }

    @Override
    public boolean equals(Object o) {
        // Test if correct class
        if (o == null || getClass() != o.getClass()) return false;
        CreationTool other = (CreationTool) o;

        // todo more lax comparison like Contact?

        // Check if vendor equivalent
        if (this.vendor != null && !this.vendor.equals(other.getVendor()))
            return false;

        // Check if name equivalent
        if (this.name != null && !this.name.equals(other.getName()))
            return false;

        // Check if version equivalent
        if (this.version != null && !this.version.equals(other.getVersion()))
            return false;

        // Compare hashes
        for (String alg : this.hashes.keySet()) {
            // Missing hash, missing doesn't imply different
            if (!other.getHashes().containsKey(alg))
                continue;
            // Same hash alg, different values
            if (!other.getHashes().get(alg).equals(this.hashes.get(alg)))
                return false;
        }

        // Check if external references are equivalent
        if (this.externalReferences != null && !this.externalReferences.equals(other.getExternalReferences()))
            return false;

        // All checks pass
        return true;
    }

    @Override
    public String toString() {
        return "Tool: " + this.name + "-" + this.version;
    }
}
