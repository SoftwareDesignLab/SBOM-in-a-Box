/ **
* Copyright 2021 Rochester Institute of Technology (RIT). Developed with
* government support under contract 70RCSA22C00000008 awarded by the United
* States Department of Homeland Security for Cybersecurity and Infrastructure Security Agency.
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the “Software”), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
* /

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

/**
 * Name: Conversion.java
 * Description: Main controller for conversion functionality. Encompasses several
 * pieces of the conversion functionality including ToSVIP SBOM conversion, ToSchema
 * SBOM conversion, and SVIPSBOM data manipulation.
 *
 * @author Tyler Drake
 */
public class Conversion {

    /**
     * Converts an internal SBOM object that is not an SVIPSBOM to an SVIPSBOM
     *
     * @param sbom The original SBOM object.
     * @param originalSchema The original schema of that SBOM object.
     * @return An SVIPSBOM Object containing all values of the original SBOM object.
     */
    public static SVIPSBOM toSVIP(SBOM sbom, SerializerFactory.Schema originalSchema) {

        ToSVIP toSVIP = ToSVIPController.getToSVIP(originalSchema);

        SVIPSBOM svipsbom = toSVIP.convertToSVIP(sbom);

        return svipsbom;

    }

    /**
     * Manipulates the data of an SVIPSBOM to match that of another schema. The SBOM will stay
     * as an internal SVIP SBOM, just with different data.
     *
     * @param sbom The SVIPSBOM object
     * @param desiredSchema The schema of the desired data to put into the SVIPSBOM
     * @return The SVIPSBOM object with the manipulated data
     */
    public static SVIPSBOM manipulate(SVIPSBOM sbom, SerializerFactory.Schema desiredSchema) {

        SchemaManipulationMap manipulationMap = ManipulateController.getManipulationMap(desiredSchema);

        SVIPSBOM manipulatedSBOM = ManipulateController.manipulateSBOM(sbom, manipulationMap);

        return manipulatedSBOM;

    }

    /**
     * Converts an internal SVIPSBOM object to an internal SBOM of a desired schema.
     *
     * @param sbom The SVIPSBOM object.
     * @param desiredSchema The desired SBOM object schema.
     * @return The SBOM object now in the desired schema.
     */
    public static SBOM toSchema(SVIPSBOM sbom, SerializerFactory.Schema desiredSchema) {

        ToSchema toSchema = ToSchemaController.getToSchema(desiredSchema);

        SBOM convertedSBOM = toSchema.convert(sbom);

        return convertedSBOM;

    }

    /**
     * Standardizes an internal SBOM object to an SVIPSBOM, then manipulates the data to match that
     * of the desired schema.
     *
     * Non-SVIPSBOM object -> SVIPSBOM with same data -> SVIPSBOM with manipulated data
     *
     * @param sbom The original SBOM object
     * @param original The original schema
     * @param desired The desired schema
     * @return An SVIPSBOM containing the data of the desired schema
     */
    public static SBOM convert(SBOM sbom, SerializerFactory.Schema original, SerializerFactory.Schema desired) {

        if (!(sbom instanceof SVIPSBOM)) {
            ToSVIP toSVIP = ToSVIPController.getToSVIP(original);

            sbom = toSVIP.convertToSVIP(sbom);
        }

        SchemaManipulationMap manipulationMap = ManipulateController.getManipulationMap(desired);

        SVIPSBOM standardizedSBOM = ManipulateController.manipulateSBOM((SVIPSBOM) sbom, manipulationMap);

        return standardizedSBOM;

    }

    /**
     * Standardizes an internal SBOM object to an SVIPSBOM, then manipulates the data to match that
     * of the desired schema. Then, converts the internal SVIPSBOM object to an internal SBOM object
     * of the desired schema.
     *
     * @param sbom The original SBOM object
     * @param original The original schema
     * @param desired The desired schema
     * @return An internal SBOM object of the desired schema containing the converted data.
     */
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
