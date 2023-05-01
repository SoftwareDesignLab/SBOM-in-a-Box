/**
 * @file NodeFactoryTest.java
 *
 * Tests for the Node Factory class
 *
 * @author Kevin Laporte
 */

package org.svip.visualizer;
import org.svip.sbom.model.*;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

public class NodeFactoryTest {

    private final NodeFactory nodeFactory = new NodeFactory();

    /**
     * Passes Node Factory a null SBOM
     */
    @Test
    public void CreateNodeGraph_NullSBOM_Null() {
        SBOM sbom = null;

        assertNull(nodeFactory.CreateNodeGraphJSON(sbom));
    }

    /**
     * Passes Node Factory an SBOM with all null fields
     */
    @Test
    public void CreateNodeGraph_SBOMWithNullFields_NullString() {
        SBOM sbom = new SBOM(null, null, null, null, null, null, null);

        assertEquals("null",nodeFactory.CreateNodeGraphJSON(sbom));
    }

    /**
     * Passes an SBOM with an empty dependency tree
     */
    @Test
    public void CreateNodeGraph_ValidSBOMEmptyDependencyBuilt_False() {
        SBOM sbom = new SBOM(SBOMType.CYCLONE_DX, "1.4", "1", null, "2023", null, null, new DependencyTree());

        assertEquals("null",nodeFactory.CreateNodeGraphJSON(sbom));
    }

    /**
     * Passes an SBOM with only one component in the dependency tree
     */
    @Test
    public void CreateNodeGraph_ValidSBOMOnlyRootDependantBuilt_True()
    {
        SBOM sbom = new SBOM(SBOMType.CYCLONE_DX, "1.4", "1", null, "2023", null, null, new DependencyTree());
        UUID headNodeId = sbom.addComponent(null, new Component("Component A", "1.1"));

        String expectedString = "{" +
                "\"name\":\"Component A\"," +
                "\"sbomId\":\""+ headNodeId + "\"," +
                "\"version\":\"1.1\"," +
                "\"vulnerabilities\":[]," +
                "\"conflicts\":[]," +
                "\"children\":[]"+
                "}";

        assertEquals(expectedString, nodeFactory.CreateNodeGraphJSON(sbom));
    }

    /**
     * Passes an SBOM with 3 components in the dependency tree. Components have no more than one child.
     * A depends on B which depends on C.
     */
    @Test
    public void CreateNodeGraph_ValidSBOMDepth2Built_True()
    {
        SBOM sbom = new SBOM(SBOMType.CYCLONE_DX, "1.4", "1", null, "2023", null, null, new DependencyTree());
        UUID depthZeroNode = sbom.addComponent(null, new Component("Component A", "0"));
        UUID depthOneNode = sbom.addComponent(depthZeroNode, new Component("Component B", "1"));
        UUID depthTwoNode = sbom.addComponent(depthOneNode, new Component("Component C", "2"));

        String depthTwoString = "{" +
                "\"name\":\"Component C\"," +
                "\"sbomId\":\""+ depthTwoNode + "\"," +
                "\"version\":\"2\"," +
                "\"vulnerabilities\":[]," +
                "\"conflicts\":[]," +
                "\"children\":[]"+
                "}";

        String depthOneString = "{" +
                "\"name\":\"Component B\"," +
                "\"sbomId\":\""+ depthOneNode + "\"," +
                "\"version\":\"1\"," +
                "\"vulnerabilities\":[]," +
                "\"conflicts\":[]," +
                "\"children\":[" + depthTwoString + "]"+
                "}";

        String expectedString = "{" +
                "\"name\":\"Component A\"," +
                "\"sbomId\":\""+ depthZeroNode + "\"," +
                "\"version\":\"0\"," +
                "\"vulnerabilities\":[]," +
                "\"conflicts\":[]," +
                "\"children\":[" + depthOneString + "]"+
                "}";

        assertEquals(expectedString, nodeFactory.CreateNodeGraphJSON(sbom));
    }

    /**
     * Passes an SBOM with 5 components.
     * A depends on B.1 and B.2
     * B.1 and B.2 depend on C.1 and C.2 respectively
     */
    @Test
    public void CreateNodeGraph_ValidSBOMDepth2MultipleChildrenBuilt_True()
    {
        SBOM sbom = new SBOM(SBOMType.CYCLONE_DX, "1.4", "1", null, "2023", null, null, new DependencyTree());
        UUID depthZeroNode = sbom.addComponent(null, new Component("Component A", "0"));
        UUID depthOneANode = sbom.addComponent(depthZeroNode, new Component("Component B.1", "1"));
        UUID depthOneBNode = sbom.addComponent(depthZeroNode, new Component("Component B.2", "1"));
        UUID depthTwoANode = sbom.addComponent(depthOneANode, new Component("Component C.1", "2"));
        UUID depthTwoBNode = sbom.addComponent(depthOneBNode, new Component("Component C.2", "2"));

        String depthTwoAString = "{" +
                "\"name\":\"Component C.1\"," +
                "\"sbomId\":\""+ depthTwoANode + "\"," +
                "\"version\":\"2\"," +
                "\"vulnerabilities\":[]," +
                "\"conflicts\":[]," +
                "\"children\":[]"+
                "}";

        String depthTwoBString = "{" +
                "\"name\":\"Component C.2\"," +
                "\"sbomId\":\""+ depthTwoBNode + "\"," +
                "\"version\":\"2\"," +
                "\"vulnerabilities\":[]," +
                "\"conflicts\":[]," +
                "\"children\":[]"+
                "}";

        String depthOneAString = "{" +
                "\"name\":\"Component B.1\"," +
                "\"sbomId\":\""+ depthOneANode + "\"," +
                "\"version\":\"1\"," +
                "\"vulnerabilities\":[]," +
                "\"conflicts\":[]," +
                "\"children\":[" + depthTwoAString + "]"+
                "}";

        String depthOneBString = "{" +
                "\"name\":\"Component B.2\"," +
                "\"sbomId\":\""+ depthOneBNode + "\"," +
                "\"version\":\"1\"," +
                "\"vulnerabilities\":[]," +
                "\"conflicts\":[]," +
                "\"children\":[" + depthTwoBString + "]"+
                "}";

        String expectedString = "{" +
                "\"name\":\"Component A\"," +
                "\"sbomId\":\""+ depthZeroNode + "\"," +
                "\"version\":\"0\"," +
                "\"vulnerabilities\":[]," +
                "\"conflicts\":[]," +
                "\"children\":[" + depthOneAString + "," + depthOneBString + "]"+
                "}";

        String expectedStringAltOrder = "{" +
                "\"name\":\"Component A\"," +
                "\"sbomId\":\""+ depthZeroNode + "\"," +
                "\"version\":\"0\"," +
                "\"vulnerabilities\":[]," +
                "\"conflicts\":[]," +
                "\"children\":[" + depthOneBString + "," + depthOneAString + "]"+
                "}";

        String createdString = nodeFactory.CreateNodeGraphJSON(sbom);

        assertTrue(expectedString.equals(createdString) || expectedStringAltOrder.equals(createdString));
    }
}