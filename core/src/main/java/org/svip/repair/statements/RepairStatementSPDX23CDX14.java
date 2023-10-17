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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RepairStatementSPDX23CDX14 implements RepairStatement {

    // This RepairStatement for a SPDX23 or CDX14 SBOM
    private final Map<String, Map<String, List<Fix<?>>>> repairs = new HashMap<>();

    @Override
    public Map<String, Map<String, List<Fix<?>>>> generateRepairStatement(String uid, SBOM sbom) throws Exception {

        QualityReport report; // get quality report

        // If the SBOM is an SPDX 2.3 SBOM
        if (sbom instanceof SPDX23SBOM) {

            // Generate the quality report
            SPDX23Pipeline pipeline = new SPDX23Pipeline();
            report = pipeline.process(uid, sbom);

        }
        // If the SBOM is a CycloneDX 1.4 SBOM
        else if (sbom instanceof CDX14SBOM) {

            // Generate the quality report
            CDX14Pipeline pipeline = new CDX14Pipeline();
            report = pipeline.process(uid, sbom);

        }
        // Otherwise, try to generate an SVIP Quality Report
        else {

            // Generate the quality report
            SVIPPipeline pipeline = new SVIPPipeline();
            report = pipeline.process(uid, sbom);

        }

        // get the Quality Report's results
        Map<String, Map<String, List<Result>>> results = report.getResults();

        //remove duplicate entry under serial number
        results.remove(sbom.getUID());

        // For each result
        for (String repairType : results.keySet()) {

            // Make a new repair type list
            Map<String, List<Fix<?>>> repairsForThisRepairType = new HashMap<>();

            // Iterate through those sub results as repair subtypes
            for (String repairSubType : results.get(repairType).keySet()) {

                ArrayList<Fix<?>> fixArrayList = new ArrayList<>();

                for (Result toFix : results.get(repairType).get(repairSubType)) {

                    // if a result fails, it needs to be fixed
                    if (toFix.getStatus().equals(STATUS.FAIL)) {

                        // fix
                        Fixes fixes = getFixes(toFix);
                        List<Fix<?>> fixList = null;

                        // If the fixes is not null
                        if (fixes != null)
                            // Set the fix list
                            fixList = fixes.fix(toFix, sbom, repairSubType);

                        // If the fix list is not null
                        if (fixList != null)
                            // Add all the fixes to the fix array list
                            fixArrayList.addAll(fixList);

                    }

                }

                // Add the repairs for this specific repair type
                if(!fixArrayList.isEmpty())
                    repairsForThisRepairType.put(repairSubType, fixArrayList);

            }


            // Add the repairs to the main repair list
            if(!repairsForThisRepairType.isEmpty())
                repairs.put(repairType, repairsForThisRepairType);

        }

        // Return the repairs
        return repairs;

    }

    /**
     * Get the fixes class depending on the test result
     *
     * @param result failed result to base fix off
     * @return appropriate fixes class
     */
    private static Fixes getFixes(Result result) {

        // Fixes
        Fixes fixes = null;

        // Depending on the failing test, get the correct "fix" class
        switch (result.getTest()) {

            case "Matching CPE" -> fixes = new CPEFixes();

            case "EmptyOrNullTest", "Has Creation Info", "HasSPDXID" -> fixes = new EmptyOrNullFixes(null);

            case "HashMap" -> fixes = new HashFixes();

            case "License" -> fixes = new LicenseFixes();

            case "PURLTest", "Matching PURL", "Accurate PURL" -> fixes = new PURLFixes();


        }

        // Return the fixes
        return fixes;

    }


}
