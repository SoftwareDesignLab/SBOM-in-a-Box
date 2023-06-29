package org.svip.sbom.model.objects;

import org.svip.sbom.model.interfaces.schemas.CycloneDX14.CDX14Schema;
import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23Schema;
import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbom.model.shared.Relationship;
import org.svip.sbom.model.shared.util.ExternalReference;

import java.util.Map;
import java.util.Set;
// todo
public class SVIPSBOM implements CDX14Schema, SPDX23Schema{

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
