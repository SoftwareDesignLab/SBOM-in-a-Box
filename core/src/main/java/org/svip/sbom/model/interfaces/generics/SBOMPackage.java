package org.svip.sbom.model.interfaces.generics;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.shared.util.Description;
import org.svip.sbom.model.shared.util.ExternalReference;
import org.svip.sbomanalysis.comparison.conflicts.Conflict;

import java.util.List;
import java.util.Set;

/**
 * File: SBOMPackage.java
 * Generic SBOM Package details that many SBOM packages share
 *
 * @author Derek Garcia
 */
public interface SBOMPackage extends Component{
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

    /**
     * Compare against another generic SBOM Package
     *
     * @param other Other SBOM Package to compare against
     * @return List of conflicts
     */
    List<Conflict> compare(SBOMPackage other) throws JsonProcessingException;
}
