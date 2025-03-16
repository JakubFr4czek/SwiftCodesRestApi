FROM openjdk:17

LABEL maintainer="jakub.fraczek.career@gmail.com"

VOLUME /tmp

EXPOSE 8080

ARG JAR_FILE=target/swift-codes.jar

ADD ${JAR_FILE} app.jar

# Run the jar file
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]