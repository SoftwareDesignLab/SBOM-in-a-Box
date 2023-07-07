/**
 * @file NewMergerTest.java
 *
 * Contains test class for the Merger class
 *
 * @author Matt London
 */

package org.svip.sbomanalysis.comparison;

import org.junit.jupiter.api.Test;
import org.svip.sbom.model.old.Component;
import org.svip.sbom.model.old.DependencyTree;
import org.svip.sbom.model.old.SBOM;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Tests for the Merge class
 */
public class OldMergerTest {
    /**
     * Merges a bom with a blank bom and makes sure that the resultant SBOM is the same as the original
     */
    @Test
    public void mergeBlankBOM() {
        // SBOM 1
        SBOM sbom1 = new SBOM(SBOM.Type.CYCLONE_DX, "1.4", "1", null, "2021-05-01T00:00:00Z", null, null, new DependencyTree());

        // Build components
        UUID mainComponent = sbom1.addComponent(null, new Component("HelloWorld", "1.0.0"));
        sbom1.addComponent(mainComponent, new Component("pandas", "1.0.0"));
        UUID subComponent = sbom1.addComponent(mainComponent, new Component("numpy", "1.2.0"));
        UUID subSubComponent = sbom1.addComponent(subComponent, new Component("six", "0.1.2"));
        UUID subSubComponent2 = sbom1.addComponent(subComponent, new Component("psutil", "0.1.2"));

        // Blank bom
        SBOM blank = new SBOM(SBOM.Type.CYCLONE_DX, "1.4", "1", null, "2021-05-01T00:00:00Z", null, null, new DependencyTree());

        List<SBOM> sboms = new ArrayList<SBOM>();
        sboms.add(sbom1);
        sboms.add(blank);

        Merger merger = new Merger();
        SBOM mergedBom = merger.merge(sboms);

        assert mergedBom.hashCode() == sbom1.hashCode();
    }

    /**
     * Builds an SBOM, passes it to the merger and makes sure that the resultant SBOM
     * is the same as the original
     */
    @Test
    public void mergeOneBOM() {
        // SBOM 1
        SBOM sbom1 = new SBOM(SBOM.Type.CYCLONE_DX, "1.4", "1", null, "2021-05-01T00:00:00Z", null, null, new DependencyTree());

        // Build components
        UUID mainComponent = sbom1.addComponent(null, new Component("HelloWorld", "1.0.0"));
        sbom1.addComponent(mainComponent, new Component("pandas", "1.0.0"));
        UUID subComponent = sbom1.addComponent(mainComponent, new Component("numpy", "1.2.0"));
        UUID subSubComponent = sbom1.addComponent(subComponent, new Component("six", "0.1.2"));
        UUID subSubComponent2 = sbom1.addComponent(subComponent, new Component("psutil", "0.1.2"));

        // SBOM 1 is now built out with components

        List<SBOM> sboms = new ArrayList<SBOM>();
        sboms.add(sbom1);

        Merger merger = new Merger();
        SBOM mergedBom = merger.merge(sboms);

        assert mergedBom.hashCode() == sbom1.hashCode();
    }

    /**
     * Builds two SBOMs, passes them to the merger and makes sure that the resultant SBOM
     * is the same as a manually built result
     */
    @Test
    public void mergeTwoBOM() {
        // SBOM 1
        SBOM sbom1 = new SBOM(SBOM.Type.CYCLONE_DX, "1.4", "1", null, "2021-05-01T00:00:00Z", null, null, new DependencyTree());

        // Build components
        UUID mainComponent = sbom1.addComponent(null, new Component("HelloWorld", "1.0.0"));
        sbom1.addComponent(mainComponent, new Component("pandas", "1.0.0"));
        UUID subComponent = sbom1.addComponent(mainComponent, new Component("numpy", "1.2.0"));
        UUID subSubComponent = sbom1.addComponent(subComponent, new Component("six", "0.1.2"));
        UUID subSubComponent2 = sbom1.addComponent(subComponent, new Component("psutil", "0.1.2"));

        // SBOM 1 is now built out with components


        // SBOM 2
        SBOM sbom2 = new SBOM(SBOM.Type.CYCLONE_DX, "1.4", "1", null, "2021-05-01T00:00:00Z", null, null, new DependencyTree());

        // Build components
        UUID helloName = sbom2.addComponent(null, new Component("HelloWorld", "1.0.0"));
        sbom2.addComponent(helloName, new Component("matplotlib", "1.0.0"));
        UUID pygame = sbom2.addComponent(helloName, new Component("pygame", "1.2.0"));
        UUID six2 = sbom2.addComponent(pygame, new Component("six", "0.1.2"));
        UUID psutil2 = sbom2.addComponent(six2, new Component("psutil", "0.1.2"));

        // SBOM 2 is now built out with components

        // Build resulting SBOM hardcoded to test the merger
        SBOM resultBom = new SBOM(SBOM.Type.CYCLONE_DX, "1.4", "1", null, "2021-05-01T00:00:00Z", null, null, new DependencyTree());
        UUID resultHead = resultBom.addComponent(null, new Component("HelloWorld", "1.0.0"));
        resultBom.addComponent(resultHead, new Component("pandas", "1.0.0"));
        UUID resultNumpy = resultBom.addComponent(resultHead, new Component("numpy", "1.2.0"));
        resultBom.addComponent(resultNumpy, new Component("six", "0.1.2"));
        resultBom.addComponent(resultNumpy, new Component("psutil", "0.1.2"));

        resultBom.addComponent(resultHead, new Component("matplotlib", "1.0.0"));

        UUID resultPygame = resultBom.addComponent(resultHead, new Component("pygame", "1.2.0"));
        UUID resultPygameSix = resultBom.addComponent(resultPygame, new Component("six", "0.1.2"));
        resultBom.addComponent(resultPygameSix, new Component("psutil", "0.1.2"));

        // SBOMS
        List<SBOM> sboms = new ArrayList<SBOM>();
        sboms.add(sbom1);
        sboms.add(sbom2);

        Merger merger = new Merger();
        SBOM mergedBom = merger.merge(sboms);

        assert mergedBom.hashCode() == resultBom.hashCode();

    }

    /**
     * Same as mergeTwoBOM test but instead uses three SBOMs as input
     * with clashing dependencies (same name and level)
     */
    @Test
    public void mergeThreeBOM() {
        // SBOM 1
        SBOM sbom1 = new SBOM(SBOM.Type.CYCLONE_DX, "1.4", "1", null, "2021-05-01T00:00:00Z", null, null, new DependencyTree());

        // Build components
        UUID mainComponent = sbom1.addComponent(null, new Component("HelloWorld", "1.0.0"));
        sbom1.addComponent(mainComponent, new Component("pandas", "1.0.0"));
        UUID subComponent = sbom1.addComponent(mainComponent, new Component("numpy", "1.2.0"));
        UUID subSubComponent = sbom1.addComponent(subComponent, new Component("six", "0.1.2"));
        UUID subSubComponent2 = sbom1.addComponent(subComponent, new Component("psutil", "0.1.2"));

        // SBOM 1 is now built out with components


        // SBOM 2
        SBOM sbom2 = new SBOM(SBOM.Type.CYCLONE_DX, "1.4", "1", null, "2021-05-01T00:00:00Z", null, null, new DependencyTree());

        // Build components
        UUID helloName = sbom2.addComponent(null, new Component("HelloWorld", "1.0.0"));
        sbom2.addComponent(helloName, new Component("matplotlib", "1.0.0"));
        UUID pygame = sbom2.addComponent(helloName, new Component("pygame", "1.2.0"));
        UUID six2 = sbom2.addComponent(pygame, new Component("six", "0.1.2"));
        UUID psutil2 = sbom2.addComponent(six2, new Component("psutil", "0.1.2"));

        // SBOM 2 is now built out with components

        // SBOM 3
        SBOM sbom3 = new SBOM(SBOM.Type.CYCLONE_DX, "1.4", "1", null, "2021-05-01T00:00:00Z", null, null, new DependencyTree());
        UUID bom3World = sbom3.addComponent(null, new Component("HelloWorld", "1.0.0"));
        sbom3.addComponent(bom3World, new Component("pandas", "1.0.0"));
        UUID matplot3 = sbom3.addComponent(bom3World, new Component("matplotlib", "1.0.0"));
        UUID graph3 = sbom3.addComponent(matplot3, new Component("graph", "1.0.0"));

        // Build resulting SBOM hardcoded to test the merger
        SBOM resultBom = new SBOM(SBOM.Type.CYCLONE_DX, "1.4", "1", null, "2021-05-01T00:00:00Z", null, null, new DependencyTree());
        UUID resultHead = resultBom.addComponent(null, new Component("HelloWorld", "1.0.0"));
        resultBom.addComponent(resultHead, new Component("pandas", "1.0.0"));
        UUID resultNumpy = resultBom.addComponent(resultHead, new Component("numpy", "1.2.0"));
        resultBom.addComponent(resultNumpy, new Component("six", "0.1.2"));
        resultBom.addComponent(resultNumpy, new Component("psutil", "0.1.2"));

        UUID matplotRes = resultBom.addComponent(resultHead, new Component("matplotlib", "1.0.0"));
        resultBom.addComponent(matplotRes, new Component("graph", "1.0.0"));

        UUID resultPygame = resultBom.addComponent(resultHead, new Component("pygame", "1.2.0"));
        UUID resultPygameSix = resultBom.addComponent(resultPygame, new Component("six", "0.1.2"));
        resultBom.addComponent(resultPygameSix, new Component("psutil", "0.1.2"));

        // SBOMS
        List<SBOM> sboms = new ArrayList<SBOM>();
        sboms.add(sbom1);
        sboms.add(sbom2);
        sboms.add(sbom3);

        Merger merger = new Merger();
        SBOM mergedBom = merger.merge(sboms);

        assert mergedBom.hashCode() == resultBom.hashCode();

    }

}
