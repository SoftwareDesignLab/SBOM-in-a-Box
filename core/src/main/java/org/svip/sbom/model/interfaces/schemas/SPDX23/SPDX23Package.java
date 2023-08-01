package org.svip.sbom.model.interfaces.schemas.SPDX23;

import org.svip.sbom.model.interfaces.generics.SBOMPackage;
import org.svip.compare.conflicts.Conflict;

import java.util.List;

/**
 * File: SPDX23Package.java
 * <p>
 * SPDX 2.3 specific fields
 * <p>
 * Source: <a href="https://spdx.github.io/spdx-spec/v2.3/package-information/">https://spdx.github.io/spdx-spec/v2.3/package-information/</a>
 *
 * @author Derek Garcia
 */
public interface SPDX23Package extends SBOMPackage, SPDX23Component {
    /**
     * @return Download location of package
     */
    String getDownloadLocation();

    /**
     * @return filename of the package as reported by the system
     */
    String getFileName();

    /**
     * @return Files have been analyzed
     */
    Boolean getFilesAnalyzed();

    /**
     * @return SPDX Verification code
     */
    String getVerificationCode();

    /**
     * @return Homepage of package
     */
    String getHomePage();

    /**
     * @return Information about the source of the package
     */
    String getSourceInfo();

    /**
     * @return When the package was release
     */
    String getReleaseDate();

    /**
     * @return When the package was built
     */
    String getBuiltDate();

    /**
     * @return When the package will no longer be supported
     */
    String getValidUntilDate();

    /**
     * Compare against another SPDX 2.3 Package
     *
     * @param other Other SPDX 2.3 Package to compare against
     * @return List of conflicts
     */
    List<Conflict> compare(SPDX23Package other);
}
