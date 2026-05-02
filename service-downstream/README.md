# quarkus-playground

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw quarkus:dev
```

## People API

The application exposes a REST API for managing people records with full CRUD operations.

### Endpoints

#### Create a new person
```http
POST /people
Content-Type: application/json

{
  "firstName": "João",
  "lastName": "Silva"
}
```

**Response:** `201 Created`
```json
{
  "id": 1,
  "firstName": "João",
  "lastName": "Silva"
}
```

#### Get all people
```http
GET /people
```

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "firstName": "João",
    "lastName": "Silva"
  }
]
```

#### Get person by ID
```http
GET /people/{id}
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "firstName": "João",
  "lastName": "Silva"
}
```

**Response:** `404 Not Found` (if person doesn't exist)

#### Update a person
```http
PUT /people/{id}
Content-Type: application/json

{
  "firstName": "João",
  "lastName": "Santos"
}
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "firstName": "João",
  "lastName": "Santos"
}
```

**Response:** `404 Not Found` (if person doesn't exist)

#### Delete a person
```http
DELETE /people/{id}
```

**Response:** `204 No Content`

**Response:** `404 Not Found` (if person doesn't exist)

### Testing with cURL

You can test the endpoints using cURL:

```bash
# Create a new person
curl -X POST http://localhost:8080/people \
  -H "Content-Type: application/json" \
  -d '{"firstName": "João", "lastName": "Silva"}'

# Get all people
curl http://localhost:8080/people

# Get person by ID
curl http://localhost:8080/people/1

# Update a person
curl -X PUT http://localhost:8080/people/1 \
  -H "Content-Type: application/json" \
  -d '{"firstName": "João", "lastName": "Santos"}'

# Delete a person
curl -X DELETE http://localhost:8080/people/1
```

### Running the Test Suite

The application includes a comprehensive test suite using JUnit 5, REST Assured, and H2 in-memory database.

#### Run all tests
```shell script
./mvnw test
```

#### Run only PeopleController tests
```shell script
./mvnw test -Dtest=PeopleControllerTest
```

#### Test Coverage

The test suite covers:
- ✅ Create person (success)
- ✅ Get all people (success)
- ✅ Get person by ID (success and not found)
- ✅ Update person (success and not found)
- ✅ Delete person (success and not found)
- ✅ Create multiple people
- ✅ Input validation (missing body)

**Test Results:**
```
Tests run: 12, Failures: 0, Errors: 0, Skipped: 0
```

### Database Configuration

#### Production (PostgreSQL)
- **Database:** PostgreSQL
- **Schema:** `quarkus-playground`
- **DDL Strategy:** update
- **Connection:** Configured via docker-compose

#### Test (H2)
- **Database:** H2 in-memory
- **DDL Strategy:** drop-and-create
- **Profile:** test
- **Auto-configured** for isolated testing

### Docker Setup

The application is configured to run with PostgreSQL using Docker Compose:

```bash
# Start all services (including PostgreSQL)
docker compose up -d

# Rebuild and restart the downstream service
docker compose build --no-cache quarkus-downstream
docker compose up -d quarkus-downstream
```

**Services:**
- `quarkus-downstream`: Main application (port 8080)
- `postgres`: PostgreSQL database (port 5432)
- `pgadmin`: Database management UI (port 5050)

## Running the Gatling test for the random names endpoint

For the simplest end-to-end execution, use the helper script:

```shell script
zsh ./scripts/run-gatling-random-names.zsh
```

You can forward Gatling properties to the script:

```shell script
zsh ./scripts/run-gatling-random-names.zsh -Dgatling.users=20 -Dgatling.rampSeconds=10 -Dgatling.repeatCount=5
```

If you prefer, start the application first and then execute the Gatling simulation manually:

```shell script
./mvnw quarkus:dev
./mvnw gatling:test -Dgatling.simulationClass=br.com.playground.performance.RandomNamesSimulation -Dgatling.baseUrl=http://localhost:8080
```

You can also customize the load profile:

```shell script
./mvnw gatling:test -Dgatling.simulationClass=br.com.playground.performance.RandomNamesSimulation -Dgatling.baseUrl=http://localhost:8080 -Dgatling.users=20 -Dgatling.rampSeconds=10 -Dgatling.repeatCount=5
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/quarkus-playground-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/maven-tooling>.

## Related Guides

- REST ([guide](https://quarkus.io/guides/rest)): A Jakarta REST implementation utilizing build time processing and Vert.x. This extension is not compatible with the quarkus-resteasy extension, or any of the extensions that depend on it.
- REST Jackson ([guide](https://quarkus.io/guides/rest#json-serialisation)): Jackson serialization support for Quarkus REST. This extension is not compatible with the quarkus-resteasy extension, or any of the extensions that depend on it
- Hibernate ORM with Panache ([guide](https://quarkus.io/guides/hibernate-orm-panache)): Simplify your persistence code for Hibernate ORM via the active record or the repository pattern
- JDBC Driver - PostgreSQL ([guide](https://quarkus.io/guides/datasource)): Connect to the PostgreSQL database via JDBC
- Micrometer metrics ([guide](https://quarkus.io/guides/micrometer)): Instrument the runtime and your application with dimensional metrics using Micrometer.
