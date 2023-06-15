package org.svip.sbomanalysis.qualityattributes.processors;

import org.svip.sbomanalysis.qualityattributes.tests.CompletenessTest;
import org.svip.sbomanalysis.qualityattributes.tests.DataVerificationTest;
import org.svip.sbomanalysis.qualityattributes.tests.MetricTest;

/**
 * Metrics that measure the relevance of data in the context of the SBOM
 *
 * @author Dylan Mulligan
 */
public class ContextualProcessor extends AttributeProcessor {
    /**
     * Construct the processor and add all contextual tests to the list of tests to perform
     */
//    public ContextualProcessor() {
//        super(new MetricTest[]{
//                new CompletenessTest(),
//                new DataVerificationTest(), // todo unused class, delete?
////                new ActionableTest()
//                // Add new tests here
//        });
//    }
}
