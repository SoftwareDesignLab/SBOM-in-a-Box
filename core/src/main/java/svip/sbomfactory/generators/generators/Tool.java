package generators;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * File: Tool.java
 * <p>
 * A dataclass to store information about an SBOM tool (in particular to be used to store data for our generators),
 * containing the vendor, name, version, hashes, and any applicable licenses.
 * </p>
 * @author Ian Dunn
 */
public class Tool {

    //#region Attributes

    /**
     * The vendor (creator/publisher) of the tool.
     */
    private final String vendor;

    /**
     * The name of the tool.
     */
    private final String name;

    /**
     * The version of the tool.
     */
    private final String version;

    /**
     * A list of licenses in the tool
     */
    private final Set<License> licenses;

    /**
     * A map to store hashes of the tool that maps a hash in string format to its algorithm (recommended SHA-256).
     */
    private final Map<String, String> hashes;

    //#endregion

    //#region Constructors

    /**
     * The default constructor to create a new Tool.
     *
     * @param vendor The vendor of the tool.
     * @param name The name of the tool.
     * @param version The version of the tool.
     */
    public Tool(String vendor, String name, String version) {
        this.vendor = vendor;
        this.name = name;
        this.version = version;

        this.licenses = new HashSet<>();
        this.hashes = new HashMap<>();
    }

    //#endregion

    //#region Static Methods

    /**
     * Create a timestamp to represent when an SBOM was generated by a tool.
     *
     * @return A timestamp of the current time with format yyyy-MM-ddTHH:mm:ssZ
     */
    public static String createTimestamp() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        return dateFormat.format(new Date());
    }

    //#endregion

    //#region Core Methods

    /**
     * Add a new license to the tool.
     *
     * @param license The license object to add.
     */
    public void addLicense(License license) throws GeneratorException {
        licenses.add(license);
    }

    /**
     * Add a hash of the tool to the tool.
     *
     * @param hash The hash string.
     * @param alg The hash algorithm.
     */
    public void addHash(String hash, String alg) throws GeneratorException {
        if(hash == null || hash.equals(""))
            throw new GeneratorException("Invalid hash: cannot be null/empty");
        if(alg == null || alg.equals(""))
            throw new GeneratorException("Invalid algorithm: cannot be null/empty");

        hashes.put(hash, alg);
    }

    //#endregion

    //#region Getters

    public String getVendor() {
        return vendor;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public Set<License> getLicenses() {
        return licenses;
    }

    public Map<String, String> getHashes() {
        return hashes;
    }

    /**
     * Gets the name and version of this generator tool, prefixed by "Tool: "
     *
     * @return The tool info of the generator.
     */
    public String getToolInfo() {
        return "Tool: " + name + " " + version;
    }

    //#endregion

}
