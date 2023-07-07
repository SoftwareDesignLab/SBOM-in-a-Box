package org.svip.builders.component;

import org.junit.jupiter.api.Test;
import org.svip.sbom.model.objects.SPDX23.SPDX23FileObject;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SPDX23FileBuilderTest {

    SPDX23FileObject test_package;
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
    }

    @Test
    void setUID() {
    }

    @Test
    void setAuthor() {
    }

    @Test
    void setName() {
    }

    @Test
    void setLicenses() {
    }

    @Test
    void setCopyright() {
    }

    @Test
    void addHash() {
    }

    @Test
    void setComment() {
    }

    @Test
    void setAttributionText() {
    }

    @Test
    void setFileNotice() {
    }

    @Test
    void build() {
    }

    @Test
    void buildAndFlush() {
    }
}