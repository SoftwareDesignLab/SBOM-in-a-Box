package org.svip.sbomfactory.generators.generators.generators;

/**
 * An exception to be thrown if there is an error processing data in one of the classes belonging to the
 * {@code Generators} package.
 *
 * @author Ian Dunn
 */
public class GeneratorException extends Exception {

    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public GeneratorException(String message) {
        super(message);
    }
}
