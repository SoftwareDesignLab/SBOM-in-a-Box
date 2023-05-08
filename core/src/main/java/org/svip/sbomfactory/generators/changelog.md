# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [v4.3.0-alpha] - (05/08/2023)

### Added

- Add `CPE` Class that generates CPE format strings by adding properties one-at-a-time as they are found.
- Add `SBOMGeneratorTest` Test to begin testing our generators' file-writing capacities.
  - `SBOMGeneratorTest` also currently tests the correct creation of various `BOMStore` Objects.
- Add `writeFileToString()` Method to `SBOMGenerator` to return a generated SBOM with a specified filetype as a string 
  rather than as written to a file.
  - Includes the option to remove whitespace from or pretty-print the string output.
- Add `GradleParserParseTest` & `RequirementsParserParseTest`

### Changed

- Update `POMParser` to build and add CPEs to individual components as they are discovered.
- Update `GradleParser` to properly cover and parse a wide variety of edge cases.
  - Gradle documentation reference provided a fairly comprehensive set of different styles of dependency declaration
  to support.
- Update `PackageMangerParser` (& implementations) to make better use of inheritance when initializing
  - Common fields moved up levels of inheritance, constructors updated to simplify child class creation
  and better delegate the responsibilities of each class
- Update `PackageMangerParser` to handle property resolution
  - Generified code to resolve properties and fixed deep recursive edge cases
    - This allows better access to the properties list that `PackageManagerParser` implementations store, and reduces
    all token replacement code to two methods
    - Any value read in from a `PackageManagerParser` has the potential to be or contain property reference tokens,
    this change makes the process of "resolving" any given value extremely easy and generic.
- Update `CycloneDXSerializer` & `SPDXSerializer` Classes to include discovered CPE data.
- Fix `ParserController`'s `toFile()` test to ensure each file is being generated as expected.
  - Method also changed from type void to type String, giving the method the ability to output both directly to file, 
  as well as return the stringified file contents instead.
    - This allows for our integration with the API, where we are not reading and writing directly to and from files,
    but passing stringified file contents around.
- Fix `GoParser` edge case bugs found during dataset development
- Fix `ParserTestCore` (& implementations) typing
  - Instead of now storing a generic instance of `Parser` and casting
  it to a child class in order to access protected fields, `ParserTestCore` was properly generalized 
  (`ParserTestCore<T extends Parser>`).
- Update `POMParserParseTest` (& all new test files) to include two main tests
  - `testProperties` - Checks multiple properties for correctness
    - Includes recursive property resolution edge cases
  - `testDependencies` - Checks multiple dependencies for correctness
    - Includes recursive dependency resolution edge cases
  - NOTE: These may end up being broken into more granular tests

### Removed
- Remove `CPEQueryWorker` Class as no API requests need to be made. 
- Remove `UNKNOWN` Type from `ParserComponent` as it doesn't make sense to default to.


## [v4.2.0-alpha] - (05/01/2023)

### Added

- Add `CycloneDXStore` & `CycloneDXSerializer` Classes
  - The `CycloneDXStore` class stores all components and metadata associated with a CDX BOM.
  - The `CycloneDXSerializer` class can serialize an instance of `CycloneDXStore` to a CDX output file, using a custom 
    implementation of a Jackson JSON serializer
- Add `Tool` Class
  - A simple dataclass that allows storage of information about our generator tool.
- Add `License` Class
  - Contains methods to store a license string and its SPDX short identifier, parsed using the now-static
    `LicenseManager` Class.
  - Replaced storage via strings in all stores, serializers, and `Tool`.
- Refactored common functionality from `SPDXStore` & `CycloneDXStore` into a `BOMStore` Class.
- Add `version`, `internalType`, & `bomStoreType` fields to `GeneratorSchema` Class.
  - This allows us to construct an `SBOMGenerator` with only an SBOM object and GeneratorSchema.

### Changed

- Refactored SPDX-specific class names into generic class names to promote semantics.
  - `SPDXRelationship` -> `Relationship`
  - `SPDXLicenseManager` -> `LicenseManager`
- Refactored `SPDXStoreException` Class into `GeneratorException` Class to use between different generators.
- Create `generators.utils` package, moved the following classes in:
  - `GeneratorException`, `GeneratorSchema`, `License`, `LicenseManager`, `Tool`
- `LicenseManager` Class is now static and queries the SPDX license URL only once when first referenced.
- `SBOMGenerator` Class is no longer abstract and contains all methods to construct a `BOMStore` instance.
- `ParserComponent` Class now stores multiple `License`'s and all files analyzed when constructing.
- `ParserComponent` Class now contains a `generateHash()` method to obtain the checksum of a component.

### Removed
- `Java_SPDX_old.json` as we now implement more fields than the previous implementation that used `spdx-java-library`.
- `SPDXGenerator` & `CycloneDXGenerator` Class
  - All functionality has been moved into `SBOMGenerator`

## [v4.1.1-alpha] - (04/21/2023)

### Added

- Added `SPDXStore`, `SPDXStoreSerializer`, & `SPDXStoreException` Classes
  - The `SPDXStore` class holds responsibility for storing all data and relationships required by an SPDX document.
  - It can then be serialized to a file using the `SPDXStoreSerializer`, which is a custom implementation of a JSON
    serializer from the Jackson library.
  - The `SPDXStoreException` class is a useful exception to allow us to throw and catch any inconsistencies or errors
    while constructing the SPDX document. This also allows us to log more verbose details.
- Added `SPDXRelationship` Class
  - Holds the data of a single SPDX relationship between packages.
  - Defines allowed relationships between packages.
- Added `SPDXLicenseManager` & `SPDXLicenseQueryWorker` Classes
  - These two classes work together to first query the page of the SPDX website that describes what licenses are allowed
    and what format they need to be in. 
  - Then, the license manager holds that data and allows us to test to see if a
    parsed license of a package is a valid string and if it can be inserted into the package information.
    - The license manager can also assume the license based on a tokenized string and a version if the license is not 
      recognized.

### Changed

- The `SPDXGenerator` class now uses an instance of `SPDXStore` and a Jackson `ObjectMapper` to write an SPDX document
  to a file.
- The `hash()` method in the `SBOMGenerator` class has been removed as an abstract method and replaced by the
  `getHash()` method that generates a unique SHA-256 hash of each generator instance.
  - `getHash()` is now used to generate an SBOM's serial number when one is passed into `SBOMGenerator`.
- The `GeneratorFormat` enumeration now contains a Jackson `ObjectMapper` field to allow file type selection.
  - This allows us to pass in a `GeneratorFormat` to the `SBOMGenerator.writeFile()` method and automatically generate
    an `ObjectMapper` for each unique file format.

### Removed
- `DependencyTreeSerializer` as it has been deprecated by the `SPDXStoreSerializer` class.

## [v4.1.0-alpha] - (04/21/2023)

### Added

- Added `GeneratorSchema` & `GeneratorFormat` Enumerations
  - These enums are tightly coupled to each other and the generators to ensure
  proper schema/format validation while allowing the user full control over the output file.
- Added `LanguageParser extends Parser` Class
  - Holds the language parsing logic previously accounted for by `Parser`
- Added abstract `ParserTestCore` Class
  - Holds the general testing logic for our testing structure
- Added abstract `ParseDepFileTestCore` Class
  - Holds the logic relating to testing the package manager data that is parsed from generated dependency files.
- Added `POMParserParseTest` Class
  - Stubbed out an implementation of `ParseDepFileTestCore` and began planning the testing structure for 
  `PackageManagerParser`s.
- Added abstract `QueryWorker` Class
  - Runnable implementation that queries a given url and stores the information within a given component.
    - The specific information storage is done by implementations of this class, which will have context on
    the information being queried.
  - `PackageManagerParser.queryURLs()` allows for execution of any number of any class that extend `QueryWorker`
  to be run in parallel via an `ExecuterService` and a `CachedThreadPool`
    - Slow package manager index query times would increase time to parse some files more than 20x over, this fixes
    the speed issue, at the risk of hitting rate limits if we cannot find a specific index site without one.
- Added `POMQueryWorker`, `CPEQueryWorker` & `RequirementsQueryWorker` Classes
  - These query their own respective package indexes and store found relevant information within `ParserComponent`s.
- Added `RequirementsParser`
  - Implemented to read in pip requirements files for dependency information
- Added `GradleParser` & `CSProjParser`
  - Stubbed out

### Changed

- `Parser` Class is now responsible for **only** general parsing logic, while abstract children will hold greater 
categorical logic, to be further implemented in the form of the individual parsers
  - What this means is that a `LanguageParser` can be treated very similarly to a `PackageManagerParser`, and any new
  types of parsers that are added. The plan is to expand this system to include an abstract `ContextParser` (name pending).
  - This abstraction will allow for easy combination of data (in any way we want) within `ParserController`.
- `ParseRegexTestCore` Class modified to **only** be responsible for logic relating to testing the regex of the language
parsers.
- `POMParser` Class collects package license info and pom.xml properties (local variables)
  - This allows us to replace values like this: `${java_version}` with values like this: `1.8` when storing the information
    - Work still needs to be done to handle recursive variable referencing, i.e. a property references another property,
    to the n-th level. This code is mostly written, but it is not functional currently

## [v4.0.0-alpha] - (04/14/2023)

### Added

- Added `SBOMGenerator` Class
  - Base abstract class for our suite of generators
  - Added `FORMAT` Enum
    - Holds valid filetypes, eventually this will also validate that any given filetype is valid for the specified 
    schema (e.x. we do not want an instance of `CDXGenerator` writing to `.spdx`)
- Added `CDXGenerator extends SBOMGenerator` Class
  - This file implements our base generator class, and serves solely to perform CycloneDX-specific data manipulation and
  write to file. We make use of CycloneDX's open-source, officially supported library `cyclonedx-core-java` to convert our internal
    objects to theirs, then are able to
  use their `BomGeneratorFactory` to format the data into stringified xml/json
    - We plan to write custom serializers to replace the usage of these OS libraries, however, this was a very simple
    solution that allowed us to output SBOMs quickly, and refine our generators right away.
- Added work-in-progress `SPDXGenerator extends SBOMGenerator` Class
  - This file will also implement the base generator class and perform SPDX-specific data manipulation and file 
    writing. This makes use of SPDX's open-source, officially supported library: `spdx-java-library`. We will be 
    able to use their internal representation of documents and packages to convert our objects to theirs and then 
    write the resulting SBOM to a file.
- Added `PackageManagerParser extends Parser` Class
  - This new type of parser aims to build on existing functionality around dependency parsing, while also expanding the
  range of data we can parse from only language files to also include managed dependency files.
- Added `POMParser extends PackageManagerParser` Class
  - This implementation of PackageManagerParser was a spike into the value of including information gathered from
  managed dependency files / package managers, specifically, `pom.xml` files.
  - We were able to gather relevant dependency information, akin to what we are parsing from language files, however,
  often with more information on each dependency (enough to build a PURL)
- Temporarily added copies of SVIP->`SBOM`/`DependencyTree`/`Component`/etc. for data class merge
  - This will be pulled from a single source in the future, but for now, they are just development artifacts

### Changed

- System now uses `SBOM` Objects from `SVIP` when collecting information
- `Component` Class -> `ParserComponent extends SVIP.Component` Class
  - This allows for seamless use of existing functionality regarding SBOM/DependencyTree/Component/etc. within our system
- System now writes to `CycloneDX SBOM` format instead of "depFile" format, with a modular design meant to support 
more SBOM formats and schemas
  - Both `JSON` and `XML` formats are currently supported

### Removed

- All deprecated parser files removed
  - Old language implementations
  - Old tests
  - Old data classes
- Filter feature removed



## [v3.0.1-alpha] - (04/05/2023)

### Added

- `SRC` Property to `ParserCore` and `Parser` to store a static reference to project src.
  - Previously, `PWD` was being used as both a moving directory pointer, as well as the
  static project reference. Obviously, this caused some wierd issues, and is now fixed.

### Fixed

- All deprecated parser classes updated (as well as their tests)
- Parent Component selection was occurring for each file (of the new project) during an append,
it now only happens once, as intended.
  - This was not apparent until a conflicting name was chosen, and a source project was appended.
- Uniqueness checking fixed once and for all (until it breaks)
  - On a more serious note, this is now in a relatively stable place after the big rework,
  and it's a miracle that it worked as well as it did out of the box, and will now (hopefully)
  function flawlessly until its use case changes.
- `PythonParser.isLanguageComponent()` URL structure fixed to properly query multipart URLs
  - e.x. "foo/bar" -> "foo.bar.html"
- Fixed bug when appending absolute paths instead of relative paths

### Changed

- `DepFileObject` Class modified to allow for short representations of filters.
  - i.e. `-f="external, internal"` -> `-f="e, i"`
  - Both are valid, and the quotes can be removed if the spaces between filters are removed.

### Removed



## [v3.0.0-alpha] - (03/24/2023)

### Added

- `ParserController` Class added to house the logic in-between the Main file 
driver and the language-specific parsers. Previously, this logic was bloating 
`ParserCore`.
- `Parser` Class added to house the code responsible for being the core of the
language parsers. This includes the code previously assigned to `ParserCore`.
- `DepFileObject` Class added to represent a collection of found Components. 
This Class is meant to house the logic and data related to that collection.
Previously, this logic was also handled in `ParserCore`.
- `Debug` Class added to house static methods used for debugging, the primary
use of this class is the `log` method.

### Changed

- `Main` Class reworked to perform **only** argument handling and file driving.
  - Argument handling is complex enough to warrant its own Class/File/Subsystem,
  and should be separated into one when possible.
  - Required arguments changed: 
    - `parser.jar`: `targetPath language <optArgs>` -> `targetPath <optArgs>`
- `Component` Class reworked into a dedicated `Component.java` file instead of 
as an internal class of `ParserCore`.
- `Append` feature reworked
  - Usage changed: 
    - `parser.jar`: `oldPath -a=newPath:parentCName` -> `oldPath:parentCName -a=newPath`
  - Appending now functions in logical order, parse oldPath -> verify parent 
  Component exists and acquire it -> parse newPath -> append to parent.
- `PythonParser` Class reworked to function with the new system design.
- `CSharpParser` Class reworked to function with the new system design.
- `CParser` Class reworked to function with the new system design.
- `CPPParser` Class reworked to function with the new system design.
- `PythonParserRegexTest` Class reworked to function with the new system design.
- `PythonParserRegexBlockCommentTest` Class reworked to function with the new system design.
- `PythonParserRegexLineCommentTest` Class reworked to function with the new system design.
- `CSharpParserRegexTest` Class reworked to function with the new system design.
- `CParserRegexTest` Class reworked to function with the new system design.
- `CppParserRegexTest` Class reworked to function with the new system design.

### Removed

- `ParserCore` Class, and its implementations, are now deprecated. Its responsibilities have 
been delegated  accordingly and its implementations can and will be updated with relative ease.