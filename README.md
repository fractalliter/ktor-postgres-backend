# Backend development task

## Introduction: Welcome to the Ever-changing Hierarchy GmbH.
Help HR manager Personia get a grasp of her ever-changing company’s hierarchy! Every week
Personia receives a JSON of employees and their supervisors from her demanding CEO Chris,
who keeps changing his mind about how to structure his company. Personia wants a tool to help
her better understand the employee hierarchy and respond to employee’s queries about who
their boss is. Can you help her?

## Flow
To start the application you need to:
1. deploy the docker compose
2. sign up to the system 
3. log in to get access token
4. send post request with token to `/hirearchy` to create the hierarchy of the organization
5. send post request with token to `/hierarchy/{name}/supervisors` to get the supervisors of the user

## Starting the application
Go to the root directory of the project and deploy the application with following command:
```bash
docker-compose up
```
for shutting down the deployment:

```bash
docker-compose down
```
You might need root user access as well

### Personia’s initial requirements were the following:
1. I would like a pure REST API to post the JSON from Chris. This JSON represents an Employee ->
   Supervisor relationship that looks like this:
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
      curl --request POST -sL \
           --url 'http://localhost:8080/hierarchy' \
           --header "Content-Type: application/json" \
           --header "Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJodHRwOi8vMC4wLjAuMDo4MDgwL2hpZXJhcmNoeSIsImlzcyI6Imh0dHA6Ly8wLjAuMC4wOjgwODAvIiwiZXhwIjoxNjUwMjQ3MDY1LCJ1c2VybmFtZSI6ImpvaG4ifQ.-g83XtVNtLInuPBe76FIRhNo1lZxosmZyUG-_VIUPXc" \
           --data '{"Pete":"Nick","Barbara":"Nick","Nick":"Sophie","Sophie":"Jonas"}'
   ```
2. As a response to querying the endpoint, I would like to have a properly formatted JSON which
   reflects the employee hierarchy in a way, where the most senior employee is at the top of the JSON
   nested dictionary. For instance, previous input would result in:
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
   Sometimes Chris gives me nonsense hierarchies that contain loops or multiple roots. I would be
   grateful if the endpoint could handle the mistakes and tell her what went wrong. The more
   detailed the error messages are, the better!

3. I would really like it if the hierarchy could be stored in a relational database (e.g. SQLite) and
   queried to get the supervisor and the supervisor’s supervisor of a given employee. I want to send
   the name of an employee to an endpoint, and receive the name of the supervisor and the name of
   the supervisor’s supervisor in return.
   ```bash
   curl --request GET -sL \
        --url 'http://localhost:8080/hierarchy/Nick/supervisors'\
        --header "Content-Type: application/json" \
        --header "Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJodHRwOi8vMC4wLjAuMDo4MDgwL2hpZXJhcmNoeSIsImlzcyI6Imh0dHA6Ly8wLjAuMC4wOjgwODAvIiwiZXhwIjoxNjUwMjQ3MDY1LCJ1c2VybmFtZSI6ImpvaG4ifQ.-g83XtVNtLInuPBe76FIRhNo1lZxosmZyUG-_VIUPXc"
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

4. I would like the API to be secure so that only I can use it. Please implement some kind of
   authentication.
   1. Sign up to the system
      ```bash
      curl --request POST -sL \
           --url 'http://localhost:8080/signup'\
           --header "Content-Type: application/json" \
           --data '{"username":"john","password":"doe"}'
      ```
   2. login to the system
      ```bash
      curl --request POST -sL \
           --url 'http://localhost:8080/login'\
           --header "Content-Type: application/json" \
           --data '{"username":"john","password":"doe"}'
      ```
      and the response will be access token
      ```json
       {"token":"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJodHRwOi8vMC4wLjAuMDo4MDgwL2hpZXJhcmNoeSIsImlzcyI6Imh0dHA6Ly8wLjAuMC4wOjgwODAvIiwiZXhwIjoxNjUwMTU3NjIxLCJ1c2VybmFtZSI6ImpvaG4ifQ.LSJUte7oy9Kv7qkozI3APBzPxHVZ56GID-n0lRIKvdY"}
      ```

Also, you can use following [Postman JSON collection file](/Personio.postman_collection.json) and test the application from Postman

### What we expect from you:
1. Write a small and simple application according to Personia’s specifications, no more, no less.
2. Provide clear and easy installation instructions (think about the reviewers!). The less steps we have
   to do to get this running, the better. If we can’t get your app to run, we won’t be able to review it.
   Docker is your friend!
3. A set of working unit/functional tests to cover all the use cases you can think of.
   Ideally take our challenge in Kotlin or Java (whichever you feel most comfortable with). PHP and Ruby
   are also acceptable, since we use them in our legacy applications. If you prefer another language, please
   reach out to us first. Remember that your solution must not require us to install any stack specific tool in
   order to test the result of your work, so use Docker in this case.
   To execute the tests:
   ```bash
   docker-compose --file docker-compose-test.yml up 
   ```
   
### What we (mainly) look at when checking out the solution:
1. Did you follow the instructions, i.e. does your solution work and meet Personia’s requirements?
   Would Personia be happy to use your solution for her work?
2. Is your solution easy to read and understand? Would we be happy to contribute to it?
3. Is your code ready for production (stable, secure, scalable)?

### Final notes

● This challenge is about showing us how you think as an engineer, how you structure your code and
how you solve problems, not how well you know tool X or design pattern Y.

● Try to design and implement your solution as you would do for real production code. Show us how
you create robust, stable and maintainable code.

● Be prepared to demonstrate your solution with a tool like curl or Postman

● There is no right or wrong, just be ready to discuss why you choose one approach over another!