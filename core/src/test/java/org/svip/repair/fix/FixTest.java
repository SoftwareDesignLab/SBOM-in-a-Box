package org.svip.repair.fix;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FixTest {

    private static Fix fix;

    @BeforeAll
    static void setup() {
        fix = new Fix<>("old", "fixed");
    }

    @Test
    public void getters_test() {
        assertEquals("old", fix.getOld());
        assertEquals("fixed", fix.getFixed());
    }

    @Test
    public void to_string_test() {
        assertEquals("old -> fixed", fix.toString());
    }

}
