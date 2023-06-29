package org.svip.sbom.model.interfaces.schemas.CycloneDX14;

import org.svip.sbom.model.interfaces.generics.SBOMPackage;

/**
 * File: CDX14Package.java
 *  <p>
 * CycloneDX 1.4 specific fields
 * <p>
 * Source: <a href="https://cyclonedx.org/docs/1.4/json/#components">https://cyclonedx.org/docs/1.4/json/#components</a>
 *
 * @author Derek Garcia
 */
public interface CDX14Package extends SBOMPackage {
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
    /*
    todo
    pedigree
    evidence
    releaseNotes
    signature
     */
}
