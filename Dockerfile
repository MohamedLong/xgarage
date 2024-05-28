FROM openjdk:17-jdk-alpine
VOLUME /tmp
ARG JAR_FILE=target/Gateway-1.0-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
COPY wait-for-it.sh wait-for-it.sh
RUN chmod +x wait-for-it.sh
ENTRYPOINT ["./wait-for-it.sh", "eureka-service", "8761", "--", "java", "-jar", "/app.jar"]
