package org.svip.conversion.toSVIP;

import org.svip.serializers.SerializerFactory;

/**
 * Name: ToSVIPController.java
 * Description: Main Controller/Driver class for ToSVIP functionality
 *
 * @author Tyler Drake
 */
public class ToSVIPController {

    /**
     * Gets a ToSVIP conversion class based on the original schema
     *
     * @param schema original schema of the SBOM
     * @return ToSVIP convert class
     */
    public static ToSVIP getToSVIP(SerializerFactory.Schema schema) {

        // Switch for the schema
        switch (schema) {

            // CDX 1.4 SBOM
            case CDX14 : return new CDX14();

            // SPDX 2.3 SBOM
            case SPDX23 : return new SPDX23();

            // Default
            default : return null;

        }

    }

}
