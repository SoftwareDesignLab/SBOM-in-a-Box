package org.svip.api.controller;

import org.svip.api.model.SBOMFile;

import java.io.IOException;
import java.util.Map;

public class OverwriteToAPITest extends APITest{

    private Map<Long, SBOMFile> testMap;

    public OverwriteToAPITest() throws IOException {
        testMap = getTestFileMap();
    }

}
