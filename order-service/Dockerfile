FROM openjdk:21-ea-jdk-slim
VOLUME /tmp

COPY target/order-service-1.0.jar OrderService.jar
ENTRYPOINT ["java","-jar","OrderService.jar"]