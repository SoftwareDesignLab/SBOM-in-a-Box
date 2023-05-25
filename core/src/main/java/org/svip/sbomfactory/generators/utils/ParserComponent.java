package org.svip.sbomfactory.generators.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.svip.sbom.model.Component;
import org.svip.sbomfactory.generators.utils.generators.License;
import org.svip.sbomfactory.generators.utils.generators.LicenseManager;

import java.util.*;

/**
 * <b>File</b>: Component.java<br>
 * <b>Description</b>: Class representation of a "Component" or
 * "Dependency", which serves as a data object mainly.
 *
 * @author Dylan Mulligan
 * @author Derek Garcia
 * @author Ian Dunn
 */
public class ParserComponent extends Component {
    //#region Enums

    /**
     * Enums for classifying component types
     * <p>
     * <b>Language</b> : Packages created by the language maintainers. ie, java.util.Arrays etc<br/>
     * <b>Internal</b> : Package inside the src code. ie, Parsers.ParserCore<br/>
     * <b>External</b> : Traditional third party packages. ie, com.fasterxml.jackson.databind.ObjectMapper<br/>
     * <b>Unknown</b>  : Unable to determine package type
     */
    public enum Type {
        LANGUAGE,
        INTERNAL,
        APPLICATION,
        DEAD_IMPORT, // Internal type used to determine whether to remove a component
        EXTERNAL;

        /**
         * Returns the enum constant of this type with the specified short value.
         * This short value is the first character of the type. i.e. 'e'|'E' -> 'EXTERNAL'.
         * It is treated as case-insensitive, and will be capitalized before comparison.
         *
         * @return the enum constant with the specified name
         * @throws IllegalArgumentException – if this enum type has no constant with the specified name
         */
        public static Type shortValueOf(Character value) {
            for (Type type : Type.values()) {
                if(type.name().charAt(0) == Character.toUpperCase(value)) return type;
            }
            throw new IllegalArgumentException("This Enum has no constant with the specified short value");
        }
    }

    //#endregion

    //#region Attributes

    // Component Details
    // NOTE: DO NOT CHANGE THE ORDER OF THESE UNLESS NECESSARY
    private Type type;                      // see above; default to EXTERNAL
    private int depth = 0;                  // set component depth, 0 by default
    private String alias;                   // Used name, ie 'import foo as bar'; name = foo, alias = bar
    private List<String> files;                    // A list of files that this component is parsed from
    private Set<License> resolvedLicenses;
    /**
     * Unique identifier for SPDX component
     */
    private String SPDXid;

    /**
     * Group of the component
     */
    protected String group;

    //#endregion

    //#region Constructors

    public ParserComponent(String name) {
        super(name, null);
        this.setUnpackaged(true);
        this.files = new ArrayList<>();
        this.type = Type.EXTERNAL;
        this.resolvedLicenses = new HashSet<>();
    }

    /**
     * "Copy" constructor that allows us to construct a ParserComponent from an existing Component. This is for use in
     * the generators, which operate solely with ParserComponents.
     */
    public ParserComponent(Component component) {
        this(component.getName());
        this.setUUID(component.getUUID());
        this.setPublisher(component.getPublisher());
        this.setVersion(component.getVersion());
        this.setLicenses(component.getLicenses());
        this.setCpes(component.getCpes());
        this.setPurls(component.getPurls());
        this.setSwids(component.getSwids());
        this.setUniqueID(component.getUniqueID());

        // Add children TODO needed?
        component.getChildren().forEach(this::addChild);

        // Add conflicts
        component.getConflicts().forEach(this::addConflict);

        // Add vulnerabilities
        component.getVulnerabilities().forEach(this::addVulnerability);
    }

    //#endregion

    //#region Getters

    public Type getType() { return this.type; }
    public int getDepth() { return this.depth; }
    public String getAlias() { if(Objects.equals(alias, ""))return null; return this.alias; }
    public List<String> getFiles() { return this.files; }

    public Set<License> getResolvedLicenses() { return resolvedLicenses; }
    public String getSPDXID() { return SPDXid; }

    public String getGroup() {
        if(Objects.equals(group, ""))
            return null;
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    //#endregion

    //#region Setters

    public void setType(Type type) { this.type = type; }
    public void setDepth(int depth) { this.depth = depth; }
    public void setAlias(String alias) { this.alias = alias; }
    public void setSPDXID(String spdxid) { this.SPDXid = spdxid; }

    public void addFile(String file) {
        this.files.add(file);
    }

    public void setPackaged() {
        setUnpackaged(false); // Set to packaged
        setType(Type.EXTERNAL);
    }

    //#endregion

    //#region Core Methods

    /**
     * Resolve all String licenses added to this ParserComponent during parsing to a {@code License} that converts the
     * license string to a short SPDX identifier. If more than one license exists in a string, it will be resolved to
     * multiple licenses.
     */
    public void resolveLicenses() {
        if(getLicenses() == null || getLicenses().isEmpty()) return;
        for(String licenseName : getLicenses()) {
            Debug.log(Debug.LOG_TYPE.DEBUG, String.format("Attempting to resolve license \"%s\"", licenseName));

            List<License> licenses = Arrays.stream(licenseName.split(",")).map(License::new).toList();

            boolean licenseFound = false;
            for(License license : licenses) {
                if (!licenseFound) {
                    addResolvedLicense(license);
                    licenseFound = true;
                } else if (license.getSpdxLicense() != null) addResolvedLicense(license);
            }
        }
    }

    /**
     * Get a set of all licenses that could not be resolved to an SPDX identifier.
     *
     * @return A set of all licenses that could not be resolved to an SPDX identifier.
     */
    public Set<License> getUnresolvedLicenses() {
        Set<License> unresolvedLicenses = new HashSet<>();
        for(License l : resolvedLicenses) {
            if(l.getSpdxLicense() == null || !LicenseManager.isValidShortString(l.getSpdxLicense()))
                unresolvedLicenses.add(l);
        }
        return unresolvedLicenses;
    }

    /**
     * Add an already-resolved license to this ParserComponent.
     *
     * @param resolved The {@code License} to add to this ParserComponent.
     */
    public void addResolvedLicense(License resolved) {
        Debug.log(Debug.LOG_TYPE.DEBUG, String.format("SPDXStore: License resolved in component %s: \"%s\"",
                this.getName(), resolved.getLicenseName()));
        resolvedLicenses.add(resolved);
    }

    /**
     * Attempt to resolve a current {@code License} to a custom identifier; this should be used when there is not a
     * valid SPDX identifier for a license string.
     *
     * @param license The license (must already be resolved to a {@code License} in this ParserComponent).
     * @param identifier The custom identifier to set the license to.
     * @return The License instance that contains the license and new custom identifier.
     */
    public License resolveLicense(String license, String identifier) {
        License toUpdate = resolvedLicenses.stream()
                .filter(currentLicense -> currentLicense.getLicenseName().equals(license)).findFirst()
                .orElse(null);

        if(toUpdate != null) {
            Debug.log(Debug.LOG_TYPE.DEBUG, String.format("Resolved license \"%s\" to short string \"%s\"",
                    license, identifier));
            return toUpdate.setSpdxLicense(identifier);
        } else {
            Debug.log(Debug.LOG_TYPE.WARN, String.format("Failed to resolve license \"%s\" to short string \"%s\"",
                    license, identifier));
            return null;
        }
    }

    /**
     * Returns a console readable string representation of this Component
     * meant to take up one line.
     *
     * @return a Console-Friendly String representation of this Component
     */
    public String toReadableString() {
        String out = "";

        if (this.group != null)
            out += String.format("FROM %s ", this.group);
        out += String.format("IMPORT %s ", this.getName());

        if (this.alias != null)
            out += String.format("AS %s ", this.alias);

        out += String.format("; [ %s ]", this.type);

        return out;
    }

    /**
     * Generate a SHA-256 of the current ParserComponent, including all data stored within.
     *
     * @return A String representation of a SHA-256 hash.
     */
    public String generateHash() {
        return DigestUtils.sha256Hex(this.toString()); // Hash the unique toString of this method
    }

    //#endregion

    //#region Overridden Methods

    /**
     * Generate a unique UUID based on the hash of this Component.
     *
     * @return A UUID unique to this Component.
     */
    public UUID generateUUID() {
        // Convert unique Component identifiers to byte representation
        byte[] uuidBytes = (generateHash()).getBytes();

        // Generate UUID
        final UUID uuid = UUID.nameUUIDFromBytes(uuidBytes);

        // Set generated UUID
        this.setUUID(uuid);

        // Return generated UUID
        return uuid;
    }

    /**
     * Returns a string representation of this ParserComponent including base Component information.
     *
     * @return a Full String representation of this ParserComponent.
     */
    @Override
    public String toString() {
        return "ParserComponent{" +
                "ComponentString=" + super.toString() +
                "type=" + type +
                ", depth=" + depth +
                ", group=" + group +
                ", alias='" + alias + '\'' +
                ", resolvedLicenses=" + resolvedLicenses +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ParserComponent that = (ParserComponent) o;

        if (depth != that.depth) return false;
        if (type != that.type) return false;
        if (!Objects.equals(group, that.group)) return false;
        return Objects.equals(alias, that.alias);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + depth;
        result = 31 * result + (group != null ? group.hashCode() : 0);
        result = 31 * result + (alias != null ? alias.hashCode() : 0);
        return result;
    }

    //#endregion
}
