FROM openjdk:17

# Set the working directory inside the container
WORKDIR /app

# Copy the application JAR file into the container
COPY target/*.jar /app/service-registry-0.0.1-SNAPSHOT.jar

# Expose any ports your application uses (if necessary)
EXPOSE 8761

# Command to run the Spring Boot application
CMD ["java", "-jar", "service-registry-0.0.1-SNAPSHOT.jar"]