# Changelog

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [v7.2.4-alpha] - (8/14/2023)

### Changed
- Fixed a bug so deserializers can now handle additional missing data

## [v7.2.2-alpha] - (8/3/2023)

### Added
- `MockMultipartFile` for testing uploading binaries
- `ParserController` null check
- `SBOMFileIdentifierGenerator` ID generator class implementing JPA's IdentifierGenerator for `SBOMFile.id`

### Changed
- `/generators/parsers` successfully takes in binary zip files of projects and generates an SBOM
    - passes Postman tests and `GenerateFromParserAPITest`
- `/generators/osi` should take in binary zip files of projects and generates an SBOM
    - passes Postman tests and `GenerateFromOSIAPITest`
- Maximum file upload and request size to 2GB in `application.properties`

## [v7.2.1-alpha] - (8/3/2023)

### Added
- Improved Diff Report readability
- SBOM Objects have built in toString methods
- `/metrics/resultFactory/enumerations/INFO` now has info for diff reports
- `/metrics/resultFactory/Text` now has outputs for getting diff report messages
- `/compare/conflicts/ConflictFactory` has been reworked to avoid using plain text, instead relying on `Text` class

### Changed
- Fix incorrect OSI filepaths not allowing API to build.

## [v7.2.0-alpha] - (8/1/2023)

### Changed
- Overhaul directory structure to be feature focused

## [v7.1.2-alpha] - (7/28/2023)

### Added
- SBOM Objects have built in comparison methods
- Added `hashcode` methods to components to use `name` and `version` as UIDs

## [v7.1.1-alpha] - (7/26/2023)

### Changed
- `PURLTest.java` and `CPETest.java` updated:
  - Provides results for each field tested in accuracy test instead of a single result
- Added two new types of `INFO.java`: `MATCHING` and `NOT_MATCHING`
- Updated QA Results to provide better and more useful information
- `ResultFactory` is built for each method-test
- Endpoints in `SVIPAPiController.java` to match documentation
- `/generators/parsers` unzips a zipped project and parses it into an SBOM
- `/sboms/content` returns the entire SBOMFile instead of just file contents

## [v7.1.0-alpha] - (7/25/2023)

### Added
- VEX API Endpoint
- VEX API test `GenerateVEXAPITest`
- `VEXResult.java`
  - Class that holds both the VEX Object and any errors that occurred for the API endpoint
- Test SBOMs that contain vulnerable components
- `/svip/generators/osi` endpoint to the `SVIPAPIController` class
- `GenerateFromOSIAPITest` class that contains unit tests for the OSI endpoint

### Changed
- Updated `API.md` documentation with the VEX endpoint
- Updated `GenerateFromParserAPITest` class to be consistent with `GenerateFromOSIAPITest` class
- Cleaned up utilities in `api.utils` and moved some methods into their respective core classes as there was some
  duplicate code.
- Fixed a bug where running OSI in the API would create bind directories in the API package instead of the core package.


## [v7.0.1-alpha] - (7/24/2023)

### Changed
- `/sboms` to `/sbom` when getting SBOM object

## [v7.0.0-alpha] - (7/21/2023)
> OSI Refactor Update (Docker build steps updated)

### Added
- `osi.scripts` package
  - `setup.sh` called from the inline Dockerfile to install all SBOM utilities to the image.
  - `ContainerController.py` - moved from `osi` package.
- `DockerNotAvailableExceptionTest` unit tests to increase code coverage to >80%.
- Methods to `OSI` class resulting in a higher abstraction level above Docker and the filesystem:
  - `addSourceFile()` - Add a single source file to be processed
  - `addSourceDirectory()` - Copy the contents of an entire directory to be processed (for unit tests)
  - `generateSBOMs()` - Runs the container, clears directories, and returns a Map of all SBOM files

### Changed
- Updated `docker-compose.yml` to contain all setup and service details for OSI
- `OSITest` unit tests
- `SVIPAPIController` now attempts to construct an `OSI` instance on startup if a constructor flag is enabled, 
  Docker is running, and an image/container exists.
  - The constructor flag allows us to disable OSI construction for unit tests as well as the OSI endpoint itself.

### Removed
- `osi/Dockerfile` as all setup behavior is taken care of in the docker-compose file and the OSI class.

## [v6.0.0-alpha] - (7/20/2023)
> **Endpoint Standardization**

### Changed
- Updated endpoints to a REST-ful standard
    - **Upload SBOM**
        - prev: [get] http://localhost:8080/svip/upload
        - new: [post] http://localhost:8080/svip/sboms

    - **Get SBOM content by id**
        - prev: [get] http://localhost:8080/svip/view
        - new: [get]  http://localhost:8080/svip/sboms/content

    - **Get SBOMs**
        - prev: [get] http://localhost:8080/svip/viewFiles
        - new:[get] http://localhost:8080/svip/sboms
      > note: /getSBOM would instead use the /sboms endpoint and instead an id parameter should be used.
      >
      > EX: [get] http://localhost:8080/svip/sboms?id={id}

    - **Delete SBOM by id**
        - prev: [delete] http://localhost:8080/svip/delete
        - new: [delete] http://localhost:8080/svip/sboms?id={id}

    - **Merge SBOMs**
        - prev: [post] http://localhost:8080/svip/merge
        - new: [post]  http://localhost:8080/svip/sboms/merge

    - **Convert SBOM by id**
        - prev: [get] http://localhost:8080/svip/convert
        - new: [put]  http://localhost:8080/svip/sboms

    - **Merge SBOMs**
        - prev: [post] http://localhost:8080/svip/merge
        - new: [post]  http://localhost:8080/svip/sboms/merge

    - **Generate SBOMs with SVIP**
        - prev: [post] http://localhost:8080/svip/generate
        - new: [post]  http://localhost:8080/svip/sboms/generate

    - **Grade SBOMs**
        - prev: [get] http://localhost:8080/svip/qa
        - new: [get]  http://localhost:8080/svip/sboms/qa

- Updated `API.md` documentation with the updated endpoints

## [v5.4.0-alpha] - (7/19/2023)

### Added
- Merge API Endpoint
- Merge API test `MergeFromAPITest.java`
- `MergerCrossSchema.java`
  - Allows merging two SBOMs regardless of origin format
- Utility classes for Merger
  - `MergerUtils.java`
  - `comparison/utils/Utils.java`
- New NVD API Client implementation:
    - `NVDClient` - Class that, when given an SBOM, will create a new VEX Document object with the NVD API database

### Changed
- `Merger.java`abstract class and overall architecture

## [v5.3.0-alpha] - (7/19/2023)

### Added
- QA API Endpoint

### Changed
- `SBOMBuilders` instantiated with empty sets and hashmaps instead of null values
- `.buildAndFlush()` methods now clear sets/hashmaps instead of setting to null

## [v5.2.0-alpha] - (7/19/2023)

### Added
- `/generators/parsers` endpoint in `SVIPApiController`.
- Relevant unit tests in `GenerateFromParserAPITest`:
  - `sbomFilesNullPropertiesTest`
    - Asserts a 'bad request' is returned.
  - `CDXTagValueTest`
    - Asserts a 'bad request' is returned.
  - `generateTest`
    - Comprehensive test to ensure generation from parsers works in the API.

### Changed
- `APITest.java`:
  - Is scalable for adding sample projects for future generator tests.

## [v5.1.2-alpha] - (7/18/2023)

### Added
- Utility classes to merge SBOMs together:
  - `Merger` - Core merger class.
  - `MergerCDX` - Merger for CDX14 SBOMs.
  - `MergerController` - Controller to handle all SBOM merging.
  - `MergerException` - Exception thrown for merge errors.
  - `MergerSPDX` - Merger for SPDX23 SBOMs.
  - `MergerSVIP` - Merger for SVIP SBOMs.
- `MergerTest` class for unit testing.

### Removed
- Old `Merger` Class & Test

## [v5.1.1-alpha] - (7/18/2023)

### Changed
- Organized all builder interfaces and objects into `sbom.builder` package.
- Organized all factory interfaces and objects into `sbom.model` package.

### Removed
- `translators` package as the translators have been deprecated by the deserializers
- Legacy endpoint API unit tests

## [v5.1.0-alpha] - (7/18/2023)

### Added
- /convert endpoint
- Relevant unit tests in `ConvertFromAPITest`
    - `invalidSchemaAndFormatTest()`
    - `CDXTagValueTest()`
    - `convertTest()`
- Serializer + deserializer fixes
### Changed
- Moved conversion functionality from `Utils.java` to `Converter.java`

## [v5.0.5-alpha] - (7/18/2023)

### Added
> NOTE: Serializers may serialize null fields; this should be fixed in a later version.
- Refactored `parsers` package to use the new `SVIPSBOM` & `SVIPComponentObject`.
  - Refactored all parser unit tests to reflect this (100% passing, code coverage TBD).
- Refactored `SBOMGeneratorCLI` class to use `serializers` & `parsers` packages.
- New OSV API Client implementation:
  - `OSVClient` - Class that, when given an SBOM, will create a new VEX Document object with the OSV API database

### Changed
- Moved `Debug`, `QueryWorker`, & `VirtualPath` classes to the base `utils` package.

### Removed
- `Resolver.java` as the new API endpoints don't take string file arguments anymore.
- `generators` package as it has now been fully replaced by the `serializers` & `parsers` packages.
- `translators` unit tests as the translators have been deprecated by the deserializers and will be removed once the 
  API endpoints have been refactored.


## [v5.0.4-alpha] - (7/13/2023)

### Added
- All unit tests for the `serializers` package at ~91% method code coverage.
- New Metrics refactor to test SBOM and component fields:
    - `ATTRIBUTE` - Enumeration  of all possible test attributes
    - `MetricTest` - Abstract class that templates all tests (Except EmptyOrNull)
      - `CPETest` - Class that holds all tests for CPEs
      - `HashTest` - Class that holds all tests for Hashes
      - `LicenseTest` - Class that holds all tests for licenses
      - `PURLTest` - Class that holds all tests for PURLs
    - `EmptyOrNullTest`
    - `QualityReport` - Class to report all results through the pipelines
    - `QAPipeline` - Generic interface for all pipelines
    - `CDX14Tests` - Interface that hold CycloneDX 1.4 specific tests
    - `SPDX23Tests` - Interface that holds SPDX 2.3 specific tests 
    - `CDX14Pipeline` - Class that runs through all tests for a CycloneDX 1.4 sbom and components
    - `SPDX23Pipeline` - Class that runs through all tests for a SPDX 2.3 sbom and components
    - `SVIPPipeline` - Class that runs through all tests for an SVIP sbom and components
- New Result class to output tests results:
  - `ResultFactory` - Class that helps create new Results
  - `Result` - Class that holds the basic information of a result
  - `Text` - Class that helps provide messages and details for Results
  - `INFO` - Enumeration that holds all possible info for a test
  - `STATUS` - Enumeration that holds all statuses of a result
- Unit tests to be completed for each test, pipeline, and ResultFactory

## [v5.0.3-alpha] - (7/12/2023)

### Added
- New Serializer refactor and implementation of the old generator serializers to use the SBOM object:
    - `SerializerFactory` Class - Responsible for recieving parameters and constructing/configuring
      serializers/deserializers
    - `Serializer` Interface
        - `CDX14JSONSerializer` Class - CycloneDX v1.4 JSON serializer
        - `SPDX23JSONSerializer` Class - SPDX v2.3 JSON serializer
        - `SPDX23TagValueSerializer` Class - SPDX v2.3 Tag-Value (.spdx) serializer
        - `SVIPSBOMJSONSerializer` Class - Data serializer to represent all fields in an SVIP SBOM object instance
    - `Deserializer` Interface
        - `CDX14JSONDeserializer` Class - CycloneDX v1.4 JSON deserializer
        - `SPDX23JSONDeserializer` Class - SPDX v2.3 JSON deserializer
        - `SPDX23TagValueDeserializer` Class - SPDX v2.3 Tag-Value (.spdx) deserializer

## [v5.0.2-alpha] - (7/12/2023)

### Added
- New VEX and VEXStatement Implementation. This covers the following files:
    - `VEX` - Class that build a VEX Document object
    - `VEXType` - An enumeration of the possible VEX file types
    - `VEXStatement` - Class for a single VEX Statement in a VEX file
    - `Product` - Class that defines a product inside a VEX Statement
    - `Vulnerability` - Class that defines the vulnerability for a VEX Statement
    - `Status` - Class that defines the status of a VEX Statement
    - `VulnStatus` -  An enumeration for all the possible statuses of a VEX Statement
    - `Justification` - An enumeration of all possible justifications for a NOT_AFFECTED VEX Statement

### Changed

### Removed

## [v5.0.2-alpha] - (7/12/2023)

### Added
- Unit tests for SBOM and Component Builders.

### Changed
- `BuildAndFlush()` methods now call their base `build()` method rather than using duplicate code.
- Fixed a bug where `SVIPSBOM.Build()` was returning an `SBOM` rather than an `SVIPSBOM`.
- Patched various methods that did not check if a list was null before trying to access it.
        

## [v5.0.2-alpha] - (7/12/2023)

### Added
- Unit tests for SBOM and Component Builders.

### Changed
- `BuildAndFlush()` methods now call their base `build()` method rather than using duplicate code.
- Fixed a bug where `SVIPSBOM.Build()` was returning an `SBOM` rather than an `SVIPSBOM`.
- Patched various methods that did not check if a list was null before trying to access it.
        

## [v5.0.1-alpha] - (7/3/2023)

### Added
- New SBOM Refactor and Implementation. This covers the following files: 
    - `SBOM` - An interface that covers SBOM information that is similar to both CycloneDX and SPDX formats
    - `CDX14Schema` - An interface that covers SBOM information specific to CycloneDX 1.4 SBOMs
    - `SPDX23Schema` - An interface that covers SBOM information specific to SPDX 2.3 SBOMs
    - `CDX14SBOM` - A class that is used to file for CycloneDX 1.4 SBOM information
    - `SVIPSBOM` - A class that is used to file for SVIP SBOM Generation
    - `SPDX23SBOM` - A class that is used to file for SPDX 2.3 SBOM information
    - `Relationship` - A class that holds relationship information between SBOM components
    - `ExternalReferences` - A class that holds external reference information for an SBOM
  - New Component Refactor and Implementation. This covers the following files:
    - `Component` - An interface that holds shared component data regardless of SBOM format
    - `LicenseCollection` - A class that holds the licenses of the component
    - `SPDX23Component` - An interface that covers SPDX 2.3 specific package/file information
    - `SPDX23File` - An interface that covers SPDX 2.3 file specific information
    - `SPDX23Package` - An interface that covers SPDX 2.3 package specific information
    - `SBOMPackage` - An interface that covers SPDX 2.3 and CycloneDX 1.4 package information
    - `Description` - A class that holds a package's description
    - `SPDX23FileObject` - A class that is used to file an SPDX 2.3 File
    - `SPDX23PackageObject` - A class that is used to file for an SPDX 2.3 package
    - `SVIPComponentObject` - A class that is used to file for an SVIP component
    - `CDX14ComponentObject` - A class that is used to file for a CycloneDX 1.4 component

### Changed

### Removed


## [v5.0.0-alpha] - (6/29/2023)

### Added
- `Dockerfile` & `docker-compose.yml` to build the API and run a MySQL server with persistent storage in separate 
  Docker containers.
  - `application.properties` & `.env` files created to setup Spring and Docker configuration.
- `repository.SBOMFileRepository` Class to interact with the MySQL database.
- New API endpoints (`upload`, `view`, `viewAll`, `delete`) to run CRUD operations on the `files` table.
  - Added unit tests that mock the `repository.SBOMFileRepository` to avoid any local storage during testing.

### Changed
- Refactored `NVIPApiController`, `PlugFestApiController`, & `NVIPApiController` Classes into `controller` package.
- Refactored `utils.Utils.SBOMFile` into its own class `model.SBOMFile`
  - Uses Hibernate decorators to automatically create a custom `files` table on the MySQL server.

### Removed
- Old API endpoints & tests (`compare`, `generateSBOM`, `merge`, `parse`, & `qa`)

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

## [v4.4.2-alpha] - (05/19/2023)

This update focuses mainly on adding several test cases for most generator classes and fixing bugs in the stores,
serializers, and translators.

### Added
- Added tests for `SBOMGenerator.writeFileToString()` in `SBOMGeneratorTest`.
    - These test for cases where pretty-printing is enabled and disabled.
- Added a mostly complete suite of test cases for all non-dataclasses in the `sbomfactory.generators.generators` package.
    - `BOMStoreTestCore` - An abstract test class to setup an SBOM and test component behavior for all classes that extend `BOMStore`.
    - `CycloneDXStoreTest` - Tests all CycloneDX BOM manipulation methods
    - `SPDXStoreTest` - Tests all SPDX Document manipulation methods
- Added test cases for all custom serializer classes in the `sbomfactory.generators.generators` package.
    - For each class, a generator serializes a test SBOM object to a file. Then, the SVIP translators are used to translate
      the SBOM file back into an SBOM object. The two are then checked to ensure equality, and if so the test passes. This
      allows us to ensure that an SBOM can go both ways, via the generators and translators.
        - `CycloneDXSerializerTest`
        - `CycloneDXXMLSerializerTest`
        - `SPDXTagValueWriterTest`
    - Note that because there are no translators for SPDX JSON and XML formats (yet), we cannot test the outputs of those
      corresponding serializers.
- Added **INTERNAL** SBOM comparison methods:
    - `SBOM.equals()` - This tests for equality of all relevant fields, including the SBOM `DependencyTree`.
    - `DependencyTree.equals()` - This is a quick-and-dirty fix to compare two instances of a `DependencyTree` WITHOUT
      regard to UUIDs. This allows us to have two separate SBOMs with the same components (with randomly generated UUIDs)
      and still be equal.
    - `DependencyTree.toString()` & `DependencyTree.dependencyMapToString()`, which were both added to support
      `DependencyTree.equals()`. The `equals()` method simply compares the `toString()` methods of each `DependencyTree`,
      which are generated by the `dependencyMapToString()` method.
        - This is a simple recursive method that uses indentations
          to represent how an individual component is nested, thus removing a reliance on UUIDs.
        - Each component can then be compared by checking its string representation (which is currently just the component name)
- Added "copy" constructor to `ParserComponent` Class to construct an instance from a `Component` instance.
    - Updated `SBOMGenerator` to allow processing SBOMs that contain `Component` instances.

### Changed
- All tests in `SBOMGeneratorTest` now use `Debug.log()` instead of default system I/O.
- Both output methods in `SBOMGenerator` (`writeFile()` & `writeFileToString`) now throw a `GeneratorException` for
  ease of testing.
- `SBOMGeneratorTest.addTestComponentsToSBOM()` is now static to allow re-use in other tests.
- `SBOMGeneratorTest.generatePathToSBOM()` is now public to allow use for filepath generation in tests.
- Translator fixes:
    - Fixed `TranslatorCDXJSON` Class parsing in timestamps in the wrong format.
    - Fixed `TranslatorCDXJSON` & `TranslatorCDXXML` Classes parsing in an incorrect tool string.
    - Fixed `TranslatorSPDX` Class incorrectly parsing an SPDX document namespace UUID.
- `SPDXTagValueWriter` Class now re-uses as many tags as possible from `TranslatorSPDX` Class for accuracy.
- Fixed `SPDXStore` not containing the default `SPDXRef-DOCUMENT DESCRIBES SPDXRef-DOCUMENT` relationship.

### Removed
- Removed file parsing in `SBOMGeneratorTest`. Instead, a sample SBOM is created from scratch.
- Removed default root wrapping property of `ObjectMapper`-based serializers, as it was causing the class name to be the
  top-level object of the generated SBOM.
- Removed `bom:` namespace prefix from generated CycloneDX XML SBOMs, as it was interfering with the corresponding
  translator.
- Removed all old sample SBOMs in `doc/Sample SBOMs`.

## [v4.4.1-alpha] - (05/17/2023)

### Added

- Added a new `APPLICATION` type to `ParserComponent.Type` Enum to reflect the type of subprocess calls.
- Added checking for duplicate components in the SBOM as well as in the components parsed in the
  `ParserController.parse()` Method.

### Changed
- Updated `LicenseManager.parseLicense()` Method to more accurately and efficiently match an arbitrary license string to
  =======
## [v4.4.1-alpha] - (05/XX/2023)

### Added

- `NugetParser` which parses the main configuration file of Nuget projects, and queries the Nuget package-manager for any existing licenses.

### Changed
- Updated `LicenseManager.parseLicense()` method to more accurately and efficiently match an arbitrary license string to
    - This is done by quantifying the number of token matches per license string and then choosing the string with the
      most matches.
    - It also contains checks for common license keywords and short identifiers to increase speed.
- Updated `ParserComponent.resolveLicenses()` Method to support finding multiple licenses in a single, comma-separated .
    - This increases the license parsing accuracy, as one string can now be separated into multiple licenses.
    - However, any invalid license after the first one is found will be discarded to avoid "garbage" licenses occuring in
      the SBOM.
- Updated `LanguageParser` Class to check for and handle import wildcards.
    - If any import wildcards are found while parsing a file, the component name will be replaced with the
      next-highest-level package in the import statement and the component group will be changed to reflect that.
- Fixed some backslashes not being changed to forward slashes to improve file readability.
    - Ex: `\\` in a filepath will now always be changed to `/`.
- Fixed components generated from subprocess calls not containing the files they were found in.
- Fixed duplicates being added to the internal SBOM in `ParserController.parse()` Method because of the way the
  `ContextParser`s were set up to store components.

## [v4.4.0-alpha] - (05/15/2023)

### Added

- Add `SPDXTagValueWriter` Class to serialize an instance of `SPDXStore` to a tag-value document and output to either a
  string or file.
    - Contains methods for `writeToString()` and `writeToFile()`
- Add new SPDX `GeneratorFormat` Enum Value in `GeneratorSchema` Class to support file output for this value.

### Changed
- Changed `SBOMGenerator` Class to use an `SPDXTagValueWriter` instead of an `ObjectMapper` when dealing with the SPDX
  tag-value format.

## [v4.3.2-alpha] - (05/12/2023)

### Added

- Add `LicenseManager.getConcatenatedLicenseString()` method to concisely concatenate all tool licenses to use when
  representing a Collection of licenses as a string.

### Changed

- Ensure `SPDXSerializer` Class properly serializes an XML Document according to the documentation and
  [official example](https://github.com/spdx/spdx-spec/blob/development/v2.3.1/examples/SPDXXMLExample-v2.3.spdx.xml).
- Update `SPDXSerializer` Class to ensure the XML root element is `Document` when serializing using an instance of
  `ToXmlGenerator`.

## [v4.3.1-alpha] - (05/11/2023)

### Added

- Add `CycloneDXXMLSerializer` Class.
    - Similar to `CycloneDXSerializer`, this class overrides the Jackson `StdSerializer` Class to allow serialization of
      a `CycloneDXStore` instance to an XML file according to the [CycloneDX v1.4 XML specification](https://cyclonedx.org/docs/1.4/xml/).
    - A separate serializer is required due to the inherent difference between JSON and XML.
- Add abstract `TranslatorCore` Class that all other translators extend to increase modularity.
    - This will eventually replace the current implementation of `Translator`, which used to act as a controller for the
      translators and is no longer needed. Instead, it will remain the abstract core class of the translators, to be
      extended to support a new schema/format easily.
- Add abstract `TranslatorTestCore` Class that allows the tests to have a similar level of modularity.
- Add `CSProjParserTest` to test the C# Package Parser.

### Changed

- Update `GeneratorSchema.getObjectMapper()` to take a schema argument to register all serializers with their respective
  `ObjectMapper`
    - Previously, registering the custom serializers was done using Jackson decorators on all `BOMStore` classes. However,
      this made it difficult to have multiple different types of serializers.
    - Now, all `ObjectMapper` configuration and serialization setup is done in `getObjectMapper()`. This allows for simply
      calling the method on a specific format and passing in the schema to get a completely set up `ObjectMapper` whose
      serializer is dependent on the file format back.
- Update `ObjectMapper` pretty-printing to stop indenting each line of an array to enhance SBOM readability.
- Refactor all translator tests (see below) to reflect the updated translator (and `TranslatorTestCore`) architecture.
    - `TranslatorCDXJSONTest`
    - `TranslatorCDXXMLTest`
    - `TranslatorSPDXTest`
- Rename `GradleParserParseTest` to `GradleParserTest` for semantics.
- Update `CommentParser`, `DeadImportParser`, & `SubprocessParser` to add parsed contexts to SBOM components.
    - `CommentParser` could use a look in terms of the value of the data collected, as many comments provide no real,
      valuable information to our SBOM.


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
## [v2.0.1] - (03/2/2023)

### Fixed

- Backend now kills after closing app
- Backend no longer uses static constant file paths for OSI.

### Known Issues
- VEXFactory has an incompatibility with NVIP API. This breaks vulnerability discovery and all features which rely on VEX data.

## [v2.0.0] - (03/24/2023)

### Added

- `Conflicts`: Now when creating a master SBOM, conflicts between SBOMS are noted and sent to user.
- `Download Report`: Users can now download SBOM reports

### Fixed

- Node graph zoom in/zoom out
- Node graph now fits within the frame
- Node graph node names no longer get cut off after a certain length
- SBOM Conflicts now save
- When there are no licenses, the SPDX translator now returns an empty array instead of "NONE"
- Log in errors are now shown to users

### Changed

- `OSI`: Docker image builds upon app start up and remains open.

### Known Issues
- VEXFactory has an incompatibility with NVIP API. This breaks vulnerability discovery and all features which rely on VEX data.

## [v1.0.0] - (02/27/2023)

### Added

- `WebApp` : Contains the frontend which uses Angular and ElectronJS.
- `SVIP` : Folder containing the backend which now utilizes Apache Maven with Springboot framework.
- `SVIP Core` : Uses a Docker Container to generate SBOMs, merges them, and creates a D3 JSON Visual

### Fixed

- UI layout is more visually appealing

### Changed

- `OSI`: OSI Now handles coverting projects into SBOMS and then formatting them for Node Graps
- `Frontend`: The frontend has been completly reworked
    - Electron JS now wraps the GUI to open as an app and obtain folder paths
    - Angular is the framework used to create GUI design and implementation
- `Backend`: SVIP controller handles api requests using Springboot Framework.
    - `OSI`: Uses docker to create SBOM using multiple tools when given a user's project filepath
    - `Merger`: Merges multiple SBOMS into one 'master SBOM'
    - `Translator`: Translates SBOMS of different formats into an internal format
    - `VEXFactory`: Communicates with the National Vulnerability Intelligence Program to get CVEs for components

### Removed

- `Tree.html`, `testSBOMS`, `Converter`: all were removed after the introduction of Apache Maven Springboot & Angular/Electron.

### Known Issues
- VEXFactory has an incompatibility with NVIP API. This breaks vulnerability discovery and all features which rely on VEX data.