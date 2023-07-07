package org.svip.sbomanalysis.comparison;

import org.junit.jupiter.api.Test;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.generics.SBOM;
import org.svip.sbom.model.objects.SVIPSBOM;
import org.svip.sbom.model.objects.SVIPComponentObject;

import org.svip.sbom.model.shared.Relationship;
import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.shared.util.Description;
import org.svip.sbom.model.shared.util.ExternalReference;
import org.svip.sbom.model.shared.util.LicenseCollection;

import java.util.*;

public class NewMergerTest {

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


    Component comp_svip_blue = new SVIPComponentObject(
            "blue_package", "1234567890-blue-id", "blue_author",
            "test_component_blue", comp_svip_a_li,
            "blue_copyright_string", comp_svip_a_hash, new Organization("test_supplier", "www.test.test"),
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

    /**
     * (String type, String uid, String author, String name,
     *                                LicenseCollection licenses, String copyright,
     *                                HashMap<String, String> hashes, Organization supplier,
     *                                String version, Description description, Set<String> cpes,
     *                                Set<String> purls, Set<ExternalReference> externalReferences,
     *                                String downloadLocation, String fileName, Boolean filesAnalyzed,
     *                                String verificationCode, String homePage, String sourceInfo,
     *                                String releaseDate, String builtDate, String validUntilDate,
     *                                String mimeType, String publisher, String scope, String group,
     *                                HashMap<String, Set<String>> properties, String fileNotice,
     *                                String comment, String attributionText)
     */


    @Test
    public void merger_should_merge_basic_SVIP_SBOMs() {

        SVIP_COMPONENTS_ONE.add(comp_svip_blue);
        SVIP_COMPONENTS_ONE.add(comp_svip_yellow);

        SBOM SBOM_one = new SVIPSBOM(
                SVIP_TEST_FORMAT_ONE, SVIP_TEST_NAME_ONE, SVIP_TEST_VERSION_ONE, SVIP_TEST_UID_ONE,
                SVIP_TEST_SPEC_VERSION_ONE, SVIP_TEST_LICENSES_ONE,
                SVIP_CREATION_DATA_ONE, SVIP_DOCUMENT_COMMENT_ONE, SVIP_ROOT_ONE, SVIP_COMPONENTS_ONE,
                SVIP_RELATIONSHIPS_ONE, SVIP_EXTRENAL_REFERENCES_ONE, SVIP_LICENSE_LIST_VERSION_ONE
        );

        SBOM SBOM_two = new SVIPSBOM(
                SVIP_TEST_FORMAT_TWO, SVIP_TEST_NAME_TWO, SVIP_TEST_VERSION_TWO, SVIP_TEST_UID_TWO,
                SVIP_TEST_SPEC_VERSION_TWO, SVIP_TEST_LICENSES_TWO,
                SVIP_CREATION_DATA_TWO, SVIP_DOCUMENT_COMMENT_TWO, SVIP_ROOT_TWO, SVIP_COMPONENTS_TWO,
                SVIP_RELATIONSHIPS_TWO, SVIP_EXTRENAL_REFERENCES_TWO, SVIP_LICENSE_LIST_VERSION_TWO
        );

        List<SBOM> sboms = new ArrayList<>(Arrays.asList(SBOM_one, SBOM_two));

        OldMerger merger = new OldMerger();

        //merger.merge(sboms);


    }

}
