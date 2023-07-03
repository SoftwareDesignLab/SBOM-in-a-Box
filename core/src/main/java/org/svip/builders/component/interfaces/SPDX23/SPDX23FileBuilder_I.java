package org.svip.builders.component.interfaces.SPDX23;

/**
 * file: SPDX23FileBuilder_I.java
 * Generic File Builder interface for SPDX 2.3
 * SBOMs
 *
 * @author Matthew Morrison
 */
public interface SPDX23FileBuilder_I extends SPDX23ComponentBuilder {

    /**
     * Set the file notice of the SPDX File
     * @param fileNotice the file notice
     * @return an SPDX23FileBuilder_I
     */
    SPDX23FileBuilder_I setFileNotice(String fileNotice);
}
