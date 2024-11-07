FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/*.jar gatewayService.jar

ENTRYPOINT ["java","-jar","gatewayService.jar"]
