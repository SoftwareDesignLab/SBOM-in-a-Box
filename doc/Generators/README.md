# Generators
> System that collects information from both source and package manager files and generates an SBOM, to be outputted
to either the CycloneDX or SPDX schema, and the XML, JSON, or YAML format.

# Last Changelog Update

## [v4.5.0-alpha] - (5/24/2023)

This update focuses on caching the filesystem at program start via the `VirtualTree` implementation and using this to
enable fixing all unit tests.

**ALL UNIT TESTS ARE NOW PASSING**

### Added
- `VirtualTree` implementation. This covers the following classes:
    - `VirtualNode` - Represents a node in a `VirtualTree`. This is either a file or directory. If a VirtualNode is a file,
      it contains the contents of the file internally. It uses `VirtualPaths` to store the name of each file/directory, and
      can then be concatenated recursively by `VirtualTree` to retrieve the whole filepath.
    - `VirtualTree` - A complete, internal, in-memory representation of any file tree. A VirtualTree is constructed by
      adding a several file paths, and an internal structure of VirtualNodes is created to represent each directory and
      file. It also stores the file contents, and can return a list of all files in the file tree.
- Unit tests for `VirtualPath`, `VirtualNode`, and `VirtualTree`.
- Added `ParserController.parseAll()` method to parse all files in the internal `VirtualTree`, as well as cache the list
  of files to pass into `parse()`.
- Added `Parser.setInternalFiles()` method to pass in a `List<VirtualNode>` of internal files to the `internalFiles`
  field to use when checking internal files.
    - This is derived from the `VirtualTree.getAllFiles()` method.

### Changed
- `ParserController` now holds a `VirtualTree` representation of an arbitrary filesystem on construction.
    - Now, each file can be parsed by looping through all files in the `VirtualTree` and parsing their contents,
      completely in-memory.
    - `parse()` now accepts an additional parameter `internalFiles` to pass into each parser, regenerated once per call to
      `parseAll()` from the `VirtualTree` representation.
- `LanguageParser.isInternalComponent()` refactored to use the `internalFiles` field instead of using `Files.walk()`.
- Moved all utilities in `sbomfactory.generators` to a single `utils` package with organized sub-packages.
- Renamed `GeneratorsTestMain` to `SBOMGeneratorCLI` and moved it to the `svip` package along with the other main classes.
- Changed `SBOMGeneratorCLI` to use the `VirtualTree.buildTree()` static method to read all files and file contents from
  a source directory into a `VirtualTree`, which can then be passed into a `ParserController` instance.
- Updated `OSITest` to check if Docker is running, and if not simply ignore the tests.


## Usage
> For running locally, see **Building Project Locally**

How to run: `<targetPath> <optionalArgs...>`

### Examples:
- Display help prompt: `-h`
- Basic: `MyProject/src`
- Debug: 
  - `MyProject/src -d`
  - `-d MyProject/src`
  - `MyProject/src -d`
- Summary: `MyProject/src -s`
- Debug (Overrides Summary): `MyProject/src -d -s`
#### CycloneDX
- `MyProject/src -d`
  - Outputs a JSON CycloneDX SBOM file with debug mode activated
- `MyProject/src -d -o=CycloneDX -f=JSON`
  - Outputs a JSON CycloneDX SBOM file with debug mode activated
- `MyProject/src -o=CycloneDX -f=XML`
  - Outputs an XML CycloneDX SBOM file without debug mode
#### SPDX
- `MyProject/src -d -o=SPDX`
  - Outputs a JSON SPDX SBOM file with debug mode activated
- `MyProject/src -o=SPDX -f=YAML`
  - Outputs an XML SPDX SBOM file without debug mode

### Required Arguments (Ordered)
- `targetPath`: Path to a target file or root directory to parse.

If these arguments are invalid or missing, a CLI script will run to get the correct input.

### Optional Arguments (Unordered, can even be mixed with Required Arguments)
Use these in any combination or order, nothing will conflict unless otherwise noted.

The driver has several optional boolean flags:
- `-d`: Debug Mode; Enable debug messages to be printed to the stdout
- `-h`: Display command line usages
- `-s`: Summary Mode; Disable ALL messages from tool
> NOTE: Debug mode will override Summary mode (safe conflict)

It also has several optional `'key=value'` flags:
- `-o=specification`: Output specification; Select a supported SBOM specification to output to
    - Supported Formats:
        - `CycloneDX`
        - `SPDX`
> NOTE: Output specification defaults to CycloneDX if not specified

- `-f=format`: Output format; Select a supported format to output to
   - Supported Formats:
      - `json`
      - `xml`
      - `yaml`
> NOTE: Output format defaults to JSON if not specified

> NOTE: If more than one component shares the same `componentName`, the user
> is given a choice between all matches.

## Supported Project File Formats:
This list of formats represents all given files that can be parsed, if the extension is not on this list,
the file will not be parsed.
- `Language Files`
  - `go`
  - `rust`
  - `ruby`
  - `perl`
  - `scala`
  - `java`
  - `js/ts`
  - `python`
  - `c#`
  - `c++`
  - `c`
- `Package Manager Files`
  - `pom.xml`
  - `build.gradle`
  - `.csproj`

## Building Project Locally
This installation is through IntelliJ and builds the project into a JAR through IntelliJ's configuration files.
1. Clone repo
2. `File` > `Project Structure` > `Project`
    - Choose SDK (OpenJDK 19 or similar)
    - `APPLY`
3. `Project Structure` > `Modules` > `Source` > Navigate to `SVIP`
    - Select `core/src/main` and click `Mark as: Sources`.
    - Select `core/src/test` and click `Mark as: Tests`.
    - Select `core/src/test/java/org/svip/sbomfactory/generators/TestData` and click `Mark as: Test Resources`.
    - The `target` folder should already be marked as `Excluded`
    - `APPLY`
4. `Project Structure` > `Modules` > `Dependencies` > `+` > `From Maven` and import the following
    - `com.fasterxml.jackson.svip:jackson-databind:2.13.3`
    - `com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.14.01`
    - `com.google.guava:guava:31.1-jre`
    - `com.googlecode.json-simple:json-simple:1.1.11`
    - `org.junit.jupiter:junit-jupiter:5.9.1`
    - `APPLY`
> NOTE: Once Installed the scope should all be set to `Compile`
5. To configure a build for a JAR artifact:
    1. `File` > `Project Settings` > `Artifacts`
    2. Click the plus sign in the top left and select `JAR` > `From modules with dependencies...`
    3. Set `Module` to `SVIP.core.main`
    4. Set `Main Class` to `SBOMGeneratorCLI (org.svip.sbomfactory.generators`
    5. Select the `copy to the output directory and link via manifest` option under `JAR files from libraries`
6. To build the JAR artifact:
    - `Build` > `Artifacts`
    - The JAR artifact will appear in the default directory specifed (`SVIP\out\artifacts\SVIP_core_main_jar`)

## Structure
The project is structured around an Abstract Parser Core, which handles basic file recursion and other generics. From
the svip, Language-Specific parsers can be created that can handle the details of the language.

![uml](uml.png)
[_Edit the Document Here_](https://drive.google.com/file/d/1RKeNBU7_Qosw1GvXTrkQyCOQ2ea-JcXX/view?usp=share_link)

### Abstract Methods
The following are a list of abstract methods to be implemented by each Language-Specific parser.

    boolean isInternalComponent(Component component);
    boolean isLanguageComponent(Component component);
    Pattern getRegex();
    Component parseRegexMatch(String match);
See the [abstract method](src/main/java/parsers/ParserCore.java) implementation of the ParserCore for more details.

## Creating New Parsers
In order to create a new Parser, follow these steps.
1. Create new Parser class that extends `ParserCore`
2. Add the new language to `SUPPORTED_LANGUAGES` in `Main.java`

   > ```put("language", new parser());```
3. Implement Abstract Methods
> NOTE: For `isLanguageComponent`, the current practice is to use an HTTP request to the official documentation to test
> to see if the package is from the Language maintainers

### Logging
The ParserCore has a number of logging options to use. Options include `INFO`, `DEBUG`, `WARN`, `ERROR`, and `EXCEPTION`.
These are not required but are available for use when creating new Parsers.


## Contributors
- [Tyler Drake](mailto:txd3634@rit.edu)
- [Ian Dunn](mailto:itd3516@g.rit.edu)
- [Derek Garcia](mailto:dlg1206@rit.edu)
- [Asa Horn](mailto:aoh9470@rit.edu)
- [Henry Keena](mailto:htk4363@rit.edu)
- [Ping Liu](mailto:htk4363@rit.edu)
- [Dylan Mulligan](mailto:dtm5568@rit.edu)
- [Henry Orsagh](mailto:hco4630@rit.edu)
- [Juan Francisco Patino](mailto:jfp6815@rit.edu)