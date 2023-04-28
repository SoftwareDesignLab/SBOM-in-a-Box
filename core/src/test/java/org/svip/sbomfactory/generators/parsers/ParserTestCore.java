package org.svip.sbomfactory.generators.parsers;

import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class ParserTestCore {
    protected Parser PARSER;

    // TODO: Docstring
    protected ParserTestCore(Parser parser, String src) {
        setDummyParser(parser, src);
    }

    /**
     * Initializes given parser and assigns it to this.PARSER
     *
     * @param parser Parser to test
     * @param src Path to directory to be tested
     */
    private void setDummyParser(Parser parser, String src) {
        final Path path = Paths.get(src);
        parser.setPWD(path);
        parser.setSRC(path);
        this.PARSER = parser;
    }
}
