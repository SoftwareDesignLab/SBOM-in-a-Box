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
    - [Adding More Tools](#adding-more-tools)
  - [Building the Image](#building-the-image)
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

| Body  |   Type   |                                                                                                   Description                                                                                                    | Is Required? |
|:-----:|:--------:|:----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|:------------:|
| tools | String[] | A JSON string array of tool names. If no tools are provided, the container will generate SBOMs using all tools that are applicable to project in the bound `/code` directory. Invalid tools will return an error |      NO      |

**Responses**

| Response Code |  Type  |                            Description                            |
|:-------------:|:------:|:-----------------------------------------------------------------:|
|      200      | String |                     Number of SBOMs generated                     |
|      204      | String |                No SBOMs were generated, returns 0                 |
 |      400      | String |                 Error message about invalid tools                 |
|      422      | String | "No tools selected" - No tools are applicable or queded to be run |

### Get Tools
**Endpoint:** `http://localhost:50001/tools`

**Request Method:** `GET`

**Parameters**

| Parameter |           Type           |                                                                Description                                                                 | Is Required? |
|:---------:|:------------------------:|:------------------------------------------------------------------------------------------------------------------------------------------:|:------------:|
|   list    | String (`all`,`project`) | `all`: (Default) Get all tools availble in the OSI instance</br>`project`: Get all tools appicable to the project in the `/code` directory |      NO      |

**Responses**

| Response Code |   Type   |            Description            |
|:-------------:|:--------:|:---------------------------------:|
|      200      | String[] | A JSON string array of tool names |
|      400      |  String  |      Unknown list parameter       |


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
> After completing these steps, the Docker Flask API and SVIP API will automatically recognize and use the tool.
1. Add the installation commands ( and any additional required software ) to [`setup.sh`](../core/src/main/java/org/svip/generation/osi/docker/scripts/setup.sh)
   * There are a number of package mangers available to use, see the file as reference
2. Add validation commands for newly added languages, package managers, or tools to [`validate.sh`](../core/src/main/java/org/svip/generation/osi/docker/scripts/validate.sh)
   * See file for examples, but main structure is `<COMMAND> &> /dev/null && pass <NAME> <1|2|3> || fail <NAME> ` 
     * `1`: add `name` to `OSI_LANG` environment variable
     * `2`: add `name` to `OSI_PM` environment variable
     * `3`: add `name` to `OSI_TOOL` environment variable
   * If this is not done, the tool **will not** appear in OSI
3. Create a new tool configuration file in the [`tool_config`](../core/src/main/java/org/svip/generation/osi/docker/server/tool_configs)
   * See [Tool Configuration Files](#tool-configuration-files) for structure details
   * The file **MUST** be named using the same name in step 2. Example: name = `foo`, config file = `foo.yml`
4. DONE! Rebuild the image ( see [Building the Image](#building-the-image) ) to recompile the image with the new tool changes

### Tool Configuration Files
Tool configuration files allow for tools to easily be added and removed from OSI. They contain one or more "run profiles", 
a set of pre-configured commands to run using the same tool. This is useful for generating different SBOMs for different
project types while using the same tool
```yaml
# Example tool config file
source: "URL source of the tool"
profiles:
  - schema: "SBOM 1 SCHEMA"
    spec_version: "SBOM 1 SPEC VERSION"
    format: "SBOM 1 FORMAT"
    languages:
      - "LANGUAGE"
    package_managers:
      - "PACKAGE MANAGER"
    commands:
      - "COMMAND 1"
      - "COMMAND 2" 
```
|            Field             | Required? |                        Description                        |
|:----------------------------:|:---------:|:---------------------------------------------------------:|
|           `source`           |    Yes    |                  URL source of the tool                   |
|          `profiles`          |    Yes    |             List of run profiles for the tool             |
|      `profile.schema`*       |    Yes    |          SBOM schema that this profile generates          |
|   `profile.spec_version`*    |    Yes    |       SBOM spec version that this profile generates       |
|      `profile.format`*       |    Yes    |       SBOM file format that this profile generates        |
|    `profile.languages`**     |    No     |    Languages that this profile can generate SBOMs for     |
| `profile.package_managers`** |    No     | Package Managers that this profile can generate SBOMs for |
|      `profile.commands`      |    Yes    |           List of cli commands to run the tool            |

*: Must be defined in [`sbom.cfg`](../core/src/main/java/org/svip/generation/osi/docker/server/configs/sbom.cfg)

**: Optional to help with restrictions. Example if tool needs Maven to generate SBOM, can exclude the languages field 
and just have the package managers field

The list of languages and package managers can be found at 
[`language_ext.cfg`](../core/src/main/java/org/svip/generation/osi/docker/server/configs/language_ext.cfg) and
[`manifest_ext.cfg`](../core/src/main/java/org/svip/generation/osi/docker/server/configs/manifest_ext.cfg) respectively.
File extensions ware used to determine the language of the project while the manifest files defined in `manifest_ext.cfg`
are explicitly searched for to determine their package manager. Each file can be updated accordingly for new languages 
and package managers.

## Building the Image
To manually build/rebuild the image, execute the following from the root directory of the repository:

```shell
$ docker compose up osi --build
```
The first build will take up to 15 minutes to complete, but subsequent builds will be significantly faster. If using 
a saved image, the first build time should be much faster.