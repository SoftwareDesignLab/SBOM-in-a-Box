package org.svip.sbomanalysis.qualityattributes.tests.Utils;

import org.svip.sbomanalysis.qualityattributes.tests.Result;
import org.svip.sbomanalysis.qualityattributes.tests.testresults.Test;
import org.svip.sbomanalysis.qualityattributes.tests.testresults.TestResults;

import java.util.ArrayList;
import java.util.List;

public class ResultTranslator {
    public static List<Result> fromTestResult(TestResults testResults) {
        List<Result> ret = new ArrayList<>();
        List<Test> tests = testResults.getTests();
        for (Test t: tests
             ) {
            ret.add(new Result(t.getClass().getName(),
                    t.getStatus() ? Result.STATUS.PASS : Result.STATUS.FAIL, t.getMessage()));
        }
        return ret;
    }
}
