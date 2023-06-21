# Additional Resources
> Additional Details about the project

## Index

- [System Requirements](#system-requirements)
- [SBOM Generator CLI](#sbom-generator-cli)
  - [Quick Start](#quick-start)
  - [Usage](#usage)
  - [Supported Source Files](#supported-source-files)
  - [NLP Techniques](#nlp-techniques)

# System Requirements
- Java 17
- Angular CLI
- npm
- Gradle
- Docker

# SBOM Generator CLI
> Generate SBOMs using Regex and Natural Language Processing techniques to analyze and enhance information found in 
> source code and manifest files

## Quick Start
> CLI Driver can be found [here](../core/src/main/java/org/svip/SBOMGeneratorCLI.java)

Usage: `java SBOMGeneratorCLI <targetPath>`

## Usage
```
java SBOMGeneratorCLI <targetPath> <additionalArgs>
```
#### Required Arguments
- `<targetPath>`: Required. Path to a target file or root directory to parse.

#### Optional Arguments
- `-d`: Show additional debug information, overrides Summary when combined with `-s`.
- `-s`: Show Summary information, disabling ALL default messages.
- `-h`: Display this usage information.
- `-o=<specification>`: Output specification. Select a supported format (`CycloneDX` or `SPDX`).
  > Will default to CycloneDX if not specified.
- `-f=formats`:  Output format. Select a supported format (`JSON`, `XML`, `YAML`).
  > Output specification defaults to JSON if not specified.

#### Examples
```
Display usages:  java -jar parser.jar -hs
Basic: java -jar parser.jar MyProject/src
Debug: java -jar parser.jar MyProject/src -d
Summary: java -jar parser.jar MyProject/src -s
Debug (Overrides Summary): java -jar parser.jar MyProject/src -d -s
Debug, Output as JSON: java -jar parser.jar MyProject/src -o=json -d

CycloneDX:
  - CycloneDX JSON:           java -jar parser.jar MyProject/src -d
  - CycloneDX XML (no debug): java -jar parser.jar MyProject/src -o=CycloneDX -f=XML

SPDX:
  - SPDX JSON:            java -jar parser.jar MyProject/src -d -o=SPDX
  - SPDX YAML (no debug): java -jar parser.jar MyProject/src -o=SPDX -f=YAML
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