package org.svip.builders.component;


import org.svip.sbom.builder.interfaces.schemas.CycloneDX14.CDX14PackageBuilder_I;
import org.svip.sbom.builder.interfaces.schemas.SPDX23.SPDX23FileBuilder_I;
import org.svip.sbom.builder.interfaces.schemas.SPDX23.SPDX23PackageBuilder_I;
import org.svip.sbom.model.objects.SVIPComponentObject;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.shared.util.Description;
import org.svip.sbom.model.shared.util.ExternalReference;
import org.svip.sbom.model.shared.util.LicenseCollection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * file: SVIPComponentBuilder.java
 * Builder class for SVIP components
 *
 * @author Matthew Morrison
 * @author Thomas Roman
 */
public class SVIPComponentBuilder implements SPDX23PackageBuilder_I, CDX14PackageBuilder_I, SPDX23FileBuilder_I {

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

    /**Component comment*/
    private String comment;

    /**Component attribution text*/
    private String attributionText;

    /**Component file notice*/
    private String fileNotice;

    /**Component download location*/
    private String downloadLocation;

    /**Component file name*/
    private String fileName;

    /**If the component's files were analyzed*/
    private Boolean filesAnalyzed;

    /**Component verification code*/
    private String verificationCode;

    /**Component home page*/
    private String homePage;

    /**Component source info*/
    private String sourceInfo;

    /**Component release date*/
    private String releaseDate;

    /**Component built date*/
    private String builtDate;

    /**Component valid until date*/
    private String validUntilDate;

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

    public SVIPComponentBuilder() {

    }

    public SVIPComponentBuilder(SVIPComponentObject component) {
        this.type = component.getType();
        this.uid = component.getUID();
        this.author = component.getAuthor();
        this.name = component.getName();
        this.licenses = component.getLicenses();
        this.copyright = component.getCopyright();
        this.hashes = (HashMap<String, String>) component.getHashes();
        this.comment = component.getComment();
        this.attributionText = component.getAttributionText();
        this.fileNotice = component.getFileNotice();
        this.downloadLocation = component.getDownloadLocation();
        this.fileName = component.getFileName();
        this.filesAnalyzed = component.getFilesAnalyzed();
        this.verificationCode = component.getVerificationCode();
        this.homePage = component.getHomePage();
        this.sourceInfo = component.getSourceInfo();
        this.releaseDate = component.getReleaseDate();
        this.builtDate = component.getBuiltDate();
        this.validUntilDate = component.getValidUntilDate();
        this.supplier = component.getSupplier();
        this.version = component.getVersion();
        this.description = component.getDescription();
        this.cpes = component.getCPEs();
        this.purls = component.getPURLs();
        this.externalReferences = component.getExternalReferences();
        this.mimeType = component.getMimeType();
        this.publisher = component.getPublisher();
        this.scope = component.getScope();
        this.group = component.getGroup();
        this.properties = component.getProperties();
    }

    /**
     * Set the component's mime type
     * @param mimeType the package's mime type
     * @return an SVIPComponentBuilder
     */
    @Override
    public SVIPComponentBuilder setMimeType(String mimeType) {
        this.mimeType = mimeType;
        return this;
    }

    /**
     * Set the component's publisher
     * @param publisher the package's publisher
     * @return an SVIPComponentBuilder
     */
    @Override
    public SVIPComponentBuilder setPublisher(String publisher) {
        this.publisher = publisher;
        return this;
    }

    /**
     * Set the component's scope
     * @param scope the package's scope
     * @return an SVIPComponentBuilder
     */
    @Override
    public SVIPComponentBuilder setScope(String scope) {
        this.scope = scope;
        return this;
    }

    /**
     * Set the component's group
     * @param group the package's group
     * @return an SVIPComponentBuilder
     */
    @Override
    public SVIPComponentBuilder setGroup(String group) {
        this.group = group;
        return this;
    }

    /**
     * Add an external references to the component
     * @param externalReference a package's external reference
     * @return an SVIPComponentBuilder
     */
    @Override
    public SVIPComponentBuilder addExternalReferences(ExternalReference externalReference) {
        // initialize the hash set
        if (this.externalReferences == null) {
            this.externalReferences = new HashSet<ExternalReference>();
        }
        this.externalReferences.add(externalReference);
        return this;
    }

    /**
     * Add a property to the component
     * @param name the name of the property
     * @param value the value of the property
     * @return an SVIPComponentBuilder
     */
    @Override
    public SVIPComponentBuilder addProperty(String name, String value) {
        // initialize the hash set
        if (this.properties == null) {
            this.properties = new HashMap<String, Set<String>>();
        }

        Set<String> values;
        if(this.properties.containsKey(name)){
            values = this.properties.get(name);
        }
        else{
            values = new HashSet<>();
        }
        values.add(value);
        this.properties.put(name, values);

        return this;
    }

    /**
     * Set the component's type
     * @param type the designated type of component
     * @return an SVIPComponentBuilder
     */
    @Override
    public SVIPComponentBuilder setType(String type) {
        this.type = type;
        return this;
    }

    /**
     * Set the component's uid
     * @param uid the uid of the component
     * @return an SVIPComponentBuilder
     */
    @Override
    public SVIPComponentBuilder setUID(String uid) {
        this.uid = uid;
        return this;
    }

    /**
     * Set the component's author
     * @param author the author of the component
     * @return an SVIPComponentBuilder
     */
    @Override
    public SVIPComponentBuilder setAuthor(String author) {
        this.author = author;
        return this;
    }

    /**
     * Set the component's name
     * @param name the name of the component
     * @return an SVIPComponentBuilder
     */
    @Override
    public SVIPComponentBuilder setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Set the component's licenses
     * @param licenses a collection of licenses
     * @return an SVIPComponentBuilder
     */
    @Override
    public SVIPComponentBuilder setLicenses(LicenseCollection licenses) {
        this.licenses = licenses;
        return this;
    }

    /**
     * Set the component's copyright info
     * @param copyright the copyright info of the component
     * @return an SVIPComponentBuilder
     */
    @Override
    public SVIPComponentBuilder setCopyright(String copyright) {
        this.copyright = copyright;
        return this;
    }

    /**
     * Add a hash value to the component
     * @param algorithm the algorithm of the hash
     * @param hash the value of the hash
     * @return an SVIPComponentBuilder
     */
    @Override
    public SVIPComponentBuilder addHash(String algorithm, String hash) {
        // initialize the hash set
        if (this.hashes == null) {
            this.hashes = new HashMap<String, String>();
        }
        this.hashes.put(algorithm, hash);
        return this;
    }

    /**
     * Set the component's supplier
     * @param supplier the component's supplier
     * @return an SVIPComponentBuilder
     */
    @Override
    public SVIPComponentBuilder setSupplier(Organization supplier) {
        this.supplier = supplier;
        return this;
    }

    /**
     * Set the component's version
     * @param version the component's version
     * @return an SVIPComponentBuilder
     */
    @Override
    public SVIPComponentBuilder setVersion(String version) {
        this.version = version;
        return this;
    }

    /**
     * Set the component's description
     * @param description the component's description
     * @return an SVIPComponentBuilder
     */
    @Override
    public SVIPComponentBuilder setDescription(Description description) {
        this.description = description;
        return this;
    }

    /**
     * Add a cpe to the component
     * @param cpe the cpe string to add
     * @return an SVIPComponentBuilder
     */
    @Override
    public SVIPComponentBuilder addCPE(String cpe) {
        // initialize the hash set
        if (this.cpes == null) {
            this.cpes = new HashSet<String>();
        }
        this.cpes.add(cpe);
        return this;
    }

    /**
     * Add a purl to the component
     * @param purl the purl string to add
     * @return an SVIPComponentBuilder
     */
    @Override
    public SVIPComponentBuilder addPURL(String purl) {
        // initialize the hash set
        if (this.purls == null) {
            this.purls = new HashSet<String>();
        }
        this.purls.add(purl);
        return this;
    }

    /**
     * Add an external reference to the component
     * @param externalReference the external component to add
     * @return an SVIPComponentBuilder
     */
    @Override
    public SVIPComponentBuilder addExternalReference(ExternalReference externalReference) {
        // initialize the hash set
        if (this.externalReferences == null) {
            this.externalReferences = new HashSet<ExternalReference>();
        }
        this.externalReferences.add(externalReference);
        return this;
    }

    /**
     * Set the component's comment
     * @param comment the comment for the component
     * @return an SVIPComponentBuilder
     */
    @Override
    public SVIPComponentBuilder setComment(String comment) {
        this.comment = comment;
        return this;
    }

    /**
     * Set the component's attribution text
     * @param attributionText the attribution text of the component
     * @return an SVIPComponentBuilder
     */
    @Override
    public SVIPComponentBuilder setAttributionText(String attributionText) {
        this.attributionText = attributionText;
        return this;
    }

    /**
     * Set the component's download location
     * @param downloadLocation the package's download location
     * @return an SVIPComponentBuilder
     */
    @Override
    public SVIPComponentBuilder setDownloadLocation(String downloadLocation) {
        this.downloadLocation = downloadLocation;
        return this;
    }

    /**
     * Set the component's file name
     * @param fileName the package's file name
     * @return an SVIPComponentBuilder
     */
    @Override
    public SVIPComponentBuilder setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    /**
     * Set if the component's files were analyzed
     * @param filesAnalyzed a boolean if the files were analyzed
     * @return an SVIPComponentBuilder
     */
    @Override
    public SVIPComponentBuilder setFilesAnalyzed(Boolean filesAnalyzed) {
        this.filesAnalyzed = filesAnalyzed;
        return this;
    }

    /**
     * Set the component's verification code
     * @param verificationCode the package's verification code
     * @return an SVIPComponentBuilder
     *
     */
    @Override
    public SVIPComponentBuilder setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
        return this;
    }

    /**
     * Set the component's home page
     * @param homePage the package's home page
     * @return an SVIPComponentBuilder
     */
    @Override
    public SVIPComponentBuilder setHomePage(String homePage) {
        this.homePage = homePage;
        return this;
    }

    /**
     * Set the component's source information
     * @param sourceInfo the package's source information
     * @return an SVIPComponentBuilder
     */
    @Override
    public SVIPComponentBuilder setSourceInfo(String sourceInfo) {
        this.sourceInfo = sourceInfo;
        return this;
    }

    /**
     * Set the component's release date
     * @param releaseDate the package's release date
     * @return an SVIPComponentBuilder
     */
    @Override
    public SVIPComponentBuilder setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
        return this;
    }

    /**
     * Set the component's build date
     * @param buildDate the package's build date
     * @return an SVIPComponentBuilder
     */
    @Override
    public SVIPComponentBuilder setBuildDate(String buildDate) {
        this.builtDate = buildDate;
        return this;
    }

    /**
     * Set the component's valid until date
     * @param validUntilDate the package's valid until date
     * @return an SVIPComponentBuilder
     */
    @Override
    public SVIPComponentBuilder setValidUntilDate(String validUntilDate) {
        this.validUntilDate = validUntilDate;
        return this;
    }

    /**
     * Set the file's file notice
     * @param fileNotice the file notice
     * @return an SVIPComponentBuilder
     */
    @Override
    public SVIPComponentBuilder setFileNotice(String fileNotice) {
        this.fileNotice = fileNotice;
        return this;
    }

    /**
     * Build an SVIPComponent
     * @return an SVIPComponentObject
     */
    @Override
    public SVIPComponentObject build() {
        return new SVIPComponentObject(type, uid, author, name, licenses,
                copyright, hashes, supplier, version, description, cpes,
                purls, externalReferences, downloadLocation, fileName,
                filesAnalyzed, verificationCode, homePage, sourceInfo,
                releaseDate, builtDate, validUntilDate, mimeType,
                publisher, scope, group, properties, fileNotice,
                comment, attributionText);
    }

    /**
     * Build and flush an SVIP component
     * @return an SVIPComponentObject
     */
    @Override
    public SVIPComponentObject buildAndFlush() {
        // build the component
        SVIPComponentObject component = build();

        // clear all the data in the builder
        this.type = null;
        this.uid = null;
        this.author = null;
        this.name = null;
        this.licenses = null;
        this.copyright = null;
        this.hashes = null;
        this.supplier = null;
        this.version = null;
        this.description = null;
        this.cpes = null;
        this.purls = null;
        this.externalReferences = null;
        this.downloadLocation = null;
        this.fileName = null;
        this.filesAnalyzed = null;
        this.verificationCode = null;
        this.homePage = null;
        this.sourceInfo = null;
        this.releaseDate = null;
        this.builtDate = null;
        this.validUntilDate = null;
        this.mimeType = null;
        this.publisher = null;
        this.scope = null;
        this.group = null;
        this.properties = null;
        this.fileNotice = null;
        this.comment = null;
        this.attributionText = null;
        return component;
    }
}
