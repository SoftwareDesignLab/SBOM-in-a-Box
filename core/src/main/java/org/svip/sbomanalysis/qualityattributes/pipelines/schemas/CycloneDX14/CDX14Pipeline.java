package org.svip.sbomanalysis.qualityattributes.pipelines.schemas.CycloneDX14;

import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbomanalysis.qualityattributes.oldpipeline.QualityReport;
import org.svip.sbomanalysis.qualityattributes.pipelines.interfaces.schemas.CycloneDX14.CDX14Tests;
import org.svip.sbomanalysis.qualityattributes.oldtests.Result;

import java.util.Set;

public class CDX14Pipeline implements CDX14Tests {
    @Override
    public QualityReport process(String uid, SBOM sbom) {
        return null;
    }

    @Override
    public Set<Result> hasBomVersion(SBOM sbom) {
        return null;
    }

    @Override
    public Set<Result> validSerialNumber(SBOM sbom) {
        return null;
    }

    @Override
    public Set<Result> hasBomRef(SBOM sbom) {
        return null;
    }
}
