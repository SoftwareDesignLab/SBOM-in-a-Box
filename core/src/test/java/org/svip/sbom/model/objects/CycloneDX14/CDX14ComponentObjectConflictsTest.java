package org.svip.sbom.model.objects.CycloneDX14;

import org.junit.jupiter.api.Test;
import org.svip.sbom.builder.objects.schemas.CDX14.CDX14PackageBuilder;
import org.svip.sbom.factory.objects.CycloneDX14.CDX14PackageBuilderFactory;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.schemas.CycloneDX14.CDX14Package;
import org.svip.sbom.model.shared.metadata.Organization;
import org.svip.sbom.model.shared.util.Description;
import org.svip.sbom.model.shared.util.LicenseCollection;
import org.svip.compare.conflicts.Conflict;
import org.svip.compare.conflicts.MismatchType;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * file: CDX14PackageBuilderTest
 * File to test the individual comparison methods in the CDX14 Component Object
 *
 * @author Kevin Laporte
 * @author Thomas Roman
 */
public class CDX14ComponentObjectConflictsTest {
    static CDX14PackageBuilderFactory packageBuilderFactory = new CDX14PackageBuilderFactory();
    static CDX14PackageBuilder packageBuilder = packageBuilderFactory.createBuilder();
    static CDX14Package controlPackage = packageBuilder.buildAndFlush();
    static Component conflictPackage;

    @Test
    public void Type_is_Conflicting_between_testPackage_and_controlPackage_test()
    {
        packageBuilder.setType("control");
        controlPackage = packageBuilder.buildAndFlush();
        packageBuilder.setType("Type");
        conflictPackage = packageBuilder.buildAndFlush();

        List<Conflict> conflictList = controlPackage.compare(conflictPackage);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.MISC_MISMATCH, conflict.getType());
        assertEquals("Type doesn't match", conflict.getMessage());
    }

    @Test
    public void UID_is_conflicting_between_testPackage_and_controlPackage_test(){
        packageBuilder.setUID("control");
        controlPackage = packageBuilder.buildAndFlush();
        packageBuilder.setUID("123");
        conflictPackage = packageBuilder.buildAndFlush();

        List<Conflict> conflictList = controlPackage.compare(conflictPackage);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.MISC_MISMATCH, conflict.getType());
        assertEquals("UID doesn't match", conflict.getMessage());
    }

    @Test
    public void name_is_conflicting_between_testPackage_and_controlPackage_test(){
        packageBuilder.setName("control");
        controlPackage = packageBuilder.buildAndFlush();
        packageBuilder.setName("name");
        conflictPackage = packageBuilder.buildAndFlush();

        List<Conflict> conflictList = controlPackage.compare(conflictPackage);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.NAME_MISMATCH, conflict.getType());
        assertEquals("Name doesn't match", conflict.getMessage());
    }

    @Test
    public void author_is_conflicting_between_testPackage_and_controlPackage_test(){
        packageBuilder.setAuthor("control");
        controlPackage = packageBuilder.buildAndFlush();
        packageBuilder.setAuthor("author");
        conflictPackage = packageBuilder.buildAndFlush();

        List<Conflict> conflictList = controlPackage.compare(conflictPackage);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.AUTHOR_MISMATCH, conflict.getType());
        assertEquals("Author doesn't match", conflict.getMessage());
    }

    // TODO This is still breaking due to casting
    @Test
    public void license_is_conflicting_between_testPackage_and_controlPackage_test(){
        LicenseCollection licenseCollection = new LicenseCollection();
        licenseCollection.addDeclaredLicense("control license");
        packageBuilder.setLicenses(licenseCollection);
        controlPackage = packageBuilder.buildAndFlush();
        LicenseCollection licenseCollectionTwo = new LicenseCollection();
        licenseCollectionTwo.addDeclaredLicense("license");
        packageBuilder.setLicenses(licenseCollectionTwo);
        conflictPackage = packageBuilder.buildAndFlush();

        List<Conflict> conflictList = controlPackage.compare(conflictPackage);
        boolean c1 = false;
        boolean c2 = false;

        assertEquals(2, conflictList.size());

        for(Conflict c : conflictList)
        {
            if (c.getType() == MismatchType.MISSING && Objects.equals(c.getMessage(), "License is missing")) {
                if(Objects.equals(c.getTarget(), "control license") && c.getOther() == null)
                    c1 = true;
                else if(c.getTarget() == null && Objects.equals(c.getOther(), "license"))
                    c2 = true;
            }
        }

        assertTrue(c1);
        assertTrue(c2);
    }

    @Test
    public void copyright_is_conflicting_between_testPackage_and_controlPackage_test(){
        packageBuilder.setCopyright("control");
        controlPackage = packageBuilder.buildAndFlush();
        packageBuilder.setCopyright("copyright");
        conflictPackage = packageBuilder.buildAndFlush();

        List<Conflict> conflictList = controlPackage.compare(conflictPackage);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.MISC_MISMATCH, conflict.getType());
        assertEquals("Copyright doesn't match", conflict.getMessage());
    }

    // TODO Component Hash comparison doesn't return intuitive information...
    @Test
    public void componentHash_is_conflicting_between_testPackage_and_controlPackage_test(){
        packageBuilder.addHash("SHA1", "control");
        controlPackage = packageBuilder.buildAndFlush();
        packageBuilder.addHash("SHA2", "hash");
        conflictPackage = packageBuilder.buildAndFlush();

        List<Conflict> conflictList = controlPackage.compare(conflictPackage);
        boolean c1 = false;
        boolean c2 = false;

        assertEquals(2, conflictList.size());

        for(Conflict c : conflictList)
        {
            if(c.getType() == MismatchType.MISSING && Objects.equals(c.getMessage(), "Component Hash is missing"))
                if(Objects.equals(c.getTarget(),"Contains Component Hash Data") && c.getOther() == null)
                    c1 = true;
                else if(c.getTarget() == null && Objects.equals(c.getOther(), "Contains Component Hash Data"))
                    c2 = true;
        }

        assertTrue(c1);
        assertTrue(c2);
    }

    @Test
    public void supplier_is_conflicting_between_testPackage_and_controlPackage_test(){
        Organization controlOrg = new Organization("Control Org.","www.control.com");
        Organization testOrg = new Organization("Test Org.", "www.test.com");
        packageBuilder.setSupplier(controlOrg);
        controlPackage = packageBuilder.buildAndFlush();
        packageBuilder.setSupplier(testOrg);
        conflictPackage = packageBuilder.buildAndFlush();

        List<Conflict> conflictList = controlPackage.compare(conflictPackage);
        boolean c1 = false;
        boolean c2 = false;

        assertEquals(2, conflictList.size());

        for(Conflict c : conflictList)
        {
            if(c.getType() == MismatchType.NAME_MISMATCH && Objects.equals(c.getMessage(), "Organization: Name doesn't match"))
                c1 = true;
            else if(c.getType() == MismatchType.MISC_MISMATCH && Objects.equals(c.getMessage(), "Organization: URL doesn't match"))
                c2 = true;
        }

        assertTrue(c1);
        assertTrue(c2);
    }

    @Test
    public void version_is_conflicting_between_testPackage_and_controlPackage_test(){
        packageBuilder.setVersion("control");
        controlPackage = packageBuilder.buildAndFlush();
        packageBuilder.setVersion("version");
        conflictPackage = packageBuilder.buildAndFlush();

        List<Conflict> conflictList = controlPackage.compare(conflictPackage);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.VERSION_MISMATCH, conflict.getType());
        assertEquals("Version doesn't match", conflict.getMessage());
    }

    @Test
    public void description_is_conflicting_between_testPackage_and_controlPackage_test(){
        Description controlDesc = new Description("control");
        Description testDesc = new Description("description");
        packageBuilder.setDescription(controlDesc);
        controlPackage = packageBuilder.buildAndFlush();
        packageBuilder.setDescription(testDesc);
        conflictPackage = packageBuilder.buildAndFlush();

        List<Conflict> conflictList = controlPackage.compare(conflictPackage);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.MISC_MISMATCH, conflict.getType());
        assertEquals("Summary doesn't match", conflict.getMessage());
    }

    @Test
    public void PURL_is_conflicting_between_testPackage_and_controlPackage_test(){
        packageBuilder.addPURL("control");
        controlPackage = packageBuilder.buildAndFlush();
        packageBuilder.addPURL("purl");
        conflictPackage = packageBuilder.buildAndFlush();

        List<Conflict> conflictList = controlPackage.compare(conflictPackage);
        boolean c1 = false;
        boolean c2 = false;

        assertEquals(2, conflictList.size());

        for(Conflict c : conflictList)
        {
            if (c.getType() == MismatchType.MISSING && Objects.equals(c.getMessage(), "PURL is missing")) {
                if(Objects.equals(c.getTarget(), "control") && c.getOther() == null)
                    c1 = true;
                else if(c.getTarget() == null && Objects.equals(c.getOther(), "purl"))
                    c2 = true;
            }
        }

        assertTrue(c1);
        assertTrue(c2);
    }

    @Test
    public void CPE_is_conflicting_between_testPackage_and_controlPackage_test(){
        packageBuilder.addCPE("control");
        controlPackage = packageBuilder.buildAndFlush();
        packageBuilder.addCPE("cpe");
        conflictPackage = packageBuilder.buildAndFlush();

        List<Conflict> conflictList = controlPackage.compare(conflictPackage);
        boolean c1 = false;
        boolean c2 = false;

        assertEquals(2, conflictList.size());

        for(Conflict c : conflictList)
        {
            if (c.getType() == MismatchType.MISSING && Objects.equals(c.getMessage(), "CPE is missing")) {
                if(Objects.equals(c.getTarget(), "control") && c.getOther() == null)
                    c1 = true;
                else if(c.getTarget() == null && Objects.equals(c.getOther(), "cpe"))
                    c2 = true;
            }
        }

        assertTrue(c1);
        assertTrue(c2);
    }

    @Test
    public void mimeType_is_conflicting_between_testPackage_and_controlPackage_test(){
        packageBuilder.setMimeType("control");
        controlPackage = packageBuilder.buildAndFlush();
        packageBuilder.setMimeType("mime type");
        conflictPackage = packageBuilder.buildAndFlush();

        List<Conflict> conflictList = controlPackage.compare(conflictPackage);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.MISC_MISMATCH, conflict.getType());
        assertEquals("Mime Type doesn't match", conflict.getMessage());
    }

    @Test
    public void publisher_is_conflicting_between_testPackage_and_controlPackage_test(){
        packageBuilder.setPublisher("control");
        controlPackage = packageBuilder.buildAndFlush();
        packageBuilder.setPublisher("publisher");
        conflictPackage = packageBuilder.buildAndFlush();

        List<Conflict> conflictList = controlPackage.compare(conflictPackage);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.PUBLISHER_MISMATCH, conflict.getType());
        assertEquals("Publisher doesn't match", conflict.getMessage());
    }

    @Test
    public void scope_is_conflicting_between_testPackage_and_controlPackage_test(){
        packageBuilder.setScope("control");
        controlPackage = packageBuilder.buildAndFlush();
        packageBuilder.setScope("scope");
        conflictPackage = packageBuilder.buildAndFlush();

        List<Conflict> conflictList = controlPackage.compare(conflictPackage);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.MISC_MISMATCH, conflict.getType());
        assertEquals("Scope doesn't match", conflict.getMessage());
    }

    @Test
    public void group_is_conflicting_between_testPackage_and_controlPackage_test(){
        packageBuilder.setGroup("control");
        controlPackage = packageBuilder.buildAndFlush();
        packageBuilder.setGroup("group");
        conflictPackage = packageBuilder.buildAndFlush();

        List<Conflict> conflictList = controlPackage.compare(conflictPackage);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.MISC_MISMATCH, conflict.getType());
        assertEquals("Group doesn't match", conflict.getMessage());
    }
}
