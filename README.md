# Kotlin Ktor and postgres backend
Here lays a Kotlin web project with Ktor framework, Postgres database, and JWT Authentication.

The project comprises following ingredients:
- [Ktor](https://ktor.io/) server includes [JSON serializers](https://ktor.io/docs/serialization.html), [Authentication](https://ktor.io/docs/authentication.html), and [Testing](https://ktor.io/docs/testing.html)
- [Netty](https://netty.io/) web server
- [Postgres](https://www.postgresql.org/) as database
- [Exposed](https://github.com/JetBrains/Exposed) as ORM
- [Hikari Connection Pool](https://github.com/brettwooldridge/HikariCP)
- [Logback](https://logback.qos.ch/) for logging purposes
- [JBCrypt](https://www.mindrot.org/projects/jBCrypt/) for hashing passwords(No salting yet)

There is a simple implementation of Graph search and traverse with DFS algorithm but if you aim for a solid solution
better go for [GUAVA Graph](https://github.com/google/guava/wiki/GraphsExplained) from Google.

Project is agnostic about database, you can dynamically change Postgres to any other databases that Exposed JDBC
supports by changing a couple of variables:
- the database driver version in `gradle.properties`
- the database driver dependency in `build.gradle.kts`
- the **WEB_DB_URL** variable in `.env` file

## Flow
1. deploy the docker compose with `docker compose up -d` command
2. sign up to the system `/signup` with a username and password(not hardened enough)
3. log in to with username and password to get access token `/login`
4. send post request with payload and token to `/hirearchy` to create the hierarchy of the organization
5. send get request with token to `/hierarchy/{name}/supervisors` to fetch the supervisors of the current user

## How to use

> You need **root access** for docker

Go to the root directory of the project where `docker-compose.yml` is and change the environment variables in
`.env-example` with yours and rename the file to `mv .env-example .env` then deploy the application with following command:

```bash
docker-compose up
```

for shutting down the deployment run following command where the `docker-compose.yml` file resides:

```bash
docker-compose down -v
```

### What it does?

We have REST API to post the JSON below. This JSON represents a Person -> Person relationship that looks like this:

```json   
{
  "Pete": "Nick",
  "Barbara": "Nick",
  "Nick": "Sophie",
  "Sophie": "Jonas"
}
```

In this case, Nick is a supervisor of Pete and Barbara, Sophie supervises Nick. The supervisor list is
not always in order.

```bash
curl --request POST -sLv \
    --url 'http://localhost:3000/hierarchy' \
    --header "Content-Type: application/json" \
    --header "Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJodHRwOi8vMC4wLjAuMDo4MDgwL2hpZXJhcmNoeSIsImlzcyI6Imh0dHA6Ly8wLjAuMC4wOjgwODAvIiwiZXhwIjoxNjUwNDkwNzUwLCJ1c2VybmFtZSI6ImphbmUifQ.Xfn4JEOHo-Px7vy0TVyo3malCFlj3eFvzAJejqlefPM" \
    --data '{"Nick":"Barbara","Barbara":"Nick","Elias":"Levi"}'
   
```

The response to querying the endpoint where the root is at the top of the JSON nested dictionary. For instance, previous
input would result in:

```bash
curl --request GET -sLv \
    --url 'http://localhost:3000/hierarchy' \
    --header "Content-Type: application/json" \
    --header "Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJodHRwOi8vMC4wLjAuMDo4MDgwL2hpZXJhcmNoeSIsImlzcyI6Imh0dHA6Ly8wLjAuMC4wOjgwODAvIiwiZXhwIjoxNjUwNDkwNzUwLCJ1c2VybmFtZSI6ImphbmUifQ.Xfn4JEOHo-Px7vy0TVyo3malCFlj3eFvzAJejqlefPM"
```

the response will be:

```json
{
  "Jonas": {
    "Sophie": {
      "Nick": {
        "Pete": {},
        "Barbara": {}
      }
    }
  }
}
```

Query for a specific Person it's the hierarchy:

```bash
curl --request GET -sLv \
    --url 'http://localhost:3000/hierarchy/Nick/supervisors'\
    --header "Content-Type: application/json" \
    --header "Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJodHRwOi8vMC4wLjAuMDo4MDgwL2hpZXJhcmNoeSIsImlzcyI6Imh0dHA6Ly8wLjAuMC4wOjgwODAvIiwiZXhwIjoxNjUwNDkwNzUwLCJ1c2VybmFtZSI6ImphbmUifQ.Xfn4JEOHo-Px7vy0TVyo3malCFlj3eFvzAJejqlefPM"
```

the response of the query will be:

```json
{
  "Nick": {
    "Sophie": {
      "Jonas": {}
    }
  }
}
```

Sophie is the supervisor of the Nick and Jonas is supervisor of the supervisor of the Nick

## It's secured by JWT authentication

Sign up to the system

```bash
curl --request POST -sL \
    --url 'http://localhost:3000/signup'\
    --header "Content-Type: application/json" \
    --data '{"username":"jane","password":"doe"}'
  ```

login to the system

```bash
curl --request POST -sL \
    --url 'http://localhost:3000/login'\
    --header "Content-Type: application/json" \
    --data '{"username":"jane","password":"doe"}'
```

The response will be the access token

```json
{
  "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJodHRwOi8vMC4wLjAuMDo4MDgwL2hpZXJhcmNoeSIsImlzcyI6Imh0dHA6Ly8wLjAuMC4wOjgwODAvIiwiZXhwIjoxNjUwMTU3NjIxLCJ1c2VybmFtZSI6ImpvaG4ifQ.LSJUte7oy9Kv7qkozI3APBzPxHVZ56GID-n0lRIKvdY"
}
```

## How to test locally
For testing the project locally you can run docker compose with `docker-compose-test.yml` file. It will run the tests
against a test database.
```bash
docker-compose --file docker-compose-test.yml up 
```
After finishing the tests you can clean test data nd  shut the containers down with following command:

```bash
docker-compose --file docker-compose-test.yml down -v
```

## Continues Integration
For continues integration, the CI workflow prepares the database, run the gradle build with tests, and generates report
to Codacy about the quality of code.
