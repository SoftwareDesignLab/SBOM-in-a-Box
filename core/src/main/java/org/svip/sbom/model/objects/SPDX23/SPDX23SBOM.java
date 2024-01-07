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

package org.svip.sbom.model.objects.SPDX23;

import org.svip.compare.conflicts.Conflict;
import org.svip.compare.conflicts.ConflictFactory;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23Schema;
import org.svip.sbom.model.shared.Relationship;
import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbom.model.shared.util.ExternalReference;

import java.util.*;

import static org.svip.compare.conflicts.MismatchType.*;

/**
 * file: SPDX23SBOM.java
 * Used to file for an SPDX 2.3 SBOM information
 *
 * @author Derek Garcia
 * @author Matthew Morrison
 */
public class SPDX23SBOM implements SPDX23Schema {

    /**
     * SBOM's format
     */
    private final String format;

    /**
     * SBOM's name
     */
    private final String name;

    /**
     * SBOM's uid
     */
    private final String uid;

    /**
     * SBOM's version
     */
    private final String version;

    /**
     * SBOM's spec version
     */
    private final String specVersion;

    /**
     * SBOM's licenses
     */
    private final Set<String> licenses;

    /**
     * SBOM's creation data
     */
    private final CreationData creationData;

    /**
     * SBOM's document comment
     */
    private final String documentComment;

    /**
     * SBOM's root component
     */
    private final SPDX23PackageObject rootComponent;

    /**
     * SBOM's component's
     */
    private final Set<Component> components;

    /**
     * SBOM's relationships
     */
    private final HashMap<String, Set<Relationship>> relationships;

    /**
     * SBOM's external references
     */
    private final Set<ExternalReference> externalReferences;

    // TODO VEX needs implementation
    // private Set<VEX> vulnerabilities;

    // TODO Snippet needs implementation
    // private Set<Snippet> snippets;

    // TODO LicenseInfo needs implementation
    // private Set<LicenseInfo> additionalLicenseInformation;

    // TODO Annotation needs implementation
    // private Set<Annotation> annotationInformation;

    /**
     * SBOM's license list version
     */
    private final String SPDXLicenseListVersion;

    /**
     * Get the SBOM's format
     *
     * @return the SBOM's format
     */
    @Override
    public String getFormat() {
        return this.format;
    }

    /**
     * Get the SBOM's name
     *
     * @return the SBOM's name
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Get the SBOM's UID
     *
     * @return the SBOM's UID
     */
    @Override
    public String getUID() {
        return this.uid;
    }

    /**
     * Get the SBOM's version
     *
     * @return the SBOM's version
     */
    @Override
    public String getVersion() {
        return this.version;
    }

    /**
     * Get the SBOM's spec version
     *
     * @return the SBOM's spec version
     */
    @Override
    public String getSpecVersion() {
        return this.specVersion;
    }

    /**
     * Get the SBOM's licenses
     *
     * @return the SBOM's licenses
     */
    @Override
    public Set<String> getLicenses() {
        return this.licenses;
    }

    /**
     * Get the SBOM's creation data
     *
     * @return the SBOM's creation data
     */
    @Override
    public CreationData getCreationData() {
        return this.creationData;
    }

    /**
     * Get the SBOM's document comment
     *
     * @return the SBOM's document comment
     */
    @Override
    public String getDocumentComment() {
        return this.documentComment;
    }

    /**
     * Get the SBOM's root component
     *
     * @return the SBOM's root component
     */
    @Override
    public SPDX23PackageObject getRootComponent() {
        return this.rootComponent;
    }

    /**
     * Get the SBOM's components
     *
     * @return the SBOM's components
     */
    @Override
    public Set<Component> getComponents() {
        return this.components;
    }

    /**
     * Get the SBOM's relationships
     *
     * @return the SBOM's relationships
     */
    @Override
    public Map<String, Set<Relationship>> getRelationships() {
        return this.relationships;
    }

    /**
     * Get the SBOM's external references
     *
     * @return the SBOM's external references
     */
    @Override
    public Set<ExternalReference> getExternalReferences() {
        return this.externalReferences;
    }

    /**
     * Get the SBOM's SPDX license list version
     *
     * @return the SBOM's SPDX license list version
     */
    @Override
    public String getSPDXLicenseListVersion() {
        return this.SPDXLicenseListVersion;
    }

    /**
     * Constructor to make a new SPDX 2.3 SBOM
     *
     * @param format                 SBOM format
     * @param name                   SBOM name
     * @param uid                    SBOM uid
     * @param version                SBOM version
     * @param specVersion            SBOM spec version
     * @param licenses               SBOM licenses
     * @param creationData           SBOM creation data
     * @param documentComment        SBOM document comment
     * @param rootComponent          SBOM root component
     * @param components             SBOM components
     * @param relationships          SBOM relationships
     * @param externalReferences     SBOM external references
     * @param spdxLicenseListVersion SBOM spdx license list version
     */
    //TODO add missing fields when implemented (VEX, Snippet, LicenseInfo, Annotation)
    public SPDX23SBOM(String format, String name, String uid, String version,
                      String specVersion, Set<String> licenses,
                      CreationData creationData, String documentComment,
                      SPDX23PackageObject rootComponent, Set<Component> components,
                      HashMap<String, Set<Relationship>> relationships,
                      Set<ExternalReference> externalReferences,
                      String spdxLicenseListVersion) {
        this.format = format;
        this.name = name;
        this.uid = uid;
        this.version = version;
        this.specVersion = specVersion;
        this.licenses = licenses;
        this.creationData = creationData;
        this.documentComment = documentComment;
        this.rootComponent = rootComponent;
        this.components = components;
        this.relationships = relationships;
        this.externalReferences = externalReferences;
        this.SPDXLicenseListVersion = spdxLicenseListVersion;

    }

    /**
     * Compare a SPDX 2.3 SBOM against another SBOM Metadata
     *
     * @param other Other SBOM to compare against
     * @return List of Metadata of conflicts
     */
    @Override
    public List<Conflict> compare(SBOM other) {
        // SPDX - OTHER Comparison
        ConflictFactory cf = new ConflictFactory();

        // Compare single String fields
        cf.addConflict("Format", ORIGIN_FORMAT_MISMATCH, this.format, other.getFormat());
        cf.addConflict("Name", NAME_MISMATCH, this.name, other.getName());
        cf.addConflict("UID", MISC_MISMATCH, this.uid, other.getUID());
        cf.addConflict("Version", VERSION_MISMATCH, this.version, other.getVersion());
        cf.addConflict("Spec Version", SCHEMA_VERSION_MISMATCH, this.specVersion, other.getSpecVersion());
        cf.addConflict("Document Comment", MISC_MISMATCH, this.documentComment, other.getDocumentComment());

        // Compare Licenses
        cf.compareStringSets("License", LICENSE_MISMATCH, this.licenses, other.getLicenses());

        // Compare Creation Data
        if (cf.comparable("Creation Data", this.creationData, other.getCreationData()))
            cf.addConflicts(this.creationData.compare(other.getCreationData()));

        // Comparable Sets
        if (cf.comparable("External Reference", this.externalReferences, other.getExternalReferences()))
            cf.compareComparableSets("External Reference", new HashSet<>(this.externalReferences), new HashSet<>(other.getExternalReferences()));

        // todo
        // compare relationships
        // compare Vulns

        // Compare SPDX specific fields
        if (other instanceof SPDX23SBOM)
            cf.addConflicts(compare((SPDX23SBOM) other));

        return cf.getConflicts();
    }

    /**
     * Compare a SPDX 2.3 SBOM against another SPDX 2.3 SBOM Metadata
     *
     * @param other other SPDX 2.3 SBOM
     * @return list of conflicts
     */
    @Override
    public List<Conflict> compare(SPDX23SBOM other) {
        // SPDX - SPDX Comparison

        ConflictFactory cf = new ConflictFactory();
        // Compare single String fields
        cf.addConflict("SPDX License List Version", LICENSE_MISMATCH, this.SPDXLicenseListVersion, other.getSPDXLicenseListVersion());

        // todo
        // snippets, additional license info, annotation info

        return cf.getConflicts();
    }
}
