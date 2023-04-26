package org.svip.visualizer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.svip.sbom.model.*;

import java.util.*;

/**
 * file: NodeFactory
 * Converts SBOM object into a usable JSON object for D3
 *
 * @author Kevin Laporte
 */
public class NodeFactory {

    /**
     * Takes an individual node from the componentMap and adds it to the Node Graph.
     * Will recursively create nodes for its children, creating edges for the Node Graph.
     *
     * @param masterSBOM The SBOM object to be converted into a Node Graph
     * @param currentComp The current component being added to the Node Graph
     * @param compId The UUID of the current component
     * @param visited A set of UUIDs that have already been visited to avoid cycles
     * @return Node
     */
    // Create a node using the UUID from the dependency node and information from the hashmap
    private static Node CreateNode(SBOM masterSBOM, Component currentComp, UUID compId, Set<UUID> visited) {
        if (currentComp != null)
            if (currentComp.getChildren() != null) {
                // Instantiate the list of children, recursively creating and adding those
                //      nodes to this list.
                int numChildren = currentComp.getChildren().size();
                Node[] children = new Node[numChildren];

                // Iterate through children
                Iterator<UUID> nodeIterator = currentComp.getChildren().iterator();
                if (visited != null) {
                    if (visited.contains(compId)) {
                        return new Node(currentComp.getName(), compId.toString(), currentComp.getVersion(), currentComp.getVulnerabilities(), currentComp.getConflicts(), new Node[0]);
                    }
                }
                if (visited == null) {
                    visited = new HashSet<>();
                }
                visited.add(compId);
                for (int i = 0; i < numChildren; i++) {
                    // Create child component from UUID
                    if (nodeIterator.hasNext()) {
                        UUID nextId = nodeIterator.next();

                        children[i] = CreateNode(masterSBOM, masterSBOM.getComponent(nextId), nextId, visited);
                    }
                }

                // Assemble and return the node.
                return new Node(currentComp.getName(), compId.toString(), currentComp.getVersion(), currentComp.getVulnerabilities(), currentComp.getConflicts(), children);
            }

        return null;
    }

    /**
     * Takes the formatted master SBOM file and creates a node graph of all components.
     *
     * @param masterSbom SBOM to convert from
     */
    public String CreateNodeGraphJSON(SBOM masterSbom) {
        if (masterSbom != null) {
            // HashMap<UUID, Component> componentMap, DependencyNode rootNode
            if (masterSbom.hasDependencyTree()) {
                Node nodeGraph = null;

                nodeGraph = CreateNode(masterSbom, masterSbom.getComponent(null), masterSbom.getHeadUUID(), null);

                // Export the Node Graph to a JSON file.
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    return objectMapper.writeValueAsString(nodeGraph);
                } catch (Exception e) {
                    return null;
                }
            }
        }
        return null;
    }
}
