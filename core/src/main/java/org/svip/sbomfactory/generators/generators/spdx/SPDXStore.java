package org.svip.sbomfactory.generators.generators.spdx;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.svip.sbomfactory.generators.generators.BOMStore;
import org.svip.sbomfactory.generators.generators.utils.GeneratorException;
import org.svip.sbomfactory.generators.generators.utils.GeneratorSchema;
import org.svip.sbomfactory.generators.generators.utils.License;
import org.svip.sbomfactory.generators.utils.Debug;
import org.svip.sbomfactory.generators.utils.ParserComponent;

import java.util.*;

import static org.svip.sbomfactory.generators.utils.Debug.log;

/**
 * File: SPDXStore.java
 * <p>
 * Dataclass to store all attributes, packages, and relationships relevant to an SPDX document.
 * </p>
 * @author Ian Dunn
 */
@JsonSerialize(using = SPDXSerializer.class)
public class SPDXStore extends BOMStore {

    //#region Attributes

    /**
     * The reference ID of the generated SPDX document.
     */
    private final String documentId;

    /**
     * A list of ALL packages in the document, regardless of depth.
     */
    private final ArrayList<ParserComponent> packages;

    /**
     * The list of packages that the document describes.
     */
    private final ArrayList<String> documentDescribes;

    /**
     * The list of relationships between packages (i.e. if a package depends on another package)
     */
    private final ArrayList<Relationship> relationships;

    private final Set<License> externalLicenses;

    /**
     * The map of an analyzed file to its generated SPDX ID
     */
    private final Map<String, String> files;

    /**
     * A variable holding the next free ID to be used for package identifiers.
     */
    private int nextId;

    /**
     * A variable holding the next free ID to be used for license identifiers.
     */
    private int nextLicenseId;

    //#endregion

    //#region Constructors

    /** TODO update dosctring
     * The default constructor to create a new instance of an SPDXStore.
     *
     */
    public SPDXStore(String serialNumber, int bomVersion, ParserComponent headComponent) {
        super(GeneratorSchema.SPDX, "2.3", serialNumber, bomVersion, headComponent);

        documentId = "SPDXRef-DOCUMENT";

        this.packages = new ArrayList<>();
        this.documentDescribes = new ArrayList<>();
        this.relationships = new ArrayList<>();
        this.files = new HashMap<>();
        this.externalLicenses = new HashSet<>();

        this.nextId = 0;
        this.nextLicenseId = 0;
    }

    //#endregion

    //#region Override Methods

    /**
     * Adds a component to this CycloneDXStore instance.
     *
     * @param component The ParserComponent storing all necessary component data.
     */
    @Override
    public void addComponent(ParserComponent component) {
        String spdxId = getNextId(); // Get the reference ID for the component

        //
        // Component manipulation
        //

        component.setSPDXID(spdxId);

        // Go through all found licenses and resolve them
        component.resolveLicenses();
        Set<License> unresolved = component.getUnresolvedLicenses();
        for(License u : unresolved) {
            String nextLicense = getNextLicenseId();
            externalLicenses.add(component.resolveLicense(u.getLicenseName(), nextLicense));
        }

        //
        // Check for files
        //

        if(component.getFiles().size() > 0) {
            component.getFiles().forEach(filename -> {
                if(!files.containsKey(filename)) files.put(filename, getNextId());
            });
        }

        //
        // SPDXStore manipulation
        //

        documentDescribes.add(spdxId); // Add reference ID to the document

        // Add package and log
        packages.add(component);
        log(Debug.LOG_TYPE.DEBUG, "SPDXStore: Added component " + component.getName() + " with SPDX ID " + spdxId);
    }

    /**
     * Adds a child to an existing component in this CycloneDXStore instance.
     *
     * @param parent The parent UUID that the child depends on.
     * @param child  The child ParserComponent storing all necessary component data.
     */
    @Override
    public void addChild(ParserComponent parent, ParserComponent child) throws GeneratorException {
        addComponent(child);

        String dependencyId = parent.getSPDXID();
        String dependsOnId = child.getSPDXID();

        Relationship relationship = new Relationship(dependencyId, dependsOnId, Relationship.RELATIONSHIP_TYPE.DEPENDS_ON);

        List<String> spdxIds = packages.stream().map(ParserComponent::getSPDXID).toList();

        if(!spdxIds.contains(dependencyId))
            throw new GeneratorException("No package with SPDX ID \"" + dependencyId + "\" found when trying to insert" +
                    relationship + ".");
        if(!spdxIds.contains(dependsOnId))
            throw new GeneratorException("No package with SPDX ID \"" + dependsOnId + "\" found when trying to insert" +
                    relationship + ".");


        relationships.add(relationship);
        log(Debug.LOG_TYPE.DEBUG, "SPDXStore: Added relationship " + relationship);
    }

    //#endregion

    //#region Getters

    public String getDocumentId() {
        return documentId;
    }

    protected ArrayList<ParserComponent> getPackages() {
        return packages;
    }

    protected ArrayList<String> getDocumentDescribes() {
        return documentDescribes;
    };

    public ArrayList<Relationship> getRelationships() {
        return relationships;
    }

    public Map<String, String> getFiles() {
        return files;
    }

    public Set<License> getExternalLicenses() {
        return externalLicenses;
    }

    //#endregion

    //#region Helper Methods

    /**
     * Private helper method to get the next available ID for the SPDX document descriptions.
     *
     * @return An SPDX reference ID in the format of SPDXRef-XX.
     */
    private String getNextId() {
        String prefix = "SPDXRef-";
        if(nextId < 10)
            prefix += "0";

        prefix += nextId;
        nextId++;
        return prefix;
    }

    /**
     * Private helper method to get the next available ID for an SPDX license identifer.
     *
     * @return An SPDX license reference ID in the format of SPDXRef-License-XX.
     */
    private String getNextLicenseId() {
        String prefix = "SPDXRef-License-";
        if(nextLicenseId < 10)
            prefix += "0";

        prefix += nextLicenseId;
        nextLicenseId++;
        return prefix;
    }

    //#endregion
}
