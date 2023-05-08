package org.svip.sbomanalysis.comparison;

import org.svip.sbomanalysis.differ.UniqueIdOccurrence;
import org.svip.sbomanalysis.differ.UniqueIdentifierType;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * File: ComponentVersionTest.java
 * Tests for ComponentVersion
 *
 * @author Juan Patino
 */
public class ComponentVersionTest {

    public final static ComponentVersion cv1 = new ComponentVersion("Test", "v1");
    public final static ComponentVersion cv2 = new ComponentVersion("Test", "v2");
    public final static Set<Integer> testAppearances = new HashSet<>(Arrays.asList(5,2,4,5));
    public final static HashMap<String, UniqueIdOccurrence> testUIDOs =
            new HashMap<>(Map.of(
                    "cpe", new UniqueIdOccurrence("cpe", UniqueIdentifierType.CPE),
                    "purl",new UniqueIdOccurrence("purl", UniqueIdentifierType.PURL),
                    "swid", new UniqueIdOccurrence("swid", UniqueIdentifierType.SWID)));

    @Test
    public void componentVersionEqualsTest() {
        ComponentVersion cv1Copy = new ComponentVersion("Test", "v1");
        assertEquals(cv1, cv1Copy);
    }
    @Test
    public void componentVersionNotEqualsTest(){
        assertNotEquals(cv1, cv2);
    }
    @Test
    public void componentVersionAppearancesTest(){

        cv1.addAppearance(5);
        cv1.addAppearance(2);
        cv1.addAppearance(4);
        cv1.addAppearance(5);

        cv2.setAppearances(testAppearances);

        assertEquals(cv1.getAppearances(), cv2.getAppearances());

    }
    @Test
    public void uidoTest(){

        cv1.addCPE(new UniqueIdOccurrence("cpe", UniqueIdentifierType.CPE));
        cv1.addCPE(new UniqueIdOccurrence("purl", UniqueIdentifierType.PURL));
        cv1.addCPE(new UniqueIdOccurrence("swid", UniqueIdentifierType.SWID));

        cv2.setCPEs(testUIDOs);
        assertEquals(cv1.getCPEs(), cv2.getCPEs());


        cv1.addPURL(new UniqueIdOccurrence("cpe", UniqueIdentifierType.CPE));
        cv1.addPURL(new UniqueIdOccurrence("purl", UniqueIdentifierType.PURL));
        cv1.addPURL(new UniqueIdOccurrence("swid", UniqueIdentifierType.SWID));

        cv2.setPURLs(testUIDOs);
        assertEquals(cv1.getPURLs(), cv2.getPURLs());


        cv1.addSWID(new UniqueIdOccurrence("cpe", UniqueIdentifierType.CPE));
        cv1.addSWID(new UniqueIdOccurrence("purl", UniqueIdentifierType.PURL));
        cv1.addSWID(new UniqueIdOccurrence("swid", UniqueIdentifierType.SWID));

        cv2.setSWIDs(testUIDOs);
        assertEquals(cv1.getSWIDs(), cv2.getSWIDs());

    }


}
