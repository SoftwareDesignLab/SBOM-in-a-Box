package org.svip.sbom.model.interfaces.generics;

import org.svip.sbom.model.shared.Relationship;
import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbom.model.shared.util.ExternalReference;
import org.svip.sbomanalysis.comparison.conflicts.Conflict;

import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * File: SBOM.java
 * Generic SBOM details that many SBOMs share
 *
 * @author Derek Garcia
 */
public interface SBOM {

    /**
     * @return Origin format of the SBOM
     */
    String getFormat();

    /**
     * @return Name of the SBOM
     */
    String getName();

    /**
     * @return Unique identifier of the SBOM
     */
    String getUID();

    /**
     * @return Version of the SBOM
     */
    String getVersion();

    /**
     * @return Specification Version of the SBOM
     */
    String getSpecVersion();

    /**
     * @return SBOM Licenses
     */
    Set<String> getLicenses();

    /**
     * @return Creation data about the SBOM
     */
    CreationData getCreationData();

    /**
     * @return Get SBOM comment
     */
    String getDocumentComment();

    /**
     * @return Get SBOM root component
     */
    Component getRootComponent();

    /**
     * @return Get SBOM components
     */
    Set<Component> getComponents();

    /**
     * @return Component relationship details
     */
    Map<String, Set<Relationship>> getRelationships();

    /**
     * @return External references from this SBOM
     */
    Set<ExternalReference> getExternalReferences();
    /**
     * @param other
     * @return component conflicts
     */
    List<Conflict> compare(SBOM other);
}
