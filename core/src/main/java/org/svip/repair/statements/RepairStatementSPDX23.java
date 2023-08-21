package org.svip.repair.statements;

import org.svip.metrics.pipelines.QualityReport;
import org.svip.metrics.pipelines.schemas.SPDX23.SPDX23Pipeline;
import org.svip.metrics.resultfactory.Result;
import org.svip.metrics.resultfactory.enumerations.STATUS;
import org.svip.repair.fix.*;
import org.svip.sbom.model.interfaces.generics.SBOM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RepairStatementSPDX23 implements RepairStatement {

    private final Map<String, Map<String, List<Fix<?>>>> repairs = new HashMap<>();

    @Override
    public Map<String, Map<String, List<Fix<?>>>> generateRepairStatement(String uid, SBOM sbom) {

        SPDX23Pipeline pipeline = new SPDX23Pipeline();

        QualityReport report = pipeline.process(uid, sbom); // get quality report

        Map<String, Map<String, List<Result>>> results = report.getResults();

        for (String repairType : results.keySet()
        ) {

            Map<String, List<Fix<?>>> repairsForThisRepairType = new HashMap<>();

            for (String repairSubType : results.get(repairType).keySet()
            ) {

                ArrayList<Fix<?>> fixArrayList = new ArrayList<>();

                for (Result toFix : results.get(repairType).get(repairSubType)
                ) {

                    if (toFix.getStatus().equals(STATUS.FAIL)) { // if a result fails, it needs to be fixed

                        // fix
                        Fixes fixes = getFixes(toFix);
                        List<Fix<?>> fixList = fixes.fix(toFix, sbom, repairSubType);
                        if (fixList != null)
                            fixArrayList.addAll(fixList);

                    }

                }

                repairsForThisRepairType.put(repairSubType, fixArrayList);

            }

            repairs.put(repairType, repairsForThisRepairType);

        }

        return repairs;
    }

    private static Fixes getFixes(Result result) {
        Fixes fixes = null;

        switch (result.getTest()) {
            case "CPE" ->
                fixes = new CPEFixes();

            case "EmptyOrNullTest", "Has Creation Info", "HasSPDXID" ->
                fixes = new EmptyOrNullFixes();

            case "HashMap" ->
                fixes = new HashFixes();

            case "License" ->
                fixes = new LicenseFixes();

            case "PURLTest", "Matching PURL" ->
                fixes = new PURLFixes();


        }
        return fixes;
    }


}
