# API
> Additional API documentation regarding quick-start, development, endpoints, and the database.

## Index

- [**System Requirements**](#system-requirements)
- [**SVIP API**](#svip-api)
    - [Deployment](#deployment)
    - [Development](#development)
    - [Usage](#usage-1)
      - [MySQL Database](#mysql-database)

# System Requirements
- Java 17
- Gradle
- Docker (run `docker ps` to determine installation status)

---

# SVIP API
> The SVIP back-end API. See [Usage](#usage) for more details of the endpoints.

## Deployment
First ensure Docker is installed and running and then deploy using the docker-compose script.
```shell
# Ensure Docker is installed and running
$ docker ps
# Start API & MySQL containers. Use --build to force rebuild the image with any updated source code
$ docker compose up
```

## Development
To modify and test this project, you will need to run the MySQL server in a Docker container and the API detached as
either a compiled JAR file or with your IDE of choice.

```shell
# Build detached API jar (skip if running in IDE)
$ ./gradlew build
# Build and deploy MySQL server ONLY to allow running API outside of its container. Use -d to run detached.
$ docker compose up mysql
# Rename jar file
$ move api/build/libs/api-1.0.0-alpha.jar SVIP_API.jar
# Run detached jar file or in IDE
$ java -jar SVIP_API.jar
```
#### Tips
- Append `-x test` to the Gradle build command in the Dockerfile to skip Gradle tests. This makes it much faster as
  skipping the tests saves 1-2 minutes per build.
- To edit the MySQL configuration/Docker port mappings, edit the `.env` file in the repository root.

## Usage
The API is located on `localhost:8080/svip`.

Current Endpoints (`/svip/`):
- `/upload` - Upload an SBOM to the server.
- `/view` - View the raw contents of an SBOM file.
- `/viewFiles` - View all file IDs uploaded to the server.
- `/delete` - Delete a file from the server.
- `/getSBOM` - Get an SBOM object from the server

### MySQL Database
Located at `localhost:3306` while the `svip-mysql` Docker container is running.

Use the following command to interact with the MySQL server instance:
```shell
$ docker exec -it svip-mysql mysql -uroot -psvipMySQL -D svip -e "<YOUR SQL STATEMENT HERE>"
```
#### Table `files` Schema:
| Field     | Type       | Null | Key | Default | Extra |
|:---------:|:----------:|:----:|:---:|:-------:|:-----:|
| id        | bigint(20) | NO   | PRI | NULL    |       |
| contents  | longtext   | YES  |     | NULL    |       |
| file_name | text       | YES  |     | NULL    |       |