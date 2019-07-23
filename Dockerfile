# Use official base image of Java Runtim
FROM openjdk:8-jdk-alpine

#change timezone
RUN date
RUN apk add tzdata
RUN cp /usr/share/zoneinfo/Asia/Bangkok /etc/localtime
RUN date

# Set volume point to /tmp
VOLUME /tmp

# Make port 8082 available to the world outside container
EXPOSE 8082

# Set application's JAR file
ARG JAR_FILE=WebScrapingInputDatabase-1.0-SNAPSHOT.jar

# Add the application's JAR file to the container
ADD ${JAR_FILE} app.jar

# Run the JAR file
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar"]