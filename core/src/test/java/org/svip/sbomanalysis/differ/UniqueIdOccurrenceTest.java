package org.svip.sbomanalysis.differ;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.svip.sbomanalysis.differ.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * File: UniqueIdOccurrenceTest.java
 * Tests for UniqueIdOccurrence
 *
 * @author Juan Patino
 */
public class UniqueIdOccurrenceTest {

    public final static Set<Integer> testAppearances = new HashSet<>(Arrays.asList(5,2,4,5));
    public final UniqueIdOccurrence u1 = new UniqueIdOccurrence("Test", UniqueIdentifierType.CPE);
    public final UniqueIdOccurrence u1Copy = new UniqueIdOccurrence("Test", UniqueIdentifierType.CPE);
    public final UniqueIdOccurrence u2 = new UniqueIdOccurrence("Test2", UniqueIdentifierType.PURL);
    public final UniqueIdOccurrence u3 = new UniqueIdOccurrence("Test3", UniqueIdentifierType.SWID);

    @Test
    public void uidoEqualsTest() {
        u1.addAppearance(1);
        u1Copy.addAppearance(1);
        assertEquals(u1, u1Copy);
    }
    @Test
    public void uidoNotEqualsTest(){
        u1Copy.addAppearance(1);
        assertNotEquals(u1, u1Copy);
        assertNotEquals(u1, u2);
        assertNotEquals(u1, u3);
    }
    @Test
    public void uidoAppearancesTest(){
        u1.addAppearance(5);
        u1.addAppearance(2);
        u1.addAppearance(4);
        u1.addAppearance(5);
        assertEquals(u1.getAppearances(), testAppearances);
    }

}
