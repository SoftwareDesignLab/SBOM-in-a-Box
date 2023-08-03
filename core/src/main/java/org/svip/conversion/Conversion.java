package org.svip.conversion;

import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.SVIPSBOM;
import org.svip.serializers.SerializerFactory;

/**
 * Name: Conversion.java
 * Description: Converts an SBOM from one schema to another.
 *
 * @author tyler_drake
 */
public class Conversion {

    /**
     * Gets the appropriate converter based on the desired schema requested
     *
     * @param desiredSchema
     * @return (A Convert object)
     */
    private static Convert getConvert(SerializerFactory.Schema desiredSchema) {

        // Return appropriate Converter depending on the desired schema
        // If it is SVIPSBOM or not found, return null
        switch (desiredSchema) {
            case SPDX23 -> {
                return new ConvertSPDX23();
            }
            case CDX14 -> {
                return new ConvertCDX14();
            }
            default -> { return null; }
        }
    }

    /**
     * A simple function for standardizing the sent SBOM as a SVIPSBOM.
     * Any other handling that may be needed shall be done here.
     *
     * @param sbom
     * @return SVIPSBOM
     */
    private static SVIPSBOM toSVIP(SBOM sbom) throws Exception {

        try {
            return (SVIPSBOM) sbom;
        } catch (Exception e) {
            throw new Exception("Couldn't standardize SBOM to SVIPSBOM: " + e.getMessage());
        }

    }

    /**
     * Main driver for directing SBOM conversion.
     *
     * @param sbom
     * @param desiredSchema
     * @return
     */
    public static SBOM convertSBOM(SBOM sbom, SerializerFactory.Schema desiredSchema) throws Exception {

        // Get the converter
        Convert converter = getConvert(desiredSchema);

        // Standardize SBOM to an SVIPSBOM
        SVIPSBOM svipsbom = toSVIP(sbom);

        // If no converter was found, return the SBOM as an SVIPSBOM
        return converter == null ? svipsbom : converter.convert(svipsbom);

    }

}
