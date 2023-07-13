package org.svip.sbom.model.objects.SPDX23;

import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOMPackage;
import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23Component;
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
                conflicts.add(new MismatchConflict("licenses", this.licenses.getConcluded().toString(), other.getLicenses().getConcluded().toString(), LICENSE_MISMATCH));
            }
            if (!this.licenses.getDeclared().containsAll(other.getLicenses().getDeclared())) {
                conflicts.add(new MismatchConflict("licenses", this.licenses.getDeclared().toString(), other.getLicenses().getDeclared().toString(), LICENSE_MISMATCH));
            }
            if (!this.licenses.getInfoFromFiles().containsAll(other.getLicenses().getInfoFromFiles())) {
                conflicts.add(new MismatchConflict("licenses", this.licenses.getInfoFromFiles().toString(), other.getLicenses().getInfoFromFiles().toString(), LICENSE_MISMATCH));
            }
        } else if (this.licenses != null) {
            conflicts.add(new MissingConflict("licenses", this.licenses.toString(), null));
        } else if (other.getLicenses() != null) {
            conflicts.add(new MissingConflict("licenses", null, other.getLicenses().toString()));
        }
        // HASHES
        if (this.hashes != null && other.getHashes() != null) {
            if (!this.hashes.values().containsAll(other.getHashes().values())) {
                conflicts.add(new MismatchConflict("hashes", this.hashes.toString(), other.getHashes().toString(), HASH_MISMATCH));
            }
        } else if (this.hashes != null) {
            conflicts.add(new MissingConflict("hashes", this.hashes.toString(), null));
        } else if (other.getHashes() != null) {
            conflicts.add(new MissingConflict("hashes", null, other.getHashes().toString()));
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
        // AUTHOR
        if (this.author != null ^ other.getAuthor() != null) {
            conflicts.add(new MissingConflict("author", this.author, other.getAuthor()));
        } else if (!Objects.equals(this.author, other.getAuthor()) && this.author != null) {
            conflicts.add(new MismatchConflict("author", this.author, other.getAuthor(), AUTHOR_MISMATCH));
        }
        // Licenses
        if (this.licenses != null && other.getLicenses() != null) {
            if (!this.licenses.getConcluded().containsAll(other.getLicenses().getConcluded())) {
                conflicts.add(new MismatchConflict("licenses", this.licenses.getConcluded().toString(), other.getLicenses().getConcluded().toString(), LICENSE_MISMATCH));
            }
            if (!this.licenses.getDeclared().containsAll(other.getLicenses().getDeclared())) {
                conflicts.add(new MismatchConflict("licenses", this.licenses.getDeclared().toString(), other.getLicenses().getDeclared().toString(), LICENSE_MISMATCH));
            }
            if (!this.licenses.getInfoFromFiles().containsAll(other.getLicenses().getInfoFromFiles())) {
                conflicts.add(new MismatchConflict("licenses", this.licenses.getInfoFromFiles().toString(), other.getLicenses().getInfoFromFiles().toString(), LICENSE_MISMATCH));
            }
        } else if (this.licenses != null) {
            conflicts.add(new MissingConflict("licenses", this.licenses.toString(), null));
        } else if (other.getLicenses() != null) {
            conflicts.add(new MissingConflict("licenses", null, other.getLicenses().toString()));
        }
        // HASHES
        if (this.hashes != null && other.getHashes() != null) {
            if (!this.hashes.values().containsAll(other.getHashes().values())) {
                conflicts.add(new MismatchConflict("hashes", this.hashes.toString(), other.getHashes().toString(), HASH_MISMATCH));
            }
        } else if (this.hashes != null) {
            conflicts.add(new MissingConflict("hashes", this.hashes.toString(), null));
        } else if (other.getHashes() != null) {
            conflicts.add(new MissingConflict("hashes", null, other.getHashes().toString()));
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
                conflicts.add(new MismatchConflict("licenses", this.licenses.getConcluded().toString(), other.getLicenses().getConcluded().toString(), LICENSE_MISMATCH));
            }
            if (!this.licenses.getDeclared().containsAll(other.getLicenses().getDeclared())) {
                conflicts.add(new MismatchConflict("licenses", this.licenses.getDeclared().toString(), other.getLicenses().getDeclared().toString(), LICENSE_MISMATCH));
            }
            if (!this.licenses.getInfoFromFiles().containsAll(other.getLicenses().getInfoFromFiles())) {
                conflicts.add(new MismatchConflict("licenses", this.licenses.getInfoFromFiles().toString(), other.getLicenses().getInfoFromFiles().toString(), LICENSE_MISMATCH));
            }
        } else if (this.licenses != null) {
            conflicts.add(new MissingConflict("licenses", this.licenses.toString(), null));
        } else if (other.getLicenses() != null) {
            conflicts.add(new MissingConflict("licenses", null, other.getLicenses().toString()));
        }
        // HASHES
        if (this.hashes != null && other.getHashes() != null) {
            if (!this.hashes.values().containsAll(other.getHashes().values())) {
                conflicts.add(new MismatchConflict("hashes", this.hashes.toString(), other.getHashes().toString(), HASH_MISMATCH));
            }
        } else if (this.hashes != null) {
            conflicts.add(new MissingConflict("hashes", this.hashes.toString(), null));
        } else if (other.getHashes() != null) {
            conflicts.add(new MissingConflict("hashes", null, other.getHashes().toString()));
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
                conflicts.add(new MismatchConflict("purls", this.purls.toString(), other.getPURLs().toString(), PURL_MISMATCH));
            }
        } else if (this.purls != null) {
            conflicts.add(new MissingConflict("purls", this.purls.toString(), null));
        } else if (other.getPURLs() != null) {
            conflicts.add(new MissingConflict("purls", null, other.getPURLs().toString()));
        }
        // CPE
        if (this.cpes != null && other.getCPEs() != null) {
            if (!this.cpes.containsAll(other.getCPEs())) {
                conflicts.add(new MismatchConflict("cpes", this.cpes.toString(), other.getCPEs().toString(), CPE_MISMATCH));
            }
        } else if (this.cpes != null) {
            conflicts.add(new MissingConflict("cpes", this.cpes.toString(), null));
        } else if (other.getCPEs() != null) {
            conflicts.add(new MissingConflict("cpes", null, other.getCPEs().toString()));
        }
        // Licenses
        if (this.licenses != null && other.getLicenses() != null) {
            if (!this.licenses.getConcluded().containsAll(other.getLicenses().getConcluded())) {
                conflicts.add(new MismatchConflict("licenses", this.licenses.getConcluded().toString(), other.getLicenses().getConcluded().toString(), LICENSE_MISMATCH));
            }
            if (!this.licenses.getDeclared().containsAll(other.getLicenses().getDeclared())) {
                conflicts.add(new MismatchConflict("licenses", this.licenses.getDeclared().toString(), other.getLicenses().getDeclared().toString(), LICENSE_MISMATCH));
            }
            if (!this.licenses.getInfoFromFiles().containsAll(other.getLicenses().getInfoFromFiles())) {
                conflicts.add(new MismatchConflict("licenses", this.licenses.getInfoFromFiles().toString(), other.getLicenses().getInfoFromFiles().toString(), LICENSE_MISMATCH));
            }
        } else if (this.licenses != null) {
            conflicts.add(new MissingConflict("licenses", this.licenses.toString(), null));
        } else if (other.getLicenses() != null) {
            conflicts.add(new MissingConflict("licenses", null, other.getLicenses().toString()));
        }
        // HASHES
        if (this.hashes != null && other.getHashes() != null) {
            if (!this.hashes.values().containsAll(other.getHashes().values())) {
                conflicts.add(new MismatchConflict("hashes", this.hashes.toString(), other.getHashes().toString(), HASH_MISMATCH));
            }
        } else if (this.hashes != null) {
            conflicts.add(new MissingConflict("hashes", this.hashes.toString(), null));
        } else if (other.getHashes() != null) {
            conflicts.add(new MissingConflict("hashes", null, other.getHashes().toString()));
        }
        // TODO SWIDs?

        return conflicts.stream().toList();
    }
}
