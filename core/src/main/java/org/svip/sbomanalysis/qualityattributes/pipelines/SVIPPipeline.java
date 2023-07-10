package org.svip.sbomanalysis.qualityattributes.pipelines;

import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbomanalysis.qualityattributes.QualityReport;
import org.svip.sbomanalysis.qualityattributes.interfaces.schemas.CycloneDX14.CDX14Tests;
import org.svip.sbomanalysis.qualityattributes.interfaces.schemas.SPDX23.SPDX23Tests;
import org.svip.sbomanalysis.qualityattributes.oldtests.Result;

import java.util.Set;

public class SVIPPipeline implements CDX14Tests, SPDX23Tests {
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

    @Override
    public Set<Result> hasDataLicense(SBOM sbom) {
        return null;
    }

    @Override
    public Set<Result> hasSPDXID(SBOM sbom) {
        return null;
    }

    @Override
    public Set<Result> hasDocumentNamespace(SBOM sbom) {
        return null;
    }

    @Override
    public Set<Result> hasCreationInfo(SBOM sbom) {
        return null;
    }

    @Override
    public Set<Result> hasDownloadLocation(SBOM sbom) {
        return null;
    }

    @Override
    public Set<Result> hasVerificationCode(SBOM sbom) {
        return null;
    }

    @Override
    public Set<Result> hasExtractedLicenses(SBOM sbom) {
        return null;
    }

    @Override
    public Set<Result> extractedLicenseMinElements(SBOM sbom) {
        return null;
    }
}
