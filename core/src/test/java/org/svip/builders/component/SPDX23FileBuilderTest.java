package org.svip.builders.component;

import org.junit.jupiter.api.Test;
import org.svip.sbom.model.objects.SPDX23.SPDX23FileObject;
import org.svip.sbom.model.shared.util.LicenseCollection;

import java.util.HashMap;


import static org.junit.jupiter.api.Assertions.*;

/**
 * file: SPDX23FileBuilderTest.java
 * File to test SPDX23FileBuilder
 *
 * @author Matthew Morrison
 * @author Kevin Laporte
 */

class SPDX23FileBuilderTest {

    SPDX23FileBuilder test_fileBuilder = new SPDX23FileBuilder();
    SPDX23FileObject test_file;
    String test_type = "library";

    String test_uid = "749d4a17-1074-4b78-a968-fafc67378f75";

    String test_author = "Guido van Rossum";

    String test_name = "Python";

    String test_license1 = "PSFL";

    String test_license2 = "GPL";

    String test_copyright = "Test Copyright";

    String test_hash_algo = "SHA1";

    String test_hash_value = "da39a3ee5e6b4b0d3255bfef95601890afd80709";

    String test_comment = "This is a test comment";

    String test_attributionText = "All advertising materials mentioning features or use of this software" +
            "must display this test acknowledgement";

    String fileNotice = "This file is licensed under GPL";

    @Test
    void setType() {
        test_fileBuilder.setType(test_type);

        test_file = test_fileBuilder.buildAndFlush();

        assertEquals(test_type, test_file.getType());

    }

    @Test
    void setUID() {
        test_fileBuilder.setUID(test_uid);

        test_file = test_fileBuilder.buildAndFlush();

        assertEquals(test_uid, test_file.getUID());
    }

    @Test
    void setAuthor() {
        test_fileBuilder.setAuthor(test_author);

        test_file = test_fileBuilder.buildAndFlush();

        assertEquals(test_author, test_file.getAuthor());
    }

    @Test
    void setName() {
        test_fileBuilder.setName(test_name);

        test_file = test_fileBuilder.buildAndFlush();

        assertEquals(test_name, test_file.getName());
    }

    @Test
    void setLicenses() {
        LicenseCollection licenseCollection = new LicenseCollection();
        licenseCollection.addDeclaredLicense(test_license1);
        licenseCollection.addDeclaredLicense(test_license2);
        test_fileBuilder.setLicenses(licenseCollection);

        test_file = test_fileBuilder.buildAndFlush();

        assertEquals(licenseCollection, test_file.getLicenses());
    }

    @Test
    void setCopyright() {
        test_fileBuilder.setCopyright(test_copyright);

        test_file = test_fileBuilder.buildAndFlush();

        assertEquals(test_copyright, test_file.getCopyright());
    }

    @Test
    void addHash() {
        test_fileBuilder.addHash(test_hash_algo, test_hash_value);

        test_file = test_fileBuilder.buildAndFlush();

        HashMap<String, String> test_map = new HashMap<String, String>();
        test_map.put(test_hash_algo, test_hash_value);

        assertEquals(test_map, test_file.getHashes());
    }

    @Test
    void setComment() {
        test_fileBuilder.setComment(test_comment);

        test_file = test_fileBuilder.buildAndFlush();

        assertEquals(test_comment, test_file.getComment());
    }

    @Test
    void setAttributionText() {
        test_fileBuilder.setAttributionText(test_attributionText);

        test_file = test_fileBuilder.buildAndFlush();

        assertEquals(test_attributionText, test_file.getAttributionText());
    }

    @Test
    void setFileNotice() {
        test_fileBuilder.setFileNotice(fileNotice);

        test_file = test_fileBuilder.buildAndFlush();

        assertEquals(fileNotice, test_file.getFileNotice());
    }
}