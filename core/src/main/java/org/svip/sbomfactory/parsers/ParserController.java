package org.svip.sbomfactory.parsers;

import org.svip.builderfactory.SVIPSBOMBuilderFactory;
import org.svip.sbom.builder.objects.SVIPSBOMBuilder;
import org.svip.sbom.model.interfaces.generics.Component;
import org.svip.sbom.model.objects.SVIPComponentObject;
import org.svip.sbom.model.objects.SVIPSBOM;
import org.svip.sbomfactory.parsers.contexts.ContextParser;
import org.svip.sbomfactory.parsers.contexts.DeadImportParser;
import org.svip.sbomfactory.parsers.contexts.SubprocessParser;
import org.svip.sbomfactory.parsers.languages.*;
import org.svip.sbomfactory.parsers.packagemanagers.*;
import org.svip.utils.Debug;
import org.svip.utils.VirtualPath;

import java.util.*;

import static org.svip.utils.Debug.LOG_TYPE;
import static org.svip.utils.Debug.log;

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
    private final SVIPSBOMBuilder builder;
    private final Map<VirtualPath, String> files;
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

    public ParserController() {
        // Set project name to root filename
        this.files = new HashMap<>();
        this.projectName = "SVIP Project";
        this.builder = new SVIPSBOMBuilderFactory().createBuilder();
    }

    /** TODO
     * Create a new ParserController.
     */
    public ParserController(Map<VirtualPath, String> files) {
        // Set project name to root filename
        this.files = files;
        this.projectName = "SVIP Project";
        this.builder = new SVIPSBOMBuilderFactory().createBuilder();
    }

    //#endregion

    //#region Getters

    public SVIPSBOM getSBOM() { return builder.Build(); }

    public void addFile(VirtualPath path, String contents) {
        files.put(path, contents);
    }

    //#endregion

    //#region Core Methods

    /**
     * Parses all files passed into the ParserController via the VirtualTree and logs the amount of time taken.
     */
    public void parseAll() {
        Set<VirtualPath> internalFiles = files.keySet();
        final long parseT1 = System.currentTimeMillis();

        int parseCounter = 0;
        for(VirtualPath path : internalFiles) {
            parse(path, files.get(path), internalFiles);
            parseCounter++;
        }

        final long parseT2 = System.currentTimeMillis();

        // Report stats
        log(Debug.LOG_TYPE.SUMMARY, String.format("Component parsing complete. " +
                        "Parsed %d components in %.2f seconds.",
                parseCounter,
                (float)(parseT2 - parseT1) / 1000));
    }


    /** TODO
     * Internal parse method that takes a filepath and a parent Component (optionally).
     * If parent is null, this method will parse normally, otherwise, it will try to
     * append all found Components to the given parent Component.
     *
     * @param filepath path to file or root directory to be parsed
     */
    private void parse(VirtualPath filepath, String fileContents, Set<VirtualPath> internalFiles) {
        // Set project name to root filename
        final String filename = filepath.getFileName().toString();

        // Extract extn from filename
        String extn = filepath.getFileExtension();

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
        List<SVIPComponentObject> components = new ArrayList<>();

        // Configure parser
        parser.setPWD(filepath);
        parser.setSourceFiles(internalFiles);

        // Parse components
        parser.parse(components, fileContents);

        // If file being parsed is a language file, execute the following additionally
        if(parser instanceof LanguageParser) {
            if (filename.equals("conanfile.py"))
                new ConanParser().parse(components, fileContents);
            else
                for (final ContextParser cp : contextParsers) cp.parse(components, fileContents);
        }

        // TODO add filenotice flag to differentiate files
        for (SVIPComponentObject c : components) {
            Parser.set(c, b -> b.setFileName(filepath.toString()));
            Parser.set(c, b -> b.setFilesAnalyzed(true));
            Parser.generateHash(c);

            if (parser instanceof PackageManagerParser)
                Parser.set(c, b -> b.setFileNotice(null));
                // TODO set to external
            else
                Parser.set(c, b -> b.setFileNotice("PARSED AS SOURCE FILE")); // TODO confirm this
        }

        // componentMap contains a map from a component's hash to itself
        Map<String, SVIPComponentObject> componentMap = new HashMap<>();
        for(Component c : builder.Build().getComponents())
            componentMap.put(c.getHashes().get("SHA256"), (SVIPComponentObject) c);

        // List of hashes to store any duplicates/dead imports we find to avoid concurrent arraylist modification
        List<String> toRemove = new ArrayList<>();
        int deadImportCounter = 0;

        for (SVIPComponentObject c : components)
            if (c.getType().equalsIgnoreCase("dead_import")) {
                toRemove.add(c.getHashes().get("SHA256"));
                Debug.log(LOG_TYPE.DEBUG, "Removed dead import " + c.getName());
                deadImportCounter++;
            }

        // Check for duplicate named components & dead imports
        for(SVIPComponentObject c : components) {
            SVIPComponentObject old = componentMap.get(c.getHashes().get("SHA256"));

            // If a component name doesn't exist, there are no duplicates
            if(old == null) continue;

            // Compare important fields and update old component
            Parser.set(old, b -> b.setGroup(c.getGroup()));
            Parser.set(old, b -> b.setFileName(c.getFileName()));
            // TODO possibly more assignments?

            toRemove.add(c.getHashes().get("SHA256")); // Remove new component directly
            Debug.log(LOG_TYPE.DEBUG, "Found and removed duplicate component " + c.getName());
        }

        // Remove all components whose hashes are in toRemove
        components = components.stream().filter(c -> toRemove.contains(c.getHashes().get("SHA256"))).toList();

        String removedComponentsLog = "Removed " + toRemove.size() + " Components parsed from file " + filename;
        if(deadImportCounter > 0) removedComponentsLog += " (" + deadImportCounter + "/" + toRemove.size()
                + " were dead imports)";
        Debug.log(LOG_TYPE.DEBUG, removedComponentsLog);

        // Add Components to SBOM
        for (SVIPComponentObject c : components) {
            // Default to external
            if (c.getType() == null) Parser.set(c, b -> b.setType("EXTERNAL"));
            builder.addComponent(c);
        }
    }

    //#endregion
}
