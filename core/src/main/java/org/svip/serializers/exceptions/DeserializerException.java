package org.svip.serializers.exceptions;

import org.svip.utils.Debug;

/**
 * Exception thrown when there is an error in deserializing an SBOM
 *
 * @author Juan Francisco Patino
 */
public class DeserializerException extends Exception {
    public DeserializerException(String message) {
        super(message);
        Debug.log(Debug.LOG_TYPE.ERROR, message);
    }
}