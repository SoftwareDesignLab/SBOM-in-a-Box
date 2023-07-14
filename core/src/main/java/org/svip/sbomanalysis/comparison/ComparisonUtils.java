package org.svip.sbomanalysis.comparison;

public class ComparisonUtils {
    public static boolean isNull(String s) {
        return s == null || s.equals("NOASSERTION") || s.equals("");
    }
}
