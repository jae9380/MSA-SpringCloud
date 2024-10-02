FROM openjdk:21-ea-jdk-slim
VOLUME /tmp
COPY target/discoveryservice-1.0.jar Discoveryservice.jar
ENTRYPOINT ["java","-jar","Discoveryservice.jar"]