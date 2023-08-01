package org.svip.sbom.model.objects.SPDX23;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.svip.sbom.builder.objects.schemas.SPDX23.SPDX23FileBuilder;
import org.svip.sbom.factory.objects.SPDX23.SPDX23FileBuilderFactory;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.interfaces.schemas.SPDX23.SPDX23File;
import org.svip.sbom.model.shared.util.LicenseCollection;
import org.svip.sbomanalysis.comparison.conflicts.Conflict;
import org.svip.sbomanalysis.comparison.conflicts.MismatchType;
import org.svip.sbomanalysis.qualityattributes.resultfactory.Text;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * file: SPDX23FileObjectConflictsTest.java
 * File to test the individual comparison methods in the SPDX23 File Object
 *
 * @author Thomas Roman
 * @author Kevin Laporte
 */
public class SPDX23FileObjectConflictsTest {
    static SPDX23FileBuilderFactory packageBuilderFactory = new SPDX23FileBuilderFactory();
    static SPDX23FileBuilder packageBuilder = packageBuilderFactory.createBuilder();
    static SPDX23File controlPackage = packageBuilder.buildAndFlush();
    static Component conflictPackage;

    @Test
    public void Type_is_Conflicting_between_testPackage_and_controlPackage_test() throws JsonProcessingException {
        packageBuilder.setType("control");
        controlPackage = packageBuilder.buildAndFlush();
        packageBuilder.setType("Type");
        conflictPackage = packageBuilder.buildAndFlush();

        List<Conflict> conflictList = controlPackage.compare(conflictPackage);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.MISC_MISMATCH, conflict.GetType());
        assertEquals("Type doesn't match", conflict.GetMessage());
    }

    @Test
    public void UID_is_conflicting_between_testPackage_and_controlPackage_test() throws JsonProcessingException {
        packageBuilder.setUID("control");
        controlPackage = packageBuilder.buildAndFlush();
        packageBuilder.setUID("123");
        conflictPackage = packageBuilder.buildAndFlush();

        List<Conflict> conflictList = controlPackage.compare(conflictPackage);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.MISC_MISMATCH, conflict.GetType());
        assertEquals("UID doesn't match", conflict.GetMessage());
    }

    @Test
    public void name_is_conflicting_between_testPackage_and_controlPackage_test() throws JsonProcessingException {
        packageBuilder.setName("control");
        controlPackage = packageBuilder.buildAndFlush();
        packageBuilder.setName("name");
        conflictPackage = packageBuilder.buildAndFlush();

        List<Conflict> conflictList = controlPackage.compare(conflictPackage);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.NAME_MISMATCH, conflict.GetType());
        assertEquals("Name doesn't match", conflict.GetMessage());
    }

    @Test
    public void author_is_conflicting_between_testPackage_and_controlPackage_test() throws JsonProcessingException {
        packageBuilder.setAuthor("control");
        controlPackage = packageBuilder.buildAndFlush();
        packageBuilder.setAuthor("author");
        conflictPackage = packageBuilder.buildAndFlush();

        List<Conflict> conflictList = controlPackage.compare(conflictPackage);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.AUTHOR_MISMATCH, conflict.GetType());
        assertEquals("Author doesn't match", conflict.GetMessage());
    }

    @Test
    public void license_is_conflicting_between_testPackage_and_controlPackage_test() throws JsonProcessingException {
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

        // Construct Text to use for diff report conflict messages
        Text text = new Text("Conflict", "License");

        for(Conflict c : conflictList)
        {
            if (c.GetType() == MismatchType.MISSING && Objects.equals(c.GetMessage(), "License is missing")) {
                if(Objects.equals(c.GetTarget(), "control license") && Objects.equals(c.GetOther(), text.getNullItemInSetResponse()))
                    c1 = true;
                else if(Objects.equals(c.GetTarget(), text.getNullItemInSetResponse()) && Objects.equals(c.GetOther(), "license"))
                    c2 = true;
            }
        }

        assertTrue(c1);
        assertTrue(c2);
    }

    @Test
    public void copyright_is_conflicting_between_testPackage_and_controlPackage_test() throws JsonProcessingException {
        packageBuilder.setCopyright("control");
        controlPackage = packageBuilder.buildAndFlush();
        packageBuilder.setCopyright("copyright");
        conflictPackage = packageBuilder.buildAndFlush();

        List<Conflict> conflictList = controlPackage.compare(conflictPackage);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.MISC_MISMATCH, conflict.GetType());
        assertEquals("Copyright doesn't match", conflict.GetMessage());
    }

    @Test
    public void componentHash_is_conflicting_between_testPackage_and_controlPackage_test(){

    }

    @Test
    public void fileNotice_is_conflicting_between_testPackage_and_controlPackage_test() throws JsonProcessingException {
        packageBuilder.setFileNotice("control");
        controlPackage = packageBuilder.buildAndFlush();
        packageBuilder.setFileNotice("notice");
        conflictPackage = packageBuilder.buildAndFlush();

        List<Conflict> conflictList = controlPackage.compare(conflictPackage);
        Conflict conflict = conflictList.get(0);

        assertEquals(1, conflictList.size());
        assertEquals(MismatchType.MISC_MISMATCH, conflict.GetType());
        assertEquals("File Notice doesn't match", conflict.GetMessage());
    }
}
