package org.svip.sbomgeneration.parsers;

import org.svip.SBOMGeneratorCLI;
import org.svip.utils.VirtualPath;

import java.util.Map;

public abstract class ParserTestCore<T extends Parser> {
    protected static final String TEST_DATA_PATH = "src/test/resources/parsers/";
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
        Map<VirtualPath, String> dummyFileMap = SBOMGeneratorCLI.buildFileMap(new VirtualPath(src));
        parser.setSourceFiles(dummyFileMap.keySet());
        this.PARSER = parser;
    }
}
