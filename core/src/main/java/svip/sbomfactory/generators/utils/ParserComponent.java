package utils;

import generators.License;
import generators.LicenseManager;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import utils.SBOM.Component;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * <b>File</b>: Component.java<br>
 * <b>Description</b>: Class representation of a "Component" or
 * "Dependency", which serves as a data object mainly.
 *
 * @author Dylan Mulligan
 * @author Derek Garcia
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
        EXTERNAL,
        UNKNOWN;

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
    private String file;                    // The file that this component is parsed from
    private Set<License> resolvedLicenses;
    // TODO we are no longer writing directly to a depfile - is this needed solely for the toString?
    private static final ObjectMapper OM = new ObjectMapper(new JsonFactory()); // Object Mapper Initialization
    static { OM.setSerializationInclusion(JsonInclude.Include.NON_NULL); } // Object Mapper Configuration

    //#endregion

    //#region Constructors

    public ParserComponent(String name) {
        super(name, null);
        this.setUnpackaged(true); // TODO Set unpackaged until we assume otherwise
        this.file = null;
        this.type = Type.UNKNOWN; // TODO Set UNKNOWN until we assume otherwise
        this.resolvedLicenses = new HashSet<>();
    }

    /**
     * Placeholder constructor that enables instances of
     * Component to be created dynamically by an ObjectMapper.
     */
    public ParserComponent() { }

    //#endregion

    //#region Getters

    public Type getType() { return this.type; }
    public int getDepth() { return this.depth; }
    public String getAlias() { return this.alias; }
    public String getFile() { return this.file; }

    public Set<License> getResolvedLicenses() {
        return resolvedLicenses;
    }

    //#endregion

    //#region Setters

    public void setType(Type type) { this.type = type; }
    public void setDepth(int depth) { this.depth = depth; }
    public void setAlias(String alias) { this.alias = alias; }
    public void setFile(String file) {
        this.file = file;
        if(this.type.equals(Type.UNKNOWN)) {
            setType(Type.INTERNAL); // If type has not been assumed, this is internal
        }
    }

    public void setPackaged() {
        setUnpackaged(false); // Set to packaged
        setType(Type.EXTERNAL);
    }

    //#endregion

    //#region Core Methods

    // TODO docstring
    public void resolveLicenses() {
        for(String licenseName : getLicenses()) {
            Debug.log(Debug.LOG_TYPE.DEBUG, String.format("Document: License found in component %s: \"%s\"",
                    this.getName(), licenseName));

            resolvedLicenses.add(new License(licenseName));
        }
    }

    public Set<License> getUnresolvedLicenses() {
        Set<License> unresolvedLicenses = new HashSet<>();
        for(License l : resolvedLicenses) {
            if(l.getSpdxLicense() == null || !LicenseManager.isValidShortString(l.getSpdxLicense()))
                unresolvedLicenses.add(l);
        }
        return unresolvedLicenses;
    }

    public void addResolvedLicense(License resolved) {
        resolvedLicenses.add(resolved);
    }

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

    //#endregion

    //#region Overridden Methods

    // TODO: Update with all unique ParserComponent identifiers and add Docstring
    @Override
    public UUID generateUUID() {
        // Convert unique Component identifiers to byte representation
        byte[] uuidBytes = (this.getName() + this.getVersion()).getBytes();

        // Generate UUID
        final UUID uuid = UUID.nameUUIDFromBytes(uuidBytes);

        // Set generated UUID
        this.setUUID(uuid);

        // Return generated UUID
        return uuid;
    }

    /**
     * Returns a string representation of this Component including parent
     * and children Component references.
     *
     * @return a Full String representation of this Component
     */
    @Override
    public String toString() {
        try { return ParserComponent.OM.writerWithDefaultPrettyPrinter().writeValueAsString(this); }
        catch (JsonProcessingException e) { return "Component[" + this.hashCode() + "]"; }
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
