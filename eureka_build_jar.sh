#!/bin/bash

# Function to build a JAR file from a project directory
build_jar() {
  project_dir="$1"
  service_name="$2"

  # Navigate to the project directory
  cd "$project_dir" || exit 1

  # Use Maven or Gradle to build the JAR (replace with your build command)
  mvn clean package -DskipTests  # Example for Maven with skipping tests

# Check if JAR file exists
  if [ -f "$service_name.jar" ]; then
    echo "Successfully built JAR for $service_name"
  else
    echo "Failed to build JAR for $service_name"
    exit 1  # Maintain the exit code for failure
  fi

  echo "Successfully built JAR for $service_name"
}

# Build JAR for Eureka service (replace with your project directory and name)
build_jar ../registryservice eureka

echo "JAR file built for Eureka service!"
