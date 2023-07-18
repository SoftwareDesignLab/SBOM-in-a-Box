package org.svip.sbomfactory.parsers.contexts;

// Declares Imports

import org.svip.sbomfactory.parsers.Parser;

import java.util.ArrayList;

/**
 * file: ContextParser.java
 * Parser for context based entities/elements in source files.
 *
 * @author Dylan Mulligan
 * @author Henry Keena
 * @author Ian Dunn
 */
public abstract class ContextParser extends Parser {
    protected final ArrayList<String> context;

    /**
     * Constructor for ContextParser
     */
    protected ContextParser() {
        super(""); // TODO: Use if we query anything
        this.context = new ArrayList<>();
    }
}
