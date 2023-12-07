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

package org.svip.sbom.builder.objects.schemas.SPDX23;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.svip.sbom.builder.objects.schemas.SPDX23.SPDX23FileBuilder;
import org.svip.sbom.factory.objects.SPDX23.SPDX23FileBuilderFactory;
import org.svip.sbom.model.objects.SPDX23.SPDX23FileObject;
import org.svip.sbom.model.shared.util.LicenseCollection;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * file: SPDX23FileBuilderTest.java
 * File to test SPDX23FileBuilder
 *
 * @author Matthew Morrison
 * @author Kevin Laporte
 */

class SPDX23FileBuilderTest {

    SPDX23FileBuilder test_fileBuilder;
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

    @BeforeEach
    void create_test_packageBuilder(){
        SPDX23FileBuilderFactory test_SPDX23FileBuilderFactory = new SPDX23FileBuilderFactory();
        test_fileBuilder = test_SPDX23FileBuilderFactory.createBuilder();
    }

    @Test
    void setType_is_test_type_value_test() {
        test_fileBuilder.setType(test_type);

        test_file = test_fileBuilder.buildAndFlush();

        assertEquals(test_type, test_file.getType());

    }

    @Test
    void setUID_is_test_UID_value_test() {
        test_fileBuilder.setUID(test_uid);

        test_file = test_fileBuilder.buildAndFlush();

        assertEquals(test_uid, test_file.getUID());
    }

    @Test
    void setAuthor_is_test_author_value_test() {
        test_fileBuilder.setAuthor(test_author);

        test_file = test_fileBuilder.buildAndFlush();

        assertEquals(test_author, test_file.getAuthor());
    }

    @Test
    void setName_is_test_name_value_test() {
        test_fileBuilder.setName(test_name);

        test_file = test_fileBuilder.buildAndFlush();

        assertEquals(test_name, test_file.getName());
    }

    @Test
    void setLicenses_contains_test_licenses_values_test() {
        LicenseCollection licenseCollection = new LicenseCollection();
        licenseCollection.addDeclaredLicense(test_license1);
        licenseCollection.addDeclaredLicense(test_license2);
        test_fileBuilder.setLicenses(licenseCollection);

        test_file = test_fileBuilder.buildAndFlush();

        assertEquals(licenseCollection, test_file.getLicenses());
    }

    @Test
    void setCopyright_is_test_copyright_value_test() {
        test_fileBuilder.setCopyright(test_copyright);

        test_file = test_fileBuilder.buildAndFlush();

        assertEquals(test_copyright, test_file.getCopyright());
    }

    @Test
    void addHash_contains_test_hash_info_test() {
        test_fileBuilder.addHash(test_hash_algo, test_hash_value);

        test_file = test_fileBuilder.buildAndFlush();

        HashMap<String, String> test_map = new HashMap<>();
        test_map.put(test_hash_algo, test_hash_value);

        assertEquals(test_map, test_file.getHashes());
    }

    @Test
    void setComment_is_test_comment_value_test() {
        test_fileBuilder.setComment(test_comment);

        test_file = test_fileBuilder.buildAndFlush();

        assertEquals(test_comment, test_file.getComment());
    }

    @Test
    void setAttributionText_is_test_attribution_text_value_test() {
        test_fileBuilder.setAttributionText(test_attributionText);

        test_file = test_fileBuilder.buildAndFlush();

        assertEquals(test_attributionText, test_file.getAttributionText());
    }

    @Test
    void setFileNotice_is_test_file_notice_value_test() {
        test_fileBuilder.setFileNotice(fileNotice);

        test_file = test_fileBuilder.buildAndFlush();

        assertEquals(fileNotice, test_file.getFileNotice());
    }
}