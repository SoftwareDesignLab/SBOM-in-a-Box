package org.svip.sbom.model.objects.CycloneDX14;

import org.svip.sbom.model.interfaces.schemas.CycloneDX14.CDX14Package;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.shared.util.Description;
import org.svip.sbom.model.shared.util.ExternalReference;

import java.util.Set;

// todo
public class CDX14ComponentObject implements CDX14Package {
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
}
