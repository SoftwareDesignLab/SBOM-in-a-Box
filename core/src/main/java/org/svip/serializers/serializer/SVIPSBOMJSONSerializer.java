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

package org.svip.serializers.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.svip.sbom.model.objects.SVIPSBOM;

/**
 * File: SVIPSBOMJSONSerializer.java
 * This class implements the Serializer interface and the Jackson StdSerializer to provide all functionality to write an
 * SVIP SBOM object to a custom file string.
 *
 * @author Ian Dunn
 */
public class SVIPSBOMJSONSerializer implements Serializer {

    private boolean prettyPrint = false;

    /**
     * Serializes an SBOM to a CDX 1.4 JSON file.
     *
     * @param sbom The SBOM to serialize.
     * @return A string containing the final SBOM file.
     */
    @Override
    public String writeToString(SVIPSBOM sbom) throws JsonProcessingException {
        if (prettyPrint)
            return getObjectMapper().writer().with(SerializationFeature.INDENT_OUTPUT).writeValueAsString(sbom);
        else
            return getObjectMapper().writer().writeValueAsString(sbom);
    }

    /**
     * Gets the ObjectMapper of the serializer to expose configuration.
     *
     * @return A reference to the ObjectMapper of the serializer.
     */
    @Override
    public ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // We don't need to register a specific serializer because we only need the raw fields/values
//        SimpleModule module = new SimpleModule();
//        module.addSerializer(SVIPSBOM.class, this);
//        mapper.registerModule(module);

        return mapper;
    }

    /**
     * Sets the ObjectMapper of the serializer to enable or disable pretty printing.
     *
     * @param prettyPrint True to pretty-print, false otherwise.
     */
    @Override
    public void setPrettyPrinting(boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
    }
}
