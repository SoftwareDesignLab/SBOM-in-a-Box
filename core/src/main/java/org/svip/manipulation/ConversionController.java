package org.svip.manipulation;

import org.svip.manipulation.manipulate.ManipulateController;
import org.svip.manipulation.manipulate.SchemaManipulationMap;
import org.svip.manipulation.toSVIP.ToSVIP;
import org.svip.manipulation.toSVIP.ToSVIPController;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.SVIPSBOM;
import org.svip.serializers.SerializerFactory;

public class ConversionController {

    public static SVIPSBOM toSVIP(SVIPSBOM sbom, SerializerFactory.Schema originalSchema) {

        ToSVIP toSVIP = ToSVIPController.getToSVIP(originalSchema);

        SVIPSBOM svipsbom = toSVIP.convertToSVIP(sbom);

        return svipsbom;

    }

    public static SVIPSBOM manipulate(SVIPSBOM sbom, SerializerFactory.Schema desiredSchema) {

        SchemaManipulationMap manipulationMap = ManipulateController.getManipulationMap(desiredSchema);

        SVIPSBOM manipulatedSBOM = ManipulateController.manipulateSBOM(sbom, manipulationMap);

        return manipulatedSBOM;

    }

    public static SBOM toSchema() {

        SBOM sbom = null;

        return sbom;

    }

    public static SBOM convertToStandard() {

        SVIPSBOM sbom = null;

        return sbom;

    }

    public static SBOM convertFull() {

        SBOM sbom = null;

        return sbom;

    }

}
