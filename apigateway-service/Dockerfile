FROM openjdk:21-ea-jdk-slim
VOLUME /tmp
COPY target/apigateway-service-1.0.jar ApiGateway-service.jar
ENTRYPOINT ["java","-jar","ApiGateway-service.jar"]