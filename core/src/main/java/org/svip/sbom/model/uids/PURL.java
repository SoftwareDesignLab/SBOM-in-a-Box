package org.svip.sbom.model.uids;

import jregex.Matcher;
import jregex.Pattern;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

/**
 * <b>File</b>: PURL.java<br>
 * <b>Description</b>: Object representation of the Package URl for a component
 * Representation details can be found here: <a href="https://github.com/package-url/purl-spec/tree/master">...</a>
 *
 * @author Juan Francisco Patino
 * @author Matt London
 * @author Derek Garcia
 */
public class PURL {

    // Purl scheme: scheme:type/namespace/name@version?qualifiers#subpath
    private static final String PURL_REGEX =
            "(.*?):" +      // Get scheme
            "(?:/*)" +    // Skip over any/all '/' characters
            "([\\w\\d.+\\-]*)/" +                     // Get type
            "(?(?=[\\w\\.\\+\\-\\%^@#?]*\\/)(.*?)\\/)" +    // Get namespaces if present
            "([\\w.+\\-%]*)" +    //  Get name
            "(?(?=.*@.*(?:\\?|#|$))@(.*?)(?=\\?|#|$))" +    // Get version, if present
            "(?(?=.*\\?.*(?:#|$))\\?(.*?)(?=#|$))" +        // Get qualifiers if present
            "(?(?=.*#.*$)#(.*?)$)";     // Get subpath, if present

    private final String scheme;  // required
    private final String type;    // required
    private List<String> namespace;   // Optional and type-specific
    private final String name;    // required
    private final String version; // Optional
    private LinkedHashMap<String, String> qualifiers = null;    // Optional
    private final String subpath; // Optional

    /**
     * Create new purl object from a given purl identifier string
     *
     * @param purl Purl string to use to make objects
     * @throws Exception purl given is invalid
     */
    public PURL(String purl) throws Exception {
        Pattern purlPattern = new Pattern(PURL_REGEX, Pattern.MULTILINE);

        Matcher matcher = purlPattern.matcher(purl);

        // Regex fails to match to string
        if(!matcher.find())
            throw new Exception("Unable to parse purl \"" + purl + "\"");

        // Check for required fields
        if(matcher.group(1) == null || matcher.group(2) == null || matcher.group(4) == null){
            throw new Exception("Invalid purl, missing the following: "+
                    ( matcher.group(1) == null ? "Schema " : "" ) +
                    ( matcher.group(2) == null ? "Type " : "" ) +
                    ( matcher.group(4) == null ? "Name " : "" )
            );
        }

        // Build purl object
        this.scheme = matcher.group(1);
        this.type = matcher.group(2);
        if(matcher.group(3) != null)
            this.namespace = Arrays.stream(matcher.group(3).split("/")).toList();   // can be 0 - n namespaces
        this.name = matcher.group(4);
        this.version = matcher.group(5);

        // Build qualifiers if present
        if(matcher.group(6) != null){
            this.qualifiers = new LinkedHashMap<>();
            // Add all key=value pairs for the quantifier
            for(String qualifier : matcher.group(6).split("&")){
                String[] keyVal = qualifier.split("=");
                this.qualifiers.put(keyVal[0], keyVal[1]);
            }
        }

        this.subpath = matcher.group(7);
    }

    ///
    /// Getters
    ///

    public String getType() {
        return type;
    }

    public List<String> getNamespace(){
        return this.namespace;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public LinkedHashMap<String, String> getQualifiers() {
        return qualifiers;
    }

    ///
    /// Overrides
    ///

    @Override
    public String toString() {
        // scheme:type/namespace/name@version?qualifiers#subpath
        // Build namespaces
        StringBuilder namespace = new StringBuilder();
        if(this.namespace != null)
            for(String n : this.namespace)
                namespace.append("/").append(n);

        // Build qualifiers
        StringBuilder qualifiers = new StringBuilder();
        if(this.qualifiers != null){
            for(String key : this.qualifiers.keySet())
                qualifiers.append(key).append("=").append(this.qualifiers.get(key)).append("&");
            qualifiers.deleteCharAt(qualifiers.length() - 1);   // truncate last '&'
        }

        // build final purl
        return this.scheme + ":"
                + this.type +
                namespace +
                "/" + this.name +
                (this.version != null ? "@" + this.version : "") +
                (!qualifiers.isEmpty() ? "?" + qualifiers : "") +
                (this.subpath != null ? "#" + this.subpath : "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PURL purl = (PURL) o;

        if (!scheme.equals(purl.scheme)) return false;
        if (!type.equals(purl.type)) return false;
        if (!Objects.equals(namespace, purl.namespace)) return false;
        if (!name.equals(purl.name)) return false;
        if (!Objects.equals(version, purl.version)) return false;
        if (!Objects.equals(qualifiers, purl.qualifiers)) return false;
        return Objects.equals(subpath, purl.subpath);
    }

    @Override
    public int hashCode() {
        int result = scheme.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + (namespace != null ? namespace.hashCode() : 0);
        result = 31 * result + name.hashCode();
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (qualifiers != null ? qualifiers.hashCode() : 0);
        result = 31 * result + (subpath != null ? subpath.hashCode() : 0);
        return result;
    }
}