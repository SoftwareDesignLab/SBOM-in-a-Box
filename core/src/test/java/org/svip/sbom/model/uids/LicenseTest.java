package org.svip.sbom.model.uids;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LicenseTest {

    private final String LICENSE_ID = "Apache-2.0";
    private final String LICENSE_NAME = "Apache License 2.0";
    private final String LICENSE_URL = "https://www.apache.org/licenses/LICENSE-2.0";

    @Test
    public void construction_test() {
        License license = new License();
        assertNotNull(license);

        license = new License(LICENSE_ID);
        assertNotNull(license);
        assertEquals(LICENSE_ID, license.getIdentifier());

        license = new License(LICENSE_NAME);
        assertNotNull(license);
        assertEquals(LICENSE_NAME, license.getIdentifier());
    }

    @Test
    public void accessors_test() {
        License license = new License();
        license.setId(LICENSE_ID);
        license.setName(LICENSE_NAME);
        license.setUrl(List.of(LICENSE_URL));

        assertEquals(LICENSE_ID, license.getId());
        assertEquals(LICENSE_NAME, license.getName());
        assertEquals(LICENSE_URL, license.getUrl());
    }

    @Test
    public void overrides_test() {
        License license0 = new License(LICENSE_ID);
        License license1 = new License(LICENSE_NAME);
        assertFalse(license0.equals(license1));

        license0.setName(LICENSE_NAME);
        license1.setId(LICENSE_ID);
        assertTrue(license0.equals(license1));

        assertNotNull(license0.toString());
        assertNotNull(license1.toString());
    }

}
