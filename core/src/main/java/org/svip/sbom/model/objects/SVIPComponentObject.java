package org.svip.sbom.model.objects;

import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOMPackage;
import org.svip.sbom.model.interfaces.schemas.CycloneDX14.CDX14Package;
import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23Component;
import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23File;
import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23Package;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.shared.util.Description;
import org.svip.sbom.model.shared.util.ExternalReference;
import org.svip.sbom.model.shared.util.LicenseCollection;
import org.svip.sbomanalysis.comparison.conflicts.Conflict;
import org.svip.sbomanalysis.comparison.conflicts.MismatchConflict;
import org.svip.sbomanalysis.comparison.conflicts.MissingConflict;

import java.util.*;

import static org.svip.sbomanalysis.comparison.conflicts.ConflictType.*;

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
    private final LicenseCollection licenses;

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
        this.comment = comment;
        this.attributionText = attributionText;
    }
    public List<Conflict> compare(Component other) {
        ArrayList<Conflict> conflicts = new ArrayList<>();
        // NAME
        if (this.name != null ^ other.getName() != null) {
            conflicts.add(new MissingConflict("name", this.name, other.getName()));
        } else if (!Objects.equals(this.name, other.getName()) && this.name != null) {
            conflicts.add(new MismatchConflict("name", this.name, other.getName(), NAME_MISMATCH));
        }
        // AUTHOR
        if (this.author != null ^ other.getAuthor() != null) {
            conflicts.add(new MissingConflict("author", this.author, other.getAuthor()));
        } else if (!Objects.equals(this.author, other.getAuthor()) && this.author != null) {
            conflicts.add(new MismatchConflict("author", this.author, other.getAuthor(), AUTHOR_MISMATCH));
        }
        // Licenses
        if (this.licenses != null && other.getLicenses() != null) {
            if (!this.licenses.getConcluded().containsAll(other.getLicenses().getConcluded())) {
                conflicts.add(new MismatchConflict("license", this.licenses.getConcluded().toString(), other.getLicenses().getConcluded().toString(), LICENSE_MISMATCH));
            }
            if (!this.licenses.getDeclared().containsAll(other.getLicenses().getDeclared())) {
                conflicts.add(new MismatchConflict("license", this.licenses.getDeclared().toString(), other.getLicenses().getDeclared().toString(), LICENSE_MISMATCH));
            }
            if (!this.licenses.getInfoFromFiles().containsAll(other.getLicenses().getInfoFromFiles())) {
                conflicts.add(new MismatchConflict("license", this.licenses.getInfoFromFiles().toString(), other.getLicenses().getInfoFromFiles().toString(), LICENSE_MISMATCH));
            }
        } else if (this.licenses != null && (this.licenses.getConcluded().size() > 0 || this.licenses.getDeclared().size() > 0 || this.licenses.getInfoFromFiles().size() > 0)) {
            conflicts.add(new MissingConflict("license", this.licenses.getConcluded().toString() + this.licenses.getDeclared().toString() + this.licenses.getInfoFromFiles().toString(), null));
        } else if (other.getLicenses() != null && (other.getLicenses().getConcluded().size() > 0 || other.getLicenses().getDeclared().size() > 0 || other.getLicenses().getInfoFromFiles().size() > 0)) {
            conflicts.add(new MissingConflict("license", null, other.getLicenses().getConcluded().toString() + other.getLicenses().getDeclared().toString() + other.getLicenses().getInfoFromFiles().toString()));
        }
        // HASHES
        if (this.hashes != null && other.getHashes() != null) {
            if (!this.hashes.values().containsAll(other.getHashes().values())) {
                conflicts.add(new MismatchConflict("hash", this.hashes.toString(), other.getHashes().toString(), HASH_MISMATCH));
            }
        } else if (this.hashes != null) {
            conflicts.add(new MissingConflict("hash", this.hashes.toString(), null));
        } else if (other.getHashes() != null) {
            conflicts.add(new MissingConflict("hash", null, other.getHashes().toString()));
        }
        // TODO SWIDs?

        return conflicts.stream().toList();
    }
    public List<Conflict> compare(SPDX23Component other) {
        ArrayList<Conflict> conflicts = new ArrayList<>();
        // NAME
        if (this.name != null ^ other.getName() != null) {
            conflicts.add(new MissingConflict("name", this.name, other.getName()));
        } else if (!Objects.equals(this.name, other.getName()) && this.name != null) {
            conflicts.add(new MismatchConflict("name", this.name, other.getName(), NAME_MISMATCH));
        }
        // AUTHOR
        if (this.author != null ^ other.getAuthor() != null) {
            conflicts.add(new MissingConflict("author", this.author, other.getAuthor()));
        } else if (!Objects.equals(this.author, other.getAuthor()) && this.author != null) {
            conflicts.add(new MismatchConflict("author", this.author, other.getAuthor(), AUTHOR_MISMATCH));
        }
        // Licenses
        if (this.licenses != null && other.getLicenses() != null) {
            if (!this.licenses.getConcluded().containsAll(other.getLicenses().getConcluded())) {
                conflicts.add(new MismatchConflict("license", this.licenses.getConcluded().toString(), other.getLicenses().getConcluded().toString(), LICENSE_MISMATCH));
            }
            if (!this.licenses.getDeclared().containsAll(other.getLicenses().getDeclared())) {
                conflicts.add(new MismatchConflict("license", this.licenses.getDeclared().toString(), other.getLicenses().getDeclared().toString(), LICENSE_MISMATCH));
            }
            if (!this.licenses.getInfoFromFiles().containsAll(other.getLicenses().getInfoFromFiles())) {
                conflicts.add(new MismatchConflict("license", this.licenses.getInfoFromFiles().toString(), other.getLicenses().getInfoFromFiles().toString(), LICENSE_MISMATCH));
            }
        } else if (this.licenses != null && (this.licenses.getConcluded().size() > 0 || this.licenses.getDeclared().size() > 0 || this.licenses.getInfoFromFiles().size() > 0)) {
            conflicts.add(new MissingConflict("license", this.licenses.getConcluded().toString() + this.licenses.getDeclared().toString() + this.licenses.getInfoFromFiles().toString(), null));
        } else if (other.getLicenses() != null && (other.getLicenses().getConcluded().size() > 0 || other.getLicenses().getDeclared().size() > 0 || other.getLicenses().getInfoFromFiles().size() > 0)) {
            conflicts.add(new MissingConflict("license", null, other.getLicenses().getConcluded().toString() + other.getLicenses().getDeclared().toString() + other.getLicenses().getInfoFromFiles().toString()));
        }
        // HASHES
        if (this.hashes != null && other.getHashes() != null) {
            if (!this.hashes.values().containsAll(other.getHashes().values())) {
                conflicts.add(new MismatchConflict("hash", this.hashes.toString(), other.getHashes().toString(), HASH_MISMATCH));
            }
        } else if (this.hashes != null) {
            conflicts.add(new MissingConflict("hash", this.hashes.toString(), null));
        } else if (other.getHashes() != null) {
            conflicts.add(new MissingConflict("hash", null, other.getHashes().toString()));
        }
        // TODO SWIDs?

        return conflicts.stream().toList();
    }
    public List<Conflict> compare(SPDX23Package other) {
        ArrayList<Conflict> conflicts = new ArrayList<>();
        // NAME
        if (this.name != null ^ other.getName() != null) {
            conflicts.add(new MissingConflict("name", this.name, other.getName()));
        } else if (!Objects.equals(this.name, other.getName()) && this.name != null) {
            conflicts.add(new MismatchConflict("name", this.name, other.getName(), NAME_MISMATCH));
        }
        // VERSION
        if (this.version != null ^ other.getVersion() != null) {
            conflicts.add(new MissingConflict("version", this.version, other.getVersion()));
        } else if (!Objects.equals(this.version, other.getVersion()) && this.version != null) {
            conflicts.add(new MismatchConflict("version", this.version, other.getVersion(), VERSION_MISMATCH));
        }
        // SUPPLIER
        if (this.supplier != null && other.getSupplier() != null) {
            if (!Objects.equals(this.supplier.getName(), other.getSupplier().getName())) {
                conflicts.add(new MismatchConflict("supplier", this.supplier.getName(), other.getSupplier().getName(), SUPPLIER_MISMATCH));
            }
        } else if (this.supplier != null) {
            conflicts.add(new MissingConflict("supplier", this.supplier.getName(), null));
        } else if (other.getSupplier() != null) {
            conflicts.add(new MissingConflict("supplier", null, other.getSupplier().getName()));
        }
        // AUTHOR
        if (this.author != null ^ other.getAuthor() != null) {
            conflicts.add(new MissingConflict("author", this.author, other.getAuthor()));
        } else if (!Objects.equals(this.author, other.getAuthor()) && this.author != null) {
            conflicts.add(new MismatchConflict("author", this.author, other.getAuthor(), AUTHOR_MISMATCH));
        }
        // PURL
        if (this.purls != null && other.getPURLs() != null) {
            if (!this.purls.containsAll(other.getPURLs())) {
                conflicts.add(new MismatchConflict("purl", this.purls.toString(), other.getPURLs().toString(), PURL_MISMATCH));
            }
        } else if (this.purls != null) {
            conflicts.add(new MissingConflict("purl", this.purls.toString(), null));
        } else if (other.getPURLs() != null) {
            conflicts.add(new MissingConflict("purl", null, other.getPURLs().toString()));
        }
        // CPE
        if (this.cpes != null && other.getCPEs() != null) {
            if (!this.cpes.containsAll(other.getCPEs())) {
                conflicts.add(new MismatchConflict("cpe", this.cpes.toString(), other.getCPEs().toString(), CPE_MISMATCH));
            }
        } else if (this.cpes != null) {
            conflicts.add(new MissingConflict("cpe", this.cpes.toString(), null));
        } else if (other.getCPEs() != null) {
            conflicts.add(new MissingConflict("cpe", null, other.getCPEs().toString()));
        }
        // Licenses
        if (this.licenses != null && other.getLicenses() != null) {
            if (!this.licenses.getConcluded().containsAll(other.getLicenses().getConcluded())) {
                conflicts.add(new MismatchConflict("license", this.licenses.getConcluded().toString(), other.getLicenses().getConcluded().toString(), LICENSE_MISMATCH));
            }
            if (!this.licenses.getDeclared().containsAll(other.getLicenses().getDeclared())) {
                conflicts.add(new MismatchConflict("license", this.licenses.getDeclared().toString(), other.getLicenses().getDeclared().toString(), LICENSE_MISMATCH));
            }
            if (!this.licenses.getInfoFromFiles().containsAll(other.getLicenses().getInfoFromFiles())) {
                conflicts.add(new MismatchConflict("license", this.licenses.getInfoFromFiles().toString(), other.getLicenses().getInfoFromFiles().toString(), LICENSE_MISMATCH));
            }
        } else if (this.licenses != null && (this.licenses.getConcluded().size() > 0 || this.licenses.getDeclared().size() > 0 || this.licenses.getInfoFromFiles().size() > 0)) {
            conflicts.add(new MissingConflict("license", this.licenses.getConcluded().toString() + this.licenses.getDeclared().toString() + this.licenses.getInfoFromFiles().toString(), null));
        } else if (other.getLicenses() != null && (other.getLicenses().getConcluded().size() > 0 || other.getLicenses().getDeclared().size() > 0 || other.getLicenses().getInfoFromFiles().size() > 0)) {
            conflicts.add(new MissingConflict("license", null, other.getLicenses().getConcluded().toString() + other.getLicenses().getDeclared().toString() + other.getLicenses().getInfoFromFiles().toString()));
        }
        // HASHES
        if (this.hashes != null && other.getHashes() != null) {
            if (!this.hashes.values().containsAll(other.getHashes().values())) {
                conflicts.add(new MismatchConflict("hash", this.hashes.toString(), other.getHashes().toString(), HASH_MISMATCH));
            }
        } else if (this.hashes != null) {
            conflicts.add(new MissingConflict("hash", this.hashes.toString(), null));
        } else if (other.getHashes() != null) {
            conflicts.add(new MissingConflict("hash", null, other.getHashes().toString()));
        }
        // TODO SWIDs?

        return conflicts.stream().toList();
    }
    public List<Conflict> compare(SPDX23File other) {
        ArrayList<Conflict> conflicts = new ArrayList<>();
        // NAME
        if (this.name != null ^ other.getName() != null) {
            conflicts.add(new MissingConflict("name", this.name, other.getName()));
        } else if (!Objects.equals(this.name, other.getName()) && this.name != null) {
            conflicts.add(new MismatchConflict("name", this.name, other.getName(), NAME_MISMATCH));
        }
        // AUTHOR
        if (this.author != null ^ other.getAuthor() != null) {
            conflicts.add(new MissingConflict("author", this.author, other.getAuthor()));
        } else if (!Objects.equals(this.author, other.getAuthor()) && this.author != null) {
            conflicts.add(new MismatchConflict("author", this.author, other.getAuthor(), AUTHOR_MISMATCH));
        }
        // Licenses
        if (this.licenses != null && other.getLicenses() != null) {
            if (!this.licenses.getConcluded().containsAll(other.getLicenses().getConcluded())) {
                conflicts.add(new MismatchConflict("license", this.licenses.getConcluded().toString(), other.getLicenses().getConcluded().toString(), LICENSE_MISMATCH));
            }
            if (!this.licenses.getDeclared().containsAll(other.getLicenses().getDeclared())) {
                conflicts.add(new MismatchConflict("license", this.licenses.getDeclared().toString(), other.getLicenses().getDeclared().toString(), LICENSE_MISMATCH));
            }
            if (!this.licenses.getInfoFromFiles().containsAll(other.getLicenses().getInfoFromFiles())) {
                conflicts.add(new MismatchConflict("license", this.licenses.getInfoFromFiles().toString(), other.getLicenses().getInfoFromFiles().toString(), LICENSE_MISMATCH));
            }
        } else if (this.licenses != null && (this.licenses.getConcluded().size() > 0 || this.licenses.getDeclared().size() > 0 || this.licenses.getInfoFromFiles().size() > 0)) {
            conflicts.add(new MissingConflict("license", this.licenses.getConcluded().toString() + this.licenses.getDeclared().toString() + this.licenses.getInfoFromFiles().toString(), null));
        } else if (other.getLicenses() != null && (other.getLicenses().getConcluded().size() > 0 || other.getLicenses().getDeclared().size() > 0 || other.getLicenses().getInfoFromFiles().size() > 0)) {
            conflicts.add(new MissingConflict("license", null, other.getLicenses().getConcluded().toString() + other.getLicenses().getDeclared().toString() + other.getLicenses().getInfoFromFiles().toString()));
        }
        // HASHES
        if (this.hashes != null && other.getHashes() != null) {
            if (!this.hashes.values().containsAll(other.getHashes().values())) {
                conflicts.add(new MismatchConflict("hash", this.hashes.toString(), other.getHashes().toString(), HASH_MISMATCH));
            }
        } else if (this.hashes != null) {
            conflicts.add(new MissingConflict("hash", this.hashes.toString(), null));
        } else if (other.getHashes() != null) {
            conflicts.add(new MissingConflict("hash", null, other.getHashes().toString()));
        }
        // TODO SWIDs?

        return conflicts.stream().toList();
    }
    public List<Conflict> compare(SBOMPackage other) {
        ArrayList<Conflict> conflicts = new ArrayList<>();
        // NAME
        if (this.name != null ^ other.getName() != null) {
            conflicts.add(new MissingConflict("name", this.name, other.getName()));
        } else if (!Objects.equals(this.name, other.getName()) && this.name != null) {
            conflicts.add(new MismatchConflict("name", this.name, other.getName(), NAME_MISMATCH));
        }
        // VERSION
        if (this.version != null ^ other.getVersion() != null) {
            conflicts.add(new MissingConflict("version", this.version, other.getVersion()));
        } else if (!Objects.equals(this.version, other.getVersion()) && this.version != null) {
            conflicts.add(new MismatchConflict("version", this.version, other.getVersion(), VERSION_MISMATCH));
        }
        // SUPPLIER
        if (this.supplier != null && other.getSupplier() != null) {
            if (!Objects.equals(this.supplier.getName(), other.getSupplier().getName())) {
                conflicts.add(new MismatchConflict("supplier", this.supplier.getName(), other.getSupplier().getName(), SUPPLIER_MISMATCH));
            }
        } else if (this.supplier != null) {
            conflicts.add(new MissingConflict("supplier", this.supplier.getName(), null));
        } else if (other.getSupplier() != null) {
            conflicts.add(new MissingConflict("supplier", null, other.getSupplier().getName()));
        }
        // AUTHOR
        if (this.author != null ^ other.getAuthor() != null) {
            conflicts.add(new MissingConflict("author", this.author, other.getAuthor()));
        } else if (!Objects.equals(this.author, other.getAuthor()) && this.author != null) {
            conflicts.add(new MismatchConflict("author", this.author, other.getAuthor(), AUTHOR_MISMATCH));
        }
        // PURL
        if (this.purls != null && other.getPURLs() != null) {
            if (!this.purls.containsAll(other.getPURLs())) {
                conflicts.add(new MismatchConflict("purl", this.purls.toString(), other.getPURLs().toString(), PURL_MISMATCH));
            }
        } else if (this.purls != null) {
            conflicts.add(new MissingConflict("purl", this.purls.toString(), null));
        } else if (other.getPURLs() != null) {
            conflicts.add(new MissingConflict("purl", null, other.getPURLs().toString()));
        }
        // CPE
        if (this.cpes != null && other.getCPEs() != null) {
            if (!this.cpes.containsAll(other.getCPEs())) {
                conflicts.add(new MismatchConflict("cpe", this.cpes.toString(), other.getCPEs().toString(), CPE_MISMATCH));
            }
        } else if (this.cpes != null) {
            conflicts.add(new MissingConflict("cpe", this.cpes.toString(), null));
        } else if (other.getCPEs() != null) {
            conflicts.add(new MissingConflict("cpe", null, other.getCPEs().toString()));
        }
        // Licenses
        if (this.licenses != null && other.getLicenses() != null) {
            if (!this.licenses.getConcluded().containsAll(other.getLicenses().getConcluded())) {
                conflicts.add(new MismatchConflict("license", this.licenses.getConcluded().toString(), other.getLicenses().getConcluded().toString(), LICENSE_MISMATCH));
            }
            if (!this.licenses.getDeclared().containsAll(other.getLicenses().getDeclared())) {
                conflicts.add(new MismatchConflict("license", this.licenses.getDeclared().toString(), other.getLicenses().getDeclared().toString(), LICENSE_MISMATCH));
            }
            if (!this.licenses.getInfoFromFiles().containsAll(other.getLicenses().getInfoFromFiles())) {
                conflicts.add(new MismatchConflict("license", this.licenses.getInfoFromFiles().toString(), other.getLicenses().getInfoFromFiles().toString(), LICENSE_MISMATCH));
            }
        } else if (this.licenses != null && (this.licenses.getConcluded().size() > 0 || this.licenses.getDeclared().size() > 0 || this.licenses.getInfoFromFiles().size() > 0)) {
            conflicts.add(new MissingConflict("license", this.licenses.getConcluded().toString() + this.licenses.getDeclared().toString() + this.licenses.getInfoFromFiles().toString(), null));
        } else if (other.getLicenses() != null && (other.getLicenses().getConcluded().size() > 0 || other.getLicenses().getDeclared().size() > 0 || other.getLicenses().getInfoFromFiles().size() > 0)) {
            conflicts.add(new MissingConflict("license", null, other.getLicenses().getConcluded().toString() + other.getLicenses().getDeclared().toString() + other.getLicenses().getInfoFromFiles().toString()));
        }
        // HASHES
        if (this.hashes != null && other.getHashes() != null) {
            if (!this.hashes.values().containsAll(other.getHashes().values())) {
                conflicts.add(new MismatchConflict("hash", this.hashes.toString(), other.getHashes().toString(), HASH_MISMATCH));
            }
        } else if (this.hashes != null) {
            conflicts.add(new MissingConflict("hash", this.hashes.toString(), null));
        } else if (other.getHashes() != null) {
            conflicts.add(new MissingConflict("hash", null, other.getHashes().toString()));
        }
        // TODO SWIDs?

        return conflicts.stream().toList();
    }
    public List<Conflict> compare(CDX14Package other) {
        ArrayList<Conflict> conflicts = new ArrayList<>();
        // NAME
        if (this.name != null ^ other.getName() != null) {
            conflicts.add(new MissingConflict("name", this.name, other.getName()));
        } else if (!Objects.equals(this.name, other.getName()) && this.name != null) {
            conflicts.add(new MismatchConflict("name", this.name, other.getName(), NAME_MISMATCH));
        }
        // VERSION
        if (this.version != null ^ other.getVersion() != null) {
            conflicts.add(new MissingConflict("version", this.version, other.getVersion()));
        } else if (!Objects.equals(this.version, other.getVersion()) && this.version != null) {
            conflicts.add(new MismatchConflict("version", this.version, other.getVersion(), VERSION_MISMATCH));
        }
        // SUPPLIER
        if (this.supplier != null && other.getSupplier() != null) {
            if (!Objects.equals(this.supplier.getName(), other.getSupplier().getName())) {
                conflicts.add(new MismatchConflict("supplier", this.supplier.getName(), other.getSupplier().getName(), SUPPLIER_MISMATCH));
            }
        } else if (this.supplier != null) {
            conflicts.add(new MissingConflict("supplier", this.supplier.getName(), null));
        } else if (other.getSupplier() != null) {
            conflicts.add(new MissingConflict("supplier", null, other.getSupplier().getName()));
        }
        // AUTHOR
        if (this.author != null ^ other.getAuthor() != null) {
            conflicts.add(new MissingConflict("author", this.author, other.getAuthor()));
        } else if (!Objects.equals(this.author, other.getAuthor()) && this.author != null) {
            conflicts.add(new MismatchConflict("author", this.author, other.getAuthor(), AUTHOR_MISMATCH));
        }
        // PURL
        if (this.purls != null && other.getPURLs() != null) {
            if (!this.purls.containsAll(other.getPURLs())) {
                conflicts.add(new MismatchConflict("purl", this.purls.toString(), other.getPURLs().toString(), PURL_MISMATCH));
            }
        } else if (this.purls != null) {
            conflicts.add(new MissingConflict("purl", this.purls.toString(), null));
        } else if (other.getPURLs() != null) {
            conflicts.add(new MissingConflict("purl", null, other.getPURLs().toString()));
        }
        // CPE
        if (this.cpes != null && other.getCPEs() != null) {
            if (!this.cpes.containsAll(other.getCPEs())) {
                conflicts.add(new MismatchConflict("cpe", this.cpes.toString(), other.getCPEs().toString(), CPE_MISMATCH));
            }
        } else if (this.cpes != null) {
            conflicts.add(new MissingConflict("cpe", this.cpes.toString(), null));
        } else if (other.getCPEs() != null) {
            conflicts.add(new MissingConflict("cpe", null, other.getCPEs().toString()));
        }
        // Licenses
        if (this.licenses != null && other.getLicenses() != null) {
            if (!this.licenses.getConcluded().containsAll(other.getLicenses().getConcluded())) {
                conflicts.add(new MismatchConflict("license", this.licenses.getConcluded().toString(), other.getLicenses().getConcluded().toString(), LICENSE_MISMATCH));
            }
            if (!this.licenses.getDeclared().containsAll(other.getLicenses().getDeclared())) {
                conflicts.add(new MismatchConflict("license", this.licenses.getDeclared().toString(), other.getLicenses().getDeclared().toString(), LICENSE_MISMATCH));
            }
            if (!this.licenses.getInfoFromFiles().containsAll(other.getLicenses().getInfoFromFiles())) {
                conflicts.add(new MismatchConflict("license", this.licenses.getInfoFromFiles().toString(), other.getLicenses().getInfoFromFiles().toString(), LICENSE_MISMATCH));
            }
        } else if (this.licenses != null && (this.licenses.getConcluded().size() > 0 || this.licenses.getDeclared().size() > 0 || this.licenses.getInfoFromFiles().size() > 0)) {
            conflicts.add(new MissingConflict("license", this.licenses.getConcluded().toString() + this.licenses.getDeclared().toString() + this.licenses.getInfoFromFiles().toString(), null));
        } else if (other.getLicenses() != null && (other.getLicenses().getConcluded().size() > 0 || other.getLicenses().getDeclared().size() > 0 || other.getLicenses().getInfoFromFiles().size() > 0)) {
            conflicts.add(new MissingConflict("license", null, other.getLicenses().getConcluded().toString() + other.getLicenses().getDeclared().toString() + other.getLicenses().getInfoFromFiles().toString()));
        }
        // HASHES
        if (this.hashes != null && other.getHashes() != null) {
            if (!this.hashes.values().containsAll(other.getHashes().values())) {
                conflicts.add(new MismatchConflict("hash", this.hashes.toString(), other.getHashes().toString(), HASH_MISMATCH));
            }
        } else if (this.hashes != null) {
            conflicts.add(new MissingConflict("hash", this.hashes.toString(), null));
        } else if (other.getHashes() != null) {
            conflicts.add(new MissingConflict("hash", null, other.getHashes().toString()));
        }
        // PUBLISHER
        if (this.publisher != null ^ other.getPublisher() != null) {
            conflicts.add(new MissingConflict("publisher", this.publisher, other.getPublisher()));
        } else if (!Objects.equals(this.publisher, other.getPublisher()) && this.publisher != null) {
            conflicts.add(new MismatchConflict("publisher", this.publisher, other.getPublisher(), PUBLISHER_MISMATCH));
        }
        // TODO SWIDs?

        return conflicts.stream().toList();
    }
}
