package org.svip.sbom.builder.objects.schemas.CDX14;

import org.svip.sbom.builder.interfaces.schemas.CycloneDX14.CDX14PackageBuilder_I;
import org.svip.sbom.model.objects.CycloneDX14.CDX14ComponentObject;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.shared.util.Description;
import org.svip.sbom.model.shared.util.ExternalReference;
import org.svip.sbom.model.shared.util.LicenseCollection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * file: CDX14PackageBuilder.java
 * Builder class for CycloneDX 1.4 Packages
 *
 * @author Matthew Morrison
 * @author Thomas Roman
 */
public class CDX14PackageBuilder implements CDX14PackageBuilder_I {


    /**Component type*/
    private String type;

    /**Component uid*/
    private String uid;

    /**Component author*/
    private String author;

    /**Component name*/
    private String name;

    /**Component licenses*/
    private LicenseCollection licenses = new LicenseCollection();

    /**Component copyright*/
    private String copyright;

    /**Component hashes*/
    private HashMap<String, String> hashes = new HashMap<>();

    /**Component supplier*/
    private Organization supplier;

    /**Component version*/
    private String version;

    /**Component description*/
    private Description description;

    /**Component CPEs*/
    private Set<String> cpes = new HashSet<>();

    /**Component PURLs*/
    private Set<String> purls = new HashSet<>();

    /**Component external references*/
    private Set<ExternalReference> externalReferences = new HashSet<>();

    /**Component mime type*/
    private String mimeType;

    /**Component publisher*/
    private String publisher;

    /**Component scope*/
    private String scope;

    /**Component group*/
    private String group;

    /**Component properties*/
    private HashMap<String, Set<String>> properties = new HashMap<>();

    /**
     * Set the component's mime type
     * @param mimeType the package's mime type
     * @return a CDX14PackageBuilder
     */
    @Override
    public CDX14PackageBuilder setMimeType(String mimeType) {
        this.mimeType = mimeType;
        return this;
    }

    /**
     * Set the component's publisher
     * @param publisher the package's publisher
     * @return a CDX14PackageBuilder
     */
    @Override
    public CDX14PackageBuilder setPublisher(String publisher) {
        this.publisher = publisher;
        return this;
    }

    /**
     * Set the component's scope
     * @param scope the package's scope
     * @return a CDX14PackageBuilder
     */
    @Override
    public CDX14PackageBuilder setScope(String scope) {
        this.scope = scope;
        return this;
    }

    /**
     * Set the component's group
     * @param group the package's group
     * @return a CDX14PackageBuilder
     */
    @Override
    public CDX14PackageBuilder setGroup(String group) {
        this.group = group;
        return this;
    }

    /**
     * Add an external references to the component
     * @param externalReference a package's external reference
     * @return a CDX14PackageBuilder
     */
    @Override
    public CDX14PackageBuilder addExternalReferences(ExternalReference externalReference) {
        this.externalReferences.add(externalReference);
        return this;
    }

    /**
     * Add a property to the component
     * @param name the name of the property
     * @param value the value of the property
     * @return a CDX14PackageBuilder
     */
    @Override
    public CDX14PackageBuilder addProperty(String name, String value) {
        if( !this.properties.containsKey(name))
            this.properties.put(name, new HashSet<>());

        this.properties.get(name).add(value);

        return this;
    }

    /**
     * Set the component's type
     * @param type the designated type of component
     * @return a CDX14PackageBuilder
     */
    @Override
    public CDX14PackageBuilder setType(String type) {
        this.type = type;
        return this;
    }

    /**
     * Set the component's uid
     * @param uid the uid of the component
     * @return a CDX14PackageBuilder
     */
    @Override
    public CDX14PackageBuilder setUID(String uid) {
        this.uid = uid;
        return this;
    }

    /**
     * Set the component's author
     * @param author the author of the component
     * @return a CDX14PackageBuilder
     */
    @Override
    public CDX14PackageBuilder setAuthor(String author) {
        this.author = author;
        return this;
    }

    /**
     * Set the component's name
     * @param name the name of the component
     * @return a CDX14PackageBuilder
     */
    @Override
    public CDX14PackageBuilder setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Set the component's licenses
     * @param licenses a collection of licenses
     * @return a CDX14PackageBuilder
     */
    @Override
    public CDX14PackageBuilder setLicenses(LicenseCollection licenses) {
        this.licenses = licenses;
        return this;
    }

    /**
     * Set the component's copyright info
     * @param copyright the copyright info of the component
     * @return a CDX14PackageBuilder
     */
    @Override
    public CDX14PackageBuilder setCopyright(String copyright) {
        this.copyright = copyright;
        return this;
    }

    /**
     * Add a hash value to the component
     * @param algorithm the algorithm of the hash
     * @param hash the value of the hash
     * @return a CDX14PackageBuilder
     */
    @Override
    public CDX14PackageBuilder addHash(String algorithm, String hash) {
        this.hashes.put(algorithm, hash);
        return this;
    }

    /**
     * Set the component's supplier
     * @param supplier the component's supplier
     * @return a CDX14PackageBuilder
     */
    @Override
    public CDX14PackageBuilder setSupplier(Organization supplier) {
        this.supplier = supplier;
        return this;
    }

    /**
     * Set the component's version
     * @param version the component's version
     * @return a CDX14PackageBuilder
     */
    @Override
    public CDX14PackageBuilder setVersion(String version) {
        this.version = version;
        return this;
    }

    /**
     * Set the component's description
     * @param description the component's description
     * @return a CDX14PackageBuilder
     */
    @Override
    public CDX14PackageBuilder setDescription(Description description) {
        this.description = description;
        return this;
    }

    /**
     * Add a cpe to the component
     * @param cpe the cpe string to add
     * @return a CDX14PackageBuilder
     */
    @Override
    public CDX14PackageBuilder addCPE(String cpe) {
        this.cpes.add(cpe);
        return this;
    }

    /**
     * Add a purl to the component
     * @param purl the purl string to add
     * @return a CDX14PackageBuilder
     */
    @Override
    public CDX14PackageBuilder addPURL(String purl) {
        this.purls.add(purl);
        return this;
    }

    /**
     * Add an external reference to the component
     * @param externalReference the external component to add
     * @return a CDX14PackageBuilder
     */
    @Override
    public CDX14PackageBuilder addExternalReference(ExternalReference externalReference) {
        this.externalReferences.add(externalReference);
        return this;
    }

    /**
     * Build a CDX14ComponentObject
     * @return a Component
     */
    @Override
    public CDX14ComponentObject build() {
        return new CDX14ComponentObject(type, uid, author, name,
                licenses, copyright, hashes, supplier, version, description, cpes,
                purls, mimeType, publisher, scope, group,
                externalReferences, properties);
    }

    //TODO implement after SBOM refactor
    /**
     * Build and flush a CDX14ComponentObject
     * @return a Component
     */
    @Override
    public CDX14ComponentObject buildAndFlush() {
        // build the component
        CDX14ComponentObject component = build();
        
        // clear all the data in the builder
        this.type = null;
        this.uid = null;
        this.author = null;
        this.name = null;
        this.licenses = null;
        this.copyright = null;
        this.hashes = new HashMap<>();
        this.supplier = null;
        this.version = null;
        this.description = null;
        this.cpes = new HashSet<>();
        this.purls = new HashSet<>();
        this.mimeType = null;
        this.publisher = null;
        this.scope = null;
        this.group = null;
        this.externalReferences = new HashSet<>();
        this.properties = new HashMap<>();
        return component;
    }
}
