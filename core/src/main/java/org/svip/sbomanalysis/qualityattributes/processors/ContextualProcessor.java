package org.svip.sbomanalysis.qualityattributes.processors;
import org.svip.sbomanalysis.qualityattributes.tests.*;

/**
 * Metrics that measure the relevance of data in the context of the SBOM
 *
 * @author Dylan Mulligan
 */
public class ContextualProcessor extends AttributeProcessor {
    /**
     * Construct the processor and add all contextual tests to the list of tests to perform
     */
    public ContextualProcessor() {
        super(new MetricTest[]{
                new CompletenessTest(),
                new DataVerificationTest(),
//                new ActionableTest()
                // Add new tests here
        });
    }
}
