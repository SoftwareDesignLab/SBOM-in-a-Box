package org.svip.generation.parsers.packagemanagers;

import org.svip.sbom.builder.objects.SVIPComponentBuilder;
import org.svip.sbom.model.objects.SVIPComponentObject;
import org.svip.generation.parsers.Parser;
import org.svip.generation.parsers.ParserTestCore;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>File</b>: ParseDepFileTestCore.java<br>
 * <b>Description</b>: Abstract test core for testing the parsing of generated
 * dependency files.
 *
 * @author Dylan Mulligan
 */
public abstract class ParseDepFileTestCore extends ParserTestCore<PackageManagerParser> {
    // Stores the ParserComponents to be tested
    protected final List<SVIPComponentBuilder> components;
    /**
     * Constructor calls super with parser and src, and parses the given
     * fileContents to test against.
     *
     * @param parser Parser object to be tested
     * @param fileContents fileContents to test against
     * @param src Relative path to dummy directory
     */
    protected ParseDepFileTestCore(PackageManagerParser parser, String fileContents, String src) {
        // Call super
        super(parser, TEST_DATA_PATH + src);

        // Init components array
        components = new ArrayList<>();

        // Parse fileContents into components array
        this.PARSER.parse(components, fileContents);
    }

    /**
     * Method to find an individual component from {@link this.components }, null is returned if not found
     * @param name Name used to find the component
     * @return The found component object or null
     */
    protected SVIPComponentObject getComponent(String name) {
        for(SVIPComponentBuilder i : this.components) {
            String cname = Parser.getName(i);
            if((cname != null) && cname.equals(name) ) {
                return i.build();
            }
        }
        return null;
    }

}
