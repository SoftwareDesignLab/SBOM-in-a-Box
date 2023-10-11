package org.svip.repair.extraction;

import org.svip.generation.parsers.utils.QueryWorker;

import java.util.ArrayList;
import java.util.HashMap;



/**
 * <b>File</b>: Extraction.java<br>
 * <b>Description</b>: Abstract core Class for extracting information
 * from package manager metadata
 * @author Justin Jantzi
 */
public abstract class Extraction {

    protected final ArrayList<QueryWorker> queryWorkers;
    protected HashMap<String, String> purl;

    protected Extraction(HashMap<String, String> purl) {
        this.purl = purl;
        this.queryWorkers = new ArrayList<>();
    }

    public abstract String extractCopyright();
}
