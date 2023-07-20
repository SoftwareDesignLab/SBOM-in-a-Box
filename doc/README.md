# Additional Resources
> Additional Details about all "features", or sub-systems that SVIP contains.

## Index

- [**System Requirements**](#system-requirements)
- [**SVIP API**](API.md)
- [**SBOM Generator CLI**](#sbom-generator-cli)
  - [Quick Start](#quick-start)
  - [Usage](#usage-1)
  - [Supported Source Files](#supported-source-files)
  - [NLP Techniques](#nlp-techniques)
- [**Open Source Intergration (OSI)**](#open-source-integration)
  - [Quick Start](#quick-start-1)
  - [Supported Tools](#supported-tools)
  - [Building the Image](#building-the-image)
    - [Saved Images](#saved-images)
  
---

# System Requirements
- Java 17
- Gradle

---

# SBOM Generator CLI
> Generate SBOMs using Regex and Natural Language Processing techniques to analyze and enhance information found in 
> source code and manifest files

## Quick Start
> CLI Driver can be found [here](../core/src/main/java/org/svip/SBOMGeneratorCLI.java)

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
> Unlike many SBOM tools, SVIP does **NOT** require package managers or manifest files to generate SBOMs, rather as
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

Place the source files of the project into `core/src/main/java/org/svip/sbomgeneration/osi/bound_dir/code`. 

Then run the following command to build the image and run the container to generate SBOMs:
```shell
# Deploy the container
$ docker compose up osi
```

### Result
The `/sboms` directory (also in `/bound_dir` will now contain generated SBOMs from the source project in `/code`.

## Supported Tools
> OSI uses 8 open source tools to support 13 different languages. Please read the tool documentation to see if it fits 
> the need of your project

|          Tool           |                          Github                           | Supported Language                                                                                                             |
|:-----------------------:|:---------------------------------------------------------:|:-------------------------------------------------------------------------------------------------------------------------------|
|        **Syft**         |              https://github.com/anchore/syft              | `Java`<br>`Python`<br>`Go`<br>`PHP`<br>`Ruby`<br>`Rust`<br>`Dart`<br>`Haskell`<br>`Javascript`<br>`Swift`<br>`C++`<br>`Erlang` |
| **SPDX SBOM Generator** | https://github.com/opensbom-generator/spdx-sbom-generator | `Java`<br>`Python`<br>`Go`<br>`PHP`<br>`Ruby`<br>`Rust`<br>`Javascript`<br>`Swift`                                             |
| **CycloneDX Generator** |            https://github.com/CycloneDX/cdxgen            | `Java`<br>`Python`<br>`Go`<br>`PHP`<br>`Ruby`<br>`Rust`<br>`Dart`<br>`Haskell`<br>`C++`                                        |
|    **Sonatype Jake**    |     https://github.com/sonatype-nexus-community/jake      | `Python`                                                                                                                       |
|   **CycloneDX Conan**   |       https://github.com/CycloneDX/cyclonedx-conan        | `C++`                                                                                                                          |
|  **CycloneDX Python**   |       https://github.com/CycloneDX/cyclonedx-python       | `Python`                                                                                                                       |
|    **CycloneDX PHP**    |    https://github.com/CycloneDX/cyclonedx-php-composer    | `PHP`                                                                                                                          |
|        **JBOM**         |              https://github.com/eclipse/jbom              | `Java`                                                                                                                         |



## Building the Image
To manually build the image, execute the following from the root directory of the repository:

```shell
$ docker compose up osi --build
```
The first build will take up to 6 minutes to complete, but subsequent builds will be significantly faster. If using 
a saved image, the first build time should be much faster.

### Saved Images
> **CURRENTLY NOT WORKING**

A saved image can be loaded to drastically decrease the time cost of the first build. This uses 
[Git Large File Storage (LFS)](https://git-lfs.com/) to store the compressed archive.

To save the image for subsequent uses, ensure the image is built and then execute the following from the root 
directory of the repository:
```shell
$ docker save -o core/src/main/java/org/svip/sbomgeneration/osi/images/osi.tar ubuntu:latest | gzip > core/src/main/java/org/svip/sbomgeneration/osi/images/osi.tar.gz
```

To load a saved image into Docker, run the following command:
```shell
$ docker load --input core/src/main/java/org/svip/sbomgeneration/osi/images/osi.tar.gz
```

> **NOTE:** If any modifications are made to:
> 1. The inline Dockerfile in `docker-compose.yml`,
> 2. The setup shell script in `core/src/main/java/org/svip/sbomgeneration/osi/scripts/setup.sh`, or
> 3. The OSI tool controller in `core/src/main/java/org/svip/sbomgeneration/osi/scripts/ContainerController.py`,
> 
> Then the image will need to be rebuilt.
