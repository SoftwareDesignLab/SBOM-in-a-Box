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

package org.svip.conversion.manipulate;


import org.svip.sbom.model.objects.SVIPSBOM;
import org.svip.serializers.SerializerFactory;

/**
 * File: ManipulateController.java
 * Description: Main Controller/Driver class for Manipulation functionality.
 *
 * @author Tyler Drake
 */
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
