/*
 * Copyright 2021 Rochester Institute of Technology (RIT). Developed with
 *  government support under contract 70RCSA22C00000008 awarded by the United
 * States Department of Homeland Security for Cybersecurity and Infrastructure Security Agency.
 *  <p>
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the “Software”), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *  <p>
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *  <p>
 *  THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package org.svip.serializers.serializer.v2.CycloneDX14.custom;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.svip.sbom.model.shared.Relationship;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import static org.svip.utils.NullOrEmpty.isNullOrEmpty;

/**
 * <b>File:</b> CDX14RelationshipsSerializer.java
 * <p>
 * <b>Description:</b> Custom serializer to write Relationships correctly
 *
 * @author Derek Garcia
 */
public class CDX14RelationshipsSerializer extends JsonSerializer<Map<String, Set<Relationship>>> {

    @Override
    public void serialize(Map<String, Set<Relationship>> relationships, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartArray();

        for (Map.Entry<String, Set<Relationship>> dep : relationships.entrySet()) {
            jsonGenerator.writeStartObject();

            jsonGenerator.writeStringField("ref", dep.getKey());

            jsonGenerator.writeFieldName("dependsOn");
            jsonGenerator.writeStartArray();
            for (Relationship rel : dep.getValue())
                jsonGenerator.writeString(rel.getOtherUID());
            jsonGenerator.writeEndArray();
            jsonGenerator.writeEndObject();
        }

        jsonGenerator.writeEndArray();
    }

    @Override
    public boolean isEmpty(SerializerProvider provider, Map<String, Set<Relationship>> value) {
        return isNullOrEmpty(value);
    }
}
