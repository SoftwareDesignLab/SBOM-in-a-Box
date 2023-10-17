package org.svip.repair.fix;

import org.svip.metrics.resultfactory.Result;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.interfaces.schemas.CycloneDX14.CDX14Package;
import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23Package;
import org.svip.sbom.model.uids.CPE;

import java.util.List;

/**
 * Fixes class to generate suggested component CPE repairs
 */
public class CPEFixes implements Fixes {

    /**
     * Iterates through the CPEs is each component and add fixes for them.
     *
     * @param result        object from quality report
     * @param sbom          sbom from quality report
     * @param repairSubType key from quality report map most directly relating to the repair type
     * @return
     */
    @Override
    public List<Fix<?>> fix(Result result, SBOM sbom, String repairSubType) {

        String expected = result.getDetails().split(" ")[1];
        String actual = result.getDetails().split(" ")[result.getDetails().split(" ").length - 1];

        // For each component in the SBOM
        for (Component component : sbom.getComponents()) {

            // If the component is SPDX 2.3
            if (component instanceof SPDX23Package spdx23Package) { // SPDX23

                // For each CPE
                for (String cpe : spdx23Package.getCPEs()) {

                    // Fix the CPE and add to the list, then return it
                    List<Fix<?>> cpe1 = performCPEFix(result, cpe, actual, expected);
                    if (cpe1 != null) return cpe1;

                }

            }

            // If the component is CDX 1.4
            else if (component instanceof CDX14Package cdx14Package) { // CDX14

                // For each CPE
                for (String cpe : cdx14Package.getCPEs()) {

                    // Fix the CPE and add to the list, then return it
                    List<Fix<?>> cpe1 = performCPEFix(result, cpe, actual, expected);
                    if (cpe1 != null) return cpe1;

                }

            }

        }

        return null;
    }

    /**
     * Return a list of fixes for a CPE
     *
     * @param result   failed test result
     * @param cpe      original CPE
     * @param actual   incorrect part of original CPE
     * @param expected what to replace `actual` with in the new CPE
     * @return list of potential fixes for this CPE
     */
    private static List<Fix<?>> performCPEFix(Result result, String cpe, String actual, String expected) {

        try {
            // Create a new CPE Object
            CPE cpeObject = new CPE(cpe);
            CPE newCPE = null;

            // Find which information is invalid
            switch (result.getMessage().split(" ")[1]) {

                // If value is invalid, add a CPE fix
                case "Value" -> {
                    if (cpeObject.getProduct().contains(actual) || cpeObject.getProduct().contains(expected) || cpeObject.getProduct().isEmpty() && actual.contains("null"))
                        newCPE = new CPE(cpeObject.getVendor(), expected, cpeObject.getVersion());
                }

                // If version is invalid, add a CPE fix
                case "Version" -> {
                    if (cpeObject.getVersion().contains(actual) || cpeObject.getVersion().contains(expected) || cpeObject.getVersion().isEmpty() && actual.contains("null"))
                        newCPE = new CPE(cpeObject.getVendor(), cpeObject.getProduct(), expected);
                }

                // If vendor is invalid, add a CPE fix
                case "Vendor" -> {
                    if (cpeObject.getVendor().contains(actual) || cpeObject.getVendor().contains(expected) || cpeObject.getVendor().isEmpty() && actual.contains("null"))
                        newCPE = new CPE(expected, cpeObject.getProduct(), cpeObject.getVersion());
                }
            }

            if(newCPE != null && !cpeObject.toString().equals(newCPE.toString()))
                return List.of(new Fix<>(FixType.COMPONENT_CPE, cpeObject, newCPE));

        } catch (Exception e) {
            // If all goes wrong, return null
            return null;
        }
        return null;
    }

}
