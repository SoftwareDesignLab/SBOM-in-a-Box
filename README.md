# SBOM-in-a-Box
> The **SBOM-in-a-Box** is a unified platform to generate the high fidelity SBOM data. It automated the
> production, consumption, and utilization of Software Bills of Materials (SBOMs). 
> This includes conversion between schemas, generation, comparision and evaluation of SBOM quality.

# Purpose of SBOM-in-a-Box
> SBOM-in-a-box is a plug-and-play environments that supports adding any form of SBOM tools into the box. This would allow generation of SBOMs using multiple tools that allow for a more a complete SBOM to be created. There is also a feature within metrics, where the tool provides suggestions if there is potentially a better way to showcase the attributes. There is also the ability to convert between SPDX and CycloneDX SBOM schemas, and to gain insight into vulnerabilities of software through SBOMs. These features allow for developers to create an SBOM that is the most relevant and suits their needs.

### Latest Release: [[v9.2.1-alpha] - (1/07/2024)](doc/changelog.md)

## System Requirements
- Java 17.X.X
- Gradle 7.5.X
- Docker 24.X.X

## Quick Start

**Launch the API**
1. `docker compose up`

Note: To launch the backend it will take at least 10 minutes, due to there being over 10 open source tools included.


**Launch the GUI**
1. Clone the [GUI](https://github.com/SoftwareDesignLab/svip-ui) repo and follow the quickstart

> If making changes to any source code, the Docker image(s) will need to be rebuilt. See 
> [Building the Image](doc/README.md#building-the-image) for detailed instructions. See 
> [SBOM-in-a-Box API](doc/API.md#svip-api) for detailed API usage.

## Features
SBOM-in-a-Box has a number of unique features to support:

- **Open Source Integrated SBOM Generation:** Makes use of open source SBOM Generator Tools to generate SBOMs
- **SBOM Generation:** Custom SBOM generation via source file and package manager file analysis
- **Vulnerability Exploitability eXchange (VEX) Generation:** Generate [VEX](https://www.cisa.gov/sites/default/files/2023-01/VEX_Use_Cases_Aprill2022.pdf) documents from SBOMs
- **SBOM Metrics:** Grade SBOMs using a series of metric tests
- **SBOM Comparison:** Compare SBOMs to identify key differences between them
- **SBOM Merging:** Merge SBOMs into a single unified document

Currently, SBOM-in-a-Box Supports the following SBOM Types

|                        Schema                        | JSON | XML |              Tag:Value               |
|:----------------------------------------------------:|:----:|:---:|:------------------------------------:|
|  [SPDX 2.3](https://spdx.github.io/spdx-spec/v2.3/)  |  ✅   |  ❌  |                  ✅                   |
| [CyloneDX 1.4](https://cyclonedx.org/docs/1.4/json/) |  ✅   |  ✅  | CycloneDX does not support Tag:Value |



## Contributors
**Principal Investigator, and Project Lead:** Mehdi Mirakhorli

**Project Manager:** Chris Enoch

**Developer Team Lead:** Derek Garcia

**Developer Team**
- Schuyler Dillon
- Tyler Drake
- Ian Dunn
- Kevin Laporte
- Matt London
- Dylan Mulligan
- Amanda Nitta
- Brian Baumann 
- Asa Horn
- Justin Jantzi
- Henry Keena
- Hubert Liang
- Henry Lu
- Matthew Morrison
- Ethan Numan
- Henry Orsagh
- Juan Francisco Patino
- Max Stein
- Tom Roman
- Liam Wilkins
- Jordan Wong
