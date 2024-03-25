/** Copyright 2021 Rochester Institute of Technology (RIT). Developed with
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
 */

package org.svip.sbom.model.objects.SPDX23;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.svip.sbom.builder.objects.schemas.SPDX23.SPDX23FileBuilder;
import org.svip.sbom.factory.objects.SPDX23.SPDX23FileBuilderFactory;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23File;
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
 * file: SPDX23FileObjectTest.java
 * File to test the comparison of a SPDX23 File Object
 *
 * @author Thomas Roman
 */
public class SPDX23FileObjectTest {
    static SPDX23FileBuilderFactory fileBuilderFactory = new SPDX23FileBuilderFactory();
    static SPDX23FileBuilder fileBuilder = fileBuilderFactory.createBuilder();
    static SPDX23File controlFile;
    static SPDX23File equalFile;

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

        fileBuilder.setType("SPDX");
        fileBuilder.setUID("123456789");
        fileBuilder.setAuthor("Control Author");
        fileBuilder.setName("Control Component");
        fileBuilder.setLicenses(licenseCollection);
        fileBuilder.setCopyright("Control Copyright");
        fileBuilder.addHash("SHA1","TestHash");
        fileBuilder.setFileNotice("Control File Notice");
        controlFile = fileBuilder.buildAndFlush();
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

        fileBuilder.setType("SPDX");
        fileBuilder.setUID("123456789");
        fileBuilder.setAuthor("Control Author");
        fileBuilder.setName("Control Component");
        fileBuilder.setLicenses(licenseCollection);
        fileBuilder.setCopyright("Control Copyright");
        fileBuilder.addHash("SHA1","TestHash");
        fileBuilder.setFileNotice("Control File Notice");
        equalFile = fileBuilder.buildAndFlush();

        // Check there are no conflicts
        Component component = equalFile;
        assertTrue(controlFile.compare(component).isEmpty());
        assertEquals(controlFile.hashCode(), component.hashCode());
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

        fileBuilder.setType("CDX");
        fileBuilder.setUID("0");
        fileBuilder.setAuthor("Author");
        fileBuilder.setName("Component");
        fileBuilder.setLicenses(licenseCollection);
        fileBuilder.setCopyright("Copyright");
        fileBuilder.addHash("1","Hash");
        fileBuilder.setFileNotice("File Notice");
        Component unequalPackage = fileBuilder.buildAndFlush();

        List<Conflict> conflicts = controlFile.compare(unequalPackage);

        // TODO This is terribly inefficient

        for(Conflict c : conflicts)
        {

            switch(c.getMessage())
            {
                case "Type doesn't match", "UID doesn't match",
                        "Copyright doesn't match", "File Notice doesn't match"
                        -> assertEquals(MismatchType.MISC_MISMATCH, c.getType());
                case "Name doesn't match" -> assertEquals(MismatchType.NAME_MISMATCH, c.getType());
                case "Author doesn't match" -> assertEquals(MismatchType.AUTHOR_MISMATCH, c.getType());
                case "License doesn't match" -> assertEquals(MismatchType.LICENSE_MISMATCH, c.getType());
            }
        }

        // Unexpected number of results means something isn't working right
        assertEquals(conflicts.size(), 10);
    }
}
