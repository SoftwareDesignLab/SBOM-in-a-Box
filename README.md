# SBOM Visualization and Integration Platform
> The SBOM Visualization and Integration Platform (**SVIP**) is a unified platform to promote the 
> production, consumption, and utilization of Software Bills of Materials.

## Quick Start

### Deploy API with Docker
First ensure Docker is installed and running and then deploy using the docker-compose script.
```shell
# Ensure Docker is installed and running.
$ docker ps
# Build images & deploy and link API & MySQL containers.
$ docker compose up
```
See the SVIP API section of [doc/README.md](doc/README.md#svip-api) for detailed usage.

### SBOM Generator CLI:
```shell
# Build project from scratch.
$ ./gradlew build
 # Find and rename JAR file.
$ cd core/build/libs && move core-1.0.0-alpha.jar SBOMGeneratorCLI.jar
# Run JAR file and generate a CycloneDX JSON SBOM from the target path.
$ java -jar SBOMGeneratorCLI.jar <targetPath>
```
See the SBOM Generator CLI section of [doc/README.md](doc/README.md#sbom-generator-cli) for detailed usage.

## Latest Release
### [v5.0.0-alpha] - (6/29/2023)

#### Added
- `Dockerfile` & `docker-compose.yml` to build the API and run a MySQL server with persistent storage in separate
  Docker containers.
    - `application.properties` & `.env` files created to setup Spring and Docker configuration.
- `repository.SBOMFileRepository` Class to interact with the MySQL database.
- New API endpoints (`upload`, `view`, `viewAll`, `delete`) to run CRUD operations on the `files` table.
    - Added unit tests that mock the `repository.SBOMFileRepository` to avoid any local storage during testing.

#### Changed
- Refactored `NVIPApiController`, `PlugFestApiController`, & `NVIPApiController` Classes into `controller` package.
- Refactored `utils.Utils.SBOMFile` into its own class `model.SBOMFile`
    - Uses Hibernate decorators to automatically create a custom `files` table on the MySQL server.

#### Removed
- Old API endpoints & tests (`compare`, `generateSBOM`, `merge`, `parse`, & `qa`)

_Full sub-system READMEs & changelogs can be found in the `doc` directory_

## Features
This is a list of all "features", or sub-systems that SVIP contains. Each links to their respective README.md file.
- **Open Source Integrated SBOM Generation:** Makes use of open source libraries to generate SBOMs
- **SBOM Generation:** Custom SBOM generation via source file and package manager file analysis
- **SBOM VEX Generation:** 
- **SBOM Metrics:** 
- **SBOM Comparison:**
- **SBOM Merging:**

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
