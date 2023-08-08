package org.svip.sbomanalysis.compare;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.svip.merge.*;
import org.svip.sbom.builder.objects.schemas.CDX14.CDX14Builder;
import org.svip.sbom.builder.objects.schemas.SPDX23.SPDX23Builder;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.CycloneDX14.CDX14ComponentObject;
import org.svip.sbom.model.objects.CycloneDX14.CDX14SBOM;
import org.svip.sbom.model.objects.SPDX23.SPDX23PackageObject;
import org.svip.sbom.model.objects.SPDX23.SPDX23SBOM;
import org.svip.sbom.model.objects.SVIPComponentObject;
import org.svip.sbom.model.objects.SVIPSBOM;
import org.svip.sbom.model.shared.Relationship;
import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.shared.util.Description;
import org.svip.sbom.model.shared.util.ExternalReference;
import org.svip.sbom.model.shared.util.LicenseCollection;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MergerTest {

    /**
     * SVIP SBOM Test Constants Set 1
     */

    private static final String SVIP_TEST_FORMAT_ONE = "svip";

    private static final String SVIP_TEST_NAME_ONE = "test_SVIP_SBOM_one";

    private static final String SVIP_TEST_UID_ONE = "2.0";

    private static final String SVIP_TEST_VERSION_ONE = "1.0";

    private static final String SVIP_TEST_SPEC_VERSION_ONE = "1.3";

    private static final Set<String> SVIP_TEST_LICENSES_ONE = Set.of("svip_license");

    private static final CreationData SVIP_CREATION_DATA_ONE = new CreationData();

    private static final String SVIP_DOCUMENT_COMMENT_ONE = "test_document_comment";

    private static SVIPComponentObject SVIP_ROOT_ONE;

    private static Set<Component> SVIP_COMPONENTS_ONE;

    private static HashMap<String, Set<Relationship>> SVIP_RELATIONSHIPS_ONE;

    private static Set<ExternalReference> SVIP_EXTRENAL_REFERENCES_ONE;

    private static final String SVIP_LICENSE_LIST_VERSION_ONE = "None.";

    /**
     * SVIP SBOM Test Constants Set 2
     */

    private static final String SVIP_TEST_FORMAT_TWO = "svip";

    private static final String SVIP_TEST_NAME_TWO = "test_SVIP_SBOM_two";

    private static final String SVIP_TEST_UID_TWO = "2.0";

    private static final String SVIP_TEST_VERSION_TWO = "1.0";

    private static final String SVIP_TEST_SPEC_VERSION_TWO = "1.4";

    private static final Set<String> SVIP_TEST_LICENSES_TWO = Set.of("svip_license");

    private static final CreationData SVIP_CREATION_DATA_TWO = new CreationData();

    private static final String SVIP_DOCUMENT_COMMENT_TWO = "test_document_comment";

    private static SVIPComponentObject SVIP_ROOT_TWO;

    private static Set<Component> SVIP_COMPONENTS_TWO;

    private static HashMap<String, Set<Relationship>> SVIP_RELATIONSHIPS_TWO;

    private static Set<ExternalReference> SVIP_EXTRENAL_REFERENCES_TWO;

    private static final String SVIP_LICENSE_LIST_VERSION_TWO = "None.";

    /**
     * Test Component Information
     */

    private LicenseCollection comp_svip_a_li = new LicenseCollection();

    private static HashMap<String, String> comp_svip_a_hash = new HashMap();

    private static Set<String> comp_svip_a_cpe = new HashSet<>(
            Arrays.asList(
                    "cpe:2.3:a:package-M:1.3.0:*:*:*:*:*:*:*",
                    "cpe:2.3:a:package-M:package-M:1.3.0:*:*:*:*:*:*:*"
            )
    );


    private static Set<String> comp_svip_a_purl = new HashSet<>(
            Arrays.asList(
                    "pkg:test/package-M@1.3.0?arch=x86_64&upstream=package-M&distro=test-1.3.0"
            )
    );

    private static Set<ExternalReference> comp_svip_a_ext = new HashSet<>(
            Arrays.asList(
                    new ExternalReference("www.test.test", "property")
            )
    );

    private static HashMap<String, Set<String>> comp_svip_a_properties = new HashMap<>(
            Collections.singletonMap("test_values", Set.of("blue_property", "another_blue_property"))
    );


    /**
     * Test Components
     */

    /** CDX Test Components **/

    // Blue
    private static HashMap<String, String> blue_cdx_hashes= new HashMap<>() {{
        put("SHA256","somerandomtestbluecdxhash");
    }};

    private static Organization blue_cdx_org = new Organization("blue_cdx_test_org", "www.blue_cdx.test");

    private static Description blue_cdx_description = new Description("a_summary_about_the_blue_cdx_component");

    private static LicenseCollection blue_cdx_licenses = new LicenseCollection();

    private static Set<ExternalReference> blue_externalReference = new HashSet<>(
            Arrays.asList(
                    new ExternalReference("www.bluecdxref.test", "blue_cdx")
            )
    );

    private static HashMap<String, Set<String>> blue_cdx_properties = new HashMap<>() {{
       put("blue_properties", Set.of("this_is_blue", "it's also cool", "but kind of outdated"));
    }};

    CDX14ComponentObject comp_cdx_blue = new CDX14ComponentObject(
            "library", "1234567890-blue-id-cdx", "blue_cdx_author", "test_component_blue_cdx",
            blue_cdx_licenses, "blue_copyright_cdx", blue_cdx_hashes, blue_cdx_org, "1.0.2",
            blue_cdx_description, Set.of("cpe2.3::test_blue_cdx_cpe"), Set.of("pkg:bluecdxpackage/blue@1.1.0"),
            "blueMimeType", "blue_cdx_publisher", "blue_cdx_scope", "blue_cdx_group",
            blue_externalReference, blue_cdx_properties
    );

    // Yellow
    private static HashMap<String, String> yellow_cdx_hashes= new HashMap<>() {{
        put("SHA256","somerandomtestyellowcdxhash");
    }};

    private static Organization yellow_cdx_org = new Organization("yellow_cdx_test_org", "www.yellow_cdx.test");

    private static Description yellow_cdx_description = new Description("a_summary_about_the_yellow_cdx_component");

    private static LicenseCollection yellow_cdx_licenses = new LicenseCollection();

    private static Set<ExternalReference> yellow_externalReference = new HashSet<>(
            Arrays.asList(
                    new ExternalReference("www.yellowcdxref.test", "yellow_cdx")
            )
    );

    private static HashMap<String, Set<String>> yellow_cdx_properties = new HashMap<>() {{
        put("yellow_properties", Set.of("this_is_yellow", "it's also really cool", "but has a large size"));
    }};

    CDX14ComponentObject comp_cdx_yellow = new CDX14ComponentObject(
            "library", "1234567890-yellow-id-cdx", "yellow_cdx_author", "test_component_yellow_cdx",
            yellow_cdx_licenses, "yellow_copyright_cdx", yellow_cdx_hashes, yellow_cdx_org, "1.0.2",
            yellow_cdx_description, Set.of("cpe2.3::test_yellow_cdx_cpe"), Set.of("pkg:yellowcdxpackage/yellow@1.1.0"),
            "yellowMimeType", "yellow_cdx_publisher", "yellow_cdx_scope", "yellow_cdx_group",
            yellow_externalReference, yellow_cdx_properties
    );


    private static HashMap<String, String> green_cdx_hashes= new HashMap<>() {{
        put("SHA256","somerandomtestbluecdxhash");
    }};

    // Green
    private static Organization green_cdx_org = new Organization("green_cdx_test_org", "www.green_cdx.test");

    private static Description green_cdx_description = new Description("a_summary_about_the_green_cdx_component");

    private static LicenseCollection green_cdx_licenses = new LicenseCollection();

    private static Set<ExternalReference> green_externalReference = new HashSet<>(
            Arrays.asList(
                    new ExternalReference("www.greencdxref.test", "green_cdx")
            )
    );

    private static HashMap<String, Set<String>> green_cdx_properties = new HashMap<>() {{
        put("green_properties", Set.of("this_is_green", "it's also cool", "but kind of slow"));
    }};

    CDX14ComponentObject comp_cdx_green = new CDX14ComponentObject(
            "library", "1234567890-green-id-cdx", "green_cdx_author", "test_component_green_cdx",
            green_cdx_licenses, "green_copyright_cdx", green_cdx_hashes, green_cdx_org, "1.0.2",
            green_cdx_description, Set.of("cpe2.3::test_green_cdx_cpe"), Set.of("pkg:greencdxpackage/green@1.1.0"),
            "greenMimeType", "green_cdx_publisher", "green_cdx_scope", "green_cdx_group",
            green_externalReference, green_cdx_properties
    );

    /** SPDX Test Components **/

    // Blue SPDX

    private static LicenseCollection blue_spdx_licenses = new LicenseCollection();

    private static HashMap<String, String> blue_spdx_hashes = new HashMap<>() {{
        put("SHA256","somerandomtestbluespdxhash");
    }};

    private static Organization blue_spdx_supplier = new Organization("blue_spdx_supplier","www.testbluespdx.com");

    private static Description blue_spdx_description = new Description("a_summary_about_the_blue_spdx_component");

    private static Set<ExternalReference> blue_spdx_externalReference = new HashSet<>(
            Arrays.asList(
                    new ExternalReference("www.bluespdxref.test", "blue_spdx")
            )
    );

    SPDX23PackageObject comp_spdx_blue = new SPDX23PackageObject(
            "library", "1234567890-blue-id-spdx", "blue_spdx_author", "test_component_blue_spdx",
            blue_spdx_licenses, "blue_copyright_spdx", blue_spdx_hashes, blue_spdx_supplier, "4.1",
            blue_spdx_description, Set.of("cpe2.3::test_blue_spdx_cpe"), Set.of("pkg:bluespdxpackage/blue@1.1.0"),
            blue_spdx_externalReference, "www.downloadbluespdx.test", "blue_spdx.txt",
            true, "42", "www.bluespdx.test", "some_blue_spdx_source_info",
            "01/01/2000", "01/01/2000", "01/01/3000", "blue_spdx_comment",
            "blue_spdx_attribution_test"
    );

    // Yellow SPDX

    private static LicenseCollection yellow_spdx_licenses = new LicenseCollection();

    private static HashMap<String, String> yellow_spdx_hashes = new HashMap<>() {{
        put("SHA256","somerandomtestyellowspdxhash");
    }};

    private static Organization yellow_spdx_supplier = new Organization("yellow_spdx_supplier","www.testyellowspdx.com");

    private static Description yellow_spdx_description = new Description("a_summary_about_the_yellow_spdx_component");

    private static Set<ExternalReference> yellow_spdx_externalReference = new HashSet<>(
            Arrays.asList(
                    new ExternalReference("www.yellowspdxref.test", "yellow_spdx")
            )
    );

    SPDX23PackageObject comp_spdx_yellow = new SPDX23PackageObject(
            "library", "1234567890-yellow-id-spdx", "yellow_spdx_author", "test_component_yellow_spdx",
            yellow_spdx_licenses, "yellow_copyright_spdx", yellow_spdx_hashes, yellow_spdx_supplier, "2.7.3",
            yellow_spdx_description, Set.of("cpe2.3::test_yellow_spdx_cpe"), Set.of("pkg:yellowspdxpackage/yellow@1.1.0"),
            yellow_spdx_externalReference, "www.downloadyellowspdx.test", "yellow_spdx.txt",
            true, "42", "www.yellowspdx.test", "some_yellow_spdx_source_info",
            "01/01/2000", "01/01/2000", "01/01/2500", "yellow_spdx_comment",
            "yellow_spdx_attribution_test"
    );

    // Green SPDX

    private static LicenseCollection green_spdx_licenses = new LicenseCollection();

    private static HashMap<String, String> green_spdx_hashes = new HashMap<>() {{
        put("SHA256","somerandomtestgreenspdxhash");
    }};

    private static Organization green_spdx_supplier = new Organization("green_spdx_supplier","www.testgreenspdx.com");

    private static Description green_spdx_description = new Description("a_summary_about_the_green_spdx_component");

    private static Set<ExternalReference> green_spdx_externalReference = new HashSet<>(
            Arrays.asList(
                    new ExternalReference("www.greenspdxref.test", "green_spdx")
            )
    );

    SPDX23PackageObject comp_spdx_green = new SPDX23PackageObject(
            "library", "1234567890-green-id-spdx", "green_spdx_author", "test_component_green_spdx",
            green_spdx_licenses, "green_copyright_spdx", green_spdx_hashes, green_spdx_supplier, "2.3",
            green_spdx_description, Set.of("cpe2.3::test_green_spdx_cpe"), Set.of("pkg:greenspdxpackage/green@1.1.0"),
            green_spdx_externalReference, "www.downloadgreenspdx.test", "green_spdx.txt",
            true, "42", "www.greenspdx.test", "some_green_spdx_source_info",
            "01/01/2000", "01/01/2000", "01/01/2750", "green_spdx_comment",
            "green_spdx_attribution_test"
    );

    /** SVIP Test Components **/

    Component comp_svip_blue = new SVIPComponentObject(
            "blue_package", "1234567890-blue-id", "blue_author",
            "test_component_blue", comp_svip_a_li,
            "blue_copyright_string", comp_svip_a_hash, new Organization("blue_supplier", "www.blue.test"),
            "2.3", new Description("some_description"), comp_svip_a_cpe, comp_svip_a_purl, comp_svip_a_ext,
            "www.downloaded.at.this.address.blue", "blue.txt", false,
            "BLUE123",  "www.downloaded.at.this.page.blue", "source_blue_info",
            "01/01/2023", "01/01/2023", "01/01/2033", "test_mime_blue",
            "test_publisher_blue", "test_scope_blue", "blue_group", comp_svip_a_properties,
            "this_is_a_file_notice_for_blue_test_component", "and_a_comment_for_blue_component",
            "attributes_for_blue"
    );

    Component comp_svip_yellow = new SVIPComponentObject(
            "yellow_package", "1234567890-yellow-id", "yellow_author",
            "test_component_yellow", comp_svip_a_li,
            "yellow_copyright_string", comp_svip_a_hash, new Organization("yellow_supplier", "www.yellow.test"),
            "2.3", new Description("yellow_description"), comp_svip_a_cpe, comp_svip_a_purl, comp_svip_a_ext,
            "www.downloaded.at.this.address.yellow", "yellow.txt", false,
            "YELLOW123",  "www.downloaded.at.this.page.yellow", "source_yellow_info",
            "01/01/2023", "01/01/2023", "01/01/2033", "test_mime_yellow",
            "test_publisher_yellow", "test_scope_yellow", "yellow_group", comp_svip_a_properties,
            "this_is_a_file_notice_for_yellow_test_component", "and_a_comment_for_yellow_component",
            "attributes_for_yellow"
    );

    Component comp_svip_green = new SVIPComponentObject(
            "yellow_green", "1234567890-green-id", "green_author",
            "test_component_green", comp_svip_a_li,
            "green_copyright_string", comp_svip_a_hash, new Organization("green_supplier", "www.green.test"),
            "3.1", new Description("green_description"), comp_svip_a_cpe, comp_svip_a_purl, comp_svip_a_ext,
            "www.downloaded.at.this.address.green", "green.txt", false,
            "GREEN123",  "www.downloaded.at.this.page.green", "source_green_info",
            "01/01/2023", "01/01/2023", "01/01/2033", "test_mime_green",
            "test_publisher_green", "test_scope_green", "green_group", comp_svip_a_properties,
            "this_is_a_file_notice_for_green_test_component", "and_a_comment_for_green_component",
            "attributes_for_green"
    );


    @Test
    public void merger_should_merge_basic_CDX_SBOMs() throws Exception {

        // SBOM One

        CDX14Builder builder_one = new CDX14Builder();

        builder_one.setFormat("CycloneDX");

        builder_one.setName("test_sbom_one");

        builder_one.setUID("urn:uuid:0000a000-0aaa-0000-00a0-0000aaa00000");

        builder_one.setVersion("1");

        builder_one.setSpecVersion("1.4");

        builder_one.addLicense("test_license");

        CreationData creationDataSBOMOne = new CreationData();

        builder_one.setCreationData(creationDataSBOMOne);

        builder_one.setDocumentComment("This is a test comment for the first SBOM");

        builder_one.setRootComponent(comp_cdx_green);

        builder_one.addComponent(comp_cdx_green);

        builder_one.addComponent(comp_cdx_yellow);

        Relationship green_to_yellow = new Relationship(comp_cdx_yellow.getUID(), "Depends on");

        builder_one.addRelationship(comp_cdx_green.getUID(), green_to_yellow);

        ExternalReference exRefSBOMOne = new ExternalReference("www.testsbom.test", "test_sbom_one");

        builder_one.addExternalReference(exRefSBOMOne);

        CDX14SBOM SBOM_one = builder_one.buildCDX14SBOM();

        // SBOM Two

        CDX14Builder builder_two = new CDX14Builder();

        builder_two.setFormat("CycloneDX");

        builder_two.setName("test_sbom_two");

        builder_two.setUID("urn:uuid:aaaa0aaa-a000-aaaa-aa0a-aaaa000aaaaa");

        builder_two.setVersion("1");

        builder_two.setSpecVersion("1.4");

        builder_two.addLicense("test_license");

        CreationData creationDataSBOMTwo = new CreationData();

        builder_two.setCreationData(creationDataSBOMTwo);

        builder_two.setDocumentComment("This is a test comment for the second SBOM");

        builder_two.setRootComponent(comp_cdx_green);

        builder_two.addComponent(comp_cdx_green);

        builder_two.addComponent(comp_cdx_blue);

        Relationship green_to_blue = new Relationship(comp_cdx_blue.getUID(), "Depends on");

        builder_two.addRelationship(comp_cdx_green.getUID(), green_to_blue);

        ExternalReference exRefSBOMTwo = new ExternalReference("www.testsbom.test", "test_sbom_two");

        builder_one.addExternalReference(exRefSBOMTwo);

        CDX14SBOM SBOM_two = builder_two.buildCDX14SBOM();

        // New merger

        Merger merger = new MergerCDX();

        // Merged SBOM Result

        SBOM result = merger.mergeSBOM(SBOM_one, SBOM_two);

        // Assertions

        assertNotNull(result);

        assertEquals("test_sbom_one", result.getName());

        assertEquals("test_component_green_cdx", result.getRootComponent().getName());

        assertEquals("urn:uuid:0000a000-0aaa-0000-00a0-0000aaa00000", result.getUID());

        assertEquals(3, result.getComponents().size());

        assertEquals(2, result.getExternalReferences().size());

    }

    @Test
    public void merger_should_merge_basic_SPDX_SBOMs() throws Exception {

        // SBOM One

        SPDX23Builder builder_one = new SPDX23Builder();

        builder_one.setFormat("SPDX");

        builder_one.setName("test_sbom_one");

        builder_one.setUID("0000a000-0aaa-0000-00a0-0000aaa00000");

        builder_one.setVersion("1");

        builder_one.setSpecVersion("2.3");

        builder_one.addLicense("test_license");

        CreationData creationDataSBOMOne = new CreationData();

        builder_one.setCreationData(creationDataSBOMOne);

        builder_one.setDocumentComment("This is a test comment for the second SBOM");

        builder_one.setRootComponent(comp_spdx_green);

        builder_one.addComponent(comp_spdx_green);

        builder_one.addComponent(comp_spdx_yellow);

        Relationship green_to_yellow = new Relationship(comp_spdx_yellow.getUID(), "Depends on");

        builder_one.addRelationship(comp_spdx_green.getUID(), green_to_yellow);

        ExternalReference exRefSBOMOne = new ExternalReference("www.testsbom.test", "test_sbom_one");

        builder_one.addExternalReference(exRefSBOMOne);

        SPDX23SBOM SBOM_one = builder_one.buildSPDX23SBOM();

        // SBOM Two

        SPDX23Builder builder_two = new SPDX23Builder();

        builder_two.setFormat("SPDX");

        builder_two.setName("test_sbom_two");

        builder_two.setUID("aaaa0aaa-a000-aaaa-aa0a-aaaa000aaaaa");

        builder_two.setVersion("1");

        builder_two.setSpecVersion("2.3");

        builder_two.addLicense("test_license");

        CreationData creationDataSBOMTwo = new CreationData();

        builder_two.setCreationData(creationDataSBOMTwo);

        builder_two.setDocumentComment("This is a test comment for the second SBOM");

        builder_two.setRootComponent(comp_spdx_green);

        builder_two.addComponent(comp_spdx_green);

        builder_two.addComponent(comp_spdx_blue);

        Relationship green_to_blue = new Relationship(comp_spdx_blue.getUID(), "Depends on");

        builder_one.addRelationship(comp_spdx_green.getUID(), green_to_blue);

        ExternalReference exRefSBOMTwo = new ExternalReference("www.testsbom.test", "test_sbom_two");

        builder_two.addExternalReference(exRefSBOMTwo);

        SPDX23SBOM SBOM_two = builder_two.buildSPDX23SBOM();

        // New Merger

        Merger merger = new MergerSPDX();

        // Merged SBOM Result

        SBOM result = merger.mergeSBOM(SBOM_one, SBOM_two);

        // Assertions

        assertNotNull(result);

        assertEquals("test_sbom_one", result.getName());

        assertEquals("test_component_green_spdx", result.getRootComponent().getName());

        assertEquals("0000a000-0aaa-0000-00a0-0000aaa00000", result.getUID());

        assertEquals(3, result.getComponents().size());

        assertEquals(2, result.getExternalReferences().size());

    }

    @Disabled("Functionality disabled for now.")
    @Test
    public void merger_should_merge_basic_SVIP_SBOMs() throws MergerException {

        Set<Component> SVIP_components_one = new HashSet<>(Arrays.asList(comp_svip_blue, comp_svip_yellow));

        SVIPSBOM SBOM_one = new SVIPSBOM(
                SVIP_TEST_FORMAT_ONE, SVIP_TEST_NAME_ONE, SVIP_TEST_VERSION_ONE, SVIP_TEST_UID_ONE,
                SVIP_TEST_SPEC_VERSION_ONE, SVIP_TEST_LICENSES_ONE,
                SVIP_CREATION_DATA_ONE, SVIP_DOCUMENT_COMMENT_ONE, SVIP_ROOT_ONE, SVIP_components_one,
                SVIP_RELATIONSHIPS_ONE, SVIP_EXTRENAL_REFERENCES_ONE, SVIP_LICENSE_LIST_VERSION_ONE
        );

        SVIPSBOM SBOM_two = new SVIPSBOM(
                SVIP_TEST_FORMAT_TWO, SVIP_TEST_NAME_TWO, SVIP_TEST_VERSION_TWO, SVIP_TEST_UID_TWO,
                SVIP_TEST_SPEC_VERSION_TWO, SVIP_TEST_LICENSES_TWO,
                SVIP_CREATION_DATA_TWO, SVIP_DOCUMENT_COMMENT_TWO, SVIP_ROOT_TWO, SVIP_COMPONENTS_TWO,
                SVIP_RELATIONSHIPS_TWO, SVIP_EXTRENAL_REFERENCES_TWO, SVIP_LICENSE_LIST_VERSION_TWO
        );

        List<SBOM> sboms = new ArrayList<>(Arrays.asList(SBOM_one, SBOM_two));

        MergerController mergerController = new MergerController();

        mergerController.mergeAll(sboms);


    }

    @Test
    public void merger_should_mergeAll_basic_CDX_SBOMs() {

        // SBOM One

        CDX14Builder builder_one = new CDX14Builder();

        builder_one.setFormat("CycloneDX");

        builder_one.setName("test_sbom_one");

        builder_one.setUID("urn:uuid:0000a000-0aaa-0000-00a0-0000aaa00000");

        builder_one.setVersion("1");

        builder_one.setSpecVersion("1.4");

        builder_one.addLicense("test_license");

        CreationData creationDataSBOMOne = new CreationData();

        builder_one.setCreationData(creationDataSBOMOne);

        builder_one.setDocumentComment("This is a test comment for the first SBOM");

        builder_one.setRootComponent(comp_cdx_green);

        builder_one.addComponent(comp_cdx_green);

        ExternalReference exRefSBOMOne = new ExternalReference("www.testsbom.test", "test_sbom_one");

        builder_one.addExternalReference(exRefSBOMOne);

        CDX14SBOM SBOM_one = builder_one.buildCDX14SBOM();

        // SBOM Two

        CDX14Builder builder_two = new CDX14Builder();

        builder_two.setFormat("CycloneDX");

        builder_two.setName("test_sbom_two");

        builder_two.setUID("urn:uuid:aaaa0aaa-a000-aaaa-aa0a-aaaa000aaaaa");

        builder_two.setVersion("1");

        builder_two.setSpecVersion("1.4");

        builder_two.addLicense("test_license");

        CreationData creationDataSBOMTwo = new CreationData();

        builder_two.setCreationData(creationDataSBOMTwo);

        builder_two.setDocumentComment("This is a test comment for the second SBOM");

        builder_two.setRootComponent(comp_cdx_yellow);

        builder_two.addComponent(comp_cdx_yellow);

        builder_two.addComponent(comp_cdx_blue);

        Relationship yellow_to_blue = new Relationship(comp_cdx_blue.getUID(), "Depends on");

        builder_two.addRelationship(comp_cdx_yellow.getUID(), yellow_to_blue);

        ExternalReference exRefSBOMTwo = new ExternalReference("www.testsbom.test", "test_sbom_two");

        builder_one.addExternalReference(exRefSBOMTwo);

        CDX14SBOM SBOM_two = builder_two.buildCDX14SBOM();

        // SBOM Three

        CDX14Builder builder_three = new CDX14Builder();

        builder_three.setFormat("CycloneDX");

        builder_three.setName("test_sbom_three");

        builder_three.setUID("urn:uuid:aaaa0000-a000-aaaa-aa0a-aaaa000000aa");

        builder_three.setVersion("1");

        builder_three.setSpecVersion("1.4");

        builder_three.addLicense("test_license");

        CreationData creationDataSBOMThree = new CreationData();

        builder_three.setCreationData(creationDataSBOMThree);

        builder_three.setDocumentComment("This is a test comment for the third SBOM");

        builder_three.setRootComponent(comp_cdx_green);

        builder_three.addComponent(comp_cdx_green);

        builder_three.addComponent(comp_cdx_blue);

        Relationship green_to_blue = new Relationship(comp_cdx_blue.getUID(), "Depends on");

        builder_three.addRelationship(comp_cdx_green.getUID(), green_to_blue);

        ExternalReference exRefSBOMThree = new ExternalReference("www.testsbom.test", "test_sbom_three");

        builder_two.addExternalReference(exRefSBOMThree);

        CDX14SBOM SBOM_three = builder_three.buildCDX14SBOM();

        // New merger and SBOM list

        MergerController mergerController = new MergerController();

        List<SBOM> SBOMs = Arrays.asList(SBOM_one, SBOM_two, SBOM_three);

        // Merged SBOM Result

        SBOM result;
        try {
                result = mergerController.mergeAll(SBOMs);
        } catch (MergerException e) {
                result = null;
                e.printStackTrace();
        }

        // Assertions

        assertNotNull(result);

        assertEquals("test_sbom_one", result.getName());

        assertEquals("test_component_green_cdx", result.getRootComponent().getName());

        assertEquals("urn:uuid:0000a000-0aaa-0000-00a0-0000aaa00000", result.getUID());

        assertEquals(3, result.getComponents().size());

        assertEquals(3, result.getExternalReferences().size());

    }

    @Test
    public void merger_should_mergeAll_basic_SPDX_SBOMs() {

        // SBOM One

        SPDX23Builder builder_one = new SPDX23Builder();

        builder_one.setFormat("SPDX");

        builder_one.setName("test_sbom_one");

        builder_one.setUID("0000a000-0aaa-0000-00a0-0000aaa00000");

        builder_one.setVersion("1");

        builder_one.setSpecVersion("2.3");

        builder_one.addLicense("test_license");

        CreationData creationDataSBOMOne = new CreationData();

        builder_one.setCreationData(creationDataSBOMOne);

        builder_one.setDocumentComment("This is a test comment for the second SBOM");

        builder_one.setRootComponent(comp_spdx_green);

        builder_one.addComponent(comp_spdx_green);

        ExternalReference exRefSBOMOne = new ExternalReference("www.testsbom.test", "test_sbom_one");

        builder_one.addExternalReference(exRefSBOMOne);

        SPDX23SBOM SBOM_one = builder_one.buildSPDX23SBOM();

        // SBOM Two

        SPDX23Builder builder_two = new SPDX23Builder();

        builder_two.setFormat("SPDX");

        builder_two.setName("test_sbom_two");

        builder_two.setUID("aaaa0aaa-a000-aaaa-aa0a-aaaa000aaaaa");

        builder_two.setVersion("1");

        builder_two.setSpecVersion("2.3");

        builder_two.addLicense("test_license");

        CreationData creationDataSBOMTwo = new CreationData();

        builder_two.setCreationData(creationDataSBOMTwo);

        builder_two.setDocumentComment("This is a test comment for the second SBOM");

        builder_two.setRootComponent(comp_spdx_yellow);

        builder_two.addComponent(comp_spdx_yellow);

        builder_two.addComponent(comp_spdx_blue);

        Relationship yellow_to_blue = new Relationship(comp_spdx_blue.getUID(), "Depends on");

        builder_one.addRelationship(comp_spdx_yellow.getUID(), yellow_to_blue);

        ExternalReference exRefSBOMTwo = new ExternalReference("www.testsbom.test", "test_sbom_two");

        builder_two.addExternalReference(exRefSBOMTwo);

        SPDX23SBOM SBOM_two = builder_two.buildSPDX23SBOM();

        // SBOM Three

        SPDX23Builder builder_three = new SPDX23Builder();

        builder_three.setFormat("SPDX");

        builder_three.setName("test_sbom_three");

        builder_three.setUID("aaaa0000-a000-aaaa-aa0a-aaaa000000aa");

        builder_three.setVersion("1");

        builder_three.setSpecVersion("2.3");

        builder_three.addLicense("test_license");

        CreationData creationDataSBOMThree = new CreationData();

        builder_three.setCreationData(creationDataSBOMThree);

        builder_three.setDocumentComment("This is a test comment for the third SBOM");

        builder_three.setRootComponent(comp_spdx_green);

        builder_three.addComponent(comp_spdx_green);

        builder_three.addComponent(comp_spdx_blue);

        Relationship green_to_blue = new Relationship(comp_spdx_blue.getUID(), "Depends on");

        builder_one.addRelationship(comp_spdx_green.getUID(), green_to_blue);

        ExternalReference exRefSBOMThree = new ExternalReference("www.testsbom.test", "test_sbom_three");

        builder_two.addExternalReference(exRefSBOMThree);

        SPDX23SBOM SBOM_three = builder_three.buildSPDX23SBOM();

        // New merger and SBOM list

        MergerController mergerController = new MergerController();

        List<SBOM> SBOMs = Arrays.asList(SBOM_one, SBOM_two, SBOM_three);

        // Merged SBOM Result

        SBOM result;
        try {
                result = mergerController.mergeAll(SBOMs);
        } catch (MergerException e) {
                result = null;
                e.printStackTrace();
        }

        // Assertions

        assertNotNull(result);

        assertEquals("test_sbom_one", result.getName());

        assertEquals("test_component_green_spdx", result.getRootComponent().getName());

        assertEquals("0000a000-0aaa-0000-00a0-0000aaa00000", result.getUID());

        assertEquals(3, result.getComponents().size());

        assertEquals(3, result.getExternalReferences().size());

    }

    @Test
    public void merger_should_merge_empty_second_CDX_SBOM() throws Exception {

        // SBOM One

        CDX14Builder builder_one = new CDX14Builder();

        builder_one.setFormat("CycloneDX");

        builder_one.setName("test_sbom_one");

        builder_one.setUID("urn:uuid:0000a000-0aaa-0000-00a0-0000aaa00000");

        builder_one.setVersion("1");

        builder_one.setSpecVersion("1.4");

        builder_one.addLicense("test_license");

        CreationData creationDataSBOMOne = new CreationData();

        builder_one.setCreationData(creationDataSBOMOne);

        builder_one.setDocumentComment("This is a test comment for the first SBOM");

        builder_one.setRootComponent(comp_cdx_green);

        builder_one.addComponent(comp_cdx_green);

        builder_one.addComponent(comp_cdx_yellow);

        Relationship green_to_yellow = new Relationship(comp_cdx_yellow.getUID(), "Depends on");

        builder_one.addRelationship(comp_cdx_green.getUID(), green_to_yellow);

        ExternalReference exRefSBOMOne = new ExternalReference("www.testsbom.test", "test_sbom_one");

        builder_one.addExternalReference(exRefSBOMOne);

        CDX14SBOM SBOM_one = builder_one.buildCDX14SBOM();

        // SBOM Two

        CDX14Builder builder_two = new CDX14Builder();

        CDX14SBOM SBOM_two = builder_two.buildCDX14SBOM();

        // New merger

        Merger merger = new MergerCDX();

        // Merged SBOM Result

        SBOM result = merger.mergeSBOM(SBOM_one, SBOM_two);

        // Assertions

        assertNotNull(result);

        assertEquals("test_sbom_one", result.getName());

        assertEquals("test_component_green_cdx", result.getRootComponent().getName());

        assertEquals("urn:uuid:0000a000-0aaa-0000-00a0-0000aaa00000", result.getUID());

        assertEquals(2, result.getComponents().size());

        assertEquals(1, result.getExternalReferences().size());

    }

    @Test
    public void merger_should_merge_empty_second_SPDX_SBOM() throws Exception {

        // SBOM One

        SPDX23Builder builder_one = new SPDX23Builder();

        builder_one.setFormat("SPDX");

        builder_one.setName("test_sbom_one");

        builder_one.setUID("0000a000-0aaa-0000-00a0-0000aaa00000");

        builder_one.setVersion("1");

        builder_one.setSpecVersion("2.3");

        builder_one.addLicense("test_license");

        CreationData creationDataSBOMOne = new CreationData();

        builder_one.setCreationData(creationDataSBOMOne);

        builder_one.setDocumentComment("This is a test comment for the second SBOM");

        builder_one.setRootComponent(comp_spdx_green);

        builder_one.addComponent(comp_spdx_green);

        builder_one.addComponent(comp_spdx_yellow);

        Relationship green_to_yellow = new Relationship(comp_spdx_yellow.getUID(), "Depends on");

        builder_one.addRelationship(comp_spdx_green.getUID(), green_to_yellow);

        ExternalReference exRefSBOMOne = new ExternalReference("www.testsbom.test", "test_sbom_one");

        builder_one.addExternalReference(exRefSBOMOne);

        SPDX23SBOM SBOM_one = builder_one.buildSPDX23SBOM();

        // SBOM Two

        SPDX23Builder builder_two = new SPDX23Builder();

        SPDX23SBOM SBOM_two = builder_two.buildSPDX23SBOM();

        // New Merger

        Merger merger = new MergerSPDX();

        // Merged SBOM Result

        SBOM result = merger.mergeSBOM(SBOM_one, SBOM_two);

        // Assertions

        assertNotNull(result);

        assertEquals("test_sbom_one", result.getName());

        assertEquals("test_component_green_spdx", result.getRootComponent().getName());

        assertEquals("0000a000-0aaa-0000-00a0-0000aaa00000", result.getUID());

        assertEquals(2, result.getComponents().size());

        assertEquals(1, result.getExternalReferences().size());

    }

    @Test
    public void merger_should_mergeAll_empty_CDX_SBOMs() {

        // SBOM One

        CDX14Builder builder_one = new CDX14Builder();

        builder_one.setFormat("CycloneDX");

        builder_one.setName("test_sbom_one");

        builder_one.setUID("urn:uuid:0000a000-0aaa-0000-00a0-0000aaa00000");

        builder_one.setVersion("1");

        builder_one.setSpecVersion("1.4");

        builder_one.addLicense("test_license");

        CreationData creationDataSBOMOne = new CreationData();

        builder_one.setCreationData(creationDataSBOMOne);

        builder_one.setDocumentComment("This is a test comment for the first SBOM");

        builder_one.setRootComponent(comp_cdx_green);

        builder_one.addComponent(comp_cdx_green);

        ExternalReference exRefSBOMOne = new ExternalReference("www.testsbom.test", "test_sbom_one");

        builder_one.addExternalReference(exRefSBOMOne);

        CDX14SBOM SBOM_one = builder_one.buildCDX14SBOM();

        // SBOM Two, format is required for mergeAll

        CDX14Builder builder_two = new CDX14Builder();

        builder_two.setFormat("CycloneDX");

        CDX14SBOM SBOM_two = builder_two.buildCDX14SBOM();

        // SBOM Three, format is required for mergeAll

        CDX14Builder builder_three = new CDX14Builder();

        builder_three.setFormat("CycloneDX");

        CDX14SBOM SBOM_three = builder_three.buildCDX14SBOM();

        // New merger and SBOM list

        MergerController mergerController = new MergerController();

        List<SBOM> SBOMs = Arrays.asList(SBOM_one, SBOM_two, SBOM_three);

        // Merged SBOM Result

        SBOM result;
        try {
                result = mergerController.mergeAll(SBOMs);
        } catch (MergerException e) {
                result = null;
                e.printStackTrace();
        }

        // Assertions

        assertNotNull(result);

        assertEquals("test_sbom_one", result.getName());

        assertEquals("test_component_green_cdx", result.getRootComponent().getName());

        assertEquals("urn:uuid:0000a000-0aaa-0000-00a0-0000aaa00000", result.getUID());

        assertEquals(1, result.getComponents().size());

        assertEquals(1, result.getExternalReferences().size());

    }

    @Test
    public void merger_should_fail_with_null_SBOM() {

        // SBOM One

        CDX14SBOM SBOM_one = null;

        // SBOM Two

        CDX14Builder builder_two = new CDX14Builder();

        CDX14SBOM SBOM_two = builder_two.buildCDX14SBOM();

        // New merger

        Merger merger = new MergerCDX();

        // Merged SBOM Result

        Assertions.assertThrows(NullPointerException.class, new Executable() {

                @Override
                public void execute() throws Throwable {
                        SBOM result = merger.mergeSBOM(SBOM_one, SBOM_two);
                }
                
        });

    }

}
