package org.svip.sbomanalysis.comparison.conflicts;

import org.svip.sbom.model.Component;
import org.svip.sbom.model.DependencyTree;
import org.svip.sbom.model.SBOM;
import org.svip.sbom.model.uids.PURL;

import java.util.*;

/**
 * file: Merger.java
 * <p>
 * Merge class collects all methods associated with merging a group of SBOMs together
 *
 * @author Matt London
 */
public class Merger {
    public Merger() {

    }

    /**
     * Merge a collection of SBOMs into one main SBOM
     *
     * @param SBOMs Collection of SBOM objects to merge together
     * @return Resulting merged bom
     */
    public SBOM merge(Collection<SBOM> SBOMs) {
        // Loop through and merge into a master SBOM
        if (SBOMs.size() == 0) {
            return null;
        } else if (SBOMs.size() == 1) {
            // Return the first element
            for (SBOM sbom : SBOMs) {
                return sbom;
            }
        }

        // Now we know there is at least two SBOMs
        Iterator<SBOM> it = SBOMs.iterator();
        SBOM a = it.next();
        SBOM b = it.next();

        // Merge it into a main SBOM
        SBOM mainBom = merge(a, b);

        // Take the remaining SBOMs and merge them into the main SBOM
        while (it.hasNext()) {
            SBOM nextBom = it.next();
            mainBom = merge(mainBom, nextBom);
        }

        // Return the main bom
        return mainBom;
    }

    /**
     * Merge two sboms together and return a result bom
     *
     * @param sbomA First bom to merge
     * @param sbomB Second bom to merge
     * @return Merged SBOM
     */
    private SBOM merge(SBOM sbomA, SBOM sbomB) {
        // Call recursive class on the root components
        UUID aHead = sbomA.getHeadUUID();
        UUID bHead = sbomB.getHeadUUID();

        // Destination tree will be received from the recursive merge
        DependencyTree dest = new DependencyTree();

        merge_recurse(dest, null, sbomA, aHead, sbomB, bHead);

        // Build the new SBOM
        // TODO merge trivial fields

        return new SBOM(sbomA.getOriginFormat(), sbomA.getSpecVersion(), sbomA.getSbomVersion(),
                sbomA.getSupplier(), sbomA.getSerialNumber(), sbomA.getTimestamp(), sbomA.getSignature(), dest);
    }

    /**
     * Loop through the components on an sbom and insert them into the DependencyTree
     * This is called by merge when it has a component in one bom but not the other
     *
     * @param dest      DependencyTree to merge into
     * @param sbom      SBOM to get component information from
     * @param component First component to merge
     * @param parent    Parent component within the dest tree to add the merged component to
     * @param visited   Set of visited components to prevent cycles
     */
    private void solo_assemble(DependencyTree dest, SBOM sbom, UUID component, UUID parent, Set<UUID> visited) {
        // Whenever we hit solo_assemble we have a conflict
        ComponentConflict con = new ComponentConflict(sbom.getComponent(component), null);

        // Mark that we are visiting the curent component
        visited.add(component);

        // Get the component from the SBOM
        Component comp = sbom.getComponent(component);

        Component compCopy = new Component(comp.getName(), comp.getPublisher(), comp.getVersion(), comp.getCpes(),
                comp.getPurls(), comp.getSwids());

        // Add the conflict to the component
        if (con.getConflictTypes().size() > 0) {
            compCopy.addConflict(con);
        }

        // Add the component to the destination tree
        UUID compID = dest.addComponent(parent, compCopy);

        // Loop through the children and recursively call this function
        for (UUID child : comp.getChildren()) {
            // Only assemble if we have not visited it before
            if (!visited.contains(child)) {
                solo_assemble(dest, sbom, child, compID, visited);
            }
        }
    }

    /**
     * Recursive merge call that is called by the two sbom merger method
     *
     * @param dest   DependencyTree to merge into
     * @param parent Parent component within the dest tree to add the merged component to
     * @param aBom   First SBOM to merge from
     * @param aUUID  UUID of the A component currently being merged
     * @param bBom   Second SBOM to merge from
     * @param bUUID  UUID of the B component currently being merged
     */
    private void merge_recurse(DependencyTree dest, UUID parent, SBOM aBom, UUID aUUID, SBOM bBom, UUID bUUID) {
        // Get the components from the SBOMs
        Component a = aBom.getComponent(aUUID);
        HashSet<String> aChildren = new HashSet<>();
        Component b = bBom.getComponent(bUUID);
        HashSet<String> bChildren = new HashSet<>();

        HashMap<String, UUID> componentMap = new HashMap<>();

        // Used when solo assemble is called at the top level
        Set<UUID> visited = new HashSet<>();
        // Make sure a and b are not null
        if (a == null && b == null) {
            return;
        } else if (a == null) {
            // Solo merge all of b
            solo_assemble(dest, bBom, bUUID, parent, visited);
            return;
        } else if (b == null) {
            // Solo merge all of a
            solo_assemble(dest, aBom, aUUID, parent, visited);
            return;
        }

        // Base case to merge the two components
        Component headRefA = aBom.getComponent(aUUID);
        Component headRefB = bBom.getComponent(bUUID);

        // Merge CPE, PURL, and SWID
        Set<String> cpes = new HashSet<>();
        cpes.addAll(headRefA.getCpes());
        cpes.addAll(headRefB.getCpes());

        Set<String> purls = new HashSet<>();
        purls.addAll(headRefA.getPurls());
        purls.addAll(headRefB.getPurls());

        Set<String> swids = new HashSet<>();
        swids.addAll(headRefA.getSwids());
        swids.addAll(headRefB.getSwids());

        // TODO merge trivial fields rather than taking from A
        Component headComp = new Component(headRefA.getName(), headRefA.getPublisher(), headRefA.getVersion(),
                cpes, purls, swids);

        // Compare the two components
        if (!headRefA.equals(headRefB)) {
            // Conflict, generate and add it to the merged component
            ComponentConflict con = new ComponentConflict(headRefA, headRefB);

            if (con.getConflictTypes().size() > 0) {
                headComp.addConflict(con);
            }
        }

        // Let's add it to the tree
        UUID currNode = dest.addComponent(parent, headComp);

        // Loop through a's children and add them to the map
        for (UUID childUUID : a.getChildren()) {
            Component child = aBom.getComponent(childUUID);

            // Add the name of the child to the aSet
            aChildren.add(child.getName());

            componentMap.put(child.getName(), childUUID);
        }

        // Loop through b's children and add them to the map
        for (UUID childUUID : b.getChildren()) {
            Component child = bBom.getComponent(childUUID);

            // Let's add it to b's children in the set
            // Add the name of the child to the bSet
            bChildren.add(child.getName());

            // Covering the merge case where the component is in both trees
            if (componentMap.containsKey(child.getName())) {
                // Now we know that the child is in both a and b
                merge_recurse(dest, currNode, aBom, componentMap.get(child.getName()), bBom, childUUID);

            } else {
                // So now we know that this child is only located in b, so we will do a solo
                // This covers trivial information
                solo_assemble(dest, bBom, childUUID, currNode, visited);
            }
        }

        // The other case is that a child is in A but not B and we will do a solo assembly once more
        // Do a set difference a-b to determine what a has that b does not
        aChildren.removeAll(bChildren);

        // Now we can loop through and solo assemble these dependencies
        for (String childName : aChildren) {
            solo_assemble(dest, aBom, componentMap.get(childName), currNode, visited);
        }

    }

}