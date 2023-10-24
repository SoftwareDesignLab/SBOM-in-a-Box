package org.svip.repair.repair;

import org.svip.repair.fix.Fix;
import org.svip.sbom.builder.interfaces.generics.ComponentBuilder;
import org.svip.sbom.builder.objects.schemas.CDX14.CDX14Builder;
import org.svip.sbom.builder.objects.schemas.CDX14.CDX14PackageBuilder;
import org.svip.sbom.factory.interfaces.ComponentBuilderFactory;
import org.svip.sbom.factory.objects.CycloneDX14.CDX14PackageBuilderFactory;
import org.svip.sbom.factory.objects.CycloneDX14.CDX14SBOMBuilderFactory;
import org.svip.sbom.factory.objects.SPDX23.SPDX23PackageBuilderFactory;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23Component;
import org.svip.sbom.model.objects.CycloneDX14.CDX14ComponentObject;
import org.svip.sbom.model.shared.Relationship;
import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.shared.util.Description;
import org.svip.sbom.model.shared.util.ExternalReference;
import org.svip.sbom.model.shared.util.LicenseCollection;
import org.svip.sbom.model.uids.Hash;

import java.util.*;

public class RepairSPDX23CDX14 implements Repair {

    @Override
    public SBOM repairSBOM(SBOM sbom, Map<String, Map<String, List<Fix<?>>>> repairs) {

        if(repairs == null)
            return sbom;

        String format = sbom.getFormat();
        String name = sbom.getName();
        String uid = sbom.getUID();
        String version = sbom.getVersion();
        String specVersion = sbom.getSpecVersion();
        Set<String> licenses = sbom.getLicenses();
        CreationData creationData = sbom.getCreationData();
        String documentComment = sbom.getDocumentComment();
        Component rootComponent = sbom.getRootComponent();
        Set<Component> components = sbom.getComponents();
        HashMap<String, Set<Relationship>> relationships = (HashMap<String, Set<Relationship>>) sbom.getRelationships();
        Set<ExternalReference> externalReferences = sbom.getExternalReferences();

        for(String key : repairs.keySet()) {
            List<Fix<?>> fixes = repairs.get(key).get(key);

            Optional<Component> potentialComp = components.stream().filter(x -> x.getName().equals(key)).findFirst();
            Component comp = null;

            String author = null;
            String copyright = null;
            Map<String, String> hashes = null;
            LicenseCollection licenseCollection = null;
            String compName = null;
            String type = null;
            String compUID = null;
            Description desc = null;
            Set<ExternalReference> compExtRef = null;
            Set<String> cpes = null;
            String group = null;
            String mime = null;
            String compVersion = null;
            Organization supplier = null;
            String scope = null;
            Set<String> purls = null;
            String publisher = null;
            HashMap<String, Set<String>> properties = null;
            String comment = null;
            String attributionText = null;

            if(potentialComp.isPresent()) {
                comp = (Component) potentialComp.get();

                author = comp.getAuthor();
                copyright = comp.getCopyright();
                hashes = comp.getHashes();
                licenseCollection = comp.getLicenses();
                compName = comp.getName();
                type = comp.getType();
                compUID = comp.getUID();

                if(comp instanceof CDX14ComponentObject) {

                    CDX14ComponentObject cdxComp = (CDX14ComponentObject) comp;

                    desc = cdxComp.getDescription();
                    compExtRef = cdxComp.getExternalReferences();
                    cpes = cdxComp.getCPEs();
                    group = cdxComp.getGroup();
                    mime = cdxComp.getMimeType();
                    compVersion = cdxComp.getVersion();
                    supplier = cdxComp.getSupplier();
                    scope = cdxComp.getScope();
                    purls = cdxComp.getPURLs();
                    publisher = cdxComp.getPublisher();
                    properties = cdxComp.getProperties();
                }

                else if(comp instanceof SPDX23Component) {
                    SPDX23Component spdx23Component = (SPDX23Component) comp;
                    attributionText = spdx23Component.getAttributionText();
                    comment = spdx23Component.getComment();
                }
            }

            for(Fix<?> fix : fixes) {
                switch(fix.getType()) {

                    case METADATA_SPDXID -> {
                        uid = fix.getNew().toString();
                    }

                    case METADATA_CREATION_DATA -> {
                        creationData.setCreationTime(fix.getNew().toString());
                    }

                    case COMPONENT_AUTHOR -> {
                        author = fix.getNew().toString();
                    }

                    case COMPONENT_PURL -> {
                        purls.add(fix.getNew().toString());
                    }

                    case COMPONENT_COPYRIGHT -> {
                        copyright = fix.getNew().toString();
                    }

                    case METADATA_COMMENT -> {
                        documentComment = fix.getNew().toString();
                    }

                    case METADATA_BOM_VERSION -> {
                        version = fix.getNew().toString();
                    }

                    case COMPONENT_LICENSE -> {
                        licenses.add(fix.getNew().toString());
                    }

                    case COMPONENT_HASH -> {
                        Hash hash = (Hash) fix.getNew();
                        hashes.put(hash.getAlgorithm().toString(), hash.getValue());
                    }

                    case COMPONENT_CPE -> {
                        cpes.add(fix.getNew().toString());
                    }

                    case COMPONENT_BOM_REF -> {
                        compUID = fix.getNew().toString();
                    }

                    case COMPONENT_ATTRIBUTION_TEXT -> {
                        attributionText = fix.getNew().toString();
                    }
                }
            }

            if(comp != null) {

                ComponentBuilderFactory packageBuilderFactory = null;
                ComponentBuilder compBuilder = null;

                if(comp instanceof CDX14ComponentObject)
                    packageBuilderFactory = new CDX14PackageBuilderFactory();

                else if(comp instanceof SPDX23Component)
                    packageBuilderFactory = new SPDX23PackageBuilderFactory();

                compBuilder = packageBuilderFactory.createBuilder();

                compBuilder.setAuthor(author);
                compBuilder.setCopyright(copyright);
                compBuilder.setLicenses(licenseCollection);
                compBuilder.setName(compName);
                compBuilder.setType(type);
                compBuilder.setCopyright(copyright);
                compBuilder.setUID(compUID);

                compBuilder.setDescription(desc);
                compBuilder.setSupplier(supplier);
                compBuilder.setGroup(group);
                compBuilder.setMimeType(mime);
                compBuilder.setPublisher(publisher);
                compBuilder.setVersion(compVersion);
                compBuilder.setScope(scope);

                for(String hash : hashes.keySet()) {
                    compBuilder.addHash(hash, hashes.get(hash));
                }

                for(ExternalReference ref : compExtRef) {
                    compBuilder.addExternalReference(ref);
                }

                for(String cpe : cpes) {
                    compBuilder.addCPE(cpe);
                }

                for(String purl : purls) {
                    compBuilder.addPURL(purl);
                }

                for(String property : properties.keySet()) {
                    for(String value : properties.get(property)) {
                        compBuilder.addProperty(property, value);
                    }
                }

                components.remove(comp);
                components.add(compBuilder.build());
            }
        }

        CDX14SBOMBuilderFactory builderFactory = new CDX14SBOMBuilderFactory();
        CDX14Builder builder = builderFactory.createBuilder();
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

        for(ExternalReference ref : externalReferences) {
            builder.addExternalReference(ref);
        }

        return builder.buildCDX14SBOM();
    }

}