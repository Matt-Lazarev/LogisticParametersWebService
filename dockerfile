FROM alpine

WORKDIR /app

RUN apk add openjdk8

COPY target/log-param-service-1.3.jar /app-dev.jar
COPY application.yaml /application.yaml

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app-dev.jar", "--spring.config.location=file:/application.yaml"]