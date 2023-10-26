package org.svip.repair.repair;

import org.svip.repair.fix.Fix;
import org.svip.sbom.builder.interfaces.generics.ComponentBuilder;
import org.svip.sbom.builder.interfaces.generics.SBOMBuilder;
import org.svip.sbom.builder.interfaces.schemas.SPDX23.SPDX23ComponentBuilder;
import org.svip.sbom.builder.objects.schemas.CDX14.CDX14Builder;
import org.svip.sbom.builder.objects.schemas.CDX14.CDX14PackageBuilder;
import org.svip.sbom.builder.objects.schemas.SPDX23.SPDX23Builder;
import org.svip.sbom.builder.objects.schemas.SPDX23.SPDX23FileBuilder;
import org.svip.sbom.builder.objects.schemas.SPDX23.SPDX23PackageBuilder;
import org.svip.sbom.factory.interfaces.ComponentBuilderFactory;
import org.svip.sbom.factory.interfaces.SBOMBuilderFactory;
import org.svip.sbom.factory.objects.CycloneDX14.CDX14PackageBuilderFactory;
import org.svip.sbom.factory.objects.CycloneDX14.CDX14SBOMBuilderFactory;
import org.svip.sbom.factory.objects.SPDX23.SPDX23FileBuilderFactory;
import org.svip.sbom.factory.objects.SPDX23.SPDX23PackageBuilderFactory;
import org.svip.sbom.factory.objects.SPDX23.SPDX23SBOMBuilderFactory;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23Component;
import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23Package;
import org.svip.sbom.model.objects.CycloneDX14.CDX14ComponentObject;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.objects.SPDX23.SPDX23FileObject;
import org.svip.sbom.model.objects.SPDX23.SPDX23PackageObject;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.sbom.model.shared.Relationship;
import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.shared.util.Description;
import org.svip.sbom.model.shared.util.ExternalReference;
import org.svip.sbom.model.shared.util.LicenseCollection;
import org.svip.sbom.model.uids.Hash;

import java.util.*;
import java.util.stream.Stream;

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

            long amount = components.stream().filter(x -> x.getName().equals(key)).count();

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
            String fileNotice = null;
            String builtDate = null;
            String fileName = null;
            Boolean filesAnalyzed = false;
            String validUntil = null;
            String verificationCode = null;
            String downloadLocation = null;
            String homePage = null;
            String releaseDate = null;
            String sourceInfo = null;

            if(!potentialComp.isPresent())
                continue;

            comp = potentialComp.get();

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

            else if(comp instanceof SPDX23PackageObject) {
                SPDX23PackageObject spdx23Component = (SPDX23PackageObject) comp;
                attributionText = spdx23Component.getAttributionText();
                comment = spdx23Component.getComment();
                desc = spdx23Component.getDescription();
                compExtRef = spdx23Component.getExternalReferences();
                cpes = spdx23Component.getCPEs();
                compVersion = spdx23Component.getVersion();
                supplier = spdx23Component.getSupplier();
                purls = spdx23Component.getPURLs();
                builtDate = spdx23Component.getBuiltDate();
                validUntil = spdx23Component.getValidUntilDate();
                verificationCode = spdx23Component.getVerificationCode();
                downloadLocation = spdx23Component.getDownloadLocation();
                fileName = spdx23Component.getFileName();
                filesAnalyzed = spdx23Component.getFilesAnalyzed();
                homePage = spdx23Component.getHomePage();
                releaseDate = spdx23Component.getReleaseDate();
                sourceInfo = spdx23Component.getSourceInfo();
            }

            else if(comp instanceof SPDX23FileObject) {
                SPDX23FileObject spdx23File = (SPDX23FileObject) comp;
                attributionText = spdx23File.getAttributionText();
                comment = spdx23File.getComment();
                fileNotice = spdx23File.getFileNotice();
            }


            for(Fix<?> fix : fixes) {
                switch(fix.getType()) {

                    case COMPONENT_FILE_NOTICE -> {
                        fileNotice = fix.getNew().toString();
                    }

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
                        cpes.clear(); //Assuming if it is a fix that the previous were invalid
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

            ComponentBuilderFactory packageBuilderFactory = null;
            ComponentBuilder compBuilder = null;

            if(comp instanceof CDX14ComponentObject)
                packageBuilderFactory = new CDX14PackageBuilderFactory();

            else if(comp instanceof SPDX23PackageObject)
                packageBuilderFactory = new SPDX23PackageBuilderFactory();

            else if(comp instanceof SPDX23FileBuilder)
                packageBuilderFactory = new SPDX23FileBuilderFactory();

            compBuilder = packageBuilderFactory.createBuilder();

            compBuilder.setAuthor(author);
            compBuilder.setCopyright(copyright);
            compBuilder.setLicenses(licenseCollection);
            compBuilder.setName(compName);
            compBuilder.setType(type);
            compBuilder.setCopyright(copyright);
            compBuilder.setUID(compUID);

            for(String hash : hashes.keySet()) {
                compBuilder.addHash(hash, hashes.get(hash));
            }


            if(compBuilder instanceof CDX14PackageBuilder) {

                CDX14PackageBuilder cdxBuilder = (CDX14PackageBuilder) compBuilder;

                cdxBuilder.setDescription(desc);
                cdxBuilder.setSupplier(supplier);
                cdxBuilder.setGroup(group);
                cdxBuilder.setMimeType(mime);
                cdxBuilder.setPublisher(publisher);
                cdxBuilder.setVersion(compVersion);
                cdxBuilder.setScope(scope);

                for(ExternalReference ref : compExtRef) {
                    cdxBuilder.addExternalReference(ref);
                }

                for(String cpe : cpes) {
                    cdxBuilder.addCPE(cpe);
                }

                for(String purl : purls) {
                    cdxBuilder.addPURL(purl);
                }

                for(String property : properties.keySet()) {
                    for(String value : properties.get(property)) {
                        cdxBuilder.addProperty(property, value);
                    }
                }
            } else if(compBuilder instanceof SPDX23PackageBuilder) {
                SPDX23PackageBuilder spdxBuilder = (SPDX23PackageBuilder) compBuilder;

                for(ExternalReference ref : compExtRef) {
                    spdxBuilder.addExternalReference(ref);
                }

                for(String cpe : cpes) {
                    spdxBuilder.addCPE(cpe);
                }

                for(String purl : purls) {
                    spdxBuilder.addPURL(purl);
                }

                spdxBuilder.setAttributionText(attributionText);
                spdxBuilder.setComment(comment);
                spdxBuilder.setDescription(desc);
                spdxBuilder.setSupplier(supplier);
                spdxBuilder.setVersion(compVersion);
                spdxBuilder.setBuildDate(builtDate);
                spdxBuilder.setDownloadLocation(downloadLocation);
                spdxBuilder.setFileName(fileName);
                spdxBuilder.setFilesAnalyzed(filesAnalyzed);
                spdxBuilder.setReleaseDate(releaseDate);
                spdxBuilder.setValidUntilDate(validUntil);
                spdxBuilder.setVerificationCode(verificationCode);
                spdxBuilder.setHomePage(homePage);
                spdxBuilder.setSourceInfo(sourceInfo);
            } else if(compBuilder instanceof SPDX23FileBuilder) {
                SPDX23FileBuilder spdx23FileBuilder = (SPDX23FileBuilder) compBuilder;

                spdx23FileBuilder.setFileNotice(fileNotice);
                spdx23FileBuilder.setComment(comment);
                spdx23FileBuilder.setAttributionText(attributionText);
            }

            components.remove(comp);
            components.add(compBuilder.build());

        }

        SBOMBuilderFactory builderFactory = null;

        if (sbom instanceof CDX14SBOM)
            builderFactory = new CDX14SBOMBuilderFactory();

        else if(sbom instanceof SPDX23SBOM)
            builderFactory = new SPDX23SBOMBuilderFactory();

        SBOMBuilder builder = builderFactory.createBuilder();

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

        if(builder instanceof SPDX23Builder) {
            SPDX23SBOM spdx23SBOM = (SPDX23SBOM) sbom;
            SPDX23Builder spdx23Builder = (SPDX23Builder) builder;

            spdx23Builder.setSPDXLicenseListVersion(spdx23SBOM.getSPDXLicenseListVersion());
        }

        return builder.Build();
    }

}