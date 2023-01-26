# Budget App

![GitHub tag (latest by date)](https://img.shields.io/github/v/tag/yehor96/budget?label=version&style=plastic)
![GitHub Workflow Status (branch)](https://img.shields.io/github/actions/workflow/status/yehor96/budget/build.yml?branch=master&style=plastic)
![Sonar Tests](https://img.shields.io/sonar/tests/yehor96_budget/master?compact_message&server=https%3A%2F%2Fsonarcloud.io&style=plastic)

Application that provides functionality for counting one's budget and financial information.
Stack: Java 17, Spring Boot, Spring Data, Hibernate, PostgreSQL, Maven, Swagger, Docker, Lombok, Mockito

Setup:
- Make sure Java, Maven and Docker are installed. Docker should be up and running
- Clone repository 
 `git clone git@github.com:yehor96/budget.git budget-app`
- Enter project root folder and build a jar file:
 `mvn clean package -Dmaven.test.skip`
- Run docker compose to start the application:
 `docker-compose up --build`
- Access application at http://localhost:18080/swagger-ui/index.html (currently only API via Swagger)