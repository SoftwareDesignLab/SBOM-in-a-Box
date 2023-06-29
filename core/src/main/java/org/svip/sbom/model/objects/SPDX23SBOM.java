package org.svip.sbom.model.objects;

import org.svip.sbom.model.metadata.CreationData;
import org.svip.sbom.model.old.Relationship;
import org.svip.sbom.model.util.ExternalReference;

import java.util.Map;
import java.util.Set;

import org.svip.sbom.model.interfaces.generics.schemas.SPDX23Schema;

public class SPDX23SBOM implements SPDX23Schema{
    @Override
    public String getFormat() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getUID() {
        return null;
    }

    @Override
    public String getVersion() {
        return null;
    }

    @Override
    public String getSpecVersion() {
        return null;
    }

    @Override
    public String getLicenses() {
        return null;
    }

    @Override
    public CreationData getCreationData() {
        return null;
    }

    @Override
    public String getDocumentComment() {
        return null;
    }

    @Override
    public Map<String, Set<Relationship>> getRelationships() {
        return null;
    }

    @Override
    public Set<ExternalReference> getExternalReferences() {
        return null;
    }

    @Override
    public String getSPDXLicenseListVersion() {
        return null;
    }
}
