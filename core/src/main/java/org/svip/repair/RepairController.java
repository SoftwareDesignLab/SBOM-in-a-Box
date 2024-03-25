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

package org.svip.repair;

import org.svip.metrics.pipelines.QualityReport;
import org.svip.repair.repair.Repair;
import org.svip.repair.fix.Fix;
import org.svip.repair.repair.RepairSPDX23CDX14;
import org.svip.repair.statements.RepairStatement;
import org.svip.repair.statements.RepairStatementSPDX23CDX14;
import org.svip.sbom.model.interfaces.generics.SBOM;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Name: RepairController.java
 * Description: Main driver class for SBOM repair
 *
 * @author Tyler Drake
 * @author Justin Jantzi
 */
public class RepairController {

    public RepairController() {
    }


    /**
     * Generate a repair statement
     *
     * @param sbom sbom to repair
     * @return repair statement
     */
    public QualityReport generateStatement(SBOM sbom) throws Exception {
        RepairStatement rs = getStatement(sbom);
        return rs.generateRepairStatement(sbom);
    }

    /**
     * Repair this SBOM with chosen repairs
     *
     * @param sbom    sbom to repair
     * @param repairs chosen repairs from SBOM
     * @return repaired SBOM
     */
    public SBOM repairSBOM(SBOM sbom, Map<Integer, Set<Fix<?>>> repairs) {
        Repair r = getRepair(sbom);
        return r.repairSBOM(sbom, repairs);
    }

    /**
     * Get repair statement class for this SBOM
     *
     * @param sbom sbom to repair
     * @return repair statement class
     */
    public RepairStatement getStatement(SBOM sbom) {

        // Get the correct RepairStatement model based on the SBOM's format
        switch (sbom.getFormat()) {

            // For SPDX 2.3 and CycloneDX 1.4
            case "SPDX", "CycloneDX" -> {
                return new RepairStatementSPDX23CDX14();
            }
            // For none found
            default -> {
                return null;
            }
        }

    }

    /**
     * Get repair class for this SBOM
     *
     * @param sbom sbom to repair
     * @return repair class
     */
    public Repair getRepair(SBOM sbom) {

        // Get the correct Repair class based on the format
        switch (sbom.getFormat()) {
            // For SPDX 2.3 and CycloneDX 1.4
            case "SPDX", "CycloneDX" -> {
                return new RepairSPDX23CDX14();
            }
            // For none found
            default -> {
                return null;
            }
        }

    }

}
