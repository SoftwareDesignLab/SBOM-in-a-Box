package org.svip.generation.parsers;

import org.svip.generation.parsers.contexts.ContextParser;
import org.svip.generation.parsers.contexts.DeadImportParser;
import org.svip.generation.parsers.contexts.SubprocessParser;
import org.svip.generation.parsers.languages.*;
import org.svip.generation.parsers.packagemanagers.*;
import org.svip.generation.parsers.utils.VirtualPath;
import org.svip.sbom.builder.objects.SVIPComponentBuilder;
import org.svip.sbom.builder.objects.SVIPSBOMBuilder;
import org.svip.sbom.factory.objects.SVIPSBOMBuilderFactory;
import org.svip.sbom.model.objects.SVIPComponentObject;
import org.svip.sbom.model.objects.SVIPSBOM;
import org.svip.sbom.model.shared.metadata.CreationData;
import org.svip.sbom.model.shared.metadata.CreationTool;
import org.svip.serializers.Metadata;
import org.svip.serializers.SerializerFactory;
import org.svip.utils.Debug;

import java.util.*;

import static org.svip.utils.Debug.LOG_TYPE;
import static org.svip.utils.Debug.log;

/**
 * <b>File</b>: ParserManager.java<br>
 * <b>Description</b>: Central controller class that interfaces between
 * the main driver and the language specific parsers.
 *
 * @author Dylan Mulligan
 * @author Derek Garcia
 * @author Ian Dunn
 */
public class ParserManager {

    //#region Attributes
    private final String projectName;
    private final SVIPSBOMBuilder builder;

    /**
     * Store a list of all SVIP components mapped to their hash as the key (to check for duplicates).
     */
    private final Map<String, SVIPComponentBuilder> components;
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

    /**
     * TODO
     * Create a new ParserManager.
     */
    public ParserManager(String projectName, Map<VirtualPath, String> files) {
        this.projectName = projectName;
        this.components = new HashMap<>();
        this.files = files;
        this.builder = new SVIPSBOMBuilderFactory().createBuilder();
    }

    //#endregion

    //#region Getters

    public SVIPSBOM buildSBOM(SerializerFactory.Schema schema) {
        // Add Components to SBOM
        for (SVIPComponentBuilder c : components.values()) {
            // Default to external
            if (Parser.getType(c) == null) c.setType("EXTERNAL");

            c.setFilesAnalyzed(true);

            c.setUID(UUID.randomUUID().toString());
            builder.addComponent(c.build());
        }

        builder.setFormat(schema.getName());
        builder.setVersion("1");
        builder.setName(this.projectName);
        builder.setSpecVersion(schema.getVersion());
        builder.setUID(UUID.randomUUID().toString());

        CreationData data = new CreationData();
        data.setCreationTime(new Date().toString());

        CreationTool tool = new CreationTool();
        tool.setName(Metadata.NAME);
        tool.setVersion(Metadata.VERSION);
        tool.setVendor(Metadata.VENDOR);
        data.addCreationTool(tool);

        builder.setCreationData(data);

        Debug.log(LOG_TYPE.SUMMARY, "Building " + schema + " SBOM");
        return builder.Build();
    }

    public void addFile(VirtualPath path, String contents) {
        files.put(path, contents);
    }

    //#endregion

    //#region Core Methods

    /**
     * Parses all files passed into the ParserManager via the VirtualTree and logs the amount of time taken.
     */
    public void parseAll() {
        Set<VirtualPath> internalFiles = files.keySet();
        final long parseT1 = System.currentTimeMillis();

        int parseCounter = 0;
        for (VirtualPath path : internalFiles) {
            parse(path, files.get(path), internalFiles);
            parseCounter++;
        }

        final long parseT2 = System.currentTimeMillis();

        // Report stats
        log(Debug.LOG_TYPE.SUMMARY, String.format("Component parsing complete. " +
                        "Parsed %d components in %.2f seconds.",
                parseCounter,
                (float) (parseT2 - parseT1) / 1000));
    }


    /**
     * TODO
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
        if (parser == null) {
            log(LOG_TYPE.DEBUG, "Skipping file with ignored filetype: " + filename);
            return;
        } else log(LOG_TYPE.SUMMARY, "Parsing file '" + filename + "'");

        final ArrayList<ContextParser> contextParsers = new ArrayList<>();
        if (parser instanceof LanguageParser) {
//            contextParsers.add(new CommentParser());
            contextParsers.add(new SubprocessParser());
            contextParsers.add(new DeadImportParser());
            // Add new ContextParser
        }

        // Init Component list
        List<SVIPComponentBuilder> found = new ArrayList<>();

        // Configure parser
        parser.setPWD(filepath);
        parser.setSourceFiles(internalFiles);

        // Parse components
        parser.parse(found, fileContents);

        found.forEach(Parser::generateHash); // Set all hashes

        // If file being parsed is a language file, execute the following additionally
        if (parser instanceof LanguageParser) {
            if (filename.equals("conanfile.py"))
                new ConanParser().parse(found, fileContents);
            else
                for (final ContextParser cp : contextParsers) cp.parse(found, fileContents);
        }

        int deadImportCounter = 0;
        int totalRemoved = 0;
        // Check for duplicate named components & dead imports
        for (SVIPComponentBuilder c : found) {
            SVIPComponentObject newComponent = c.build();
            String hash = newComponent.getHashes().get("SHA256");
            SVIPComponentBuilder oldComponent = components.get(hash);

            // Dead import found
            if (newComponent.getType().equalsIgnoreCase("dead_import")) {
                Debug.log(LOG_TYPE.DEBUG, "Removed dead import " + newComponent.getName());
                deadImportCounter++;
                totalRemoved++;
                continue;
            }

            // Duplicate found
            if (oldComponent != null) {
                Debug.log(LOG_TYPE.DEBUG, "Found and removed duplicate component " + newComponent.getName());
                totalRemoved++;
                continue;
            }

            // Otherwise, configure component
            c.setFileName(filename);

            if (parser instanceof PackageManagerParser)
                c.setFileNotice(null);
            else
                c.setFileNotice("PARSED AS SOURCE FILE"); // TODO confirm this
            components.put(hash, c);
            continue;
        }

        String removedComponentsLog = "Removed " + totalRemoved + " Components parsed from file " + filename;
        if (deadImportCounter > 0) removedComponentsLog += " (" + deadImportCounter + "/" + totalRemoved
                + " were dead imports)";
        Debug.log(LOG_TYPE.DEBUG, removedComponentsLog);
    }

    //#endregion
}
