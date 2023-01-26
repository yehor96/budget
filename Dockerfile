FROM openjdk:17-jdk-alpine
COPY target/budget-0.0.1-SNAPSHOT.jar budget-app-0.8.jar
ENTRYPOINT ["java","-jar","/budget-app-0.8.jar"]