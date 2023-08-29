package org.svip.repair.fix;

import org.svip.metrics.resultfactory.Result;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.interfaces.schemas.CycloneDX14.CDX14Package;
import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23Package;
import org.svip.sbom.model.uids.PURL;

import java.util.Collections;
import java.util.List;

/**
 * Fixes class to generate suggested component PURL repairs
 */
public class PURLFixes implements Fixes {
    @Override
    public List<Fix<?>> fix(Result result, SBOM sbom, String repairSubType) {
        PURL newPurl = null;

        for (Component component : sbom.getComponents() // for each component in the SBOM
        ) {

            String[] split = result.getDetails().split(" "); // split up result message

            boolean fixThisPurl = false;

            // are we fixing this PURL, if so is it because something doesn't match, or is inaccurate?
            if (result.getMessage().toLowerCase().contains("does not match"))
                fixThisPurl = split[split.length - 1].contains(component.getName());
            else if (result.getTest().contains("Accurate")) {
                if (component instanceof SPDX23Package spdx23Package)
                    for (String purl : spdx23Package.getPURLs()
                    ) {
                        if (purl.equals(split[0])) {
                            fixThisPurl = true;
                            break;
                        }
                    }
                else if (component instanceof CDX14Package cdx14Package)
                    for (String purl : cdx14Package.getPURLs()
                    ) {
                        if (purl.equals(split[0])) {
                            fixThisPurl = true;
                            break;
                        }
                    }
            }

            if (fixThisPurl)
                try {

                    String type = "pkg"; // todo is this true for all components?
                    String name = repairSubType;
                    if (component.getName() != null)
                        name = component.getName();
                    String nameSpace = null;
                    String version = null;

                    if (component instanceof SPDX23Package spdx23Package) {
                        if (spdx23Package.getType() != null)
                            type = spdx23Package.getType();
                        if (spdx23Package.getSupplier() != null)
                            nameSpace = spdx23Package.getSupplier().getName();
                        if (spdx23Package.getVersion() != null)
                            version = spdx23Package.getVersion();
                    } else if (component instanceof CDX14Package cdx14Package) {
                        if (cdx14Package.getType() != null)
                            type = cdx14Package.getType();
                        if (cdx14Package.getSupplier() != null)
                            nameSpace = cdx14Package.getSupplier().getName();
                        if (cdx14Package.getVersion() != null)
                            version = cdx14Package.getVersion();
                    }

                    if (nameSpace == null)
                        nameSpace = component.getAuthor();
                    if (nameSpace == null)
                        nameSpace = repairSubType;

                    newPurl = new PURL(type + ":" + nameSpace + "/" + name +
                            ((version != null) ? ("@" + version) : "")); // todo qualifiers?

                    break;

                } catch (Exception ignored) {
                    return null;
                }

        }

        if (newPurl == null)
            return null;

        try {
            return Collections.singletonList(new Fix<>("", newPurl));
        } catch (Exception e) {
            return null;
        }
    }


}
