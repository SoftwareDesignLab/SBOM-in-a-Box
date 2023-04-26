package svip.sbomanalysis.qualityattributes.processors;

import com.svip.sbomAnalysis.qualityAttributes.tests.*;

/**
 * Metrics that relate specifically to the data stored in the SBOM
 */
public class IntrinsicProcessor extends AttributeProcessor {
    /**
     * Construct the intrinsic processor and add all tests that relate to intrinsic metrics
     */
    public IntrinsicProcessor() {
        super(new MetricTest[]{
                // Add new tests here
        });
    }
}
