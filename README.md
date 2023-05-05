# SBOM Visualization and Integration Platform
> The SBOM Visualization and Integration Platform (**SVIP**) is a unified platform to promote the 
> production, consumption, and utilization of Software Bills of Materials.

## Latest Release
v1.0.0 - alpha (2/27/23)

- Electron.js App for Front end
- Spring-Boot backend to handle API requests
- SVIP Core that uses a Docker Container to generate SBOMs, merges them, and creates a D3 JSON Visual

_The full changelog can be found [here](CHANGELOG.md)_

## Quick Start
- Core:
  - run SVIPApplication.java
- API:
  - run UnifiedApplication.java

## Features

### Open Source Integrated SBOM Generation
- Use Docker, for instance, to build virtual containers that use open source tools
### SBOM Generation
- Transform relevant data into serialized SBOM objects
- Translator: Parse SBOMS from files and deserialize from formats:
  - CycloneDX
    > .xml and .json
  - SPDX
    > .spdx
### SBOM VEX Generation
- Vulnerability analysis
- Make API calls to NVIP (National Vulnerability Intelligence Program) to gather vulnerability information associated with a CPE
### SBOM Analysis
- Differ
- Comparison
- Quallity
### SBOM Comparison
- Generate detailed DiffReports from a target SBOM and a list of SBOMs. 
### SBOM Merging
- todo

## Contributors
**Principal Investigator:** [Mehdi Mirakhorli](mailto:mxmvse@rit.edu)

**Senior Project Manager:** [Chris Enoch](mailto:ctevse@rit.edu)


**Senior Developer Team Lead:** [Derek Garcia](mailto:dlg1206@rit.edu)

**Developer Team Leads**
- [Schuyler Dillon](mailto:sdd4181@rit.edu)
- [Tina DiLorenzo](mailto:tnd3015@rit.edu)
- [Matt London](mailto:mrl2534@rit.edu)
- [Dylan Mulligan](mailto:dtm5568@rit.edu)

**Developer Team**
- [Michael Alfonzetti](mailto:michael.alfonzetti93@gmail.com)
- [Tyler Drake](mailto:txd3634@rit.edu)
- [Ian Dunn](mailto:itd3516@g.rit.edu)
- [Asa Horn](mailto:aoh9470@rit.edu)
- [Justin Jantzi](mailto:jwj7297@rit.edu)
- [Henry Keena](mailto:htk4363@rit.edu)
- [Kevin Laporte](mailto:kjl8898@rit.edu)
- [Ping Liu](mailto:htk4363@rit.edu)
- [Henry Lu](mailto:hyl2415@rit.edu)
- [Matthew Morrison](mailto:msm8275@rit.edu)
- [Henry Orsagh](mailto:hco4630@rit.edu)
- [Juan Francisco Patino](mailto:jfp6815@rit.edu)
- [Steven Simmons](mailto:sdsimmons44@gmail.com)
- [Max Stein](mailto:mhs8558@rit.edu)
