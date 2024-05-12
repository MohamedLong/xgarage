FROM openjdk:17

# Set the working directory inside the container
WORKDIR /app

# Copy the application JAR file into the container
COPY target/*.jar /app/Kernel-1.0-SNAPSHOT.jar

# Expose any ports your application uses (if necessary)
EXPOSE 5050

# # Set the system property for Eureka
# ENV JAVA_OPTS="-Deureka.instance.preferIpAddress=true"

# Command to run the Spring Boot application
CMD ["java", "-jar", "Kernel-1.0-SNAPSHOT.jar"]