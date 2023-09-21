package org.svip.sbom.model.objects.CycloneDX14;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.svip.sbom.builder.objects.schemas.CDX14.CDX14PackageBuilder;
import org.svip.sbom.factory.objects.CycloneDX14.CDX14PackageBuilderFactory;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.schemas.CycloneDX14.CDX14Package;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.shared.util.Description;
import org.svip.sbom.model.shared.util.ExternalReference;
import org.svip.sbom.model.shared.util.LicenseCollection;
import org.svip.compare.conflicts.Conflict;
import org.svip.compare.conflicts.MismatchType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * file: CDX14ComponentObjectConflictsTest.java
 * File to test the comparison of a CDX14 Component Object
 *
 * @author Kevin Laporte
 */
public class CDX14ComponentObjectTest {
    static CDX14PackageBuilderFactory packageBuilderFactory = new CDX14PackageBuilderFactory();
    static CDX14PackageBuilder packageBuilder = packageBuilderFactory.createBuilder();
    static CDX14Package controlPackage;
    static CDX14Package equalPackage;

    @BeforeAll
    public static void createTargetPackage(){
        // Build Control Component
        LicenseCollection licenseCollection = new LicenseCollection();
        licenseCollection.addDeclaredLicense("Control License One");
        //licenseCollection.addDeclaredLicense("Control License 2");
        Organization organization = new Organization("Control Inc.", "www.control.io");
        Description description = new Description("This is the control component.");
        ExternalReference externalReferenceOne = new ExternalReference("url","www.refOne.com", "controlRef");
        ExternalReference externalReferenceTwo = new ExternalReference("url","www.ref2.com", "controlRef");

        packageBuilder.setType("CDX");
        packageBuilder.setUID("123456789");
        packageBuilder.setAuthor("Control Author");
        packageBuilder.setName("Control Component");
        packageBuilder.setLicenses(licenseCollection);
        packageBuilder.setCopyright("Control Copyright");
        packageBuilder.addHash("SHA1","TestHash");
        packageBuilder.setSupplier(organization);
        packageBuilder.setVersion("1.0");
        packageBuilder.setDescription(description);
        packageBuilder.addCPE("Control CPE One");
        packageBuilder.addCPE("Control CPE 2");
        packageBuilder.addPURL("Control PURL One");
        packageBuilder.addPURL("Control PURL 2");
        packageBuilder.setMimeType("Control Mime");
        packageBuilder.setPublisher("Control Publisher");
        packageBuilder.setScope("Control Scope");
        packageBuilder.setGroup("Control Group");
        packageBuilder.addExternalReference(externalReferenceOne);
        packageBuilder.addExternalReference(externalReferenceTwo);
        packageBuilder.addProperty("Control Property One","1");
        //packageBuilder.addProperty("Control Property 2", "2");
        controlPackage = packageBuilder.buildAndFlush();
    }

    @Test
    public void conflicts_isEmpty_equals_true_when_testPackage_equals_controlPackage_test() {
        // Build Equal Component
        LicenseCollection licenseCollection = new LicenseCollection();
        licenseCollection.addDeclaredLicense("Control License One");
        //licenseCollection.addDeclaredLicense("Control License 2");
        Organization organization = new Organization("Control Inc.", "www.control.io");
        Description description = new Description("This is the control component.");
        ExternalReference externalReferenceOne = new ExternalReference("url","www.refOne.com", "controlRef");
        ExternalReference externalReferenceTwo = new ExternalReference("url","www.ref2.com", "controlRef");

        packageBuilder.setType("CDX");
        packageBuilder.setUID("123456789");
        packageBuilder.setAuthor("Control Author");
        packageBuilder.setName("Control Component");
        packageBuilder.setLicenses(licenseCollection);
        packageBuilder.setCopyright("Control Copyright");
        packageBuilder.addHash("SHA1","TestHash");
        packageBuilder.setSupplier(organization);
        packageBuilder.setVersion("1.0");
        packageBuilder.setDescription(description);
        packageBuilder.addCPE("Control CPE One");
        packageBuilder.addCPE("Control CPE 2");
        packageBuilder.addPURL("Control PURL One");
        packageBuilder.addPURL("Control PURL 2");
        packageBuilder.setMimeType("Control Mime");
        packageBuilder.setPublisher("Control Publisher");
        packageBuilder.setScope("Control Scope");
        packageBuilder.setGroup("Control Group");
        packageBuilder.addExternalReference(externalReferenceOne);
        packageBuilder.addExternalReference(externalReferenceTwo); //TODO ConflictFactory doesn't seem to be properly checking sets
        packageBuilder.addProperty("Control Property One","1");
        //packageBuilder.addProperty("Control Property 2", "2");
        equalPackage = packageBuilder.buildAndFlush();

        // Check there are no conflicts
        Component component = equalPackage;
        List<Conflict> conflicts = controlPackage.compare(component);
        assertTrue(conflicts.isEmpty());
        assertEquals(controlPackage.hashCode(), component.hashCode());
    }


    @Test
    public void all_fields_conflict_when_unequalPackage_compared_to_controlPackage_test() {
        // Build Unequal Component
        LicenseCollection licenseCollection = new LicenseCollection();
        licenseCollection.addDeclaredLicense("License One");
        //licenseCollection.addDeclaredLicense("License 2");
        Organization organization = new Organization("Inc.", "www.c.io");
        Description description = new Description("This is the component.");
        ExternalReference externalReferenceOne = new ExternalReference("url","www.One.com", "controlRef");
        //ExternalReference externalReferenceTwo = new ExternalReference("url","www.2.com", "controlRef");

        packageBuilder.setType("SPDX");
        packageBuilder.setUID("0");
        packageBuilder.setAuthor("Author");
        packageBuilder.setName("Component");
        packageBuilder.setLicenses(licenseCollection);
        packageBuilder.setCopyright("Copyright");
        packageBuilder.addHash("1","Hash");
        packageBuilder.setSupplier(organization);
        packageBuilder.setVersion("10");
        packageBuilder.setDescription(description);
        packageBuilder.addCPE("CPE One");
        packageBuilder.addCPE("CPE 2");
        packageBuilder.addPURL("PURL One");
        packageBuilder.addPURL("PURL 2");
        packageBuilder.setMimeType("Mime");
        packageBuilder.setPublisher("Publisher");
        packageBuilder.setScope("Scope");
        packageBuilder.setGroup("Group");
        packageBuilder.addExternalReference(externalReferenceOne);
        //packageBuilder.addExternalReference(externalReferenceTwo);
        packageBuilder.addProperty("Property One","1");
        //packageBuilder.addProperty("Property 2", "2");
        Component unequalPackage = packageBuilder.buildAndFlush();

        List<Conflict> conflicts = controlPackage.compare(unequalPackage);

        // TODO This is terribly inefficient

        for(Conflict c : conflicts)
        {

            switch(c.getMessage())
            {
                case "Type doesn't match", "UID doesn't match",
                        "Copyright doesn't match", "Mime Type doesn't match",
                        "Scope doesn't match", "Group doesn't match"
                        -> assertEquals(MismatchType.MISC_MISMATCH, c.getType());
                case "Name doesn't match" -> assertEquals(MismatchType.NAME_MISMATCH, c.getType());
                case "Author doesn't match" -> assertEquals(MismatchType.AUTHOR_MISMATCH, c.getType());
                case "License doesn't match" -> assertEquals(MismatchType.LICENSE_MISMATCH, c.getType());
            }
        }

        // Unexpected number of results means something isn't working right
        assertEquals( 28, conflicts.size());
        }
}
