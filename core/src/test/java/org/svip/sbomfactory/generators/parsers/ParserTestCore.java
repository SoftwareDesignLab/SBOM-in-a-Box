package org.svip.sbomfactory.generators.parsers;

import org.svip.sbomfactory.generators.utils.virtualtree.VirtualPath;
import org.svip.sbomfactory.generators.utils.virtualtree.VirtualTree;
import org.svip.sbomfactory.parsers.Parser;

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
        VirtualTree dummyFileTree = VirtualTree.buildVirtualTree(new VirtualPath(src));
        parser.setInternalFiles(dummyFileTree.getAllFiles());
        this.PARSER = parser;
    }
}
