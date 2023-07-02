package org.svip.sbom.model.objects.CycloneDX14;

import org.svip.sbom.model.interfaces.schemas.CycloneDX14.CDX14Package;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.shared.util.Description;
import org.svip.sbom.model.shared.util.ExternalReference;
import org.svip.sbom.model.shared.util.LicenseCollection;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * file: CDX14ComponentObject.java
 * Holds information for a single CycloneDX 1.4 component object
 *
 * @author Derek Garcia
 * @author Matthew Morrison
 */
public class CDX14ComponentObject implements CDX14Package {

    /**Component's type*/
    private final String type;

    /**Component's uid*/
    private final String uid;

    /**Component's author*/
    private final String author;

    /**Component's name*/
    private final String name;

    /**Component's licenses*/
    private final LicenseCollection licenses;

    /**Component's copyright*/
    private final String copyright;

    /**Component's hashes*/
    private final HashMap<String, String> hashes;

    /**Component's supplier*/
    private final Organization supplier;

    /**Component's version*/
    private final String version;

    /**Component's description*/
    private final Description description;

    /**Component's CPEs*/
    private final Set<String> cpes;

    /**Component's PURLs*/
    private final Set<String> purls;

    /**Component's mime type*/
    private final String mimeType;

    /**Component's publisher*/
    private final String publisher;

    /**Component's scope*/
    private final String scope;

    /**Component's group*/
    private final String group;

    /**Component's external references*/
    private final Set<ExternalReference> externalReferences;

    /**Component's properties*/
    private final HashMap<String, Set<String>> properties;

    /**
     * Get the component's type
     * @return the component's type
     */
    @Override
    public String getType() {
        return this.type;
    }

    /**
     * Get the component's uid
     * @return the component's uid
     */
    @Override
    public String getUID() {
        return this.uid;
    }

    /**
     * Get the component's author
     * @return the component's author
     */
    @Override
    public String getAuthor() {
        return this.author;
    }

    /**
     * Get the component's name
     * @return the component's name
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Get the component's licenses
     * @return the component's licenses
     */
    @Override
    public LicenseCollection getLicenses() {
        return this.licenses;
    }

    /**
     * Get the component's copyright info
     * @return the component's copyright info
     */
    @Override
    public String getCopyright() {
        return this.copyright;
    }

    /**
     * Get the component's hashes
     * @return the component's hashes
     */
    @Override
    public Map<String, String> getHashes() {
        return this.hashes;
    }

    /**
     * Get the component's supplier
     * @return The component's supplier
     */
    @Override
    public Organization getSupplier() {
        return this.supplier;
    }

    /**
     * Get the component's version
     * @return the component's version
     */
    @Override
    public String getVersion() {
        return this.version;
    }

    /**
     * Get the component's description
     * @return the component's description
     */
    @Override
    public Description getDescription() {
        return this.description;
    }

    /**
     * Get the component's CPEs
     * @return the component's CPEs
     */
    @Override
    public Set<String> getCPEs() {
        return this.cpes;
    }

    /**
     * Get the component's PURLs
     * @return the component's PURLs
     */
    @Override
    public Set<String> getPURLs() {
        return this.purls;
    }

    /**
     * Get the component's external references
     * @return the component's external references
     */
    @Override
    public Set<ExternalReference> getExternalReferences() {
        return this.externalReferences;
    }

    /**
     * Get the component's mime type
     * @return the component's mime type
     */
    @Override
    public String getMimeType() {
        return this.mimeType;
    }

    /**
     * Get the component's publisher
     * @return the component's publisher
     */
    @Override
    public String getPublisher() {
        return this.publisher;
    }

    /**
     * Get the component's scope
     * @return the component's scope
     */
    @Override
    public String getScope() {
        return this.scope;
    }

    /**
     * Get the component's group
     * @return the component's group
     */
    @Override
    public String getGroup() {
        return this.group;
    }

    /**
     * Get the component's properties
     * @return the component's properties
     */
    @Override
    public HashMap<String, Set<String>> getProperties() {
        return this.properties;
    }

    /**
     * Constructor to build a new CDX 1.4 Component Object
     * @param type component type
     * @param uid component uid
     * @param author component author
     * @param name component name
     * @param licenses component licenses
     * @param copyright component copyright
     * @param hashes component hashes
     * @param supplier component supplier
     * @param version component version
     * @param description component description
     * @param cpes component CPEs
     * @param purls component PURLs
     * @param mimeType component mime type
     * @param publisher component publisher
     * @param scope component scope
     * @param group component group
     * @param externalReferences component external references
     * @param properties component properties
     */
    public CDX14ComponentObject(String type, String uid, String author, String name,
                                LicenseCollection licenses, String copyright,
                                HashMap<String, String> hashes, Organization supplier,
                                String version, Description description, Set<String> cpes,
                                Set<String> purls, String mimeType, String publisher,
                                String scope, String group, Set<ExternalReference> externalReferences,
                                HashMap<String, Set<String>> properties){
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

}