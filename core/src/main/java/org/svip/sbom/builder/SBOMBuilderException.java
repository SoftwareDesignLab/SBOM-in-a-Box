package org.svip.sbom.builder;

import org.svip.utils.Debug;

/**
 * Exception thrown when there is an error building an SBOM
 *
 * @author Juan Francisco Patino
 */
public class SBOMBuilderException extends Exception {
    public SBOMBuilderException(String message) {
        super(message);
        Debug.log(Debug.LOG_TYPE.ERROR, message);
    }
}