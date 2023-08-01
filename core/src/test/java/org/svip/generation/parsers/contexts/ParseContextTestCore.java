package org.svip.generation.parsers.contexts;

import org.svip.sbom.builder.objects.SVIPComponentBuilder;
import org.svip.generation.parsers.Parser;
import org.svip.generation.parsers.ParserTestCore;

import java.util.ArrayList;

/**
 * <b>File</b>: ParseContextTestCore.java<br>
 * <b>Description</b>: Abstract test core for testing the parsing of file contexts.
 *
 * @author Dylan Mulligan
 */
public abstract class ParseContextTestCore extends ParserTestCore {
    // Stores the ParserComponents to be tested
    protected final ArrayList<SVIPComponentBuilder> components;

    /**
     * Constructor calls super with parser and src, and parses the given
     * fileContents to test against.
     *
     * @param parser Parser object to be tested
     * @param fileContents fileContents to test against
     * @param src Relative path to dummy directory
     */
    protected ParseContextTestCore(Parser parser, String fileContents, String src) {
        // Call super
        super(parser, src);

        // Init components array
        components = new ArrayList<>();

        // Parse fileContents into components array
        this.PARSER.parse(components, fileContents);
    }
}
