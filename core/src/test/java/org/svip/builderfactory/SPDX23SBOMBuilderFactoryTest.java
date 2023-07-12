package org.svip.builderfactory;

import org.junit.jupiter.api.Test;
import org.svip.builders.component.SPDX23FileBuilder;
import org.svip.builders.component.SPDX23PackageBuilder;
import org.svip.sbom.builder.interfaces.schemas.SPDX23.SPDX23SBOMBuilder;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.SPDX23.SPDX23FileObject;
import org.svip.sbom.model.objects.SPDX23.SPDX23PackageObject;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.sbom.model.shared.Relationship;
import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbom.model.shared.util.ExternalReference;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * file: SPDX23SBOMBuilderFactoryTest.java
 * File to test SPDX23SBOMBuilderFactory and SPDX23Builder Object
 *
 * @author Kevin Laporte
 */
public class SPDX23SBOMBuilderFactoryTest {
    SPDX23SBOMBuilderFactory test_sbomBuilderFactory = new SPDX23SBOMBuilderFactory();
    SPDX23FileBuilder test_fileBuilder = new SPDX23FileBuilder();
    SPDX23PackageBuilder test_packageBuilder = new SPDX23PackageBuilder();
    SPDX23SBOMBuilder test_SPDX23SBOMBuilder = test_sbomBuilderFactory.createBuilder();
    SPDX23SBOM test_SPDX23SBOM = test_SPDX23SBOMBuilder.buildSPDX23SBOM();

    String test_format = "SPDX";

    String test_name = "Test SPDX SBOM";

    String test_uid = "749d4a17-1074-4b78-a968-fafc67378f75";

    String test_version = "1.1";

    String test_specVersion = "2.3";

    String test_license1 = "PSFL";

    String test_license2 = "GPL";

    CreationData test_creationData = new CreationData();

    String test_documentComment = "This is a test comment.";

    Component test_rootComponent;

    Set<Component> test_components = new HashSet<Component>();

    @Test
    void getFormat_is_test_format_when_setFormat_is_used_test() {
        test_SPDX23SBOMBuilder.setFormat(test_format);
        test_SPDX23SBOM = test_SPDX23SBOMBuilder.buildSPDX23SBOM();

        assertEquals(test_format, test_SPDX23SBOM.getFormat());
    }
    
    @Test
    void getName_is_test_name_when_setName_is_used_test(){
        test_SPDX23SBOMBuilder.setName(test_name);
        test_SPDX23SBOM = test_SPDX23SBOMBuilder.buildSPDX23SBOM();

        assertEquals(test_name, test_SPDX23SBOM.getName());
    }

    @Test
    void getUID_is_test_uid_when_setUID_is_used_test(){
        test_SPDX23SBOMBuilder.setUID(test_uid);
        test_SPDX23SBOM = test_SPDX23SBOMBuilder.buildSPDX23SBOM();

        assertEquals(test_uid, test_SPDX23SBOM.getUID());
    }

    @Test
    void getVersion_is_test_version_when_setVersion_is_used_test(){
        test_SPDX23SBOMBuilder.setVersion(test_version);
        test_SPDX23SBOM = test_SPDX23SBOMBuilder.buildSPDX23SBOM();

        assertEquals(test_version, test_SPDX23SBOM.getVersion());
    }

    @Test
    void getSpecVersion_is_test_specVersion_when_setSpecVersion_is_used_test(){
        test_SPDX23SBOMBuilder.setSpecVersion(test_specVersion);
        test_SPDX23SBOM = test_SPDX23SBOMBuilder.buildSPDX23SBOM();

        assertEquals(test_specVersion, test_SPDX23SBOM.getSpecVersion());
    }

    @Test
    void getLicenses_is_test_set_when_addLicense_is_used_test(){
        test_SPDX23SBOMBuilder.addLicense(test_license1);
        test_SPDX23SBOMBuilder.addLicense(test_license2);
        test_SPDX23SBOM = test_SPDX23SBOMBuilder.buildSPDX23SBOM();

        HashSet<String> test_Set = new HashSet<>();
        test_Set.add(test_license1);
        test_Set.add(test_license2);

        assertEquals(test_Set, test_SPDX23SBOM.getLicenses());
    }

    @Test
    void getCreationData_is_test_creationData_when_setCreationData_is_used_test(){
        test_creationData.setCreationTime("17:32");
        test_creationData.setCreatorComment("This was created as a test.");

        test_SPDX23SBOMBuilder.setCreationData(test_creationData);
        test_SPDX23SBOM = test_SPDX23SBOMBuilder.buildSPDX23SBOM();

        assertEquals(test_creationData, test_SPDX23SBOM.getCreationData());
    }

    @Test
    void getDocumentComment_is_test_documentComment_when_setDocumentComment_is_used_test(){
        test_SPDX23SBOMBuilder.setDocumentComment(test_documentComment);
        test_SPDX23SBOM = test_SPDX23SBOMBuilder.buildSPDX23SBOM();

        assertEquals(test_documentComment, test_SPDX23SBOM.getDocumentComment());
    }
    @Test
    void getRootComponent_is_test_rootComponent_when_setRootSPDXComponentObject_is_used_test(){
        test_rootComponent = new SPDX23PackageObject("SPDX", null, "Tester", "Test Package",null,null,null,null,null,null,null,null,null, null, null, null,null,null,null,null,null,null,null,null);
        test_SPDX23SBOMBuilder.setRootComponent(test_rootComponent);
        test_SPDX23SBOM = test_SPDX23SBOMBuilder.buildSPDX23SBOM();

        assertEquals(test_rootComponent, test_SPDX23SBOM.getRootComponent());
    }

    @Test
    void getComponents_is_test_components_when_addSPDXComponentObject_is_used_test(){
        SPDX23PackageObject test_componentA = new SPDX23PackageObject("SPDX Package", null, "Tester One", "Test Component A",null,null,null,null,null,null,null,null,null, null, null, null,null,null,null,null,null,null,null,null);
        SPDX23FileObject test_componentB = new SPDX23FileObject("SPDX File", null, "Tester 2", "Test Component B",null,null,null,null,null,null);
        test_SPDX23SBOMBuilder.addComponent(test_componentA);
        test_SPDX23SBOMBuilder.addComponent(test_componentB);
        test_SPDX23SBOMBuilder.addSPDX23Component(test_componentA);
        test_SPDX23SBOM = test_SPDX23SBOMBuilder.buildSPDX23SBOM();

        test_components.add(test_componentA);
        test_components.add(test_componentA);
        test_components.add(test_componentB);

        assertEquals(test_components, test_SPDX23SBOM.getComponents());
    }

    @Test
    void getRelationships_contains_test_relationships_when_addRelationship_is_used_test(){
        Relationship test_relationship = new Relationship("001", "dependant");
        test_SPDX23SBOMBuilder.addRelationship("test_component", test_relationship);

        test_SPDX23SBOM = test_SPDX23SBOMBuilder.buildSPDX23SBOM();

        assertTrue(test_SPDX23SBOM.getRelationships().containsKey("test_component"));
    }

    @Test
    void getExternalReferences_is_test_externalRefs_when_addExternalReference_is_used_test(){
        HashSet<ExternalReference> test_externalRefs = new HashSet<ExternalReference>();
        ExternalReference test_externalRef = new ExternalReference("really cool url", "CPE");

        test_SPDX23SBOMBuilder.addExternalReference(test_externalRef);
        test_externalRefs.add(test_externalRef);

        test_SPDX23SBOM = test_SPDX23SBOMBuilder.buildSPDX23SBOM();

        assertEquals(test_externalRefs, test_SPDX23SBOM.getExternalReferences());
    }

    @Test
    void getSPDXLicenseListVersion_is_v143_when_setSPDXLicenseListVersion_is_used_test(){
        test_SPDX23SBOMBuilder.setSPDXLicenseListVersion("v142");
        test_SPDX23SBOM = test_SPDX23SBOMBuilder.buildSPDX23SBOM();

        assertEquals("v142", test_SPDX23SBOM.getSPDXLicenseListVersion());
    }

    @Test
    void getName_is_test_name_when_SBOM_is_built_with_Build_method_test(){
        test_SPDX23SBOMBuilder.setName(test_name);
        SBOM test_SBOM = test_SPDX23SBOMBuilder.Build();

        assertEquals(test_name, test_SBOM.getName());
    }
}
