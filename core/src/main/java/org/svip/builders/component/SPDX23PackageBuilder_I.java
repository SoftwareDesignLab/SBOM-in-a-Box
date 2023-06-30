package org.svip.builders.component;

import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23Package;

/**
 * file: SPDX23PackageBuilder_I.java
 * Generic Package Builder interface for SPDX 2.3
 * SBOM components
 *
 * @author Matthew Morrison
 */
public interface SPDX23PackageBuilder_I extends SPDX23ComponentBuilder, SBOMComponentBuilder{

    /**
     * Set the file notice of the package
     * @param fileNotice the package's file notice
     * @return an SPDX23PackageBuilder_I
     */
    SPDX23PackageBuilder_I setFileNotice(String fileNotice);

    /**
     * Set the download location of the package
     * @param downloadLocation the package's download location
     * @return an SPDX23PackageBuilder_I
     */
    SPDX23PackageBuilder_I setDownloadLocation(String downloadLocation);

    /**
     * Set the file name of the package
     * @param fileName the package's file name
     * @return an SPDX23PackageBuilder_I
     */
    SPDX23PackageBuilder_I setFileName(String fileName);

    /**
     * Set if the package's files were analyzed or not
     * @param filesAnalyzed a boolean if the files were analyzed
     * @return an SPDX23PackageBuilder_I
     */
    SPDX23PackageBuilder_I setFilesAnalyzed(Boolean filesAnalyzed);

    /**
     * Set the verification code of the package
     * @param verificationCode the package's verification code
     * @return an SPDX23PackageBuilder_I
     */
    SPDX23PackageBuilder_I setVerificationCode(String verificationCode);

    /**
     * Set the home page of the package
     * @param homePage the package's home page
     * @return an SPDX23PackageBuilder_I
     */
    SPDX23PackageBuilder_I setHomePage(String homePage);

    /**
     * Set the source information for the package
     * @param sourceInfo the package's source information
     * @return an SPDX23PackageBuilder_I
     */
    SPDX23PackageBuilder_I setSourceInfo(String sourceInfo);

    /**
     * Set the release date of the package
     * @param releaseDate the package's release date
     * @return an SPDX23PackageBuilder_I
     */
    SPDX23PackageBuilder_I setReleaseDate(String releaseDate);

    /**
     * Set the build date of the package
     * @param buildDate the package's build date
     * @return an SPDX23PackageBuilder_I
     */
    SPDX23PackageBuilder_I setBuildDate(String buildDate);

    /**
     * Set the valid until date for the package
     * @param validUntilDate the package's valid until date
     * @return an SPDX23PackageBuilder_I
     */
    SPDX23PackageBuilder_I setValidUntilDate(String validUntilDate);
}
