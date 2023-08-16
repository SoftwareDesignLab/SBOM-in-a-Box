package org.svip.repair.statements;

import org.svip.metrics.pipelines.QualityReport;
import org.svip.metrics.pipelines.schemas.SPDX23.SPDX23Pipeline;
import org.svip.metrics.resultfactory.Result;
import org.svip.metrics.resultfactory.enumerations.STATUS;
import org.svip.sbom.model.interfaces.generics.SBOM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RepairStatementSPDX23 implements RepairStatement{
    @Override
    public Map<String, Map<String, String>> generateRepairStatement(String uid, SBOM sbom) {
        // First key would be either: Metadata or a Component UID
        // Second key in the map would be the value to replace (Example: CPE, Version?)
        // Second key points to the value to replace it with
        // Ex: Map<"bom-ref:abc123", Map<"cpe", "cpe2.3:asdfghjkl">>
        Map<String, Map<String, String>> repairs = new HashMap<>();

        SPDX23Pipeline pipeline = new SPDX23Pipeline();

        QualityReport report = pipeline.process(uid, sbom);

        Map<String, Map<String, List<Result>>> results = report.getResults();

        // TODO: You may want this - Map<String, List<Result>> metadataResults = results.get("metadata");


        for (String repairType: results.keySet()
             ) {

            HashMap<String, String> repairsForThisRepairType = new HashMap<>();

            for (String repairSubType: results.get(repairType).keySet()
                 ) {

                String repair = "";

                for (Result result: results.get(repairType).get(repairSubType)
                     ) {

                    if(result.getStatus().equals(STATUS.FAIL)) {
                        /*
                        TODO REPAIR LOGIC HERE
                         */
                    }

                }

                repairsForThisRepairType.put(repairSubType, repair);

            }

            repairs.put(repairType, repairsForThisRepairType);

        }

        int x = 0;

        // For each of the results
        // Check each component
        // Check the result
        // If result is failing, try to find a suggestion, add it to the fixes list

        return repairs;
    }
}
