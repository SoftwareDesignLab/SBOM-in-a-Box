package org.svip.sbomfactory.parsers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.svip.sbom.model.old.Component;
import org.svip.sbom.model.old.SBOM;
import org.svip.sbomfactory.generators.generators.SBOMGenerator;
import org.svip.sbomfactory.generators.utils.Debug;
import org.svip.sbomfactory.generators.utils.ParserComponent;
import org.svip.sbomfactory.generators.utils.generators.GeneratorSchema;
import org.svip.sbomfactory.generators.utils.virtualtree.VirtualNode;
import org.svip.sbomfactory.generators.utils.virtualtree.VirtualPath;
import org.svip.sbomfactory.generators.utils.virtualtree.VirtualTree;
import org.svip.sbomfactory.parsers.contexts.ContextParser;
import org.svip.sbomfactory.parsers.contexts.DeadImportParser;
import org.svip.sbomfactory.parsers.contexts.SubprocessParser;
import org.svip.sbomfactory.parsers.languages.*;
import org.svip.sbomfactory.parsers.packagemanagers.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.svip.sbomfactory.generators.utils.Debug.LOG_TYPE;
import static org.svip.sbomfactory.generators.utils.Debug.log;

/**
 * <b>File</b>: ParserController.java<br>
 * <b>Description</b>: Central controller class that interfaces between
 * the main driver and the language specific parsers.
 *
 * @author Dylan Mulligan
 * @author Derek Garcia
 * @author Ian Dunn
 */
public class ParserController {

    //#region Attributes
    private final String projectName;
    private final SBOM SBOM;
    private final VirtualTree fileTree;
    private static final ObjectMapper OM = new ObjectMapper(new JsonFactory()); // ObjectMapper Initialization
    static { OM.setSerializationInclusion(JsonInclude.Include.NON_NULL); } // ObjectMapper Configuration
    private static final HashMap<String, Parser> EXTENSION_MAP = new HashMap<>() {{
        //
        // New Parsers
        // TODO use pointers to call address of c parser, etc

        final CParser cParser = new CParser();
        put("c", cParser);
        put("h", cParser);
        final CppParser cppParser = new CppParser();
        put("cpp", cppParser);
        put("hpp", cppParser);
        put("cc", cppParser);
        put("cs", new CSharpParser());
        put("py", new PythonParser());
        put("java", new JavaParser());
        final JSTSParser jstsParser = new JSTSParser();
        put("js", jstsParser);
        put("ts", jstsParser);
        put("pl", new PerlParser());
        put("rb", new RubyParser());
        final ScalaParser scalaParser = new ScalaParser();
        put("sc", scalaParser);
        put("scala", scalaParser);
        final RustParser rustParser = new RustParser();
        put("rs", rustParser);
//        put("rlib", rustParser); // TODO: include .rlib files?
        put("go", new GoParser());

        // Package Manager Parsers
        put("pom.xml", new POMParser());
        put("csproj", new CSProjParser());
        put("requirements.txt", new RequirementsParser());
        put("gradle", new GradleParser());
        //search for the string conanfile.py in this file to see the additional processing logic.
        put("conanfile.txt", new ConanParser());
        // ADD NEW PARSER HERE: put("fileExtn", new Parser);
    }};

    //#endregion

    //#region Constructors

    /**
     * Create a new ParserController with a VirtualTree representation of the filesystem to parse.
     *
     * @param fileTree A VirtualTree representation of the filesystem to parse
     */
    public ParserController(VirtualTree fileTree) {
        // Set project name to root filename
        this.fileTree = fileTree;

        // Set project name to the common directory of the project.
        this.projectName = this.fileTree.getCommonDirectory().getPath().toString();

        // Create new SBOM Object
        ParserComponent headComponent = new ParserComponent(this.projectName);
        this.SBOM = new SBOM(headComponent);
    }

    //#endregion

    //#region Getters

    public SBOM getSBOM() { return this.SBOM; }

    //#endregion

    //#region Core Methods

    /**
     * Parses all files passed into the ParserController via the VirtualTree and logs the amount of time taken.
     */
    public void parseAll() {
        List<VirtualNode> internalFiles = fileTree.getAllFiles();
        final long parseT1 = System.currentTimeMillis();

        int parseCounter = 0;
        for(VirtualNode file : fileTree.getAllFiles()) {
            parse(this.SBOM.getHeadUUID(), file.getPath(), file.getFileContents(), internalFiles);
            parseCounter++;
        }

        final long parseT2 = System.currentTimeMillis();

        // Report stats
        log(Debug.LOG_TYPE.SUMMARY, String.format("Component parsing complete. " +
                        "Parsed %d components in %.2f seconds.",
                parseCounter,
                (float)(parseT2 - parseT1) / 1000));
    }


    /**
     * Internal parse method that takes a filepath and a parent Component (optionally).
     * If parent is null, this method will parse normally, otherwise, it will try to
     * append all found Components to the given parent Component.
     *
     * @param filepath path to file or root directory to be parsed
     * @param parent parent Component to be appended to
     */
    public void parse(UUID parent, VirtualPath filepath, String fileContents, List<VirtualNode> internalFiles) {
        // Set project name to root filename
        final String filename = filepath.getFileName().toString();

        // Extract extn from filename
        String extn = filename.substring(filename.lastIndexOf('.') + 1);

        // If extn matches some generic filetypes, use whole filename instead
        switch (extn) {
            case "xml", "txt" -> extn = filename;
        }

        // Get correct parser (if and only if extn relates to a valid Parser)
        final Parser parser = EXTENSION_MAP.get(extn);
        // Skip if extn did not correlate to valid Parser
        if(parser == null) {
            log(LOG_TYPE.DEBUG, "Skipping file with ignored filetype: " + filename);
            return;
        } else log(LOG_TYPE.SUMMARY, "Parsing file '" + filename + "'");

        final ArrayList<ContextParser> contextParsers = new ArrayList<>();
        if(parser instanceof LanguageParser) {
//            contextParsers.add(new CommentParser());
            contextParsers.add(new SubprocessParser());
            contextParsers.add(new DeadImportParser());
            // Add new ContextParser
        }

        // Init Component list
        ArrayList<ParserComponent> components = new ArrayList<>();

        // Configure parser
        parser.setPWD(filepath);
        parser.setInternalFiles(internalFiles);

        // Parse components
        parser.parse(components, fileContents);

        // If file being parsed is a language file, execute the following additionally
        if(parser instanceof LanguageParser) {
            for (final ContextParser cp : contextParsers) cp.parse(components, fileContents);
            switch (filename) {
                case "conanfile.py": new ConanParser().parse(components, fileContents);
            }
        }
        // If file being parsed is a package manager file
        if(parser instanceof PackageManagerParser) { // Parsing an EXTERNAL dependency
            components.forEach(ParserComponent::setPackaged); // Sets it to packaged and EXTERNAL
        } // Otherwise it will be unpackaged and INTERNAL (LIBRARY if it has been parsed as such)

        components.forEach(c -> c.addFile(filepath.toString()));

        // componentMap contains a map from a component's name to itself
        Map<String, ParserComponent> componentMap = new HashMap<>();
        for(Component c : this.SBOM.getAllComponents()) {
            componentMap.put(c.getName(), (ParserComponent) c);
        }

        // List to store any duplicates/dead imports we find to avoid concurrent arraylist modification
        ArrayList<ParserComponent> toRemove = new ArrayList<>();

        // Get list of all components that have dead imports and remove from main components array
        List<String> deadImportNames = components.stream()
                .filter(c -> {
                    if(c != null && c.getType() == ParserComponent.Type.DEAD_IMPORT) {
                        toRemove.add(c); // Remove dead import component parsed by DeadImportParser
                        return true;
                    }
                    return false;
                }).map(Component::getName).toList();

        components.removeAll(toRemove);
        toRemove.clear(); // Clear components to remove for below loop

        int deadImportCounter = 0;
        // Check for duplicate named components & dead imports
        for(ParserComponent component : components) {
            if(deadImportNames.contains(component.getName())) {
                toRemove.add(component);
                Debug.log(LOG_TYPE.DEBUG, "Removed dead import " + component.getName());
                deadImportCounter++;
                continue;
            }

            ParserComponent old = componentMap.get(component.getName());
            // If a component name doesn't exist, there are no duplicates
            if(old == null) continue;

            // Add the component to the map to avoid further duplicates from THIS FILE
            componentMap.put(component.getName(), component);

            // Compare important fields and update old component
            old.setGroup(component.getGroup());
            component.getFiles().forEach(old::addFile);

            // TODO possibly more assignments?

            toRemove.add(component); // Remove new component directly
            Debug.log(LOG_TYPE.DEBUG, "Found and removed duplicate component " + component.getName());
        }

        components.removeAll(toRemove); // Remove all duplicates

        String removedComponentsLog = "Removed " + toRemove.size() + " Components parsed from file " + filename;
        if(deadImportCounter > 0) removedComponentsLog += " (" + deadImportCounter + "/" + toRemove.size()
                + " were dead imports)";
        Debug.log(LOG_TYPE.DEBUG, removedComponentsLog);

        // Add Components to SBOM
        for (Component c : components) {
            this.SBOM.addComponent(parent, c);
        }
    }

    /**
     * Write the parsed SBOM object to an SBOM file with a given schema and format.
     *
     * @param outPath Path to write file to
     * @param outSchema The schema of the SBOM file to write to.
     * @param outFormat The format of the SBOM file to write to.
     */
    public String toFile(String outPath, GeneratorSchema outSchema, GeneratorSchema.GeneratorFormat outFormat) throws IOException {
        // If format is not supported by schema
        if(!outSchema.supportsFormat(outFormat)) {
            // Acquire default format from schema
            final GeneratorSchema.GeneratorFormat defaultFormat = outSchema.getDefaultFormat();

            // Warn of mismatch
            log(LOG_TYPE.ERROR, String.format("Format '%s' does not match schema '%s', '%s' will be used instead",
                    outFormat,
                    outSchema,
                    defaultFormat
                    ));

            // Replace erroneous format
            outFormat = defaultFormat;
        }

        // Create generator based on schema
        final SBOMGenerator generator = new SBOMGenerator(this.SBOM, outSchema);

        if(outPath != null) {
            // Make new out directory if none exist
            final File outDir = new File(outPath);
            if(outDir.mkdirs())
                log(LOG_TYPE.SUMMARY, "New Output Directory created [ " + outPath + " ]");

            // Write SBOM to file according to schema and file format
            generator.writeFile(outPath, outFormat);
            return null;
        } else {
            String fileString = generator.writeFileToString(outFormat, true);
            log(Debug.LOG_TYPE.INFO, "SBOM String:\n" + fileString);
            return fileString;
        }
    }

    //#endregion
}
