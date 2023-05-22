package org.svip.sbomfactory.generators.parsers;

import org.svip.sbomfactory.generators.utils.virtualtree.VirtualPath;

public abstract class ParserTestCore<T extends Parser> {
    protected T PARSER;

    // TODO: Docstring
    protected ParserTestCore(T parser, String src) {
        setDummyParser(parser, src);
    }

    /**
     * Initializes given parser and assigns it to this.PARSER
     *
     * @param parser Parser to test
     * @param src Path to directory to be tested
     */
    private void setDummyParser(T parser, String src) {
        parser.setPWD(new VirtualPath(src));
        parser.setSRC(new VirtualPath(src));
        this.PARSER = parser;
    }
}
