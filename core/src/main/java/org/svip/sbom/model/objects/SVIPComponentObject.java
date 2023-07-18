package org.svip.sbom.model.objects;

import org.svip.sbom.model.interfaces.schemas.CycloneDX14.CDX14Package;
import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23File;
import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23Package;
import org.svip.sbom.model.objects.SPDX23.SPDX23PackageObject;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.shared.util.Description;
import org.svip.sbom.model.shared.util.ExternalReference;
import org.svip.sbom.model.shared.util.LicenseCollection;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * file: SVIPComponentObject.java
 * Holds information for an SVIP Component Object
 *
 * @author Derek Garcia
 * @author Matthew Morrison
 */
public class SVIPComponentObject implements CDX14Package, SPDX23Package, SPDX23File {

    /**Component's type*/
    private final String type;

    /**Component's uid*/
    private final String uid;

    /**Component's author*/
    private final String author;

    /**Component's name*/
    private final String name;

    /**Component's licenses*/
    private LicenseCollection licenses;

    /**Component's copyright*/
    private final String copyright;

    /**Component's hashes*/
    private final HashMap<String, String> hashes;

    /**Component's file notice*/
    private final String fileNotice;

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

    /**Component's download location*/
    private final String downloadLocation;

    /**Component's file name*/
    private final String fileName;

    /**If component's files were analyzed*/
    private final Boolean filesAnalyzed;

    /**Component's verification code*/
    private final String verificationCode;

    /**Component's home page*/
    private final String homePage;

    /**Component's source info*/
    private final String sourceInfo;

    /**Component's release date*/
    private final String releaseDate;

    /**Component's built date*/
    private final String builtDate;

    /**Component's valid until date*/
    private final String validUntilDate;

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

    /**Component's comment*/
    private final String comment;

    /**Component's attribution text*/
    private final String attributionText;

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
        if (this.licenses == null) this.licenses = new LicenseCollection();
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
     * Get the component's comment
     * @return the component's comment
     */
    @Override
    public String getComment() {
        return this.comment;
    }

    /**
     * Get the component's attribution text
     * @return the component's attribution text
     */
    @Override
    public String getAttributionText() {
        return this.attributionText;
    }

    /**
     * Get the component's file notice
     * @return the component's file notice
     */
    @Override
    public String getFileNotice() {
        return this.fileNotice;
    }

    /**
     * Get the component's download location
     * @return the component's download location
     */
    @Override
    public String getDownloadLocation() {
        return this.downloadLocation;
    }

    /**
     * Get the component's file name
     * @return the component's file name
     */
    @Override
    public String getFileName() {
        return this.fileName;
    }

    /**
     * Get if the component's files were analyzed
     * @return if the component's files were analyzed or not
     */
    @Override
    public Boolean getFilesAnalyzed() {
        return this.filesAnalyzed;
    }

    /**
     * Get the component's verification code
     * @return the component's verification code
     */
    @Override
    public String getVerificationCode() {
        return this.verificationCode;
    }

    /**
     * Get the component's home page
     * @return the component's home page
     */
    @Override
    public String getHomePage() {
        return this.homePage;
    }

    /**
     * Get the component's source info
     * @return the component's source info
     */
    @Override
    public String getSourceInfo() {
        return this.sourceInfo;
    }

    /**
     * Get the component's release data
     * @return the component's release data
     */
    @Override
    public String getReleaseDate() {
        return this.releaseDate;
    }

    /**
     * Get the component's built date
     * @return the component's built date
     */
    @Override
    public String getBuiltDate() {
        return this.builtDate;
    }

    /**
     * Get the component's valid until date
     * @return the component's valid until date
     */
    @Override
    public String getValidUntilDate() {
        return this.validUntilDate;
    }

    /**
     * Constructor to build the SVIP Component Object
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
     * @param externalReferences component external references
     * @param downloadLocation component download location
     * @param fileName component file name
     * @param filesAnalyzed if component's files were analyzed
     * @param verificationCode component verification code
     * @param homePage component home page
     * @param sourceInfo component source info
     * @param releaseDate component release date
     * @param builtDate component build date
     * @param validUntilDate component valid until date
     * @param mimeType component mime type
     * @param publisher component publisher
     * @param scope component scope
     * @param group component group
     * @param properties component properties
     * @param fileNotice component file notice
     */
    public SVIPComponentObject(String type, String uid, String author, String name,
                               LicenseCollection licenses, String copyright,
                               HashMap<String, String> hashes, Organization supplier,
                               String version, Description description, Set<String> cpes,
                               Set<String> purls, Set<ExternalReference> externalReferences,
                               String downloadLocation, String fileName, Boolean filesAnalyzed,
                               String verificationCode, String homePage, String sourceInfo,
                               String releaseDate, String builtDate, String validUntilDate,
                               String mimeType, String publisher, String scope, String group,
                               HashMap<String, Set<String>> properties, String fileNotice,
                               String comment, String attributionText){
        this.type = type;
        this.uid = uid;
        this.author = author;
        this.name = name;
        this.licenses = licenses;
        this.copyright = copyright;
        if (hashes == null) this.hashes = new HashMap<>();
        else this.hashes = hashes;
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
        this.comment = comment;
        this.attributionText = attributionText;
    }

        /*
        Construct from other component objects
     */

    /**
     * // Constructs an SVIPComponent object from an spdx23PackageObject
     * @param spdx23PackageObject said object to construct from
     */
    public SVIPComponentObject(SPDX23PackageObject spdx23PackageObject){
        this.type = spdx23PackageObject.getType();
        this.uid = spdx23PackageObject.getUID();
        this.author = spdx23PackageObject.getAuthor();
        this.name = spdx23PackageObject.getName();
        this.licenses = spdx23PackageObject.getLicenses();
        this.copyright = spdx23PackageObject.getCopyright();
        this.hashes = (HashMap<String, String>) spdx23PackageObject.getHashes();
        this.supplier = spdx23PackageObject.getSupplier();
        this.version = spdx23PackageObject.getVersion();
        this.description = spdx23PackageObject.getDescription();
        this.cpes = spdx23PackageObject.getCPEs();
        this.purls = spdx23PackageObject.getPURLs();
        this.externalReferences = spdx23PackageObject.getExternalReferences();
        this.downloadLocation =spdx23PackageObject.getDownloadLocation();
        this.fileName = spdx23PackageObject.getFileName();
        this.filesAnalyzed = spdx23PackageObject.getFilesAnalyzed();
        this.verificationCode = spdx23PackageObject.getVerificationCode();
        this.homePage = spdx23PackageObject.getHomePage();
        this.sourceInfo = spdx23PackageObject.getSourceInfo();
        this.releaseDate = spdx23PackageObject.getReleaseDate();
        this.builtDate = spdx23PackageObject.getBuiltDate();
        this.validUntilDate = spdx23PackageObject.getValidUntilDate();
        this.mimeType = spdx23PackageObject.getType();


        // todo is this okay
        this.publisher = "";
        this.scope = "";
        this.group = "";            // attributes not supported in spdx23PackageObject
        this.properties = null;
        this.fileNotice  = null;

        this.comment = getComment();
        this.attributionText = getAttributionText();
    }

}
