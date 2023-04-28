package org.svip.sbomfactory.generators.generators;

import org.svip.sbomfactory.generators.generators.spdx.SPDXStore;
import org.svip.sbomfactory.generators.generators.utils.GeneratorException;
import org.svip.sbomfactory.generators.generators.utils.GeneratorSchema;
import org.svip.sbomfactory.generators.utils.Debug;
import org.svip.sbomfactory.generators.utils.ParserComponent;
import org.svip.sbom.model.Component;
import org.svip.sbom.model.SBOM;
import org.svip.sbom.model.SBOMType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import static org.svip.sbomfactory.generators.utils.Debug.log;

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

        log(Debug.LOG_TYPE.DEBUG, "Building SPDX SBOM object");
        try {
            // Build model
            SPDXStore SPDXStore = buildDocument(); // TODO make generic method that returns Object, cast CycloneDXStore/SPDXStore

            // Serialize
            log(Debug.LOG_TYPE.DEBUG, "Attempting to write to " + path);

            // Get the correct OM from the format and write the file to it
            format.getObjectMapper().writerWithDefaultPrettyPrinter().writeValue(new File(path), SPDXStore);

            log(Debug.LOG_TYPE.SUMMARY, "SPDX SBOM saved to: " + path);
        } catch (GeneratorException e) {
            log(Debug.LOG_TYPE.ERROR, "SPDXStore: " + e.getMessage());
        } catch (IOException e) {
            log(Debug.LOG_TYPE.EXCEPTION, e);
            log(Debug.LOG_TYPE.ERROR, "Error writing to file " + path);
        }
    }

    //#endregion

    /**
     * Builds an SPDX document from the internal SBOM returned in an SPDXStore that can be serialized to a file.
     *
     * @return An <code>SPDXStore</code> containing the data for a complete SPDX document.
     */
    private SPDXStore buildDocument() throws GeneratorException {
        SBOM intSBOM = getInternalSBOM();

        // Prepare required fields for the SPDXStore
        ParserComponent headComponent = (ParserComponent) intSBOM.getComponent(intSBOM.getHeadUUID());
        String name = "SPDX SPDXStore: " + this.getProjectName();
        String documentUri = getInternalSBOM().getSerialNumber();

        // Create new SPDXStore with the required fields
        SPDXStore SPDXStore = new SPDXStore(documentUri, 1, headComponent);
        SPDXStore.addTool(this.getTool()); // Add our tool as info

        // Add all depth 0 components as packages
        final Set<Component> componentSet = intSBOM
                .getComponentChildren(intSBOM.getHeadUUID()); // Get all depth 0 dependencies

        for(Component c : componentSet) { // Loop through and add all packages
            this.addPackage(SPDXStore, (ParserComponent) c, true);
        }

        return SPDXStore;
    }

    /**
     * Private helper method to add a single package represented as a ParserComponent and its children (if specified)
     * to a provided SPDXStore.
     *
     * @param SPDXStore The SPDXStore to add the package to.
     * @param component The ParserComponent to add to the SPDXStore.
     * @param recursive Whether to recursively add children of the package to the SPDXStore and mark as dependent on
     *                  the package one level above.
     * @return The ParserComponent that was added to the SPDXStore.
     */
    private ParserComponent addPackage(SPDXStore SPDXStore, ParserComponent component, boolean recursive) throws GeneratorException {
        SPDXStore.addComponent(component);

        if(recursive) {
            ArrayList<ParserComponent> children = new ArrayList<>();

            // Loop through and recursively convert children
            for (Component internal :
                    getInternalSBOM().getComponentChildren(component.getUUID())) {
                // Add child to store as well as internal list so we can add dependencies recursively
                children.add(this.addPackage(SPDXStore, (ParserComponent) internal, true));

                // Add all dependencies to this component
                for(ParserComponent dependency : children) {
                    SPDXStore.addChild(dependency, component); // TODO make sure this is tested
//                    SPDXStore.addDependency(dependency.getSPDXID(), component.getSPDXID());
                }
            }
        }

        return component;
    }
}
