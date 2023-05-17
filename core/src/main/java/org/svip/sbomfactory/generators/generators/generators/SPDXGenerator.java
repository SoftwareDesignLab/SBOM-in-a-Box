package org.svip.sbomfactory.generators.generators.generators;

import org.svip.sbomfactory.generators.generators.generators.spdx.Document;
import org.svip.sbomfactory.generators.generators.utils.Debug;
import org.svip.sbomfactory.generators.generators.utils.ParserComponent;
import utils.SBOM.Component;
import utils.SBOM.SBOM;
import utils.SBOM.SBOMType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

/**
 * File: SPDXGenerator.java
 * <p>
 * Extension of SBOMGenerator to write an SBOM object to a SPDX file.
 * <br>
 * See <a href="https://spdx.github.io/spdx-spec/v2.3/">SPDX 2.3 Specification</a> for reference.
 * </p>
 * @author Ian Dunn
 */
public class SPDXGenerator extends SBOMGenerator {

    //#region Constants

    /**
     * The version of the SPDX specification that this generator is using.
     */
    public static final String SPEC_VERSION = "2.3";

    //#endregion


    //#region Constructors

    /**
     * Default constructor used to instantiate a new SPDXGenerator.
     *
     * @param internalSBOM an internal SBOM representation with a completed DependencyTree.
     */
    public SPDXGenerator(SBOM internalSBOM) {
        super(internalSBOM, SBOMType.SPDX, SPEC_VERSION);
    }

    //#endregion

    //#region Overrides

    /**
     * Write an SPDX SBOM to a specified filepath and file format.
     *
     * @param directory The name of the SBOM file.
     * @param format The file format of the file to write to.
     */
    @Override
    public void writeFile(String directory, GeneratorSchema.GeneratorFormat format) {
        String path = generatePathToSBOM(directory, format);

        Debug.log(Debug.LOG_TYPE.DEBUG, "Building SPDX SBOM object");
        try {
            // Build model
            Document document = buildDocument(); // TODO make generic method that returns Object, cast BOM/Document

            // Serialize
            Debug.log(Debug.LOG_TYPE.DEBUG, "Attempting to write to " + path);

            // Get the correct OM from the format and write the file to it
            format.getObjectMapper().writerWithDefaultPrettyPrinter().writeValue(new File(path), document);

            Debug.log(Debug.LOG_TYPE.SUMMARY, "SPDX SBOM saved to: " + path);
        } catch (GeneratorException e) {
            Debug.log(Debug.LOG_TYPE.ERROR, "Document: " + e.getMessage());
        } catch (IOException e) {
            Debug.log(Debug.LOG_TYPE.EXCEPTION, e);
            Debug.log(Debug.LOG_TYPE.ERROR, "Error writing to file " + path);
        }
    }

    //#endregion

    /**
     * Builds an SPDX document from the internal SBOM returned in an Document that can be serialized to a file.
     *
     * @return An <code>Document</code> containing the data for a complete SPDX document.
     */
    private Document buildDocument() throws GeneratorException {
        SBOM intSBOM = getInternalSBOM();

        // Prepare required fields for the Document
        String name = "SPDX Document: " + this.getProjectName();
        String documentUri = getInternalSBOM().getSerialNumber();

        // Create new Document with the required fields
        Document document = new Document(name, documentUri, this.getTool());

        // Add all depth 0 components as packages
        final Set<Component> componentSet = intSBOM
                .getComponentChildren(intSBOM.getHeadUUID()); // Get all depth 0 dependencies

        for(Component c : componentSet) { // Loop through and add all packages
            this.addPackage(document, (ParserComponent) c, true);
        }

        return document;
    }

    /**
     * Private helper method to add a single package represented as a ParserComponent and its children (if specified)
     * to a provided Document.
     *
     * @param document The Document to add the package to.
     * @param component The ParserComponent to add to the Document.
     * @param recursive Whether to recursively add children of the package to the Document and mark as dependent on
     *                  the package one level above.
     * @return The ParserComponent that was added to the Document.
     */
    private ParserComponent addPackage(Document document, ParserComponent component, boolean recursive) throws GeneratorException {
        document.addPackage(component);

        if(recursive) {
            ArrayList<ParserComponent> children = new ArrayList<>();

            // Loop through and recursively convert children
            for (utils.SBOM.Component internal :
                    getInternalSBOM().getComponentChildren(component.getUUID())) {
                // Add child to store as well as internal list so we can add dependencies recursively
                children.add(this.addPackage(document, (ParserComponent) internal, true));

                // Add all dependencies to this component
                for(ParserComponent dependency : children) {
                    document.addDependency(dependency.getSPDXID(), component.getSPDXID());
                }
            }
        }

        return component;
    }
}
