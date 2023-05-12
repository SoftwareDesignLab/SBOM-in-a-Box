package org.svip.sbomfactory.generators.generators;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.svip.sbom.model.Component;
import org.svip.sbomfactory.generators.generators.cyclonedx.CycloneDXStore;
import org.svip.sbomfactory.generators.generators.spdx.SPDXStore;
import org.svip.sbomfactory.generators.generators.spdx.SPDXTagValueWriter;
import org.svip.sbomfactory.generators.generators.utils.GeneratorException;
import org.svip.sbomfactory.generators.generators.utils.GeneratorSchema;
import org.svip.sbomfactory.generators.generators.utils.License;
import org.svip.sbomfactory.generators.generators.utils.Tool;
import org.svip.sbomfactory.generators.utils.Debug;
import org.svip.sbom.model.SBOM;
import org.svip.sbom.model.SBOMType;
import org.svip.sbomfactory.generators.utils.ParserComponent;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

import static org.svip.sbomfactory.generators.utils.Debug.log;

/**
 * File: SBOMGenerator.java
 * <p>
 * A class to write our internal {@code SBOM} object to a file by transforming all relevant data into one of our
 * schema-specific store classes and then serializing it to multiple possible file formats.
 * </p>
 * @author Ian Dunn
 */
public class SBOMGenerator {

    //#region Constants

    /**
     * The name of our SBOMGenerator tool.
     */
    public static final String TOOL_NAME = "BenchmarkGenerator";

    /**
     * The version of our SBOMGenerator tool.
     */
    public static final String TOOL_VERSION = "v4.3.2-alpha";

    /**
     * The license of our SBOMGenerator tool.
     */
    public static final String TOOL_LICENSE = "MIT";

    /**
     * The license URL of our SBOMGenerator tool.
     */
    public static final String TOOL_LICENSE_URL = "https://opensource.org/license/mit/";

    //#endregion

    //#region Attributes

    /**
     * The internal SBOM representation used to generate an SBOM file.
     */
    private final SBOM internalSBOM;

    private final Tool tool;

    private final GeneratorSchema schema;

    //#endregion

    //#region Constructors

    /**
     * Default constructor to instantiate a new SBOMGenerator.
     *
     * @param internalSBOM an internal SBOM representation with a completed DependencyTree.
     * @param schema The schema of the output of this generator.
     */
    public SBOMGenerator(SBOM internalSBOM, GeneratorSchema schema) {
        this.internalSBOM = internalSBOM;
        this.schema = schema;

        String hash = DigestUtils.sha256Hex(this.toString());

        /*
            Non-SBOM specific settings
         */
        this.tool = new Tool("SVIP", TOOL_NAME, TOOL_VERSION);
        try {
            License toolLicense = new License(TOOL_LICENSE);
            toolLicense.setUrl(TOOL_LICENSE_URL);
            this.tool.addLicense(toolLicense);
            this.tool.addHash(hash, "SHA-256");
        } catch (GeneratorException e) {
            log(Debug.LOG_TYPE.ERROR, e.getMessage());
        }

        // Set sbom serial number based off this generator hash
        UUID uuid = UUID.nameUUIDFromBytes(hash.getBytes());
        internalSBOM.setSerialNumber(String.format("urn:uuid:%s", uuid));

        /*
            SBOM specific settings
         */

        internalSBOM.setOriginFormat(schema.getInternalType());
        internalSBOM.setSpecVersion(schema.getVersion());
    }

    //#endregion

    //#region Core Methods

    /**
     * Write an SBOM to a specified filepath and file format.
     *
     * @param directory The path of the SBOM file including the file name and type to write to.
     * @param format The file format to write to the file.
     */
    public void writeFile(String directory, GeneratorSchema.GeneratorFormat format) throws IOException {
        String path = generatePathToSBOM(directory, format);

        log(Debug.LOG_TYPE.DEBUG, "Building " + schema.name() + " SBOM object");
        try {
            // Build model
            BOMStore bomStore = buildBOMStore();

            // Serialize
            log(Debug.LOG_TYPE.DEBUG, "Attempting to write to " + path);
            if(format == GeneratorSchema.GeneratorFormat.SPDX) {
                SPDXTagValueWriter writer = new SPDXTagValueWriter((SPDXStore) bomStore);
                writer.writeToFile(path);
            } else {
                format.getObjectMapper(schema).writerWithDefaultPrettyPrinter().writeValue(new File(path), bomStore);
            }
            log(Debug.LOG_TYPE.SUMMARY, schema.name() + " SBOM saved to: " + path);
        } catch (GeneratorException e) {
            log(Debug.LOG_TYPE.ERROR, "Unable to write " + schema.name() + " SBOM to " + path);
        }
    };

    /**
     * Write an SBOM to a single String, either pretty-printed or on one line.
     *
     * @param format The file format of the SBOM to write to the string.
     * @param prettyPrint Whether to pretty-print the SBOM or leave it on one line.
     *
     * @return A string representation of an SBOM file.
     */
    public String writeFileToString(GeneratorSchema.GeneratorFormat format, boolean prettyPrint) {
        log(Debug.LOG_TYPE.DEBUG, "Building " + schema.name() + " SBOM object");

        try {
            BOMStore bomStore = buildBOMStore();
            ObjectMapper mapper = format.getObjectMapper(schema);
            if(!prettyPrint) mapper.setDefaultPrettyPrinter(null);

            String out;

            if(format == GeneratorSchema.GeneratorFormat.SPDX) {
                SPDXTagValueWriter writer = new SPDXTagValueWriter((SPDXStore) bomStore);
                out = writer.writeToString(); // TODO should we support pretty-printing?
            } else {
                out = mapper.writeValueAsString(bomStore);
            }
            log(Debug.LOG_TYPE.SUMMARY, schema.name() + " SBOM successfully written to string");
            return out;
        } catch (GeneratorException | JsonProcessingException e) {
            log(Debug.LOG_TYPE.ERROR, "Unable to write " + schema.name() + " SBOM to a string");
        }

        return null;
    }

    //#endregion

    //#region Utility Methods

    /**
     * This uses the internal SBOM object passed into the SBOMGenerator and converts it into a store (depending on the
     * schema) that extends the BOMStore abstract class.
     *
     * @return A BOMStore containing all transformed data of the SBOM.
     */
    protected BOMStore buildBOMStore() throws GeneratorException {
        ParserComponent headComponent = (ParserComponent) internalSBOM.getComponent(internalSBOM.getHeadUUID());
        String serialNumber = internalSBOM.getSerialNumber();
        int version = 1; // TODO should we have to increment this?

        Object[] parameters = {serialNumber, version, headComponent};
        Class<?>[] parameterTypes = Arrays.stream(parameters).map(Object::getClass).toArray(Class<?>[]::new);

        BOMStore bomStore = null;
        try {
            bomStore = schema.getBomStoreType().getDeclaredConstructor(parameterTypes).newInstance(parameters);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new GeneratorException(e.getMessage());
        }

        bomStore.addTool(tool); // Add our tool as info

        // Add all depth 0 components as packages
        final Set<Component> componentSet = internalSBOM
                .getComponentChildren(internalSBOM.getHeadUUID()); // Get all depth 0 dependencies

        for(Component c : componentSet) { // Loop through and add all packages
            this.addComponent(bomStore, (ParserComponent) c, true);
        }

        return bomStore;
    }

    /**
     * This adds a ParserComponent to a given BOMStore, with the option to recursively add its children.
     *
     * @param bomStore The BOMStore to add the component to.
     * @param component The component that will be added to the BOMStore.
     * @param recursive Whether to recursively add children of {@code component} to the BOMStore.
     */
    protected void addComponent(BOMStore bomStore, ParserComponent component, boolean recursive) {
        bomStore.addComponent(component);

        if(recursive) {
            // Note: We can't make addComponent recursive because the specific BOMStore may not want the component added
            // to the top-level list
            addChildren(bomStore, component);
        }

    }

    /**
     * A private helper method to recursively add children of a specified component to a BOMStore without adding it to
     * the top-level list of components.
     *
     * @param bomStore The BOMStore to add the component to.
     * @param component The component whose children will be added to the BOMStore.
     */
    protected void addChildren(BOMStore bomStore, ParserComponent component) {
        // Get set of all children from the internal SBOM
        Set<ParserComponent> children = (Set<ParserComponent>) (Set<?>) internalSBOM
                .getComponentChildren(component.getUUID());

        // Loop through children and add the child and its children recursively to the CycloneDXStore
        for (ParserComponent internal : children) {
            try {
                bomStore.addChild(component, internal);
            } catch(GeneratorException e) {
                Debug.log(Debug.LOG_TYPE.WARN, "BOMStore: " + e.getMessage());
            }

            addChildren(bomStore, internal);
        }
    }

    /**
     * Utility method to generate a filepath to output including a file name and type to based on a given directory
     * and format.
     *
     * @param directory The directory to write the SBOM to.
     * @param format The format of the SBOM that will be written.
     * @return The complete filepath including file name and extension.
     */
    protected String generatePathToSBOM(String directory, GeneratorSchema.GeneratorFormat format) {
        // Get project name from head component of the SBOM
        StringBuilder path = new StringBuilder(directory);

        // Language specific slash
        final String os = System.getProperty("os.name").toLowerCase();
        if(os.contains("win")) path.append('\\');
        if(os.contains("mac") || os.contains("nix") || os.contains("nux") || os.contains("aix")) path.append('/');

        String projectName = internalSBOM.getComponent(internalSBOM.getHeadUUID()).getName();
        path.append(projectName) // Append project name
                .append("_").append(this.schema) // Append origin format for transparency
                .append('.').append(format.getExtension()); // Append file extension

        return path.toString();
    }

    //#endregion

    //#region Getters

    public SBOM getInternalSBOM() {
        return internalSBOM;
    }

    public Tool getTool() {
        return tool;
    }

    public GeneratorSchema getSchema() {
        return schema;
    }


    //#endregion

    //#region Overrides

    /**
     * Generates a unique string representation by adding the internal SBOM, format and specification information, and
     * other unique values to an output string.
     *
     * @return A string unique to this instance of the SBOM generator.
     */
    @Override
    public String toString() {
        return "SBOMGenerator{" +
                "internalSBOM=" + internalSBOM +
                "tool=" + tool +
                ", originFormat=" + schema +
                ", specVersion='" + internalSBOM.getSpecVersion() + '\'' +
                '}';
    }

    //#endregion
}
