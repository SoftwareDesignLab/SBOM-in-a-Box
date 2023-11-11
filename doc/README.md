# Additional Resources
> Additional Details about all "features", or sub-systems that SBOM-in-a-Box contains.

## Index

- [**SBOM-in-a-Box API**](API.md)
- [**SBOM Generator CLI**](#sbom-generator-cli)
  - [Quick Start](#quick-start)
  - [Usage](#usage)
  - [Supported Source Files](#supported-source-files)
  - [NLP Techniques](#nlp-techniques)
- [**Open Source Integration (OSI)**](#open-source-integration)
  - [Quick Start](#quick-start-1)
  - [API](#api)
  - [Supported Tools](#supported-tools)
  - [Building the Image](#building-the-image)
    - [Saved Images](#saved-images)
  
---

# SBOM Generator CLI
> Generate SBOMs using Regex and Natural Language Processing techniques to analyze and enhance information found in 
> source code and manifest files

## Quick Start
> CLI Driver can be found [here](../core/src/main/java/org/svip/generation/SBOMGeneratorCLI.java)

To build from scratch, use:
```shell
# Build core jar
$ ./gradlew build
# Rename jar file
$ move core/build/libs/core-1.0.0-alpha.jar SBOMGeneratorCLI.jar
# Run jar file or in IDE
$ java -jar SBOMGeneratorCLI.jar <targetPath>
```

## Usage
```
java -jar SBOMGeneratorCLI.jar <targetPath> [-d|-s|-h] [-o=<CDX|SPDX>] [-f=<JSON|XML|YAML|SPDX>]
```
#### Required Arguments
- `<targetPath>`: Required. Path to a target file or root directory to parse.

#### Optional Arguments
- `-d`: Show additional debug information, overrides Summary when combined with `-s`.
- `-s`: Show Summary information, disabling ALL default messages.
- `-h`: Display this usage information.
- `-o=<CYCLONEDX|CDX|SPDX>`: Output specification. Defaults to CycloneDX.
- `-f=<JSON|XML|YAML|SPDX>`: Output file format. Defaults to JSON.

#### Examples
```
Display usages:               java -jar SBOMGeneratorCLI.jar -h
Basic:                        java -jar SBOMGeneratorCLI.jar MyProject/src
Debug:                        java -jar SBOMGeneratorCLI.jar MyProject/src -d
Summary:                      java -jar SBOMGeneratorCLI.jar MyProject/src -s
Debug, Output as JSON:        java -jar SBOMGeneratorCLI.jar MyProject/src -f=json -d

CycloneDX:
  - CycloneDX JSON:           java -jar SBOMGeneratorCLI.jar MyProject/src -d
  - CycloneDX XML (no debug): java -jar SBOMGeneratorCLI.jar MyProject/src -o=CycloneDX -f=XML

SPDX:
  - SPDX JSON:                java -jar SBOMGeneratorCLI.jar MyProject/src -d -o=SPDX
  - SPDX YAML (no debug):     java -jar SBOMGeneratorCLI.jar MyProject/src -o=SPDX -f=YAML
```

## Supported Source Files
> Unlike many SBOM tools, SBOM-in-a-Box does **NOT** require package managers or manifest files to generate SBOMs, rather as
> a basis for additional SBOM enhancement

|   Language   |  Package Manager  |          Manifest File          |
|:------------:|:-----------------:|:-------------------------------:|
|   `Python`   |  Pip <br> Conda   |        requirements.txt         |
| `Javascript` ||
| `Typescript` ||
|    `Java`    | Gradle <br> Maven |      *.gradle <br> pom.xml      |
|   `Scala`    |
|     `C#`     |       Nuget       |            *.csproj             |
|    `C++`     |       Conan       | conanfile.txt <br> conanfile.py |
|     `C`      |       Conan       | conanfile.txt <br> conanfile.py |
|     `Go`     ||
|    `Rust`    ||
|    `Ruby`    ||
|    `Perl`    ||

## NLP Techniques
> We use a number of NLP techniques to identify unique edge cases to ensure further SBOM accuracy

- "Dead Code" detection: Check to see if imported dependencies are actually used in source code
- Commented imports: 
- Subprocess calls to archives/binaries: Check for subprocess calls to external dependencies

---

# Open Source Integration
> OSI is a Docker Container that dynamically utilizes several Open Source SBOM Generations using a given
> project directory and generate CycloneDX and SPDX SBOMs into a target out directory.

## Quick Start
> Make sure the Docker Daemon / Docker Desktop is running and the current working directory is the root of the 
> repository.

Place the source files of the project into `core/src/main/java/org/svip/generation/osi/bound_dir/code`. 

Then run the following command to build the image and send an API request to the container to generate SBOMs:
```shell
# Deploy the container
$ docker compose up osi
# Send API request to container to generate SBOMs with ALL tools. To specify tools, add a request body of tool names.
$ curl -X POST -G http://localhost:50001/generate
```

### Result
The `/sboms` directory (also in `/bound_dir` will now contain generated SBOMs from the source project in `/code`.

## API
> After deploying the OSI container using `docker compose up osi`, an API will be started at `http://localhost:50001`.
> Sending requests to the API will allow users to get a list of valid tool names to be used or generate SBOMs with 
> specified tools.

### Generate SBOMs
**Endpoint:** `http://localhost:50001/generate`

**Request Method:** `POST`

**Request Body**

| Body  |   Type   |                                                                                                               Description                                                                                                               | Is Required? |
|:-----:|:--------:|:---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|:------------:|
| tools | String[] | A JSON string array of tool names. If no tools are provided, the container will generate SBOMs <br/>using all possible tools. If a tool name is invalid or doesn't support the project languages or manifest files, it will be ignored. |      NO      |

**Responses**

| Response Code |  Type  |               Description                |
|:-------------:|:------:|:----------------------------------------:|
|      200      | String | Successfully generated one or more SBOMs |
|      204      | String |         No SBOMs were generated          |

### Get Tools
**Endpoint:** `http://localhost:50001/tools`

**Request Method:** `GET`

**Responses**

| Response Code |   Type   |                            Description                            |
|:-------------:|:--------:|:-----------------------------------------------------------------:|
|      200      | String[] | A JSON string array of all currently supported open-source tools. |


## Supported Tools
> OSI uses 18 open source tools to support 17 different languages multiple times over. Please read the tool documentation 
> to see if it fits the need of your project.

|          Tool           |                          Github                           | Supported Language(s)                                                                                                                                                        |
|:-----------------------:|:---------------------------------------------------------:|:-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
|        **Syft**         |              https://github.com/anchore/syft              | `C`<br>`C++`<br>`Dart`<br>`.NET`<br>`Objective-C`<br>`Elixir`<br>`Erlang`<br>`Go`<br>`Haskell`<br>`Java`<br>`JavaScript`<br>`PHP`<br>`Python`<br>`Ruby`<br>`Rust`<br>`Swift` |
| **SPDX SBOM Generator** | https://github.com/opensbom-generator/spdx-sbom-generator | `.NET`<br>`Go`<br>`Java`<br>`JavaScript`<br>`PHP`<br>`Python`<br>`Ruby`<br>`Rust`<br>`Swift`<br>                                                                             |
| **CycloneDX Generator** |            https://github.com/CycloneDX/cdxgen            | `C`<br>`C++`<br>`Clojure`<br>`Go`<br>`.NET`<br>`JavaScript`<br>`Java`<br>`PHP`<br>`Python`<br>`Ruby`<br>`Rust`<br>`Dart`<br>`Haskell`<br>`Elixir`<br>`Swift`<br>             |
|    **Sonatype Jake**    |     https://github.com/sonatype-nexus-community/jake      | `Python`                                                                                                                                                                     |
|   **CycloneDX Conan**   |       https://github.com/CycloneDX/cyclonedx-conan        | `C`<br>`C++`                                                                                                                                                                 |
|  **CycloneDX Python**   |       https://github.com/CycloneDX/cyclonedx-python       | `Python`                                                                                                                                                                     |
|    **CycloneDX PHP**    |    https://github.com/CycloneDX/cyclonedx-php-composer    | `PHP`                                                                                                                                                                        |
|        **JBOM**         |              https://github.com/eclipse/jbom              | `Java`                                                                                                                                                                       |
|      **Covenant**       |        https://github.com/patriksvensson/covenant         | `.NET`                                                                                                                                                                       |
|   **CycloneDX Bower**   |         https://github.com/hanstdam/cdx-bower-bom         | `Javascript`                                                                                                                                                                 |
|    **CycloneDX Go**     |          https://github.com/ozonru/cyclonedx-go           | `Go`                                                                                                                                                                         |
|   **CycloneDX Rust**    |     https://github.com/CycloneDX/cyclonedx-rust-cargo     | `Rust`                                                                                                                                                                       |
|        **GoBom**        |            https://github.com/mattermost/gobom            | `Go`                                                                                                                                                                         |
|     **SBOM4Files**      |       https://github.com/anthonyharrison/sbom4files       | `C`<br>`C++`<br>`Go`<br>`Java`<br>`Javascript`<br>`PHP`<br>`Python`                                                                                                          | 
|     **SBOM4Python**     |      https://github.com/anthonyharrison/sbom4python       | `Python`                                                                                                                                                                     |
|      **SBOM4Rust**      |       https://github.com/anthonyharrison/sbom4rust        | `Rust`                                                                                                                                                                       |
|      **SBOM Tool**      |          https://github.com/microsoft/sbom-tool           | `.NET`                                                                                                                                                                       |                                                                                                                                                                        |
|      **Retire.js**      |           https://github.com/RetireJS/retire.js           | `Javascript`                                                                                                                                                                 |

### Adding More Tools
To add additional open source tools, there are 2 steps:
1. Add any additional dependencies as well as the tool installation command to [`setup.sh`](../core/src/main/java/org/svip/generation/osi/docker/scripts/setup.sh)
2. Register the tool in [`ToolMapper.py`](../core/src/main/java/org/svip/generation/osi/docker/server/ToolMapper.py) 
   by adding an entry to `TOOL_LIST` describing the generated BOM format, languages used, and commands required. See 
   the current entries for examples.

> After completing these steps, the Docker Flask API and SVIP API will automatically recognize and use the tool.


## Building the Image
To manually build/rebuild the image, execute the following from the root directory of the repository:

```shell
$ docker compose up osi --build
```
The first build will take up to 6 minutes to complete, but subsequent builds will be significantly faster. If using 
a saved image, the first build time should be much faster.