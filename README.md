# SBOM Visualization and Integration Platform
> The SBOM Visualization and Integration Platform (**SVIP**) is a unified platform to promote the 
> production, consumption, and utilization of Software Bills of Materials.

### Latest Release: [[v7.3.0-alpha] - (8/11/2023)](doc/changelog.md)

## System Requirements
- Java 17.X.X
- Gradle 7.5.X
- Docker 24.X.X

## Quick Start

**Launch the API**
1. `docker ps`
2. `docker compose up`

**Launch the GUI**
1. Clone the [GUI](https://github.com/SoftwareDesignLab/svip-ui) repo and follow the quickstart

> If making changes to any source code, the Docker image(s) will need to be rebuilt. See 
> [Building the Image](doc/README.md#building-the-image) for detailed instructions. See 
> [SVIP API](doc/API.md#svip-api) for detailed API usage.

## Features
SVIP has a number of unique features to support:

- **Open Source Integrated SBOM Generation:** Makes use of open source SBOM Generator Tools to generate SBOMs
- **SBOM Generation:** Custom SBOM generation via source file and package manager file analysis
- **Vulnerability Exploitability eXchange (VEX) Generation:** Generate [VEX](https://www.cisa.gov/sites/default/files/2023-01/VEX_Use_Cases_Aprill2022.pdf) documents from SBOMs
- **SBOM Metrics:** Grade SBOMs using a series of metric tests
- **SBOM Comparison:** Compare SBOMs to identify key differences between them
- **SBOM Merging:** Merge SBOMs into a single unified document

Currently, SVIP Supports the following SBOM Types

|                        Schema                        | JSON |              Tag:Value               |
|:----------------------------------------------------:|:----:|:------------------------------------:|
|  [SPDX 2.3](https://spdx.github.io/spdx-spec/v2.3/)  |  ✅   |                  ✅                   |
| [CyloneDX 1.4](https://cyclonedx.org/docs/1.4/json/) |  ✅   | CycloneDX does not support Tag:Value |



## Contributors
**Principal Investigator:** [Mehdi Mirakhorli](mailto:mxmvse@rit.edu)

**Senior Project Manager:** [Chris Enoch](mailto:ctevse@rit.edu)

**Senior Developer Team Lead:** [Derek Garcia](mailto:dlg1206@rit.edu)

**Developer Team Leads**
- [Schuyler Dillon](mailto:sdd4181@rit.edu)
- [Tyler Drake](mailto:txd3634@rit.edu)
- [Kevin Laporte](mailto:kjl8898@rit.edu)
- [Matt London](mailto:mrl2534@rit.edu)
- [Dylan Mulligan](mailto:dtm5568@rit.edu)

**Developer Team**
- [Ian Dunn](mailto:itd3516@g.rit.edu)
- [Asa Horn](mailto:aoh9470@rit.edu)
- [Henry Keena](mailto:htk4363@rit.edu)
- [Ping Liu](mailto:htk4363@rit.edu)
- [Henry Lu](mailto:hyl2415@rit.edu)
- [Matthew Morrison](mailto:msm8275@rit.edu)
- [Ethan Numan](mailto:ehn4602@rit.edu)
- [Henry Orsagh](mailto:hco4630@rit.edu)
- [Juan Francisco Patino](mailto:jfp6815@rit.edu)
- [Max Stein](mailto:mhs8558@rit.edu)
- [Tom Roman](mailto:tfr8811@rit.edu)
