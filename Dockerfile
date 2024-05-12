FROM openjdk:17

# Set the working directory inside the container
WORKDIR /app

# Copy the application JAR file into the container
COPY target/*.jar /app/MainApp-1.0-SNAPSHOT.jar

# Expose any ports your application uses (if necessary)
EXPOSE 4040

# Command to run the Spring Boot application
CMD ["java", "-jar", "MainApp-1.0-SNAPSHOT.jar"]