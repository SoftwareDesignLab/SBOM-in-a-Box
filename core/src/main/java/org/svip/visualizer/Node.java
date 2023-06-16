package org.svip.visualizer;


import org.svip.sbom.model.Vulnerability;
import org.svip.sbomanalysis.comparison.conflicts.ComponentConflict;

import java.util.Set;

/**
 * file: Node.java
 * Node representation used in D3 JSON Objects
 *
 * @author Kevin Laporte
 */
public class Node {
    private final String name;
    private final String sbomId;
    private final String version;
    private final Set<Vulnerability> vulnerabilities;
    private final Set<ComponentConflict> conflicts;
    private final Node[] children;

    /**
     * Create new node
     *
     * @param nodeName Name of node
     * @param nodeSbomId SBOM ID
     * @param nodeVersion Version of Node
     * @param vulnerabilities Vulnerabilities of the node
     * @param conflicts Conflicts of the node
     * @param nodeChildren Array of Children
     */
    public Node(String nodeName, String nodeSbomId, String nodeVersion, Set<Vulnerability> vulnerabilities, Set<ComponentConflict> conflicts, Node[] nodeChildren) {
        this.name = nodeName;
        this.sbomId = nodeSbomId;
        this.version = nodeVersion;
        this.vulnerabilities = vulnerabilities;
        this.conflicts = conflicts;
        this.children = nodeChildren;
    }

    ///
    /// Getters
    ///

    public String getName() {
        return name;
    }

    public String getSbomId() {
        return sbomId;
    }

    public String getVersion() {
        return version;
    }

    public Node[] getChildren() {
        return children;
    }

    public Set<Vulnerability> getVulnerabilities() {
        return vulnerabilities;
    }

    public Set<ComponentConflict> getConflicts() {
        return conflicts;
    }
}
