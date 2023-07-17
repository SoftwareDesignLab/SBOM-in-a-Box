package org.svip.sbom.model.objects.CycloneDX14;

import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.interfaces.schemas.CycloneDX14.CDX14Schema;
import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbom.model.shared.Relationship;
import org.svip.sbom.model.shared.util.ExternalReference;
import org.svip.sbomanalysis.comparison.conflicts.Conflict;
import org.svip.sbomanalysis.comparison.conflicts.ConflictFactory;

import java.util.*;

import static org.svip.sbomanalysis.comparison.conflicts.MismatchType.*;

/**
 * file: CDX14SBOM.java
 * Used to file for CycloneDX 1.4 SBOM information
 *
 * @author Derek Garcia
 * @author Matthew Morrison
 */
public class CDX14SBOM implements CDX14Schema {

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
    private final CDX14ComponentObject rootComponent;

    /**SBOM's component's*/
    private final Set<Component> components;

    /**SBOM's relationships*/
    private final HashMap<String, Set<Relationship>> relationships;

    /**SBOM's external references*/
    private final Set<ExternalReference> externalReferences;

    // TODO VEX needs implementation
    // private final Set<VEX> vulnerabilities;

    // TODO Service needs implementation
    // private final Set<Service> services;

    // TODO Composition needs implementation
    // private final Set<Composition> compositions;

    // TODO Signature needs implementation
    // private final Signature signature;

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
    public CDX14ComponentObject getRootComponent() {
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
     * Constructor to make a new CycloneDX 1.4 SBOM
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
     */
    //TODO add missing fields when implemented (VEX, Service, Composition, Signature)
    public CDX14SBOM(String format, String name, String uid, String version,
                     String specVersion, Set<String> licenses,
                     CreationData creationData, String documentComment,
                     CDX14ComponentObject rootComponent, Set<Component> components,
                     HashMap<String, Set<Relationship>> relationships,
                     Set<ExternalReference> externalReferences){
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

    }

    /**
     * Compare a CycloneDX 1.4 SBOM against another SBOM Metadata
     *
     * @param other Other SBOM to compare against
     * @return List of Metadata to compare against
     */
    @Override
    public List<Conflict> compare(SBOM other) {
        // CDX - OTHER Comparison
        ConflictFactory cf = new ConflictFactory();

        // Compare single String fields
        cf.addConflict("Format", ORIGIN_FORMAT_MISMATCH, this.format, other.getFormat());
        cf.addConflict("Name", NAME_MISMATCH, this.name, other.getName());
        cf.addConflict("UID", MISC_MISMATCH, this.uid, other.getUID());
        cf.addConflict("Version", VERSION_MISMATCH, this.version, other.getVersion());
        cf.addConflict("Spec Version", SCHEMA_VERSION_MISMATCH, this.specVersion, other.getSpecVersion());
        cf.addConflict("Document Comment", MISC_MISMATCH, this.documentComment, other.getDocumentComment());

        // Compare Licenses todo add util method in ConflictFactory?
        cf.compareSets("License", LICENSE_MISMATCH, this.licenses, other.getLicenses());

        // Compare Creation Data
        cf.addConflicts(this.creationData.compare(other.getCreationData()));


        // todo
        // compare relationships
        // compare Vulns
        // compare external refs

        return cf.getConflicts();
    }

    /**
     * Compare a CycloneDX 1.4 SBOM against another CycloneDX 1.4 SBOM Metadata
     *
     * @param other other CycloneDX 1.4 SBOM
     * @return list of conflict
     */
    @Override
    public List<Conflict> compare(CDX14SBOM other) {
//        // CDX - CDX Comparison
//        ArrayList<Conflict> conflicts = new ArrayList<>();
//        // NAME
//        if (this.name != null ^ other.getName() != null) {
//            conflicts.add(new MissingConflict("name", this.name, other.getName()));
//        } else if (!Objects.equals(this.name, other.getName()) && this.name != null) {
//            conflicts.add(new MismatchConflict("name", this.name, other.getName(), NAME_MISMATCH));
//        }
//        // VERSION
//        if (this.version != null ^ other.getVersion() != null) {
//            conflicts.add(new MissingConflict("version", this.version, other.getVersion()));
//        } else if (!Objects.equals(this.version, other.getVersion()) && this.version != null) {
//            conflicts.add(new MismatchConflict("version", this.version, other.getVersion(), VERSION_MISMATCH));
//        }
//        // SPECVERSION
//        if (this.specVersion != null ^ other.getSpecVersion() != null) {
//            conflicts.add(new MissingConflict("specVersion", this.specVersion, other.getSpecVersion()));
//        } else if (!Objects.equals(this.specVersion, other.getSpecVersion()) && this.specVersion != null) {
//            conflicts.add(new MismatchConflict("specVersion", this.specVersion, other.getSpecVersion(), SBOM_VERSION_MISMATCH));
//        }
//        // LICENSES
//        if (this.licenses != null && other.getLicenses() != null) {
//            if (!this.licenses.containsAll(other.getLicenses())) {
//                conflicts.add(new MismatchConflict("license", this.licenses.toString(), other.getLicenses().toString(), LICENSE_MISMATCH));
//            }
//        } else if (this.licenses != null) {
//            conflicts.add(new MissingConflict("license", this.licenses.toString(), null));
//        } else if (other.getLicenses() != null) {
//            conflicts.add(new MissingConflict("license", null, other.getLicenses().toString()));
//        }
//        // Creation data - includes timestamp, licenses
//        if (this.creationData != null && other.getCreationData() != null) {
//            // TIMESTAMP
//            if (this.creationData.getCreationTime() != null ^ other.getCreationData().getCreationTime() != null) {
//                conflicts.add(new MissingConflict("timestamp", this.creationData.getCreationTime(), other.getCreationData().getCreationTime()));
//            } else if (!Objects.equals(this.creationData.getCreationTime(), other.getCreationData().getCreationTime()) && this.creationData.getCreationTime() != null) {
//                conflicts.add(new MismatchConflict("timestamp", this.creationData.getCreationTime(), other.getCreationData().getCreationTime(), TIMESTAMP_MISMATCH));
//            }
//        }
//        return conflicts.stream().toList();
        return null;
    }
}
