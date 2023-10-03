package org.svip.conversion;

import org.svip.conversion.manipulate.ManipulateController;
import org.svip.conversion.manipulate.SchemaManipulationMap;
import org.svip.conversion.toSVIP.ToSVIP;
import org.svip.conversion.toSVIP.ToSVIPController;
import org.svip.conversion.toSchema.ToSchema;
import org.svip.conversion.toSchema.ToSchemaController;
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

    public static SBOM toSchema(SVIPSBOM sbom, SerializerFactory.Schema desiredSchema) {

        ToSchema toSchema = ToSchemaController.getToSchema(desiredSchema);

        SBOM convertedSBOM = toSchema.convert(sbom);

        return convertedSBOM;

    }

    public static SBOM convertToSVIPManipulate() {

        SVIPSBOM sbom = null;

        return sbom;

    }

    public static SBOM convertFull() {

        SBOM sbom = null;

        return sbom;

    }

}
