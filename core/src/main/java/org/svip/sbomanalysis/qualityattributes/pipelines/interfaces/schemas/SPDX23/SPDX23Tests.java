package org.svip.sbomanalysis.qualityattributes.pipelines.interfaces.schemas.SPDX23;

import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbomanalysis.qualityattributes.pipelines.interfaces.generics.QAPipeline;
import org.svip.sbomanalysis.qualityattributes.oldtests.Result;

import java.util.Set;

/**
 * file: SPDX23Tests.java
 * An interface that contains a collection of tests specific to
 * SPDX 2.3 SBOMs
 *
 * @author Matthew Morrison
 */
public interface SPDX23Tests extends QAPipeline {

    /**
     * Test the SPDX SBOM Metadata to see if it contains a data license of
     * CC0-1.0
     * @param sbom SPDX 2.3 SBOM to test
     * @return The result of checking for the SBOM's data license
     */
    Set<Result> hasDataLicense(SBOM sbom);

    /**
     * Test every component in a given SPDX 2.3 SBOM for a valid SPDXID
     * @param sbom SPDX 2.3 SBOM to test
     * @return a collection of results for every component in the SBOM
     */
    Set<Result> hasSPDXID(SBOM sbom);

    /**
     * Test the SPDX 2.3 sbom's metadata for a valid document namespace
     * @param sbom SPDX 2.3 SBOM to test
     * @return the result of if the sbom's metadata contains a valid
     * document namespace
     */
    Set<Result> hasDocumentNamespace(SBOM sbom);

    /**
     * Given an SPDX 2.3 SBOM, check that it has creator and created info
     * @param sbom SPDX 2.3 SBOM to test
     * @return a collection of results of if the sbom contains creator and
     * created time info
     */
    Set<Result> hasCreationInfo(SBOM sbom);

    /**
     * Test every component in the SPDX 2.3 SBOM for the PackageDownloadLocation field
     * and that it has a value
     * @param sbom SPDX 2.3 SBOM to test
     * @return a collection of results from each component and if it contains
     * info about its download location
     */
    Set<Result> hasDownloadLocation(SBOM sbom);

    /**
     * Test all components in a given SPDX 2.3 SBOM for their verification code
     * based on FilesAnalyzed
     * @param sbom SPDX 2.3 SBOM to test
     * @return a collection of results for each component in the SBOM
     */
    Set<Result> hasVerificationCode(SBOM sbom);

    /**
     * Check all components in a given SPDX 2.3 SBOM for extracted licenses not on
     * the SPDX license list
     * @param sbom SPDX 2.3 SBOM to test
     * @return a collection of results if any extracted licenses exist
     * in the SBOM
     */
    Set<Result> hasExtractedLicenses(SBOM sbom);

    /**
     * Check all components in a given SPDX 2.3 SBOM for extracted licenses not on
     * the SPDX license list
     * If an extracted license is present, check for the following fields:
     * LicenseName, LicenseID, LicenseCrossReference
     * @param sbom SPDX 2.3 SBOM to test
     * @return a collection of results if any extracted licenses exist
     * in the SBOM
     */
    Set<Result> extractedLicenseMinElements(SBOM sbom);
}
