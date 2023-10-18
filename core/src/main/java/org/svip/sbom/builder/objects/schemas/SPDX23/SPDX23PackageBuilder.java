package org.svip.sbom.builder.objects.schemas.SPDX23;


import org.svip.sbom.builder.interfaces.schemas.SPDX23.SPDX23PackageBuilder_I;
import org.svip.sbom.model.objects.SPDX23.SPDX23PackageObject;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.shared.util.Description;
import org.svip.sbom.model.shared.util.ExternalReference;
import org.svip.sbom.model.shared.util.LicenseCollection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * file: SPDX23PackageBuilder.java
 * Builder class for SPDX 2.3 specific packages
 *
 * @author Matthew Morrison
 * @author Thomas Roman
 */
public class SPDX23PackageBuilder implements SPDX23PackageBuilder_I {
    /**
     * File type
     */
    private String type;

    /**
     * File uid
     */
    private String uid;

    /**
     * File author
     */
    private String author;

    /**
     * File name
     */
    private String name;

    /**
     * File licenses
     */
    private LicenseCollection licenses = new LicenseCollection();

    /**
     * File copyright
     */
    private String copyright;

    /**
     * File hashes
     */
    private HashMap<String, String> hashes = new HashMap<>();

    /**
     * File comment
     */
    private String comment;

    /**
     * File attribution text
     */
    private String attributionText;

    /**
     * Component download location
     */
    private String downloadLocation;

    /**
     * Component file name
     */
    private String fileName;

    /**
     * If the component's files were analyzed
     */
    private Boolean filesAnalyzed;

    /**
     * Component verification code
     */
    private String verificationCode;

    /**
     * Component home page
     */
    private String homePage;

    /**
     * Component source info
     */
    private String sourceInfo;

    /**
     * Component release date
     */
    private String releaseDate;

    /**
     * Component built date
     */
    private String builtDate;

    /**
     * Component valid until date
     */
    private String validUntilDate;

    /**
     * Component supplier
     */
    private Organization supplier;

    /**
     * Component version
     */
    private String version;

    /**
     * Component description
     */
    private Description description;

    /**
     * Component CPEs
     */
    private Set<String> cpes = new HashSet<>();

    /**
     * Component PURLs
     */
    private Set<String> purls = new HashSet<>();

    /**
     * Component external references
     */
    private Set<ExternalReference> externalReferences = new HashSet<>();

    /**
     * Set the component's type
     *
     * @param type the designated type of component
     * @return an SPDX23PackageBuilder
     */
    @Override
    public SPDX23PackageBuilder setType(String type) {
        this.type = type;
        return this;
    }

    /**
     * Set the component's uid
     *
     * @param uid the uid of the component
     * @return an SPDX23PackageBuilder
     */
    @Override
    public SPDX23PackageBuilder setUID(String uid) {
        this.uid = uid;
        return this;
    }

    /**
     * Set the component's author
     *
     * @param author the author of the component
     * @return an SPDX23PackageBuilder
     */
    @Override
    public SPDX23PackageBuilder setAuthor(String author) {
        this.author = author;
        return this;
    }

    /**
     * Set the component's name
     *
     * @param name the name of the component
     * @return an SPDX23PackageBuilder
     */
    @Override
    public SPDX23PackageBuilder setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Set the component's licenses
     *
     * @param licenses a collection of licenses
     * @return an SPDX23PackageBuilder
     */
    @Override
    public SPDX23PackageBuilder setLicenses(LicenseCollection licenses) {
        this.licenses = licenses;
        return this;
    }

    /**
     * Set the component's copyright info
     *
     * @param copyright the copyright info of the component
     * @return an SPDX23PackageBuilder
     */
    @Override
    public SPDX23PackageBuilder setCopyright(String copyright) {
        this.copyright = copyright;
        return this;
    }

    /**
     * Add a hash value to the component
     *
     * @param algorithm the algorithm of the hash
     * @param hash      the value of the hash
     * @return an SPDX23PackageBuilder
     */
    @Override
    public SPDX23PackageBuilder addHash(String algorithm, String hash) {
        this.hashes.put(algorithm, hash);
        return this;
    }

    /**
     * Set the component's comment
     *
     * @param comment the comment for the component
     * @return an SPDX23PackageBuilder
     */
    @Override
    public SPDX23PackageBuilder setComment(String comment) {
        this.comment = comment;
        return this;
    }

    /**
     * Set the component's attribution text
     *
     * @param attributionText the attribution text of the component
     * @return an SPDX23PackageBuilder
     */
    @Override
    public SPDX23PackageBuilder setAttributionText(String attributionText) {
        this.attributionText = attributionText;
        return this;
    }

    /**
     * Set the component's download location
     *
     * @param downloadLocation the package's download location
     * @return an SPDX23PackageBuilder
     */
    @Override
    public SPDX23PackageBuilder setDownloadLocation(String downloadLocation) {
        this.downloadLocation = downloadLocation;
        return this;
    }

    /**
     * Set the component's file name
     *
     * @param fileName the package's file name
     * @return an SPDX23PackageBuilder
     */
    @Override
    public SPDX23PackageBuilder setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    /**
     * Set if the component's files were analyzed
     *
     * @param filesAnalyzed a boolean if the files were analyzed
     * @return an SPDX23PackageBuilder
     */
    @Override
    public SPDX23PackageBuilder setFilesAnalyzed(Boolean filesAnalyzed) {
        this.filesAnalyzed = filesAnalyzed;
        return this;
    }

    /**
     * Set the component's verification code
     *
     * @param verificationCode the package's verification code
     * @return an SPDX23PackageBuilder
     */
    @Override
    public SPDX23PackageBuilder setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
        return this;
    }

    /**
     * Set the component's home page
     *
     * @param homePage the package's home page
     * @return an SPDX23PackageBuilder
     */
    @Override
    public SPDX23PackageBuilder setHomePage(String homePage) {
        this.homePage = homePage;
        return this;
    }

    /**
     * Set the component's source information
     *
     * @param sourceInfo the package's source information
     * @return an SPDX23PackageBuilder
     */
    @Override
    public SPDX23PackageBuilder setSourceInfo(String sourceInfo) {
        this.sourceInfo = sourceInfo;
        return this;
    }

    /**
     * Set the component's release date
     *
     * @param releaseDate the package's release date
     * @return an SPDX23PackageBuilder
     */
    @Override
    public SPDX23PackageBuilder setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
        return this;
    }

    /**
     * Set the component's build date
     *
     * @param buildDate the package's build date
     * @return an SPDX23PackageBuilder
     */
    @Override
    public SPDX23PackageBuilder setBuildDate(String buildDate) {
        this.builtDate = buildDate;
        return this;
    }

    /**
     * Set the component's valid until date
     *
     * @param validUntilDate the package's valid until date
     * @return an SPDX23PackageBuilder
     */
    @Override
    public SPDX23PackageBuilder setValidUntilDate(String validUntilDate) {
        this.validUntilDate = validUntilDate;
        return this;
    }

    /**
     * Set the component's supplier
     *
     * @param supplier the component's supplier
     * @return an SPDX23PackageBuilder
     */
    @Override
    public SPDX23PackageBuilder setSupplier(Organization supplier) {
        this.supplier = supplier;
        return this;
    }

    /**
     * Set the component's version
     *
     * @param version the component's version
     * @return an SPDX23PackageBuilder
     */
    @Override
    public SPDX23PackageBuilder setVersion(String version) {
        this.version = version;
        return this;
    }

    /**
     * Set the component's description
     *
     * @param description the component's description
     * @return an SPDX23PackageBuilder
     */
    @Override
    public SPDX23PackageBuilder setDescription(Description description) {
        this.description = description;
        return this;
    }

    /**
     * Add a cpe to the component
     *
     * @param cpe the cpe string to add
     * @return an SPDX23PackageBuilder
     */
    @Override
    public SPDX23PackageBuilder addCPE(String cpe) {
        this.cpes.add(cpe);
        return this;
    }

    /**
     * Add a purl to the component
     *
     * @param purl the purl string to add
     * @return an SPDX23PackageBuilder
     */
    @Override
    public SPDX23PackageBuilder addPURL(String purl) {
        this.purls.add(purl);
        return this;
    }

    /**
     * Add an external reference to the component
     *
     * @param externalReference the external component to add
     * @return an SPDX23PackageBuilder
     */
    @Override
    public SPDX23PackageBuilder addExternalReference(ExternalReference externalReference) {
        this.externalReferences.add(externalReference);
        return this;
    }

    /**
     * Build an SPDX23PackageObject
     *
     * @return an SPDX23PackageObject
     */
    @Override
    public SPDX23PackageObject build() {
        return new SPDX23PackageObject(type, uid, author, name, licenses,
                copyright, hashes, supplier, version, description, cpes,
                purls, externalReferences, downloadLocation, fileName,
                filesAnalyzed, verificationCode, homePage, sourceInfo,
                releaseDate, builtDate, validUntilDate,
                comment, attributionText);
    }

    /**
     * Build and flush the SPDX23PackageObject
     *
     * @return an SPDX23PackageObject
     */
    @Override
    public SPDX23PackageObject buildAndFlush() {
        // build the component
        SPDX23PackageObject component = build();
        // clear all the data in the builder
        this.type = null;
        this.uid = null;
        this.author = null;
        this.name = null;
        this.licenses = new LicenseCollection();
        this.copyright = null;
        this.hashes = new HashMap<>();
        this.supplier = null;
        this.version = null;
        this.description = null;
        this.cpes = new HashSet<>();
        this.purls = new HashSet<>();
        this.externalReferences = new HashSet<>();
        this.downloadLocation = null;
        this.fileName = null;
        this.filesAnalyzed = null;
        this.verificationCode = null;
        this.homePage = null;
        this.sourceInfo = null;
        this.releaseDate = null;
        this.builtDate = null;
        this.validUntilDate = null;
        this.comment = null;
        this.attributionText = null;
        return component;
    }
}
