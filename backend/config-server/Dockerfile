FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/*.jar configServer.jar

ENTRYPOINT ["java","-jar","configServer.jar"]
