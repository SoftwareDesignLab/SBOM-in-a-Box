package org.svip.sbomfactory.parsers;

import org.svip.utils.VirtualPath;

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
        // TODO set source files
//        VirtualTree dummyFileTree = VirtualTree.buildVirtualTree(new VirtualPath(src));
//        parser.setSourceFiles(dummyFileTree.getAllFiles());
        this.PARSER = parser;
    }
}
