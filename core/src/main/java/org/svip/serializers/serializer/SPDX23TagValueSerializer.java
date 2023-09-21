package org.svip.serializers.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.objects.SVIPComponentObject;
import org.svip.sbom.model.objects.SVIPSBOM;
import org.svip.sbom.model.shared.Relationship;
import org.svip.sbom.model.shared.metadata.Contact;
import org.svip.sbom.model.shared.util.ExternalReference;
import org.svip.sbom.model.shared.util.LicenseCollection;
import org.svip.serializers.Metadata;

import java.util.HashSet;
import java.util.Map;
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

        return getDocumentInfo(sbom);
    }

    private String buildTagValue(String tag, String value) {
        if (tag == null || value == null) return "";
        return tag + ": " + value + "\n";
    }

    private String getCreatorString(String type, String primaryId, String secondaryId) {
        if (type.equalsIgnoreCase("tool"))
            return String.format("Tool: %s-%s", primaryId, secondaryId);

        return String.format("%s: %s (%s)", type, primaryId, secondaryId);
    }

    private String getDocumentInfo(SVIPSBOM sbom) {
        StringBuilder out = new StringBuilder();

        Set<String> creators = new HashSet<>(sbom.getCreationData().getAuthors().stream()
                .map(a -> getCreatorString("Person", a.getName(), a.getEmail())).toList());
        creators.addAll(sbom.getCreationData().getCreationTools().stream()
                .map(t -> getCreatorString("Tool", t.getName(), t.getVersion())).toList());

        if (sbom.getCreationData().getSupplier() != null) {
            Optional<Contact> supplierContact = sbom.getCreationData().getSupplier().getContacts().stream().findFirst();
            String supplierEmail = "";
            if (supplierContact.isPresent())
                supplierEmail = supplierContact.get().getEmail();
            creators.add(getCreatorString("Organization", sbom.getCreationData().getSupplier().getName(), supplierEmail));
        }

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

        Set<SVIPComponentObject> files = new HashSet<>();
        Set<SVIPComponentObject> packages = new HashSet<>();

        for (Component component : sbom.getComponents()) {
            SVIPComponentObject c = (SVIPComponentObject) component;
            if (c.getFileNotice() != null && !c.getFileNotice().isEmpty())
                files.add(c);
            else
                packages.add(c);
        }

        out.append("\n");
        for (SVIPComponentObject pkg : packages)
            out.append(getPackageInfo(pkg));

        out.append("\n### Unpackaged Files\n\n");
        for (SVIPComponentObject file : files)
            out.append(getFileInfo(file));

        out.append(getRelationships(sbom.getRelationships()));

        return out.toString();
    }

    private String getChecksum(Map<String, String> hashes) {
        if (hashes == null) return "";

        StringBuilder out = new StringBuilder();
        for (Map.Entry<String, String> hash : hashes.entrySet())
            out.append(buildTagValue("PackageChecksum", hash.getKey() + ": " + hash.getValue()));

        return out.toString();
    }

    private String getLicenseInfo(LicenseCollection licenses, boolean file) {
        if (licenses == null) return "";

        StringBuilder out = new StringBuilder();
        String concludedTag = (file ? "" : "Package") + "LicenseConcluded";
        String declaredTag = (file ? "" : "Package") + "LicenseDeclared";
        String fileTag = file ? "LicenseInfoInFile" : "PackageLicenseInfoFromFiles";
        String commentTag = (file ? "" : "Package") + "LicenseComments";

        if (licenses.getConcluded() != null)
            for (String concluded : licenses.getConcluded())
                out.append(buildTagValue(concludedTag, concluded));

        if (licenses.getInfoFromFiles() != null)
            for (String fromFile : licenses.getInfoFromFiles())
                out.append(buildTagValue(fileTag, fromFile));

        if (licenses.getDeclared() != null)
            for (String declared : licenses.getInfoFromFiles())
                out.append(buildTagValue(declaredTag, declared));

        out.append(buildTagValue(commentTag, licenses.getComment()));

        return out.toString();
    }

    private String getPackageInfo(SVIPComponentObject pkg) {
        StringBuilder out = new StringBuilder();

        out.append("### Package: " + pkg.getName() + "\n\n");
        out.append(buildTagValue("SPDXID", pkg.getUID()));
        out.append(buildTagValue("PackageName", pkg.getName()));
        out.append(buildTagValue("PackageVersion", pkg.getVersion()));
        if (pkg.getDescription() != null) {
            out.append(buildTagValue("PackageSummary", pkg.getDescription().getSummary()));
            out.append(buildTagValue("PackageDescription", pkg.getDescription().getDescription()));
        }
        out.append(buildTagValue("PackageComment", pkg.getComment()));
        out.append(buildTagValue("PackageFileName", pkg.getFileName()));

        if (pkg.getSupplier() != null) {
            Optional<Contact> supplierContact = pkg.getSupplier().getContacts().stream().findFirst();
            String supplierEmail = "";
            if (supplierContact.isPresent())
                supplierEmail = supplierContact.get().getEmail();
            out.append(buildTagValue("PackageSupplier",
                    String.format("Organization: %s (%s)", pkg.getSupplier().getName(), supplierEmail)));
        }

        out.append(buildTagValue("PackageOriginator", pkg.getAuthor()));
        out.append(buildTagValue("PackageDownloadLocation", pkg.getDownloadLocation()));
        if (pkg.getFilesAnalyzed() != null)
            out.append(buildTagValue("FilesAnalyzed", pkg.getFilesAnalyzed().toString()));
        out.append(buildTagValue("PackageVerificationCode", pkg.getVerificationCode()));
        out.append(getChecksum(pkg.getHashes()));
        out.append(buildTagValue("PackageHomePage", pkg.getHomePage()));
        out.append(buildTagValue("PackageSourceInfo", pkg.getSourceInfo()));
        out.append(getLicenseInfo(pkg.getLicenses(), false));
        out.append(buildTagValue("PackageCopyrightText", pkg.getCopyright()));

        Set<ExternalReference> references = new HashSet<>();
        if (pkg.getExternalReferences() != null) references = pkg.getExternalReferences();

        if (pkg.getCPEs() != null)
            references.addAll(pkg.getCPEs().stream()
                    .map(cpe -> new ExternalReference("SECURITY", cpe, "cpe23Type"))
                    .toList());

        if (pkg.getPURLs() != null)
            references.addAll(pkg.getPURLs().stream()
                    .map(purl -> new ExternalReference("PACKAGE-MANAGER", purl, "purl"))
                    .toList());

        for (ExternalReference ref : references) {
            out.append(buildTagValue("ExternalRef",
                    String.format("%s %s %s", ref.getCategory(), ref.getType(), ref.getUrl())));
            // TODO ExternalRefComment
        }

        out.append(buildTagValue("PackageAttributionText", pkg.getAttributionText()));
        out.append(buildTagValue("PrimaryPackagePurpose", pkg.getType()));
        out.append(buildTagValue("ReleaseDate", pkg.getReleaseDate()));
        out.append(buildTagValue("BuiltDate", pkg.getBuiltDate()));
        out.append(buildTagValue("ValidUntilDate", pkg.getValidUntilDate()));

        return out.append("\n").toString();
    }

    private String getFileInfo(SVIPComponentObject file) {

        String out = buildTagValue("SPDXID", file.getUID()) +
                buildTagValue("FileName", file.getName()) +
                buildTagValue("FileType", file.getType()) +
                buildTagValue("FileComment", file.getComment()) +
                getChecksum(file.getHashes()) +
                getLicenseInfo(file.getLicenses(), true) +
                buildTagValue("FileCopyrightText", file.getCopyright()) +
                buildTagValue("FileNotice", file.getFileNotice()) +
                buildTagValue("FileContributor", file.getAuthor()) +
                buildTagValue("FileAttributionText", file.getAttributionText()) +
                "\n";

        return out;
    }

    private String getRelationships(Map<String, Set<Relationship>> relationships) {
        StringBuilder out = new StringBuilder();

        for (Map.Entry<String, Set<Relationship>> rMap : relationships.entrySet()) {
            for (Relationship rel : rMap.getValue()) {
                out.append(buildTagValue("Relationship",
                        String.format("%s %s %s", rMap.getKey(), rel.getRelationshipType(), rel.getOtherUID())));
                out.append(buildTagValue("RelationshipComment", rel.getComment()));
            }
        }

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
    }
}
