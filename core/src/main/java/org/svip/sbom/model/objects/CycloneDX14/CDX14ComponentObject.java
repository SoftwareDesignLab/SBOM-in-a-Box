package org.svip.sbom.model.objects.CycloneDX14;

import org.svip.sbom.model.interfaces.schemas.CycloneDX14.CDX14Package;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.shared.util.Description;
import org.svip.sbom.model.shared.util.ExternalReference;
import org.svip.sbom.model.shared.util.LicenseCollection;

import java.util.HashMap;
import java.util.Set;

/**
 * file: CDX14ComponentObject.java
 * Holds information for a single CycloneDX 1.4 component object
 *
 * @author Derek Garcia
 * @author Matthew Morrison
 */
// todo
public class CDX14ComponentObject implements CDX14Package {

    private String type;

    private String uid;

    private String author;

    private String name;

    private LicenseCollection licenses;

    private String copyright;

    private HashMap<String, String> hashes;

    private Organization supplier;

    private String version;

    private Description description;

    private Set<String> cpes;

    private Set<String> purls;

    private String mimeType;

    private String publisher;

    private String scope;

    private String group;

    private Set<ExternalReference> externalReferences;

    private HashMap<String, Set<String>> properties;

    @Override
    public Organization getSupplier() {
        return this.supplier;
    }

    @Override
    public String getVersion() {
        return this.version;
    }

    @Override
    public Description getDescription() {
        return this.description;
    }

    @Override
    public Set<String> getCPEs() {
        return this.cpes;
    }

    @Override
    public Set<String> getPURLs() {
        return this.purls;
    }

    @Override
    public Set<ExternalReference> getExternalReferences() {
        return this.externalReferences;
    }

    @Override
    public String getMimeType() {
        return this.mimeType;
    }

    @Override
    public String getPublisher() {
        return this.publisher;
    }

    @Override
    public String getScope() {
        return this.scope;
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public HashMap<String, Set<String>> getProperties() {
        return this.properties;
    }

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
