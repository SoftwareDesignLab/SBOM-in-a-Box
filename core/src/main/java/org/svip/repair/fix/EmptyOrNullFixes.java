package org.svip.repair.fix;

import org.svip.metrics.resultfactory.Result;
import org.svip.metrics.tests.enumerations.ATTRIBUTE;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.sbom.model.objects.SVIPSBOM;

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
            return bomVersionFix(sbom);
        else if(result.getDetails().contains("Creation Data"))
            return creationDataFix();
        else if(result.getDetails().contains("SPDXID"))
            return SPDXIDFix(result);
        else if(result.getDetails().contains("Comment"))
            return commentNullFix();
        else if(result.getDetails().contains("Attribution Text"))
            return attributionTextNullFix();

        return null;

    }

    /**
     * @param sbom sbom
     * @return potential fixes for bom version
     */
    private List<Fix<?>> bomVersionFix(SBOM sbom) {

        if(sbom instanceof SPDX23SBOM)
            return Collections.singletonList(new Fix<>("", "2.3"));
        else if(sbom instanceof CDX14SBOM)
            return Collections.singletonList(new Fix<>("", "1.4"));
        else if(sbom instanceof SVIPSBOM)
            return Collections.singletonList(new Fix<>("", "1.04a"));
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
     * @return empty string in place for null comment
     */
    private List<Fix<?>> commentNullFix() {
        return Collections.singletonList(new Fix<>("null", ""));
    }

    /**
     * @return empty string in place for null attribution text
     */
    private List<Fix<?>> attributionTextNullFix() {
        return Collections.singletonList(new Fix<>("null", "")); // todo make sure this is okay
    }

}
