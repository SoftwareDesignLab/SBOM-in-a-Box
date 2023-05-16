package org.svip.generators.parsers.contexts;

// Declares Imports
import java.util.*;
import org.svip.generators.parsers.Parser;

/**
 * file: ContextParser.java
 * @description Parser for context based entities/elements in source files.
 *
 * @author Henry Keena , Dylan Mulligan
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
