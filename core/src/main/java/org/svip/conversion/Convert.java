package org.svip.conversion;

import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.SVIPSBOM;

public interface Convert {

    /**
     * Builds a new SBOM of a specific schema using data from
     * a passed in SVIPSBOM. The new SBOM should map in as much
     * data as possible.
     *
     * @param sbom The SVIPSBOM with the data that needs to be mapped.
     * @return The new SBOM in the requested schema with the mapped data.
     */
    SBOM convert(SVIPSBOM sbom);

}
