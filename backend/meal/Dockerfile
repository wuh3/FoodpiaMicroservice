FROM eclipse-temurin:17-jdk-jammy

RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

WORKDIR /foodopia

COPY target/meal-*.jar app.jar

EXPOSE 8082

CMD ["java", "-jar", "app.jar"]
