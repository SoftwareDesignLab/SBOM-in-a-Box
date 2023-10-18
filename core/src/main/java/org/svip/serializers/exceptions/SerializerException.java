package org.svip.serializers.exceptions;

import org.svip.utils.Debug;

/**
 * Exception thrown when there is an error in serializing an SBOM
 *
 * @author Juan Francisco Patino
 */
public class SerializerException extends Exception {
    public SerializerException(String message) {
        super(message);
        Debug.log(Debug.LOG_TYPE.ERROR, message);
    }
}