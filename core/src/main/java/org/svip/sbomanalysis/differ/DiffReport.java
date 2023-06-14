package org.svip.sbomanalysis.differ;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.nvip.plugfest.tooling.differ.conflicts.ComponentConflict;
import org.nvip.plugfest.tooling.differ.conflicts.ComponentConflictType;
import org.nvip.plugfest.tooling.differ.conflicts.SBOMConflict;
import org.nvip.plugfest.tooling.differ.conflicts.SBOMConflictType;
import org.nvip.plugfest.tooling.sbom.Component;
import org.nvip.plugfest.tooling.sbom.SBOM;

import java.util.*;

import static org.nvip.plugfest.tooling.differ.conflicts.ComponentConflictType.*;

/**
 * Class to hold results of a diff comparison between two SBOMs
 *
 * @author Matt London
 * @author Derek Garcia
 */
@JsonPropertyOrder({"target", "diffreport"})
public class DiffReport {

    /**
     * Utility record for storing conflict data
     */
    private record ConflictData(@JsonProperty String type, @JsonProperty String target, @JsonProperty String other) {
        /**
         * Create new conflict and record differences
         *
         * @param type    Type of conflict
         * @param target  data stored in the target SBOM
         * @param other data stored in the current SBOM
         */
        private ConflictData(String type, String target, String other) {
            this.type = type;
            this.target = target;
            this.other = other;
        }
    }

    /**
     * Utility class for organizing conflict data
     */
    private static class ConflictBody {
        @JsonProperty
        private final List<ConflictData> sbomConflicts = new ArrayList<>();
        @JsonProperty
        private final HashMap<String, HashMap<String, List<ConflictData>>> componentConflicts = new HashMap<>();

        /**
         * Add new conflict data for the SBOMs
         *
         * @param data conflict data
         */
        public void addSBOMConflict(ConflictData data){
            this.sbomConflicts.add(data);
        }


        /**
         * Add new conflict data for the components
         *
         * @param targetCUID target component uid
         * @param otherCUID other component uid
         * @param data conflict data
         */
        public void addComponentConflict(String targetCUID, String otherCUID, ConflictData data){
            // add new target component if it doesn't exist
            this.componentConflicts.computeIfAbsent(targetCUID, k -> new HashMap<>());

            // add new other component if it doesn't exist
            this.componentConflicts.get(targetCUID).computeIfAbsent(otherCUID, k -> new ArrayList<>());

            // add the conflict data
            this.componentConflicts.get(targetCUID).get(otherCUID).add(data);
        }
    }

    private static final String MISSING_TAG = "MISSING";
    @JsonProperty("target")
    private String targetUID;
    @JsonProperty
    private HashMap<String, ConflictBody> diffReport = new HashMap<>();
    private final SBOM targetSBOM;

    /**
     * Create a new DiffReport of a target SBOM
     *
     * @param targetUID UID of target SBOM
     * @param targetSBOM Target SBOM to reference
     */
    public DiffReport(String targetUID, SBOM targetSBOM){
        this.targetUID = targetUID;
        this.targetSBOM = targetSBOM;
    }


    /**
     * Generate a report of the differences between two SBOMs and store the results
     *
     * @param otherUID UID of other SBOM
     * @param otherSBOM other SBOM to compare against
     */
    public void compare(String otherUID, SBOM otherSBOM) {
        ConflictBody body = new ConflictBody();
        // Compare SBOM level differences
        compareSBOMs(otherSBOM, body);
        // Compare Component level Differences
        compareComponents(otherSBOM.getAllComponents(), body);
        // Update diff report
        this.diffReport.put(otherUID, body);
    }

    /**
     * Compare the target SBOM with another SBOM and update the report
     *
     * @param otherSBOM Other SBOM to compare to
     * @param body Conflict body object to update with data
     */
    private void compareSBOMs(SBOM otherSBOM, ConflictBody body){
        // Compare SBOMs
        SBOMConflict sbomConflict = new SBOMConflict(this.targetSBOM, otherSBOM);

        // Get data for conflicts
        // todo move sbom conflicts here
        for(SBOMConflictType conflictType : sbomConflict.getConflicts()){
            String targetValue = null, otherValue = null;
            switch (conflictType) {
                case SUPPLIER_MISMATCH -> {
                    targetValue = targetSBOM.getSupplier();
                    otherValue = otherSBOM.getSupplier();
                }
                // todo not implemented
                case AUTHOR_MISMATCH -> {
                    targetValue = "";
                    otherValue = "";
                }
                case TIMESTAMP_MISMATCH -> {
                    targetValue = targetSBOM.getTimestamp();
                    otherValue = otherSBOM.getTimestamp();
                }
                case ORIGIN_FORMAT_MISMATCH -> {
                    targetValue = targetSBOM.getOriginFormat().name();
                    otherValue = otherSBOM.getOriginFormat().name();
                }
                case SCHEMA_VERSION_MISMATCH -> {
                    targetValue = targetSBOM.getSpecVersion();
                    otherValue = otherSBOM.getSpecVersion();
                }
                case SBOM_VERSION_MISMATCH -> {
                    targetValue = targetSBOM.getSbomVersion();
                    otherValue = otherSBOM.getSbomVersion();
                }
                case SERIAL_NUMBER_MISMATCH -> {
                    targetValue = targetSBOM.getSerialNumber();
                    otherValue = otherSBOM.getSerialNumber();
                }
            }

            // add details to diff report
            body.addSBOMConflict(new ConflictData(conflictType.name(), targetValue, otherValue));
        }
    }


    /**
     * Compare the target SBOM with another SBOM and update the report
     *
     * @param otherComponents Other SBOM components to compare to
     * @param body Conflict body object to update with data
     */
    private void compareComponents(Set<Component> otherComponents, ConflictBody body){
        Set<ComponentConflict> componentConflicts = new HashSet<>();

        Set<Component> targetComponents = this.targetSBOM.getAllComponents();

        Set<String> targetComponentNames = new HashSet<>();
        Set<String> otherComponentNames = new HashSet<>();

        // Add all target components to a map
        HashMap<String, Component> targetComponentMap = new HashMap<>();
        for (Component targetComponent : targetComponents) {
            // Only look at packaged components
            if (targetComponent.isUnpackaged())
                continue;

            // add to target map
            targetComponentMap.put(targetComponent.getName(), targetComponent);
            targetComponentNames.add(targetComponent.getName());
        }

        // Check to see if all other components are in target
        for (Component otherComponent : otherComponents) {
            // Only look at packaged components
            if (otherComponent.isUnpackaged())
                continue;

            // add to other map
            otherComponentNames.add(otherComponent.getName());

            // Check to see if target SBOM contains the other component
            if (!targetComponentMap.containsKey(otherComponent.getName())) {
                // target doesn't contain other component
                ComponentConflict conflict = new ComponentConflict(null, otherComponent);
                componentConflicts.add(conflict);
            } else {
                // Compare the two
                ComponentConflict conflict = new ComponentConflict(targetComponentMap.get(otherComponent.getName()), otherComponent);

                // add new conflict to existing conflict
                if (conflict.getConflictTypes().size() > 0)
                    componentConflicts.add(conflict);

            }
        }

        // Check to see if target SBOM contains the other component
        for (String targetComponent : targetComponentNames) {
            if (!otherComponentNames.contains(targetComponent)) {
                // other doesn't contain target component
                ComponentConflict conflict = new ComponentConflict(targetComponentMap.get(targetComponent), null);
                componentConflicts.add(conflict);
            }
        }
        
        // get data
        // todo this and components will need a refactor to use new diff report model
        for(ComponentConflict conflict : componentConflicts){
            for(ComponentConflictType ct : conflict.getConflictTypes()){
                String targetValue = null, otherValue = null;
                switch (ct) {
                    // todo need better way to handle this
                    case COMPONENT_NOT_FOUND -> {
                        targetValue = conflict.getComponentA() == null ? null : conflict.getComponentA().getName();
                        otherValue = conflict.getComponentB() == null ? null : conflict.getComponentB().getName();
                    }
                    case COMPONENT_VERSION_MISMATCH -> {
                        targetValue = conflict.getComponentA().getVersion();
                        otherValue = conflict.getComponentB().getVersion();
                    }

                    // todo need better solution with component refactor
                    case COMPONENT_LICENSE_MISMATCH -> {
                        // get licenses
                        Set<String> licenseA = new HashSet<>(conflict.getComponentA().getLicenses());
                        Set<String> licenseB = new HashSet<>(conflict.getComponentB().getLicenses());

                        // remove duplicates
                        licenseA.removeAll(conflict.getComponentB().getLicenses());
                        licenseB.removeAll(conflict.getComponentA().getLicenses());
                        for (String license : licenseA) {
                            body.addComponentConflict(
                                    conflict.getComponentA().getName(),
                                    conflict.getComponentB().getName(),
                                    new ConflictData(COMPONENT_LICENSE_MISMATCH.name(), license, null)
                            );
                        }
                        for (String license : licenseB) {
                            body.addComponentConflict(
                                    conflict.getComponentB().getName(),
                                    conflict.getComponentA().getName(),
                                    new ConflictData(COMPONENT_LICENSE_MISMATCH.name(), null, license)
                            );
                        }
                        continue;
                    }
                    case COMPONENT_PUBLISHER_MISMATCH -> {
                        targetValue = conflict.getComponentA().getPublisher();
                        otherValue = conflict.getComponentB().getPublisher();
                    }
                    case COMPONENT_NAME_MISMATCH -> {
                        targetValue = conflict.getComponentA().getName();
                        otherValue = conflict.getComponentB().getName();
                    }
                    case COMPONENT_CPE_MISMATCH -> {
                        // get licenses
                        Set<String> cpeA = new HashSet<>(conflict.getComponentA().getCpes());
                        Set<String> cpeB = new HashSet<>(conflict.getComponentB().getCpes());

                        // remove duplicates
                        cpeA.removeAll(conflict.getComponentB().getCpes());
                        cpeB.removeAll(conflict.getComponentA().getCpes());
                        for (String cpe : cpeA) {
                            body.addComponentConflict(
                                    conflict.getComponentA().getName(),
                                    conflict.getComponentB().getName(),
                                    new ConflictData(COMPONENT_CPE_MISMATCH.name(), cpe, null)
                            );
                        }
                        for (String cpe : cpeB) {
                            body.addComponentConflict(
                                    conflict.getComponentB().getName(),
                                    conflict.getComponentA().getName(),
                                    new ConflictData(COMPONENT_CPE_MISMATCH.name(), null, cpe)
                            );
                        }
                        continue;
                    }
                    case COMPONENT_PURL_MISMATCH -> {
                        // get cpes
                        Set<String> purlA = new HashSet<>(conflict.getComponentA().getPurls());
                        Set<String> purlB = new HashSet<>(conflict.getComponentB().getPurls());

                        // remove duplicates
                        purlA.removeAll(conflict.getComponentB().getPurls());
                        purlB.removeAll(conflict.getComponentA().getPurls());
                        for (String purl : purlA) {
                            body.addComponentConflict(
                                    conflict.getComponentA().getName(),
                                    conflict.getComponentB().getName(),
                                    new ConflictData(COMPONENT_PURL_MISMATCH.name(), purl, null)
                            );
                        }
                        for (String purl : purlB) {
                            body.addComponentConflict(
                                    conflict.getComponentB().getName(),
                                    conflict.getComponentA().getName(),
                                    new ConflictData(COMPONENT_PURL_MISMATCH.name(), null, purl)
                            );
                        }
                        continue;
                    }
                    case COMPONENT_SWID_MISMATCH -> {
                        // get swids
                        Set<String> swidA = new HashSet<>(conflict.getComponentA().getSwids());
                        Set<String> swidB = new HashSet<>(conflict.getComponentA().getSwids());

                        // remove duplicates
                        swidA.removeAll(conflict.getComponentB().getSwids());
                        swidB.removeAll(conflict.getComponentA().getSwids());
                        for (String swid : swidA) {
                            body.addComponentConflict(
                                    conflict.getComponentA().getName(),
                                    conflict.getComponentB().getName(),
                                    new ConflictData(COMPONENT_SWID_MISMATCH.name(), swid, null)
                            );
                        }
                        for (String swid : swidB) {
                            body.addComponentConflict(
                                    conflict.getComponentB().getName(),
                                    conflict.getComponentA().getName(),
                                    new ConflictData(COMPONENT_SWID_MISMATCH.name(), null, swid)
                            );
                        }
                        continue;
                    }
                    case COMPONENT_SPDXID_MISMATCH -> {
                        targetValue = conflict.getComponentA().getUniqueID();
                        otherValue = conflict.getComponentB().getUniqueID();
                    }
                    case COMPONENT_UNKNOWN_MISMATCH -> {
                        targetValue = conflict.getComponentA().toString();
                        otherValue = conflict.getComponentB().toString();
                    }
                    // ignore unhandled cases
                    default -> {
                        continue;
                    }
                }

                String targetIdentifier = conflict.getComponentA() == null ? MISSING_TAG : conflict.getComponentA().getName();
                String conflictIdentifier = conflict.getComponentB() == null ? MISSING_TAG : conflict.getComponentB().getName();

                // Skip if keys are null
                if(targetIdentifier == null || conflictIdentifier == null)
                    continue;

                body.addComponentConflict(
                        targetIdentifier,
                        conflictIdentifier,
                        new ConflictData(ct.name(), targetValue, otherValue));
            }
        }
    }
}
