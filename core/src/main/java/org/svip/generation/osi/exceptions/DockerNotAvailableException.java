package org.svip.generation.osi.exceptions;

/**
 * Exception thrown when Docker is not available or not installed
 *
 * @author Matt London
 */
public class DockerNotAvailableException extends RuntimeException {
    public DockerNotAvailableException(String message) {
        super(message);
    }

    public DockerNotAvailableException() {
        super();
    }
}