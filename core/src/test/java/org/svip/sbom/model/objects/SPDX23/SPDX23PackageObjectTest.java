/ **
* Copyright 2021 Rochester Institute of Technology (RIT). Developed with
* government support under contract 70RCSA22C00000008 awarded by the United
* States Department of Homeland Security for Cybersecurity and Infrastructure Security Agency.
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the “Software”), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
* /

package org.svip.sbom.model.objects.SPDX23;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.svip.sbom.builder.objects.schemas.SPDX23.SPDX23PackageBuilder;
import org.svip.sbom.factory.objects.SPDX23.SPDX23PackageBuilderFactory;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23Package;
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
 * file: SPDX23PackageObjectTest.java
 * File to test the comparison of a SPDX23 Package Object
 *
 * @author Thomas Roman
 */
public class SPDX23PackageObjectTest {
    static SPDX23PackageBuilderFactory packageBuilderFactory = new SPDX23PackageBuilderFactory();
    static SPDX23PackageBuilder packageBuilder = packageBuilderFactory.createBuilder();
    static SPDX23Package controlPackage;
    static SPDX23Package equalPackage;

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

        packageBuilder.setType("SPDX");
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

        packageBuilder.addExternalReference(externalReferenceOne);
        //packageBuilder.addExternalReference(externalReferenceTwo);
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

        packageBuilder.setType("SPDX");
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

        packageBuilder.addExternalReference(externalReferenceOne);
        //packageBuilder.addExternalReference(externalReferenceTwo); TODO ConflictFactory doesn't seem to be properly checking sets
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

        packageBuilder.setType("CDX");
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

        packageBuilder.addExternalReference(externalReferenceOne);
        //packageBuilder.addExternalReference(externalReferenceTwo);
        Component unequalPackage = packageBuilder.buildAndFlush();

        List<Conflict> conflicts = controlPackage.compare(unequalPackage);

        // TODO This is terribly inefficient

        for(Conflict c : conflicts)
        {

            switch(c.getMessage())
            {
                case "Type doesn't match", "UID doesn't match",
                        "Copyright doesn't match", "Download Location doesn't match",
                        "File Name doesn't match", "Files Analyzed doesn't match",
                        "Home Page doesn't match", "Source Info doesn't match"
                        -> assertEquals(MismatchType.MISC_MISMATCH, c.getType());
                case "Name doesn't match" -> assertEquals(MismatchType.NAME_MISMATCH, c.getType());
                case "Author doesn't match" -> assertEquals(MismatchType.AUTHOR_MISMATCH, c.getType());
                case "License doesn't match" -> assertEquals(MismatchType.LICENSE_MISMATCH, c.getType());
                case "Release Date doesn't match", "Build Date doesn't match",
                        "Valid Until Date doesn't match" -> assertEquals(MismatchType.TIMESTAMP_MISMATCH, c.getType());
            }
        }

        // Unexpected number of results means something isn't working right
        assertEquals(31, conflicts.size());
    }
}
