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

public class Conversion {

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

    public static SBOM convert(SBOM sbom, SerializerFactory.Schema original, SerializerFactory.Schema desired) {

        if (!(sbom instanceof SVIPSBOM)) {
            ToSVIP toSVIP = ToSVIPController.getToSVIP(original);

            sbom = toSVIP.convertToSVIP(sbom);
        }

        SchemaManipulationMap manipulationMap = ManipulateController.getManipulationMap(desired);

        SVIPSBOM standardizedSBOM = ManipulateController.manipulateSBOM((SVIPSBOM) sbom, manipulationMap);

        return standardizedSBOM;

    }

    public static SBOM convertFull(SBOM sbom, SerializerFactory.Schema original, SerializerFactory.Schema desired) {

        if (!(sbom instanceof SVIPSBOM)) {
            ToSVIP toSVIP = ToSVIPController.getToSVIP(original);

            sbom = toSVIP.convertToSVIP(sbom);
        }

        SchemaManipulationMap manipulationMap = ManipulateController.getManipulationMap(desired);

        SVIPSBOM standardizedSBOM = ManipulateController.manipulateSBOM((SVIPSBOM) sbom, manipulationMap);

        ToSchema toSchema = ToSchemaController.getToSchema(desired);

        if(toSchema == null) return standardizedSBOM;

        SBOM fullConvertSBOM = toSchema.convert(standardizedSBOM);

        return fullConvertSBOM;

    }

}
