package org.svip.sbom.model.objects.SPDX23;

import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOMPackage;
import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23Component;
import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23Package;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.shared.util.Description;
import org.svip.sbom.model.shared.util.ExternalReference;
import org.svip.sbom.model.shared.util.LicenseCollection;
import org.svip.compare.conflicts.Conflict;
import org.svip.compare.conflicts.ConflictFactory;

import java.util.*;

/**
 * file: SPDX23PackageObject.java
 * Holds information for an SPDX 2.3 Package object
 *
 * @author Derek Garcia
 * @author Matthew Morrison
 */
public class SPDX23PackageObject implements SPDX23Package {

    /**Package's type*/
    private final String type;

    /**Package's uid*/
    private final String uid;

    /**Package's author*/
    private final String author;

    /**Package's name*/
    private final String name;

    /**Package's licenses*/
    private final LicenseCollection licenses;

    /**Package's copyright*/
    private final String copyright;

    /**Package's hashes*/
    private final HashMap<String, String> hashes;

    /**Package's supplier*/
    private final Organization supplier;

    /**Package's version*/
    private final String version;

    /**Package's description*/
    private final Description description;

    /**Package's CPEs*/
    private final Set<String> cpes;

    /**Package's PURLs*/
    private final Set<String> purls;

    /**Package's external references*/
    private final Set<ExternalReference> externalReferences;

    /**Package's comment*/
    private final String comment;

    /**Package's attribution text*/
    private final String attributionText;

    /**Package's download location*/
    private final String downloadLocation;

    /**Package's file name*/
    private final String fileName;

    /**If Package's files were analyzed*/
    private final Boolean filesAnalyzed;

    /**Package's verification code*/
    private final String verificationCode;

    /**Package's home page*/
    private final String homePage;

    /**Package's source info*/
    private final String sourceInfo;

    /**Package's release date*/
    private final String releaseDate;

    /**Package's built date*/
    private final String builtDate;

    /**Package's valid until date*/
    private final String validUntilDate;

    /**
     * Get the package's type
     * @return the package's type
     */
    @Override
    public String getType() {
        return this.type;
    }

    /**
     * Get the package's uid
     * @return the package's uid
     */
    @Override
    public String getUID() {
        return this.uid;
    }

    /**
     * Get the package's author
     * @return the package's author
     */
    @Override
    public String getAuthor() {
        return this.author;
    }

    /**
     * Get the package's name
     * @return the package's name
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Get the package's licenses
     * @return the package's licenses
     */
    @Override
    public LicenseCollection getLicenses() {
        return this.licenses;
    }

    /**
     * Get the package's copyright info
     * @return the package's copyright info
     */
    @Override
    public String getCopyright() {
        return this.copyright;
    }

    /**
     * Get the package's hashes
     * @return the package's hashes
     */
    @Override
    public Map<String, String> getHashes() {
        return this.hashes;
    }

    /**
     * Get the package's supplier
     * @return The package's supplier
     */
    @Override
    public Organization getSupplier() {
        return this.supplier;
    }

    /**
     * Get the package's version
     * @return the package's version
     */
    @Override
    public String getVersion() {
        return this.version;
    }

    /**
     * Get the package's description
     * @return the package's description
     */
    @Override
    public Description getDescription() {
        return this.description;
    }

    /**
     * Get the package's CPEs
     * @return the package's CPEs
     */
    @Override
    public Set<String> getCPEs() {
        return this.cpes;
    }

    /**
     * Get the package's PURLs
     * @return the package's PURLs
     */
    @Override
    public Set<String> getPURLs() {
        return this.purls;
    }

    /**
     * Get the package's external references
     * @return the package's external references
     */
    @Override
    public Set<ExternalReference> getExternalReferences() {
        return this.externalReferences;
    }

    /**
     * Get the package's download location
     * @return the package's download location
     */
    @Override
    public String getDownloadLocation() {
        return this.downloadLocation;
    }

    /**
     * Get the package's file name
     * @return the package's file name
     */
    @Override
    public String getFileName() {
        return this.fileName;
    }

    /**
     * Get if the package's files were analyzed
     * @return if the package's files were analyzed or not
     */
    @Override
    public Boolean getFilesAnalyzed() {
        return this.filesAnalyzed;
    }

    /**
     * Get the package's verification code
     * @return the package's verification code
     */
    @Override
    public String getVerificationCode() {
        return this.verificationCode;
    }

    /**
     * Get the package's home page
     * @return the package's home page
     */
    @Override
    public String getHomePage() {
        return this.homePage;
    }

    /**
     * Get the package's source info
     * @return the package's source info
     */
    @Override
    public String getSourceInfo() {
        return this.sourceInfo;
    }

    /**
     * Get the package's release data
     * @return the package's release data
     */
    @Override
    public String getReleaseDate() {
        return this.releaseDate;
    }

    /**
     * Get the package's built date
     * @return the package's built date
     */
    @Override
    public String getBuiltDate() {
        return this.builtDate;
    }

    /**
     * Get the package's valid until date
     * @return the package's valid until date
     */
    @Override
    public String getValidUntilDate() {
        return this.validUntilDate;
    }

    /**
     * Get the package's comment
     * @return the package's comment
     */
    @Override
    public String getComment() {
        return this.comment;
    }

    /**
     * Get the package's attribution text
     * @return the package's attribution text
     */
    @Override
    public String getAttributionText() {
        return this.attributionText;
    }

    /**
     * Constructor to build new SPDX 2.3 Package Object
     * @param type package type
     * @param uid package uid
     * @param author package author
     * @param name package name
     * @param licenses package licenses
     * @param copyright package copyright
     * @param hashes package hashes
     * @param supplier package supplier
     * @param version package version
     * @param description package description
     * @param cpes package CPEs
     * @param purls package PURLs
     * @param externalReferences package external references
     * @param downloadLocation package download location
     * @param fileName package file name
     * @param filesAnalyzed if package's files were analyzed
     * @param verificationCode package verification code
     * @param homePage package home page
     * @param sourceInfo package source info
     * @param releaseDate package release date
     * @param builtDate package build date
     * @param validUntilDate package valid until date
     *
     */
    public SPDX23PackageObject(String type, String uid, String author, String name,
                               LicenseCollection licenses, String copyright,
                               HashMap<String, String> hashes, Organization supplier,
                               String version, Description description, Set<String> cpes,
                               Set<String> purls, Set<ExternalReference> externalReferences,
                               String downloadLocation, String fileName, Boolean filesAnalyzed,
                               String verificationCode, String homePage, String sourceInfo,
                               String releaseDate, String builtDate, String validUntilDate,
                               String comment, String attributionText){
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
        this.comment = comment;
        this.attributionText = attributionText;
    }

    /**
     * Compare against another generic SBOM Package
     *
     * @param other Other SBOM Package to compare against
     * @return List of conflicts
     */
    @Override
    public List<Conflict> compare(Component other) {
        ConflictFactory cf = new ConflictFactory();

        // Type
        cf.addConflict("Type", MISC_MISMATCH, this.type, other.getType());

        // UID
        cf.addConflict("UID", MISC_MISMATCH, this.uid, other.getUID());

        // NAME
        // shouldn't occur
        cf.addConflict("Name", NAME_MISMATCH, this.name, other.getName());

        // AUTHOR
        cf.addConflict("Author", AUTHOR_MISMATCH, this.author, other.getAuthor());

        // Licenses
        if(cf.comparable("License", this.licenses, other.getLicenses()))
            cf.addConflicts(this.licenses.compare(other.getLicenses()));

        // Copyright
        cf.addConflict("Copyright", MISC_MISMATCH, this.copyright, other.getCopyright());

        // Hashes
        cf.compareHashes("Component Hash", this.hashes, other.getHashes());

        // Compare SPDX component specific fields
        if( other instanceof SPDX23Component)
            cf.addConflicts( compare((SPDX23Component) other) );

        // Compare SBOMPackage specific fields
        if( other instanceof SBOMPackage)
            cf.addConflicts( compare((SBOMPackage) other) );

        return cf.getConflicts();
    }

    /**
     * Compare against another generic SBOM Package
     *
     * @param other Other SBOM Package to compare against
     * @return List of conflicts
     */
    public List<Conflict> compare(SBOMPackage other) {
        ConflictFactory cf = new ConflictFactory();

        // Supplier
        if(cf.comparable("Supplier", this.supplier, other.getSupplier()))
            cf.addConflicts(this.supplier.compare(other.getSupplier()));

        // Version
        // shouldn't occur
        cf.addConflict("Version", VERSION_MISMATCH, this.version, other.getVersion());

        // Description
        if(cf.comparable("Description", this.description, other.getDescription()))
            cf.addConflicts(this.description.compare(other.getDescription()));

        // PURLs
        // todo use util PURL objects?
        cf.compareStringSets("PURL", PURL_MISMATCH, this.purls, other.getPURLs());

        // CPEs
        // todo use util CPE objects?
        cf.compareStringSets("CPE", CPE_MISMATCH, this.cpes, other.getCPEs());

        // External References
        cf.compareComparableSets("External Reference", new HashSet<>(this.externalReferences), new HashSet<>(other.getExternalReferences()));


        // Compare SBOMPackage specific fields
        if( other instanceof SPDX23Package)
            cf.addConflicts( compare((SPDX23Package) other) );


        return cf.getConflicts();
    }

    /**
     * Compare against another SPDX 2.3 Component
     *
     * @param other Other SPDX 2.3 Component to compare against
     * @return List of conflicts
     */
    @Override
    public List<Conflict> compare(SPDX23Component other){
        ConflictFactory cf = new ConflictFactory();

        // Comment
        cf.addConflict("Comment", MISC_MISMATCH, this.comment, other.getComment());

        // Attribution Text
        cf.addConflict("Attribution Text", MISC_MISMATCH, this.attributionText, other.getAttributionText());

        return cf.getConflicts();
    }

    /**
     * Compare against another SPDX 2.3 Package
     *
     * @param other Other SPDX 2.3 Package to compare against
     * @return List of conflicts
     */
    @Override
    public List<Conflict> compare(SPDX23Package other){
        ConflictFactory cf = new ConflictFactory();

        // Component Fields compare here to prevent duplicates

        // Comment
        cf.addConflict("Comment", MISC_MISMATCH, this.comment, other.getComment());

        // Attribution Text
        cf.addConflict("Attribution Text", MISC_MISMATCH, this.attributionText, other.getAttributionText());

        // SPDX Package fields

        // Download Location
        cf.addConflict("Download Location", MISC_MISMATCH, this.downloadLocation, other.getDownloadLocation());

        // File Name
        cf.addConflict("File Name", MISC_MISMATCH, this.fileName, other.getFileName());

        // Files Analyzed
        // needs null check because you cannot call null.toString
        if (this.filesAnalyzed != null) cf.addConflict("Files Analyzed", MISC_MISMATCH, this.filesAnalyzed.toString(), other.getFilesAnalyzed().toString());

        // Verification Code
        cf.addConflict("Verification Code", MISC_MISMATCH, this.verificationCode, other.getVerificationCode());

        // Home Page
        cf.addConflict("Home Page", MISC_MISMATCH, this.homePage, other.getHomePage());

        // Source Info
        cf.addConflict("Source Info", MISC_MISMATCH, this.sourceInfo, other.getSourceInfo());

        // Release Date
        cf.addConflict("Release Date", TIMESTAMP_MISMATCH, this.releaseDate, other.getReleaseDate());

        // Built Date
        cf.addConflict("Built Date", TIMESTAMP_MISMATCH, this.builtDate, other.getBuiltDate());

        // Valid Until Date
        cf.addConflict("Valid Until Date", TIMESTAMP_MISMATCH, this.validUntilDate, other.getValidUntilDate());

        return cf.getConflicts();
    }

    @Override
    public int hashCode() {
        if(name == null || version == null) return super.hashCode();
        return this.name.hashCode() + this.version.hashCode();
    }
}
