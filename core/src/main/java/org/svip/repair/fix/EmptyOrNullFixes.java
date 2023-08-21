package org.svip.repair.fix;

import org.svip.metrics.resultfactory.Result;
import org.svip.metrics.tests.enumerations.ATTRIBUTE;
import org.svip.sbom.model.interfaces.generics.SBOM;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EmptyOrNullFixes implements Fixes{
    @Override
    public List<Fix<?>> fix(Result result, SBOM sbom) {

        if(result.getDetails().contains("Bom Version was a null value"))
            return bomVersionFix(result);
        else if(result.getDetails().contains("Creation Data"))
            return creationDataFix();
        else if(result.getDetails().contains("SPDXID"))
            return SPDXIDFix(result);
        else if(result.getDetails().contains("Comment"))
            return commentNullFix();

        return null;

    }

    /**
     * @param result failed test result
     * @return potential fixes for bom version
     */
    private List<Fix<?>> bomVersionFix(Result result) {

        if(result.getAttributes().contains(ATTRIBUTE.SPDX23))
            return Collections.singletonList(new Fix<>("", "2.3"));
        else if(result.getAttributes().contains(ATTRIBUTE.CDX14))
            return Collections.singletonList(new Fix<>("", "1.4"));
        else
            return new ArrayList<>(List.of(new Fix<>("", "2.3"), new Fix<>("", "1.4"),
                    new Fix<>("", "1.04a"))); // todo check 1.04a is current SVIP bomVersion

    }

    /**
     * Creation date is the only fixable attribute for creation data
     *
     * @return potential fixes for creation data
     */
    private List<Fix<?>> creationDataFix(){

        String dateAndTime;
        LocalDate localDate = LocalDate.now();
        DateTimeFormatter formatterLocalDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        dateAndTime = formatterLocalDate.format(localDate);
        LocalTime localTime = LocalTime.now();
        DateTimeFormatter formatterLocalTime = DateTimeFormatter.ofPattern("HH:mm:ss");
        dateAndTime+= "T" + formatterLocalTime.format(localTime) + localTime.atOffset(ZoneOffset.UTC);

        return Collections.singletonList(new Fix<>("", "\"created\" : .\"" + dateAndTime + "\"" ));

    }

    /**
     * Fixes SPDXID
     * @param result failed test result
     * @return fix for SPDXID
     */
    private List<Fix<?>> SPDXIDFix(Result result) {
        return Collections.singletonList(new Fix<>(result.getMessage(), "SPDXRef-DOCUMENT"));
    }

    /**
     * Fixes comment
     * @param result failed test result
     * @return empty string in place for null comment
     */
    private List<Fix<?>> commentNullFix() {
        return Collections.singletonList(new Fix<>("null", ""));
    }

}
