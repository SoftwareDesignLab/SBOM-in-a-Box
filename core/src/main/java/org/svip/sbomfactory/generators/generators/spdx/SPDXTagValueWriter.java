package org.svip.sbomfactory.generators.generators.spdx;

import org.svip.sbom.model.CPE;
import org.svip.sbom.model.PURL;
import org.svip.sbomfactory.generators.generators.utils.License;
import org.svip.sbomfactory.generators.generators.utils.LicenseManager;
import org.svip.sbomfactory.generators.generators.utils.Tool;
import org.svip.sbomfactory.generators.utils.ParserComponent;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * File: SPDXTagValueWriter.java
 * <p>
 * A custom file writer to write an {@code SPDXStore} to a file as a tag-value document. All tag-value documentation and
 * examples can be found in the <a href="https://spdx.github.io/spdx-spec/v2.3/">official SPDX v2.3 specification</a>.
 * </p>
 * @author Ian Dunn
 */
public class SPDXTagValueWriter {

    //#region Attributes

    /**
     * The SPDXStore instance to write data from.
     */
    private final SPDXStore spdxStore;

    //#endregion

    //#region Constructors

    /**
     * The default constructor to construct an instance of SPDXTagValueWriter with an SPDXStore instance to write data
     * from.
     *
     * @param spdxStore The SPDXStore instance to write data from.
     */
    public SPDXTagValueWriter(SPDXStore spdxStore) {
        this.spdxStore = spdxStore;
    }

    //#endregion

    //#region Core Methods

    /**
     * Write an SPDX tag-value document to a string and return it.
     *
     * @return A string containing the entire SPDX tag-value document.
     */
    public String writeToString() {
        StringBuilder out = new StringBuilder();

        out.append(getDocumentHeader());
        out.append("## Creation Information\n").append(getCreationInformation());
        out.append("## Extracted License Information\n").append(getExtractedLicenseInformation());

        if(spdxStore.getFiles().size() > 0) {
            out.append("## File Information\n");
            for (Map.Entry<String, String> entry : spdxStore.getFiles().entrySet()) {
                out.append(getFile(entry.getKey(), entry.getValue()));
            }
        }

        out.append("## Packages\n");
        for(ParserComponent pkg : spdxStore.getPackages()) {
            out.append(getPackage(pkg));
        }

        if(spdxStore.getRelationships().size() > 0) {
            out.append("## Relationships\n");
            for(Relationship relationship : spdxStore.getRelationships()) {
                out.append("Relationship: ").append(relationship.toString()).append("\n");
            }
        }

        return out.toString();
    }

    /**
     * Write an SPDX tag-value document to the specified filepath.
     *
     * @param filePath The full filepath (including filename) to write the document to.
     */
    public void writeToFile(String filePath) throws IOException {
        PrintWriter out = new PrintWriter(filePath);
        out.println(this.writeToString());
        out.close();
    }

    private String getDocumentHeader() {
        StringBuilder out = new StringBuilder();
        out.append("SPDXVersion: ").append(spdxStore.getSpecVersion()).append("\n");
        out.append("DataLicense: ")
                .append(LicenseManager.getConcatenatedLicenseString(spdxStore.getToolLicenses())).append("\n");
        out.append("SPDXID: ").append(spdxStore.getDocumentId()).append("\n");
        out.append("DocumentName: ").append(spdxStore.getHeadComponent().getName()).append("\n");
        out.append("DocumentNamespace: ").append(spdxStore.getSerialNumber()).append("\n\n");
//        out.append("LicenseListVersion: ").append(spdxStore.getSpecVersion()).append("\n");

        return out.toString();
    }

    private String getCreationInformation() {
        StringBuilder out = new StringBuilder();
        for(Tool tool : spdxStore.getTools()) {
            out.append("Creator: ").append(tool.getToolInfo()).append("\n");
        }
        out.append("Created: ").append(spdxStore.getTimestamp()).append("\n\n");

        return out.toString();
    }

    private String getExtractedLicenseInformation() {
        StringBuilder out = new StringBuilder();
        for(License license : spdxStore.getExternalLicenses()) {
            out.append("LicenseID: ").append(license.getSpdxLicense()).append("\n");
            out.append("LicenseName: ").append(license.getLicenseName()).append("\n\n");
        }

        return out.toString();
    }

    private String getFile(String file, String spdxId) {
        StringBuilder out = new StringBuilder();

        out.append("FileName: ").append(file).append("\n");
        out.append("SPDXID: ").append(spdxId).append("\n");
        out.append("FileType: SOURCE\n\n"); // TODO we currently only analyze source files??

        return out.toString();
    }

    private String getPackage(ParserComponent component) {
        StringBuilder out = new StringBuilder();

        out.append("PackageName: ").append(component.getName()).append("\n");
        out.append("SPDXID: ").append(component.getSPDXID()).append("\n");
        if(component.getVersion() != null) out.append("PackageVersion: ").append(component.getVersion()).append("\n");
        if(component.getGroup() != null) out.append("PackageFileName: ").append(component.getGroup()).append("\n");

        if(component.getPublisher() != null && component.getPublisher().length() > 0
                && !component.getPublisher().equals("Unknown")) // TODO is this correct? See SPDXSerializer as well
            out.append("PackageSupplier: Organization: ").append(component.getPublisher());

        out.append("PackageChecksum: SHA-256: ").append(component.generateHash()).append("\n");
        out.append("PackageCopyrightText: NOASSERTION\n");

        if(component.getResolvedLicenses().size() > 0) {
            out.append("PackageLicenseConcluded: ")
                    .append(LicenseManager.getConcatenatedLicenseString(component.getResolvedLicenses())).append("\n");
        } else {
            out.append("PackageLicenseConcluded: NONE\n");
        }

        out.append("PackageLicenseDeclared: NOASSERTION\n");

        for(String cpe : component.getCpes()) {
            out.append("ExternalRef: SECURITY cpe23Type ").append(cpe).append("\n");
        }

        for(PURL purl : component.getPurls()) {
            out.append("ExternalRef: SECURITY purl ").append(purl.toString()).append("\n");
        }

        if(component.getFiles().size() > 0) {
            out.append("FilesAnalyzed: true\n");

            for(String file : component.getFiles()) {
                out.append("HasFile: ").append(spdxStore.getFiles().get(file)).append("\n");
            }

        } else {
            out.append("FilesAnalyzed: false\n");
        }

        return out.append("\n").toString();
    }

    //#endregion
}
