package org.svip.repair.repair;

import org.svip.repair.fix.Fix;
import org.svip.sbom.builder.interfaces.generics.SBOMBuilder;
import org.svip.sbom.builder.interfaces.schemas.SPDX23.SPDX23ComponentBuilder;
import org.svip.sbom.builder.objects.schemas.CDX14.CDX14Builder;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23Component;
import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23Package;
import org.svip.sbom.model.objects.CycloneDX14.CDX14ComponentObject;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.objects.SPDX23.SPDX23PackageObject;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.sbom.model.shared.Relationship;
import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbom.model.shared.util.ExternalReference;
import org.svip.sbom.model.uids.Hash;

import java.util.*;

public class RepairCDX14 implements Repair { // todo depreciate class

    private final static String METADATA_FIX = "metadata";

    @Override
    public SBOM repairSBOM(String uid, SBOM sbom, Map<String, Map<String, List<Fix<?>>>> repairs) {

        String format = sbom.getFormat();
        String name = sbom.getName();
        //String uid = sbom.getUID();
        String version = sbom.getVersion();
        String specVersion = sbom.getSpecVersion();
        Set<String> licenses = sbom.getLicenses();
        CreationData creationData = sbom.getCreationData();
        String documentComment = sbom.getDocumentComment();
        SPDX23PackageObject rootComponent = (SPDX23PackageObject) sbom.getRootComponent();
        Set<Component> components = sbom.getComponents();
        HashMap<String, Set<Relationship>> relationships = (HashMap<String, Set<Relationship>>) sbom.getRelationships();
        Set<ExternalReference> externalReferences = sbom.getExternalReferences();

        for(String key : repairs.keySet()) {
            List<Fix<?>> fixes = repairs.get(key).get(key);

            Optional<Component> potentialComp = components.stream().filter(x -> x.getName().equals(key)).findFirst();
            Component comp = null;

            if(potentialComp.isPresent())
                comp = potentialComp.get();

            for(Fix<?> fix : fixes) {
                switch(fix.getType()) {
                    case METADATA_SPDXID -> {
                        uid = fix.toString();
                    }

                    case METADATA_CREATION_DATA -> {
                        creationData.setCreationTime(fix.toString());
                    }

                    case COMPONENT_AUTHOR -> {

                    }
                }
            }
        }

        CDX14Builder builder = new CDX14Builder();
        builder.setDocumentComment(documentComment);
        builder.setFormat(format);
        builder.setName(name);
        builder.setUID(uid);
        builder.setVersion(version);
        builder.setSpecVersion(specVersion);
        builder.setRootComponent(rootComponent);
        builder.setCreationData(creationData);

        for(Component c : components) {
            builder.addComponent(c);
        }

        for(String s : relationships.keySet()) {
            Set<Relationship> relations = relationships.get(s);

            for(Relationship r : relations) {
                builder.addRelationship(s, r);
            }
        }

        return builder.buildCDX14SBOM();
    }

}
