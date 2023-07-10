package org.svip.componentfactory;

import org.junit.jupiter.api.Test;
import org.svip.builders.component.SPDX23FileBuilder;
import org.svip.sbom.model.objects.SPDX23.SPDX23FileObject;
import org.svip.sbom.model.shared.util.LicenseCollection;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * file: SPDX23FileBuilderFactoryTest.java
 * File to test SPDX23FileBuilderFactory
 *
 * @author Matthew Morrison
 */
public class SPDX23FileBuilderFactoryTest {
    SPDX23FileBuilder test_fileBuilder;

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

    SPDX23FileObject test_file;

    @Test
    void create_SPDX_file_builder_test() {
        SPDX23FileBuilderFactory spdx23FileBuilderFactory = new SPDX23FileBuilderFactory();
        test_fileBuilder = spdx23FileBuilderFactory.createBuilder();

        LicenseCollection licenseCollection = new LicenseCollection();
        licenseCollection.addDeclaredLicense(test_license1);
        licenseCollection.addDeclaredLicense(test_license2);

        HashMap<String, String> test_map = new HashMap<>();
        test_map.put(test_hash_algo, test_hash_value);

        test_fileBuilder.setType(test_type);
        test_fileBuilder.setUID(test_uid);
        test_fileBuilder.setAuthor(test_author);
        test_fileBuilder.setName(test_name);
        test_fileBuilder.setLicenses(licenseCollection);
        test_fileBuilder.setCopyright(test_copyright);
        test_fileBuilder.addHash(test_hash_algo, test_hash_value);
        test_fileBuilder.setComment(test_comment);
        test_fileBuilder.setAttributionText(test_attributionText);
        test_fileBuilder.setFileNotice(fileNotice);

        test_file = test_fileBuilder.buildAndFlush();

        assertEquals(test_type, test_file.getType());
        assertEquals(test_uid, test_file.getUID());
        assertEquals(test_author, test_file.getAuthor());
        assertEquals(test_name, test_file.getName());
        assertEquals(licenseCollection, test_file.getLicenses());
        assertEquals(test_copyright, test_file.getCopyright());
        assertEquals(test_map, test_file.getHashes());
        assertEquals(test_comment, test_file.getComment());
        assertEquals(test_attributionText, test_file.getAttributionText());
        assertEquals(fileNotice, test_file.getFileNotice());
    }
}
