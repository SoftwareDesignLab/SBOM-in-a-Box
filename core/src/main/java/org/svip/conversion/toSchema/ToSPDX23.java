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

package org.svip.conversion.toSchema;

import org.svip.sbom.builder.objects.schemas.SPDX23.SPDX23Builder;
import org.svip.sbom.builder.objects.schemas.SPDX23.SPDX23PackageBuilder;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.objects.SPDX23.SPDX23PackageObject;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.sbom.model.objects.SVIPComponentObject;
import org.svip.sbom.model.objects.SVIPSBOM;

import java.util.Map;
import java.util.Optional;

/**
 * Name: ToSPDX23.java
 * Description: Builds a SPDX 2.3 SBOM using
 * the information from an SVIPSBOM.
 *
 * @author Tyler Drake
 */
public class ToSPDX23 implements ToSchema {

    /**
     * Converts an SVIPSBOM into an SPDX23SBOM Object.
     *
     * @param sbom The SVIPSBOM with the data that needs to be mapped.
     * @return An SPDX23SBOM Object with the relevant data from the SVIPSBOM.
     */
    @Override
    public SPDX23SBOM convert(SVIPSBOM sbom) {

        // Create a new builder
        SPDX23Builder builder = new SPDX23Builder();

        // Format
        builder.setFormat("SPDX");

        // Name
        builder.setName(sbom.getName());

        // UID
        builder.setUID(sbom.getUID());

        // Version
        builder.setVersion(sbom.getVersion());

        // Spec Version
        builder.setSpecVersion("2.3");

        // Stream Licenses into new SBOM
        if (sbom.getLicenses() != null) sbom.getLicenses().stream().forEach(x -> builder.addLicense(x));

        // Creation Data
        builder.setCreationData(sbom.getCreationData());

        // Document Comment
        builder.setDocumentComment(sbom.getDocumentComment());

        // Root Component
        Optional.ofNullable(sbom.getRootComponent()).map(b -> builder.setRootComponent(convertComponent(b))).orElse(null);

        // Stream components from SVIP SBOM, convert them, then put into SPDX SBOM
        sbom.getComponents().stream().filter(x -> x != null).forEach(x -> builder.addComponent(convertComponent(x)));

        // Stream Relationship data into new SBOM
        if (sbom.getRelationships() != null) sbom.getRelationships().keySet().forEach(
                x -> sbom.getRelationships().get(x).stream().forEach(
                        y -> builder.addRelationship(x, y)
                )
        );

        // Stream External References into new SBOM
        if (sbom.getExternalReferences() != null) sbom.getExternalReferences().stream().forEach(
                x -> builder.addExternalReference(x)
        );

        // SPDX License List
        builder.setSPDXLicenseListVersion(sbom.getSPDXLicenseListVersion());

        // Return new SPDX 2.3 SBOM
        return builder.buildSPDX23SBOM();

    }

    /**
     * Coverts an SVIP Component into a SPDX23PackageObject
     *
     * @param component the SVIPComponent to use for information
     * @return An SPDX23PackageObject containing the data from the SVIPComponent
     */
    private SPDX23PackageObject convertComponent(Component component) {

        // Cast component as an SVIPComponentObject
        SVIPComponentObject componentSVIP = (SVIPComponentObject) component;

        // New builder for the CycloneDX Component
        SPDX23PackageBuilder builder = new SPDX23PackageBuilder();

        // Type
        builder.setType(componentSVIP.getType());

        // UID
        builder.setUID(componentSVIP.getUID());

        // Author
        builder.setAuthor(componentSVIP.getAuthor());

        // Name
        builder.setName(componentSVIP.getName());

        // Licenses
        builder.setLicenses(componentSVIP.getLicenses());

        // Copyright
        builder.setCopyright(componentSVIP.getCopyright());

        // get hashes then stream them into new CDX component
        Map<String, String> hashesSVIP = componentSVIP.getHashes();
        if (hashesSVIP != null) hashesSVIP.keySet().forEach(x -> builder.addHash(x, hashesSVIP.get(x)));

        // Comment
        builder.setComment(componentSVIP.getComment());

        // Attribution Text
        builder.setAttributionText(componentSVIP.getAttributionText());

        // Download Location
        builder.setDownloadLocation(componentSVIP.getDownloadLocation());

        // File Name
        builder.setFileName(componentSVIP.getFileName());

        // Files Analyzed
        builder.setFilesAnalyzed(componentSVIP.getFilesAnalyzed());

        // Verification Code
        builder.setVerificationCode(componentSVIP.getVerificationCode());

        // Home Page
        builder.setHomePage(componentSVIP.getHomePage());

        // Source Info
        builder.setSourceInfo(componentSVIP.getSourceInfo());

        // Release Date
        builder.setReleaseDate(componentSVIP.getReleaseDate());

        // Build Date
        builder.setBuildDate(componentSVIP.getBuiltDate());

        // Valid Until Date
        builder.setValidUntilDate(componentSVIP.getValidUntilDate());

        // Supplier
        builder.setSupplier(componentSVIP.getSupplier());

        // Version
        builder.setVersion(componentSVIP.getVersion());

        // Description
        builder.setDescription(componentSVIP.getDescription());

        // Stream CPEs into new SPDX Component
        if (componentSVIP.getCPEs() != null) componentSVIP.getCPEs().stream().forEach(x -> builder.addCPE(x));

        // Stream PURLs into new SPDX Component
        if (componentSVIP.getPURLs() != null) componentSVIP.getPURLs().stream().forEach(x -> builder.addPURL(x));

        // Stream External References into new SPDX Component
        if (componentSVIP.getExternalReferences() != null) {
            componentSVIP.getExternalReferences().stream().forEach(x -> builder.addExternalReference(x));
        }

        // Return new SPDX 2.3 Component
        return builder.buildAndFlush();

    }

}