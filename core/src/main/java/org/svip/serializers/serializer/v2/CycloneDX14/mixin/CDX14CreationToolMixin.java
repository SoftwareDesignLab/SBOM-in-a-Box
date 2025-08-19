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
package org.svip.serializers.serializer.v2.CycloneDX14.mixin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.svip.sbom.model.shared.util.ExternalReference;
import org.svip.serializers.serializer.v2.CycloneDX14.custom.CDX14HashesSerializer;

import java.util.Map;
import java.util.Set;

/**
 * <b>File:</b> CDX14CreationToolMixin.java
 * <p>
 * <b>Description:</b> Template for naming fields for jackson serialization
 *
 * @author Derek Garcia
 */
@JsonPropertyOrder({"vendor", "name", "version", "hashes", "externalReferences"})
public abstract class CDX14CreationToolMixin {
    @JsonProperty("vendor")
    abstract String getVendor();

    @JsonProperty("name")
    abstract String getName();

    @JsonProperty("version")
    abstract String getVersion();

    @JsonProperty("hashes")
    @JsonSerialize(using = CDX14HashesSerializer.class)
    abstract Map<String, String> getHashes();

    @JsonProperty("externalReferences")
    abstract Set<ExternalReference> getExternalReferences();
}
