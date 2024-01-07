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

package org.svip.generation.serializers.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.svip.serializers.serializer.CDX14XMLSerializer;
import org.svip.utils.Debug;
import org.xml.sax.InputSource;
import javax.xml.parsers.SAXParserFactory;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Name: CDX14XMLSerializerTest
 * Description: Test class for CDX14XMLSerializer.java
 *
 * @author Tyler Drake
 */
public class CDX14XMLSerializerTest extends SerializerTest {

    public CDX14XMLSerializerTest() {
        super(new CDX14XMLSerializer());
    }

    @Test
    public void writeToStringTest() throws JsonProcessingException {
        Debug.logBlockTitle("CDX 1.4 XML");
        String serialized = getSerializer().writeToString(getTestSBOM());
        Debug.log(Debug.LOG_TYPE.DEBUG, "\n" + serialized);
        Debug.logBlock();
        Debug.log(Debug.LOG_TYPE.SUMMARY, "Successfully serialized SBOM.");

        // Check if the SBOM was serialized to XML. If we can parse it using SAXParserFactory, it worked.
        try {
            SAXParserFactory.newInstance().newSAXParser().getXMLReader().parse(new InputSource(new StringReader(serialized)));
        } catch (Exception e) {
            // Fail the test if the SaxParserFactory could not parse the XML
            fail("SBOM was not serialized to XML.");
        }

    }

}