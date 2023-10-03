package org.svip.manipulation;

import org.svip.manipulation.manipulate.ManipulateSVIP;
import org.svip.manipulation.manipulate.SchemaManipulationMap;
import org.svip.manipulation.toSVIP.ToSVIP;
import org.svip.manipulation.toSVIP.ToSVIPController;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.SVIPSBOM;
import org.svip.serializers.SerializerFactory;

public class ManipulationController {

    public static SVIPSBOM toSVIP(SVIPSBOM sbom, SerializerFactory.Schema originalSchema) {

        ToSVIP toSVIP = ToSVIPController.getToSVIP(originalSchema);

        SVIPSBOM manipulatedSBOM = toSVIP.convertToSVIP(sbom);

        return manipulatedSBOM;

    }

    public static SVIPSBOM manipulate() {

        SVIPSBOM sbom = null;

        return sbom;

    }

    public static SBOM toSchema() {

        SBOM sbom = null;

        return sbom;

    }

    public static SBOM convertStandard() {

        SVIPSBOM sbom = null;

        return sbom;

    }

    public static SBOM fullConvert() {

        SBOM sbom = null;

        return sbom;

    }

}
