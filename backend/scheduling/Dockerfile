FROM eclipse-temurin:21-jdk-jammy

RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

WORKDIR /foodopia

COPY target/scheduling-*.jar app.jar

EXPOSE 8084

CMD ["java", "-jar", "app.jar"]
