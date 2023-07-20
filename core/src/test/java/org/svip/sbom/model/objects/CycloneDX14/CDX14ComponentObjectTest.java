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
}
