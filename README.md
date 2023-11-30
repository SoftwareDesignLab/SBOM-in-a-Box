# SBOM-in-a-Box
> The **SBOM-in-a-Box** is a unified platform to promote the 
> production, consumption, and utilization of Software Bills of Materials (SBOMs). 
> This includes conversion between schemas, generation, comparision and evaluation of quality.

# Purpose of SBOM-in-a-Box
> SBOM-in-a-box has unique features including generation of SBOMs using multiple tools that allow for a more a complete SBOM to be created. There is also a feature within metrics, where the tool provides suggestions if there is a potential better way to showcase the attributes. There is also the ability to convert between SPDX and CycloneDX SBOM schemas, and to gain insight into vulnerabilities of software through SBOMs. These features allow for developers to create an SBOM that is the most relevant and suits their needs.

### Latest Release: [[v9.2.0-alpha] - (11/30/2023)](doc/changelog.md)

## System Requirements
- Java 17.X.X
- Gradle 7.5.X
- Docker 24.X.X

## Quick Start

**Launch the API**
1. `docker compose up`

Note: To launch the backend it will take at least 10 minutes, due to there being over 10 open source tools included.

> Note: Due to installation of multiple tools, building of the container may take a long time, however it is less time than learning all tools individually.

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
**Principal Investigator:** [Mehdi Mirakhorli](mailto:mxmvse@rit.edu)

**Senior Project Manager:** [Chris Enoch](mailto:ctevse@rit.edu)

**Senior Developer Team Lead:** [Derek Garcia](mailto:dlg1206@rit.edu)

**Developer Team Leads**
- [Schuyler Dillon](mailto:sdd4181@rit.edu)
- [Tyler Drake](mailto:txd3634@rit.edu)
- [Ian Dunn](mailto:itd3516@g.rit.edu)
- [Kevin Laporte](mailto:kjl8898@rit.edu)
- [Matt London](mailto:mrl2534@rit.edu)
- [Dylan Mulligan](mailto:dtm5568@rit.edu)
- [Amanda Nitta](mailto:nittaak@hawaii.edu)

**Developer Team**
- [Brian Baumann](mailto:bmb5957@rit.edu)
- [Asa Horn](mailto:aoh9470@rit.edu)
- [Justin Jantzi](mailto:jwj7297@rit.edu)
- [Henry Keena](mailto:htk4363@rit.edu)
- [Hubert Liang](mailto:hubertl@hawaii.edu)
- [Ping Liu](mailto:htk4363@rit.edu)
- [Henry Lu](mailto:hyl2415@rit.edu)
- [Matthew Morrison](mailto:msm8275@rit.edu)
- [Ethan Numan](mailto:ehn4602@rit.edu)
- [Henry Orsagh](mailto:hco4630@rit.edu)
- [Juan Francisco Patino](mailto:jfp6815@rit.edu)
- [Max Stein](mailto:mhs8558@rit.edu)
- [Tom Roman](mailto:tfr8811@rit.edu)
- [Liam Wilkins](mailto:ljw1484@rit.edu)
- [Jordan Wong](mailto:jordanw4@hawaii.edu)
