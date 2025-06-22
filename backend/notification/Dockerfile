FROM openjdk:21-jdk-slim

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Create app directory
WORKDIR /foodopia

# Copy the JAR file
COPY target/notification-*.jar app.jar

# Expose port 9020 (matching application.yml)
EXPOSE 9020

# Run the application
CMD ["java", "-jar", "app.jar"]