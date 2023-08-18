package org.svip.repair.fix;

import org.svip.metrics.resultfactory.Result;
import org.svip.metrics.tests.enumerations.ATTRIBUTE;

import java.util.ArrayList;
import java.util.List;

public class EmptyOrNullFixes implements Fixes{
    @Override
    public List<Fix<?>> fix(Result result) {

        switch (result.getMessage()){
            case "Bom Version was a null value" -> {
                return new ArrayList<>(List.of(bomVersionFix(result)));
            }
            default -> {
                return null;
            }
        }

    }

    /**
     * @param result failed test result
     * @return fix for bom version
     */
    private Fix<String> bomVersionFix(Result result) {

        if(result.getAttributes().contains(ATTRIBUTE.SPDX23))
            return new Fix<>("", "2.3");
        else if(result.getAttributes().contains(ATTRIBUTE.CDX14))
            return new Fix<>("", "1.4");
        return new Fix<>("", "1.0a"); //SVIP

    }
}
