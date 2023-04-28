package org.svip.sbomfactory.generators.generators.spdx;

import org.svip.sbomfactory.generators.generators.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.svip.sbomfactory.generators.generators.utils.GeneratorException;
import org.svip.sbomfactory.generators.generators.utils.License;
import org.svip.sbomfactory.generators.generators.utils.Tool;
import org.svip.sbomfactory.generators.utils.Debug;
import org.svip.sbomfactory.generators.utils.ParserComponent;

import java.util.*;
import java.util.stream.Collectors;

import static org.svip.sbomfactory.generators.utils.Debug.log;

/**
 * File: Document.java
 * <p>
 * Dataclass to store all attributes, packages, and relationships relevant to an SPDX document.
 * </p>
 * @author Ian Dunn
 */
@JsonSerialize(using = DocumentSerializer.class)
public class Document {

    //#region Constants

    /**
     * The reference ID of the generated SPDX document.
     */
    public final static String SPDXID = "SPDXRef-DOCUMENT";

    /**
     * The SPDX version of the document.
     */
    private static final String SPDX_VERSION = "SPDX-" + SPDXGenerator.SPEC_VERSION;

    //#endregion

    //#region Attributes

    /**
     * The creation date of the document. This is automatically generated when the store is initialized, since the store
     * is a temporary class to hold information when the document is serialized.
     */
    private final String creationDate;

    /**
     * A list of creators of the document. Must be prefixed with "Person: ", "Organization: ", or "Tool: ".
     */
    private final ArrayList<String> creators;

    /**
     * The name of the generated SPDX document.
     */
    private final String name;

    /**
     * The URI of the document.
     */
    private final String documentNamespace;

    /**
     * The license STRING of the tool used to create the document.
     */
    private final String dataLicense;

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

    private int nextLicenseId;

    //#endregion

    //#region Constructors

    /**
     * The default constructor to create a new instance of an Document.
     *
     * @param name The document name.
     * @param documentUri The unique URI of the document.
     * @param tool The tool used to generate the document.
     */
    public Document(String name, String documentUri, Tool tool) {
        this.creationDate = Tool.createTimestamp();

        this.name = name;
        this.creators = new ArrayList<>();
        this.creators.add(tool.getToolInfo());
        this.documentNamespace = documentUri;
        this.dataLicense = String.join(" AND ", tool.getLicenses().stream().map(License::toString)
                .collect(Collectors.toSet()));

        this.packages = new ArrayList<>();
        this.documentDescribes = new ArrayList<>();
        this.relationships = new ArrayList<>();
        this.files = new HashMap<>();
        this.externalLicenses = new HashSet<>();
        this.nextId = 0;
        this.nextLicenseId = 0;
    }

    //#endregion

    //#region Getters

    public String getCreationDate() {
        return creationDate;
    }

    public ArrayList<String> getCreators() {
        return creators;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return SPDX_VERSION;
    }

    public String getDocumentNamespace() {
        return documentNamespace;
    }

    public String getDataLicense() {
        return dataLicense;
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

    //#region Core Methods

    /**
     * Adds a package to the SPDX document, generates a unique ID for it, and adds that ID to the list of SPDX
     * identifiers that the document describes.
     *
     * @param component The ParserComponent storing all necessary package data.
     */
    public void addPackage(ParserComponent component) {
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

        if(component.getFile() != null && !files.containsKey(component.getFile())) {
            files.put(component.getFile(), getNextId());
        }

        //
        // Document manipulation
        //

        documentDescribes.add(spdxId); // Add reference ID to the document

        // Add package and log
        packages.add(component);
        log(Debug.LOG_TYPE.DEBUG, "Document: Added component " + component.getName() + " with SPDX ID " + spdxId);
    }

    /**
     * Adds a dependency to the SPDX document showing that one package depends on another.
     *
     * @param dependencyId The SPDX identifier of the package that is dependent.
     * @param dependsOnId The SPDX identifier of the package that the dependencyId depends on.
     */
    public void addDependency(String dependencyId, String dependsOnId) throws GeneratorException {
        Relationship relationship = new Relationship(dependencyId, dependsOnId, Relationship.RELATIONSHIP_TYPE.DEPENDS_ON);

        List<String> spdxIds = packages.stream().map(ParserComponent::getSPDXID).toList();

        if(!spdxIds.contains(dependencyId))
            throw new GeneratorException("No package with SPDX ID \"" + dependencyId + "\" found when trying to insert" +
                    relationship + ".");
        if(!spdxIds.contains(dependsOnId))
            throw new GeneratorException("No package with SPDX ID \"" + dependsOnId + "\" found when trying to insert" +
                    relationship + ".");


        relationships.add(relationship);
        log(Debug.LOG_TYPE.DEBUG, "Document: Added relationship " + relationship);
    }

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

    private String getNextLicenseId() {
        String prefix = "SPDXLicenseRef-";
        if(nextLicenseId < 10)
            prefix += "0";

        prefix += nextLicenseId;
        nextLicenseId++;
        return prefix;
    }

    //#endregion
}
