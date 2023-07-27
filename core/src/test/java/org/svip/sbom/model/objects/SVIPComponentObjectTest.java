package org.svip.sbom.model.objects;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.svip.sbom.builder.objects.SVIPComponentBuilder;
import org.svip.sbom.factory.objects.SVIPSBOMComponentFactory;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.objects.SVIPComponentObject;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.shared.util.Description;
import org.svip.sbom.model.shared.util.ExternalReference;
import org.svip.sbom.model.shared.util.LicenseCollection;
import org.svip.sbomanalysis.comparison.conflicts.Conflict;
import org.svip.sbomanalysis.comparison.conflicts.MismatchType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * file: SVIPComponentObjectTest.java
 * File to test the comparison of a SVIP Component Object
 *
 * @author Thomas Roman
 */
public class SVIPComponentObjectTest {
    static SVIPSBOMComponentFactory packageBuilderFactory = new SVIPSBOMComponentFactory();
    static SVIPComponentBuilder packageBuilder = packageBuilderFactory.createBuilder();
    static SVIPComponentObject controlPackage;
    static SVIPComponentObject equalPackage;

    @BeforeAll
    public static void createTargetPackage(){
        // Build Control Component
        LicenseCollection licenseCollection = new LicenseCollection();
        licenseCollection.addDeclaredLicense("Control License One");
        //licenseCollection.addDeclaredLicense("Control License 2");
        Organization organization = new Organization("Control Inc.", "www.control.io");
        Description description = new Description("This is the control component.");
        ExternalReference externalReferenceOne = new ExternalReference("url","www.refOne.com", "controlRef");
        //ExternalReference externalReferenceTwo = new ExternalReference("url","www.ref2.com", "controlRef");

        packageBuilder.setType("SVIP");
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

        packageBuilder.setDownloadLocation("Control Download");
        packageBuilder.setFileName("Control File");
        packageBuilder.setFilesAnalyzed(false);
        packageBuilder.setHomePage("Control HomePage");
        packageBuilder.setSourceInfo("Control Info");
        packageBuilder.setReleaseDate("Control Release Date");
        packageBuilder.setBuildDate("Control Build Date");
        packageBuilder.setValidUntilDate("Control Valid Until");
        packageBuilder.setMimeType("Control Mime");
        packageBuilder.setPublisher("Control Publisher");
        packageBuilder.setScope("Control Scope");
        packageBuilder.setGroup("Control Group");
        packageBuilder.addExternalReference(externalReferenceOne);
        //packageBuilder.addExternalReference(externalReferenceTwo);
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
        //ExternalReference externalReferenceTwo = new ExternalReference("url","www.ref2.com", "controlRef");

        packageBuilder.setType("SVIP");
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

        packageBuilder.setDownloadLocation("Control Download");
        packageBuilder.setFileName("Control File");
        packageBuilder.setFilesAnalyzed(false);
        packageBuilder.setHomePage("Control HomePage");
        packageBuilder.setSourceInfo("Control Info");
        packageBuilder.setReleaseDate("Control Release Date");
        packageBuilder.setBuildDate("Control Build Date");
        packageBuilder.setValidUntilDate("Control Valid Until");
        packageBuilder.setMimeType("Control Mime");
        packageBuilder.setPublisher("Control Publisher");
        packageBuilder.setScope("Control Scope");
        packageBuilder.setGroup("Control Group");
        packageBuilder.addExternalReference(externalReferenceOne);
        //packageBuilder.addExternalReference(externalReferenceTwo); TODO ConflictFactory doesn't seem to be properly checking sets
        packageBuilder.addProperty("Control Property One","1");
        //packageBuilder.addProperty("Control Property 2", "2");
        equalPackage = packageBuilder.buildAndFlush();

        // Check there are no conflicts
        Component component = equalPackage;
        assertTrue(controlPackage.compare(component).isEmpty());
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

        packageBuilder.setDownloadLocation("Download");
        packageBuilder.setFileName("File");
        packageBuilder.setFilesAnalyzed(true);
        packageBuilder.setHomePage("HomePage");
        packageBuilder.setSourceInfo("Info");
        packageBuilder.setReleaseDate("Release Date");
        packageBuilder.setBuildDate("Build Date");
        packageBuilder.setValidUntilDate("Valid Until");
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

            switch(c.GetMessage())
            {
                case "Type doesn't match", "UID doesn't match",
                        "Copyright doesn't match", "Mime Type doesn't match",
                        "Scope doesn't match", "Group doesn't match", "Download Location doesn't match",
                            "File Name doesn't match", "Files Analyzed doesn't match",
                            "Home Page doesn't match", "Source Info doesn't match"
                        -> assertEquals(MismatchType.MISC_MISMATCH, c.GetType());
                case "Name doesn't match" -> assertEquals(MismatchType.NAME_MISMATCH, c.GetType());
                case "Author doesn't match" -> assertEquals(MismatchType.AUTHOR_MISMATCH, c.GetType());
                case "License doesn't match" -> assertEquals(MismatchType.LICENSE_MISMATCH, c.GetType());
                case "Release Date doesn't match", "Build Date doesn't match",
                        "Valid Until Date doesn't match" -> assertEquals(MismatchType.TIMESTAMP_MISMATCH, c.GetType());
            }
        }

        // Unexpected number of results means something isn't working right
        assertEquals(conflicts.size(), 34);
    }
}
