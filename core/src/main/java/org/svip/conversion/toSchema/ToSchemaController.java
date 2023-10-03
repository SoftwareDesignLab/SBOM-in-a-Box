package org.svip.conversion.toSchema;

import org.svip.serializers.SerializerFactory;

public class ToSchemaController {

    /**
     * Gets a ToSchema conversion class based on the desired schema
     *
     * @param schema desired schema of the SBOM
     * @return ToSchema convert class
     */
    public static ToSchema getToSchema(SerializerFactory.Schema schema) {

        // Switch for the schema
        switch (schema) {

            // CDX 1.4 SBOM
            case CDX14 : return new ToCDX14();

            // SPDX 2.3 SBOM
            case SPDX23 : return new ToSPDX23();

            // Default
            default : return null;

        }

    }
}
