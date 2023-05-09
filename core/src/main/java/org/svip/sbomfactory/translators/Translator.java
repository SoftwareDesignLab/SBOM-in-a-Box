package org.svip.sbomfactory.translators;

import com.google.common.collect.Multimap;
import org.cyclonedx.model.Dependency;
import org.svip.sbom.model.Component;
import org.svip.sbom.model.SBOM;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public abstract class Translator {

    // File path of the target SBOM
    String filePath;

    public Translator(String filePath) {
        this.filePath = filePath;
    }

    public static String convertContents(String filePath) {
        // Read the contents at path into a string
        String contents = null;
        try {
            contents = new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

        return contents;
    }

    public static void dependencyBuilder(Map dependencies, HashMap components, Component parent, SBOM sbom, Set<String> visited) {

        // If top component is null, return. There is nothing to process.
        if (parent == null) { return; }

        if (visited != null) {
            // Add this parent to the visited set
            visited.add(parent.getUniqueID());
        }

        // Get the parent's dependencies as a list
        String parent_id = parent.getUniqueID();
        List<Dependency> childrenID = (List<Dependency>) dependencies.get(parent_id);

        // If there are no
        if( childrenID == null ) { return; }

        // Cycle through each dependency the parent component has
        for (Dependency childID: childrenID) {
            // Retrieve the component the parent has a dependency for
            Component child = (Component) components.get(childID.getRef());

            // If component is already in the dependency tree, add it as a child to the parent
            // Else, add it to the dependency tree while setting the parent
            if(sbom.hasComponent(child.getUUID())) {
                parent.addChild(child.getUUID());
            } else {
                sbom.addComponent(parent.getUUID(), child);
            }

            if (visited == null) {
                // This means we are in the top level component
                // Pass in a new hashset instead of the visited set
                visited = new HashSet<>();
                dependencyBuilder(dependencies, components, child, sbom, new HashSet<>());
            }
            else {
                // Only explore if we haven't already visited this component
                if (!visited.contains(child.getUniqueID())) {
                    // Pass the child component as the new parent into dependencyBuilder
                    dependencyBuilder(dependencies, components, child, sbom, visited);
                }
            }
        }
    }

    public abstract void translate(String fileContents, String filePath);


}
