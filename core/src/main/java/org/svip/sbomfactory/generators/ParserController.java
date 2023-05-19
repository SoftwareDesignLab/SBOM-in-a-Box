package org.svip.sbomfactory.generators;

import org.svip.sbomfactory.generators.generators.utils.GeneratorException;
import org.svip.sbomfactory.generators.generators.utils.GeneratorSchema;
import org.svip.sbomfactory.generators.generators.SBOMGenerator;
import org.svip.sbomfactory.generators.parsers.languages.*;
import org.svip.sbomfactory.generators.parsers.packagemanagers.*;
import org.svip.sbomfactory.generators.parsers.contexts.*;
import org.svip.sbomfactory.generators.parsers.Parser;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.svip.sbomfactory.generators.utils.Debug;
import org.svip.sbomfactory.generators.utils.ParserComponent;
import org.svip.sbom.model.SBOM;
import org.svip.sbomfactory.generators.utils.VirtualPath;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.svip.sbomfactory.generators.utils.Debug.LOG_TYPE;
import static org.svip.sbomfactory.generators.utils.Debug.log;

/**
 * <b>File</b>: ParserController.java<br>
 * <b>Description</b>: Central controller class that interfaces between
 * the main driver and the language specific parsers.
 *
 * @author Dylan Mulligan
 * @author Derek Garcia
 */
public class ParserController {

    //#region Attributes
    private final String projectName;
    private String PWD;
    private final String SRC;
    private final SBOM SBOM;
    private final AtomicInteger dirCount;
    private final AtomicInteger fileCount;
    private static final ObjectMapper OM = new ObjectMapper(new JsonFactory()); // ObjectMapper Initialization
    static { OM.setSerializationInclusion(JsonInclude.Include.NON_NULL); } // ObjectMapper Configuration
    private static final HashMap<String, Parser> EXTENSION_MAP = new HashMap<>() {{
        //
        // New Parsers
        //

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
        // ADD NEW PARSER HERE: put("fileExtn", new Parser);
    }};

    //#endregion

    //#region Constructors

    /**
     * Create a new ParserController with a path to the PWD.
     *
     * @param String a Path to the present working directory
     */
    public ParserController(String PWD) {
        // Set attributes
        // Get filename
        final String[] pathParts = PWD.split("/");
        // Set project name to root filename
        this.projectName = pathParts[pathParts.length - 1];
        this.PWD = PWD;
        this.SRC = PWD; // A new ParserController is created in the source directory, this shouldn't need to be changed
        this.dirCount = new AtomicInteger();
        this.fileCount = new AtomicInteger();
//        this.outputFileType = outputFileType;

        // Create new SBOM Object
        ParserComponent headComponent = new ParserComponent(this.projectName);
        this.SBOM = new SBOM(headComponent);
    }

    //#endregion

    //#region Getters

    public String getProjectName() { return this.projectName; }
    public String getPWD() { return this.PWD; }
    public String getSRC() { return this.SRC; }
    public int getDirCount() { return this.dirCount.intValue(); }
    public int getFileCount() { return this.fileCount.intValue(); }
    public int getDepCount() { return this.SBOM.getAllComponents().size(); } // TODO: Direct method in SBOM to count
    public SBOM getSBOM() { return this.SBOM; }

    //#endregion

    //#region Setters

    public void setPWD(String PWD) { this.PWD = PWD; }
    public void incrementDirCounter() { this.dirCount.getAndIncrement(); }

    //#endregion

    //#region Core Methods

    /**
     * Parses the given filepath recursively.
     *
     * @param filepath path to file or root directory to be parsed
     */
    public void parse(String filepath, String fileContents) { parse(this.SBOM.getHeadUUID(), filepath, fileContents); }


    /**
     * Internal parse method that takes a filepath and a parent Component (optionally).
     * If parent is null, this method will parse normally, otherwise, it will try to
     * append all found Components to the given parent Component.
     *
     * @param filepath path to file or root directory to be parsed
     * @param parent parent Component to be appended to
     */
    public void parse(UUID parent, String filepath, String fileContents) {
        // Get filename
        final String[] pathParts = PWD.split("/");
        // Set project name to root filename
        final String filename = pathParts[pathParts.length - 1];

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

        final String parentDir = String.join("/", Arrays.copyOfRange(pathParts, 0, pathParts.length - 1));

        // Set parser details
        parser.setPWD(new VirtualPath(parentDir));
        parser.setSRC(new VirtualPath(this.SRC));

        final ArrayList<ContextParser> contextParsers = new ArrayList<>();
        if(parser instanceof LanguageParser) {
            contextParsers.add(new CommentParser());
            contextParsers.add(new SubprocessParser());
            contextParsers.add(new DeadImportParser());
            // Add new ContextParser
        }

        // Otherwise, parse file
        // Init Component list
        ArrayList<ParserComponent> components = new ArrayList<>();

        // Parse components
        parser.parse(components, fileContents);

        // TODO: Make sure this works
        components.forEach(c -> c.addFile(filepath));

        // If file being parsed is a language file
        if(parser instanceof LanguageParser) // Execute all added ContextParsers
            for(final ContextParser cp : contextParsers) { cp.parse(components, fileContents); }

        // If file being parsed is a package manager file
        if(parser instanceof PackageManagerParser) { // Parsing an EXTERNAL dependency
            components.forEach(ParserComponent::setPackaged); // Sets it to packaged and EXTERNAL
            // TODO check CPEs & modify component with that data
        } // Otherwise it will be unpackaged and INTERNAL (LIBRARY if it has been parsed as such)

        // Add Components to SBOM
        this.SBOM.addComponents(parent, components);
        this.fileCount.getAndIncrement();
    }

    /**
     * Write this core to a dep.yml file
     *
     * @param outPath Path to write file to
     */
    public String toFile(String outPath, GeneratorSchema outSchema, GeneratorSchema.GeneratorFormat outFormat) throws IOException, GeneratorException {
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
        } else {
            String fileString = generator.writeFileToString(outFormat, true);
            log(Debug.LOG_TYPE.INFO, "SBOM String:\n" + fileString);
            return fileString;
        }
        return null;
    }

    //#endregion
}
