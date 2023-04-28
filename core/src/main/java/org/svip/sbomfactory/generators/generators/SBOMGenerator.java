package generators;

import org.apache.commons.codec.digest.DigestUtils;
import utils.Debug;
import utils.SBOM.SBOM;
import utils.SBOM.SBOMType;

import java.util.UUID;

import static utils.Debug.log;

/**
 * File: SBOMGenerator.java
 * <p>
 * An abstract class to be extended by specific generators such as CDX and SPDX that implement origin format specific
 * methods.
 * </p>
 * @author Ian Dunn
 */
public abstract class SBOMGenerator {

    //#region Constants

    /**
     * The name of our SBOMGenerator tool.
     */
    public static final String TOOL_NAME = "BenchmarkGenerator";

    /**
     * The version of our SBOMGenerator tool.
     */
    public static final String TOOL_VERSION = "v4.1.0-alpha";

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

    /**
     * The format of the SBOM to be generated.
     */
    private final SBOMType originFormat;

    /**
     * The specification version of the SBOM to be generated.
     */
    private final String specVersion;

    //#endregion

    //#region Constructors

    /**
     * Default constructor to instantiate a new SBOMGenerator.
     *
     * @param internalSBOM an internal SBOM representation with a completed DependencyTree.
     */
    protected SBOMGenerator(SBOM internalSBOM, SBOMType originFormat, String specVersion) {
        this.internalSBOM = internalSBOM;
        this.originFormat = originFormat;
        this.specVersion = specVersion;

        String hash = getHash();

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
        internalSBOM.setOriginFormat(originFormat);
        internalSBOM.setSpecVersion(specVersion);
    }

    //#endregion

    //#region Abstract Methods

    /**
     * Write an SBOM to a specified filepath and file format.
     *
     * @param directory The path of the SBOM file including the file name and type to write to.
     * @param format The file format to write to the file.
     */
    public abstract void writeFile(String directory, GeneratorSchema.GeneratorFormat format);

    //#endregion

    //#region Getters

    protected SBOM getInternalSBOM() { return internalSBOM; }

    protected Tool getTool() {
        return tool;
    }

    /**
     * Generate a unique hash for this generator instance based on unique fields such as the internal SBOM and its
     * filetype, origin format, and specification version.
     *
     * @return A SHA256 hash represented as a string.
     */
    protected String getHash() {
        return DigestUtils.sha256Hex(this.toString());
    }

    /**
     * Gets the project name, which is defined as the name of the head component of the internal DependencyTree by the
     * parser.
     *
     * @return The name of the project.
     */
    public String getProjectName() { return internalSBOM.getComponent(internalSBOM.getHeadUUID()).getName(); }

    //#endregion

    //#region Utility Methods

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


        path.append(getProjectName()) // Append project name
                .append("_").append(this.originFormat) // Append origin format for transparency
                .append('.').append(format.getExtension()); // Append file extension

        return path.toString();
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
                ", originFormat=" + originFormat +
                ", specVersion='" + specVersion + '\'' +
                '}';
    }

    //#endregion
}
