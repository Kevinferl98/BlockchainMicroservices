FROM openjdk:22-jdk-slim

COPY target/consumer-0.0.1-SNAPSHOT.jar /app/consumer-0.0.1-SNAPSHOT.jar

WORKDIR /app

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "consumer-0.0.1-SNAPSHOT.jar"]