package org.svip.sbom.model.interfaces.schemas.SPDX23;

import org.svip.compare.conflicts.Conflict;

import java.util.List;

/**
 * File: SPDX23File.java
 * <p>
 * SPDX 2.3 specific fields
 * <p>
 * Source: <a href="https://spdx.github.io/spdx-spec/v2.3/file-information/">https://spdx.github.io/spdx-spec/v2.3/file-information</a>
 *
 * @author Derek Garcia
 */
public interface SPDX23File extends SPDX23Component {

    /**
     * @return License notices or other such related notices
     */
    String getFileNotice();

    /**
     * Compare against another SPDX 2.3 File
     *
     * @param other Other SPDX 2.3 File to compare against
     * @return List of conflicts
     */
    List<Conflict> compare(SPDX23File other);
}
