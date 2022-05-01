# Kotlin-Ktor and postgres backend
## Introduction

Here lays a demonstration of my growing interest in Kotlin language and Ktor server framework. It's an example with complex Graph algorithms.

## Flow
To start the application you need to:
1. deploy the docker compose
2. sign up to the system `/signup`
3. log in to get access token `/login`
4. send post request with token to `/hirearchy` to create the hierarchy of the organization
5. send post request with token to `/hierarchy/{name}/supervisors` to get the supervisors of the user

## Starting the application
Go to the root directory of the project and deploy the application with following command:
```bash
docker-compose up
```
for shutting down the deployment:

```bash
docker-compose down -v
```
You might need root user access as well

### What it does?
We have REST API to post the JSON below. This JSON represents an Person -> Person relationship that looks like this:
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
The response to querying the endpoint where the root is at the top of the JSON nested dictionary. For instance, previous input would result in:
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
   { "Nick": {
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
       {"token":"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJodHRwOi8vMC4wLjAuMDo4MDgwL2hpZXJhcmNoeSIsImlzcyI6Imh0dHA6Ly8wLjAuMC4wOjgwODAvIiwiZXhwIjoxNjUwMTU3NjIxLCJ1c2VybmFtZSI6ImpvaG4ifQ.LSJUte7oy9Kv7qkozI3APBzPxHVZ56GID-n0lRIKvdY"}
```

Also, you can use following [Postman JSON collection file](/postman_collection.json) and test the application from Postman


To deploy the test environment and test the application:
   ```bash
   docker-compose --file docker-compose-test.yml up 
   ```
