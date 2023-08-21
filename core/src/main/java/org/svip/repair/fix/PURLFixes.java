package org.svip.repair.fix;

import org.svip.metrics.resultfactory.Result;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.interfaces.schemas.CycloneDX14.CDX14Package;
import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23Component;
import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23File;
import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23Package;
import org.svip.sbom.model.objects.CycloneDX14.CDX14ComponentObject;
import org.svip.sbom.model.uids.PURL;

import java.util.Collections;
import java.util.List;

public class PURLFixes implements Fixes{
    @Override
    public List<Fix<?>> fix(Result result, SBOM sbom) {

        if(result.getMessage().contains("does not match"))
            return purlMatchFix(result, sbom);


        return null;
    }

    public List<Fix<?>> purlMatchFix(Result result, SBOM sbom){

        PURL newPurl = null;

        for (Component component: sbom.getComponents()
             ) {

            String[] split = result.getDetails().split(" ");

            if(split[split.length - 1].contains(component.getName()))
                try{

                    String type = "pkg"; // todo is this true for all components
                    String name = component.getName();
                    String nameSpace;
                    String version = null;

                    if(component instanceof SPDX23Package spdx23Package){
                        type = spdx23Package.getType();
                        nameSpace = spdx23Package.getSupplier().getName();
                        version = spdx23Package.getVersion();
                    }
                    else if(component instanceof CDX14Package cdx14Package) {
                        type = cdx14Package.getType();
                        nameSpace = cdx14Package.getSupplier().getName();
                        version = cdx14Package.getVersion();
                    }
                    else
                        nameSpace = component.getAuthor();

                    newPurl = new PURL(type + ":" + nameSpace + "/" + name + ((version != null) ?
                            ("@" + version) : "")); // todo qualifiers?

                }catch (Exception ignored){return null;}

        }

        if(newPurl == null)
            return null;

        try{
            return Collections.singletonList(new Fix<>("", newPurl));
        }
        catch(Exception e){
            return null;
        }
    }

}
