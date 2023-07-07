package org.svip.sbomfactory.serializers.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.svip.sbom.model.objects.SVIPSBOM;
import org.svip.sbom.model.shared.metadata.Contact;
import org.svip.sbomfactory.serializers.Metadata;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * File: SPDX23TagValueSerializer.java
 * This class implements the Serializer interface to provide all functionality to write an SBOM object to an SPDX 2.3
 * tag-value file string.
 *
 * @author Ian Dunn
 */
public class SPDX23TagValueSerializer implements Serializer {

    /**
     * Serializes an SBOM to an SPDX 2.3 tag-value file.
     *
     * @param sbom The SBOM to serialize.
     * @return A string containing the final SBOM file.
     */
    @Override
    public String writeToString(SVIPSBOM sbom) {
        StringBuilder out = new StringBuilder();

        out.append(getCreationInfo(sbom));

        return out.toString();
    }

    private String buildTagValue(String tag, String value) {
        return tag + ": " + value + "\n";
    }

    private String getCreatorString(String type, String primaryId, String secondaryId) {
        if (type.equalsIgnoreCase("tool"))
            return String.format("Tool: %s-%s", primaryId, secondaryId);

        return String.format("%s: %s (%s)", type, primaryId, secondaryId);
    }

    private String getCreationInfo(SVIPSBOM sbom) {
        StringBuilder out = new StringBuilder();

        Set<String> creators = new HashSet<>(sbom.getCreationData().getAuthors().stream()
                .map(a -> getCreatorString("Person", a.getName(), a.getEmail())).toList());
        creators.addAll(sbom.getCreationData().getCreationTools().stream()
                .map(t -> getCreatorString("Tool", t.getName(), t.getVersion())).toList());

        Optional<Contact> supplierContact = sbom.getCreationData().getSupplier().getContacts().stream().findFirst();
        String supplierEmail = "";
        if (supplierContact.isPresent())
            supplierEmail = supplierContact.get().getEmail();
        creators.add(getCreatorString("Organization", sbom.getCreationData().getSupplier().getName(), supplierEmail));

        out.append(buildTagValue("SPDXVersion", "SPDX-" + sbom.getSpecVersion()));
        out.append(buildTagValue("DataLicense", "CC0-1.0"));
        out.append(buildTagValue("SPDXID", "SPDXRef-DOCUMENT"));
        out.append(buildTagValue("DocumentName", sbom.getName()));
        out.append(buildTagValue("DocumentNamespace", sbom.getUID()));
        out.append(buildTagValue("LicenseListVersion", sbom.getSPDXLicenseListVersion()));

        for (String creator : creators) {
            out.append(buildTagValue("Creator", creator));
        }

        out.append(buildTagValue("Created", sbom.getCreationData().getCreationTime()));

        String creatorComment = sbom.getCreationData().getCreatorComment();
        String documentComment = sbom.getDocumentComment();
        if (creatorComment == null || creatorComment.isEmpty())
            creatorComment = Metadata.SERIALIZED_COMMENT;
        else if (documentComment == null || documentComment.isEmpty())
            documentComment = Metadata.SERIALIZED_COMMENT;

        out.append(buildTagValue("CreatorComment", creatorComment));
        out.append(buildTagValue("DocumentComment", documentComment));

        return out.toString();
    }

    /**
     * Gets the ObjectMapper of the serializer to expose configuration.
     *
     * @return A reference to the ObjectMapper of the serializer.
     */
    @Override
    public ObjectMapper getObjectMapper() {
        // We don't need an objectmapper for tag value but removing this breaks tests
        return new ObjectMapper();
    }

    /**
     * Sets the ObjectMapper of the serializer to enable or disable pretty printing.
     *
     * @param prettyPrint True to pretty-print, false otherwise.
     */
    @Override
    public void setPrettyPrinting(boolean prettyPrint) {
        // We don't need pretty printing for tag value either
        return;
    }
}
