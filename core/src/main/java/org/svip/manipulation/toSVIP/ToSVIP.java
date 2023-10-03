package org.svip.manipulation.toSVIP;

import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.SVIPSBOM;

public interface ToSVIP {

    /**
     * Builds an SVIP SBOM referencing an SBOM of a selected schema. This SVIP
     * SBOM will retain all the original values from the non-SVIP sbom,
     * so only the internal Object will be converted, not the fields itself.
     *
     * @param sbom The Internal SBOM Object
     * @return An SVIP SBOM containing all original SBOM Values
     */
    SVIPSBOM convertToSVIP(SBOM sbom);

}
