FROM openjdk:8-jre-alpine

ARG JAR_FILE=build/libs/amazing-co-0.1.0.jar

COPY ${JAR_FILE} amazing-co-0.1.0.jar

EXPOSE 8080

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/amazing-co-0.1.0.jar"]

