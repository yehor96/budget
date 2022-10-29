# Budget App

![GitHub Workflow Status (branch)](https://img.shields.io/github/workflow/status/yehor96/budget/build-workflow/master)
![GitHub tag (latest by date)](https://img.shields.io/github/v/tag/yehor96/budget?label=version&style=plastic)

Application that provides functionality for counting one's budget and financial information. Available API is accessible via http://localhost:8080/swagger

Stack: Java 17, Spring Boot, Spring Data JPA, Swagger, PostgreSQL, Docker, Lombok, Mockito

Set up Postgres Db in Docker before working with the application:
```
docker run --name budget -p 5432:5432 -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=root -e POSTGRES_DB=budget -d postgres:alpine
```
