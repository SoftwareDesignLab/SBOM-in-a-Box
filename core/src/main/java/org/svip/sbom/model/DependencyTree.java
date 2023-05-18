package org.svip.sbom.model;

import java.util.*;

/**
 * File: DependencyTree.java
 * Tracks a SBOM's dependency tree
 *
 * @author Matt London
 */
public class DependencyTree {

    /**
     * Mapping of UUID to its component
     */
    private final HashMap<UUID, Component> components;

    /**
     * UUID of the head component
     */
    private UUID headComponent;

    /**
     * Construct a blank dependency tree
     */
    public DependencyTree() {
        components = new HashMap<>();
    }

    /**
     * Get all components within the hashmap and return the set
     *
     * @return Set of all components that have been added to the hashmap
     */
    public Set<Component> getAllComponents() {
        return new HashSet<>(components.values());
    }

    /**
     * Adds a component into the dependency tree
     *
     * @param parent UUID of the component parent (send null if root component)
     * @param toAdd  Component we want to add into the tree
     * @return UUID of the component that was just added, null if it was unsuccessful
     */
    public UUID addComponent(UUID parent, Component toAdd) {
        // This will be used as the UUID for toAdd
        UUID componentUUID = UUID.randomUUID();

        // Locate parent within the dependency node and attach
        if (parent == null) {
            headComponent = componentUUID;
        } else {
            // Get the parent
            Component parentComponent = this.getComponent(parent);

            if (parentComponent == null) {
                return null;
            }

            // At this point we now know that the node has a value
            parentComponent.addChild(componentUUID);

        }

        // Now we have the component in the dependency tree and it has a valid position
        // Now we need to add it to the hashmap
        components.put(componentUUID, toAdd);

        // set component UUID
        if (toAdd != null) { toAdd.setUUID(componentUUID); }

        return componentUUID;
    }

    // TODO: Docstring
    public void addComponents(UUID parent, List<? extends Component> toAdd) {
        for (Component c : toAdd) {
            UUID added = this.addComponent(parent, c);
            // TODO: Use logging system?
//            if(added != null) log(Debug.LOG_TYPE.INFO, "New Component: " + ((ParserComponent) c).toReadableString());
        }
    }

    /**
     * Search for a component by UUID and return it to the caller
     *
     * @param componentUUID UUID of component to search for (null for root component)
     * @return Component, null if not found
     */
    public Component getComponent(UUID componentUUID) {
        if (componentUUID == null) {
            return components.get(headComponent);
        }

        return components.get(componentUUID);

    }

    public UUID getHeadUUID() {
        return headComponent;
    }

    /**
     * Get a set of UUIDs of a component's children
     *
     * @param parent Parent UUID to get children of
     * @return Set of UUIDs
     */
    public Set<UUID> getChildrenUUIDs(UUID parent) {
        // Make sure the key exists
        if (!components.containsKey(parent)) {
            return null;
        }

        // Get the list of children
        return components.get(parent).getChildren();
    }

    /**
     * Get a component's children from the component's UUID
     *
     * @param parent Parent UUID to get components of
     * @return Set of children components (null if failed)
     */
    public Set<Component> getComponentChildren(UUID parent) {
        Set<UUID> childrenUUIDs = this.getChildrenUUIDs(parent);

        // Build a set of components
        Set<Component> children = new HashSet<>();

        // Loop through UUIDS and get their value
        for (UUID childUUID : childrenUUIDs) {
            children.add(components.get(childUUID));
        }

        return children;
    }

    /**
     * Check if a component exists in the dependency tree
     *
     * @param componentUUID UUID of component to check
     * @return True if component exists, false otherwise
     */
    public boolean hasComponent(UUID componentUUID) {
        return components.containsKey(componentUUID);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DependencyTree that)) return false;

        return this.toString().equals(that.toString());
    }

    @Override
    public int hashCode() {
        int result = components.hashCode();
        result = 31 * result + (headComponent != null ? headComponent.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        String componentMap = dependencyMapToString(headComponent, "  ");

        return "\nSBOM Dependency Tree Information\n" +
                "  + Component Map: \n  " + componentMap;
    }

    /**
     * Generates a string representation of the internal dependency map that does NOT depend on UUIDs. Each child is
     * indented by a fixed amount of spaces to allow comparing two dependency trees without regard to UUIDs.
     *
     * @param component The root component of the dependency map to generate a string from.
     * @param indent The indentation of each child.
     * @return A string representation of the internal dependency map.
     */
    private String dependencyMapToString(UUID component, String indent) {
        Component depComponent = getComponent(component);
        StringBuilder out = new StringBuilder(indent + "+ ");

        // All fields in this string will be used to compare
        out.append(depComponent.getName()).append("\n"); // TODO add more comparison fields

        for(UUID child : getChildrenUUIDs(component)) {
            out.append(dependencyMapToString(child, indent + "  "));
        }

        return out.toString();
    }
}
