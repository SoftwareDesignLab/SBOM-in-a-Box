package org.svip.merge;

import org.svip.sbom.model.interfaces.generics.SBOM;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Function;

/**
 * Name: MergerController.java
 * Description: Main controller for merging SBOMs.
 *
 * @author Tyler Drake
 * @author Matt London
 */

public class MergerController {

    /**
     * Invalid Format Message
     */
    private final static Function<String, String> INVALID_FORMAT_TYPE = (formats) ->
            "Cross format merging not supported for " + formats + ".";

    /**
     * Merge a collection of SBOMs into one main SBOM
     *
     * @param SBOMs Collection of SBOM objects to merge together
     * @return Resulting merged bom
     */
    public SBOM mergeAll(Collection<SBOM> SBOMs) throws MergerException {

        // Loop through and merge into a master SBOM
        if (SBOMs.size() == 0) {
            return null;
        } else if (SBOMs.size() == 1) {
            // Return the first element
            for (SBOM sbom : SBOMs) {
                return sbom;
            }
        }

        // Now we know there is at least two SBOMs
        Iterator<SBOM> it = SBOMs.iterator();
        SBOM a = it.next();
        SBOM b = it.next();

        SBOM mainBom;

        // Merge it into a main SBOM
        try {
            Merger merger = getMerger(a.getFormat(), b.getFormat());
            mainBom = merger.mergeSBOM(a, b);
        } catch (MergerException e) {
            mainBom = null;
            throw new MergerException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Take the remaining SBOMs and merge them into the main SBOM
        while (it.hasNext()) {
            SBOM nextBom = it.next();
            // Merge it into a main SBOM
            try {
                Merger merger = getMerger(mainBom.getFormat(), nextBom.getFormat());
                mainBom = merger.mergeSBOM(mainBom, nextBom);
            } catch (MergerException e) {
                mainBom = null;
                throw new MergerException(e.getMessage());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        // Return the main bom
        return mainBom;
    }

    /**
     * A smaller merge call for a single merge between two SBOMs
     *
     * @param a
     * @param b
     * @return
     */
    public SBOM merge(SBOM a, SBOM b) throws MergerException {

        SBOM mainBom;

        // Merge it into a main SBOM
        try {
            Merger merger = getMerger(a.getFormat(), b.getFormat());
            mainBom = merger.mergeSBOM(a, b);
        } catch (MergerException e) {
            mainBom = null;
            throw new MergerException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return mainBom;
    }

    /**
     * Gets the necessary merger for the two SBOMs
     *
     * @param formatOne format of SBOM one
     * @param formatTwo format of SBOM two
     */
    private static Merger getMerger(String formatOne, String formatTwo) throws MergerException {
        switch (formatOne.toLowerCase() + ":" + formatTwo.toLowerCase()) {
            case "cyclonedx:cyclonedx" -> {
                return new MergerCDX();
            }
            case "spdx:spdx" -> {
                return new MergerSPDX();
            }
            case "svip:svip" -> {
                return new MergerSVIP();
            }
            default -> {
                return new MergerCrossSchema();
            }
        }
    }
}