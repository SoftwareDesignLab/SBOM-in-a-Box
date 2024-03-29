# API
> Additional API documentation regarding quick-start, development, endpoints, and the database.

## Index

- [**System Requirements**](#system-requirements)
- [**SBOM-in-a-Box API**](#svip-api)
    - [Deployment](#deployment)
    - [Development](#development)
    - [Endpoints](#endpoints)
      - [Upload SBOM](#upload-sboms)
      - [Get SBOM File](#get-sbom-file)
      - [Get all SBOMs IDs](#get-all-sboms-ids)
      - [Get SBOM Object](#get-sbom-object)
      - [Delete SBOMs](#delete-sboms)
      - [Convert SBOM](#convert-an-sbom)
      - [Compare SBOMs](#compare-sboms)
      - [Merge SBOMs](#merge-sboms)
      - [Generate Quality Report](#generate-quality-report)
      - [Generate SBOM with SBOM-in-a-Box](#generate-an-sbom-using-svip-parsers)
      - [Upload a project for OSI](#upload-a-project-for-osi)
      - [Get List of Open-Source Tools Supported By OSI](#get-list-of-open-source-tools-supported-by-osi)
      - [Generate SBOM with OSI](#generate-an-sbom-using-osi)
      - [Generate VEX](#generate-vex)
    - [MySQL Database](#mysql-database)

# System Requirements
- Java 17
- Gradle
- Docker (run `docker ps` to determine installation status)

---

# SVIP API
> The SBOM-in-a-Box back-end API. See [Endpoints](#endpoints) for detailed endpoint documentation.

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
- [Get SBOM File](#get-sbom-file)
- [Get all SBOMs IDs](#get-all-sboms-ids)
- [Get SBOM Object](#get-sbom-object)
- [Delete SBOMs](#delete-sboms)
- [Convert SBOM](#convert-an-sbom)
- [Compare SBOMs](#compare-sboms)
- [Merge SBOMs](#merge-sboms)
- [Generate Quality Report](#generate-quality-report)
- [Generate SBOM with SBOM-in-a-Box](#generate-an-sbom-using-svip-parsers)
- [Upload a project for OSI]()
- [Get List of Open-Source Tools Supported By OSI](#get-list-of-open-source-tools-supported-by-osi)
- [Generate SBOM with OSI](#generate-an-sbom-using-osi)
- [Generate VEX](#generate-vex)

### Upload SBOMs
> Upload an SBOM file to the Database

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
|      200      |  Long  |               The ID corresponding to the uploaded file                |
|      400      | String | Invalid field(s) / Unable to process SBOM. Body contains error message |

**Example**
```bash
curl -X POST -d '{"fileName":"mySBOM","contents":"SBOM Data"}' http://localhost:8080/svip/sboms
```

### Get SBOM File
> Get the contents of an SBOM object from the Database

**Endpoint:** `http://localhost:8080/svip/sboms/content`

**Request Method:** `GET`

**Parameters**

| Parameter | Type |             Description             | Is Required? |
|:---------:|:----:|:-----------------------------------:|:------------:|
|    id     | Long | The ID of the SBOM file to retrieve |     YES      |

**Responses**

| Response Code |   Type   |                 Description                 |
|:-------------:|:--------:|:-------------------------------------------:|
|      200      | SBOMFile | JSON File containing SBOM name and contents |
|      404      |  String  |      SBOM with given ID does not exist      |

**Example**
```bash
curl -X GET -G http://localhost:8080/svip/sboms/content -d 'id=1'
```

### Get all SBOMs IDs
> Get all SBOM ids stored in the database

**Endpoint:** `http://localhost:8080/svip/sboms`

**Request Method:** `GET`

**Responses**

| Response Code |  Type  |       Description       |
|:-------------:|:------:|:-----------------------:|
|      200      | Long[] |    Array of SBOM IDs    |
|      204      |  ---   | No data in the database |

**Example**
```bash
curl -X GET http://localhost:8080/svip/sboms
```

### Get SBOM Object
> Get a single deserialized SBOM object from the database using its ID

**Endpoint:** `http://localhost:8080/svip/sbom`

**Request Method:** `GET`

**Parameters**

| Parameter | Type |             Description             | Is Required? |
|:---------:|:----:|:-----------------------------------:|:------------:|
|    id     | Long | The ID of the SBOM file to retrieve |     YES      |

**Responses**

| Response Code |  Type  |            Description            |
|:-------------:|:------:|:---------------------------------:|
|      200      |  SBOM  |     A JSON of the SBOM Object     |
|      404      | String | SBOM with given ID does not exist |
|      500      | String |   Failed to deserialize content   |

**Example**
```bash
curl -X GET -G http://localhost:8080/svip/sbom -d 'id=1'
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

| Response Code | Type |            Description            |
|:-------------:|:----:|:---------------------------------:|
|      200      | Long |    The id of the deleted SBOM     |
|      404      | ---  | SBOM with given ID does not exist |

**Example**
```bash
curl -X DELETE -G http://localhost:8080/svip/sboms 'id=1'
```

### Convert an SBOM
> Convert an SBOM to a desired format and schema

**Endpoint:** `http://localhost:8080/svip/sboms`

**Request Method:** `PUT`

**Parameters**

| Parameter |       Type        |            Description             | Is Required? |
|:---------:|:-----------------:|:----------------------------------:|:------------:|
|    id     |       Long        | The ID of the SBOM file to convert |     YES      |
|  schema   | Serializer.Schema |        Schema to convert to        |     YES      |
|  format   | Serializer.Format |        Format to convert to        |     YES      |
| overwrite |      Boolean      |        Overwrite given file        |     YES      |

**Supported Schema Params:** `CDX14`, `SPDX23`, `SVIP`
**Supported Format Params:** `JSON`, `TAGVALUE`

**Responses**

| Response Code |  Type  |            Description            |
|:-------------:|:------:|:---------------------------------:|
|      200      | String |          Converted SBOM           |
|      204      |  ---   |         No content found          |
|      404      |  ---   | SBOM with given ID does not exist |
|      500      |  ---   |      SBOM Failed to convert       |

**Example**
```bash
curl -X PUT -G http://localhost:8080/svip/sboms/content \
-d 'id=1' \
-d 'schema=SPDX23' \
-d 'format=TAGVALUE' \
-d 'overwrite=true'
```

### Compare SBOMs
> Compares two or more given SBOMs (split into filename and contents), with the first one used as the baseline, and returns a comparison report.

**Endpoint:** `http://localhost:8080/svip/sboms/compare`

**Request Method:** `POST`

**Parameters**

|  Parameter  |  Type   |          Description          | Is Required? |
|:-----------:|:-------:|:-----------------------------:|:------------:|
|     ids     | Long[]  |       Array of SBOM IDs       |     YES      |
| targetIndex | Integer | Index of target SBOM in array |     YES      |

**Responses**

| Response Code |    Type    |                    Description                    |
|:-------------:|:----------:|:-------------------------------------------------:|
|      200      | Comparison |     A Comparison report of all provided SBOMs     |
|      400      |   String   | A list of invalid fields received from the client |
|      500      |   String   |          SBOM Failed to be deserialized           |

**Example**
```bash
curl -X POST -G http://localhost:8080/svip/sboms/compare \
-d 'ids=[1,2]' \
-d 'targetIndex=0'
```

### Merge SBOMs
> Merge 2 SBOMs together, regardless of origin format

**Endpoint:** `http://localhost:8080/svip/sboms/merge`

**Request Method:** `POST`

**Request Body**

| Body |  Type   |          Description          | Is Required? |
|:----:|:-------:|:-----------------------------:|:------------:|
| ids  | Long[]  |       Array of SBOM IDs       |     YES      |

**Responses**

| Response Code |  Type  |          Description           |
|:-------------:|:------:|:------------------------------:|
|      200      |  Long  |     ID of the merged SBOM      |
|      400      |  ---   |    SBOM has null properties    |
|      404      |  ---   |     Error in merging SBOMs     |
|      500      | String | SBOM Failed to be deserialized |

**Example**
```bash
curl -X POST -d '{"ids":[1,2]}' http://localhost:8080/svip/sboms/merge
```

### Generate Quality Report
> Analyze a given SBOM file and return a QualityReport

**Endpoint:** `http://localhost:8080/svip/sboms/qa`

**Request Method:** `GET`

**Parameters**

| Parameter | Type |                Description                 | Is Required? |
|:---------:|:----:|:------------------------------------------:|:------------:|
|    id     | Long | The ID of the SBOM file to run QA tests on |     YES      |

**Responses**

| Response Code |     Type      |            Description            |
|:-------------:|:-------------:|:---------------------------------:|
|      200      | QualityReport |  JSON QualityReport of the SBOM   |
|      404      |      ---      | SBOM with given ID does not exist |
|      500      |    String     |  SBOM Failed to be deserialized   |

**Example**
```bash
curl -X GET -G http://localhost:8080/svip/sboms/qa -d 'id=1'
```

### Generate an SBOM using SBOM-in-a-Box Parsers
> Generates an SBOM with a given schema and format from project source files using SBOM-in-a-Box parsers. 
> Request body contains an array of multiple objects with properties fileName and contents (one for each source file)

**Endpoint:** `http://localhost:8080/svip/generators/parsers`

**Request Method:** `POST`

**Parameters**

|  Parameter  |       Type        |       Description        | Is Required? |
|:-----------:|:-----------------:|:------------------------:|:------------:|
|   zipFile   |   MultipartFile   | Zipped folder of project |     YES      |
| projectName |      String       | Name of SBOM to generate |     YES      |
|   schema    | Serializer Schema |   Schema to convert to   |     YES      |
|   format    | Serializer Format |   Format to convert to   |     YES      |

> Note: Request must be sent using type multipart/form-data

**Responses**

| Response Code |  Type  |                  Description                  |
|:-------------:|:------:|:---------------------------------------------:|
|      200      |  Long  | ID from Generated SBOM from supported project |
|      404      | String |    Description of error during generation     |
|      400      | String |    Schema, format or body contents invalid    |

**Example**
```bash
curl -X POST -G http://localhost:8080/svip/generators/parsers \
-d 'projectName=Java_Project_1' \
-d 'schema=SPDX23' \
-d 'format=TAGVALUE' \
-d '[{"fileName": "testfile1.java", "contents": "..."}, {"fileName": "testfile2.java", "contents": "..."}]'
```

### Upload a project for OSI
> Upload a code project to generate SBOMs for using OSI

**Endpoint:** `http://localhost:8080/svip/generators/osi/project`

**Request Method:** `POST`

**Request Body**

| Parameter |     Type      |       Description        | Is Required? |
|:---------:|:-------------:|:------------------------:|:------------:|
|  project  | MultipartFile | Zipped folder of project |     YES      |


**Responses**

| Response Code |   Type   |                   Description                    |
|:-------------:|:--------:|:------------------------------------------------:|
|      200      | String[] | A list of tools relevant to the uploaded project |
|      400      |  String  |        Error message, project file is bad        |
|      404      |  String  |          Error message, OSI is disabled          |
**Example**
```bash
curl --request POST \
  --url http://localhost:8080/svip/generators/osi/project \
  --header 'Content-Type: multipart/form-data' \
  --form 'project=path\to\project.zip'
```


### Get List of Open-Source Tools Supported By OSI
> Gets a list of all tool names currently supported by [Open Source Integration](README.md#open-source-integration) that
> can then be passed into /generators/osi.

**Endpoint:** `http://localhost:8080/svip/generators/osi/tools`

**Request Method:** `GET`

**Parameters**

| Parameter |  Type  |             Description             | Is Required? |
|:---------:|:------:|:-----------------------------------:|:------------:|
|   list    | String | Either `all` (default) or `project` |      NO      |

* `all`: Get all tools installed on OSI
* `project`: Get all tools relevant to the project uploaded to OSI


**Responses**

| Response Code |   Type   |                   Description                   |
|:-------------:|:--------:|:-----------------------------------------------:|
|      200      | String[] |            A list of all tool names             |
|      404      |  String  |         Error message, OSI is disabled          |
|      500      |  String  | Error message, failed to get tool list from OSI |

**Example**
```bash
curl -X GET -G http://localhost:8080/svip/generators/osi/tools
curl -X GET -G http://localhost:8080/svip/generators/osi/tools?list=project
```

### Generate an SBOM using OSI
> Generates an SBOM with a given schema and format from project source files using 
> [Open Source Integration](README.md#open-source-integration).
> Request body contains an array of multiple objects with properties fileName and contents (one for each source file)
> Use the http://localhost:8080/svip/generators/osi/project endpoint to upload a project before generation

**Endpoint:** `http://localhost:8080/svip/generators/osi`

**Request Method:** `POST`

**Parameters**

|  Parameter  |       Type        |                                                      Description                                                      | Is Required? |
|:-----------:|:-----------------:|:---------------------------------------------------------------------------------------------------------------------:|:------------:|
| projectName |      String       |                                               Name of SBOM to generate                                                |     YES      |
|   schema    | Serializer Schema |                                                  Schema to generate                                                   |     YES      |
|   format    | Serializer Format |                                                  Format to generate                                                   |     YES      |
|  toolNames  |     String[]      | JSON-formatted string array of open-source tool names to use when generating SBOMs. If not provided, defaults to all. |      NO      |


**Responses**

| Response Code |  Type  |                            Description                            |
|:-------------:|:------:|:-----------------------------------------------------------------:|
|      200      |  Long  |           ID from Generated SBOM from supported project           |
|      204      |        | No SBOMs were generated by the OSI container or able to be parsed |
|      400      | String |              Schema, format or body contents invalid              |
|      404      | String |              Description of error during generation               |
|      500      | String |                           Error message                           |

**Example**
```bash
curl --request POST \
  --url 'http://localhost:8080/svip/generators/osi?projectName=foo&schema=SPDX23&format=TAGVALUE'
```

### Generate VEX
> Generate a VEX document using an SBOM's components and information

**Endpoint:** `http://localhost:8080/svip/sboms/vex`

**Request Method:** `GET`

**Headers**

| Parameter |  Type  |          Description          | Is Required? |
|:---------:|:------:|:-----------------------------:|:------------:|
|  apiKey   | String | The user's NVD API Key to use |      NO      |


**Parameters**

| Parameter |  Type  |                    Description                     | Is Required? |
|:---------:|:------:|:--------------------------------------------------:|:------------:|
|    id     |  Long  | The ID of the SBOM file to get information for VEX |     YES      |
|  format   | String |           The format of the VEX Document           |     YES      |
|  client   | String |               The API Client to use                |     YES      |

**Supported format Params:** `[CycloneDX`](https://github.com/CycloneDX/bom-examples/tree/master/VEX), [`CSAF`](https://github.com/oasis-tcs/csaf/tree/master/csaf_2.0/examples/csaf/csaf_vex)

**Supported Client Params:** [`nvd`](https://nvd.nist.gov/), [`osv`](https://osv.dev/)

**Responses**

| Response Code |   Type    |                  Description                  |
|:-------------:|:---------:|:---------------------------------------------:|
|      200      | VEXResult | A VEX and HashMap of any errors that occurred |
|      400      |    ---    |           Invalid Format or Client            |
|      404      |    ---    |       SBOM with given ID does not exist       |
|      500      |    ---    |    The message of the error that occurred     |

**Example**
```bash
curl -X GET -G http://localhost:8080/svip/sboms/vex \
-H 'apiKey: 0imfnc8mVLWwsAawjYr4Rx-Af50DDqtlx' \
-d 'id=1' \
-d 'format=CSAF' \
-d 'client=OSV'
```

---

## MySQL Database
Located at `localhost:3306` while the `svip-mysql` Docker container is running.

Use the following command to interact with the MySQL server instance:
```shell
$ docker exec -it svip-mysql mysql -uroot -psvipMySQL -D svip -e "<YOUR SQL STATEMENT HERE>"
```
#### Table `files` Schema:
|   Field   |    Type    | Null | Key | Default | Extra |
|:---------:|:----------:|:----:|:---:|:-------:|:-----:|
|    id     | bigint(20) |  NO  | PRI |  NULL   |       |
| contents  |  longtext  | YES  |     |  NULL   |       |
| file_name |    text    | YES  |     |  NULL   |       |
