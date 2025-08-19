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
import org.svip.sbom.model.objects.CycloneDX14.CDX14ComponentObject;
import org.svip.sbom.model.shared.metadata.Contact;
import org.svip.sbom.model.shared.metadata.CreationTool;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.serializers.serializer.v2.CycloneDX14.custom.CDX14LicensesSerializer;
import org.svip.serializers.serializer.v2.CycloneDX14.custom.CDX14PropertiesSerializer;

import java.util.Map;
import java.util.Set;

/**
 * <b>File:</b> CDX14CreationDataMixin.java
 * <p>
 * <b>Description:</b> Template for naming fields for jackson serialization
 *
 * @author Derek Garcia
 * @author Ian Dunn
 */
@JsonPropertyOrder({"timestamp", "tools", "authors", "component", "manufacture", "supplier", "properties", "licenses"})
// todo component
public abstract class CDX14CreationDataMixin {
    @JsonProperty("timestamp")
    abstract String getCreationTime();

    @JsonProperty("tools")
    abstract Set<CreationTool> getCreationTools();

    @JsonProperty("authors")
    abstract Set<Contact> getAuthors();

    @JsonProperty("manufacture")
    abstract Organization getManufacture();

    @JsonProperty("supplier")
    abstract Organization getSupplier();

    @JsonProperty("properties")
    @JsonSerialize(using = CDX14PropertiesSerializer.class)
    abstract Map<String, Set<String>> getProperties();

    @JsonProperty("licenses")
    @JsonSerialize(using = CDX14LicensesSerializer.class)
    abstract Set<String> getLicenses();

    @JsonProperty("component")
    abstract CDX14ComponentObject getRootComponent();

}
