package org.svip.sbomanalysis.comparison.merger;

import org.checkerframework.checker.units.qual.C;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.CycloneDX14.CDX14ComponentObject;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.objects.SVIPComponentObject;
import org.svip.sbom.model.shared.util.LicenseCollection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class Merger {

    public Merger(SBOM A, SBOM B) {

    }

    protected abstract SBOM mergeSBOM(SBOM A, SBOM B);

    protected Set<Component> mergeComponents(Set<Component> A, Set<Component> B) {

        Set<Component> merged_components = new HashSet<>();

        for(Component current_A : A) {
            for(Component current_B : B) {
                if(current_A.getUID().equals(current_B.getUID()) && current_A.getName().equals(current_B.getName())) {
                    /** Merge all relevant data into A component **/
                    merged_components.add(mergeComponent(current_A, current_B));
                    B.remove(current_B);
                }
            }
        }
        merged_components.addAll(B);
        return merged_components;
    }

    protected Component mergeComponent(Component A, Component B) {

        for(String concluded : B.getLicenses().getConcluded().stream().toList()) {
            A.getLicenses().addConcludedLicenseString(concluded);
        }
        for(String declared : B.getLicenses().getDeclared().stream().toList()) {
            A.getLicenses().addDeclaredLicense(declared);
        }
        for(String info : B.getLicenses().getInfoFromFiles().stream().toList()) {
            A.getLicenses().addLicenseInfoFromFile(info);
        }

        for(Map.Entry hash : B.getHashes().entrySet()) {
            //A.getHashes().putAll();
        }

        return A;
    }
}
