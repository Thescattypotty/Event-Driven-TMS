FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/*.jar registryService.jar

ENTRYPOINT ["java","-jar","registryService.jar"]
