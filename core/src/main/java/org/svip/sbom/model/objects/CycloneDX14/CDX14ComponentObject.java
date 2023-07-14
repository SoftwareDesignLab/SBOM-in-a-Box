package org.svip.sbom.model.objects.CycloneDX14;

import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOMPackage;
import org.svip.sbom.model.interfaces.schemas.CycloneDX14.CDX14Package;
import org.svip.sbom.model.objects.SPDX23.SPDX23PackageObject;
import org.svip.sbom.model.objects.SVIPComponentObject;
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
 * file: CDX14ComponentObject.java
 * Holds information for a CycloneDX 1.4 component object
 *
 * @author Derek Garcia
 * @author Matthew Morrison
 */
public class CDX14ComponentObject implements CDX14Package {

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
     * Constructor to build a new CDX 1.4 Component Object
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
     * @param mimeType component mime type
     * @param publisher component publisher
     * @param scope component scope
     * @param group component group
     * @param externalReferences component external references
     * @param properties component properties
     */
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
        if (other instanceof SPDX23PackageObject) {
            // VERSION
            if (this.version != null ^ ((SPDX23PackageObject)other).getVersion() != null) {
                conflicts.add(new MissingConflict("version", this.version, ((SPDX23PackageObject)other).getVersion()));
            } else if (!Objects.equals(this.version, ((SPDX23PackageObject)other).getVersion()) && this.version != null) {
                conflicts.add(new MismatchConflict("version", this.version, ((SPDX23PackageObject)other).getVersion(), VERSION_MISMATCH));
            }
            // SUPPLIER
            if (this.supplier != null && ((SPDX23PackageObject)other).getSupplier() != null) {
                if (!Objects.equals(this.supplier.getName(), ((SPDX23PackageObject)other).getSupplier().getName())) {
                    conflicts.add(new MismatchConflict("supplier", this.supplier.getName(), ((SPDX23PackageObject)other).getSupplier().getName(), SUPPLIER_MISMATCH));
                }
            } else if (this.supplier != null) {
                conflicts.add(new MissingConflict("supplier", this.supplier.getName(), null));
            } else if (((SPDX23PackageObject)other).getSupplier() != null) {
                conflicts.add(new MissingConflict("supplier", null, ((SPDX23PackageObject)other).getSupplier().getName()));
            }
            // PURL
            if (this.purls != null && ((SPDX23PackageObject)other).getPURLs() != null) {
                if (!this.purls.containsAll(((SPDX23PackageObject)other).getPURLs())) {
                    conflicts.add(new MismatchConflict("purl", this.purls.toString(), ((SPDX23PackageObject)other).getPURLs().toString(), PURL_MISMATCH));
                }
            } else if (this.purls != null) {
                conflicts.add(new MissingConflict("purl", this.purls.toString(), null));
            } else if (((SPDX23PackageObject)other).getPURLs() != null) {
                conflicts.add(new MissingConflict("purl", null, ((SPDX23PackageObject)other).getPURLs().toString()));
            }
            // CPE
            if (this.cpes != null && ((SPDX23PackageObject)other).getCPEs() != null) {
                if (!this.cpes.containsAll(((SPDX23PackageObject)other).getCPEs())) {
                    conflicts.add(new MismatchConflict("cpe", this.cpes.toString(), ((SPDX23PackageObject)other).getCPEs().toString(), CPE_MISMATCH));
                }
            } else if (this.cpes != null) {
                conflicts.add(new MissingConflict("cpe", this.cpes.toString(), null));
            } else if (((SPDX23PackageObject)other).getCPEs() != null) {
                conflicts.add(new MissingConflict("cpe", null, ((SPDX23PackageObject)other).getCPEs().toString()));
            }
        } else if (other instanceof SVIPComponentObject) {
            // VERSION
            if (this.version != null ^ ((SVIPComponentObject)other).getVersion() != null) {
                conflicts.add(new MissingConflict("version", this.version, ((SVIPComponentObject)other).getVersion()));
            } else if (!Objects.equals(this.version, ((SVIPComponentObject)other).getVersion()) && this.version != null) {
                conflicts.add(new MismatchConflict("version", this.version, ((SVIPComponentObject)other).getVersion(), VERSION_MISMATCH));
            }
            // SUPPLIER
            if (this.supplier != null && ((SVIPComponentObject)other).getSupplier() != null) {
                if (!Objects.equals(this.supplier.getName(), ((SVIPComponentObject)other).getSupplier().getName())) {
                    conflicts.add(new MismatchConflict("supplier", this.supplier.getName(), ((SVIPComponentObject)other).getSupplier().getName(), SUPPLIER_MISMATCH));
                }
            } else if (this.supplier != null) {
                conflicts.add(new MissingConflict("supplier", this.supplier.getName(), null));
            } else if (((SVIPComponentObject)other).getSupplier() != null) {
                conflicts.add(new MissingConflict("supplier", null, ((SVIPComponentObject)other).getSupplier().getName()));
            }
            // PURL
            if (this.purls != null && ((SVIPComponentObject)other).getPURLs() != null) {
                if (!this.purls.containsAll(((SVIPComponentObject)other).getPURLs())) {
                    conflicts.add(new MismatchConflict("purl", this.purls.toString(), ((SVIPComponentObject)other).getPURLs().toString(), PURL_MISMATCH));
                }
            } else if (this.purls != null) {
                conflicts.add(new MissingConflict("purl", this.purls.toString(), null));
            } else if (((SVIPComponentObject)other).getPURLs() != null) {
                conflicts.add(new MissingConflict("purl", null, ((SVIPComponentObject)other).getPURLs().toString()));
            }
            // CPE
            if (this.cpes != null && ((SVIPComponentObject)other).getCPEs() != null) {
                if (!this.cpes.containsAll(((SVIPComponentObject)other).getCPEs())) {
                    conflicts.add(new MismatchConflict("cpe", this.cpes.toString(), ((SVIPComponentObject)other).getCPEs().toString(), CPE_MISMATCH));
                }
            } else if (this.cpes != null) {
                conflicts.add(new MissingConflict("cpe", this.cpes.toString(), null));
            } else if (((SVIPComponentObject)other).getCPEs() != null) {
                conflicts.add(new MissingConflict("cpe", null, ((SVIPComponentObject)other).getCPEs().toString()));
            }
            // PUBLISHER
            if (this.publisher != null ^ ((SVIPComponentObject)other).getPublisher() != null) {
                conflicts.add(new MissingConflict("publisher", this.publisher, ((SVIPComponentObject)other).getPublisher()));
            } else if (!Objects.equals(this.publisher, ((SVIPComponentObject)other).getPublisher()) && this.publisher != null) {
                conflicts.add(new MismatchConflict("publisher", this.publisher, ((SVIPComponentObject)other).getPublisher(), PUBLISHER_MISMATCH));
            }
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

