package org.svip.builderfactory;

import org.junit.jupiter.api.Test;
import org.svip.builders.component.SVIPComponentBuilder;
import org.svip.sbom.builder.objects.SVIPSBOMBuilder;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.interfaces.schemas.CycloneDX14.CDX14Package;
import org.svip.sbom.model.objects.CycloneDX14.CDX14ComponentObject;
import org.svip.sbom.model.objects.SVIPComponentObject;
import org.svip.sbom.model.shared.Relationship;
import org.svip.sbom.model.shared.metadata.CreationData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * file: SVIPSBOMBuilderFactoryTest.java
 * File to test SVIPSBOMBuilderFactory and SVIP Builder Object
 *
 * @author Kevin Laporte
 */
public class SVIPSBOMBuilderFactoryTest {
    SVIPSBOMBuilderFactory test_sbomBuilderFactory = new SVIPSBOMBuilderFactory();
    SVIPComponentBuilder test_componentBuilder = new SVIPComponentBuilder();
    SVIPSBOMBuilder test_SVIPSBOMBuilder = test_sbomBuilderFactory.createBuilder();
    SBOM test_SVIPSBOM = test_SVIPSBOMBuilder.Build();

    String test_format = "SVIP";

    String test_name = "Test SVIP SBOM";

    String test_uid = "749d4a17-1074-4b78-a968-fafc67378f75";

    String test_version = "1.1";

    String test_specVersion = "1.0";

    String test_license1 = "PSFL";

    String test_license2 = "GPL";

    CreationData test_creationData = new CreationData();

    String test_documentComment = "This is a test comment.";

    SVIPComponentObject test_rootComponent;

    Set<Component> test_components = new HashSet<Component>();

    HashMap<String, Set<Relationship>> test_relationships;
    @Test
    void getFormat_is_test_format_when_setFormat_is_used_test() {
        test_SVIPSBOMBuilder.setFormat(test_format);
        test_SVIPSBOM = test_SVIPSBOMBuilder.Build();

        assertEquals(test_format, test_SVIPSBOM.getFormat());
    }

    @Test
    void getName_is_test_name_when_setName_is_used_test(){
        test_SVIPSBOMBuilder.setName(test_name);
        test_SVIPSBOM = test_SVIPSBOMBuilder.Build();

        assertEquals(test_name, test_SVIPSBOM.getName());
    }

    @Test
    void getUID_is_test_uid_when_setUID_is_used_test(){
        test_SVIPSBOMBuilder.setUID(test_uid);
        test_SVIPSBOM = test_SVIPSBOMBuilder.Build();

        assertEquals(test_uid, test_SVIPSBOM.getUID());
    }

    @Test
    void getVersion_is_test_version_when_setVersion_is_used_test(){
        test_SVIPSBOMBuilder.setVersion(test_version);
        test_SVIPSBOM = test_SVIPSBOMBuilder.Build();

        assertEquals(test_version, test_SVIPSBOM.getVersion());
    }

    @Test
    void getSpecVersion_is_test_specVersion_when_setSpecVersion_is_used_test(){
        test_SVIPSBOMBuilder.setSpecVersion(test_specVersion);
        test_SVIPSBOM = test_SVIPSBOMBuilder.Build();

        assertEquals(test_specVersion, test_SVIPSBOM.getSpecVersion());
    }

    @Test
    void getLicenses_is_test_set_when_addLicense_is_used_test(){
        test_SVIPSBOMBuilder.addLicense(test_license1);
        test_SVIPSBOMBuilder.addLicense(test_license2);
        test_SVIPSBOM = test_SVIPSBOMBuilder.Build();

        HashSet<String> test_Set = new HashSet<>();
        test_Set.add(test_license1);
        test_Set.add(test_license2);

        assertEquals(test_Set, test_SVIPSBOM.getLicenses());
    }

    @Test
    void getCreationData_is_test_creationData_when_setCreationData_is_used_test(){
        test_creationData.setCreationTime("17:32");
        test_creationData.setCreatorComment("This was created as a test.");

        test_SVIPSBOMBuilder.setCreationData(test_creationData);
        test_SVIPSBOM = test_SVIPSBOMBuilder.Build();

        assertEquals(test_creationData, test_SVIPSBOM.getCreationData());
    }

    @Test
    void getDocumentComment_is_test_documentComment_when_setDocumentComment_is_used_test(){
        test_SVIPSBOMBuilder.setDocumentComment(test_documentComment);
        test_SVIPSBOM = test_SVIPSBOMBuilder.Build();

        assertEquals(test_documentComment, test_SVIPSBOM.getDocumentComment());
    }

    @Test
    void getRootComponent_is_test_rootComponent_when_setRootComponent_is_used_test(){
        test_rootComponent = new SVIPComponentObject("SVIP", null, "Tester", "Test Component",null,null,null,null,null,null,null,null,null, null, null, null,null,null,null,null,null,null,null,null,null,null,null,null,null,null);
        test_SVIPSBOMBuilder.setRootComponent(test_rootComponent);
        test_SVIPSBOM = test_SVIPSBOMBuilder.Build();

        assertEquals(test_rootComponent, test_SVIPSBOM.getRootComponent());
    }

    @Test
    void getComponents_is_test_components_when_addComponent_is_used_test(){
        SVIPComponentObject test_componentA = new SVIPComponentObject("SVIP", null, "Tester 1", "Test Component A",null,null,null,null,null,null,null,null,null, null, null, null,null,null,null,null,null,null,null,null,null,null,null,null,null,null);
        SVIPComponentObject test_componentB = new SVIPComponentObject("SVIP", null, "Tester Two", "Test Component B",null,null,null,null,null,null,null,null,null, null, null, null,null,null,null,null,null,null,null,null,null,null,null,null,null,null);
        test_SVIPSBOMBuilder.addComponent(test_componentA);
        test_SVIPSBOMBuilder.addComponent(test_componentB);
        test_SVIPSBOM = test_SVIPSBOMBuilder.Build();

        test_components.add(test_componentA);
        test_components.add(test_componentB);

        assertEquals(test_components, test_SVIPSBOM.getComponents());
    }

    // TODO addCDX14Package
//    @Test
//    void getComponents_is_test_components_when_addCDX14Package_is_used_test(){
//        SVIPComponentObject test_componentA = new SVIPComponentObject("SVIP", null, "Tester 1", "Test Component A",null,null,null,null,null,null,null,null,null, null, null, null,null,null,null,null,null,null,null,null,null,null,null,null,null,null);
//        CDX14Package test_componentB = new CDX14Package("SVIP", null, "Tester Two", "Test Component B",null,null,null,null,null,null,null,null,null, null, null, null,null,null,null,null,null,null,null,null,null,null,null,null,null,null);
//        test_SVIPSBOMBuilder.addComponent(test_componentA);
//        test_SVIPSBOMBuilder.addCDX14Package(test_componentB);
//        test_SVIPSBOM = test_SVIPSBOMBuilder.Build();
//
//        test_components.add(test_componentA);
//        test_components.add(test_componentB);
//
//        assertEquals(test_components, test_SVIPSBOM.getComponents());
//    }

    // TODO addSPDX23Component

    // TODO addRelationship

    // TODO addExternalReference

    // TODO setSPDXLicenseListVersion

    // TODO buildSPDX23SBOM

    // TODO buildCDX14SBOM
}
