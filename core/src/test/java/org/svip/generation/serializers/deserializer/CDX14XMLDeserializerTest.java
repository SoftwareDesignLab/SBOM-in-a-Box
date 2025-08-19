/**
 * Copyright 2021 Rochester Institute of Technology (RIT). Developed with
 * government support under contract 70RCSA22C00000008 awarded by the United
 * States Department of Homeland Security for Cybersecurity and Infrastructure Security Agency.
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the “Software”), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.svip.generation.serializers.deserializer;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.svip.sbom.model.objects.CycloneDX14.CDX14ComponentObject;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.shared.metadata.CreationTool;
import org.svip.serializers.deserializer.CDX14XMLDeserializer;
import org.svip.serializers.deserializer.Deserializer;
import org.svip.serializers.deserializer.v2.CDX14Deserializer;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.svip.serializers.FileFormat.XML;

public class CDX14XMLDeserializerTest extends DeserializerTest {
    private final CDX14SBOM cdx14xml;

    public CDX14XMLDeserializerTest() throws IOException {
        cdx14xml = new CDX14Deserializer(XML).deserialize(new File(CDX_14_XML_SBOM));
    }

    // TODO, make a new SBOM in the style of the other test SBOMs. Swap back the timestamp, metadata, and other tests I changed for use with the temp SBOM

    @Override
    public Deserializer getDeserializer() {
        return new CDX14XMLDeserializer();
    }

    @Disabled
    @Test
    public void formatTest() {
        assertEquals("CycloneDX", cdx14xml.getFormat());
    }


    @Disabled
    @Test
    public void specVersionTest() {
        // todo - embed in xml
        assertEquals("1.4", cdx14xml.getSpecVersion());
    }

    @Test
    public void uidTest() {
        assertEquals("urn:uuid:f057a217-e332-4981-94dc-799d6a776f58", cdx14xml.getUID());
    }

    @Test
    public void versionTest() {
        assertEquals("1", cdx14xml.getVersion());
    }

    @Test
    public void metadataTest() {
        assertEquals("2023-02-21T08:50:33-05:00", cdx14xml.getCreationData().getCreationTime());
    }

    @Test
    public void metadataToolTest() {
        CreationTool tool = cdx14xml.getCreationData().getCreationTools().stream().findFirst().get();
        assertEquals("syft", tool.getName());
        assertEquals("anchore", tool.getVendor());
        assertEquals("0.69.1", tool.getVersion());
    }

    @Test
    public void rootComponentTest() {
        CDX14ComponentObject root = cdx14xml.getRootComponent();
        assertEquals("alpine:latest", root.getName());
        assertEquals("5339058ca5e06f8a", root.getUID());
        assertEquals("container", root.getType());
    }

    @Test
    public void componentTest() {
        assertEquals(17, cdx14xml.getComponents().size());
    }
}
