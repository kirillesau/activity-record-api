# Activity Record API

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

The project is a simple REST API for managing activity records. It is a Spring Boot application that uses a PostgreSQL
database to store the data. The project provides an endpoint for uploading data in XLSX format, which is then stored in
the database.

## Disclaimer

The project and the functions offered have only been developed for a specific private purpose and only for the format
described below. The project is not intended for general use and is not suitable for it. The project is not maintained
and is not intended for further development. The project is not suitable for productive use.

## Upload Data

To upload data to the database, you need to send a POST request to the `/activity-record/upload` endpoint with xlsx file
in the body of
the request. The file should contain the following columns: `Name`, `Date of proof`, `Location`, `Featurename`,
`Description`, `Workload`, `Project-Number`, `Project` and  `Customer billable` (KV=true, NKV=false). The first lines
can be empty. The start of the table is determined using the `Name` column. The end of the table is marked by the
aggregating `Sum` cell. Both the 'Name' cell and the 'Sum' cell can be configured. Other formats are not supported.

| Name          | Date of proof | Location    | Featurename | Description   | Workload | Project-Number | Project   | Customer billable |
|---------------|---------------|-------------|-------------|---------------|----------|----------------|-----------|-------------------|
| John Doe      | 2024-01-01    | New York    | Feature X   | Description X | 9,50 h   | 12345          | Project X | KV                |
| Jane Smith    | 2024-01-02    | Los Angeles | Feature Y   | Description Y | 4,00 h   | 67890          | Project Y | NKV               |
| Alice Johnson | 2024-01-03    | Chicago     | Feature Z   | Description Z | 5,00 h   | 54321          | Project Z | KV                |
| Bob Brown     | 2024-01-04    | Houston     | Feature W   | Description W | 9,00 h   | 98765          | Project W | NKV               |
| Charlie Davis | 2024-01-05    | Phoenix     | Feature V   | Description V | 10,00 h  | 11223          | Project V | KV                |
|               |               |             |             | Sum           | 37,50 h  |                |           |                   |

### Configuration

Example configuration for the upload:

```properties
# Configuration for Excel import
activity-record.upload.name-cell=0
activity-record.upload.date_of_proof-cell=1
activity-record.upload.location-cell=2
activity-record.upload.work_package-cell=3
activity-record.upload.description-cell=4
activity-record.upload.workload-cell=5
activity-record.upload.project-cell=7
activity-record.upload.customer_billable-cell=8
```

### Example Request with cURL

```shell
curl -X POST http://localhost:8080/activity-record/upload \
  -H "Content-Type: multipart/form-data;charset=utf-8; boundary=boundary" \
  -F "file=@<path to xlsx-file>;type=application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
```

### Example Request with HTTPie

```http request
POST http://localhost:8080/activity-record/upload
Content-Type: multipart/form-data;charset=utf-8;boundary=boundary

--boundary
Content-Disposition: form-data; name="file"; filename="example.xlsx"
Content-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet

< <path to xlsx-file>
--boundary--
```

## Run the Application

To run the application, you need to execute the following command:

```shell
./gradlew bootRun
```

or

```shell
docker-compose up
```

## Build the Application

To build the application, you need to execute the following command:

```shell
./gradlew build
```

## Test the Application

To test the application, you need to execute the following command:

```shell
./gradlew test
```

## Useful SQL Queries

**Get number of entries per month:**

```postgresql
SELECT EXTRACT(MONTH FROM date_of_proof) AS Month,
       COUNT(*)                          AS "Number of Entries"
FROM ACTIVITY_RECORD
WHERE EXTRACT(YEAR FROM date_of_proof) = 2024
GROUP BY Month
ORDER BY Month;
```

**Get total workload per month, without travel time records:**

```postgresql
SELECT DATE_OF_PROOF, SUM(WORKLOAD) AS "Total workload"
FROM ACTIVITY_RECORD
WHERE WORK_PACKAGE <> 'Fahrzeiten' -- Travel time work package
GROUP BY DATE_OF_PROOF
ORDER BY DATE_OF_PROOF;
```

**Determines the number of days per location:**

```postgresql
SELECT LOCATION, COUNT(DISTINCT DATE_OF_PROOF) AS COUNT
FROM ACTIVITY_RECORD
GROUP BY LOCATION;
```

## Dependencies

The project uses the following dependencies:

- Spring Boot
- Spring Data JPA
- PostgreSQL
- H2 Database
- Apache POI
- MapStruct
- Flyway
- Testcontainers
- JUnit 5
- Lombok

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.