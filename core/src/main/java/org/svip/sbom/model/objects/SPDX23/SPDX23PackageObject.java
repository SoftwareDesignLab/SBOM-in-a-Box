package org.svip.sbom.model.objects.SPDX23;

import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23Package;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.shared.util.Description;
import org.svip.sbom.model.shared.util.ExternalReference;
import org.svip.sbom.model.shared.util.LicenseCollection;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * file: SPDX23PackageObject.java
 * Holds information for a single SPDX 2.3 Package object
 *
 * @author Derek Garcia
 * @author Matthew Morrison
 */
// todo
public class SPDX23PackageObject implements SPDX23Package {

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

    private Set<ExternalReference> externalReferences;

    private String downloadLocation;

    private String fileName;

    private boolean filesAnalysed;

    private String verificationCode;

    private String homePage;

    private String sourceInfo;

    private String releaseDate;

    private String builtDate;

    private String validUntilDate;

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
    public String getComment() {
        return null;
    }

    @Override
    public String getAttributionText() {
        return null;
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
        return this.filesAnalysed;
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


    public SPDX23PackageObject(String type, String uid, String author, String name,
                               LicenseCollection licenses, String copyright,
                               HashMap<String, String> hashes, Organization supplier,
                               String version, Description description, Set<String> cpes,
                               Set<String> purls, Set<ExternalReference> externalReferences,
                               String downloadLocation, String fileName, Boolean filesAnalysed,
                               String verificationCode, String homePage, String sourceInfo,
                               String releaseDate, String builtDate, String validUntilDate){
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
        this.filesAnalysed = filesAnalysed;
        this.verificationCode = verificationCode;
        this.homePage = homePage;
        this.sourceInfo = sourceInfo;
        this.releaseDate = releaseDate;
        this.builtDate = builtDate;
        this.validUntilDate = validUntilDate;

    }
}
