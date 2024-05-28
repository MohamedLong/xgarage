FROM openjdk:17

# Set the working directory inside the container
WORKDIR /app

# Copy the application JAR file into the container
COPY target/*.jar /app/Gateway-1.0-SNAPSHOT.jar

# Expose any ports your application uses (if necessary)
EXPOSE 6060

# Command to run the Spring Boot application
CMD ["java", "-jar", "Gateway-1.0-SNAPSHOT.jar"]