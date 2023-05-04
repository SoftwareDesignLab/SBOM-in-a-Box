# OSI Container v2.0.0

> Docker Container that builds environment to install several Open Source SBOM Generations using a given
> project directory and generate CycloneDX and SPDX SBOMs into a target out directory.

### Supported Tools
- [Sonatype Jake](https://github.com/sonatype-nexus-community/jake)
- [CycloneDX Conan](https://github.com/CycloneDX/cyclonedx-conan)
- [CycloneDX Generator](https://github.com/CycloneDX/cdxgen) 
- [CycloneDX Python](https://github.com/CycloneDX/cyclonedx-python)
- [CycloneDX PHP](https://github.com/CycloneDX/cyclonedx-php-composer)
- [SPDX SBOM Generator](https://github.com/opensbom-generator/spdx-sbom-generator)
- [Syft](https://github.com/anchore/syft)
- [JBOM](https://github.com/eclipse/jbom)

### Quick Start
> Make sure the Docker Daemon / Docker Desktop is running and running commands inside the directory with the Dockerfile
1. In the following commands replace `{project_path}` with the path to the project you wish to scan 
2. `docker build -t svip-osi .`
3. `docker run --rm --mount type=bind,source={project_path},target=/bound_dir/code --mount type=bind,source="$(pwd)"/bound_dir/sboms,target=/bound_dir/sboms -it svip-osi`

## Building the Image

To build the image navigate to the Dockerfile execute the following:

```shell
$ docker build -t svip-osi .
```

The first build will take up to 6 minutes to complete, but subsequent builds will be significantly faster

### Build Arguments

`build` - Instructs docker to build a container

`-t` - Specifies the name of the container to create

`.` - Directory where docker should look for the build instructions (Dockerfile)

## Running the Container

To run the container, first place your code in the code/ directory. Then execute the container and bind the folder:

```shell
$ docker run --rm --mount type=bind,source={project_path},target=/bound_dir/code --mount type=bind,source="$(pwd)"/bound_dir/sboms,target=/bound_dir/sboms -it svip-osi
```

### Execution breakdown

`run` - Tells docker to run the given container

`--rm` - After running container cleanup and delete it from cache

`--mount ...` - Mount a folder on the host ```$(pwd)/bound_dir``` to a folder on the container ```/bound_dir```

`-it` - Run in interactive mode (allows ^C and user input)

`svip-osi` - Name of container to run


### Result

The `sboms/` directory will now contain generated SBOMs

## Contributors
- Matt London
- Derek Garcia
- Ian Dunn
