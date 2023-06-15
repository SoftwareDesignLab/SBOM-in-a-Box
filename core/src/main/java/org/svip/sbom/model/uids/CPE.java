package org.svip.sbom.model.uids;

import jregex.Matcher;
import jregex.Pattern;
import org.svip.sbomfactory.generators.utils.Debug;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * file: CPE.java
 *
 * Class representation of a CPE
 * Specifications can be found in NIST's Common Platform Enumeration: Naming Specification Version 2.3
 * <a href="https://nvlpubs.nist.gov/nistpubs/Legacy/IR/nistir7695.pdf">...</a>
 *
 * @author Derek Garcia
 * @author Ian Dunn
 */
public class CPE {


    /*
    CPE's conform to the following structure:
    cpe:<cpe_version>:<part>:<vendor>:<product>:<version>:<update>:<edition>:<language>:<sw_edition>:<target_sw>:<target_hw>:<other>
     TODO: Only temporary regex, this has no built in string value checking and only for 2.3
     */
    private static final String CPE_REGEX = "cpe:2\\.3:([aho]?):(.*?):(.*?):(.*?):(.*?):(.*?):(.*?):(.*?):(.*?):(.*?):(.*?)$";
    private static final String WILDCARD = "*";
    private static final String WILDCARD_REGEX = ".*";

    // Supported types for CPE
    public enum Type{
        APPLICATION,
        HARDWARE,
        OPERATING_SYSTEMS
    }

    // todo required fields?
    private final String cpeVersion = "2.3";    // todo fix to support more versions
    private final Type part;

    // "Values for this attribute SHOULD describe or identify the person or organization that manufactured or created the product."
    private String vendor;

    // "Values for this attribute SHOULD describe or identify the most common and recognizable title or name of the product."
    private String product;

    // "Values for this attribute SHOULD be vendor-specific alphanumeric strings characterizing the particular release
    // version of the product."
    private String version;

    // "Values for this attribute SHOULD be vendor-specific alphanumeric strings characterizing the particular update,
    // service pack, or point release of the product."
    private String update;

    // "The edition attribute is considered deprecated in [the 2.3] specification, and it SHOULD be assigned the logical
    // value ANY except where required for backward compatibility with version 2.2 of the CPE specification. "
    private String edition;

    // "Values for this attribute SHALL be valid language tags as defined by [RFC5646], and SHOULD be used to define the
    // language supported in the user interface of the product being described"
    private String language;

    // "Values for this attribute SHOULD characterize how the product is tailored to a particular market or class
    //of end users."
    private String sw_edition;

    // "Values for this attribute SHOULD characterize the software computing environment within which the product operates"
    private String target_sw;

    // "Values for this attribute SHOULD characterize the instruction set architecture (e.g., x86) on which the
    // product being described or identified by the WFN operates"
    private String target_hw;

    // "Values for this attribute SHOULD capture any other general descriptive or identifying information which
    // is vendor- or product-specific and which does not logically fit in any other attribute value."
    private String other;


    /**
     * Create new cpe object from a given cpe identifier string
     *
     * @param cpe cpe string to use to make objects
     * @throws Exception cpe given is invalid
     */
    public CPE(String cpe) throws Exception {
        // Build regex
        Pattern cpePattern = new Pattern(CPE_REGEX, Pattern.MULTILINE);
        Matcher matcher = cpePattern.matcher(cpe);

        // Regex fails to match to string
        if(!matcher.find()){
            Debug.log(Debug.LOG_TYPE.DEBUG, "Unable to parse cpe \"" + cpe + "\"");
            throw new Exception("Unable to parse cpe \"" + cpe + "\"");
        }

        // Check for missing fields
        if(Arrays.stream(matcher.groups()).toList().contains(null)){
            throw new Exception("Invalid CPE, missing the following: "+
                    ( matcher.group(1) == null ? "Type " : "" ) +
                    ( matcher.group(2) == null ? "Vendor " : "" ) +
                    ( matcher.group(3) == null ? "Product " : "" ) +
                    ( matcher.group(4) == null ? "Version " : "" ) +
                    ( matcher.group(5) == null ? "Update " : "" ) +
                    ( matcher.group(6) == null ? "Edition " : "" ) +
                    ( matcher.group(7) == null ? "Language " : "" ) +
                    ( matcher.group(8) == null ? "sw_edition " : "" ) +
                    ( matcher.group(9) == null ? "target_sw " : "" ) +
                    ( matcher.group(10) == null ? "target_hw " : "" ) +
                    ( matcher.group(11) == null ? "other " : "" )
            );
        }

        // get type
        switch (matcher.group(1)) {
            case "a" -> this.part = Type.APPLICATION;
            case "h" -> this.part = Type.HARDWARE;
            case "o" -> this.part = Type.OPERATING_SYSTEMS;
            default -> throw new Exception("\"" + matcher.group(1) + "\" is not a valid type");
        }

        // Fill in fields
        this.vendor = matcher.group(2);
        this.product = matcher.group(3);
        this.version = matcher.group(4);
        this.update = matcher.group(5);
        this.edition = matcher.group(6);
        this.language = matcher.group(7);
        this.sw_edition = matcher.group(8);
        this.target_sw = matcher.group(9);
        this.target_hw = matcher.group(10);
        this.other = matcher.group(11);
    }

    /**
     * Create a new CPE object from a given vendor and product.
     *
     * @param vendor
     * @param product
     */
    public CPE(String vendor, String product) {
        this.part = Type.APPLICATION;
        this.vendor = vendor;
        this.product = product;
    }

    /**
     * Create a new CPE object from a given vendor, product, and version.
     *
     * @param vendor
     * @param product
     * @param version
     */
    public CPE(String vendor, String product, String version) {
        this(vendor, product);
        this.version = version;
    }

    /**
     * Compares 2 strings to test for equivalence and accounts for wildcard characters
     *
     * @param a String a
     * @param b String b
     * @return if a and b are equivalent
     */
    public static boolean isEqualWildcard(String a, String b){
        // Check for direct equivalence first
        if(a.equals(b))
            return true;

        String wildCardString = "";
        String other = "";

        // Get wildcard and target string
        if(a.contains(WILDCARD)){
            wildCardString = a;
            other = b;
        } else {
            wildCardString = b;
            other = a;
        }

        // Convert wildCardString to regex
        String regex = wildCardString.replace(WILDCARD, WILDCARD_REGEX);
        Pattern pattern = new Pattern(regex, Pattern.MULTILINE);

        // Test regex
        return pattern.matches(other);
    }

    ///
    /// Getters
    ///

    public String getCpeVersion() {
        return cpeVersion;
    }

    public Type getPart() {
        return part;
    }

    public String getVendor() {
        return vendor;
    }

    public String getProduct() {
        return product;
    }

    public String getVersion() {
        return version;
    }

    public String getUpdate() {
        return update;
    }

    public String getEdition() {
        return edition;
    }

    public String getLanguage() {
        return language;
    }

    public String getSw_edition() {
        return sw_edition;
    }

    public String getTarget_sw() {
        return target_sw;
    }

    public String getTarget_hw() {
        return target_hw;
    }

    public String getOther() {
        return other;
    }
    @Override
    public boolean equals(Object o) {
        // Check if same object
        if (this == o)
            return true;

        // Check if null or different class
        if (o == null || getClass() != o.getClass())
            return false;

        // Compare CPE strings to check for equivalence
        return isEqualWildcard(this.toString(), o.toString());
    }

    @Override
    public String toString() {
        // cpe:<cpe_version>:<part>:<vendor>:<product>:<version>:<update>:<edition>:<language>:<sw_edition>:<target_sw>:<target_hw>:<other>

        // Get application string
        String typeString = "";
        switch (this.part) {
            case APPLICATION -> typeString = "a";
            case HARDWARE -> typeString = "h";
            case OPERATING_SYSTEMS -> typeString = "o";
        }

        List<String> values = new ArrayList<>(Stream.of(
                "cpe",
                cpeVersion,
                typeString,
                vendor,
                product,
                version,
                update,
                edition,
                language,
                sw_edition,
                target_sw,
                target_hw,
                other
        ).toList());

        values = values.stream().map(v -> {
            if(v == null) v = "ANY";
            return bindValueForFS(v);
        }).collect(Collectors.toList());

        return String.join(":", values);
    }

    /**
     * Binds a value to its "safe" characters for a format string. Replaces "ANY" & "NA" wildcards with "*" & "-",
     * respectively.
     *
     * @param value
     * @return
     */
    private String bindValueForFS(String value) {
        switch(value) {
            case "ANY" -> { return "*"; }
            case "NA" -> { return "-"; }
            default -> { return processQuotedChars(value); }
        }
    }

    /**
     * Processes quoted characters to construct a CPE format string.
     *
     * @param value
     * @return The formatted quoted characters string.
     */
    private String processQuotedChars(String value) {
        String formattedValue = value.trim().replaceAll(" ", "_");
        StringBuilder processed = new StringBuilder();

        int idx = 0;
        while(idx < value.length()) {
            char c = value.charAt(idx);
            if(c != '\\') {
                processed.append(c);
            } else {
                char next = value.charAt(idx + 1);
                if(next == '.' || next == '-' || next == '_') {
                    processed.append(next);
                } else {
                    processed.append('\\').append(next);
                }
                idx += 2;
            }

            idx++;
        }

        return processed.toString();
    }
}
