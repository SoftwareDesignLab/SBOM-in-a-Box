package org.svip.sbomgeneration.osi.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DockerNotAvailableExceptionTest {
    private static final String MESSAGE = "Exception";
    @Test
    public void testExceptionWithoutMessage() {
        DockerNotAvailableException exception = new DockerNotAvailableException();
        assertEquals(exception.getMessage(), null);
    }

    @Test
    public void testExceptionWithMessage() {
        DockerNotAvailableException exception = new DockerNotAvailableException(MESSAGE);
        assertEquals(exception.getMessage(), MESSAGE);
    }
}
