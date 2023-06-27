# SBOM Visualization and Integration Platform
> The SBOM Visualization and Integration Platform (**SVIP**) is a unified platform to promote the 
> production, consumption, and utilization of Software Bills of Materials.

## Latest Release
v1.0.0 - alpha (2/27/23)

- Electron.js App for Front end
- Spring-Boot backend to handle API requests
- SVIP Core that uses a Docker Container to generate SBOMs, merge them, and create a D3 JSON Visual

_Full sub-system READMEs & changelogs can be found in the `doc` directory_

## Quick Start
Build the project using:
```
$ ./gradlew build
```

### SBOM Generator CLI:
```
$ java -jar core/build/libs/core-1.0.0-alpha.jar
```

### API:
```
$ java -jar api/build/libs/api-1.0.0-alpha.jar
```

### API (Docker Container):
First ensure Docker is installed and running.
```
$ docker ps
```
Then build and run the container. The API can be accessed at `localhost:8080/svip`.
```
$ docker build -t svip .
$ docker run -p 8080:8080 svip
```

## Features
This is a list of all "features", or sub-systems that SVIP contains. Each links to their respective README.md file.
#### [Open Source Integrated SBOM Generation](doc/OSI/README.md) - Makes use of open source libraries to generate SBOMs
#### [SBOM Generation](doc/Generators/README.md) - Custom SBOM generation via source file and package manager file analysis
#### [SBOM VEX Generation](doc/VEX/README.md) - 
#### [SBOM Metrics](doc/Metrics/README.md) - 
#### [SBOM Comparison](doc/Comparer/README.md) - 
#### [SBOM Merging](doc/Merger/README.md) - 

## Contributors
**Principal Investigator:** [Mehdi Mirakhorli](mailto:mxmvse@rit.edu)

**Senior Project Manager:** [Chris Enoch](mailto:ctevse@rit.edu)

**Senior Developer Team Lead:** [Derek Garcia](mailto:dlg1206@rit.edu)

**Developer Team Leads**
- [Schuyler Dillon](mailto:sdd4181@rit.edu)
- [Tina DiLorenzo](mailto:tnd3015@rit.edu)
- [Tyler Drake](mailto:txd3634@rit.edu)
- [Matt London](mailto:mrl2534@rit.edu)
- [Dylan Mulligan](mailto:dtm5568@rit.edu)

**Developer Team**
- [Michael Alfonzetti](mailto:michael.alfonzetti93@gmail.com)
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
