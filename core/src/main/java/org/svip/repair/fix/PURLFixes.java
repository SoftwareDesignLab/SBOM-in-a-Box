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
    public List<Fix<?>> fix(Result result, SBOM sbom, String componentName) {

        // Create a new purl
        PURL newPurl = null;

        for (Component component : sbom.getComponents() // for each component in the SBOM
        ) {

            String[] split = result.getDetails().split(" "); // split up result message

            boolean fixThisPurl = false;

            // are we fixing this PURL, if so is it because something doesn't match, or is inaccurate?
            if (result.getMessage().toLowerCase().contains("does not match"))
                fixThisPurl = split[split.length - 1].contains(component.getName());

            // Otherwise, if the result was accurate
            else if (result.getTest().contains("Accurate")) {

                // If the component is an SPDX 2.3 Package
                if (component instanceof SPDX23Package spdx23Package) {

                    // For each PURL
                    for (String purl : spdx23Package.getPURLs()) {
                        // If the PURL is invalid, set fixThisPurl to true and break
                        if (purl.equals(split[0])) {

                            fixThisPurl = true;
                            break;

                        }

                    }
                }
                // Otherwise, if the component is a CycloneDX 1.4 Package
                else if (component instanceof CDX14Package cdx14Package)

                    // For each PURL
                    for (String purl : cdx14Package.getPURLs()) {

                        // If the PURL is invalid, set fixThisPurl to true and break
                        if (purl.equals(split[0])) {

                            fixThisPurl = true;
                            break;

                        }

                    }

            }

            // If This PURL needs to be fixes
            if (fixThisPurl)
                try {

                    // Set the standard identifiers/fields
                    String type = "pkg"; // todo is this true for all components?
                        String name = componentName;
                    if (component.getName() != null)
                        name = component.getName();
                    String nameSpace = null;
                    String version = null;

                    // If the component is an SPDX 2.3 Package
                    if (component instanceof SPDX23Package spdx23Package) {

                        // And if the type is null, set the type
                        if (spdx23Package.getType() != null)
                            type = spdx23Package.getType();

                        // And if the Supplier is null, set the Supplier
                        if (spdx23Package.getSupplier() != null)
                            nameSpace = spdx23Package.getSupplier().getName();

                        // And if the Version is null, set the Version
                        if (spdx23Package.getVersion() != null)
                            version = spdx23Package.getVersion();

                    }
                    // Otherwise, if the component is a CycloneDX 1.4 Package
                    else if (component instanceof CDX14Package cdx14Package) {

                        // If the type is null, set the Type
                        if (cdx14Package.getType() != null)
                            type = cdx14Package.getType();

                        // If the Supplier is null, set the Supplier
                        if (cdx14Package.getSupplier() != null)
                            nameSpace = cdx14Package.getSupplier().getName();

                        // If the Version is null, set the Version
                        if (cdx14Package.getVersion() != null)
                            version = cdx14Package.getVersion();
                    }

                    // If the namespace is null, set the namespace equal to the component author
                    if (nameSpace == null)
                        nameSpace = component.getAuthor();
                    // And if that doesn't work, try setting the namespace to the repairSubType
                    if (nameSpace == null)
                        nameSpace = componentName;

                    // Create the new PURL
                    newPurl = new PURL(type + ":" + nameSpace + "/" + name +
                            ((version != null) ? ("@" + version) : "")); // todo qualifiers?

                    // break
                    break;

                } catch (Exception ignored) {
                    // If all goes wrong, return null
                    return null;
                }

        }

        // If the newPURL is null, return null
        if (newPurl == null)
            return null;

        try {

            // Then, try to return the fixes
            return Collections.singletonList(new Fix<>(FixType.COMPONENT_PURL, "", newPurl));

        } catch (Exception e) {

            // But if that goes wrong return null
            return null;

        }

    }

}
