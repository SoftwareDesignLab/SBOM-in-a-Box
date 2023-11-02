package org.svip.repair.fix;

import org.svip.metrics.resultfactory.Result;
import org.svip.metrics.resultfactory.enumerations.STATUS;
import org.svip.metrics.tests.PURLTest;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.interfaces.schemas.CycloneDX14.CDX14Package;
import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23Package;
import org.svip.sbom.model.uids.PURL;

import java.util.*;

/**
 * Fixes class to generate suggested component PURL repairs
 */
public class PURLFixes implements Fixes {
    @Override
    public List<Fix<?>> fix(Result result, SBOM sbom, String componentName, Integer componentHashCode) {

        // Create a new purl
        PURL newPurl = null;
        Set<String> oldPurls = new HashSet<>();

        Optional<Component> potentialComp = sbom.getComponents().stream().filter(x -> x.hashCode() == componentHashCode).findFirst();
        Component component = potentialComp.get();

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

                oldPurls = spdx23Package.getPURLs();

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

                oldPurls = cdx14Package.getPURLs();

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


            //Ensure we aren't suggesting an invalid purl...
            PURLTest purlTest = new PURLTest(component);
            Set<Result> results =purlTest.test("purl", newPurl.toString());

            if(results.stream().filter(x -> x.getStatus() != STATUS.PASS).count() > 0)
                return null;


        } catch (Exception ignored) {
            // If all goes wrong, return null
            return null;
        }


        // If the newPURL is null, return null
        if (newPurl == null || oldPurls.contains(newPurl.toString()))
            return null;

        try {

            // Then, try to return the fixes
            return Collections.singletonList(new Fix<>(FixType.COMPONENT_PURL, oldPurls, newPurl));

        } catch (Exception e) {

            // But if that goes wrong return null
            return null;

        }

    }

}
