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

    private final String type;

    private final String uid;

    private final String author;

    private final String name;

    private final LicenseCollection licenses;
    private final String copyright;

    private final HashMap<String, String> hashes;
    private final String fileNotice;

    private final Organization supplier;

    private final String version;

    private final Description description;

    private final Set<String> cpes;

    private final Set<String> purls;

    private final String downloadLocation;

    private final String fileName;

    private final Boolean filesAnalyzed;

    private final String verificationCode;

    private final String homePage;

    private final String sourceInfo;

    private final String releaseDate;

    private final String builtDate;

    private final String validUntilDate;

    private final String mimeType;

    private final String publisher;

    private final String scope;

    private final String group;

    private final Set<ExternalReference> externalReferences;

    private final HashMap<String, Set<String>> properties;

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
                               HashMap<String, Set<String>> properties, String fileNotice){
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
        this.fileNotice = fileNotice;
    }
}
