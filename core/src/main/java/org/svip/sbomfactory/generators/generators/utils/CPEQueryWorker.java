package org.svip.sbomfactory.generators.generators.utils;

import java.util.ArrayList;

/**
 * File: CPEQueryWorker.java
 * <p>
 * TODO: Description
 * </p>
 * @author Ian Dunn
 */
public class CPEQueryWorker extends QueryWorker {

    /**
     * The NVID API URL TODO replace with NVID URL and figure out what responses are
     */
    private static final String NVID_URL = "https://services.nvd.nist.gov/rest/json/cpes/2.0";

    public CPEQueryWorker(ParserComponent component/*, String endpoint*/) {
        super(component, NVID_URL/* + endpoint*/);
    }

    // TODO: Docstring
    @Override
    public void run() {
        // Get page contents
        final String contents = getUrlContents(queryURL(this.url, false));

        // TODO process contents

        ArrayList<String> cpes = new ArrayList<>();
        cpes.forEach(this.component::addCPE);
    }
}
