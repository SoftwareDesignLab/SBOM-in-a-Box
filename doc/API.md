# API
> Additional API documentation regarding quick-start, development, endpoints, and the database.

## Index

- [**System Requirements**](#system-requirements)
- [**SVIP API**](#svip-api)
    - [Deployment](#deployment)
    - [Development](#development)
    - [Endpoints](#endpoints)
      - [Upload SBOMs](#upload-sboms)
      - [Delete SBOMs](#delete-sboms)
      - [Get SBOMs](#get-sboms)
      - [Get Single SBOM](#get-single-sbom)
      - [Get SBOM Object](#get-sbom-contents)
      - [Convert SBOM](#convert-an-sbom)
      - [Generate SBOM with SVIP](#generate-an-sbom-using-svip-parsers)
      - [Apply SBOM Metrics](#quality-attributes-testing)
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

## Endpoints
The API is located on `http://localhost:8080/svip`.

- [Upload SBOM](#upload-sboms)

### Upload SBOMs
> Upload an SBOM file to the SQL Database

**Endpoint:** `http://localhost:8080/svip/sboms`

**Request Method:** `POST`

**Request Body**

|   Body   |  Type  |          Description          | Is Required? |
|:--------:|:------:|:-----------------------------:|:------------:|
| fileName | String |   The name of the SBOM file   |     YES      |
| contents | String | The contents of the SBOM file |     YES      |

**Responses**

| Response Code |  Type  |                              Description                               |
|:-------------:|:------:|:----------------------------------------------------------------------:|
|      200      | String |               The ID corresponding to the uploaded file                |
|      400      | String | Invalid field(s) / Unable to process SBOM. Body contains error message |

**Example**
```bash
curl -X POST -d '{"fileName":"mySBOM","contents":"{SBOM Data...}"}' http://localhost:8080/svip/sboms
```

### Delete SBOMs
> Delete an SBOM file from the SQL Database using its ID

**Endpoint:** `http://localhost:8080/svip/sboms`

**Request Method:** `DELETE`

**Parameters**

| Parameter | Type |             Description             | Is Required? |
|:---------:|:----:|:-----------------------------------:|:------------:|
|    id     | Long | The ID of the SBOM file to retrieve |     YES      |

**Responses**

| Response Code |  Type  |          Description          |
|:-------------:|:------:|:-----------------------------:|
|      200      | String | The contents of the SBOM file |

**Example**
```bash
curl -X DELETE -G http://localhost:8080/svip/sboms \
-d 'id=<SBOM UID>'
```

### Get SBOMs
> Get all existing uploaded file IDs

**Endpoint:** `http://localhost:8080/svip/sboms`

**Request Method:** `GET`

**Responses**

| Response Code |  Type  |              Description              |
|:-------------:|:------:|:-------------------------------------:|
|      200      | String | A JSON array of all uploaded file IDs |

**Example**
```bash
curl -X GET http://localhost:8080/svip/sboms
```

### Get Single SBOM
> Get a single deserialized SBOM object from the SQL Database using its ID

**Endpoint:** `http://localhost:8080/svip/sboms`

**Request Method:** `GET`

**Parameters**

| Parameter | Type |             Description             | Is Required? |
|:---------:|:----:|:-----------------------------------:|:------------:|
|    id     | Long | The ID of the SBOM file to retrieve |     YES      |

**Responses**

| Response Code |  Type  |          Description          |
|:-------------:|:------:|:-----------------------------:|
|      200      | String |   A JSON of the SBOM Object   |
|      404      | String |      File does not exist      |
|      500      | String | Failed to deserialize content |

**Example**
```bash
curl -X GET -G http://localhost:8080/svip/sboms \
-d 'id=<SBOM UID>'
```

### Get SBOM Object
> Get a single SBOM object from the SQL Database using its ID

**Endpoint:** `http://localhost:8080/svip/sboms/content`

**Request Method:** `GET`

**Parameters**

| Parameter | Type |             Description             | Is Required? |
|:---------:|:----:|:-----------------------------------:|:------------:|
|    id     | Long | The ID of the SBOM file to retrieve |     YES      |

**Responses**

| Response Code |  Type  |          Description          |
|:-------------:|:------:|:-----------------------------:|
|      200      | String |   A JSON of the SBOM Object   |
|      404      | String |      File does not exist      |
|      500      | String | Failed to deserialize content |

**Example**
```bash
curl -X GET -G http://localhost:8080/svip/sboms/content \
-d 'id=<SBOM UID>'
```

### Convert an SBOM
> Convert an SBOM to a desired format and schema

**Endpoint:** `http://localhost:8080/svip/sboms/`

**Request Method:** `PUT`

**Parameters**

| Parameter |       Type        |            Description             | Is Required? |
|:---------:|:-----------------:|:----------------------------------:|:------------:|
|    id     |       Long        | The ID of the SBOM file to convert |     YES      |
|  schema   | Serializer Schema |        Schema to convert to        |     YES      |
|  format   | Serializer Format |        Format to convert to        |     YES      |
| overwrite |      Boolean      |      Converted SBOM or Error       |     YES      |

**Responses**

| Response Code |  Type  |              Description               |
|:-------------:|:------:|:--------------------------------------:|
|      200      | String |             Converted SBOM             |
|      204      | String |            No content found            |
|      400      | String |         SBOM with ID not found         |
|      500      | String | Description of error during conversion |

**Example**
```bash
curl -X PUT -G http://localhost:8080/svip/sboms/content \
-d 'id=<SBOM UID>' \
-d 'schema=SPDX23' \
-d 'format=TAGVALUE' \
-d 'overwrite=true'
```

### Generate an SBOM using SVIP Parsers
> Generates an SBOM with a given schema and format from project source files using SVIP parsers. 
> Request body contains an array of multiple objects with properties fileName and contents (one for each source file)

**Endpoint:** `http://localhost:8080/svip/generators/parsers`

**Request Method:** `POST`

**Parameters**

| Parameter |       Type        |            Description             | Is Required? |
|:---------:|:-----------------:|:----------------------------------:|:------------:|
|    id     |       Long        | The ID of the SBOM file to convert |     YES      |
|  schema   | Serializer Schema |        Schema to convert to        |     YES      |
|  format   | Serializer Format |        Format to convert to        |     YES      |

**Request Body**

| Body  |    Type    |              Description               | Is Required? |
|:-----:|:----------:|:--------------------------------------:|:------------:|
| files | SBOMFile[] | fileName: string, fileContents: string |     YES      |

**Responses**

| Response Code |  Type  |               Description               |
|:-------------:|:------:|:---------------------------------------:|
|      200      | String |        A JSON of the SBOM Object        |
|      404      | String | Description of error during generation  |
|      400      | String | Schema, format or body contents invalid |

**Example**
```bash
curl -X PUT -G http://localhost:8080/svip/sboms/content \
-d 'id=<SBOM UID>' \
-d 'schema=SPDX23' \
-d 'format=TAGVALUE' \
-d '[{"fileName": "testfile1.java", "contents": "..."}, {"fileName": "testfile2.java", "contents": "..."}]'
```

### Quality Attributes Testing
> Analyze a given SBOM file and return a QualityReport

**Endpoint:** `http://localhost:8080/svip/sboms/qa`

**Request Method:** `GET`

**Parameters**

| Parameter | Type |                Description                 | Is Required? |
|:---------:|:----:|:------------------------------------------:|:------------:|
|    id     | Long | The ID of the SBOM file to run QA tests on |     YES      |

**Responses**

| Response Code |     Type      |              Description               |
|:-------------:|:-------------:|:--------------------------------------:|
|      200      | QualityReport |      A QualityReport of the SBOM       |
|      500      |    String     | The message of the error that occurred |

**Example**
```bash
curl -X GET -G http://localhost:8080/svip/sboms/qa \
-d 'id=<SBOM UID>'
```

## MySQL Database
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