package org.svip.sbom.model.objects;

import org.svip.sbom.model.interfaces.schemas.CycloneDX14.CDX14Package;
import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23File;
import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23Package;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.shared.util.Description;
import org.svip.sbom.model.shared.util.ExternalReference;
import org.svip.sbom.model.shared.util.LicenseCollection;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * file: SVIPComponentObject.java
 * Holds information for a single SVIP Component Object
 *
 * @author Derek Garcia
 * @author Matthew Morrison
 */
//todo
public class SVIPComponentObject implements CDX14Package, SPDX23Package, SPDX23File {

    private String type;

    private String uid;

    private String author;

    private String name;

    private LicenseCollection licenses;
    private String copyright;

    private HashMap<String, String> hashes;
    private String fileNotice;

    private Organization supplier;

    private String version;

    private Description description;

    private Set<String> cpes;

    private Set<String> purls;

    private String downloadLocation;

    private String fileName;

    private Boolean filesAnalyzed;

    private String verificationCode;

    private String homePage;

    private String sourceInfo;

    private String releaseDate;

    private String builtDate;

    private String validUntilDate;

    private String mimeType;

    private String publisher;

    private String scope;

    private String group;

    private Set<ExternalReference> externalReferences;

    private HashMap<String, Set<String>> properties;

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public String getUID() {
        return this.uid;
    }

    @Override
    public String getAuthor() {
        return this.author;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public LicenseCollection getLicenses() {
        return this.licenses;
    }

    @Override
    public String getCopyright() {
        return this.copyright;
    }

    @Override
    public Map<String, String> getHashes() {
        return this.hashes;
    }

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

    @Override
    public String getComment() {
        return null;
    }

    @Override
    public String getAttributionText() {
        return null;
    }

    @Override
    public String getFileNotice() {
        return this.fileNotice;
    }

    @Override
    public String getDownloadLocation() {
        return this.downloadLocation;
    }

    @Override
    public String getFileName() {
        return this.fileName;
    }

    @Override
    public Boolean getFilesAnalyzed() {
        return this.filesAnalyzed;
    }

    @Override
    public String getVerificationCode() {
        return this.verificationCode;
    }

    @Override
    public String getHomePage() {
        return this.homePage;
    }

    @Override
    public String getSourceInfo() {
        return this.sourceInfo;
    }

    @Override
    public String getReleaseDate() {
        return this.releaseDate;
    }

    @Override
    public String getBuiltDate() {
        return this.builtDate;
    }

    @Override
    public String getValidUntilDate() {
        return this.validUntilDate;
    }

    public SVIPComponentObject(String type, String uid, String author, String name,
                               LicenseCollection licenses, String copyright,
                               HashMap<String, String> hashes, Organization supplier,
                               String version, Description description, Set<String> cpes,
                               Set<String> purls, Set<ExternalReference> externalReferences,
                               String downloadLocation, String fileName, Boolean filesAnalyzed,
                               String verificationCode, String homePage, String sourceInfo,
                               String releaseDate, String builtDate, String validUntilDate,
                               String mimeType, String publisher, String scope, String group,
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
        this.externalReferences = externalReferences;
        this.downloadLocation =downloadLocation;
        this.fileName = fileName;
        this.filesAnalyzed = filesAnalyzed;
        this.verificationCode = verificationCode;
        this.homePage = homePage;
        this.sourceInfo = sourceInfo;
        this.releaseDate = releaseDate;
        this.builtDate = builtDate;
        this.validUntilDate = validUntilDate;
        this.mimeType = mimeType;
        this.publisher = publisher;
        this.scope = scope;
        this.group = group;
        this.properties = properties;
    }
}
