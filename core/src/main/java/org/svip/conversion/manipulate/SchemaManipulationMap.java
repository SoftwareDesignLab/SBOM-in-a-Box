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

/**
 * Name: SchemaManipulationMap.java
 * Description: Holds default values for fields by format. These maps are typically
 * used by convert to determine the values of several fields during the conversion
 * process.
 *
 * @author Tyler Drake
 */
public enum SchemaManipulationMap {

    CDX14("CycloneDX", "1.4", "bom-ref:uuid:"),
    SPDX23("SPDX", "2.3", "SPDXRef-"),
    SVIP10("SVIP", "1.0-a", "SVIPComponent- ");

    /**
     * Schema's name
     */
    private final String schema;

    /**
     * The Version of the Schema
     */
    private final String version;

    /**
     * Component identifier type the Schema uses
     */
    private final String componentIDType;

    SchemaManipulationMap(String schema, String version, String componentIDType) {
        this.schema = schema;
        this.version = version;
        this.componentIDType = componentIDType;
    }

    public String getSchema() {
        return schema;
    }

    public String getVersion() {
        return version;
    }

    public String getComponentIDType() {
        return componentIDType;
    }

}
