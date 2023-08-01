package org.svip.sbom.builder.objects.schemas.CDX14;

import org.junit.jupiter.api.Test;
import org.svip.sbom.builder.interfaces.schemas.CycloneDX14.CDX14SBOMBuilder;
import org.svip.sbom.builder.objects.schemas.CDX14.CDX14PackageBuilder;
import org.svip.sbom.factory.objects.CycloneDX14.CDX14SBOMBuilderFactory;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.interfaces.schemas.CycloneDX14.CDX14Package;
import org.svip.sbom.model.objects.CycloneDX14.CDX14ComponentObject;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.shared.Relationship;
import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbom.model.shared.util.ExternalReference;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * file: CDX14SBOMBuilderFactoryTest.java
 * File to test CDX14SBOMBuilderFactory and CDX14Builder Object
 *
 * @author Kevin Laporte
 */
public class CDX14SBOMBuilderFactoryTest {

    CDX14SBOMBuilderFactory test_sbomBuilderFactory = new CDX14SBOMBuilderFactory();
    CDX14PackageBuilder test_packageBuilder = new CDX14PackageBuilder();
    CDX14SBOMBuilder test_CDX14SBOMBuilder = test_sbomBuilderFactory.createBuilder();

    CDX14SBOM test_CDX14SBOM = test_CDX14SBOMBuilder.buildCDX14SBOM();

    String test_format = "CycloneDX";

    String test_name = "Test CDX SBOM";

    String test_uid = "749d4a17-1074-4b78-a968-fafc67378f75";

    String test_version = "1.1";

    String test_specVersion = "1.4";

    String test_license1 = "PSFL";

    String test_license2 = "GPL";

    CreationData test_creationData = new CreationData();

    String test_documentComment = "This is a test comment.";

    CDX14ComponentObject test_rootComponent;

    Set<CDX14ComponentObject> test_componentsCDX = new HashSet<CDX14ComponentObject>();
    Set<Component> test_components = new HashSet<Component>();


    @Test
    void getFormat_is_test_format_when_setFormat_is_used_test() {
        test_CDX14SBOMBuilder.setFormat(test_format);
        test_CDX14SBOM = test_CDX14SBOMBuilder.buildCDX14SBOM();

        assertEquals(test_format, test_CDX14SBOM.getFormat());
    }

    @Test
    void getName_is_test_name_when_setName_is_used_test(){
        test_CDX14SBOMBuilder.setName(test_name);
        test_CDX14SBOM = test_CDX14SBOMBuilder.buildCDX14SBOM();

        assertEquals(test_name, test_CDX14SBOM.getName());
    }

    @Test
    void getUID_is_test_uid_when_setUID_is_used_test(){
        test_CDX14SBOMBuilder.setUID(test_uid);
        test_CDX14SBOM = test_CDX14SBOMBuilder.buildCDX14SBOM();

        assertEquals(test_uid, test_CDX14SBOM.getUID());
    }

    @Test
    void getVersion_is_test_version_when_setVersion_is_used_test(){
        test_CDX14SBOMBuilder.setVersion(test_version);
        test_CDX14SBOM = test_CDX14SBOMBuilder.buildCDX14SBOM();

        assertEquals(test_version, test_CDX14SBOM.getVersion());
    }

    @Test
    void getSpecVersion_is_test_specVersion_when_setSpecVersion_is_used_test(){
        test_CDX14SBOMBuilder.setSpecVersion(test_specVersion);
        test_CDX14SBOM = test_CDX14SBOMBuilder.buildCDX14SBOM();

        assertEquals(test_specVersion, test_CDX14SBOM.getSpecVersion());
    }

    @Test
    void getLicenses_is_test_set_when_addLicense_is_used_test(){
        test_CDX14SBOMBuilder.addLicense(test_license1);
        test_CDX14SBOMBuilder.addLicense(test_license2);
        test_CDX14SBOM = test_CDX14SBOMBuilder.buildCDX14SBOM();

        HashSet<String> test_Set = new HashSet<>();
        test_Set.add(test_license1);
        test_Set.add(test_license2);

        assertEquals(test_Set, test_CDX14SBOM.getLicenses());
    }

    @Test
    void getCreationData_is_test_creationData_when_setCreationData_is_used_test(){
        test_creationData.setCreationTime("17:32");
        test_creationData.setCreatorComment("This was created as a test.");

        test_CDX14SBOMBuilder.setCreationData(test_creationData);
        test_CDX14SBOM = test_CDX14SBOMBuilder.buildCDX14SBOM();

        assertEquals(test_creationData, test_CDX14SBOM.getCreationData());
    }

    @Test
    void getDocumentComment_is_test_documentComment_when_setDocumentComment_is_used_test(){
        test_CDX14SBOMBuilder.setDocumentComment(test_documentComment);
        test_CDX14SBOM = test_CDX14SBOMBuilder.buildCDX14SBOM();

        assertEquals(test_documentComment, test_CDX14SBOM.getDocumentComment());
    }

    @Test
    void getRootComponent_is_test_rootComponent_when_setRootCDXComponentObject_is_used_test(){
        test_rootComponent = new CDX14ComponentObject("CycloneDX", null, "Tester", "Test Component",null,null,null,null,null,null,null,null,null, null, null, null,null,null);
        test_CDX14SBOMBuilder.setRootComponent(test_rootComponent);
        test_CDX14SBOM = test_CDX14SBOMBuilder.buildCDX14SBOM();

        assertEquals(test_rootComponent, test_CDX14SBOM.getRootComponent());
    }

    @Test
    void getComponents_is_test_components_when_addCDXComponentObject_is_used_test(){
        CDX14ComponentObject test_componentA = new CDX14ComponentObject("CycloneDX", null, "Tester One", "Test Component A",null,null,null,null,null,null,null,null,null, null, null, null,null,null);
        CDX14ComponentObject test_componentB = new CDX14ComponentObject("CycloneDX", null, "Tester 2", "Test Component B",null,null,null,null,null,null,null,null,null, null, null, null,null,null);
        test_CDX14SBOMBuilder.addComponent(test_componentA);
        test_CDX14SBOMBuilder.addComponent(test_componentB);
        test_CDX14SBOM = test_CDX14SBOMBuilder.buildCDX14SBOM();

        test_componentsCDX.add(test_componentA);
        test_componentsCDX.add(test_componentB);

        test_components.add(test_componentA);
        test_components.add(test_componentB);

        assertEquals(test_components, test_CDX14SBOM.getComponents());
    }

    @Test
    void getComponents_contains_test_package_when_addCDX14Package_is_used_test(){
        test_packageBuilder.setName("test name");
        CDX14Package test_package = test_packageBuilder.buildAndFlush();

        test_CDX14SBOMBuilder.addCDX14Package(test_package);
        test_CDX14SBOM = test_CDX14SBOMBuilder.buildCDX14SBOM();

        assertTrue(test_CDX14SBOM.getComponents().contains(test_package));
    }

    @Test
    void getRelationships_contains_test_relationships_when_addRelationship_is_used_test(){
        HashMap<String, Relationship> test_relationships = new HashMap<String, Relationship>();
        Relationship test_relationship = new Relationship("001", "dependant");
        test_CDX14SBOMBuilder.addRelationship("test_component", test_relationship);

        test_CDX14SBOM = test_CDX14SBOMBuilder.buildCDX14SBOM();

        assertTrue(test_CDX14SBOM.getRelationships().containsKey("test_component"));
    }

    @Test
    void getExternalReferences_is_test_externalRefs_when_addExternalReference_is_used_test(){
        HashSet<ExternalReference> test_externalRefs = new HashSet<ExternalReference>();
        ExternalReference test_externalRef = new ExternalReference("really cool url", "CPE");

        test_CDX14SBOMBuilder.addExternalReference(test_externalRef);
        test_externalRefs.add(test_externalRef);

        test_CDX14SBOM = test_CDX14SBOMBuilder.buildCDX14SBOM();

        assertEquals(test_externalRefs, test_CDX14SBOM.getExternalReferences());
    }

    @Test
    void getName_is_test_name_when_SBOM_is_built_with_Build_method_test(){
        test_CDX14SBOMBuilder.setName(test_name);
        SBOM test_SBOM = test_CDX14SBOMBuilder.Build();

        assertEquals(test_name, test_SBOM.getName());
    }
}
