/**
 * Copyright 2021 Rochester Institute of Technology (RIT). Developed with
 * government support under contract 70RCSA22C00000008 awarded by the United
 * States Department of Homeland Security for Cybersecurity and Infrastructure Security Agency.
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the “Software”), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.svip.sbom.model.objects.CycloneDX14;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.svip.compare.conflicts.Conflict;
import org.svip.compare.conflicts.ConflictFactory;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOMPackage;
import org.svip.sbom.model.interfaces.schemas.CycloneDX14.CDX14Package;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.shared.util.Description;
import org.svip.sbom.model.shared.util.ExternalReference;
import org.svip.sbom.model.shared.util.LicenseCollection;
import org.svip.serializers.serializer.v2.CycloneDX14.custom.CDX14HashesSerializer;
import org.svip.serializers.serializer.v2.CycloneDX14.custom.CDX14LicensesSerializer;
import org.svip.serializers.serializer.v2.CycloneDX14.custom.CDX14PropertiesSerializer;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.svip.compare.conflicts.MismatchType.*;
import static org.svip.utils.NullOrEmpty.isNullOrEmpty;

/**
 * file: CDX14ComponentObject.java
 * Holds information for a CycloneDX 1.4 component object
 *
 * @author Derek Garcia
 * @author Matthew Morrison
 */
// todo - release notes
@JsonPropertyOrder({"type", "mime-type", "bom-ref", "supplier", "author", "publisher",  "group", "name", "version", "description", "scope",  "hashes", "licenses", "copyright", "cpe", "purl", "externalReferences", "properties"})
public class CDX14ComponentObject implements CDX14Package {

    /**
     * Component's type
     */
    private final String type;

    /**
     * Component's uid
     */
    private final String uid;

    /**
     * Component's author
     */
    private final String author;

    /**
     * Component's name
     */
    private final String name;

    /**
     * Component's licenses
     */
    private final LicenseCollection licenses;

    /**
     * Component's copyright
     */
    private final String copyright;

    /**
     * Component's hashes
     */
    private final Map<String, String> hashes;

    /**
     * Component's supplier
     */
    private final Organization supplier;

    /**
     * Component's version
     */
    private final String version;

    /**
     * Component's description
     */
    private final Description description;

    /**
     * Component's CPEs
     */
    private final Set<String> cpes;

    /**
     * Component's PURLs
     */
    private final Set<String> purls;

    /**
     * Component's mime type
     */
    private final String mimeType;

    /**
     * Component's publisher
     */
    private final String publisher;

    /**
     * Component's scope
     */
    private final String scope;

    /**
     * Component's group
     */
    private final String group;

    /**
     * Component's external references
     */
    private final Set<ExternalReference> externalReferences;

    /**
     * Component's properties
     */
    private final HashMap<String, Set<String>> properties;

    /**
     * Constructor to build a new CDX 1.4 Component Object
     *
     * @param type               component type
     * @param uid                component uid
     * @param author             component author
     * @param name               component name
     * @param licenses           component licenses
     * @param copyright          component copyright
     * @param hashes             component hashes
     * @param supplier           component supplier
     * @param version            component version
     * @param description        component description
     * @param cpes               component CPEs
     * @param purls              component PURLs
     * @param mimeType           component mime type
     * @param publisher          component publisher
     * @param scope              component scope
     * @param group              component group
     * @param externalReferences component external references
     * @param properties         component properties
     */
    public CDX14ComponentObject(String type, String uid, String author, String name,
                                LicenseCollection licenses, String copyright,
                                HashMap<String, String> hashes, Organization supplier,
                                String version, Description description, Set<String> cpes,
                                Set<String> purls, String mimeType, String publisher,
                                String scope, String group, Set<ExternalReference> externalReferences,
                                HashMap<String, Set<String>> properties) {
        this.type = type;
        this.uid = uid;
        this.author = author;
        this.name = name;
        this.licenses = licenses;
        this.copyright = copyright;
        this.hashes = hashes;
        this.supplier = supplier;
        this.version = version;
        this.description = description;
        this.cpes = cpes;
        this.purls = purls;
        this.mimeType = mimeType;
        this.publisher = publisher;
        this.scope = scope;
        this.group = group;
        this.externalReferences = externalReferences;
        this.properties = properties;

    }

    /**
     * Get the component's type
     *
     * @return the component's type
     */
    @Override
    @JsonProperty("type")
    public String getType() {
        return this.type;
    }

    /**
     * Get the component's uid
     *
     * @return the component's uid
     */
    @Override
    @JsonProperty("bom-ref")
    public String getUID() {
        return this.uid;
    }

    /**
     * Get the component's author
     *
     * @return the component's author
     */
    @Override
    @JsonProperty("author")
    public String getAuthor() {
        return this.author;
    }

    /**
     * Get the component's name
     *
     * @return the component's name
     */
    @Override
    @JsonProperty("name")
    public String getName() {
        return this.name;
    }

    /**
     * Get the component's licenses
     *
     * @return the component's licenses
     */
    @Override
    @JsonProperty("licenses")
    @JsonSerialize(using = CDX14LicenseCollectionSerializer.class)
    public LicenseCollection getLicenses() {
        return this.licenses;
    }

    /**
     * Get the component's copyright info
     *
     * @return the component's copyright info
     */
    @Override
    @JsonProperty("copyright")
    public String getCopyright() {
        return this.copyright;
    }

    /**
     * Get the component's hashes
     *
     * @return the component's hashes
     */
    @Override
    @JsonProperty("hashes")
    @JsonSerialize(using = CDX14HashesSerializer.class)
    public Map<String, String> getHashes() {
        return this.hashes;
    }

    /**
     * Get the component's supplier
     *
     * @return The component's supplier
     */
    @Override
    @JsonProperty("supplier")
    public Organization getSupplier() {
        return this.supplier;
    }

    /**
     * Get the component's version
     *
     * @return the component's version
     */
    @Override
    @JsonProperty("version")
    public String getVersion() {
        return this.version;
    }

    /**
     * Get the component's description
     *
     * @return the component's description
     */
    @Override
    @JsonProperty("description")
    @JsonSerialize(using = CDX14DescriptionSerializer.class)
    public Description getDescription() {
        return this.description;
    }

    /**
     * Get the component's CPEs
     *
     * @return the component's CPEs
     */
    @Override
    @JsonIgnore
    public Set<String> getCPEs() {
        return this.cpes;
    }

    /**
     * Get the component's PURLs
     *
     * @return the component's PURLs
     */
    @Override
    @JsonIgnore
    public Set<String> getPURLs() {
        return this.purls;
    }

    /**
     * Get the component's external references
     *
     * @return the component's external references
     */
    @Override
    @JsonProperty("externalReferences")
    public Set<ExternalReference> getExternalReferences() {
        return this.externalReferences;
    }

    /**
     * Get the component's mime type
     *
     * @return the component's mime type
     */
    @Override
    @JsonProperty("mime-type")
    public String getMimeType() {
        return this.mimeType;
    }

    /**
     * Get the component's publisher
     *
     * @return the component's publisher
     */
    @Override
    @JsonProperty("publisher")
    public String getPublisher() {
        return this.publisher;
    }

    /**
     * Get the component's scope
     *
     * @return the component's scope
     */
    @Override
    @JsonProperty("scope")
    public String getScope() {
        return this.scope;
    }

    /**
     * Get the component's group
     *
     * @return the component's group
     */
    @Override
    @JsonProperty("group")
    public String getGroup() {
        return this.group;
    }

    /**
     * Get the component's properties
     *
     * @return the component's properties
     */
    @Override
    @JsonProperty("properties")
    @JsonSerialize(using = CDX14PropertiesSerializer.class)
    public HashMap<String, Set<String>> getProperties() {
        return this.properties;
    }

    /**
     * Compare against another generic component
     *
     * @param other Other Component to compare against
     * @return List of conflicts
     */
    @Override
    public List<Conflict> compare(Component other) {
        ConflictFactory cf = new ConflictFactory();

        // Type
        cf.addConflict("Type", MISC_MISMATCH, this.type, other.getType());

        // UID
        cf.addConflict("UID", MISC_MISMATCH, this.uid, other.getUID());

        // NAME
        // shouldn't occur
        cf.addConflict("Name", NAME_MISMATCH, this.name, other.getName());

        // AUTHOR
        cf.addConflict("Author", AUTHOR_MISMATCH, this.author, other.getAuthor());

        // Licenses
        if (cf.comparable("License", this.licenses, other.getLicenses()))
            cf.addConflicts(this.licenses.compare(other.getLicenses()));

        // Copyright
        cf.addConflict("Copyright", MISC_MISMATCH, this.copyright, other.getCopyright());

        // Hashes
        cf.compareHashes("Component Hash", this.hashes, other.getHashes());

        // Compare SBOMPackage specific fields
        if (other instanceof SBOMPackage)
            cf.addConflicts(compare((SBOMPackage) other));

        return cf.getConflicts();
    }

    /**
     * Compare against another generic SBOM Package
     *
     * @param other Other SBOM Package to compare against
     * @return List of conflicts
     */
    public List<Conflict> compare(SBOMPackage other) {
        ConflictFactory cf = new ConflictFactory();

        // Supplier
        if (cf.comparable("Supplier", this.supplier, other.getSupplier()))
            cf.addConflicts(this.supplier.compare(other.getSupplier()));

        // Version
        // shouldn't occur
        cf.addConflict("Version", VERSION_MISMATCH, this.version, other.getVersion());

        // Description
        if (cf.comparable("Description", this.description, other.getDescription()))
            cf.addConflicts(this.description.compare(other.getDescription()));

        // PURLs
        // todo use util PURL objects?
        cf.compareStringSets("PURL", PURL_MISMATCH, this.purls, other.getPURLs());

        // CPEs
        // todo use util CPE objects?
        cf.compareStringSets("CPE", CPE_MISMATCH, this.cpes, other.getCPEs());

        // External References
        cf.compareComparableSets("External Reference", new HashSet<>(this.externalReferences), new HashSet<>(other.getExternalReferences()));

        // Compare CDX14SBOMPackage specific fields
        if (other instanceof CDX14Package)
            cf.addConflicts(compare((CDX14Package) other));

        return cf.getConflicts();
    }


    /**
     * Compare against another CycloneDX 1.4 Package
     *
     * @param other Other CycloneDX 1.4 Package to compare against
     * @return List of conflicts
     */
    @Override
    public List<Conflict> compare(CDX14Package other) {
        ConflictFactory cf = new ConflictFactory();
        // Mime Type
        cf.addConflict("Mime Type", MISC_MISMATCH, this.mimeType, other.getMimeType());

        // Publisher
        cf.addConflict("Publisher", PUBLISHER_MISMATCH, this.publisher, other.getPublisher());

        // Scope
        cf.addConflict("Scope", MISC_MISMATCH, this.scope, other.getScope());

        // Group
        cf.addConflict("Group", MISC_MISMATCH, this.group, other.getGroup());

        // todo
        // properties

        return cf.getConflicts();
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof CDX14Package cdx14Package)) return false;
        return Objects.equals(this.name, cdx14Package.getName())
                && Objects.equals(this.version, cdx14Package.getVersion());
    }

    @Override
    public int hashCode() {
        return this.name.hashCode() + (this.version != null ? this.version.hashCode() : 0);
    }

    /**
     * Internal serializer to join uids
     * TODO is this the right way to represent multiple CPEs/PURLs?
     * TODO - add extras as external refs
     */
    @JsonProperty("cpe")
    public String getCPEsAsString() {
        if (isNullOrEmpty(cpes)) return null;
        return String.join(", ", cpes);
    }

    /**
     * Internal serializer to join uids
     * TODO is this the right way to represent multiple CPEs/PURLs?
     * TODO - add extras as external refs
     */
    @JsonProperty("purl")
    public String getPURLsAsString() {
        if (isNullOrEmpty(purls)) return null;
        return String.join(", ", purls);
    }


    /**
     * Internal serializer to just get summary of a description
     */
    static class CDX14DescriptionSerializer extends JsonSerializer<Description> {
        @Override
        public void serialize(Description description, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            // skip if no data
            if (isNullOrEmpty(description)) return;
            jsonGenerator.writeString(description.toString());
        }
    }

    /**
     * Internal serializer to join license types
     */
    static class CDX14LicenseCollectionSerializer extends JsonSerializer<LicenseCollection> {

        @Override
        public void serialize(LicenseCollection licenseCollection, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            // skip if no data
            if (isNullOrEmpty(licenseCollection)) return;

            // get all licenses, remove null, and convert to set
            Set<String> allLicenses = Stream.of(
                            licenseCollection.getConcluded(),
                            licenseCollection.getDeclared(),
                            licenseCollection.getInfoFromFiles()
                    )
                    .filter(Objects::nonNull)
                    .flatMap(Set::stream)
                    .collect(Collectors.toSet());
            // use existing serializer to write license
            new CDX14LicensesSerializer().serialize(allLicenses, jsonGenerator, serializerProvider);
        }
    }

}

