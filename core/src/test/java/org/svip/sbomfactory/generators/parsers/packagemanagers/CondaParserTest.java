package org.svip.sbomfactory.generators.parsers.packagemanagers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CondaParserTest extends ParseDepFileTestCore{
    protected CondaParserTest() throws IOException { //todo
        super(new GradleParser(),
                null,
                null);
    }


}
