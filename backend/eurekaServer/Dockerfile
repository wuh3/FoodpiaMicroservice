FROM openjdk:21-jdk-slim

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Create app directory
WORKDIR /foodopia

# Copy the JAR file
COPY target/eurekaServer-*.jar app.jar

# Expose port 8070 (matching application.yml)
EXPOSE 8070

# Run the application
CMD ["java", "-jar", "app.jar"]