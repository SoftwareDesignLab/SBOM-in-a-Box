package org.svip.sbomanalysis.old;

import org.svip.sbom.model.old.Component;
import org.svip.sbom.model.uids.Hash;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Class to mark a conflict between two components
 *
 * @author Matt London
 */
public class ComponentConflict {
    /** The two components that have a conflict */
    private Component componentA;
    private Component componentB;
    /** The type of conflict between the components */
    private Set<ComponentConflictType> componentConflictTypes;

    /**
     * Determine the type of conflict between the two components
     */
    private void assignConflictType() {
        // Compare the components and find the difference, then add it to the list of conflicts
        if (componentA.getName() != null && !componentA.getName().equals(componentB.getName())) {
            componentConflictTypes.add(ComponentConflictType.COMPONENT_NAME_MISMATCH);
        }
        if (componentA.getPublisher() != null && !componentA.getPublisher().equals(componentB.getPublisher())) {
            componentConflictTypes.add(ComponentConflictType.COMPONENT_PUBLISHER_MISMATCH);
        }
        if (componentA.getVersion() != null && !componentA.getVersion().equals(componentB.getVersion())) {
            componentConflictTypes.add(ComponentConflictType.COMPONENT_VERSION_MISMATCH);
        }
        if (componentA.getCpes() != null && !componentA.getCpes().equals(componentB.getCpes())) {
            // Check if one set doesn't contain all items from the other
            if (!(componentA.getCpes().containsAll(componentB.getCpes()) || componentB.getCpes().containsAll(componentA.getCpes()))) {
                componentConflictTypes.add(ComponentConflictType.COMPONENT_CPE_MISMATCH);
            }
        }
        if (componentA.getPurls() != null && !componentA.getPurls().equals(componentB.getPurls())) {
            if (!(componentA.getPurls().containsAll(componentB.getPurls()) || componentB.getPurls().containsAll(componentA.getPurls()))) {
                componentConflictTypes.add(ComponentConflictType.COMPONENT_PURL_MISMATCH);
            }
        }
        if (componentA.getSwids() != null && !componentA.getSwids().equals(componentB.getSwids())) {
            if (!(componentA.getSwids().containsAll(componentB.getSwids()) || componentB.getSwids().containsAll(componentA.getSwids()))) {
                componentConflictTypes.add(ComponentConflictType.COMPONENT_SWID_MISMATCH);
            }
        }

        /*
        if (componentA.getUniqueID() != null && !componentA.getUniqueID().equals(componentB.getUniqueID())) {
            componentConflictTypes.add(ComponentConflictType.COMPONENT_SPDXID_MISMATCH);
        }
        */

        if (componentA.getHashes() != null && !componentA.getHashes().equals(componentB.getHashes())) {
            componentConflictTypes.add(ComponentConflictType.COMPONENT_HASH_MISMATCH);
        }
        if (componentA.getLicenses() != null && !componentA.getLicenses().equals(componentB.getLicenses())) {
            componentConflictTypes.add(ComponentConflictType.COMPONENT_LICENSE_MISMATCH);
        }
        if (componentConflictTypes.isEmpty()) {
//            componentConflictTypes.add(ComponentConflictType.COMPONENT_UNKNOWN_MISMATCH);
        }

    }

    /**
     * Construct a conflict between two components
     *
     * @param componentA First component
     * @param componentB Second component
     */
    public ComponentConflict(Component componentA, Component componentB) {
        componentConflictTypes = new HashSet<>();

        // Deep copy all information
        // In the case that the conflict is the component doesn't exist at all, then a or b may be null
        if (componentA != null) {
            this.componentA = new Component();
            this.componentA.copyFrom(componentA);

            componentCleanup(this.componentA);
        }
        else {
            this.componentA = null;
            this.componentConflictTypes.add(ComponentConflictType.COMPONENT_NOT_FOUND);
        }

        if (componentB != null) {
            this.componentB = new Component();
            this.componentB.copyFrom(componentB);

            componentCleanup(this.componentB);
        }
        else {
            this.componentB = null;
            this.componentConflictTypes.add(ComponentConflictType.COMPONENT_NOT_FOUND);
        }

        // Determine the type
        if (!this.componentConflictTypes.contains(ComponentConflictType.COMPONENT_NOT_FOUND)) {
            // In this case we know both components exist
            // Now we need to determine what the conflict is
            assignConflictType();
        }
    }

    /**
     * Cleanup a component, set unknowns to null and such
     *
     * @param component Component to clean up
     */
    private void componentCleanup(Component component) {
        Set<String> emptyNames = new HashSet<>(Arrays.asList("", "Unknown", "N/A"));
        // Set unknowns to null
        if (component.getPublisher() != null && emptyNames.contains(component.getPublisher())) {
            component.setPublisher(null);
        }

        if (component.getName() != null && emptyNames.contains(component.getName())) {
            // This should never really happen, but we will guard against it
            component.setName(null);
        }

        if (component.getVersion() != null && emptyNames.contains(component.getVersion())) {
            component.setVersion(null);
        }

        // TODO figure out a better way to handle situations like this
        // Occasionally the SBOM will name a component's version with its hash
        if (component.getVersion() != null && component.getVersion().length() > 32) {
            component.setVersion(null);
        }

    }

    public Component getComponentA() {
        return componentA;
    }

    public Component getComponentB() {
        return componentB;
    }

    public Set<ComponentConflictType> getConflictTypes() {
        return componentConflictTypes;
    }

    @Override
    public String toString() {
        StringBuilder conflictString = new StringBuilder();
        // Check if we are only showing stuff that isn't in the component string
        boolean printEquals = true;

        if (componentA == null) {
            // This means the component only exists in B
            return "  - " + componentB.toString() + "\n";
        }
        else if (componentB == null) {
            // Component only exists in A
            return "  + " + componentA.toString() + "\n";
        }

        // Check publisher equivalence
        if (componentA.getPublisher() != null) {
            printEquals = componentA.getPublisher().equals(componentB.getPublisher());
        }
        else {
            printEquals = componentB.getPublisher() == null;
        }

        if (componentA.getName() != null) {
            printEquals = componentA.getName().equals(componentB.getName());
        }
        else {
            printEquals = componentB.getName() == null;
        }
        if (componentA.getVersion() != null) {
            printEquals = componentA.getVersion().equals(componentB.getVersion());
        }
        else {
            printEquals = componentB.getVersion() == null;
        }

        if (printEquals) {
            // This means we only are showing internal component differences
            // Only do this if there are conflicts
            if (componentConflictTypes.size() > 0 && componentConflictTypes.containsAll(Arrays.asList(ComponentConflictType.COMPONENT_NOT_FOUND))) {
                conflictString.append("  = ").append(componentA.toString()).append("\n");
            }
        }
        else {
            // Need to print a plus
            conflictString.append("  + ").append(componentA.toString()).append("\n");
        }

        // Print internal conflicts if they exist
        for (ComponentConflictType conflictType : componentConflictTypes) {
            // Don't show data that is being shown in the component string
            if (conflictType == ComponentConflictType.COMPONENT_NAME_MISMATCH
                    || conflictType == ComponentConflictType.COMPONENT_PUBLISHER_MISMATCH
                    || conflictType == ComponentConflictType.COMPONENT_VERSION_MISMATCH) {
                continue;
            }

            // Otherwise we need to print the conflict that occurs
            switch (conflictType) {
                case COMPONENT_CPE_MISMATCH:
                    conflictString.append("    CPE:\n");
                    // Get differences
                    Set<String> cpeA = new HashSet<>(componentA.getCpes());
                    Set<String> cpeB = new HashSet<>(componentB.getCpes());
                    cpeA.removeAll(componentB.getCpes());
                    cpeB.removeAll(componentA.getCpes());

                    for (String cpe : cpeA) {
                        conflictString.append("      + ").append(cpe).append("\n");
                    }

                    for (String cpe : cpeB) {
                        conflictString.append("      - ").append(cpe).append("\n");
                    }

                    break;
                case COMPONENT_PURL_MISMATCH:
                    conflictString.append("    PURL:\n");
                    // Get differences
                    Set<String> purlA = new HashSet<>(componentA.getPurls());
                    Set<String> purlB = new HashSet<>(componentB.getPurls());
                    purlA.removeAll(componentB.getPurls());
                    purlB.removeAll(componentA.getPurls());

                    for (String purl : purlA) {
                        conflictString.append("      + ").append(purl).append("\n");
                    }

                    for (String purl : purlB) {
                        conflictString.append("      - ").append(purl).append("\n");
                    }

                    break;
                case COMPONENT_SWID_MISMATCH:
                    conflictString.append("    SWID:\n");
                    // Get differences
                    Set<String> swidA = new HashSet<>(componentA.getSwids());
                    Set<String> swidB = new HashSet<>(componentB.getSwids());

                    swidA.removeAll(componentB.getSwids());
                    swidB.removeAll(componentA.getSwids());

                    for (String swid : swidA) {
                        conflictString.append("      + ").append(swid).append("\n");
                    }

                    for (String swid : swidB) {
                        conflictString.append("      - ").append(swid).append("\n");
                    }
                    break;
                case COMPONENT_HASH_MISMATCH:
                    conflictString.append("    Hashes:\n");
                    // Get differences
                    Set<Hash> hashA = new HashSet<>(componentA.getHashes());
                    Set<Hash> hashB = new HashSet<>(componentB.getHashes());

                    hashA.removeAll(componentB.getHashes());
                    hashB.removeAll(componentA.getHashes());

                    for (Hash hash : hashA) {
                        conflictString.append("      + ").append(hash).append("\n");
                    }

                    for (Hash hash : hashB) {
                        conflictString.append("      - ").append(hash).append("\n");
                    }
                    break;
                case COMPONENT_SPDXID_MISMATCH:
                    // TODO Skip this one for now because we need to first confirm both components are from SPDXID
//                    conflictString.append(" SPDXID: ").append(componentA.getSPDXID()).append(" vs ").append(componentB.getSPDXID());
                    break;
                case COMPONENT_LICENSE_MISMATCH:
                    conflictString.append("    License:\n");
                    // Get differences
                    Set<String> licenseA = new HashSet<>(componentA.getLicenses());
                    Set<String> licenseB = new HashSet<>(componentB.getLicenses());

                    licenseA.removeAll(componentB.getLicenses());
                    licenseB.removeAll(componentA.getLicenses());

                    for (String license : licenseA) {
                        conflictString.append("      + ").append(license).append("\n");
                    }

                    for (String license : licenseB) {
                        conflictString.append("      - ").append(license).append("\n");
                    }
                    break;
                case COMPONENT_UNKNOWN_MISMATCH:
                    conflictString.append("    Other conflicts not displayed\n");
                    break;
                default:
                    break;
            }
        }

        // Print the component B if it exists or if they are not equal
        if (componentB != null && !printEquals) {
            conflictString.append("  - ").append(componentB.toString()).append("\n");
        }

        return conflictString.toString();
    }
}
