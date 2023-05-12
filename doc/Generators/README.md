# Generators
> System that collects information from both source and package manager files and generates an SBOM, to be outputted
to either the CycloneDX or SPDX schema, and the XML, JSON, or YAML format.

# Last Changelog Update

## [v4.3.2-alpha] - (05/12/2023)

### Added

- Add `LicenseManager.getConcatenatedLicenseString()` method to concisely concatenate all tool licenses to use when
  representing a Collection of licenses as a string.

### Changed

- Ensure `SPDXSerializer` Class properly serializes an XML Document according to the documentation and
  [official example](https://github.com/spdx/spdx-spec/blob/development/v2.3.1/examples/SPDXXMLExample-v2.3.spdx.xml).
- Update `SPDXSerializer` Class to ensure the XML root element is `Document` when serializing using an instance of
  `ToXmlGenerator`.

> See the full [changelog](changelog.md) for more information regarding recent changes.


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
This installation is through Intellij and runs the project through Intellij's configuration files
1. Clone repo
2. `File` > `Project Structure` > `Project`
   - Choose SDK (Openjdk 19 or similar)
   - Make sure Compiler output is set. (`BenchmarkParser/target`)
   - `APPLY`
3. `Project Structure` > `Modules` > `Source` > Navigate to `BenchmarkParser`
   - Select `src` and click `Mark as: Sources`.
   - Select `test` and click `Mark as: Tests`.
   - Select `TestData` and click `Mark as: Test Resources`.
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
5. Building and running Main.java should work
6. Running tests should fail
   - After running, set click the config drop down (in between build and run) > `edit configurations`
   - change `Working directory` to `BenchmarkParser` and re-run
> NOTE: If running program doesn't find necessary files the above is most likely the issue

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
- Derek Garcia
- Dylan Mulligan