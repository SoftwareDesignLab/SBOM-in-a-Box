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

package org.svip.serializers.serializer.v2.CycloneDX14;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.shared.metadata.Contact;
import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbom.model.shared.metadata.CreationTool;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.shared.util.ExternalReference;
import org.svip.serializers.FileFormat;
import org.svip.serializers.Schema;
import org.svip.serializers.exceptions.UnsupportedFileFormatException;
import org.svip.serializers.serializer.v2.CycloneDX14.mixin.*;
import org.svip.serializers.serializer.v2.Serializer;

/**
 * <b>File:</b> CDX14Serializer.java
 * <p>
 * <b>Description:</b> Generic serializer that coverts CycloneDX SBOMS into files
 *
 * @author Derek Garcia
 */
public class CDX14Serializer extends Serializer {

    public CDX14Serializer(FileFormat fileFormat, Boolean prettyPrint) {
        super(fileFormat, prettyPrint);
        // todo - only support json for now
        if (fileFormat != FileFormat.JSON)
            throw new UnsupportedFileFormatException(Schema.CycloneDX_14, fileFormat);
        mapper = new ObjectMapper();
        // add mixin classes - handle schema specific naming
        mapper.addMixIn(Contact.class, CDX14ContactMixin.class);
        mapper.addMixIn(CreationData.class, CDX14CreationDataMixin.class);
        mapper.addMixIn(CreationTool.class, CDX14CreationToolMixin.class);
        mapper.addMixIn(ExternalReference.class, CDX14ExternalReferenceMixin.class);
        mapper.addMixIn(Organization.class, CDX14OrganizationMixin.class);
        // skip null and empty lists
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    }

    /**
     * Deserialize the file into an SBOM object
     *
     * @param sbom SBOM to write
     * @throws SerializerException Failed to write to string
     */
    @Override
    public String writeToString(SBOM sbom) throws SerializerException {
        try{
            // setup for output - todo better way to do this
            sbom.getCreationData().setRootComponent(sbom.getRootComponent());
            // attempt to serialize
            return prettyPrint
                    ? mapper.writerWithDefaultPrettyPrinter().writeValueAsString(sbom)
                    : mapper.writeValueAsString(sbom);
        } catch (JsonProcessingException e) {
            throw new SerializerException("Failed to serialize SBOM", sbom, fileFormat, e);
        }
    }

}
