package org.svip.sbom.model.objects.CycloneDX14;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.svip.sbom.builder.objects.schemas.CDX14.CDX14PackageBuilder;
import org.svip.sbom.factory.objects.CycloneDX14.CDX14PackageBuilderFactory;
import org.svip.sbom.model.interfaces.schemas.CycloneDX14.CDX14Package;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.shared.util.Description;
import org.svip.sbom.model.shared.util.ExternalReference;
import org.svip.sbom.model.shared.util.LicenseCollection;
import org.svip.sbomanalysis.comparison.conflicts.Conflict;
import org.svip.sbomanalysis.comparison.conflicts.MismatchType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        licenseCollection.addDeclaredLicense("Control License 2");
        Organization organization = new Organization("Control Inc.", "www.control.io");
        Description description = new Description("This is the control component.");
        ExternalReference externalReferenceOne = new ExternalReference("www.refOne.com", "controlRef");
        ExternalReference externalReferenceTwo = new ExternalReference("www.ref2.com", "controlRef");

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
        packageBuilder.addProperty("Control Property 2", "2");
        controlPackage = packageBuilder.buildAndFlush();
    }

    @Test
    public void conflicts_isEmpty_equals_true_when_testPackage_equals_controlPackage_test() {
        // Build Equal Component
        LicenseCollection licenseCollection = new LicenseCollection();
        licenseCollection.addDeclaredLicense("Control License One");
        licenseCollection.addDeclaredLicense("Control License 2");
        Organization organization = new Organization("Control Inc.", "www.control.io");
        Description description = new Description("This is the control component.");
        ExternalReference externalReferenceOne = new ExternalReference("www.refOne.com", "controlRef");
        ExternalReference externalReferenceTwo = new ExternalReference("www.ref2.com", "controlRef");

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
        packageBuilder.addProperty("Control Property 2", "2");
        equalPackage = packageBuilder.buildAndFlush();

        assertTrue(controlPackage.compare(equalPackage).isEmpty());
    }

    @Test
    public void all_fields_conflict_when_unequalPackage_compared_to_controlPackage_test() {
        // Build Equal Component
        LicenseCollection licenseCollection = new LicenseCollection();
        licenseCollection.addDeclaredLicense("License One");
        licenseCollection.addDeclaredLicense("License 2");
        Organization organization = new Organization("Inc.", "www.c.io");
        Description description = new Description("This is the component.");
        ExternalReference externalReferenceOne = new ExternalReference("www.One.com", "controlRef");
        ExternalReference externalReferenceTwo = new ExternalReference("www.2.com", "controlRef");

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
        packageBuilder.addExternalReference(externalReferenceTwo);
        packageBuilder.addProperty("Property One","1");
        packageBuilder.addProperty("Property 2", "2");
        equalPackage = packageBuilder.buildAndFlush();

        List<Conflict> conflicts = controlPackage.compare(equalPackage);

        // TODO This is terribly inefficient
        for(Conflict c : conflicts)
        {
            switch(c.GetMessage())
            {
                case "Type doesn't match", "UID doesn't match",
                        "Copyright doesn't match", "Mime Type doesn't match",
                        "Scope doesn't match", "Group doesn't match"
                        -> assertEquals(MismatchType.MISC_MISMATCH, c.GetType());
                case "Name doesn't match" -> assertEquals(MismatchType.NAME_MISMATCH, c.GetType());
                case "Author doesn't match" -> assertEquals(MismatchType.AUTHOR_MISMATCH, c.GetType());
                case "License doesn't match" -> assertEquals(MismatchType.LICENSE_MISMATCH, c.GetType());
            }
        }
    }


    // TODO How do you test a generic package?
//    @Test
//    public void conflicts_isEmpty_equals_true_when_testGenericPackage_equals_controlPackage_test(){
//        SBOMPackage testGenericPackage = controlPackage;
//
//
//        List<Conflict> conflicts = controlPackage.compare(testGenericPackage);
//
//        assertTrue(conflicts.isEmpty());
//    }
}
