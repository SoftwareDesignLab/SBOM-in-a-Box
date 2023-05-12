package org.svip.sbomfactory.generators.generators.spdx;

import org.svip.sbomfactory.generators.generators.utils.License;
import org.svip.sbomfactory.generators.generators.utils.LicenseManager;
import org.svip.sbomfactory.generators.generators.utils.Tool;
import org.svip.sbomfactory.generators.utils.ParserComponent;

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

        return out.toString();
    }

    /**
     * Write an SPDX tag-value document to the specified filepath.
     *
     * @param filePath The full filepath (including filename) to write the document to.
     */
    public void writeToFile(String filePath) {
        String tagValueString = this.writeToString();

        // TODO write tagValueString to filePath
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

    private String getFile(ParserComponent component) {
        return "";
    }

    private String getPackage(ParserComponent component) {
        return "";
    }

    //#endregion
}
