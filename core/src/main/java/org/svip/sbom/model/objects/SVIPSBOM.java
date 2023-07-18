package org.svip.sbom.model.objects;

import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.interfaces.schemas.CycloneDX14.CDX14Schema;
import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23Schema;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbom.model.shared.Relationship;
import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbom.model.shared.util.ExternalReference;
import org.svip.sbomanalysis.comparison.conflicts.Conflict;
import org.svip.sbomanalysis.comparison.conflicts.ConflictFactory;

import java.util.Collections;
import java.util.*;

import static org.svip.sbomanalysis.comparison.conflicts.MismatchType.*;
import java.util.*;
import java.util.HashSet;

import static org.svip.sbomanalysis.comparison.conflicts.MismatchType.*;

/**
 * file: SVIPSBOM.java
 * Used to file for SVIP SBOM Generation
 *
 * @author Derek Garcia
 * @author Matthew Morrison
 */
public class SVIPSBOM implements CDX14Schema, SPDX23Schema{

    /**SBOM's format*/
    private final String format;

    /**SBOM's name*/
    private final String name;

    /**SBOM's uid*/
    private final String uid;

    /**SBOM's version*/
    private final String version;

    /**SBOM's spec version*/
    private final String specVersion;

    /**SBOM's licenses*/
    private final Set<String> licenses;

    /**SBOM's creation data*/
    private final CreationData creationData;

    /**SBOM's document comment*/
    private final String documentComment;

    /**SBOM's root component*/
    private final SVIPComponentObject rootComponent;

    /**SBOM's component's*/
    private final Set<Component> components;

    /**SBOM's relationships*/
    private final HashMap<String, Set<Relationship>> relationships;

    /**SBOM's external references*/
    private final Set<ExternalReference> externalReferences;

    //TODO VEX needs implementation
    // private Set<VEX> vulnerabilities;

    //TODO Service needs implementation
    // private Set<Service> services;

    //TODO Composition needs implementation
    // private Set<Composition> compositions;

    //TODO Signature needs implementation
    // private Signature signature;


    //TODO Snippet needs implementation
    // private Set<Snippet> snippets;

    //TODO LicenseInfo needs implementation
    // private Set<LicenseInfo> additionalLicenseInformation;

    //TODO Annotation needs implementation
    // private Set<Annotation> annotationInformation;

    /**SBOM's license list version*/
    private final String SPDXLicenseListVersion;

    /**
     * Get the SBOM's format
     * @return the SBOM's format
     */
    @Override
    public String getFormat() {
        return this.format;
    }

    /**
     * Get the SBOM's name
     * @return the SBOM's name
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Get the SBOM's UID
     * @return the SBOM's UID
     */
    @Override
    public String getUID() {
        return this.uid;
    }

    /**
     * Get the SBOM's version
     * @return the SBOM's version
     */
    @Override
    public String getVersion() {
        return this.version;
    }

    /**
     * Get the SBOM's spec version
     * @return the SBOM's spec version
     */
    @Override
    public String getSpecVersion() {
        return this.specVersion;
    }

    /**
     * Get the SBOM's licenses
     * @return the SBOM's licenses
     */
    @Override
    public Set<String> getLicenses() {
        return this.licenses;
    }

    /**
     * Get the SBOM's creation data
     * @return the SBOM's creation data
     */
    @Override
    public CreationData getCreationData() {
        return this.creationData;
    }

    /**
     * Get the SBOM's document comment
     * @return the SBOM's document comment
     */
    @Override
    public String getDocumentComment() {
        return this.documentComment;
    }

    /**
     * Get the SBOM's root component
     * @return the SBOM's root component
     */
    @Override
    public SVIPComponentObject getRootComponent() {
        return this.rootComponent;
    }

    /**
     * Get the SBOM's components
     * @return the SBOM's components
     */
    @Override
    public Set<Component> getComponents() {
        return this.components;
    }

    /**
     * Get the SBOM's component as a Set of SVIPComponentObject
     */
    public Set<SVIPComponentObject> getSVIPComponents() {
        return Collections.singleton((SVIPComponentObject) this.components);
    }

    /**
     * Get the SBOM's relationships
     * @return the SBOM's relationships
     */
    @Override
    public Map<String, Set<Relationship>> getRelationships() {
        return this.relationships;
    }

    /**
     * Get the SBOM's external references
     * @return the SBOM's external references
     */
    @Override
    public Set<ExternalReference> getExternalReferences() {
        return this.externalReferences;
    }

    /**
     * Get the SBOM's SPDX license list version
     * @return the SBOM's SPDX license list version
     */
    @Override
    public String getSPDXLicenseListVersion() {
        return this.SPDXLicenseListVersion;
    }

    /**
     * Constructor to make a new SVIP SBOM
     * @param format SBOM format
     * @param name SBOM name
     * @param uid SBOM uid
     * @param version SBOM version
     * @param specVersion SBOM spec version
     * @param licenses SBOM licenses
     * @param creationData SBOM creation data
     * @param documentComment SBOM document comment
     * @param rootComponent SBOM root component
     * @param components SBOM components
     * @param relationships SBOM relationships
     * @param externalReferences SBOM external references
     * @param spdxLicenseListVersion SBOM spdx license list version
     */
    //TODO add missing fields when implemented (VEX, Service, Composition, Signature, Snippet, LicenseInfo, Annotation)
    public SVIPSBOM(String format, String name, String uid, String version,
                      String specVersion, Set<String> licenses,
                      CreationData creationData, String documentComment,
                      SVIPComponentObject rootComponent, Set<Component> components,
                      HashMap<String, Set<Relationship>> relationships,
                      Set<ExternalReference> externalReferences,
                      String spdxLicenseListVersion){
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
     * Compare a SVIP SBOM against another SBOM Metadata
     *
     * @param other Other SBOM to compare against
     * @return List of Metadata of conflicts
     */
    @Override
    public List<Conflict> compare(SBOM other) {
        // SVIP - OTHER Comparison
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
        if(cf.comparable("Creation Data", this.creationData, other.getCreationData()))
            cf.addConflicts(this.creationData.compare(other.getCreationData()));

        // Comparable Sets
        if(cf.comparable("External Reference", this.externalReferences, other.getExternalReferences()))
            cf.compareComparableSets("External Reference", new HashSet<>(this.externalReferences), new HashSet<>(other.getExternalReferences()));
        // todo
        // compare relationships
        // compare Vulns

        return cf.getConflicts();
    }

    /**
     * Compare a SVIP SBOM against another SPDX 2.3 SBOM Metadata
     *
     * @param other other SPDX 2.3 SBOM
     * @return list of conflicts
     */
    @Override
    public List<Conflict> compare(SPDX23SBOM other) {
        // SVIP - SPDX Comparison

        ConflictFactory cf = new ConflictFactory();
        // Compare single String fields
        cf.addConflict("SPDX License List Version", LICENSE_MISMATCH, this.SPDXLicenseListVersion, other.getSPDXLicenseListVersion());

        // todo
        // snippets, additional license info, annotation info

        // Compare shared items
        cf.addConflicts(compare((SBOM) other));

        return cf.getConflicts();
    }


    /**
     * Compare a SVIP SBOM against another CycloneDX 1.4 SBOM Metadata
     *
     * @param other other CycloneDX 1.4 SBOM
     * @return list of conflicts
     */
    @Override
    public List<Conflict> compare(CDX14SBOM other) {
        // SVIP - CDX Comparison
        ConflictFactory cf = new ConflictFactory();

        // todo
        // Services
        // Compositions
        // Signature

        // Compare shared items
        cf.addConflicts(compare((SBOM) other));
        return cf.getConflicts();
    }
}
