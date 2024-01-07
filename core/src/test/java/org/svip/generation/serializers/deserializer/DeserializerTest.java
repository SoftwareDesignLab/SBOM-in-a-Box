/** Copyright 2021 Rochester Institute of Technology (RIT). Developed with
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
 */

package org.svip.generation.serializers.deserializer;

import org.svip.serializers.deserializer.Deserializer;

public abstract class DeserializerTest {

    protected static final String TEST_DATA_PATH = "/src/test/resources/serializers/";
    protected static final String CDX_14_JSON_SBOM = System.getProperty("user.dir") +
            TEST_DATA_PATH + "cdx_json/sbom.test.json";

    protected static final String CDX_14_XML_SBOM = System.getProperty("user.dir") +
            TEST_DATA_PATH + "cdx_xml/sbom.alpine.xml";

    protected static final String SPDX23_JSON_SBOM = System.getProperty("user.dir") +
            TEST_DATA_PATH + "spdx_json/sbom.test.json";

    protected static final String SPDX23_TAGVALUE_SBOM = System.getProperty("user.dir") +
            TEST_DATA_PATH + "spdx_tagvalue/sbom.test.spdx";

    // TODO in the future: no metadata, no components, empty sbom

    public abstract Deserializer getDeserializer();
}
