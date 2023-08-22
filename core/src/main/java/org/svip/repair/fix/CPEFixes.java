package org.svip.repair.fix;

import org.svip.metrics.resultfactory.Result;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.interfaces.schemas.CycloneDX14.CDX14Package;
import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23Package;
import org.svip.sbom.model.objects.SVIPComponentObject;
import org.svip.sbom.model.uids.CPE;

import java.util.List;

public class CPEFixes implements Fixes {
    @Override
    public List<Fix<?>> fix(Result result, SBOM sbom, String repairSubType) {
        try{
            return CPEFix(result, sbom);
        }catch (Exception e){
            return null;
        }
    }

    public List<Fix<?>> CPEFix(Result result, SBOM sbom) throws Exception {

        String expected = result.getDetails().split(" ")[1];
        String actual = result.getDetails().split(" ")[result.getDetails().split(" ").length - 1];

        for (Component component: sbom.getComponents()
             )
            if(component instanceof SPDX23Package spdx23Package){
                for (String cpe: spdx23Package.getCPEs()){
                    List<Fix<?>> cpe1 = performCPEFix(result, cpe, actual, expected);
                    if (cpe1 != null) return cpe1;
                }

            }
            else if (component instanceof CDX14Package cdx14Package){
                for (String cpe: cdx14Package.getCPEs()){
                    List<Fix<?>> cpe1 = performCPEFix(result, cpe, actual, expected);
                    if (cpe1 != null) return cpe1;}
            }

        return null;
    }

    private static List<Fix<?>> performCPEFix(Result result, String cpe, String actual, String expected) throws Exception {
        CPE cpeObject = new CPE(cpe);
        switch (result.getMessage().split(" ")[1]){
            case "Value" ->{
                if(cpeObject.getProduct().contains(actual) || cpeObject.getProduct().contains(expected) || cpeObject.getProduct().isEmpty() && actual.contains("null"))
                    return List.of(new Fix<>(new CPE(cpe), new CPE(cpeObject.getVendor(), expected,
                            cpeObject.getVersion())));
            }
            case "Version" ->{
                if(cpeObject.getVersion().contains(actual) || cpeObject.getVersion().contains(expected) || cpeObject.getVersion().isEmpty() && actual.contains("null"))
                    return List.of(new Fix<>(new CPE(cpe), new CPE(cpeObject.getVendor(), cpeObject.getProduct(),
                            expected)));
            }
            case "Vendor" ->{
                if(cpeObject.getVendor().contains(actual) || cpeObject.getVendor().contains(expected) || cpeObject.getVendor().isEmpty() && actual.contains("null"))
                    return List.of(new Fix<>(new CPE(cpe), new CPE(expected, cpeObject.getProduct(),
                            cpeObject.getVersion())));
            }
        }
        return null;
    }

}
