package org.svip.sbom.model.interfaces.generics;

import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.shared.util.Description;
import org.svip.sbom.model.shared.util.ExternalReference;

import java.util.Set;

/**
 * File: SBOMPackage.java
 * Generic SBOM Package details that many SBOM packages share
 *
 * @author Derek Garcia
 */
public interface SBOMPackage {
    /**
     * @return Supplier of the package
     */
    Organization getSupplier();

    /**
     * @return version of the package
     */
    String getVersion();

    /**
     * @return Description of the package
     */
    Description getDescription();

    /**
     * @return CPEs of the package
     */
    Set<String> getCPEs();

    /**
     * @return PURLs of the package
     */
    Set<String> getPURLs();

    /**
     * @return External References from the package
     */
    Set<ExternalReference> getExternalReferences();
}
