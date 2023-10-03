package org.svip.conversion.manipulate;


import org.svip.sbom.model.objects.SVIPSBOM;
import org.svip.serializers.SerializerFactory;

public class ManipulateController {

    /**
     * Gets a manipulation map based on the desired schema
     *
     * @param schema desired schema of the SBOM
     * @return ManipulationMap
     */
    public static SchemaManipulationMap getManipulationMap(SerializerFactory.Schema schema) {

        // Switch for the schema
        switch (schema) {

            // CDX 1.4 SBOM
            case CDX14 : return SchemaManipulationMap.CDX14;

            // SPDX 2.3 SBOM
            case SPDX23 : return SchemaManipulationMap.SPDX23;

            // SVIP 1.0 SBOM
            case SVIP: return SchemaManipulationMap.SVIP10;

            // Default
            default : return null;

        }

    }

    /**
     * Manipulates an SVIPSBOM's values to match a desired schema while retaining
     * the SVIPSBOM object.
     *
     * @param sbom  SVIPSBOM
     * @param manipulationMap Desired schema enum containing standard values
     * @return  Modified SVIPSBOM
     */
    public static SVIPSBOM manipulateSBOM(SVIPSBOM sbom, SchemaManipulationMap manipulationMap) {
        ManipulateSVIP manipulateSVIP = new ManipulateSVIP();
        return manipulateSVIP.modify(sbom, manipulationMap);
    }

}
