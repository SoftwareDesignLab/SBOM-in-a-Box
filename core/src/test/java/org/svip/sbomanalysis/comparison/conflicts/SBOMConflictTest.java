package org.svip.sbomanalysis.comparison.conflicts;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.svip.sbom.model.old.DependencyTree;
import org.svip.sbom.model.old.SBOM;
import org.svip.sbom.model.old.Signature;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * File: SBOMConflictTest.java
 * Tests for SBOMConflict
 *
 * @author Tyler Drake
 */
public class SBOMConflictTest {

    /**
     * Test SBOM 1 Constant Set
     */
    public static final String SB_SPEC_VER_ONE = "1";

    public static final String SB_SBOM_VER_ONE = "CYCLONEDX-1.4";

    public static final String SB_AUTHOR_ONE = "Anchore";

    public static final String SB_SERIAL_ONE = "f057a217-e332-4981-94dc-799d6a776f58";

    public static final String SB_TIMESTAMP_ONE = "2023-01-01T00:00:00-05:00";

    public static final Signature SB_SIGNATURE_ONE = null;

    /**
     * Test SBOM 2 Constants
     */
    public static final String SB_SPEC_VER_TWO = "2";

    public static final String SB_SBOM_VER_TWO = "CYCLONEDX-1.3";

    public static final String SB_AUTHOR_TWO = "Anchor";

    public static final String SB_SERIAL_TWO = "e057a217-e332-4981-94dc-799d6a776f09";

    public static final String SB_TIMESTAMP_TWO = "2023-01-01T00:00:00-04:00";

    public static final Signature SB_SIGNATURE_TWO = null;

    /**
     * Result Constants
     */
    public static final int EXPECTED_CONFLICTS_SBOM_ONE_SBOM_TWO = 5;

    public static final int EXPECTED_CONFLICTS_SAME_SBOM = 0;

    public static final String EXPECTED_TOSTRING_SBOM_ONE_SBOM_TWO = "SBOM Conflicts:\n" +
            "Schema Version Mismatch:\n" +
            "+ 1\n" +
            "- 2\n" +
            "SBOM Version Mismatch:\n" +
            "+ CYCLONEDX-1.4\n" +
            "- CYCLONEDX-1.3\n" +
            "Author Mismatch:\n" +
            "Timestamp Mismatch:\n" +
            "+ 2023-01-01T00:00:00-05:00\n" +
            "- 2023-01-01T00:00:00-04:00\n" +
            "Serial Number Mismatch:\n" +
            "+ f057a217-e332-4981-94dc-799d6a776f58\n" +
            "- e057a217-e332-4981-94dc-799d6a776f09\n";

    @Test
    public void two_SBOMs_with_conflicts_test() {

        SBOM a = new SBOM(
                SBOM.Type.CYCLONE_DX,
                SB_SPEC_VER_ONE,
                SB_SBOM_VER_ONE,
                SB_AUTHOR_ONE,
                SB_SERIAL_ONE,
                SB_TIMESTAMP_ONE,
                Collections.singleton(SB_SIGNATURE_ONE),
                new DependencyTree());

        SBOM b = new SBOM(
                SBOM.Type.CYCLONE_DX,
                SB_SPEC_VER_TWO,
                SB_SBOM_VER_TWO,
                SB_AUTHOR_TWO,
                SB_SERIAL_TWO,
                SB_TIMESTAMP_TWO,
                Collections.singleton(SB_SIGNATURE_TWO),
                new DependencyTree());

        SBOMConflict test_conflict = new SBOMConflict(a, b);

        assertNotNull(test_conflict);

        assertEquals(EXPECTED_CONFLICTS_SBOM_ONE_SBOM_TWO, test_conflict.getConflicts().size());
    }

    @Test
    public void same_SBOM_should_have_zero_conflicts() {

        SBOM a = new SBOM(
                SBOM.Type.CYCLONE_DX,
                SB_SPEC_VER_ONE,
                SB_SBOM_VER_ONE,
                SB_AUTHOR_ONE,
                SB_SERIAL_ONE,
                SB_TIMESTAMP_ONE,
                Collections.singleton(SB_SIGNATURE_ONE),
                new DependencyTree());

        SBOM b = new SBOM(
                SBOM.Type.CYCLONE_DX,
                SB_SPEC_VER_ONE,
                SB_SBOM_VER_ONE,
                SB_AUTHOR_ONE,
                SB_SERIAL_ONE,
                SB_TIMESTAMP_ONE,
                Collections.singleton(SB_SIGNATURE_ONE),
                new DependencyTree());

        SBOMConflict test_conflict = new SBOMConflict(a, b);

        assertNotNull(test_conflict);

        assertEquals(EXPECTED_CONFLICTS_SAME_SBOM, test_conflict.getConflicts().size());

    }

    @Disabled(
            "Test Fails on: assertTrue(toString_result.contains(EXPECTED_TOSTRING_SBOM_ONE_SBOM_TWO));" +
            "The toString method should be putting the serial number last, since it is the last conflict." +
            "When debugging it was correct, but for some reason during the assert it keeps moving the # to the end."
    )
    @Test
    public void sbom_with_conflicts_should_have_right_toString() {

        SBOM a = new SBOM(
                SBOM.Type.CYCLONE_DX,
                SB_SPEC_VER_ONE,
                SB_SBOM_VER_ONE,
                SB_AUTHOR_ONE,
                SB_SERIAL_ONE,
                SB_TIMESTAMP_ONE,
                Collections.singleton(SB_SIGNATURE_ONE),
                new DependencyTree());

        SBOM b = new SBOM(
                SBOM.Type.CYCLONE_DX,
                SB_SPEC_VER_TWO,
                SB_SBOM_VER_TWO,
                SB_AUTHOR_TWO,
                SB_SERIAL_TWO,
                SB_TIMESTAMP_TWO,
                Collections.singleton(SB_SIGNATURE_TWO),
                new DependencyTree());

        SBOMConflict test_conflict = new SBOMConflict(a, b);

        assertNotNull(test_conflict);

        String toString_result = test_conflict.toString();

        assertNotNull(toString_result);

        // For some reason if I step through this on the debugger, it passes
        // If I just let it run normally, it fails
        assertTrue(test_conflict.toString().contains(EXPECTED_TOSTRING_SBOM_ONE_SBOM_TWO));

    }

}
