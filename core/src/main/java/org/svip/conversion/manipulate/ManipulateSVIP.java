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

package org.svip.conversion.manipulate;

import org.svip.sbom.builder.objects.SVIPComponentBuilder;
import org.svip.sbom.builder.objects.SVIPSBOMBuilder;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.objects.SVIPComponentObject;
import org.svip.sbom.model.objects.SVIPSBOM;
import org.svip.sbom.model.shared.Relationship;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Name: ManipulateSVIP.java
 * Description: Creates a new SVIP SBOM and 'manipulates'
 * the fields based on the desired schema and format.
 *
 * @author Tyler Drake
 */
public class ManipulateSVIP {

    /**
     * Modifies an SVIPSBOM to match the fields of a requested schema.
     *
     * @param sbom  The SBOM to be modified
     * @param manipulationMap   Enumeration containing the values for the requested Schema
     * @return  An SBOM containing the modified data
     */
    public static SVIPSBOM modify(SVIPSBOM sbom, SchemaManipulationMap manipulationMap) {

        HashMap<String, String> relationshipMap = new HashMap<>();

        // Create new SVIP SBOM Builder
        SVIPSBOMBuilder builder = new SVIPSBOMBuilder();

        // Format
        builder.setFormat(manipulationMap.getSchema());

        // Name
        builder.setName(sbom.getName());

        // UID
        builder.setUID(sbom.getUID());

        // Version
        builder.setVersion(sbom.getVersion());

        // Spec Version
        builder.setSpecVersion(manipulationMap.getVersion());

        // Stream licenses into new SBOM
        if(sbom.getLicenses() != null)
            sbom.getLicenses().stream().forEach(x -> builder.addLicense(x));

        // Creation Data
        builder.setCreationData(sbom.getCreationData());

        // Document Comment
        builder.setDocumentComment(sbom.getDocumentComment());

        // Document Comment
        builder.setDocumentComment(sbom.getDocumentComment());

        // Root Component
        if(sbom.getRootComponent() != null)
            builder.setRootComponent(modifyComponent(sbom.getRootComponent(), manipulationMap, relationshipMap));
        else
            builder.setRootComponent(null);

        // Stream components from SVIP SBOM, convert them, then put into CDX SBOM
        if(sbom.getComponents() != null)
            sbom.getComponents().stream().filter(x -> x != null).forEach(
                    x -> builder.addComponent(modifyComponent(x, manipulationMap, relationshipMap))
            );

        // Stream Relationship data into new SBOM
        if(sbom.getRelationships() != null) {
            sbom.getRelationships().keySet().stream().filter(x -> x!=null).forEach(
                    x -> sbom.getRelationships().get(x).stream().forEach(
                            y -> builder.addRelationship(
                                    relationshipMap.get(x),
                                    new Relationship(
                                            relationshipMap.get(y.getOtherUID()),
                                            y.getRelationshipType()
                                    )
                            )
                    )
            );
        }

        // Stream External References into new SBOM
        if(sbom.getExternalReferences() != null) {
            sbom.getExternalReferences().stream().forEach(
                    x -> builder.addExternalReference(x)
            );
        }

        // Set SPDX License List Version
        builder.setSPDXLicenseListVersion(sbom.getSPDXLicenseListVersion());


        // Build the SBOM and return it
        return builder.Build();

    }

    /**
     * Modifies an SVIPComponentObject to match the fields of a requested schema.
     *
     * @param originalComponent The Original SVIP Component
     * @param manipulationMap   Enumeration containing the values for the requested Schema
     * @param relationshipMap   A map containing the old ID for each component along with the new one
     * @return  An SVIPComponentObject with the new values
     */
    public static SVIPComponentObject modifyComponent(
            Component originalComponent, SchemaManipulationMap manipulationMap, HashMap relationshipMap
    ) {

        // Cast component
        SVIPComponentObject component = (SVIPComponentObject) originalComponent;

        //
        SVIPComponentBuilder builder = new SVIPComponentBuilder();

        // Type
        builder.setType(component.getType());

        // UID
        String oldId = component.getUID();
        String newId = resolveID(component, manipulationMap);
        relationshipMap.put(oldId, newId);
        builder.setUID(newId);

        // Author
        builder.setAuthor(component.getAuthor());

        // Name
        builder.setName(component.getName());

        // Licenses
        builder.setLicenses(component.getLicenses());

        // Copyright
        builder.setCopyright(component.getCopyright());

        // Hashes
        Map<String, String> hashes = component.getHashes();
        if (hashes != null) hashes.keySet().forEach(x -> builder.addHash(x, hashes.get(x)));

        // Comment
        builder.setComment(component.getComment());

        // Attribution Text
        builder.setAttributionText(component.getAttributionText());

        // File Notice
        builder.setFileNotice(component.getFileNotice());

        // Download Location
        builder.setDownloadLocation(component.getDownloadLocation());

        // File Name
        builder.setFileName(component.getFileName());

        // Files Analyzed
        builder.setFilesAnalyzed(component.getFilesAnalyzed());

        // Verification Code
        builder.setVerificationCode(component.getVerificationCode());

        // Home Page
        builder.setHomePage(component.getHomePage());

        // Source Info
        builder.setSourceInfo(component.getSourceInfo());

        // Release Date
        builder.setReleaseDate(component.getReleaseDate());

        // Built Date
        builder.setBuildDate(component.getBuiltDate());

        // Valid Until Date
        builder.setValidUntilDate(component.getValidUntilDate());

        // Supplier
        builder.setSupplier(component.getSupplier());

        // Version
        builder.setVersion(component.getVersion());

        // Description
        builder.setDescription(component.getDescription());

        // CPEs
        if(component.getCPEs() != null) component.getCPEs().forEach(x -> builder.addCPE(x));

        // PURLs
        if(component.getPURLs() != null) component.getPURLs().forEach(x -> builder.addPURL(x));

        // External References
        if(component.getExternalReferences() != null)
            component.getExternalReferences().forEach(x -> builder.addExternalReference(x));

        // Mime Type
        builder.setMimeType(component.getMimeType());

        // Publisher
        builder.setPublisher(component.getPublisher());

        // Scope
        builder.setScope(component.getScope());

        // Group
        builder.setGroup(component.getGroup());

        // Properties
        if(component.getProperties() != null) {
            component.getProperties().keySet().forEach(
                    x -> component.getProperties().get(x).stream().forEach(
                            y -> builder.addProperty(x, y)
                    )
            );
        }

        // Build the component and return it
        return builder.buildAndFlush();

    }

    /**
     * Generates a new ID for a component based on the requested Schema
     *
     * @param component
     * @param manipulationMap
     * @return
     */
    public static String resolveID(SVIPComponentObject component, SchemaManipulationMap manipulationMap) {

        // Look at the type of Schema
        switch (manipulationMap) {

            // If it's SPDX 2.3, generate a new ID containing the SPDXID Identifier along with name and version of component
            case SPDX23 -> {
                return manipulationMap.getComponentIDType() + component.getName() + "-" + component.getVersion();
            }

            // Otherwise, generate a new ID containing the Schema Identifier along with a UUID
            default -> {
                return manipulationMap.getComponentIDType() + UUID.randomUUID();
            }

        }

    }

}
