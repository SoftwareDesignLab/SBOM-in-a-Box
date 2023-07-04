package org.svip.builders.component;

import org.svip.builders.component.interfaces.CycloneDX14.CDX14PackageBuilder_I;
import org.svip.sbom.model.interfaces.generics.Component;
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
    private LicenseCollection licenses;

    /**Component copyright*/
    private String copyright;

    /**Component hashes*/
    private HashMap<String, String> hashes;

    /**Component supplier*/
    private Organization supplier;

    /**Component version*/
    private String version;

    /**Component description*/
    private Description description;

    /**Component CPEs*/
    private Set<String> cpes;

    /**Component PURLs*/
    private Set<String> purls;

    /**Component external references*/
    private Set<ExternalReference> externalReferences;

    /**Component mime type*/
    private String mimeType;

    /**Component publisher*/
    private String publisher;

    /**Component scope*/
    private String scope;

    /**Component group*/
    private String group;

    /**Component properties*/
    private HashMap<String, Set<String>> properties;

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
        if (this.properties == null)
            this.properties = new HashMap<>();

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

    //TODO implement after SBOM refactor
    /**
     * Build a CDX14PackageObject
     * @return a Component
     */
    @Override
    public Component build() {
        return null;
    }

    //TODO implement after SBOM refactor
    /**
     * Build and flush a CDX14PackageObject
     * @return a Component
     */
    @Override
    public Component buildAndFlush() {
        return null;
    }
}
