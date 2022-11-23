FROM alpine

WORKDIR /app

RUN apk add openjdk8

COPY target/log-param-service-1.3.jar /app-dev.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app-dev.jar"]