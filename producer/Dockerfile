FROM openjdk:22-jdk-slim

COPY target/producer-0.0.1-SNAPSHOT.jar /app/producer-0.0.1-SNAPSHOT.jar

WORKDIR /app

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "producer-0.0.1-SNAPSHOT.jar"]