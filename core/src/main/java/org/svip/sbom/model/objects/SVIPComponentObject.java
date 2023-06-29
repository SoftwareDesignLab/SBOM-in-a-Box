package org.svip.sbom.model.objects;

import org.svip.sbom.model.interfaces.schemas.CycloneDX14.CDX14Package;
import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23File;
import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23Package;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.shared.util.Description;
import org.svip.sbom.model.shared.util.ExternalReference;
import org.svip.sbom.model.shared.util.LicenseCollection;

import java.util.Map;
import java.util.Set;

//todo
public class SVIPComponentObject implements CDX14Package, SPDX23Package, SPDX23File {
    @Override
    public String getType() {
        return null;
    }

    @Override
    public String getUID() {
        return null;
    }

    @Override
    public String getAuthor() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public LicenseCollection getLicenses() {
        return null;
    }

    @Override
    public String getCopyright() {
        return null;
    }

    @Override
    public Map<String, String> getHashes() {
        return null;
    }

    @Override
    public Organization getSupplier() {
        return null;
    }

    @Override
    public String getVersion() {
        return null;
    }

    @Override
    public Description getDescription() {
        return null;
    }

    @Override
    public Set<String> getCPEs() {
        return null;
    }

    @Override
    public Set<String> getPURLs() {
        return null;
    }

    @Override
    public Set<ExternalReference> getExternalReferences() {
        return null;
    }

    @Override
    public String getMimeType() {
        return null;
    }

    @Override
    public String getPublisher() {
        return null;
    }

    @Override
    public String getScope() {
        return null;
    }

    @Override
    public String getGroup() {
        return null;
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
        return null;
    }

    @Override
    public String getDownloadLocation() {
        return null;
    }

    @Override
    public String getFileName() {
        return null;
    }

    @Override
    public Boolean getFilesAnalyzed() {
        return null;
    }

    @Override
    public String getVerificationCode() {
        return null;
    }

    @Override
    public String getHomePage() {
        return null;
    }

    @Override
    public String getSourceInfo() {
        return null;
    }

    @Override
    public String getReleaseDate() {
        return null;
    }

    @Override
    public String getBuiltDate() {
        return null;
    }

    @Override
    public String getValidUntilDate() {
        return null;
    }
}
