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

package org.svip.repair.statements;

import org.svip.metrics.pipelines.QualityReport;
import org.svip.metrics.pipelines.SVIPPipeline;
import org.svip.metrics.pipelines.schemas.CycloneDX14.CDX14Pipeline;
import org.svip.metrics.pipelines.schemas.SPDX23.SPDX23Pipeline;
import org.svip.metrics.resultfactory.Result;
import org.svip.metrics.resultfactory.enumerations.STATUS;
import org.svip.repair.fix.*;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.utils.Debug;

import java.util.List;
import java.util.Map;

public class RepairStatementSPDX23CDX14 implements RepairStatement {

    @Override
    public QualityReport generateRepairStatement(SBOM sbom) throws Exception {

        QualityReport report; // get quality report

        // If the SBOM is an SPDX 2.3 SBOM
        if (sbom instanceof SPDX23SBOM) {

            // Generate the quality report
            SPDX23Pipeline pipeline = new SPDX23Pipeline();
            report = pipeline.process(sbom);

        }
        // If the SBOM is a CycloneDX 1.4 SBOM
        else if (sbom instanceof CDX14SBOM) {

            // Generate the quality report
            CDX14Pipeline pipeline = new CDX14Pipeline();
            report = pipeline.process(sbom);

        }
        // Otherwise, try to generate an SVIP Quality Report
        else {

            // Generate the quality report
            SVIPPipeline pipeline = new SVIPPipeline();
            report = pipeline.process(sbom);

        }

        // get the Quality Report's results
        Map<Integer, List<Result>> results = report.getResults();
        Map<Integer, String> hashCodeMapping = report.getHashCodeMapping();

        //remove duplicate entry under serial number
        results.remove(sbom.getUID());

        // For each result
        for (Integer component : results.keySet()) {

            // Iterate through those sub results as repair subtypes
            for (Result toFix : results.get(component)) {

                // if a result fails, it needs to be fixed
                if (toFix.getStatus().equals(STATUS.FAIL) || toFix.getStatus().equals(STATUS.ERROR)) {

                    // fix
                    Fixes fixes = getFixes(toFix);

                    // If the fixes is not null
                    if (fixes != null)
                        // Set the fix list
                        try {
                            toFix.addFixes(fixes.fix(toFix, sbom, hashCodeMapping.get(component), component));
                        }

                        catch(Exception ex) {
                            Debug.log(Debug.LOG_TYPE.ERROR, "Error while generating fix for: " + toFix.getMessage());
                        }

                }
            }

        }

        // Return the repairs
        return report;

    }

    /**
     * Get the fixes class depending on the test result
     *
     * @param result failed result to base fix off
     * @return appropriate fixes class
     */
    public static Fixes getFixes(Result result) {

        // Fixes
        Fixes fixes = null;

        // Depending on the failing test, get the correct "fix" class
        switch (result.getTest()) {

            case "Matching CPE" -> fixes = new CPEFixes();

            case "EmptyOrNullTest", "Has Creation Info", "HasSPDXID" -> fixes = new EmptyOrNullFixes(null);

            case "HashMap" -> fixes = new HashFixes();

            case "License" -> fixes = new LicenseFixes();

            case "PURLTest", "Matching PURL", "Accurate PURL", "Valid PURL" -> fixes = new PURLFixes();


        }

        // Return the fixes
        return fixes;

    }


}
