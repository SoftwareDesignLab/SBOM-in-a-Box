package org.svip.repair.fix;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FixTest {

    @Test
    public void to_string_test() {
        Fix<String> fix = new Fix<>(FixType.METADATA_BOM_REF,"old", "fixed");
        assertEquals("old -> fixed", fix.toString());
    }

}
