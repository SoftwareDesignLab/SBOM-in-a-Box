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
