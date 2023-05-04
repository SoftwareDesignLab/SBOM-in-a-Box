package org.svip.sbomfactory.osi.exceptions;

/**
 * Exception thrown when Docker is not available or not installed
 *
 * @author Matt London
 */
public class DockerNotAvailableException extends RuntimeException {
    public DockerNotAvailableException(String message) {
        super(message);
    }

    public DockerNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public DockerNotAvailableException(Throwable cause) {
        super(cause);
    }

    public DockerNotAvailableException() {
        super();
    }

}
