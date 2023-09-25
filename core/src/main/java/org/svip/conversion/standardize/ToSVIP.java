package org.svip.conversion.standardize;

import org.svip.sbom.builder.objects.SVIPSBOMBuilder;
import org.svip.sbom.model.interfaces.generics.SBOM;

/**
 * Name: CDX14.java
 * Description: Converts an SPDX 2.3 Internal SBOM Object
 * into an SVIP SBOM Object while retaining all the original
 * information from the SPDX 2.3 SBOM. This will not "completely"
 * convert the SBOM to an SVIP SBOM, as the fields will still
 * represent SPDX 2.3 values.
 *
 * @author Tyler Drake
 */
public class ToSVIP {

    public static SBOM convertToSVIP() {

        SVIPSBOMBuilder builder = new SVIPSBOMBuilder();

        return builder.Build();
    }

}
