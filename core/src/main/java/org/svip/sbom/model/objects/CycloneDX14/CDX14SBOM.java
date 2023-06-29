package org.svip.sbom.model.objects.CycloneDX14;

import org.svip.sbom.model.interfaces.schemas.CycloneDX14.CDX14Schema;
import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbom.model.shared.Relationship;
import org.svip.sbom.model.shared.util.ExternalReference;
import org.svip.sbom.model.interfaces.schemas.CycloneDX14.CDX14Schema;

import java.util.Map;
import java.util.Set;
// todo
public class CDX14SBOM implements CDX14Schema {
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
}
