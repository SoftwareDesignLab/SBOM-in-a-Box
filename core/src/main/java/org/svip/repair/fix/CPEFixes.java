/** Copyright 2021 Rochester Institute of Technology (RIT). Developed with
* government support under contract 70RCSA22C00000008 awarded by the United
* States Department of Homeland Security for Cybersecurity and Infrastructure Security Agency.
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the “Software”), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
 */

package org.svip.repair.fix;

import org.svip.metrics.resultfactory.Result;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.interfaces.schemas.CycloneDX14.CDX14Package;
import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23Package;
import org.svip.sbom.model.uids.CPE;

import java.util.List;
import java.util.Optional;

/**
 * Fixes class to generate suggested component CPE repairs
 *
 * @author Tyler Drake
 * @author Justin Jantzi
 */
public class CPEFixes implements Fixes {

    /**
     * Iterates through the CPEs is each component and add fixes for them.
     *
     * @param result        object from quality report
     * @param sbom          sbom from quality report
     * @param componentName key from quality report map most directly relating to the repair type
     * @return
     */
    @Override
    public List<Fix<?>> fix(Result result, SBOM sbom, String componentName, Integer componentHashCode) {

        String expected = result.getDetails().split(" ")[1];
        String actual = result.getDetails().split(" ")[result.getDetails().split(" ").length - 1];

        //TODO: Find a better way of finding actual old CPE as in super rare cases mutliple CPEs
        Optional<Component> potentialComp = sbom.getComponents().stream().filter(x -> x.hashCode() == componentHashCode).findFirst();
        ;
        Component fixingComp = potentialComp.get();

        String oldCPE = "";

        if (fixingComp instanceof SPDX23Package spdx23Package) {
            Optional<String> cpe = spdx23Package.getCPEs().stream().findFirst();

            if (cpe.isPresent())
                oldCPE = cpe.get();
        }

        else if (fixingComp instanceof CDX14Package cdx14Package) {
            Optional<String> cpe = cdx14Package.getCPEs().stream().findFirst();

            if(cpe.isPresent())
                oldCPE = cpe.get();
        }

        // For each component in the SBOM
        for (Component component : sbom.getComponents()) {

            // If the component is SPDX 2.3
            if (component instanceof SPDX23Package spdx23Package) { // SPDX23

                // For each CPE
                for (String cpe : spdx23Package.getCPEs()) {

                    // Fix the CPE and add to the list, then return it
                    List<Fix<?>> cpe1 = performCPEFix(result, cpe, actual, expected, oldCPE);
                    if (cpe1 != null) return cpe1;

                }

            }

            // If the component is CDX 1.4
            else if (component instanceof CDX14Package cdx14Package) { // CDX14

                // For each CPE
                for (String cpe : cdx14Package.getCPEs()) {

                    // Fix the CPE and add to the list, then return it
                    List<Fix<?>> cpe1 = performCPEFix(result, cpe, actual, expected, oldCPE);
                    if (cpe1 != null)
                        return cpe1;

                }

            }

        }

        return null;
    }

    /**
     * Return a list of fixes for a CPE
     *
     * @param result   failed test result
     * @param cpe      The CPE of that component being tested against
     * @param actual   incorrect part of original CPE
     * @param expected what to replace `actual` with in the new CPE
     * @param oldCPE   the old cpe of the component being fixed
     * @return list of potential fixes for this CPE
     */
    private static List<Fix<?>> performCPEFix(Result result, String cpe, String actual, String expected, String oldCPE) {

        try {
            // Create a new CPE Object
            CPE cpeObject = new CPE(cpe);
            CPE newCPE = null;
            CPE oldCPEObject = new CPE(oldCPE);

            // Find which information is invalid
            switch (result.getMessage().split(" ")[1]) {

                // If value is invalid, add a CPE fix
                case "Value" -> {
                    if (actual.contains(cpeObject.getProduct()) || (expected.contains(cpeObject.getProduct()) && !expected.equals(cpeObject.getProduct())) || cpeObject.getProduct().isEmpty() && actual.contains("null"))
                        newCPE = new CPE(oldCPEObject.getVendor(), cpeObject.getProduct(), oldCPEObject.getVersion());
                }

                // If version is invalid, add a CPE fix
                case "Version" -> {
                    if (actual.contains(cpeObject.getVersion()) || (expected.contains(cpeObject.getVersion()) && !expected.equals(cpeObject.getVersion())) || cpeObject.getVersion().isEmpty() && actual.contains("null"))
                        newCPE = new CPE(oldCPEObject.getVendor(), oldCPEObject.getProduct(), expected);
                }

                // If vendor is invalid, add a CPE fix
                case "Vendor" -> {
                    if (actual.contains(cpeObject.getVendor()) || (expected.contains(cpeObject.getVendor()) && !expected.equals(cpeObject.getVendor())) || cpeObject.getVendor().isEmpty() && actual.contains("null"))
                        newCPE = new CPE(cpeObject.getVendor(), oldCPEObject.getVendor(), oldCPEObject.getVersion());
                }
            }

            if(newCPE != null && !cpeObject.toString().equals(newCPE.toString()) && !oldCPEObject.toString().equals(newCPE.toString()))
                return List.of(new Fix<>(FixType.COMPONENT_CPE, oldCPEObject.toString(), newCPE.toString()));

        } catch (Exception e) {
            // If all goes wrong, return null
            return null;
        }
        return null;
    }

}
