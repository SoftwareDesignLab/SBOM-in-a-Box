package org.svip.conversion.toSVIP;

import org.svip.sbom.builder.objects.SVIPComponentBuilder;
import org.svip.sbom.builder.objects.SVIPSBOMBuilder;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.CycloneDX14.CDX14ComponentObject;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.objects.SVIPComponentObject;
import org.svip.sbom.model.objects.SVIPSBOM;

/**
 * Name: SPDX2.3.java
 * Description: Converts an SPDX 2.3 Internal SBOM Object
 * into an SVIP SBOM Object while retaining all the original
 * information from the SPDX 2.3 SBOM. This will not "completely"
 * convert the SBOM to an SVIP SBOM, as the fields will still
 * represent SPDX 2.3 values.
 *
 * @author Tyler Drake
 */
public class SPDX23 {

    /**
     * Builds an SVIP SBOM referencing an SPDX 2.3 SBOM. This SVIP
     * SBOM will retain all the original values from the SPDX 2.3 sbom,
     * so only the internal Object will be converted, not the fields itself.
     *
     * @param sbom SPDX 2.3 SBOM Object
     * @return An SVIP SBOM containing all original SPDX 2.3 Values
     */
}
