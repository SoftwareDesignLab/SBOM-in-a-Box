package org.svip.repair.fix;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LicenseFixesTest {

    @Test
    public void test() {
        String details = "AGPL-1.0 is an invalid license";
        String license = details.substring(0, details.indexOf("is"));
        System.out.println(license);
    }

}