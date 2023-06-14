package org.svip.sbomanalysis.old.comparison;

import org.svip.sbomanalysis.old.differ.DiffReport;
import org.svip.sbomanalysis.old.differ.UniqueIdOccurrence;
import org.svip.sbom.model.*;
import org.svip.sbomanalysis.old.differ.UniqueIdentifierType;

import java.util.*;

/**
 * file: Comparison.java
 *
 * Comparison pipeline class used by the frontend to generate
 * DiffReports from a target SBOM and a list of SBOMs.
 *
 * This class may also generate a list of component comparisons with
 * ComponentVersion objects.
 *
 * @author Tyler Drake
 */
public class Comparison {

    // Target SBOM (Ground Truth) for comparison.
    private SBOM targetSBOM;

    // List of SBOMs to stream against the Target SBOM for comparison
    private List<SBOM> sbomStream;

    // List of diff reports
    private List<DiffReport> diffReportList;

    // Set of comparisons
    private Map<String, HashSet<ComponentVersion>> comparisons;

    /**
     * Default constructor for Comparison
     *
     * @param stream a list of SBOMs
     */
    public Comparison(List<SBOM> stream) {
        this.targetSBOM = stream.get(0);
        this.sbomStream = stream.subList(1, stream.size());
        this.diffReportList = new ArrayList<>();
        this.comparisons = new HashMap<>();
    }

    /**
     * General driver for Comparison. Runs through the stream of SBOMs.
     * Each SBOM will be compared against the target and will go through assignComponents.
     */
    public void runComparison() {

        // Index of the current SBOM from the list
        int SBOM_index = 0;

        // Assign components for target SBOM
        assignComponents(targetSBOM, SBOM_index++);

        // Cycle through each sbom in the stream
        for(SBOM current_sbom : sbomStream) {

            // Run assignComponents for each sbom
            assignComponents(current_sbom, SBOM_index++);

            //generate a DiffReport for the current sbom and target sbom
            diffReportList.add(Comparer.generateReport(targetSBOM, current_sbom));

        }

    }

    /**
     * Cycles through all components in an SBOM and creates ComponentVersion objects.
     * These ComponentVersion objects are added to the comparisons list.
     *
     * @param current_sbom
     * @param SBOM_index
     */
    public void assignComponents(SBOM current_sbom, int SBOM_index) {

        // Loop through all components in SBOM and add them to comparisons list.
        for(Component current_component : current_sbom.getAllComponents()) {
            // Don't look out unpackaged components
            if (current_component.isUnpackaged()) {
                continue;
            }

            // Create a temporary ComponentVersion object for the current SBOM component
            ComponentVersion temporary_cv = generateComponentVersion(current_component, SBOM_index);

            // If the comparisons collection contains
            if(comparisons.containsKey(current_component.getName())) {

                // Get that component's component version collection
                Set<ComponentVersion> current_cv_list = comparisons.get(current_component.getName());

                // Get all ComponentVersions that match the temporary ComponentVersion's version
                List<ComponentVersion> matching_cv_list = current_cv_list
                        .stream()
                        .filter(x -> {
                            boolean objEqual = Objects.equals(x.getComponentVersion(), current_component.getVersion());
                            boolean someNull = x.getComponentVersion() == null || current_component.getVersion() == null;
                            // This means one of the versions is null and the other is not
                            if (someNull && !objEqual) {
                                return false;
                            }
                            boolean containsCurrent = x.getComponentVersion() != null && x.getComponentVersion().contains(current_component.getVersion());
                            boolean containsX = current_component.getVersion() != null && current_component.getVersion().contains(x.getComponentVersion());
                            return objEqual || containsCurrent || containsX;
                        })
                        .toList();
                // If there are no matching ComponentVersion objects in the Set for that package name
                // Else, find all matching ComponentVersion objects and add relevant information
                if (matching_cv_list.isEmpty()) {

                    // Get the old collection for this
                    HashSet<ComponentVersion> new_set = comparisons.get(current_component.getName());

                    // Remove the old set for this key
                    comparisons.remove(current_component.getName());

                    // Add the new ComponentVersion to the set
                    new_set.add(temporary_cv);

                    // Add which SBOM it appears in
                    temporary_cv.addAppearance(SBOM_index);

                    // Add the new set with the newly added ComponentVersion to the collection
                    comparisons.put(current_component.getName(), new_set);

                } else {

                    // Get the old collection for this
                    HashSet<ComponentVersion> new_set = comparisons.get(current_component.getName());

                    // Remove the old set for this key
                    comparisons.remove(current_component.getName());

                    // Take all CPEs, PURLs, and SWIDs, that don't exist in the original ComponentVersion and add them in
                    for (ComponentVersion matching_cv : matching_cv_list) {

                        // Remove the old matching ComponentVersion object from the set
                        new_set.remove(matching_cv);

                        // Update the ComponentVersion object with the extra CPEs
                        temporary_cv.getCPEs().entrySet().stream().forEach(
                                cpe -> {
                                    if(!matching_cv.getCPEs().containsKey(cpe.getKey())) {
                                        matching_cv.addCPE(cpe.getValue());
                                    }
                                    matching_cv.getCPE(cpe.getKey()).addAppearance(SBOM_index);
                                }
                        );

                        // Update the ComponentVersion object with extra PURLs
                        temporary_cv.getPURLs().entrySet().stream().forEach(
                                purl -> {
                                    if(!matching_cv.getPURLs().containsKey(purl.getKey())) {
                                        matching_cv.addPURL(purl.getValue());
                                    }
                                    matching_cv.getPURL(purl.getKey()).addAppearance(SBOM_index);

                                }
                        );

                        // Update the ComponentVersion object with extra SWIDs
                        temporary_cv.getSWIDs().entrySet().stream().forEach(
                                swid -> {
                                    if(!matching_cv.getSWIDs().containsKey(swid.getKey())) {
                                        matching_cv.addSWID(swid.getValue());
                                    }
                                    matching_cv.getSWID(swid.getKey()).addAppearance(SBOM_index);
                                }
                        );

                        // Add the appearance to ComponentVersion
                        matching_cv.addAppearance(SBOM_index);

                        // Add it back into the map
                        new_set.add(matching_cv);

                    }

                    // Add the new ComponentVersion to the set
                    new_set.add(temporary_cv);

                    // Add the new set with the newly added ComponentVersion to the collection
                    comparisons.put(current_component.getName(), new_set);

                }

            } else {

                // Add which SBOM this ComponentVersion appears in
                temporary_cv.addAppearance(SBOM_index);

                // Add a new entry to the comparisons list along with the new ComponentVersion object
                comparisons.put(temporary_cv.getComponentName(), new HashSet<>(Arrays.asList(temporary_cv)));

            }

        }

    }

    /**
     * Builds a ComponentVersion object for the current component including all IDs.
     *
     * @param component The current component
     * @param SBOM_index The index of this SBOM in the whole set
     * @return The ComponentVersion object
     */
    private ComponentVersion generateComponentVersion(Component component, int SBOM_index) {

        // Create the new ComponentVersion
        ComponentVersion new_cv = new ComponentVersion(component.getName(), component.getVersion());
        new_cv.addAppearance(SBOM_index);

        // Cycle through CPEs
        for (String cpe : component.getCpes()) {

            // Create new UniqueIDOccurrence object for the CPE, then add it to the ComponentVersion object
            UniqueIdOccurrence new_cpe_uid = new UniqueIdOccurrence(cpe, UniqueIdentifierType.CPE);
            new_cpe_uid.addAppearance(SBOM_index);
            new_cv.addCPE(new_cpe_uid);

        }
        for (PURL purl : component.getPurls()) {

            // Create new UniqueIDOccurrence object for the PURL, then add it to the ComponentVersion object
            UniqueIdOccurrence new_purl_uid = new UniqueIdOccurrence(purl.toString(), UniqueIdentifierType.PURL);
            new_purl_uid.addAppearance(SBOM_index);
            new_cv.addPURL(new_purl_uid);

        }
        for (String swid : component.getSwids()) {

            // Create new UniqueIDOccurrence object for the SWID, then add it to the ComponentVersion object
            UniqueIdOccurrence new_swid_uid = new UniqueIdOccurrence(swid, UniqueIdentifierType.SWID);
            new_swid_uid.addAppearance(SBOM_index);
            new_cv.addSWID(new_swid_uid);

        }

        return new_cv;

    }

    ///
    /// Getters
    //

    public SBOM getTargetSBOM() {
        return this.targetSBOM;
    }

    public List<DiffReport> getDiffReports() {
        return this.diffReportList;
    }

    public Map<String, HashSet<ComponentVersion>> getComparisons() {
        return this.comparisons;
    }

}