package org.svip.sbom.model.interfaces.schemas.CycloneDX14;

import org.svip.compare.conflicts.Conflict;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOMPackage;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * File: CDX14Package.java
 * <p>
 * CycloneDX 1.4 specific fields
 * <p>
 * Source: <a href="https://cyclonedx.org/docs/1.4/json/#components">https://cyclonedx.org/docs/1.4/json/#components</a>
 *
 * @author Derek Garcia
 */
public interface CDX14Package extends SBOMPackage, Component {
    /**
     * @return Mime type of package
     */
    String getMimeType();

    /**
     * @return publisher of package
     */
    String getPublisher();

    /**
     * @return scope of package
     */
    String getScope();

    /**
     * @return grouping name or identifier
     */
    String getGroup();

    /**
     * @return grouping name or identifier
     */
    HashMap<String, Set<String>> getProperties();

    /*
    todo
    pedigree
    evidence
    releaseNotes
    signature
     */

    /**
     * Compare against another CycloneDX 1.4 Package
     *
     * @param other Other CycloneDX 1.4 Package to compare against
     * @return List of conflicts
     */
    List<Conflict> compare(CDX14Package other);
}
