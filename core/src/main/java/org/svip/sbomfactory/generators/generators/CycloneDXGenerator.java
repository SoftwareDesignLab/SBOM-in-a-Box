package org.svip.sbomfactory.generators.generators;

import org.svip.sbomfactory.generators.generators.cyclonedx.CycloneDXStore;
import org.svip.sbomfactory.generators.generators.utils.GeneratorException;
import org.svip.sbomfactory.generators.generators.utils.GeneratorSchema;
import org.svip.sbomfactory.generators.utils.Debug;
import org.svip.sbomfactory.generators.utils.ParserComponent;
import org.svip.sbom.model.Component;
import org.svip.sbom.model.SBOM;
import org.svip.sbom.model.SBOMType;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import static org.svip.sbomfactory.generators.utils.Debug.log;

/**
 * File: CDXGenerator.java
 * <p>
 * Extension of SBOMGenerator to write an SBOM object to a CDX file
 * <br>
 * See <a href="https://cyclonedx.org/docs/1.4/json/">CDX 1.4 JSON Reference</a> for reference.
 * </p>
 * @author Ian Dunn
 * @author Dylan Mulligan
 */
public class CycloneDXGenerator extends SBOMGenerator {

    //#region Constants

    /**
     * The version of the CDX specification that this generator is using.
     */
    public static final String SPEC_VERSION = "1.4";

    //#endregion

    //#region Constructors

    /**
     * Default constructor used to instantiate a new CDXGenerator.
     *
     * @param internalSBOM an internal SBOM representation with a completed DependencyTree
     */
    public CycloneDXGenerator(SBOM internalSBOM) {
        super(internalSBOM, SBOMType.CYCLONE_DX, SPEC_VERSION);
    }

    //#endregion

    //#region Overrides

    /**
     * Write a CDX SBOM to a specified filepath and file format.
     *
     * @param directory The name of the SBOM file.
     * @param format The file format to write to.
     */
    @Override
    public void writeFile(String directory, GeneratorSchema.GeneratorFormat format) {
        String path = generatePathToSBOM(directory, format);

        log(Debug.LOG_TYPE.DEBUG, "Building CDX SBOM object");
        try {
            // Build model
            CycloneDXStore cycloneDXStore = buildCDXBOM();

            // Serialize
            log(Debug.LOG_TYPE.DEBUG, "Attempting to write to " + path);

            // Get the correct OM from the format and write the file to it
            format.getObjectMapper().writerWithDefaultPrettyPrinter().writeValue(new File(path), cycloneDXStore);

            log(Debug.LOG_TYPE.SUMMARY, "CycloneDX SBOM saved to: " + path);
        } catch (IOException e) {
            log(Debug.LOG_TYPE.EXCEPTION, e);
            log(Debug.LOG_TYPE.ERROR, "Error writing to file " + path);
        }
    }

    //#endregion

    /**
     * Builds a CDX document from the internal SBOM returned in a CycloneDXStore that can be serialized to a file.
     *
     * @return A <code>CycloneDXStore</code> containing the data for a complete CDX document.
     */
    private CycloneDXStore buildCDXBOM() {
        // Get internal SBOM Object
        final SBOM intSBOM = getInternalSBOM();

        ParserComponent headComponent = (ParserComponent) intSBOM.getComponent(intSBOM.getHeadUUID());
        String serialNumber = intSBOM.getSerialNumber();
        int version = 1; // TODO should we have to increment this?

        // Construct cycloneDXStore for output
        final CycloneDXStore cycloneDXStore = new CycloneDXStore(serialNumber, version, headComponent);
        cycloneDXStore.addTool(this.getTool()); // Add our tool as info

        // Add all depth 0 components as packages
        final Set<Component> componentSet = intSBOM
                .getComponentChildren(intSBOM.getHeadUUID()); // Get all depth 0 dependencies

        for(Component c : componentSet) { // Loop through and add all packages
            this.addComponent(cycloneDXStore, (ParserComponent) c, true);
        }

        return cycloneDXStore;
    }


    /**
     * Private helper method to add a single package represented as a ParserComponent and its children (if specified)
     * to a provided SPDXStore.
     *
     * @param cycloneDXStore The CycloneDXStore to add the component to.
     * @param component The ParserComponent to add to the CycloneDXStore.
     * @param recursive Whether to recursively add children of the package to the CycloneDXStore and mark as dependent on
     *                  the package one level above.
     */
    private void addComponent(CycloneDXStore cycloneDXStore, ParserComponent component, boolean recursive) {
        cycloneDXStore.addComponent(component);

        if(recursive) {
            // Loop through and recursively convert children
            addChildren(cycloneDXStore, component);
        }
    }

    /**
     * Recursive private helper method to add children of top level components to the CycloneDXStore provided. Since they are not
     * directly added to the top-level list of packages, this needs to be a separate method to add all children of
     * children.
     *
     * @param cycloneDXStore The CycloneDXStore to add the component child to.
     * @param component The ParserComponent to add to the CycloneDXStore.
     */
    private void addChildren(CycloneDXStore cycloneDXStore, ParserComponent component) {
        // Get set of all children from the internal SBOM
        Set<ParserComponent> children = (Set<ParserComponent>) (Set<?>) getInternalSBOM()
                .getComponentChildren(component.getUUID());

        // Loop through children and add the child and its children recursively to the CycloneDXStore
        for (ParserComponent internal : children) {
            try {
                cycloneDXStore.addChild(component, internal);
            } catch(GeneratorException e) {
                Debug.log(Debug.LOG_TYPE.WARN, "CycloneDXStore: " + e.getMessage());
            }

            addChildren(cycloneDXStore, internal);
        }
    }
}
