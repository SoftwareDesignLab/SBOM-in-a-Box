package org.svip.repair.statements;

import org.svip.metrics.pipelines.QualityReport;
import org.svip.metrics.pipelines.schemas.CycloneDX14.CDX14Pipeline;
import org.svip.metrics.resultfactory.Result;
import org.svip.repair.fix.Fix;
import org.svip.sbom.model.interfaces.generics.SBOM;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RepairStatementCDX14 implements RepairStatement { // todo depreciate class

    @Override
    public Map<String, Map<String, List<Fix<?>>>> generateRepairStatement(String uid, SBOM sbom) {
        // First key would be either: Metadata or a Component UID
        // Second key in the map would be the value to replace (Example: CPE, Version?)
        // Second key points to the value to replace it with
        // Ex: Map<"bom-ref:abc123", Map<"cpe", "cpe2.3:asdfghjkl">>
        Map<String, Map<String, String>> repairs = new HashMap<>();

        CDX14Pipeline pipeline = new CDX14Pipeline();

        QualityReport report = pipeline.process(uid, sbom);

        Map<String, Map<String, List<Result>>> results = report.getResults();

        // TODO: You may want this - Map<String, List<Result>> metadataResults = results.get("metadata");

        // For each of the results
        // Check each component
        // Check the result
        // If result is failing, try to find a suggestion, add it to the fixes list

        return null;
    }
}
