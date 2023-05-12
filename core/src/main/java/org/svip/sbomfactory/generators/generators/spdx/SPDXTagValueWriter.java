package org.svip.sbomfactory.generators.generators.spdx;

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
    private SPDXStore spdxStore;

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
        // TODO
        return "";
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

    //#endregion
}
