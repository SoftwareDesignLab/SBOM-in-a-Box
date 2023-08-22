package org.svip.repair.fix;

import org.svip.metrics.resultfactory.Result;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.interfaces.schemas.CycloneDX14.CDX14Package;
import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23Package;
import org.svip.sbom.model.objects.SVIPComponentObject;

import java.util.List;

public class CPEFixes implements Fixes {
    @Override
    public List<Fix<?>> fix(Result result, SBOM sbom, String repairSubType) {
        return CPEFix(result, sbom);
    }

    public List<Fix<?>> CPEFix(Result result, SBOM sbom){

        String expected = result.getDetails().split(" ")[1];
        String actual = result.getDetails().split(" ")[result.getDetails().split(" ").length - 1];

        for (Component component: sbom.getComponents()
             )
            if(component instanceof SPDX23Package spdx23Package){
                for (String cpe: spdx23Package.getCPEs())
                    if(cpe.contains(expected))
                        return List.of(new Fix<>(cpe, cpe.replace(expected, actual)));
            }
            else if (component instanceof CDX14Package cdx14Package){
                for (String cpe1: cdx14Package.getCPEs())
                    if(cpe1.contains(expected))
                        return List.of(new Fix<>(cpe1, cpe1.replace(expected, actual)));
            }
//            else if (component instanceof SVIPComponentObject svipComponentObject){ // todo intelliJ says this is always false?
//                for (String cpe2: svipComponentObject.getCPEs())
//                    if(cpe2.contains(expected))
//                        return List.of(new Fix<>(cpe2, cpe2.replace(expected, actual)));
//            }

        return null;
    }

}
