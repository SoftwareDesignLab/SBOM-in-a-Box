package parsers.packagemanagers;

import parsers.Parser;
import parsers.ParserTestCore;
import utils.ParserComponent;

import java.util.ArrayList;

/**
 * <b>File</b>: ParseDepFileTestCore.java<br>
 * <b>Description</b>: Abstract test core for testing the parsing of generated
 * dependency files.
 *
 * @author Dylan Mulligan
 */
public abstract class ParseDepFileTestCore extends ParserTestCore {
    // Stores the ParserComponents to be tested
    protected final ArrayList<ParserComponent> components;
    /**
     * Constructor calls super with parser and src, and parses the given
     * fileContents to test against.
     *
     * @param parser Parser object to be tested
     * @param fileContents fileContents to test against
     * @param src Relative path to dummy directory
     */
    protected ParseDepFileTestCore(Parser parser, String fileContents, String src) {
        // Call super
        super(parser, src);

        // Init components array
        components = new ArrayList<>();

        // Parse fileContents into components array
        this.PARSER.parse(components, fileContents);
    }
}
